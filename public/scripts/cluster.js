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
		this.data = data
		this.vms = []
		for (var j = 0; j < data.resources.length; j++) {
			var vm = new VM(data.resources[j])
			this.vms.push(vm)
		}
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
	showUtilization(pageId) {
		var $page = jq(pageId)
		var $control   = find($page,'#header')
		$control.text(this.name)
		
		var $description = find($page, '#description')
		$description.html(' <b>' + this.name + '</b> cluster has'
				+ ' <b>' + this.vms.length + '</b> VMs.<p>'
				+ ' Showing <b>capacity utilization</b>'
				+ ' of entire cluster '
				+ ' and of individual VMs<br>'
				+ ' <b>Capacity utilization</b> of a cluster'
				+ ' is weighted avrage of utilization'
				+ ' of individual VMs. '
				+ ' Capacity utilization of a VM is ratio of used and total capacity '
				+ ' of memory, disk and cpu.<br>')
				
				
		var $utilization = find($page,'#chart-utilization')

		$control.on('click', function(e) {
			$utilization.toggle()
		})
		
		var total     = new Capacity(this.data['totalCapacity'])
		var available = new Capacity(this.data['availableCapacity'])
		new Google().drawQuantityUtilization($utilization, available, total)
		
		var $distribution = find($page,'#chart-distribution')
		new Google().drawQuantityDistribution($distribution, this.vms)
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
		$text.text(this.name + ' (' + this.vms.length + ' VMs)')
		$text.addClass('w3-text-green')
		$cluster.append($text)
		$cluster.data('cluster', this)
		$text.on('click', function(e){
			$(this).parent().find('ol').toggle()
		})
		
		$cluster.on('click', function(e) {
			e.stopPropagation()
			var cluster = $(this).data('cluster')
			cluster.showUtilization('#cluster-view')
		})
		
		var $vms = $('<ol>')
		$cluster.append($vms)
		for (var i = 0; i < this.vms.length; i++) {
			var vm = this.vms[i]
			var $vm = vm.createItem()
			$vms.append($vm)
		}
		$vms.hide()
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
