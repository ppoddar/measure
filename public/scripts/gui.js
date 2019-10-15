/**
 * create navigation bar.
 * each bar item when clicked, show the perspective
 * @param config
 * @returns
 */
function createBar(config) {
	var $bar = $('#tab-bar')
	$bar.addClass('w3-bar ' + config.style.color.bar)
	for (var i = 0; i < config.perspectives.length; i++) {
		var perspective = config.perspectives[i]
		var $item = $('<div>')
		$item.addClass('w3-bar-item w3-button')
		$bar.append($item)
		$item.text(perspective.name)
		$item.data('perspective', perspective)
		$item.on('click', function(e) {
			var perspective = $(this).data('perspective')
			console.log('loading perspective [' + perspective.name + '] page=' + perspective.page)
			
			$('#perspective').load(perspective.page, function() {
				// HTML page is loaded. 
				// decorate loaded page with style
				decorate($(this), config['style'])
			})
		})
	}
}

function decorate(perspective, config) {
//	find(perspective, '#bar').addClass(config.style.color.bar2)
}




