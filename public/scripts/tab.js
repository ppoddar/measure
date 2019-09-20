/**
 * singleton to manage tabs
 */


var instance

class TabManager {
	/**
	 * provide two jQuery elements
	 * @param $tab a tab bar where all tab controls are hosted
	 * @param $tabView a area where views are hosted
	 */
	constructor($tab, $tabView) {
		if ($tab.length == 0) throw 'no tab control'
		if ($tabView.length == 0) throw 'no tab viewing area'
		this.tab = $tab
		this.view = $tabView
		instance = this
	}
	/**
	 * gets singleton tab manager.
	 */
	static instance() {
		if (instance == undefined) 
			throw 'tab manager is not initialized'
		return instance
	}
	/**
	 * shows a named tab with given content
	 * empties previous content
	 */
	showTab(tabName, $content) {
		var $tabControl = this.tab.children('#' + tabName).first()
		if ($tabControl.length == 0) 
			throw 'tab [' + tabName + '] not found'
			
		var $el = $tabControl.data('content')
		$el.empty()
		$el.append($content)
		$tabControl.trigger('click')
	}
	
	/**
	 * adds a tab
	 * 
	 * @param text text for tab control
	 * @param content id of tab content element.
	 * content is hidden till the tab control button is clicked
	 * @param pointer to a section in an HTML page that
	 * would be the content. optional 
	 */
	addTab(tabName, content, htmlFragment) {
		console.log('creating tab [' + tabName + '] from ' + htmlFragment)
		var $tabContent = $('<div>')
		$tabContent.attr('id', content)
		if (htmlFragment)
			$tabContent.load(htmlFragment)
		else
			$tabContent = $('<div>')
			
		$tabContent.addClass('tab-content w3-margin')
		this.view.append($tabContent)
		
		var $tabControl = $('<button>')
		$tabControl.text(tabName)
		$tabControl.addClass('w3-bar-item w3-black')
		$tabControl.attr('id', tabName)
		$tabControl.data('content', $tabContent)
		
		this.tab.append($tabControl)
		
		$tabControl.on('click', function(e) {
			var $tabContent = $(this).data('content')
			
			console.log('clicked tab-control ' 
					+ $(this).attr('id')
					+ ' content ' + $tabContent.attr('id'))
			$('.tab-content').hide()
			$tabContent.show()
		})
	}
}

/**
 * a tab page is created with identifer of
 * a DOM element
 */
class Tab {
	constructor(id) {
		this.id = id
		this.el = $(this.id)
		if (this.el.length == 0) {
			throw 'tab page [' + this.id + '] does not exist'
		} else {
			this.el.empty()
		}
	}
	
	append($el) {
		this.el.append($el)
		return this
	}
	
	show() {
		$('.tab-content').each(function() {
			$(this).hide()
		})
		this.el.show()
	}
}