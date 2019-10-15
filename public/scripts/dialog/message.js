class MessageDialog {
	constructor() {
		this.$dialog = $('<div>')
		this.$dialog.addClass('w3-modal')
		this.$form = $('<form>')
	  	this.$form.addClass('w3-contanier w3-card w3-modal-content')
	    this.$dialog.append(this.$form)
	}
	
	setMessage(msg) {
		var $p = $('<p>')
		$p.text(msg)
		this.$form.append($p)
		
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