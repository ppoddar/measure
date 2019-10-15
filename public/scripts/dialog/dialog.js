class Dialog {
	constructor(name) {
		this.$dialog = $('<div>')
		this.$dialog.addClass('w3-modal')
		
		this.$form = jq(name)
		this.$form.removeClass('w3-hide')
		this.$form.addClass('w3-modal-content')
		this.$dialog.append(this.$form)
		this.$dialog.css('display', 'block')
		$('body').append(this.$dialog)
	}
	/**
	 * shows dialog with submit function
	 * @param fn
	 * @returns
	 */
	show(fn) {
		if (fn == undefined) {
			throw 'no submit function for ' + this
		}
		this.$form.on('submit', fn)
		this.$dialog.show()
		this.$dialog.children().css('display', 'block')
	}
	
	hide() {
		this.$dialog.hide()
	}
}


/**
 * prepares a HTML element as dialog
 * appends given element in a dialog
 * @param $el a normal html element
 * @returns
 */
function prepareDialog($el) {
	// show the element
	$el.removeClass('w3-hide')
	
	var $dialog = $('<div>')
	$dialog.addClass('w3-modal')
	
	$el.addClass('w3-modal-content')
	$dialog.append($el)
	
//	var $container = $('<div>')
//	var $closeButton = $('<button>')
//	$closeButton .addClass('w3-button w3-display-topright w3-hover-red w3-margin w3-round')
//	$closeButton.text('close')
//	$closeButton.on('click', function() {
//		$dialog.hide()
//	})
//	$container.append($closeButton, $el)
//	$content.append($container)
	
	$('body').append($dialog)
	return $dialog

} 

function showDialog($dialog) {
	if ($dialog.length == 0) {
		throw 'dialog not defined'
	}
	$dialog.draggable()
	//$el.resizable()
	$dialog.css('display', 'block')
	$dialog.show()
	$dialog.children().css('display', 'block')
}

function openJobSubmitDialog(e) {
	// IMPORTANT
	e.stopPropagation()
	e.preventDefault()
	var $form = jq('#job-submit-dialog')
	fillJobSubmitForm($form)
	var $dialog = prepareDialog($form)
	console.log('showing ' + $dialog.attr('id'))
	showDialog($dialog)
	$form.on('submit', function(e) {
		// IMPORTANT
		e.stopPropagation()
		e.preventDefault()
		console.log('click on ' + $(this).attr('id'))
		submitJob($(this))
		$dialog.hide()
	})

}

function showMessageDialog(text) {
	var $body = $('#message-dialog')
	$body.find('#message-text').first().text(text)
	var $dialog = prepareDialog($body)
	showDialog($dialog)
}

showErrorDialog = function(text) {
	var $form = $('#error-dialog')
	$form.find('#error-message').first().text(text)
	console.log($form.html())
	var $dialog = prepareDialog($form)
	showDialog($dialog)
}

closeDialog = function() {
	$('.w3-modal').hide()
	
}

/**
 * create a couple of HTMK element to accept an input
 * @param option 
 *    label: text on label
 *    value: 
 *    
 * @returns input element
 */
function createTextInput(option) {
	if (option.readonly) {
		var $label = $('<label>')
		$label.text(option.value);
		return $label
	} else {
		var $input = $('<input>')
		$input.attr('type', 'text');
		$input.attr('placeholder', option.value);
		return $input
	}
}



/**
 * create a check box true|false
 * @param option
 * @returns
 */
function createBooleanInput(option) {
	var $input = $('<input>')
	$input.addClass('w3-check')
	$input.attr('type', 'checkbox');
	if (option.checked)
	  $input.prop('checked', 'checked');
	
	return $input
}


function createSelectInput(option) {
	var $select = $('<div>')
	for (var i = 0; i < option.choices.length; i++) {
		var choice = option.choices[i]
	    var $input = $('<input>')
	    $input.addClass('w3-check')
		$input.attr('type', 'checkbox');
		if (choice.checked)
			$input.prop('checked', 'checked');
		
		var $label = $('<label>')
		$label.text(choice.label)
		
		$select.append($input, $label, '<br>')
	}
	return $select
}


