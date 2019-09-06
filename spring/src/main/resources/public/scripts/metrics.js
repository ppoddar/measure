class Metrics {
	constructor() {
		
	}
	/**
	 * fetch catalog from server
	 * and display all metrices
	 */
	getCatalog($div) {
		var _this = this
		$.ajax({
			url: 'metrices'
		}).done(function(data){
			_this.drawTree(data, $div)
		})
	}
	
	drawTree(data, $div) {
		$div.empty()
		var $tree = $('<div>')
		var $desc = $('<div>')
		$tree.addClass('w3-quarter w3-padding')
		$desc.addClass('w3-threequarter')
		var $ul = $('<ul>')
		for (var i = 0; i < data.length; i++) {
			var $li = this.drawMetrics(data[i], $desc)
			$ul.append($li)
		}
		$tree.append($ul)
		$div.append($tree, $desc)
	}
	
	/**
	 * create an <li> item for a metrics.
	 * all dimensions are sub-node of <li> items
	 * If <li> item is clicked, the item is 
	 * described in given view area
	 */
	drawMetrics(data) {
		var _this = this;
		var $li = $('<li>')
		$li.data('data', data)
		
		$li.addClass('w3-bold')
		$li.text(data['name'])
		var $dims = $('<ul>')
		for (var i = 0; i < data['dimensions'].length; i++) {
			var dim = data['dimensions'][i]
			var $dim = this.drawDim(dim);
			$dims.append($dim)
		}
		$li.append($dims)
		$dims.hide()
		$li.on('click', function(e) {
			e.stopPropagation()
			$(this).children('ul').toggle()
		})
//		$li.on('click', function() {
//			_this.describe($(this).data('data'), $desc)
//		})
		return $li
	}
	
	
	
	drawDim(data) {
		var $li = $('<li>')
		var $name = $('<span>')
		$name.text(data['name'])
		$name.css('font-weight', 'bold')
		var $type = $('<span>')
		$type.text(data['sql-type'])
		$type.addClass('w3-text-purple w3-margin')
		$li.append($name, $type)
		return $li
	}
	
//	describe(data, $container) {
//		$container.empty()
//		var $title = $('<h2>')
//		$title.text(data['name'])
//		
//		var $desc = $('<p>')
//		$desc.text(data['description'])
//		$container.append($title, $('<hr>'), $desc, $('<hr>'))
//		var $dimensions = $('<ul>')
//		$dimensions.text('Dimensions')
//		$container.append($dimensions)
//		for (var i = 0; i < data['dimensions'].length; i++) {
//			var dim = data['dimensions'][i]
//			var $dim = $('<li>')
//			$dim.text(dim['name'] + ':' + dim['sql-type'])
//			$dimensions.append($dim)
//		}
//		$container.append($dimensions)
//	}
}