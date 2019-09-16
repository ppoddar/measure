/**
 * database related functions
 * 
 */

/* TODO: should come from sever */
var DATABASE_KINDS
var CATALOG

DATABASE_KINDS = [
	{'name':'Postgres', 'host':'127.0.0.1', 'port':5432, 'user':''},
	{'name':'MySQL',    'host':'localhost', 'port':3306, 'user':''}
	]
// catalog of databases indexed by name
CATALOG = {}

/**
 * a database 
 */
class Database {
	constructor(data) {
		if (data) {
			this.name = data['name']
			this.data = data
		}
	}
	
	/**
	 * gets database catalog known to server
	 * by an Ajax call.
	 * The database catalog is an array of database
	 * where each database would have their metrics
	 * The databases would be displayed in a tree view.
	 */
	 getCatalog($tree) {
		var _this = this;
		$.ajax({
			url: '/repo/databases'
		}).done(function(data){
			console.log('fetched ' + data.length + ' databases')
			console.log(data)
			for (var i = 0; i < data.length; i++) {
				var db = new Database(data[i])
				CATALOG[db.name] = db
			}
			if (data.length == 0) {
				console.log('WARN: empty databse catalog from server')
			} else {
				_this.showAll(CATALOG, $tree)
			}
		})
	}
	
	/**
	 * draws a database as an <li> items
	 * the item has subtree for each metrics
	 * 
	 */
	 createItem($tree) {
		console.log('drawDatabase ' + this.name)
		var metrices = this.data.metrics.elements

		var $li = $('<li>')
		$li.addClass('w3-bar')
				
		var $barItem = $('<div>')
		$barItem.addClass('w3-bar-item')
		
//		var $img = $('<img>')
//		$img.addClass('w3-margin')
//		$img.attr('src', 'images/png/database.png')
		var $name = $('<span>')
		$name.addClass('w3-large')
		$name.text(this.name)
		$name.css('font-weight', 'bold')
		
		var $kind = $('<p>')
		$kind.text(this.data['kind'])
		
		var $host = $('<p>')
		$host.text(this.data['host'] + ':' + this.data['port'])
		
		var $metricsLabel = $('<li>')
		$metricsLabel.addClass('w3-text-blue')
		$metricsLabel.text('Metrics (' + metrices.length + ')')
		$metricsLabel.css('font-weight', 'bold')
		var $metricsList = $('<ul>')
		$metricsList.addClass('metrics-list')
		for (var i = 0; i < metrices.length; i++) {
			// TODO: decorate dimension 
			var m = metrices[i]
			var $metrics = new Metrics().drawMetrics(m)
			$metricsList.append($metrics)
		}
		$metricsList.hide()
		$metricsLabel.append($metricsList)
		
		$barItem.append($name, 
				$kind,
				$host,
				$metricsLabel)
		
		$li.append($barItem)
		
		$li.on('click', function(e) {
			e.stopPropagation()
			$(this).children('ul').toggle()
		})
		$metricsLabel.on('click', function(e) {
			e.stopPropagation()
			$(this).children('ul').toggle()
		})
		
		$tree.append($li)
	}
	 
	/**
	 * draws a dictionary of databases
	 * adds it to given tree
	 */ 
	showAll(dbs, $tree) {
		console.log('in databse.show()')
		console.log('drawing  databases')
		for (var name in dbs) {
			var db = dbs[name]
			db.createItem($tree)
		}
		return $tree
	}
	
	getAllDatabases() {
		var result = []
		console.log('collecting '  + Object.keys(CATALOG).length + ' databases')
		for (var name in CATALOG) {
			var db = CATALOG[name]
			result.push(db)
		}
		return result
	}

	/**
	 * get all distinct metrics names
	 */
	getAllMetrics() {
		var result = []
		console.log('collecting all metrices from '  
				+ Object.keys(CATALOG).length + ' databases')
		for (var name in CATALOG) {
			var db = CATALOG[name]
			var metrics = db.data.metrics.elements
			for (var i = 0; i < metrics.length; i++) {
				var m = metrics[i]
				if (!result.includes(m)) {
					result.push(m)
				}
			}
		}
		
		return result
	}

	/**
	 * registers a database with server.
	 * @param e submit event 
	 * @param $form carries the input
	 */
     static register(e, $form) {
    	// IMPORTANT
		e.preventDefault()
		e.stopPropagation()
		// read input
		var kind = $form.find('#database-kind').first().val()
		var name = $form.find('#database-name').first().val()
		var host = $form.find('#database-host').first().val()
		var port = $form.find('#database-port').first().val()
		var user = $form.find('#database-user').first().val()
		var pwd  = $form.find('#database-pwd').first().val()
		var payload = {
				kind: kind,
				name: name,
				host: host,
				port: port,
				user: user,
				port: port
			}
		var url = 'database'
		console.log('register database with payload')
		console.log(payload)
		// send AJAX request
		$.ajax({
			url: url,
			type: 'POST',
			contentType:"application/json; charset=utf-8",
			data: JSON.stringify(payload)
		}).done(function(data){
			console.log('received response ' + url)
			console.log(data)
			
			var db = new Database(data)
			CATALOG[db.name] = db
			db.createItem($('#database-tree'))
		}).fail(function(err) {
			console.log('***ERROR')
			var errObj = JSON.parse(err.responseText)
			showErrorDialog(errObj)
		})
		$('#new-database-dialog').hide()

    }
    
    /**
     * registers a database
     */
     createNew(e) {
    	e.preventDefault()
    	e.stopPropagation()
    	var $dialog = $('#new-database-dialog')
    	var $form = $dialog.find('form').first()
	    var $select = $form
		          .find('#database-kind').first()
        populateComboBox($select, 
        		DATABASE_KINDS,
    	    {'host': $('#database-host'),
    	     'port': $('#database-port'),
    	     'user': $('#database-user')});
    	
    	showDialog($dialog)
    	$form.submit(function(e) {
    		Database.register(e, $form)
    		return
    	})
    }
}






