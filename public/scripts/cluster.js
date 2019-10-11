/**
 * cluster represents a set of VM
 * utilization is utilization of all VMs
 */
class Cluster {
	constructor(data) {
		console.log('cluster data')
		console.log(data)
		this.id   = data['id']
		this.name = data['name']
		this.host = data['host']
		this.total     = new Capacity(data['totalCapacity'])
		this.available = new Capacity(data['availableCapacity'])
	}
	
	/**
	 * show utilization of this cluster and 
	 * capacity distribution over each VMs.
	 * 
	 * populates given page template
	 * 
	 * @return a jQuery <div>  element. 
	 */
	showUtilization() {
		var $page = $('<div>')
		var $control   = $('<h1>')
		$control.text(this.name)
		
		var $description = $('<p>')
		$description.text('IP address:' + this.host)
				
		var $utilization = new Google()
			.drawQuantityUtilization(
					this.available, this.total) 

		$control.on('click', function(e) {
			$utilization.toggle()
		})
		
		$page.append($control, $description, $utilization)
		return $page
	}
	
	
	/**
	 * create a list item for this cluster
	 * list item when clicked shows utilization
	 * on 'cluster view'
	 */
	createItem() {
		var $cluster = $('<li>')
		var $text = $('<span>')
		$text.text(this.name)
		$text.addClass('w3-text-green')
		$cluster.append($text)
		$cluster.data('cluster', this)
		
		/**
		 * cluster item on click displays 'cluster-view'
		 * on t=right side viewing area
		 */
		$cluster.on('click', function(e) {
			e.stopPropagation()
			$(this).parent().find('ol').toggle()
			var cluster = $(this).data('cluster')
			$('#cluster-view').empty()
			$('#cluster-view').append(cluster.showUtilization())
		})
		return $cluster
	}
	
	/**
	 * creates a array of two numbers for available
	 * and total value of the given quantity
	 */
	getQuantity(q) {
		return [this.available[q]['value'],  
			    this.total[q]['value']]
	}

}
