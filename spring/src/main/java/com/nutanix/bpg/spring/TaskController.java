package com.nutanix.bpg.spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nutanix.bpg.task.JobQueueManager;
import com.nutanix.bpg.task.TaskQueue;
import com.nutanix.bpg.task.TaskToken;

@RestController
@RequestMapping("/task")
public class TaskController {
	private JobQueueManager taskQueues
		= JobQueueManager.instance();
	
	private static Logger logger = LoggerFactory.getLogger(TaskController.class);
	
	@GetMapping("/{queue}/result/{id}")
	public ResponseEntity<?> getTaskResult(
			@PathVariable("queue") String queue,
			@PathVariable("id") String id) throws Exception {
		logger.debug("getting result for task " + id);
		TaskToken task = taskQueues.getQueue(queue).getTask(id);
		if (task != null) {
			Object result = task.getResult();
			logger.debug("sending task result " + result);
			return new ResponseEntity<Object>(result, HttpStatus.OK);
		} else {
			throw new IllegalArgumentException("task " + id + " does not exist");
		}
	}

	/**
	 * gets token for a task identified by given identifier.
	 * 
	 * @param id
	 * @return no response if task is not queued.
	 */
	@GetMapping("{queue}/status/{id}")
	public ResponseEntity<TaskToken> getTaskStatus(
			@PathVariable("queue") String queue,
			@PathVariable("id") String id) {
		TaskToken task = taskQueues.getQueue(queue).getTask(id);
		if (task != null) {
			return new ResponseEntity<TaskToken>(task, HttpStatus.CREATED);
		} else {
			// logger.warn("no task [" + id + "] found");
			return new ResponseEntity<TaskToken>(HttpStatus.NOT_FOUND);
		}

	}

	@GetMapping("/{queue}")
	public Collection<TaskToken> getAllTasks(@PathVariable("queue") String queue) {
		return taskQueues.getQueue(queue).getTasks();
	}
	
	@PostMapping("/{queue}/cancel/{id}")
	public ResponseEntity<?> cancelTask(@PathVariable("queue") String queue, @PathVariable("id") String id) {
		TaskToken task = taskQueues.getQueue(queue).getTask(id);
		if (task != null) {
			task.cancel();
			return new ResponseEntity<String>("canceled task [" + id + "]", HttpStatus.OK);
		} else {
			throw new IllegalArgumentException("task [" + id + "] not found");
		}
	}

}
