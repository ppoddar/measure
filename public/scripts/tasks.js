/** dictionary of all tasks indexed by task id */
var TaskQueue      = {}

/**
 * style for task state
 */
var stateColors = {
		'DONE'      : 'w3-green',
		'RUNNING'   : 'w3-yellow',
		'CANCELLED' : 'w3-red'
}

class Task {
	constructor(task) {
		console.log('creating task ' + JSON.stringify(task))
		this.id       = task.id
		this.name     = task.name
		this.category = task.category
		this.startTime = task.startTime
		this.expectedDuration  = task.expectedDuration
		this.state    = 'RUNNING'
		
		var poll_time = Math.max(this.expectedDuration/10, 1000)
		// start a poll
		console.log('poll for completion at ' + poll_time + ' ms interval')
		this.poll = setInterval(this.poll.bind(this), poll_time)
		
		TaskQueue[this.id] = this
		
		this.updateCount(1)
	}

	
	updateCount(inc) {
		var $count = $('#task-count')
		var count = $count.data('count')
		$count.text((count+inc).toString())
		$count.data('count', count+1)
	}
	
	// fetch all tasks and update their latest status
	// display all tasks in a table
	static showAll($table) {
		$.ajax({
			url : 'tasks/'
		}).done(function(serverTasks){
			console.log('all ' + serverTasks.length + ' tasks')
			console.log(serverTasks)
			if (serverTasks.length == 0) {
				console.log('no server tasks found')
			} else {
				Task.addColumnHeaders($table)
				for (var i = 0; i < serverTasks.length; i++) {
					var serverTask = serverTasks[i]
					var task = Task.update(serverTask, $table)
				}
//				for (var id in TaskQueue) {
//					var exists = false
//					for (var j = 0; j < serverTasks.length; j++) {
//						if (id == serverTasks[j].id) {
//							exists = true
//							break
//						}
//						if (exists == false) {
//							var task = TaskQueue[id]
//							task.deleteRow($table)
//						}
//					}
//				}
				new Tab('#task-view').show()
			}
		}).fail(function(err){
			showErrorDialog(err.responseText)
		})
	}
	
	/**
	 * Updates a cached version with server version or
	 * creates a new one
	 * 
	 * if id exists  --> update state
	 * if id not exists --> add
	 * 
	 * A new row is created or existing row is updated in 
	 * given table
	 */
	static update(serverTask, $table) {
		var task
		if (serverTask.id in TaskQueue) {
			task = TaskQueue[serverTask.id]
			console.log('updated task ' + task)
			task.state = serverTask.state
		} else {
			task = new Task(serverTask)
			console.log('created task ' + task)
		}
		task.createOrUpdateRow($table)
		return task
	}
	
	deleteRow($table) {
		var el = '#task-' + this.id;
		var $row = $table.find(el).first()
		if ($row) {
			console.log('deleteRow ' + el)
			$row.remove()
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
	
	static addColumnHeaders($table) {
		var $thead = $('<thead>')
		var columns = ['status', 'ETA', 'category', 'name', 'action']
		columns.forEach(function(e){
			var $col = $('<th>')
			$col.text(e)
			$thead.append($col)
		})
		$table.append($thead)
	}

	getRowIdentifer() {
		return 'task-' + this.id
	}
	
	// create or update a table row for this task 
	//
    createOrUpdateRow($table) {
    	var $row = $('#' + this.getRowIdentifer())
    	if ($row.length != 0) {
    		console.log('updating table row ' + this.id)
    		$row.empty()
    	} else {
    		console.log('creating new table row ' + this.id)
    		$row = $('<tr>')
    		$row.attr('id', this.getRowIdentifer())
    		$table.append($row)
    	}
    	
		var columStyle = 'w3-col m3'
		
		var $id = $('<td>')
		$id.text(shorten(this.id))
		
		var $name = $('<td>')
		$name.text(this.name)

		var $category = $('<td>')
		$category.text(this.category)

		var $state = $('<td>')
		$state.text(this.state)
		$state.addClass(stateColors[this.state])

		var $eta = $('<td>')
		var startTime         = this.startTime
		var expectedDuration  = this.expectedDuration
		var endTime = startTime + expectedDuration
		var currentTime = new Date().getTime()
		var remainingTime = (endTime - currentTime)/1000;
		if (this.state == 'RUNNING') {
			$eta.text(remainingTime + ' s')
			if (remainingTime < 0) {
				$eta.addClass('w3-text-red')
			} 
		}
		
		var $action = $('<td>')
		$action.addClass(columStyle)
		if (this.state == 'DONE') {
			$action.text('view')
			$action.addClass('w3-text-blue')
			$action.data('task', this)
			$action.data('url',  'task/result/' + this.id)
			$action.on('click', fetchTaskResult);
		} else if (this.state == 'RUNNING') {
			$action.text('cancel')
			$action.addClass('w3-text-red')
			$action.data('task', this)
			$action.data('url',  'task/cancel/' + this.id)
			$action.on('click', cancelTask);
		}
		$row.append($state, $eta, $category, $name, $action)
		
		return $row
	}
	
}


Task.prototype.toString = function () {
	return 'task:[' + this.name + ']'
}


shorten = function(s) {
	 var n = s.length
	 var m = Math.min(4, n)
	 var eclipses = ''
	 if (n > 4) eclipses = '...'
	 return eclipses + s.substring(n-m, n)
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

cancelTask = function(e) {
	var task = $(this).data('task')
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
