var Utils = (function(lang) {
	
	/**
	 * Default lang 
	 * @private
	 */
	var _lang;
	
	/**
	 * File containing all text
	 * @private 
	 */
	var _file;
	
	/**
	 * Is File loaded
	 * @private 
	 */
	var _loaded = false;
	
	/**
	 * Set a new lang
	 * @private
	 */
	var _setLang = function(lang) {
		if (lang == null || lang == "") {
			return;
		}
		
		_lang = lang;
		_loaded = false
		
		fetch('i18n/' + _lang + '.json').then((res) => res.json()).then((translation) => {
			_file = translation;

			if (!_loaded) {
				_loaded = true;
			}
		});
	}
	
	/**
	 * Get text to be inserted in the element
	 * @private
	 */
	var _translate = function(val) {
		if (!_loaded) {
			setTimeout(function() {
				return _getText(val);
			}, 1000);
		}
		
		if (val == null || val == "") {
			return val;
		}
		
		var keys = val.split(".");
		return keys.reduce((obj, i) => obj[i], _file);
	}
	
	/**
	 * Convert String to Base64
	 * @private
	 */
	var _toBase64 = function(str, fromArrayBuffer) {
		if (fromArrayBuffer) {
			str = _fromArrayBuffer(str);
		}
		
		return btoa(str);
	};
	
	/**
	 * Convert Base64 to String
	 * @private
	 */
	var _fromBase64 = function(str) {
		return atob(str);
	};
	
	/**
	 * Convert String to an ArrayBuffer
	 * @private
	 */
	var _toArrayBuffer = function(str, fromBase64) {
		if (fromBase64) {
			str = _fromBase64(str);
		}
		
		return Uint8Array.from(str, function(c) { 
			return c.charCodeAt(0);
		});
	};
	
	/**
	 * Convert an ArrayBuffer to String
	 * @private
	 */
	var _fromArrayBuffer = function(arrayBuffer) {
		return new Uint8Array(arrayBuffer).reduce(function(data, byte) { 
			return data + String.fromCharCode(byte); 
		}, '');
	};
	
	// Set Lang on startup
	_setLang(lang);
	
	return {
		// String manipulations
		toBase64: _toBase64,
		fromBase64: _fromBase64,
		toArrayBuffer: _toArrayBuffer,
		fromArrayBuffer: _fromArrayBuffer,
		
		// Text translation
		setLang: _setLang,
		translate: _translate
	};
	
})(navigator.language.substr(0, 2));