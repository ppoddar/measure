package com.nutanix.bpg.spring;

import java.util.Map;
import java.util.Properties;
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

import com.nutanix.config.Configuration;
import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobQueue;
import com.nutanix.bpg.job.JobToken;
import com.nutanix.bpg.measure.MeasurementServer;
import com.nutanix.bpg.measure.MeasurementServerImpl;
import com.nutanix.bpg.measure.model.SnapshotSchedule;
import com.nutanix.bpg.model.Database;
import com.nutanix.bpg.model.Metrics;
import com.nutanix.bpg.repo.Repository;
import com.nutanix.bpg.repo.RepositoryImpl;
import com.nutanix.bpg.scheduler.JobQueueManagerImpl;
import com.nutanix.bpg.spring.config.YAMLConfig;
import com.nutanix.bpg.workload.PGBenchOptions;

@RestController
@RequestMapping("/measure")
public class MeasurementController extends MicroService {
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
		Configuration config = loadConfiguration("database");
		api = MeasurementServerImpl.configure(config);
		
		repo = RepositoryImpl.configure(null);
		jobQueue = JobQueueManagerImpl.instance()
				.newQueue("measurement", repo);
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
		Job job = null;
				//createJob("snapshot", db, metrics, schedule);

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
	
}

