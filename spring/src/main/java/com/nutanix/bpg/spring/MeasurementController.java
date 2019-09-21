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

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobQueue;
import com.nutanix.bpg.job.JobToken;
import com.nutanix.bpg.job.Stage;
import com.nutanix.bpg.job.Task;
import com.nutanix.bpg.job.impl.JobImpl;
import com.nutanix.bpg.job.impl.JobQueueManagerImpl;
import com.nutanix.bpg.measure.MeasurementServer;
import com.nutanix.bpg.measure.MeasurementServerImpl;
import com.nutanix.bpg.measure.model.Measurement;
import com.nutanix.bpg.measure.model.Snapshot;
import com.nutanix.bpg.measure.model.SnapshotSchedule;
import com.nutanix.bpg.model.Database;
import com.nutanix.bpg.model.Metrics;
import com.nutanix.bpg.repo.Repository;
import com.nutanix.bpg.repo.RepositoryImpl;
import com.nutanix.bpg.spring.config.YAMLConfig;
import com.nutanix.bpg.workload.PGBenchOptions;

@RestController
@RequestMapping("/measure")
public class MeasurementController {
	private MeasurementServer api;
	private Repository repo;
	private JobQueue jobQueue;
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
		jobQueue = JobQueueManagerImpl.instance()
				.newQueue("measurement");
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
	public JobToken takeSnapshot(@PathVariable("name") String name,
			@PathVariable("database") String databaseName, @PathVariable("metrics") String metricsName,
			@RequestBody SnapshotSchedule schedule) throws Exception {
		Database db = repo.getDatabase(databaseName);
		Metrics metrics = repo.getMetrics(metricsName);
		Job<?,?> job = createJob("snapshot", db, metrics, schedule);

		JobToken token = jobQueue.addJob(job);
		return token;
	}

	@Async
	@PostMapping(value = "/benchmark/{name}/{database}/")
	public JobToken takeBenchmark(@PathVariable("name") String name,
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
//		return taskQueue.addJob(name, "benchmark", duration, benchmarks);
		return null;
	}




//	@Bean
	public CommonsRequestLoggingFilter requestLoggingFilter() {
		CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
		loggingFilter.setIncludeClientInfo(false);
		loggingFilter.setIncludeQueryString(true);
		loggingFilter.setIncludePayload(true);
		return loggingFilter;
	}
	
	/**
	 * create a Job for taking snapshot.
	 * A snapshot schedule specifies how many measurements
	 * are to be taken and at what time interval.
	 * Each stage is a single task that takes a measurement.
	 * The stages are executed sequentially by a snapshot job.
	 *  
	 * @param name
	 * @param db
	 * @param m
	 * @param schedule
	 * @return
	 */
	Job<?,?> createJob(String name, Database db, Metrics m, SnapshotSchedule schedule) {
		Job<Measurement,Measurement> job = new JobImpl<Measurement,Measurement>();
		job.setName(name);
		
		for (int i = 0; i < schedule.getCount(); i++) {
			Stage<Measurement,Measurement> stage = new Stage<Measurement, Measurement>() {
				@Override
				protected Measurement combine(Measurement r, Measurement t) {
					return null;
				}
			};
			stage.setName("stage-"+i);
			Task<Measurement> task = new Task<Measurement>() {
			@Override
			public Measurement call() throws Exception {
				return api.takeMeasurement(name, db, m);
			}
			@Override
			public long getExpectedCompletionTimeInMillis() {
				return 1000;
			}
			};
			stage.addTask(task);
			job.addStage(stage);
		}
		return job;
	}
}

