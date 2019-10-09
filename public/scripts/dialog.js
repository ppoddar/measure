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