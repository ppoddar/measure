package com.nutanix.bpg.measure.spring;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.config.Task;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.nutanix.bpg.measure.MeasurementServer;
import com.nutanix.bpg.measure.MeasurementServerImpl;
import com.nutanix.bpg.measure.jdbc.SnapshotPlugin;
import com.nutanix.bpg.measure.model.Catalog;
import com.nutanix.bpg.measure.model.Database;
import com.nutanix.bpg.measure.model.DatabaseBuilder;
import com.nutanix.bpg.measure.model.DatabaseProperties;
import com.nutanix.bpg.measure.model.Metrics;
import com.nutanix.bpg.measure.model.MetricsDimension;
import com.nutanix.bpg.measure.model.Snapshot;
import com.nutanix.bpg.measure.model.SnapshotSchedule;
import com.nutanix.bpg.measure.spring.serde.CatalogDeserializer;
import com.nutanix.bpg.measure.spring.serde.CatalogSerializer;
import com.nutanix.bpg.measure.spring.serde.IndexibleMapDeserializer;
import com.nutanix.bpg.measure.spring.serde.IndexibleMapSerializer;
import com.nutanix.bpg.measure.utils.IndexibleMap;
import com.nutanix.bpg.workload.PGBenchOptions;

@RestController
public class MeasurementController {
	private MeasurementServer api;
	private TaskQueue taskQueue = new TaskQueue();
	@Autowired HttpServletRequest request;
	private static Logger logger = LoggerFactory.getLogger(MeasurementController.class);
	
	@PostConstruct
	public void initService() throws Exception {
		api = MeasurementServerImpl.init();
	}
	
	@GetMapping(value= "/databases")
	public Collection<Database> getDatabases() {
		return api.getDatabases();
	}
	
	@PostMapping(value= "/database", 
			consumes="application/json")
	public ResponseEntity<?> registerDatabases(
			@RequestBody DatabaseProperties dbProps) throws Exception {
		logger.info("register " + dbProps);
		Database db = DatabaseBuilder.build(dbProps);
		api.addDatabase(db, false);
		return new ResponseEntity<Database>(db, HttpStatus.CREATED);
	}

	
	/**
	 * takes a snapshot. 
	 * This is an asynchronous method.
	 * <p>
	 * A snapshot is collection of measurement taken
	 * at regular interval. Hence it may take
	 * long time to complete all measurements. 
	 * <p>
	 * The method returns a {@link TaskToken token}
	 * similar to a future promise.
	 * 
	 * 
	 * @param name snapshot name
	 * @param databaseName name of a registered
	 * database
	 * @param metricsName name of a metrics
	 * @param schedule schedule how many
	 * measurements to be taken and when
	 * @return
	 * @throws Exception
	 */
	@Async
	@PostMapping(value="/snapshot/{name}/{database}/{metrics}")
	public TaskToken<Snapshot> takeSnapshot(
			@PathVariable("name") String name,
			@PathVariable("database") String databaseName,
			@PathVariable("metrics") String metricsName,
			@RequestBody SnapshotSchedule schedule) throws Exception {
		Database db = api.getDatabase(databaseName);
		Metrics metrics = db.getMetrics(metricsName);
		
		CompletableFuture<Snapshot> sn = 
				api.takeSnapshot(name,
				db, metrics, schedule);
		logger.debug(sn + " has started and future promise is added to queue");
		
		return taskQueue.addTask(name, 
				"snapshot", 
				schedule.getTimeLimit(),
				sn);
	}
	
