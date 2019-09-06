/**
 * shows a dialog for error.
 * the dialog template is defined in HTML
 * @param response error message text
 */
showErrorDialog = function (errObj) {
	var $dialog = $('#error-dialog')
	console.log(errObj)
	var txt = 'no message available'
	if (errObj.message) txt = errObj.message
	$('#error-message').text(txt)
	showDialog($dialog)
}
/**
 * shows a message in a dialog box.
 * @param response text with HTML
 * @param fade if true, fades out
 */
showMessageDialog = function (response, fadeOut) {
	var $dialog = $('#message-dialog')
	var $message = $('#message-text')
	console.log('found message block:' + $message.attr('id'))
	console.log('setting message:' + response)
	$message.html(response)
	showDialog($dialog)
	
	if (fadeOut) {
		$dialog.fadeOut(5000)
	}
}

/**
 * shows a dialog
 * @param $el an jQuery element with w3-modal
 * @returns
 */
function showDialog($el) {
	console.log('showDialog ' + $el.attr('id'))
	$el.draggable()
	$el.resizable()
	$el.show()
}

/**
 * Populates combo 
 * a jQuery <select> element
 * to be populated with database kinds
 * 
 * @param $select a select to be populated
 * @param an array of items 
 * @param input fields whose value are to be 
 * filled with given properties of selected
 * item
 * The visual representation of the item in
 * combo box drop down list is its 'name'
 */
function populateComboBox($select, items,
		valuesToBeUpdated) {
	console.log('populate select ' + $select.attr('id')
			+ ' with ' + items.length + ' items');
	for (var i = 0; i < items.length; i++) {
		var item = items[i]
		var $option = $('<option>')
		$option.text(item['name'])
		$option.data('data', item)
		$select.append($option)
		if (i == 0) $option.prop('selected', true)
	}
	$select.on('change', function(e) {
		var $selected = $(this).find('option:selected')
		var $item = $selected.data('data')
		if (valuesToBeUpdated) {
			for (var p in valuesToBeUpdated) {
				console.log('update ' + p)
				valuesToBeUpdated[p].val($item[p])
			}
		}
	})
	$select.change()
}

