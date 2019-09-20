package com.nutanix.bpg.spring;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import com.nutanix.bpg.measure.MeasurementServer;
import com.nutanix.bpg.measure.MeasurementServerImpl;
import com.nutanix.bpg.measure.model.Snapshot;
import com.nutanix.bpg.measure.model.SnapshotSchedule;
import com.nutanix.bpg.model.Database;
import com.nutanix.bpg.model.Metrics;
import com.nutanix.bpg.repo.Repository;
import com.nutanix.bpg.repo.RepositoryImpl;
import com.nutanix.bpg.spring.config.YAMLConfig;
import com.nutanix.bpg.task.JobQueueManager;
import com.nutanix.bpg.task.TaskQueue;
import com.nutanix.bpg.task.TaskToken;
import com.nutanix.bpg.workload.PGBenchOptions;

@RestController
@RequestMapping("/measure")
public class MeasurementController {
	private MeasurementServer api;
	private Repository repo;
	private TaskQueue taskQueue;
	@Autowired YAMLConfig config;
	@Autowired ServletContext context;
	@Autowired HttpServletRequest request;
	private static Logger logger = LoggerFactory.getLogger(MeasurementController.class);

	@PostConstruct
	public void initService() throws Exception {
		Map<String, String> catalog = config.getCatalog();
		Properties p = new Properties();
		for (String key : catalog.keySet()) {
			p.setProperty("catalog."+key, catalog.get(key));
		}
		MeasurementServerImpl.init(p);
		api = MeasurementServerImpl.instance();
		
		repo = RepositoryImpl.instance();
		taskQueue = JobQueueManager.instance().newQueue("measure");
	}


	/**
	 * takes a snapshot. This is an asynchronous method.
	 * <p>
	 * A snapshot is collection of measurement taken at regular interval. Hence it
	 * may take long time to complete all measurements.
	 * <p>
	 * The method returns a {@link TaskToken token} similar to a future promise.
	 * 
	 * 
	 * @param name         snapshot name
	 * @param databaseName name of a registered database
	 * @param metricsName  name of a metrics
	 * @param schedule     schedule how many measurements to be taken and when
	 * @return
	 * @throws Exception
	 */
	@Async
	@PostMapping(value = "/snapshot/{name}/{database}/{metrics}")
	public TaskToken takeSnapshot(@PathVariable("name") String name,
			@PathVariable("database") String databaseName, @PathVariable("metrics") String metricsName,
			@RequestBody SnapshotSchedule schedule) throws Exception {
		Database db = repo.getDatabase(databaseName);
		Metrics metrics = repo.getMetrics(metricsName);

		CompletableFuture<Snapshot> sn = api.takeSnapshot(name, db, metrics, schedule);
		logger.debug(sn + " has started and future promise is added to queue"
				+ " schedule " + schedule);

		return taskQueue.addTask(name, "snapshot", schedule.getTimeLimit(), sn);
	}

	@Async
	@PostMapping(value = "/benchmark/{name}/{database}/")
	public TaskToken takeBenchmark(@PathVariable("name") String name,
			@PathVariable("database") String databaseName, 
			@RequestBody PGBenchOptions[] options) throws Exception {

		logger.info("----------------------------");
		logger.info("takeBenachmark options=" + options);
		Database db = repo.getDatabase(databaseName);
		CompletableFuture<Snapshot> benchmarks = api.takeBenchmark(name, db, options);
		long duration = 0;
		for (PGBenchOptions option : options) {
			duration += TimeUnit.MILLISECONDS.convert(option.getTimeToRun(), option.getTimeToRunUnit());
		}
		return taskQueue.addTask(name, "benchmark", duration, benchmarks);
	}




//	@Bean
	public CommonsRequestLoggingFilter requestLoggingFilter() {
		CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
		loggingFilter.setIncludeClientInfo(false);
		loggingFilter.setIncludeQueryString(true);
		loggingFilter.setIncludePayload(true);
		return loggingFilter;
	}
}

