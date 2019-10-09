/**
 * style for task state
 */
var stateColors = {
		'DONE'      : 'w3-green',
		'RUNNING'   : 'w3-yellow',
		'CANCELLED' : 'w3-red'
}

var JOB_CATEGORIES = {
		"smoke-test"   :'Smoke Tests', 
		"check-in-test":'Check-in Tests', 
		"performance"  :'Measurment',
		"benchmark"    :'Benchmark',
		
}
var RESOURCE_POOLS = {
		"default"   :'Standard', 
		"jenkins"   :'Development Test' 
		
}

	
showJobs = function() {
	console.log('show jobs')
	var jobQ = new JobQueue('default')
	jobQ.fetchAll()
	
}
fillJobSubmitForm = function($form) {
	console.log('filling form ' + $form.attr('id'))
	var $jobTypes = $form.find('select#job-category').first();
	for (var v in JOB_CATEGORIES) {
		var $option = $('<option>')
		$option.attr('value', v)
		$option.text(JOB_CATEGORIES[v])
		$jobTypes.append($option)
	}
	var $pools = $form.find('select#resource-pool').first();
	for (var v in RESOURCE_POOLS) {
		var $option = $('<option>')
		$option.attr('value', v)
		$option.text(RESOURCE_POOLS[v])
		$pools.append($option)
	}

	
}

submitJob = function($form) {
	console.log('click on ' + $form.attr('id'))
	if ($form.length == 0) 
		throw 'form not found' 
	var category = $form.find('#job-name').val()
	var name = $form.find('#job-name').val()
	var submitter = $form.find('#job-submitter').val()
	console.log('job submitted by [' +  submitter + ']')
	
	var demand = {
		"MEMORY" : $form.find('#job-memory').val() + ' ' 
	         + $form.find('#job-memory-unit').val().toUpperCase(),
		"STORAGE" : $form.find('#job-storage').val() + ' ' 
		         + $form.find('#job-storage-unit').val().toUpperCase(),
		"COMPUTE" : $form.find('#job-compute').val()
	}
	var script     = $form.find('job-script').val();
	var scriptArgs = $form.find('job-script-args').val();
	var env        = $form.find('job-env').val();
	var payload = {
			'name'    : name,
			'category': category,
			'demand'  : demand,
			'script'  : script,
			'script-args': scriptArgs,
			'environment': env
	}
	var url = '/resource/job/nutest/'
	console.log('request POST ' + url)
	$.ajax({
		method: 'POST',
		url: url,
		data: JSON.stringify(payload),
		contentType: 'text/plain'
	}).done(function (response) {
		console.log('response from ' + url)
		console.log(response)
		//incrementJobCount()
		var msg = response['category'] + ' job '
		+ '[' + response['name'] + '] has been submitted'
		console.log(msg)
		//showMessageDialog(msg)
	}).fail(function (err) {
		console.log('error from ' + url)
		var msg = JSON.parse(err.responseText)['cause']['message']
		console.log(msg)
		showErrorDialog(msg)
	})
	
}


incrementJobCount = function() {
	var $count = $('#job-count')
	var n = parseInt($count.text())
	$count.text((n+1).toString())

}
/**
 * a single job.
 * rendered as a row
 * or as output
 */
class Job {
	/**
	 * create from description
	 */
	constructor(job) {
		this.id       = job.id
		this.name     = job.name
		this.category = job.category
		this.startTime = job.startTime
		this.expectedDuration  = job.expectedDuration
		this.status    = job.status
		this.output = job.outputURI
		this.errorOutput = job.errorOutputURI
	}
	
	getRowIdentifer() {
		return 'job-' + this.id
	}
	/**
	 * create a row for this job
	 */
	createRow() {
		var $row = $('<tr>')
		var rowId = this.getRowIdentifer();
		$row.attr('id', rowId)
		var $status = $('<td>')
		$status.text(this.status)
		var $name = $('<td>')
		$name.text(this.name)
		var $time = $('<td>')
		$time.text(this.getTime())
		var $action = $('<td>')
		$action.append(this.getAction())
		$row.append($status, $name, $time, $action)
		return $row
	}
	
	/**
	 * create a list item that when clicked shows a row
	 * in job table
	 */
	createItem() {
		var $li = $('<li>')
		$li.text(this.name)
		var _this = this
		$li.on('click', function() {
			var $table = $('#job-table')
			$table.append(_this.createRow())
		})
		return $li
	}
	
	updateRow($row) {
		var $status = $row.children("td").get(0)
		var $name   = $row.children("td").get(1)
		if (this.status != $status.text()) {
			$status.text(this.status)
		}
		
	}
	
	cancel() {
		var url  = 'task/cancel/' + task.id
		console.log('calling ' + url)
		$.ajax({
			url: url,
			method: 'POST'
		}).done(function(data) {
			console.log('cancel result')
			console.log(data)
			$('task-' + task.id).remove()
		}).fail(function(data) {
			showErrorDialog(data)
		})
	}

