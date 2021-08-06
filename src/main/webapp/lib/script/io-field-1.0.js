var Field = function(params) {
	
	var _uid = params.id;
	var _type = params.type;
	var _effectiveType = params.type;
	var _icon = params.icon;
	var _maxLength = params.maxLength;
	var _width = params.width;
	var _name = params.name;
	
	var _pk;
	var _currentValue = "";
	
	var _translatable = !!params.translatable;
	
	var _title = params.title;
	if (_translatable) {
		_title = DAuth.lang.text.getText(_title);
	}
	
	/**
	 * If the Field is enabled
	 * @private
	 */
	var _enabled = params.enabled != null ? params.enabled : true;
	
	/**
	 * If the Field is mandatory
	 * @private
	 */
	var _mandatory = params.mandatory != null ? params.mandatory : false;
	
	/**
	 * Validator aginst the Field's type
	 * @private
	 */
	var _validator;
	
	if (_type == 'date') {
		_effectiveType = 'text';
		_validator = function(value) {
			var result;
			if (value == null || value == "") {
				result = { valid: true };
			} else {
				var regex = /(^(((0[1-9]|1[0-9]|2[0-8])[/](0[1-9]|1[012]))|((29|30|31)[/](0[13578]|1[02]))|((29|30)[/](0[4,6,9]|11)))[/](19|[2-9][0-9])\d\d$)|(^29[/]02[/](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)$)/;
				var valid = regex.test(value);
				
				if (valid) {
					result = { valid: true };
				} else {
					result = { valid: false, reason: 'Wrong date format' };
				}
			}
			
			_field.dispatchEvent(new CustomEvent('validate', { detail: { result: result } }));
		};
	} else if (_type == 'number') {
		_effectiveType = 'text';
		_validator = function(value) {
			var result;
			if (value == null || value == "") {
				result = { valid: true };
			} else {
				var regex = /^[+-]?\d+(\.\d+)?$/;
				var valid = regex.test(value);
				
				if (valid) {
					result = { valid: true };
				} else {
					result = { valid: false, reason: 'This is not a number' };
				}
			}
			
			_field.dispatchEvent(new CustomEvent('validate', { detail: { result: result } }));
		};
	}
	
	/**
	 * Icon placeholder
	 * @private
	 */
	var _iconElement = document.createElement("i");
	_iconElement.classList.add("dauth-field-icon");
	_iconElement.classList.add("fa");
	_iconElement.classList.add("fa-" + _icon);
	
	/**
	 * Field placeholder
	 * @private
	 */
	var _field = document.createElement("input");
	_field.classList.add("dauth-field-input");
	_field.setAttribute("type", _effectiveType);
	_field.setAttribute("maxlength", _maxLength);
	_field.addEventListener("focusin", () => {
		_label.style.fontSize = '10px';
		_label.style.top = '0px';
		_label.style.left = '5px';
		_label.style.paddingLeft = '10px';
		_label.style.paddingRight = '10px';
	}, false);
	_field.addEventListener("focusout", () => {
		if (_field.value == "") {
			_label.style.fontSize = '14px';
			_label.style.top = '22px';
			_label.style.left = '40px';
			_label.style.paddingLeft = '0px';
			_label.style.paddingRight = '0px';
		}
		
		if (_hasValueChanged()) {
			_validate();
		}
	}, false);
	_field.addEventListener("validate", (e) => {
		var status = e.detail.result;

		if (!status.valid) {
			_container.classList.add("dauth-validation-error");
			_error.innerHTML = status.reason;
			_error.style.display = 'block';
		} else {
			_container.classList.remove("dauth-validation-error");
			_error.innerHTML = '';
			_error.style.display = 'none';
		}
	}, false);
	
	/**
	 * Label for the Field
	 * @private
	 */
	var _span = document.createElement("span");
	_span.appendChild(document.createTextNode(_title));
	
	/**
	 * Main HTML label for the Field
	 * @private
	 */
	var _label = document.createElement("div");
	_label.classList.add("dauth-field-label");
	_label.appendChild(_span);
	
	/**
	 * Main HTML container for the Error
	 * @private
	 */
	var _error = document.createElement("div");
	_error.classList.add("dauth-field-error");
	_error.style.width = _width + "px";
	
	/**
	 * Main HTML container for the Field
	 * @private
	 */
	var _container = document.createElement("div");
	_container.classList.add("dauth-field");
	_container.style.width = _width + "px";
	_container.setAttribute("id", _uid);
	_container.setAttribute("name", _name);
	_container.appendChild(_iconElement);
	_container.appendChild(_field);
	_container.appendChild(_label);
	_container.appendChild(_error);
	
	/**
	 * Check if the field is enabled
	 * @private
	 */
	var _isEnabled = function() {
		return _enabled;
	};
	
	/**
	 * Enable or not the field
	 * @private
	 */
	var _setEnabled = function(enabled) {
		_enabled = enabled;
		
		if (enabled) {
			_field.removeAttribute("disabled");
			_iconElement.style.opacity = 1;
		} else {
			_field.setAttribute("disabled", "disabled");
			_iconElement.style.opacity = 0.5;
		}
	};
	
	/**
	 * Returns if the field mandatory or not
	 * @private
	 */
	var _isMandatory = function() {
		return _mandatory;
	};
	
	/**
	 * Set the field mandatory or not
	 * @private
	 */
	var _setMandatory = function(mandatory) {
		_mandatory = mandatory;
		
		if (mandatory) {
			_field.setAttribute("required", "required");
			_span.textContent = _span.textContent + " *";
		} else {
			_field.removeAttribute("required");
		}
	};
	
	/**
	 * Get the value from the field
	 * @private
	 */
	var _getRawValue = function() {
		return _field.value;
	};
	
	/**
	 * Get the pk from the field
	 * @private
	 */
	var _getPk = function() {
		return _pk;
	};
	
	/**
	 * Set the pk to the field
	 * @private
	 */
	var _setPk = function(pk) {
		_pk = pk;
	};
	
	/**
	 * Get the value from the field
	 * @private
	 */
	var _getValue = function() {
		var value = _getRawValue();
		if (_type == 'date') {
			if (value == null || value == "") {
				return -1;
			}
			
			var dateParts = value.split("/");
			var date = dateParts[1] + '/' + dateParts[0] + '/' + dateParts[2];
			
			return Date.parse(date);
		} else if (_type == 'number') {
			return parseFloat(value);
		}
		
		if (value == null || value == "") {
			return null;
		}
		
		return value;
	};
	
	/**
	 * Set the value to the field
	 * @private
	 */
	var _setValue = function(value) {
		if (_type == 'date') {
			var d = new Date(value);
			var formatDay = (d.getDate()) > 9 ? (d.getDate()) : '0' + (d.getDate());
			var formatMonth = (d.getMonth() + 1) > 9 ? (d.getMonth() + 1) : '0' + (d.getMonth() + 1);
			value = formatDay + '/' + formatMonth + '/' + d.getFullYear();
		}
		
		_field.value = value;
		
		if (_field.value != "") {
			_label.style.fontSize = '10px';
			_label.style.top = '0px';
			_label.style.left = '5px';
			_label.style.paddingLeft = '10px';
			_label.style.paddingRight = '10px';
		} else {
			_label.style.fontSize = '14px';
			_label.style.top = '22px';
			_label.style.left = '40px';
			_label.style.paddingLeft = '0px';
			_label.style.paddingRight = '0px';
		}
		
		if (_hasValueChanged()) {
			_validate();
		}
	};
	
	/**
	 * Checks if the field is empty
	 * @private
	 */
	var _isEmpty = function() {
		return _getValue() == null || _getValue() == "";
	};
	
	/**
	 * Clear the value to the field
	 * @private
	 */
	var _clear = function() {
		_container.classList.remove("dauth-validation-error");
		_error.innerHTML = '';
		_error.style.display = 'none';
		_setValue("");
	};
	
	/**
	 * Check if a new value has been entered
	 * @private
	 */
	var _hasValueChanged = function() {
		if (_currentValue != _getRawValue()) {
			_currentValue = _getRawValue();
			return true;
		}
		
		return false;
	};
	
	/**
	 * Validate the field
	 * @private
	 */
	var _validate = function() {
		if (!_isEnabled()) {
			_container.classList.remove("dauth-validation-error");
			_error.innerHTML = '';
			_error.style.display = 'none';
			
			return { valid: true };
		}
		
		if (_isMandatory() && _isEmpty()) {
			_container.classList.add("dauth-validation-error");
			_error.innerHTML = 'Cannot be empty';
			_error.style.display = 'block';
			
			return { valid: false, reason: 'Cannot be empty' };
		}
		
		try {
			if (_validator != null) {
				return _validator(_getRawValue());
			}
		} catch (e) {
			_container.classList.add("dauth-validation-error");
			_error.innerHTML = e;
			_error.style.display = 'block';
			
			return { valid: true, reason: e };
		}
		
		_container.classList.remove("dauth-validation-error");
		_error.innerHTML = '';
		_error.style.display = 'none';
			
		return { valid: true };
	};
	
	/**
	 * Get the component Id
	 * @public
	 */
	this.getId = function() {
		return _uid;
	};
	
	/**
	 * Returns if the field enabled or not
	 * @public
	 */
	this.isEnabled = function() {
		return _isEnabled();
	};
	
	/**
	 * Enable or not the field
	 * @public
	 */
	this.setEnabled = function(enabled) {
		_setEnabled(enabled);
	};
	
	/**
	 * Returns if the field mandatory or not
	 * @public
	 */
	this.isMandatory = function() {
		return _isMandatory();
	};
	
	/**
	 * Set the field mandatory or not
	 * @public
	 */
	this.setMandatory = function(mandatory) {
		_setMandatory(mandatory);
	};
	
	/**
	 * Get the Selector
	 * @public
	 */
	this.getSelector = function() {
		return _getSelector();
	};
	
	/**
	 * Set the Selector
	 * @public
	 */
	this.setSelector = function(selector) {
		_setSelector(selector);
	};
	
	/**
	 * Get the pk from the field
	 * @public
	 */
	this.getPk = function() {
		return _getPk();
	};
	
	/**
	 * Set the pk to the field
	 * @public
	 */
	this.setPk = function(pk) {
		_setPk(pk);
	};
	
	/**
	 * Get the value from the field
	 * @public
	 */
	this.getValue = function() {
		return _getValue();
	};
	
	/**
	 * Set the value to the field
	 * @public
	 */
	this.setValue = function(value) {
		_setValue(value);
	};
	
	/**
	 * Checks if the field is empty
	 * @public
	 */
	this.isEmpty = function() {
		return _isEmpty();
	};
	
	/**
	 * Clear the value to the field
	 * @public
	 */
	this.clear = function() {
		_clear();
	};
	
	/**
	 * Validate the field
	 * @public
	 */
	this.validate = function() {
		return _validate();
	};
	
	/**
	 * Get the <pre>HTML Element</pre>
	 * @public
	 */
	this.get = function() {
		return _container;
	};
	
	/**
	 * Converts the parameters to an <pre>HTML Element</pre>
	 * @public
	 */
	this.set = function(element) {
		_setEnabled(_enabled);
		_setMandatory(_mandatory);
		element.appendChild(_container);
	};
	
}