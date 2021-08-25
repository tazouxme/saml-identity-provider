function Checkbox(params) {
	
	/**
	 * Checkbox ID
	 * @private
	 */
	var _uid = params.id;
	
	/**
	 * Translatable labels
	 * @private
	 */
	var _translatable = !!params.translatable;
	
	/**
	 * Checkbox title
	 * @private
	 */
	var _title = params.title;
	if (_translatable) {
		_title = Utils.translate(_title);
	}
	
	/**
	 * Box HTML element for the Checkbox
	 * @private
	 */
	var _box = document.createElement("input");
	_box.setAttribute("type", "checkbox");
	
	/**
	 * Main HTML container for the Checkbox
	 * @private
	 */
	var _container = document.createElement("label");
	_container.classList.add("dauth-checkbox");
	_container.setAttribute("id", _uid);
	_container.addEventListener("mouseup", function() {
		if (_onCheck != null && !_isChecked()) {
			_onCheck();
		}
	});
	
	/**
	 * Checkbox builder
	 * @private
	 */
	var _buildCheckbox = function() {
		var span = document.createElement("span");
		span.classList.add("checkmark");
		
		_container.appendChild(document.createTextNode(_title));
		_container.appendChild(_box);
		_container.appendChild(span);
	}
	
	/**
	 * Checkbox is enabled
	 * @private
	 */
	var _enabled = true;
	
	/**
	 * OnCheck callback
	 * @private
	 */
	var _onCheck;
	
	/**
	 * Returns if the Checkbox is checked
	 * @private
	 */
	var _isChecked = function() {
		return _box.checked;
	};
	
	/**
	 * Set the Checkbox as checked
	 * @private
	 */
	var _setChecked = function(checked) {
		_box.checked = checked;
		
		if (checked) {
			_box.setAttribute("checked", "checked");
			_container.dispatchEvent(new Event("mouseup"));
		} else {
			_box.removeAttribute("checked");
		}
	};
	
	/**
	 * Returns if the Checkbox is enabled
	 * @private
	 */
	var _isEnabled = function() {
		return _enabled;
	};
	
	/**
	 * Set the Checkbox as enabled
	 * @private
	 */
	var _setEnabled = function(enabled) {
		_enabled = enabled;
		
		if (!enabled) {
			_box.setAttribute("disabled", "disabled");
		} else {
			_box.removeAttribute("disabled");
		}
	};
	
	/**
	 * Clear the Checkbox 
	 * @private
	 */
	var _clear = function() {
		_setChecked(false);
	};
	
	/**
	 * Get the component Id
	 * @public
	 */
	this.getId = function() {
		return _uid;
	};
	
	/**
	 * Returns if the Checkbox is checked
	 * @public
	 */
	this.getValue = function() {
		return _isChecked();
	};
	
	/**
	 * Set the Checkbox as checked
	 * @public
	 */
	this.setValue = function(checked) {
		_setChecked(checked);
	};
	
	/**
	 * Returns if the Checkbox is enabled
	 * @public
	 */
	this.isEnabled = function() {
		return _isEnabled();
	};
	
	/**
	 * Set the Checkbox as enabled
	 * @public
	 */
	this.setEnabled = function(enabled) {
		_setEnabled(enabled);
	};
	
	/**
	 * Clear the Checkbox 
	 * @public
	 */
	this.clear = function() {
		_clear();
	};
	
	/**
	 * Callback for the Checkbox 
	 * @public
	 */
	this.onCheck = function(onCheck) {
		_onCheck = onCheck;
	}
	
	/**
	 * Converts the parameters to an <pre>HTML Element</pre>
	 * @public
	 */
	this.set = function(element) {
		_buildCheckbox();
		element.appendChild(_container);
	};
	
	/**
	 * Get the <pre>HTML Element</pre>
	 * @public
	 */
	this.get = function() {
		return _container;
	};
	
}