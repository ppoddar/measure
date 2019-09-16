class Benchmark {
	constructor() {
		
	}
	runBenchmark(e) {
    	e.preventDefault()
    	e.stopPropagation()
    	var $dialog = $('#run-benchmark-dialog')
    	var $form = $dialog.find('form').first()
	    var $databaseOptions = $form
		          .find('#benchmark-database').first()
        populateComboBox($databaseOptions, 
        	new Database().getAllDatabases());
	     
    	showDialog($dialog)
    	$form.submit(function(e) {
    		Benchmark.executeOnServer(e, $form )
   			return
    	})
	}
	



	static executeOnServer(e, $form) {
    	e.preventDefault()
    	e.stopPropagation()
		console.log('calling executeOnServer')
		var name      = $form.find('#benchmark-name').first().val()
		var database  = $form.find('#benchmark-database').first().val()
		
		var scaleFactors  = $form.find("#benchmark-scale-factors").first().val()
		var duration  = $form.find("#benchmark-duration").first().val()
		
		var url = '/benchmark/' + name +'/' + database + '/'
		var payload = []
		var factors = scaleFactors.split(',')
		for (var i = 0; i < factors.length; i++) {
			var p = {
					scaleFactor: factors[i],
					timeToRun: duration
			}
			payload.push(p)
		}
		console.log('payload:')
		console.log(payload)
		$.ajax({
			url: url,
			type: 'POST',
			contentType:"application/json; charset=utf-8",
			data: JSON.stringify(payload)
		}).done(function(task){
			console.log('received result (pending)')
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
		$('#run-benchmark-dialog').hide()
	}
}