	@Async
	@PostMapping(value="/benchmark/{name}/{database}/")
	public TaskToken<Snapshot> takeBenchmark(
			@PathVariable("name") String name,
			@PathVariable("database") String databaseName,
			@RequestBody PGBenchOptions[] options) throws Exception {
		
		logger.debug("takeBenachmark options=" + options);
		Database db = api.getDatabase(databaseName);
		CompletableFuture<Snapshot> benchmarks = 
				api.takeBenchmark(name, db, options);
		long duration = 0;
		for (PGBenchOptions option : options) {
			duration += TimeUnit.MILLISECONDS.convert(
					option.getTimeToRun(), 
					option.getTimeToRunUnit());
		}
		return taskQueue.addTask(name, "benchmark", 
				duration,
				benchmarks);
	}

	
	/**
	 * gets token for a task identified by given
	 * identifier.
	 * 
	 * @param id
	 * @return no response if task is not queued.
	 */
	@GetMapping("/task/status/{id}")
	public ResponseEntity<TaskToken<?>> getTaskStatus(
			@PathVariable("id") String id) {
		TaskToken<?> task = taskQueue.getTask(id);
		if (task != null) {
			return new ResponseEntity<TaskToken<?>>(task, HttpStatus.CREATED);
		} else {
			//logger.warn("no task [" + id + "] found");
			return new ResponseEntity<TaskToken<?>>(HttpStatus.NOT_FOUND);
		}

	}
	
	@GetMapping("/task/result/{id}")
	public ResponseEntity<?> getTaskResult(@PathVariable("id") String id) 
		throws Exception {
		logger.debug("getting result for task " + id );
		TaskToken<?> task = taskQueue.getTask(id);
		if (task != null) {
			Object result = task.getResult();
			logger.debug("sending task result " + result);
			return new ResponseEntity<Object>(result, HttpStatus.OK);
		} else {
			throw new IllegalArgumentException("task " + id + " does not exist");
		}
	}

	
	@PostMapping("/task/cancel/{id}")
	public ResponseEntity<?> cancelTask(@PathVariable("id") String id) {
		TaskToken<?> task = taskQueue.getTask(id);
		if (task != null) {
			task.cancel();
			return new ResponseEntity<String>("canceled task [" + id + "]",  
					HttpStatus.OK);
		} else {
			throw new IllegalArgumentException("task [" + id + "] not found");
		}
	}

	
	@Bean
	public CommonsRequestLoggingFilter requestLoggingFilter() {
	    CommonsRequestLoggingFilter loggingFilter = 
	    		new CommonsRequestLoggingFilter();
	    loggingFilter.setIncludeClientInfo(false);
	    loggingFilter.setIncludeQueryString(true);
	    loggingFilter.setIncludePayload(true);
	    return loggingFilter;
	}
	
	@Bean
	@Primary
	public  ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		CollectionLikeType type =
				mapper.getTypeFactory()
				.constructCollectionLikeType(Catalog.class, Metrics.class);
		CollectionLikeType type2 =
				mapper.getTypeFactory()
				.constructCollectionLikeType(IndexibleMap.class, MetricsDimension.class);
		
		module.addSerializer(new CatalogSerializer(type));
		module.addDeserializer(Catalog.class, new CatalogDeserializer(type));
		module.addSerializer(new IndexibleMapSerializer(type2));
		module.addDeserializer(IndexibleMap.class, new IndexibleMapDeserializer(type2));
		
		mapper.registerModule(module);
		
	    return mapper;
	}
	
	@GetMapping("/tasks")
	public Collection<TaskToken<?>> getAllTaksk() {
		return taskQueue.getTasks();
	}
	
//	@ExceptionHandler(Exception.class)
//	public ResponseEntity<Object> handleError(
//			HttpStatus status, 
//			Exception e) {
//		
//        logger.error("Exception caught at server side: ", e);
//        e.printStackTrace();
//        return new ResponseEntity<>(e, status);
//    }
//	
//	private ResponseEntity<String> handleError(
//			HttpStatus status, 
//			String message) {
//		return new ResponseEntity<String>(message, status);
//	}
//	private ResponseEntity<Object> handleError(
//			HttpStatus status, 
//			Exception ex) {
//		return handleError(status, ex.getMessage(), ex);
//	}
//
	
}
