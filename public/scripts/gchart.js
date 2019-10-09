/**
 * chart function
 */


var QUANTITIES = {
		'STORAGE' : {color:'blue'},
		'MEMORY'  : {color:'green'},
		'COMPUTE' : {color:'yellow'}
		
}



class Google {
	constructior() {
		
	} 

	/**
	 * draws a pie chart in a div
	 * @param attr one of memory, storage, cpu
	 * @param available dictionary of double indexed by attr
	 * @param total dictionary of doubles indexed by attr
	 * return div
	 */
    drawPieChart(attr, available, total) {
		var $chart = $('<div>')
		var tot = total[attr]['value']
		var used = tot - available[attr]['value']
		var unit = total[attr]['unit']
		var chartData = google.visualization.arrayToDataTable([
			['Total',  'Used'],
			['Total ' + tot + ' ' + unit, tot],
			['Used ' + used + ' ' + unit,  used]
		])
		
		var style = QUANTITIES[attr]
		var options = {
	          title: attr + ' utilization',
	          sliceVisibilityThreshold: 0,
	          colors: [style, 'red'],
	          is3D: true
	        };
	
	    var chart = new google.visualization.PieChart($chart[0]);
	
	    chart.draw(chartData, options);
	    
	    return $chart
    }
    
     drawQuantityUtilization ($utilization, available, total) {
    	$utilization.empty()
    	for (var attr in QUANTITIES) {
    		var $piechart = this.drawPieChart(attr, available, total )
    		$piechart.addClass('w3-container w3-cell w3-margin')
    		$utilization.append($piechart)
        }
    }

    drawQuantityDistribution($distribution, elements) {
    	$distribution.empty()
    	for (var q in QUANTITIES) {
    		var lineData = this.computeDistribution(q, elements)
    		var $plot = this.drawDistribution(lineData);
    		$distribution.append($plot)
    	}
    }
    /**
     * creates a sequence of given attribute values
     * across all VMs
     * Returns a distribution in following form
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
    computeDistribution(q, elements) {
    	var array = {}
    	var distribution = {
    			'name' : q,
    			'size' : elements.length,
    			'array' : array
    	}
    	for (var i = 0; i < elements.length; i++) {
    		var e = elements[i]
    		array[e.name] = e.getQuantity(q)
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
    				
    	chartData.push(['name', 'available', 'used'])
    	
    	for (var e in dist.array) {
    		var values = dist.array[e]
    		var available = values[0]
    		var total     = values[1]
    		var used = total - available
    		chartData.push([e, available, used])
    	}
    	
    	var chart = new google.visualization.ColumnChart($chart[0]);
    	var N = chartData.length
    	var options = {
    	        title: 'distribution of ' + dist.name + ' capacity across ' + N + ' vms',
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
}
