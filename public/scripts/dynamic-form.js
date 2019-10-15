/**
 * given script option create a form
 */

function createForm(options) {
	var $div = $('<div>')
	$div.addClass('w3-container')
	for (var i = 0; i < options.length; i++) {
		$div.append(createInput(options[i]))
	}
	return $div
}
/**
 * an option renders as row of two columns
 * 
 * @param option
 * @returns
 */
function createInput(option) {
	var $row = $('<div>')
    $row.addClass('w3-row')
	var $label = $('<label>')
	$label.text(option.key)
	

	var $input
	if (option.kind == 'text') {
		$input = createTextInput(option)
	} else if (option.kind == 'choice') {
		$input = createRadioInput(option)
	} else if (option.kind == 'select') {
		$input = createSelectInput(option)
	}
	
    
    $label.addClass('w3-col m3 l3')
    $input.addClass('w3-col m8 l8')
	$row.append($label)
    $row.append($input)
	
	return $row
}




