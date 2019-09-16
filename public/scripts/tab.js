/**
 * 
 */
class Tab {
	constructor(id) {
		this.id = id
	}
	
	show() {
		var $tab = $(this.id)
		console.log('tab element to show ' + this.id)
		$('.tab-content').each(function() {
			$(this).hide()
		})
		$tab.show()
	}
}