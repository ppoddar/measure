var POOLS = {}

//  Pool -> providers[]   -> 
//         = Clusters[] -> VMS[] -> capacity
class ResourcePool {
	constructor(data) {
		console.log('populate:')
		console.log(data)
		this.id = data['id']
		this.name = data['name']
		this.data = data
		this.clusters = []
		for (var i = 0; i < data.providers.length; i++) {
			var cluster = new Cluster(data.providers[i])
			this.clusters.push(cluster)
		}
	}
	
	/**
	 * fetch all pool items and shows in a tree
	 * pool -> cluster -> vm in a sidebar
	 * The tree nodes on click shows capacity 
	 * utilization view
	 * 
	 * @param the sidebar tree that would be populated
	 */
	static fetchAllPools($ul) {
		var url = 'resource/pools/'
		$.ajax({
			url: url
		}).done(function(data) {
			console.log('fetched data for ' + data.length + ' pool')
			for (var i = 0; i < data.length; i++) {
				var pool = new ResourcePool(data[i]) 
				POOLS[pool.id] = pool
				var $li = pool.createItem()
				$ul.append($li)
			}
		}).fail(function(data){
			console.log('***ERROR')
			console.log(data)
		})
	}
	
	/**
	 * Creates list item for pool.
	 * A list shows all clusters in pool. 
	 */
	createItem() {
		var $pool = $('<li>')
		var $label = $('<span>')
		$label.text(this.name + ' (' + this.clusters.length + ' clusters)')
		$pool.append($label)
		$label.css('font-weight', 'bold')
		$pool.data('pool', this)
		$pool.on('click', function(e){
			//console.log('show capacity')
			var pool = $(this).data('pool')
			var $chart = pool.showUtilization('#cluster-view')
		})
		var $clusters = $('<ul>')
		$pool.append($clusters)
		for (var i = 0; i < this.clusters.length; i++) {
			var $cluster = this.clusters[i].createItem()
			$clusters.append($cluster)
		}
		return $pool
	}
	
	/**
	 * show utilization of this pool and 
	 * capacity distribution over each cluster.
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
		$description.html(' <b>' + this.name + '</b> pool has'
				+ ' <b>' + this.clusters.length + '</b> clusters.<p>'
				+ ' Showing <b>capacity utilization</b>'
				+ ' of entire pool '
				+ ' and of individual clusters<br>'
				+ ' <b>Capacity utilization</b> of a pool'
				+ ' is weighted avrage of utilization'
				+ ' of individual clusters. ')
				
				
		var $utilization = find($page,'#chart-utilization')

		$control.on('click', function(e) {
			$utilization.toggle()
		})
		
		var total     = new Capacity(this.data['totalCapacity'])
		var available = new Capacity(this.data['availableCapacity'])
		new Google().drawQuantityUtilization($utilization, available, total)
		
		var $distribution = find($page,'#chart-distribution')
		new Google().drawQuantityDistribution($distribution, this.clusters)
		return $page
	}
}