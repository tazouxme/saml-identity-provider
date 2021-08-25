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
	var _setLang = async function(lang) {
		if (lang == null || lang == "") {
			return;
		}
		
		_lang = lang;
		_loaded = false;
		
		await _loadFile();
		
		document.dispatchEvent(new Event("I18nContentLoaded"));
	}
	
	/**
	 * Load File
	 * @private
	 */
	var _loadFile = async function() {
		var response = await fetch('i18n/' + _lang + '.json');
		_file = await response.json();
		_loaded = true;
	};
	
	/**
	 * Get text to be inserted in the element
	 * @private
	 */
	var _translate = function(val) {
		if (val == null || val == "") {
			return val;
		}
		
		var entries = val.split("|");
		val = entries.splice(entries, 1)[0];
		
		try {
			var translated = val.split(".").reduce((obj, i) => obj[i], _file);
			
			if (entries.length > 0) {
				translated = _parse(translated, entries);
			}
			
			translated = decodeURI(translated);
			
			if (translated == 'undefined') {
				return "!!" + val + "!!";
			}
			
			return translated;
		} catch (e) {
			return "!!" + val + "!!";
		}
	}
	
	/**
	 * Parse text and replace %s by a value
	 * @private
	 */
	var _parse = function(str) {
		var args = [].slice.call(arguments, 1);
        var i = 0;

    	return str.replace(/%s/g, () => args[i++]);
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
		translate: _translate,
		parse: _parse
	};
	
})(navigator.language.substr(0, 2));