var WIZARD_PAGE = 'tab'
Wizard {
	constructor() {
		
	}
	
	createPages(formId,action,options) {
		var $form = $('<form>')
		$form.on('submit', action)
		
		for (var i = 0; i < options.length; i++) {
			$form.append(this.createOption(options[i]))
		}


	}
	/**
	 * create input to display an option
	 */
	createOption(option) {
		var $div = $('<div>')
		$div.addClass(WIZARD_PAGE)
			
		var $label  = $('<label>')
		var $input  = $('<input>')
		$label.text(option.key)
		$input.attr('placeholder', option.plaeholder)
		$input.attr('id', option.key)
		$div.append($label, $input)
		
		return $div
		  
	}
	
}