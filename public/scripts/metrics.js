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
		var $dims = $('<table>')
		$dims.addClass('w3-margin-left')
		for (var i = 0; i < data['dimensions'].length; i++) {
			var dim = data['dimensions'][i]
			var $dim = this.drawDim(dim);
			$dims.append($dim)
		}
		$li.append($dims)
		$dims.hide()
		$li.on('click', function(e) {
			e.stopPropagation()
			$(this).children('table').toggle()
		})
		return $li
	}
	
	
	
	drawDim(dim) {
		var $row = $('<tr>')
		var $name = $('<td>')
		$name.text(dim['name'])
		$name.addClass('w3-margin-right')
		var $type = $('<td>')
		$type.text(dim['sql-type'])
		$type.css('font-weight', 'bold')
		$row.append($name, $type)
		return $row
	}
	
}