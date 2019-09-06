class Drawing {
	constructor() {
		
	}
	createSnapshotView(snapshot, $container) {
		$container.empty()
		$container.addClass('w3-container w3-border')
		
		console.log('display snapshot')
		console.log(snapshot)
		
		var $nameLabel = $('<span>')
		$nameLabel.text('Snapshot:')
		var $name = $('<span>')
		$name.text(snapshot['name'])
		$name.css('font-weight', 'bold')
		
		var $countLabel = $('<span>')
		$countLabel.text('Measurements (actual/expected):')
		var $count = $('<span>')
		var actual   = snapshot['actualMeasurementCount']
		var expected = snapshot['expectedMeasurementCount']
		var text = actual + '/' + expected 
		if (actual >= expected) {
			text += ' (completed)'
		}
		$count.text(text)
		
		var $startTimeLabel = $('<span>')
		var $endTimeLabel   = $('<span>')
		$startTimeLabel.text('Started:')
		$endTimeLabel.text('Ended:')
		var $startTime = $('<span>')
		var $endTime   = $('<span>')
		var startTime = -1
		if ('startTime' in snapshot) {
			startTime = snapshot['startTime']
		}
		
		var endTime   = -1
		if ('endTime' in snapshot) {
			endTime = snapshot['endTime']
		}
		$startTime.text(startTime.toString())
		$endTime.text(endTime.toString())
	
		var $durationLabel = $('<span>')
		if (startTime > 0 && endTime > 0) {
			var duration = endTime - startTime
			$durationLabel.text('time taken:' + duration + ' ms')
		} else {
			$durationLabel.text('time taken: ')
		}
		$container.append($nameLabel, $name, $('<p>'),
					$countLabel, $count, $('<p>'),
					$startTimeLabel, $startTime, $('<p>'),
					$endTimeLabel, $endTime, $('<p>'),
					$durationLabel
					)
	
		var dimensions = snapshot.metrics.dimensions
		var $table = $('<table>')
		$table.addClass('w3-table')
		$container.append($table)
		for (var i = 0; i < dimensions.length; i++) {
			var d = dimensions[i]
			var $tr = $('<tr>')
			var $td1 = $('<td>')
			$td1.text(d.name)
			$td1.css('font-weight', 'bold')
			
			var $td2 = $('<td>')
			if (d.numeric) {
				//console.log('drawing chart for ' + d.name)
				var $chart = $('<div>')
				this.drawChart(snapshot.measurements, d.name, $chart[0])
				$td2.append($chart)
			} else {
				//console.log('drawing label for ' + d.name)
				var val = snapshot.measurements.data[0].values[d.name]
				$td2.text('' + val + ' ('
					+ (snapshot.measurements.data.length-1) 
					+ ' more values)')
			}
			$tr.append($td1, $td2)
			$table.append($tr)
		}
	}

	
	
	
	
	createBenchmarkView(snapshot, $container) {
		$container.empty()
		$container.addClass('w3-container w3-border')
		var $nameLabel = $('<span>')
		$nameLabel.text('Benchmark:')
		var $name = $('<span>')
		$name.text(snapshot['name'])
		$name.css('font-weight', 'bold')
		
		var $chart = $('<div>')
		this.drawTrend(snapshot.measurements, 
				'scale', 'tps', $chart[0])
		$container.append($chart)
	}
	
	/**
	 * draw a google chart given a measurement dimension.
	 * 
	 * @param measurements
	 *            array of measurements. Each measurement has 'data' which has
	 *            'values'
	 * @param y
	 *            range axis name
	 * @param el
	 *            an HTML element (not a jQuery element)
	 */
    drawChart(measurements, y, el) {
		var chartData = new google.visualization.DataTable();
		chartData.addColumn('number', 'time')
		chartData.addColumn('number', y)
		
		for (var i = 0; i < measurements.data.length; i++) {
			var e = measurements.data[i]
			var values = e.values
			chartData.addRow([e.startTime, values[y]])
		}
		var chart = new google.visualization.LineChart(el);
		var options = {}
		chart.draw(chartData, options)
	}
	
    drawTrend(measurements, x, y, el) {
		var chartData = new google.visualization.DataTable();
		chartData.addColumn('number', x)
		chartData.addColumn('number', y)
		
		for (var i = 0; i < measurements.data.length; i++) {
			var e = measurements.data[i]
			console.log(e)
			var values = e.values
			console.log("measurement data values " + JSON.stringify(values))
			console.log('' + x + '=' + values[x])
			console.log('' + y + '=' + values[y])
			chartData.addRow([parseInt(values[x]), parseInt(values[y])])
		}
		var chart = new google.visualization.LineChart(el);
		var options = {}
		chart.draw(chartData, options)
	}
}
