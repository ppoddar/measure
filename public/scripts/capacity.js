var QUANTITY_KINDS = [ 'MEMORY', 'STORAGE',  'COMPUTE']
/**
 * a capacity is dictionary of quantities.
 * the key is MEMORY, STORAGE or COMPUTE
 * the value is a object with two fields:
 * 'value' and 'unit'.
 * 
 */
class Capacity {
	/**
	 * constructed from quantities that are values with unit
	 * the kind of quantities are enumerated in  QUANTITY_KINDS
	 * for each kind the value is a number and (optional) unit
	 * 
	 * for example: 
	 *    {'memory': '1026 MB'}
	 *    
	 *  IMPORTANT: the quantity values must be numbers generate
	 *  by parseFloat -- otherwise google chart which is later
	 *  used to display this data, does not render it
	 *  properly
	 */
	constructor(data) {
		var _this = this
		QUANTITY_KINDS.forEach(function(k) {
			if (!(k in data)) {
				throw 'key [' + k + '] does not exist in given input. '
				+ ' available keys are ' + Object.keys(data)
			}
			var tokens = data[k].split(' ')
			var value = parseFloat(tokens[0])
			if (k == 'COMPUTE') {
				// COMPUTE is unit less
				_this[k] = {'value' : value}
			} else if (k == 'MEMORY')  {
				_this[k] = {'value': value, 'unit': tokens[1]}
			} else if (k == 'STORAGE')  {
				_this[k] = {'value': value, 'unit': tokens[1]}
			}
		})
	}
	
	
}