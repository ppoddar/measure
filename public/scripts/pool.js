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
	
	createItem() {
		console.log('createPoolItem ' + this.name)
		console.log(this)
				
		var $li = $('<li>')
		$li.text(this.name + ' (' + this.clusters.length + ' clusters)')
		$li.data('pool', this)
		var pool = this
		$li.on('click', function(e){
			console.log('show capacity')
			var pool = $(e.target).data('pool')
			var $chart = pool.showUtilization()
			new Tab('#utilization-view').append($chart)
		})
		var $ul = $('<ul>')
		$li.append($ul)
		for (var i = 0; i < this.clusters.length; i++) {
			var $subItem = this.clusters[i].createItem()
			$ul.append($subItem)
		}
		return $li
	}
}