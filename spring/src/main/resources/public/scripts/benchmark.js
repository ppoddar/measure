	runBenchmark(url, payload) {
		console.log('run benchmark ' + url + ' with payload:' 
				+ JSON.stringify(payload))
		$.ajax({
			url: url,
			type: 'POST',
			contentType:"application/json; charset=utf-8",
			data: JSON.stringify(payload)
		}).done(function(data){
			console.log('received result (pending)')
			console.log(data)
			TaskQueue.addTask(data)
		}).fail(function(err) {
			console.log('***ERROR')
			console.log(err)
			showErrorDialog(err.responseText)
		})
	}
