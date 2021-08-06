function Message(params) {
	
	var TYPES = {
		info : 		{ label : "info", 		icon : "info-circle" },
		question : 	{ label : "question", 	icon : "question-circle" },
		warning : 	{ label : "warning", 	icon : "warning" },
		error : 	{ label : "error",	 	icon : "times-circle" }
	};
	
	/**
	 * Model for the Popup
	 * @private
	 */
	var _uid = params.id;
	
	/**
	 * Model for the Popup
	 * @private
	 */
	var _type = params.type;
	
	/**
	 * Model for the Popup
	 * @private
	 */
	var _title = params.title;
	
	/**
	 * Text for the Popup
	 * @private
	 */
	var _text = "";
	
	/**
	 * Validation callback called when "Yes" or "Ok" button is pressed
	 * @private
	 */
	var _onValidate;
	
	/**
	 * Build the Header of the Popup
	 * @private
	 */
	var _buildHeader = function() {
		var header = document.createElement("div");
		header.classList.add("dauth-message-header");
		
		var icon = document.createElement("i");
		icon.classList.add("fa");
		
		for (var i in TYPES) {
			if (TYPES[i].label == _type) {
				icon.classList.add("fa-" + TYPES[i].icon);
				break;
			}
		}
		
		var title = document.createElement("span");
		title.appendChild(document.createTextNode(_title));
		
		header.appendChild(icon);
		header.appendChild(title);
		
		return header;
	};
	
	/**
	 * Build the Body of the Popup
	 * @private
	 */
	var _buildBody = function() {
		var body = document.createElement("div");
		body.classList.add("dauth-message-body");
		body.textContent = _text;
		
		return body;
	};
	
	/**
	 * Build the Buttons of the Popup
	 * @private
	 */
	var _buildButtons = function() {
		var buttons = document.createElement("div");
		buttons.classList.add("dauth-message-buttons");
		
		if (TYPES.info.label == _type) {
			var ok = new Button({ text : "Ok", uuid : "software-popup-ok-btn" });
			ok.setAction(function() {
				_close();
			});
			buttons.appendChild(ok.get());
		} else if (TYPES.question.label == _type) {
			var ok = new Button({ text : "Yes", uuid : "software-popup-ok-btn" });
			ok.setAction(function() {
				_onValidate();
				_close();
			});
			buttons.appendChild(ok.get());
			
			var cancel = new Button({ text : "No", uuid : "software-popup-cancel-btn" });
			cancel.setAction(function() {
				_close();
			});
			buttons.appendChild(cancel.get());
		} else if (TYPES.warning.label == _type) {
			var ok = new Button({ text : "Ok", uuid : "software-popup-ok-btn" });
			ok.setAction(function() {
				_onValidate();
				_close();
			});
			buttons.appendChild(ok.get());
			
			var cancel = new Button({ text : "Cancel", uuid : "software-popup-cancel-btn" });
			cancel.setAction(function() {
				_close();
			});
			buttons.appendChild(cancel.get());
		} else if (TYPES.error.label == _type) {
			var ok = new Button({ text : "Ok", uuid : "software-popup-ok-btn" });
			ok.setAction(function() {
				_close();
			});
			buttons.appendChild(ok.get());
		}
		
		return buttons;
	};
	
	/**
	 * Set the text for the Popup
	 * @private
	 */
	var _setText = function(text) {
		_text = text;
	};
	
	/**
	 * Set the callback for the Popup
	 * @private
	 */
	var _setOnValidate = function(onValidate) {
		_onValidate = onValidate;
	};
	
	/**
	 * Build and show the Popup
	 * @private
	 */
	var _show = function() {
		var overlay = document.createElement("div");
		overlay.setAttribute("id", "dauth-message-overlay");
		
		var popup = document.createElement("div");
		popup.classList.add("dauth-message");
		popup.setAttribute("id", _uid);
		popup.appendChild(_buildHeader());
		popup.appendChild(_buildBody());
		popup.appendChild(_buildButtons());
		
		document.body.appendChild(overlay);
		document.body.appendChild(popup);
	};
	
	/**
	 * Destroy and close Popup
	 * @private
	 */
	var _close = function() {
		var overlay = document.getElementById("dauth-message-overlay");
		overlay.parentNode.removeChild(overlay);
		
		var popup = document.getElementById(_uid);
		popup.parentNode.removeChild(popup);
	};
	
	/**
	 * Set the text for the Popup
	 * @public
	 */
	this.setText = function(text) {
		_setText(text);
	};
	
	/**
	 * Set the callback for the Popup
	 * @public
	 */
	this.onValidate = function(onValidate) {
		_setOnValidate(onValidate);
	};
	
	/**
	 * Build and show the Popup
	 * @public
	 */
	this.show = function() {
		_show();
	};
	
	/**
	 * Destroy and close Popup
	 * @public
	 */
	this.close = function() {
		_close();
	};
	
}