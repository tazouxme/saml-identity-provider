function Select(params) {
	
	var _uid = params.id;
	var _defaultValue = params.defaultValue != null ? params.defaultValue : "dauth.select.default";
	var _translatable = !!params.translatable;
	
	if (_translatable) {
		_defaultValue = Utils.translate(_defaultValue);
	}
	
	var _open = false;
	var _options = [];
	
	var _change;
	var _converter;
	var _renderer;
	
	/**
	 * Main container for the Select
	 * @private
	 */
	var _container = document.createElement("div");
	_container.classList.add("dauth-select");
	_container.setAttribute("id", _uid);

	var _list = document.createElement("ul");
	
	/**
	 * Main field for the Select
	 * @private
	 */
	var _field = new Field({ id: 'dauth-select-field', type: 'text', icon: 'bars', title: '' });
	_field.setEnabled(false);
	_field.set(_container);
	
	var _build = function() {
		var item = document.createElement("li");
		item.dataset.option = _defaultValue;
		item.appendChild(document.createTextNode(_defaultValue))
		_list.appendChild(item);
		_field.setValue(_defaultValue);
		
		for (var i = 0; i < _options.length; i++) {
			_buildOption(_options[i]);
		}
		
		_container.appendChild(_list);
		_container.addEventListener("click", function() {
			if (_open) {
				_list.classList.remove('displayed');
				_open = false;
			} else {
				_list.classList.add('displayed');
				_open = true;
			}
		}, false);
	}
	
	var _buildOption = function(option) {
		if (_translatable && (option.translate == null || option.translate == true)) {
			option.value = Utils.translate(option.value);
		}
		
		var item = document.createElement("li");
		item.classList.add("dauth-select-option");
		item.dataset.option = option.value;
		item.dataset.id = option.id;
		item.appendChild(document.createTextNode(option.value))
		_list.appendChild(item);
		
		item.addEventListener("click", function(e) {
			e.stopPropagation();
			e.preventDefault();
			
			_field.setValue(this.dataset.option);
			
			_list.classList.remove('displayed');
			_open = false;
			
			if (_change != null) {
				_change(_findOption(this.dataset.id));
			}
		}, false);
	};
	
	/**
	 * Find an Option object by its Id
	 * @private
	 * @param value
	 */
	var _findOption = function(id) {
		if (id == null) {
			return null;
		}
		
		for (var i = 0; i < _options.length; i++) {
			if (_options[i].id == id) {
				return _options[i];
			}
		}
		
		return null;
	};
	
	/**
	 * Get the value of the Select
	 * @private
	 */
	var _getValue = function() {
		if (_converter != null) {
			return _converter(_getSelectedObject());
		}
		
		if (_field.getValue() == _defaultValue) {
			return "";
		} else {
			return _field.getValue();
		}
	};
	
	/**
	 * Set the value of the Select
	 * @private
	 * @param value
	 */
	var _setValue = function(value) {
		if (_renderer != null) {
			_field.setValue(_renderer(value));
		} else {
			_field.setValue(value);
		}
		
		if (_change != null) {
			_change(_getSelectedObject());
		}
	};
	
	/**
	 * Get the selected object of the Select
	 * @private
	 */
	var _getObject = function(val) {
		for (var i = 0; i < _options.length; i++) {
			if (_options[i].value == val) {
				return _options[i];
			}
		}
		
		return null;
	};
	
	/**
	 * Get the selected object of the Select
	 * @private
	 */
	var _getSelectedObject = function() {
		return _getObject(_field.getValue());
	};
	
	/**
	 * Set the callback zhen value has changed
	 * @private
	 * @param change
	 */
	var _onChange = function(change) {
		_change = change;
	};
	
	/**
	 * Set the converter for value getter
	 * @private
	 * @param converter
	 */
	var _setConverter = function(converter) {
		_converter = converter;
	};
	
	/**
	 * Set the renderer for value setter
	 * @private
	 * @param renderer
	 */
	var _setRenderer = function(renderer) {
		_renderer = renderer;
	};
	
	/**
	 * Validate the field
	 * @private
	 */
	var _validate = function() {
		if (_field.getValue() == _defaultValue) {
			_container.classList.add("dauth-validation-error");
			return { valid: false, reason: 'Nothing selected' };
		}
		
		_container.classList.remove("dauth-validation-error");
		return { valid: true };
	};
	
	/**
	 * Clear the selection
	 * @private
	 */
	var _clear = function() {
		_field.setValue(_defaultValue);
	};
	
	/**
	 * Clear and Add options to the Select
	 * @private
	 * @param option
	 */
	var _setOptions = function(options) {
		_options = [];
		var items = _list.getElementsByClassName("dauth-select-option");
		while (items.length > 0) {
			_list.removeChild(items[0]);
		}
		
		for (var i = 0; i < options.length; i++) {
			_addOption(options[i]);
		}
	}
	
	/**
	 * Add an option to the Select
	 * @private
	 * @param option
	 */
	var _addOption = function(option) {
		_options.push(option);
		_buildOption(option);
	}
	
	/**
	 * Add options to the Select
	 * @private
	 * @param options
	 */
	var _addOptions = function(options) {
		for (var i = 0; i < options.length; i++) {
			_addOption(options[i]);
		}
	}
	
	/**
	 * Get the component Id
	 * @public
	 */
	this.getId = function() {
		return _uid;
	};
	
	/**
	 * Get the value of the Select
	 * @public
	 */
	this.getValue = function() {
		return _getValue();
	};
	
	/**
	 * Set the value of the Select
	 * @public
	 * @param value
	 */
	this.setValue = function(value) {
		_setValue(value);
	};
	
	/**
	 * Get the selected object of the Select
	 * @public
	 */
	this.getSelectedObject = function() {
		return _getSelectedObject();
	};
	
	/**
	 * Set the callback when value has changed
	 * @public
	 * @param change
	 */
	this.onChange = function(change) {
		_onChange(change);
	};
	
	/**
	 * Set the converter for value getter
	 * @public
	 * @param converter
	 */
	this.setConverter = function(converter) {
		_setConverter(converter);
	};
	
	/**
	 * Set the renderer for value setter
	 * @public
	 * @param renderer
	 */
	this.setRenderer = function(renderer) {
		_setRenderer(renderer);
	};
	
	/**
	 * Validate the field
	 * @public
	 */
	this.validate = function() {
		return _validate();
	};
	
	/**
	 * Clear the selection
	 * @public
	 */
	this.clear = function() {
		_clear();
	};
	
	/**
	 * Clear and Set all Options to the Select
	 * @public
	 * @param option
	 */
	this.setOptions = function(options) {
		_setOptions(options);
	}
	
	/**
	 * Add an option to the Select
	 * @public
	 * @param option
	 */
	this.addOption = function(option) {
		_addOption(option);
	}
	
	/**
	 * Add options to the Select
	 * @public
	 * @param options
	 */
	this.addOptions = function(options) {
		_addOptions(options);
	};
	
	/**
	 * Converts the parameters to an <pre>HTML Element</pre>
	 * @public
	 */
	this.set = function(element) {
		_build();
		element.appendChild(_container);
	};
	
}