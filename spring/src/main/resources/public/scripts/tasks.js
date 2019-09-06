var tasks      = {}
var stateColors = {
		'DONE'      : 'w3-green',
		'RUNNING'   : 'w3-yellow',
		'CANCELLED' : 'w3-red'
}
/**
 * queues all tasks
 */
class TaskQueue {
	/**
	 * add a task. update task count
	 */
	static addTask(t) {
		var task = new Task(t)
		tasks[t.id] = task
		var $count = $('#task-count')
		var count = $count.data('count')
		$count.text((count+1).toString())
		$count.data('count', count+1)
		
		return task
	}
	
	static removeTask(t) {
		console.log(t + ' has been removed')
		var task = tasks[t.id]
		var $count = $('#task-count')
		var count = $count.data('count')
		var newCount = count - 1
		$count.text(newCount.toString())
		$count.data('count', newCount)
	}
	// fetch all tasks and update their latest status
	// display all tasks in a table
	static showTasks($table) {
		$.ajax({
			url : 'tasks/'
		}).done(function(data){
			console.log('all ' + data.length + ' tasks')
			console.log(data)
			$('#task-count').text(data.length.toString())
			if (data.length == 0) {
				console.log('no tasks found')
			} else {
				$table.empty()
				Task.addColumnHeaders($table)
				for (var i = 0; i < data.length; i++) {
					var id = data[i].id
					var task = tasks[id]
					if (task == undefined) {
						task = TaskQueue.addTask(data[i])
					} else {
						task.state = data[i].state
					}
					var $taskView = task.createRow()
					$table.append($taskView)
				}
				new Tab('#task-view').show()
			}
		}).fail(function(err){
			showErrorDialog(err.responseText)
		})
	}
	
}

/**
 * a task pools for its status
 * and once completed flashed as message
 */
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
	}
	/**
	 * continually polls for status update
	 */
	poll() {
		console.log('polling task ' + this)
		var _this = this
		$.ajax({
			url: 'task/status/' + this.id
		}).done(function(data) {
			console.log('server task:')
			console.log(data)
			var task   = tasks[data.id]
			task.state = data.state
			if (task.state == 'DONE') {
				console.log('------- DONE ' + _this)
				clearInterval(_this.poll)
				TaskQueue.removeTask(_this)
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

	// create a table row for a task 
    createRow() {
		var $row = $('<tr>')
		$row.attr('id', 'task-' + this.id)
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
