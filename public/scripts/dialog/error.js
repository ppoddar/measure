class ErrorDialog {
	constructor() {
		this.$dialog = $('<div>')
		this.$dialog.addClass('w3-modal')
		this.$form = $('<form>')
	  	this.$form.addClass('w3-contanier w3-card w3-modal-content')
	    this.$dialog.append(this.$form)
	    
	}
	
	setError(response) {
		var $p = $('<p>')
		$p.text(msg)
		this.$form.append($p)
	    var $stack = $('<div>')
	    var $control = $('<button>')
	    $control.addClass('w3-button')
	    $control.on('click', function(e) {
	    	$stack.toggle()
	    	var visible = $stack.is(':visible') 
	    	$(this).text(visible ? 'Hide details' : 'Show details')
	    })
	    var stackTrace = JSON.parse(response)['stackTrace']
		for (var i = 0; i < stackTrace.length; i++) {
			var $line = $('<p>')
			var fields = ['methodName', 'fileName', 'lineNumber', 'className']
			$line.text(stack['className'] + '#' + stack['methodName'] + ':' 
					+ stack['lineNumber'])
			$stack.append($line)
		}
	    
	    this.$form.append($control, $stack)
		createFormControl(this.$dialog, this.$form, 'OK')
		return this
	}

	open() {
		$('body').append(this.$dialog)
		this.$dialog.show()
	}
	close() {
		this.$dialog.remove()
	}

}