	getTime() {
		var diff = new Date().getTime() - this.startTime;
		return 'started ' + new Date(this.startTime)
		  + ' (' + diff + ' ago)'
	}
	
	
	getAction() {
		var $action = $('<span>')
		var $output = $('<button>')
		var $error  = $('<button>')
		$output.text('out')
		$error.text('err')
		$action.append($output, ' | ', $error)
		var _this = this
		$output.on('click', function() {
			console.log('show output ' + _this.output)
			$('#output').load(_this.output) 
		})
		$error.on('click', function() {
			$('#error-output').load(_this.errorOutput) 
		})
		return $action
	}
	shorten(s) {
		 var n = s.length
		 var m = Math.min(4, n)
		 var eclipses = ''
		 if (n > 4) eclipses = '...'
		 return eclipses + s.substring(n-m, n)
	}

}

/**
 * a queue of jobs has a name maintains a dictionary of job object
 */
class JobQueue {
	/**
	 * create a queue with given name
	 */
	constructor(name) {
		console.log('creating job queue [' + name + ']')
		this.name = name
		this.jobs = {}
		this.$li = $('<li>')
		var $label = $('<span>')
		$label.text(this.name)
		$label.css('font-weight', 'bold')
		
		this.$count = $('<span>')
		this.$count.addClass('w3-badge w3-green')
		this.$count.text('0')
		this.$li.append($label, this.$count)
		this.$jobs = $('<ul>')
		this.$li.append(this.$jobs)
	}
	
	/**
	 * show queue size in a badge
	 */
	addOrUpdate(job) {
		if (job.id in this.jobs) {
			console.log('update ' + job)
		} else {
			console.log('add ' + job)
		}
		this.jobs[job.id] = job
		
		var $job = job.createItem()
		this.$jobs.append($job)
		this.$count.text((Object.keys(this.jobs).length).toString())
	}

	
	// fetch all tasks and update their latest status
	fetchAll() {
		var _this = this;
		var url = '/task/' + this.name
		$.ajax({
			url : url
		}).done(function(serverJobs){
			console.log('fetched ' + serverJobs.length 
					+ ' jobs from [' + _this.name + '] queue by ' + url)
				console.log(serverJobs)
				for (var i = 0; i < serverJobs.length; i++) {
					var serverJob = new Job(serverJobs[i])
					_this.addOrUpdateJob(serverJob)
				}
		}).fail(function(err){
			showErrorDialog(err.responseText)
		})
	}
	
	/**
	 * shows each job of the queue as rows of given table
	 */
	showQueue($table) {
		console.log('jobs')
		console.log(this.jobs)
		$table.empty()
		var $thead = $('<thead>')
		$table.append($thead)
		var columns = ['status', 'name', 'time']
		for (var i = 0; i < columns.length; i++) {
			var $td = $('<td>')
			$td.text(columns[i])
			$td.css('font-weight', 'bold')
			$thead.append($td)
		}
		for (var id in this.jobs) {
			var job = this.jobs[id]
			var rowId = job.getRowIdentifer()
			var $row = $table.find('tr #'+rowId).first()
			if ($row.length == 0) {
				$row = job.createRow()
				$table.append($row)
			} else {
				job.update($row)
			}
		}
	}

	/**
	 * continually polls for status update
	 */
	poll() {
		console.log('polling task ' + this)
		var _this = this
		$.ajax({
			url: 'task/status/' + this.id
		}).done(function(serverTask) {
			console.log('server task:')
			console.log(serverTask)
			var task  =  Task.update(serverTask, $('#task-table'))
			if (task.state == 'DONE') {
				console.log('------- DONE ' + _this)
				clearInterval(task.poll)
				
				var text = 'Task [' + _this + '] has been completed'
				showMessageDialog(text, true)
			}
		}).fail(function(err){
			console.log('*** task status error')
			console.log(err)
			showErrorDialog(err.responseText)
		})
	}
	
	/**
	 * create a list item and add all jobs as children
	 */
	createItem() {
		var _this = this
		$.ajax({
			url: '/task/' + this.name
		}).done(function(jobs) {
			_this.jobs = {}
			for (var i = 0; i < jobs.length; i++) {
				var job = new Job(jobs[i])
				_this.addOrUpdate(job)
			}
			
		})
		return this.$li
	}
}






/**
 * fetch task result given task id.
 * 
 */
fetchTaskResult = function(e) {
	var task = $(this).data('task')
	var url  = 'task/result/' + task.id
	console.log('calling ' + url)
	$.ajax({
		url: url
	}).done(function(data) {
		console.log('got task result')
		console.log(data)
		if (task.category == 'benchmark') {
			new Drawing().createBenchmarkView(
					data, $('#benchmark-view'))
			new Tab('#benchmark-view').show()
		} else if (task.category == 'snapshot') {
			new Drawing().createSnapshotView(
					data, $('#snapshot-view'))
			new Tab('#snapshot-view').show()
		} else {
			showErrorDialog('unknown task category ' 
					+ JSON.stringify(task))
		}
		
	}).fail(function(data) {
		showErrorDialog(data)
	})
}

