/**
 * view of a virtual machine from resource capacity utilization
 */
class VM {
	constructor(data) {
		if (!('id' in data)) throw 'missing [id] key. available keys are ' + Object.keys(data)
		if (!('name' in data)) throw 'missing [name] key. available keys are ' + Object.keys(data)
		if (!('totalCapacity' in data)) throw 'missing [totalCapacity] key. available keys are ' + Object.keys(data)
		if (!('availableCapacity' in data)) throw 'missing [availableCapacity] key. available keys are ' + Object.keys(data)
		this.id   = data['id']
		this.name = data['name']
		this.total     = new Capacity(data['totalCapacity'])
		this.available = new Capacity(data['availableCapacity'])
	}
	
	showUtilization() {
		var $accordion = $('<div>')
		var $control   = $('<button>')
		$control.text('VM: ' + this.name)
		
		var chartId = 'chart-vm-' + this.id
		var $chart = this.drawBarChart(this.available, this.total )
		$chart.attr('id', chartId)
		$control.on('click', function(e) {
			$('#'+chartId).toggle()
		})
		$accordion.append($control, $chart)
		return $accordion
	}
	
	/**
	 * draw a horizontal bar chart for MEMEORY, COMPUTE and STOARGE
	 * 
	 * @param available capacity
	 * @param total capacity
	 */
	drawBarChart(available, total) {
		var $chart = $('<div>')
		var chartData = google.visualization.arrayToDataTable(
		[
			['Kind',   'Available',               'Total',     { role: 'annotation' }],
			['Memory', available.memory['value'],  total.memory['value'],   'MEMORY'],
			['CPU',    available.cpu['value'],     total.cpu['value'],      'COMPUTE'],
			['Disk',   available.storage['value'], total.storage['value'],  'STORAGE']
		])
				
		var chart = new google.visualization.BarChart($chart[0]);
		var options = {
		        title: 'Available/Total Capacity',
		        chartArea: {width: '50%'},
		        legend:{position:'bottom'},
		        bar: {groupWidth:'95%'},
		        isStacked:'percent',
		        series: {
		        	0:{color:'blue'},  // available
		        	1:{color:'green'} // total
		        }
		      };
		
		chart.draw(chartData, options)
		return $chart
	}

	createItem() {
		var $vm = $('<li>')
		$vm.data('vm', this)
		$vm.on('click', function(e){
			e.stopPropagation()
			var vm = $(this).data('vm')
			console.log('clicked on vm')
			console.log(vm)
			var $chart = vm.showUtilization()
			new Tab('#utilization-view').append($chart).show()
		})
		var fullName = this.name
		$vm.attr('title', fullName)
		var $label = $('<span>')
		$label.text(fullName)
		$label.addClass('w3-small')
		$vm.append($label)
		return $vm
	}
	
	/**
	 * creates a array of two numbers for available
	 * and total value of the given quantity
	 */
	getQuantity(q) {
		if (!(q in this.available)) 
			throw 'missing quantity [' + q + ']'
		+ ' availble quantities are [' + Object.keys(this.available) + ']'
		if (!(q in this.total)) 
			throw 'missing quantity [' + q + ']'
		+ ' availble quantities are [' + Object.keys(this.total) + ']'
		return [this.available[q]['value'],  this.total[q]['value']]
	}
}
