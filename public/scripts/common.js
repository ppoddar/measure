/**
 * common functions
 */

/**
 * find a element by id in whole page
 * raises exception if not found
 */
function jq(id) {
	if (!id.startsWith('#')) {
		id = '#' + id;
	}
	var $el = $(id)
	if ($el.length == 0)
		throw 'no element with id [' + id + ']'
	return $el
	
}
/**
 * find a element by css in parent element
 * raises exception if not found
 */
function find($parent, css) {
	var $el = $parent.find(css).first()
	if ($el.length == 0)
		throw 'no child element with selector [' + css + ']'
		+ ' found in parent ' + $parent.attr('id')
	return $el
}


