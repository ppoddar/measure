var QUANTITY_NAMES = {
		'STORAGE' : 'storage',
		'MEMORY'  : 'memory',
		'COMPUTE' : 'cpu'
		
}

class Cluster {
	constructor(data) {
		this.id   = data['id']
		this.name = data['name']
		console.log('creating cluster ' + this.name + ' (id=' + this.id + ')')
		this.data = data
		this.vms = []
		for (var j = 0; j < data.resources.length; j++) {
			var vm = new VM(data.resources[j])
			this.vms.push(vm)
		}
	}
	
	/**
	 * show utilization of this cluster and 
	 * capacity distribution over each VMs.
	 * 
	 * @return a jQuery <div>  accordion element. 
	 */
	showUtilization() {
		var $accordion = $('<div>')
		
		var $control   = $('<button>')
		$control.text('Cluster: ' + this.name)
		$control.addClass('w3-text-blue w3-xxxlarge w3-margin')
		
		var $description = $('<div>')
		$description.addClass('w3-border w3-pale-yellow w3-padding-48 w3-margin w3-card')
		$description.html(' This cluster has'
				+ ' <span>' + this.vms.length + '</span><p>'
				+ ' Capacity utilization of a cluster'
				+ ' is weighted avrage of utilization'
				+ ' of individual VMs. Utilization of a VM '
				+ ' is ratio of available and total capacity '
				+ ' of each resource e.g. memory, disk, cpu<br>'
				+ ' virtual machines.<br>'
				+ ' Schematic below shows capacity utilization'
				+ ' of each reource over entire cluster '
				+ ' and individual VMs')
				
				
		var chartId = 'chart-cluster-' + this.id
		
		var total     = new Capacity(this.data['totalCapacity'])
		var available = new Capacity(this.data['availableCapacity'])
		
		var $chart = this.drawBarChart(available, total )
		$chart.attr('id', chartId)
		$control.on('click', function(e) {
			$('#'+chartId).toggle()
		})
		$accordion.append($control, $description, $chart)
		return $accordion

	}
	
	/**
	 * draws a stacked bar chart of available/total capacity
	 * @return a <div> where chart is drawn
	 */
	drawBarChart(available, total) {
		var $chart = $('<div>')
		var usedColor = 'grey'
		var chartData = google.visualization.arrayToDataTable([
			['quantity',       'available',               {role:'style'},      'used',                                            {role:'style'}],
			['memory',     available['MEMORY']['value'],        'blue',          total['MEMORY']['value']  - available['MEMORY']['value'],     usedColor],
			['cpu',        available['COMPUTE']['value'],       'green',		 total['COMPUTE']['value'] - available['COMPUTE']['value'],    usedColor],
		    ['storage',    available['STORAGE']['value'],       'yellow',  	     total['STORAGE']['value'] - available['STORAGE']['value'],    usedColor]
		 ])
//	
//
//		var chartData = new google.visualization.DataTable()
//		
//		chartData.addColumn('string', 'Quantity')
//		chartData.addColumn('number', 'Available')
//		chartData.addColumn('number', 'Used')
//		
//		chartData.addRow(['Memory', available.memory['value'],  total.memory['value'] - available.memory['value']])
//		chartData.addRow(['CPU',    available.cpu['value'],     total.cpu['value'] - available.cpu['value']])
//		chartData.addRow(['Disk',   available.storage['value'], total.storage['value'] - - available.storage['value']])
		
	var options = {
	        title: 'Capacity utilization',
	        chartArea: {width: '50%'},
	        legend:'none',
	        bar: {groupWidth:'90%'},
	        isStacked:'percent',
	        chartArea: {
	            backgroundColor: {
	                stroke: 'black',
	                strokeWidth: 2
	            }
	        }
	      };
		var chart = new google.visualization.BarChart($chart[0]);
		chart.draw(chartData, options)
		
		for (var q in QUANTITY_NAMES) {
			var lineData = this.computeDistribution(q)
			var $plot = this.drawDistribution(lineData);
			$chart.append($plot)
		}
		
		return $chart
	}
	
	/**
	 * creates a sequence of given attribute values
	 * acorss all VMs
	 * Returns a distribution in followin form
	 * {
	 *   name: 'memory distribution',
	 *   data: {
	 *   	vm1: [10, 24],
	 *      vm2: [20, 30],
	 *      ...
	 *   }
	 *  }
	 *  of [available,total] over all VMs for gien quantity
	 */
	computeDistribution(q) {
		var array = {}
		var distribution = {
				'name' : q,
				'size' : this.vms.length,
				'array' : array
		}
		for (var i = 0; i < this.vms.length; i++) {
			var vm = this.vms[i]
			var availableAndTotal = vm.getQuantity(q)
			array[vm.name] = availableAndTotal
		}
		console.log('distribution ')
		console.log(distribution)
		return distribution
	}
	
	/**
	 * a stacked vertical bar chart
	 */
	drawDistribution(dist) {
		var $chart = $('<div>')
		$chart.css('width', '1000px')
		var chartData = []
			
		chartData.push(['VM name', 'available', 'used'])
		for (var vm in dist.array) {
			var values = dist.array[vm]
			var available = values[0]
			var used = values[1] - available
			console.log(vm + ': available:' + available + ' used:' + used)
			chartData.push([vm, available, used])
		}
		
		var chart = new google.visualization.ColumnChart($chart[0]);
		var options = {
		        title: dist.name,
		        width: '1000',
		        chartArea: {width: '50%'},
		        legend:{position:'bottom'},
		        bar: {groupWidth:'95%'},
		        hAxis: { textPosition: 'none' },
		        isStacked: 'true',
		        series: {
		        	'0':{},
		        	'1':{}
		        },
		        chartArea: {
				    backgroundColor: {
				        stroke: '#4322c0',
				        strokeWidth: 3
				    }
				}
		      };
		
		chart.draw(new google.visualization.arrayToDataTable(chartData), options)
		
		return $chart
	}
	
	createItem() {
		var $li = $('<li>')
		var $text = $('<span>')
		$text.text(this.name
				+ ' (' + this.vms.length + ' VMs)')
		$text.addClass('w3-text-green')
		$li.append($text)
		$li.data('cluster', this)
		var $ol = $('<ol>')
		$li.on('click', function(e) {
			e.stopPropagation()
			var cluster = $(this).data('cluster')
			console.log('clicked on cluster ' + cluster.name)
			var $chart = cluster.showUtilization()
			$chart.attr('id', 'google chart')
			TabManager.instance().showTab('utilization', $chart)
		})
		$li.append($ol)
		for (var i = 0; i < this.vms.length; i++) {
			var vm = this.vms[i]
			var $vm = vm.createItem()
			$ol.append($vm)
		}
		$ol.on('click', function(e) {
			$(this).children().toggle()
		})
		return $li
	}
}