/**
 * create submit and cancel buttons in a form
 * 
 * @param $dialog dialog to be closed once cancel is pressed
 * @param $form form of which submit is called
 * @param submitText text of 'submit' button
 * @param fn function to be called on submit
 * @returns button bar
 */
function createFormControl($dialog, $form, submitText, fn, style) {
	  var $buttonBar = $('<div>')
	  $buttonBar.addClass('w3-bar w3-center w3-panel')
	  var $submitJob = $('<button>')
	  $submitJob.addClass('w3-btn')
	  if (style) {
		  //$submitJob.addClass(style.color.submit)
	  }
	  $submitJob.attr('type', 'submit')
	  $submitJob.text(submitText)
	  var $cancel = $('<button>')
	  $cancel.addClass('w3-btn')
	  $cancel.text('cancel')
	  if (style) {
		 // $cancel.addClass(style.color.cancel)
	  }
	  $cancel.on('click', function(e) {
		  e.preventDefault()
		  e.stopPropagation()
		  $dialog.hide()
	  })
	  
	  
	  $form.on('submit', function(e) {
		  e.preventDefault()
		  e.stopPropagation()
		  if (fn == undefined) {
			  $dialog.hide()
		  } else {
			  fn()
		  }
	  })
  
	  $buttonBar.append($submitJob, $cancel)
	
	  $form.append($buttonBar)
	  
	  return $buttonBar
}

/**
 * single option to be selected from multiple choices
 * @param option
 * @returns
 */
function createChoiceInput(option) {
	var $radioGroup = $('<div>')
	if (option.group == undefined) 
		throw 'no group defined for option [' + option.key + ']'
	for (var i = 0; i < option.choices.length; i++) {
		var choice = option.choices[i]
	    var $input = $('<input>')
	    $input.addClass('w3-radio')
		$input.attr('type', 'radio');
		$input.attr('name',  option.group);
		$input.attr('value', choice.value);
		if (choice.checked)
			$input.prop('checked', 'checked');
		if (choice.disabled)
			$input.prop('disabled', true);
		var $label = $('<label>')
		$label.text(choice.label)
		
		$radioGroup.append($input, $label, '<br>')
	}
	return $radioGroup
}

/**
 * create a row for input option
 * 
 * return the input element
 */
function createRow($section, option) {
	var $row = $('<div>')
	$row.addClass('w3-row w3-padding')
	
	var $label = $('<label>')
	$label.text(option.label)
	if (option.required) {
		$label.css('font-weight', 'bold')
	}
	if (option.tooltip) {
		$label.addClass('w3-tooltip')
		var $tooltip = $('<span>')
		$tooltip.addClass('w3-text')
		$tooltip.text(option.tooltip)
		$label.append($tooltip)
	}
	var $input
	if (option.kind == 'text') {
		$input = createTextInput(option)
	} else if (option.kind == 'boolean') {
		$input = createBooleanInput(option)
		
	} else if (option.kind == 'choice') {
		$input = createChoiceInput(option)
	} else if (option.kind == 'select') {
		$input = createSelectInput(option)
	}
	if (option.required) {
		$input.prop('required', true)
	}
	if (option.kind == 'boolean') {
		$label.addClass('w3-col m7 l7')
		$input.addClass('w3-col m3 l3')
		$row.append($input, $label)
	} else {
		$label.addClass('w3-col m3 l3')
		$input.addClass('w3-col m7 l7')
		$row.append($label, $input)
	}
	$section.append($row)
	return $input
}

function newSection($container, text, collapsible) {
	var $section = $('<div>')
	if (text) {
		var $title = $('<button>')
		$title.addClass('w3-button w3-block w3-teal')
		$title.text(text)
		$section.append($title)
		if (collapsible) {
			$title.on('click', function(e) {
				e.preventDefault()
				e.stopPropagation()
				$section.children().toggle()
				$title.show()
			})
			
		}
	}
	$container.append($section)

	return $section
}




