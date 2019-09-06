class Snapshot {
	constructor() {
		
	}
    takeSnapshot(e, $form) {
    	// IMPORTANT
		e.preventDefault()
		e.stopPropagation()
		
		var name      = $form.find('#snapshot-name').first().val()
		var db        = $form.find('#snapshot-database').first().val()
		var metrics   = $form.find('#snapshot-metrics').first().val()
		var count     = $form.find('#snapshot-schedule-count').first().val()
		var interval  = $form.find('#snapshot-schedule-interval').first().val()
		var payload = {
				count: count,
				interval: interval
			}
		var url = 'snapshot/' + name + '/' + db + '/' + metrics
		
		console.log('sending' + url + ' with payload ' + JSON.stringify(payload))
		$.ajax({
			url: url,
			type: 'POST',
			contentType:"application/json; charset=utf-8",
			data: JSON.stringify(payload)
		}).done(function(task){
			console.log(task)
			TaskQueue.addTask(task)
			var message = 'task <b>' + task.name + '</b> has been requested.<br>'
		       + " This task would complete in approximately <b>" 
		       + (task.expectedDuration/1000) + '</b> second.<br>'
		       + " You can see tast result and its status "
		       + " in Tasks tab"
		    showMessageDialog(message)
		}).fail(function(err) {
			console.log('***ERROR')
			console.log(err)
			showErrorDialog(err.responseText)
		})
	}
}

