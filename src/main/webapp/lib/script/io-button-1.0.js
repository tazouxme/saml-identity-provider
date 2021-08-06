var Button = function(params) {
	
	var _uid = params.id;
	var _text = params.text;
	var _icon = params.icon;
	var _translatable = !!params.translatable;
	
	if (_translatable) {
		_text = Utils.translate(_text);
	}
	
	/**
	 * Data to be used on button click
	 * @private
	 */
	var _data;
	
	/**
	 * Callback containing a function()
	 * @private
	 */
	var _action = params.action;
	
	/**
	 * Is the field enabled
	 * @private
	 */
	var _enabled = true;
	
	/**
	 * Button
	 * @private
	 */
	var _button = document.createElement("button");
	_button.setAttribute("id", _uid);
	_button.setAttribute("class", "dauth-button");
	
	var _span = document.createElement("span");
	if (_icon != null) {
		_span.setAttribute("class", "dauth-button-img");
		
		var img = document.createElement("img");
		img.setAttribute("src", _icon);
		_button.appendChild(img);
	} else {
		_span.setAttribute("class", "dauth-button-blank");
	}
	
	_span.appendChild(document.createTextNode(_text));
	_button.appendChild(_span);
	
	/**
	 * Get data to be used by the button
	 * @private
	 */
	var _getData = function() {
		return _data;
	};
	
	/**
	 * Set data to be used by the button
	 * @private
	 * @param data
	 */
	var _setData = function(data) {
		_data = data;
	};
	
	/**
	 * Get the component Id
	 * @public
	 */
	this.getId = function() {
		return _uid;
	};
	
	/**
	 * Set the button enabled or not
	 * @public
	 * @param enabled
	 */
	this.setEnabled = function(enabled) {
		_enabled = enabled;
		
		if (_enabled) {
			_button.removeAttribute("disabled");
		} else {
			_button.setAttribute("disabled", "disabled");
		}
	};
	
	/**
	 * Set the icon
	 * @public
	 * @param icon
	 */
	this.setIcon = function(icon) {
		_icon = icon;
	}
	
	/**
	 * Sets the actionListener on the button
	 * @public
	 * @param action
	 */
	this.setAction = function(action) {
		_action = action;
		_button.addEventListener("click", function(e) {
			_action(e);
		});
	};
	
	/**
	 * Get data to be used by the button
	 * @public
	 */
	this.getData = function() {
		return _getData();
	};
	
	/**
	 * Set data to be used by the button
	 * @public
	 * @param data
	 */
	this.setData = function(data) {
		_setData(data);
	};
	
	/**
	 * Get the <pre>HTML Element</pre>
	 * @public
	 * @return <pre>Button HTML Element</pre>
	 */
	this.get = function() {
		return _button;
	};
	
	/**
	 * Converts the parameters to an <pre>HTML Element</pre>
	 * @public
	 * @return <pre>Button HTML Element</pre>
	 */
	this.set = function(element) {
		element.appendChild(_button);
	};
	
}