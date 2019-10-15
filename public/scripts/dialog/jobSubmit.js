var DEFAULT_STYLE = {}
var JOB_CATEGORIES={
	"smoke":   {"server":"nutest", "display":"Smoke Test"},
	"checkin": {"server":"nutest", "display":"Check-In Test"},
	"perf":    {"server":"perf",   "display":"Database Performance"},
	"bench":   {"server":"benchmark", "display":"Benchmark"}

}
/**
 * dialog to submit a job
 */
class JobSubmitDialog {
	/**
	 * style
	 */
	constructor(style, category) {
		this.style =  style || DEFAULT_STYLE
		this.category = category
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
		$subtitle.text('specify options')
		$header.append($title, $subtitle)
		$headerSection.append($header)
		
		var $commonSection = newSection($form)
		
		createRow($commonSection, 
			{kind: 'text', 
			label: 'Job Category', 
			key: 'job_category',
			value: JOB_CATEGORIES[category]['display'], 
			readonly: true}
		)
		this.$name = createRow($commonSection, 
				{label: 'Job name', 
			     key: 'job_name',
			     kind: 'text', 
			     value: 'my first job',
			     required:true}
		)
		this.$submitter = createRow($commonSection, 
			{kind: 'text', 
			 label: 'Submitter',
			 key: 'job_submitter', 
			 value:'engineer@era.com',
			required: true}
		)
	
		var $optionSection = newSection($form, 'test options', true) 
		this.skip_delete = createRow($optionSection, 
			{kind: 'boolean', 
			 checked:true,
			 key:  'skip_delete', 
			 label: 'do not delete created VMs after test run'
		    }
		)
		this.use_era_server = createRow($optionSection, 
			{kind: 'text', 
			 tooltip: 'leave empty if not using ERA server',
			 key:  'use_era_server', 
			 value: 'leave empty if not using existing ERA server for test',
			 label:'ERA server'
			}
		)
		this.deploy_type = createRow($optionSection, 
				{kind: 'choice', 
			     group: 'deploy_type',
				 key:  'deploy_type', 
				 tooltip: 'how would you deploy ERA server?',
				 label:'Deploy Type',
				 choices:[
					 {label:'do not deploy', value:'NONE'},
					 {label:'upgrade', value:'UPGRADE'},
					 {label:'install', value:'INSTALL', checked:true}
				 ]
				}
		)
		this.build_type = createRow($optionSection, 
				{kind: 'choice', 
		         group: 'build_type',
				 key:  'build_type', 
				 label:'Build Type',
				 tooltip: 'ignored If deploy type is none',
				 choices:[
					 {label:'do not build UI', value:'skipui', checked:true},
					 {label:'all', value:'all'}
				 ]
				}
		)
		
		var $buttonBar = createFormControl(this.$dialog, $form, 
				'Submit Job', 
				// IMPORTANT: a function in this dialog's scope
				this.submitJob.bind(this), style)
		
	}
	
	
	open() {
		$('body').append(this.$dialog)
		this.$dialog.show()
	}
	close() {
		this.$dialog.remove()
	}

	
	/**
	 * submits job by collecting all user inputs
	 */
	submitJob() {
		// send all values as string e.g. "true"
		var options = {
				'skip_delete'   : this.skip_delete.is(":checked").toString(),
				'use_era_server': this.use_era_server.val(),
				'deploy_type'   : 
					$("input:radio[name='deploy_type']:checked").val(),
				'buiild_type'   : 
					$("input:radio[name='build_type']:checked").val()
		}
		var payload = {
				'name'     : this.$name.val(),
				'category' : JOB_CATEGORIES[this.category]['server'],
				'submitter': this.$submitter.val(),
				'options'  : options
		}
		var url = '/resource/job/'
		console.log('request POST ' + url)
		console.log('payload ')
		console.log(payload)
		var _this = this
		$.ajax({
			url: '/resource/job/',
			method: 'POST',
			data: JSON.stringify(payload),
			contentType: 'text/plain'
		}).done(function(data){
			console.log('server response')
			console.log(data)
			var msg = 'submitted job [' + data['name'] + ']'
			  + ' in [' + data['queue'] + '] queue'
			new MessageDialog().setMessage(msg).open()
		}).fail(function(data) {
			console.log('*ERROR')
			console.log(data)
			new MessageDialog().setError(data).open()
		}).always(function(data) {
			_this.close()
		})
	}
	
}