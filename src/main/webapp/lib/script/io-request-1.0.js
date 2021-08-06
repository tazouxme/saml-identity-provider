var Request = function(params) {
	
	/**
	 * Unique Request ID
	 * @private
	 */
	var _id = "ID_" + Math.random().toString(16).substring(2, 19);
	
	/**
	 * Method of the request (GET, POST, ...)
	 * @private
	 */
	var _method = params.method;
	
	/**
	 * URL of the request
	 * @private
	 */
	var _url = params.url;
	
	/**
	 * Response code for successful request
	 * @private
	 */
	var _successCode = params.successCode;
	
	/**
	 * Asynchronous request
	 * @private
	 */
	var _async = params.async != null ? params.async : true;
	
	/**
	 * Response type for successful request (default JSON)
	 * @private
	 */
	var _responseType = params.responseType != null ? params.responseType : 'json';
	
	/**
	 * Data sent with the request
	 * @private
	 */
	var _data = params.data;
	
	/**
	 * Request Headers
	 * @private
	 */
	var _headers = [];
	_headers.push({ name: "Cache-Control", value: "no-cache" });
	_headers.push({ name: "Content-Type", value: "application/json" });
	_headers.push({ name: "x-csrf", value: _id });
	
	var _onSuccess = params.onSuccess;
	var _onError = params.onError;
	var _onUpload = params.onUpload;
	var _onProgress = params.onProgress;
	
	/**
	 * Main request object
	 * @private
	 */
	var _request = new XMLHttpRequest();
	
	/**
	 * Add Request Header
	 * @private
	 */
	var _addHeader = function(key, value) {
		_headers.push({ name: key, value: value });
	};
	
	/**
	 * Send the request
	 * @private
	 */
	var _send = async function() {
		_request.onload = async function() {
			if (this.status == _successCode) {
				_onSuccess({ status: _request.status, statusText: _request.statusText, data: _request.response });
			} else {
				_onError({ status: _request.status, statusText: _request.statusText, data: _request.response });
			}
		};
		
		if (_onUpload != null) {
			_request.upload.addEventListener("progress", _onUpload, false);
		}
		
		if (_onProgress != null) {
			_request.addEventListener("progress", _onProgress, false);
		}

		_request.open(_method, _url, _async);
		_request.responseType = _responseType;
		
		for (var i in _headers) {
			_request.setRequestHeader(_headers[i].name, _headers[i].value);
		}
		
		_request.send(await _obtainData());
	};
	
	/**
	 * Retrieve the data for the request
	 * @private
	 */
	var _obtainData = async function() {
		if (_data == null) {
			return null;
		}
		
		return JSON.stringify(_data);
	};
	
	/**
	 * Add Request Header
	 * @public
	 */
	this.addHeader = function(key, value) {
		_addHeader(key, value);
	};
	
	/**
	 * Send the request
	 * @public
	 */
	this.send = function() {
		_send();
	};
	
}