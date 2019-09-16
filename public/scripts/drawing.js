/**
 * draws using google chart library
 * 
 */
class Drawing {
	constructor() {
		this.options = {
				
		}
	}
	
	createSnapshotView(snapshot, $container) {
		$container.empty()
		$container.addClass('w3-container w3-border')
		
		console.log('display snapshot')
		console.log(snapshot)
		var $heading = $('<div>')
		$heading.html('Plots each dimension of measurments taken in a snapshot.'
				+ ' <br>A snapshot is a set of measurments taken in regular'
				+ ' interval under a specific databse workload'
				+ ' A snapshot, currently, uses a single metrics'
				+ ' Each plot below shows value of a metric dimension '
				+ ' for each measurment against time<br>')
		
		var $nameLabel = $('<span>')
		$nameLabel.addClass('w3-margin')
		$nameLabel.text('Snapshot:')
		var $name = $('<span>')
		$name.text(snapshot['name'])
		$name.addClass('w3-text-blue')
		$name.css('font-weight', 'bold')
		
		var $countLabel = $('<span>')
		$countLabel.addClass('w3-margin')
		$countLabel.text('Measurements (actual/expected):')
		var $count = $('<span>')
		$count.css('font-weight', 'bold')
		var actual   = snapshot['actualMeasurementCount']
		var expected = snapshot['expectedMeasurementCount']
		var text = actual + '/' + expected 
		$count.text(text)

		var $status = $('<span>') 		
		$status.text("")
		if (actual >= expected) {
			$status.text(' (completed)')
			$status.addClass('w3-text-green')
		}
		
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
		$startTime.text(new Date(startTime))
		$endTime.text(new Date(endTime))
	
		var $durationLabel = $('<span>')
		if (startTime > 0 && endTime > 0) {
			var duration = endTime - startTime
			$durationLabel.text('time taken:' + duration/1000 + ' s')
		} else {
			$durationLabel.text('time taken: ')
		}
		$container.append($heading,
				    $nameLabel, $name, $('<p>'),
					$countLabel, $count, $status, $('<p>'),
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
				var stats = snapshot.measurements.ranges[d.name]
				var val = stats['most-frequent-value']
				$td2.text('' + val + ' ('
					+ stats['distinct-values']
					+ ' distinct values)')
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
