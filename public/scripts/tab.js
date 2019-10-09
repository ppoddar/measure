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
		if ($tab.length == 0)     throw 'no tab control bar'
		if ($tabView.length == 0) throw 'no tab viewing area'
		this.tab  = $tab
		this.view = $tabView
		this.tabControls = {}
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
	showTab($tabContent) {
		if (!$tabContent ||  $tabContent.length == 0) 
			throw 'no content has been supplied'
		
		var $view = this.view
		$view.empty()
		$view.append($tabContent)
		$tabContent.show()
	}
	
	
	
	/**
	 * adds a tab.
	 * 
	 * @param text text for tab control
	 */
	addTab(tabName, $tabContent) {
		var $tabControl = $('<button>')
//		$tabControl.text(tabName)
//		$tabControl.addClass('w3-bar-item w3-black')
		$tabControl.attr('id', tabName)
		$tabControl.data('content', $tabContent)
		$tabControl.data('view', this.view)
		this.tab.append($tabControl)
//		$tabContent.addClass('tab-content')
//		$tabContent.hide()
		/**
		 * tabControl shows content at tab view
		 * caller must provide the content
		 */
		$tabControl.on('click', function(e) {
			var $tabContent = $(this).data('content')
			if (!$tabContent ||  $tabContent.length == 0) 
				throw 'no content has been atached to tab-control ' 
				+ $(this).attr('id')
			console.log('clicked tab-control ' 
					+ $(this).attr('id')
					+ ' content ' + $tabContent.attr('id'))
			$('.tab-content').hide()
			
			var $view = $(this).data('view')
			$view.empty()
			$view.append($tabContent)
			$tabContent.show()
		})
		
		return $tabControl
	}
}
