function Slide(params) {
	
	/**
	 * ID for the Slider
	 * @private
	 */
	var _uid = params.id;
	
	/**
	 * Title for the Slider
	 * @private
	 */
	var _title = params.title;
	
	/**
	 * Size for the Slider
	 * @private
	 */
	var _size = params.size;
	
	/**
	 * Check if the slider is already displayed
	 * @private
	 */
	var _loaded = false;
	
	/**
	 * All added components
	 * @private
	 */
	var _components = [];
	
	/**
	 * Slider Overlay
	 * @private
	 */
	var _overlay = document.createElement("div");
	_overlay.classList.add("dauth-slide-overlay");
	document.body.appendChild(_overlay);
	
	/**
	 * Slider
	 * @private
	 */
	var _slider = document.createElement("div");
	_slider.classList.add("dauth-slide");
	_slider.setAttribute("id", _uid);

	var _body = document.createElement("div");
	_body.classList.add("dauth-slide-body");
	
	var _title = document.createElement("span");
	
	/**
	 * Show the Slider
	 * @private
	 */
	var _show = function(params) {
		_overlay.style.display = 'block';
		_slider.style.width = _size + "px";
		_slider.style.opacity = 1;
		_loaded = true;
		
		if (params != null) {
			for (var i = 0; i < params.show.length; i++) {
				document.getElementById(params.show[i]).style.display = 'block';
			}
			for (var i = 0; i < params.hide.length; i++) {
				document.getElementById(params.hide[i]).style.display = 'none';
			}
		}
	};
	
	/**
	 * Hide the Slider
	 * @private
	 */
	var _hide = function() {
		_overlay.style.display = 'none';
		_slider.style.width = "0px";
		_slider.style.opacity = 0;
		_loaded = false;
	};
	
	/**
	 * Build the header of the Slider
	 * @private
	 */
	var _buildHeader = function() {
		var header = document.createElement("div");
		header.classList.add("dauth-slide-head");
		
		var button = document.createElement("button");
		button.appendChild(document.createTextNode("\u00D7"));
		button.addEventListener("click", function(e) {
			_clear();
			_hide();
		});
		
		_title.appendChild(document.createTextNode(_title));
		
		header.appendChild(button);
		header.appendChild(_title);
		
		return header;
	};
	
	/**
	 * Build the Slider
	 * @private
	 */
	var _buildSlider = function() {
		_slider.appendChild(_buildHeader());
		_slider.appendChild(_body);
	};
	
	/**
	 * Change the title of the Slider
	 * @private
	 */
	var _setTitle = function(title) {
		_title.textContent = title;
	};
	
	/**
	 * Set the values to the fields
	 * @private
	 */
	var _setValues = function(data) {
		for (var i = 0; i < _components.length; i++) {
			var value = data[_components[i].mapTo];
			
			if (value != null) {
				_components[i].component.setValue(value);
			}
		}
	};
	
	/**
	 * Add a component to the Area template
	 * @private
	 */
	var _addComponent = function(element) {
		if (element.component instanceof Field || element.component instanceof Checkbox) {
			// { component: element.component, mapTo: element.mapTo }
			_components.push(element);
		}
		
		element.component.set(_body);
	};
	
	/**
	 * Transforms the component into a JSON object
	 * @private
	 */
	var _toObject = function() {
		var obj = {};
		
		for (var i = 0; i < _components.length; i++) {
			var element = _components[i];
			
			if (element.component instanceof Field || element.component instanceof Checkbox) {
				obj[element.mapTo] = element.component.getValue();
			}
		}
		
		return obj;
	};
	
	/**
	 * Validate the Area template
	 * @private
	 */
	var _validate = function() {
		for (var i = 0; i < _components.length; i++) {
			var element = _components[i].component;
			
			if (element instanceof Field) {
				if (!element.validate().valid) {
					return false;
				}
			}
		}
		
		return true;
	};
	
	/**
	 * Clear all component in the Area template
	 * @private
	 */
	var _clear = function() {
		for (var i = 0; i < _components.length; i++) {
			_components[i].component.clear();
		}
	};
	
	/**
	 * Set the values to the fields
	 * @public
	 */
	this.setValues = function(data) {
		_setValues(data);
	};
	
	/**
	 * Add a component to the Area template
	 * @public
	 */
	this.addComponent = function(element) {
		_addComponent(element);
	};
	
	/**
	 * Transforms the component into a JSON object
	 * @public
	 */
	this.toObject = function() {
		return _toObject();
	};
	
	/**
	 * Validate the Area template
	 * @public
	 */
	this.validate = function() {
		return _validate();
	};
	
	/**
	 * Clear all component in the Area template
	 * @public
	 */
	this.clear = function() {
		_clear();
	};
	
	/**
	 * Show the Slider
	 * @public
	 */
	this.show = function(params) {
		_show(params);
	};
	
	/**
	 * Hide the Slider
	 * @public
	 */
	this.hide = function() {
		_hide();
	};
	
	/**
	 * Change the title of the Slider
	 * @public
	 */
	this.setTitle = function(title) {
		_setTitle(title);
	};
	
	/**
	 * Get the status of the slider
	 * @public
	 */
	this.isLoaded = function() {
		return _loaded;
	};
	
	/**
	 * Insert the Slider to a parent HTML Element
	 * @public
	 */
	this.set = function(element) {
		_buildSlider();
		element.appendChild(_slider);
	};
	
	/**
	 * Get the <pre>HTML Element</pre>
	 * @public
	 */
	this.get = function() {
		return _slider;
	};
	
}