var DEFAULT_STYLE = {}
/**
 * select job category
 * open JobSubmitDialog
 */
class JobSelectCategoryDialog {
	/**
	 * @param style 
	 */
	constructor(style) {
		this.style = style || DEFAULT_STYLE
		this.$dialog = $('<div>')
		this.$dialog.addClass('w3-modal')
		
		var $form = $('<form>')
		this.$dialog.append($form)
		$form.addClass('w3-contanier w3-card w3-modal-content')
		
		var $headerSection = newSection($form)
		$headerSection.addClass('w3-container')
		//$headerSection.addClass(style.color.dialog)
		var $header   = $('<header>')
		var $title    = $('<h1>')
		var $subtitle = $('<h3>')
				
		$title.text('Submit a Job')
		$subtitle.text('select job category')
		$header.append($title, $subtitle)
		$headerSection.append($header)
		
		var $mainSection = newSection($form)
		$mainSection.addClass('w3-container')

		$mainSection.append('<p>Select one of following category of jobs')
		createRow($mainSection,  
			{kind:'choice',
			 key:'Job Category', 
			 group: 'job-category',
			 choices:[
				 {label:'Smoke test',    value:'smoke', checked:true},
				 {label:'Check-in test', value:'checkin'},
				 {label:'Database Performance', value:'perf', disabled:true},
				 {label:'Benchmark',      value:'benchmark',   disabled:true}
			]}
		)
		
		var $buttonBar = createFormControl(this.$dialog, $form, 
				'Next', 
				// IMPORTANT: a function in this dialog's scope
				this.next.bind(this), style)
				
	}
	/**
	 * open this dialog by appending it to body
	 */
	open() {
		$('body').append(this.$dialog)
		this.$dialog.show()
	}

	/**
	 * go to next dialog 
	 * by job category
	 */
	next() {
		var $category = $("input:radio[name='job-category']:checked")
		console.log('selected category ' + $category.val())
		this.$dialog.remove()
		var dialog = new JobSubmitDialog(this.style, $category.val())
		dialog.open()
	}
}