function Uploader(params) {
	
	/**
	 * Unique ID for the Uploader
	 * @private
	 */
	var _uid = params.id;
	
	/**
	 * Translatable labels
	 * @private
	 */
	var _translatable = !!params.translatable;
	
	/**
	 * Accepted MimeTypes for the Uploader
	 * @private
	 */
	var _accept = params.accept;
	
	/**
	 * URL to upload the data
	 * @private
	 */
	var _url = params.url;
	
	/**
	 * Other data to be sent to the server
	 * @private
	 */
	var _data = params.data;
	
	/**
	 * File Readers for the Uploader
	 * @private
	 */
	var _readers = [];
	
	/**
	 * Transform the data before sending to server
	 * @private
	 */
	var _transform = function(data) {
		return data;
	};
	
	/**
	 * Callback when all is uploaded to the server
	 * @private
	 */
	var _uploadEnd;
	
	function InternalFile(fileName, fileSize, fileType) {
			
		var _fileName = fileName;
		var _fileSize = fileSize;
		var _fileType = fileType;
		var _fileData;
		
		this.setData = function(data) {
			_fileData = Utils.toBase64(data, true);
		};
		
		this.get = function() {
			return {
				name: _fileName,
				size: _fileSize,
				type: _fileType,
				data: _fileData
			};
		};
			
	}
	
	function InternalReader(index, file) {
		
		var _index = index;
		var _file = file;
		var size = 1024;
		var prefix = 0;
		while (_file.size > size) {
			size = size * 1024;
			prefix++;
		}
		
		var _uploadable = true;
		var _uploadSuccess;
		var _onRemove;
		
		var fileReader = new FileReader();
		var prefixes = ["b", "kb", "Mb", "Gb"];
		var _internalFile = new InternalFile(_file.name, parseFloat(_file.size / (size / 1024)).toFixed(1) + prefixes[prefix], _file.type);
		
		var _request = new Request({
			method: 'PUT',
			successCode: 202,
			onUpload: function(e) {
				if (e.lengthComputable) {
					var percentComplete = e.loaded / e.total;
					_waiterBar.style.width = (percentComplete * 100) + "px";
				}
			},
			onSuccess: function(response) {
				_uploadSuccess(response.data);
			},
			onError: function(response) {
				var message = new Message({ id : 'upload-error-message', type : 'error', title : 'Error during upload' });
				message.setText("Error: " + response.data.message);
				message.show();
			}
		});
		
		var _container = document.createElement("div");
		_container.classList.add("dauth-uploader-loader");
		
		var _span = document.createElement("span");
		_span.classList.add("dauth-uploader-loader-text");
		_container.appendChild(_span);
		
		var _closer = document.createElement("i");
		_closer.classList.add("far");
		_closer.classList.add("fa-times-circle");
		_closer.addEventListener("click", function() { _close(); }, false);
		_container.appendChild(_closer);
		
		var _waiter = document.createElement("div");
		_waiter.classList.add("dauth-uploader-waiter");
		_container.appendChild(_waiter);
		
		var _waiterBar = document.createElement("div");
		_waiterBar.classList.add("dauth-uploader-waiter-bar");
		_waiter.appendChild(_waiterBar);
		
		var _show = function() {
			var file = _internalFile.get();
			
			_span.appendChild(document.createTextNode(file.name + " (" + file.size + ")"));
			document.getElementsByClassName("dauth-uploader-files")[0].appendChild(_container);
		};
		
		var _close = function() {
			_onRemove(_index, _file);
			
			_file == null;
			_internalFile == null;
			_uploadable = false;
			
			document.getElementsByClassName("dauth-uploader-files")[0].removeChild(_container);
		};
		
		var _upload = function(url, uploadData, transform) {
			if (!_uploadable) {
				return;
			}
			
			if (uploadData == null) {
				uploadData = {};
			}
			
			var f = _internalFile.get();
			for (var i in uploadData) {
				f[i] = uploadData[i];
			}
			
			_request.setUrl(url);
			_request.setData(transform(f));
			_request.send();
		};
		
		fileReader.onloadstart = function() {
			_show();
		};
	 
		fileReader.onload = function(e) {
			_internalFile.setData(e.target.result);
		};
		
		this.upload = function(url, data, transform) {
			_upload(url, data, transform);
		};
		
		this.onUploadSuccess = function(uploadSuccess) {
			_uploadSuccess = uploadSuccess;
		};
		
		this.onRemove = function(onRemove) {
			_onRemove = onRemove;
		};
	 
		this.process = function() {
			fileReader.readAsArrayBuffer(_file, "UTF-8");
		};
		
		this.close = function() {
			_close();
		};
		
	}
	
	/**
	 * Main HTML container for the Uploader
	 * @private
	 */
	var _container = document.createElement("div");
	_container.classList.add("dauth-uploader");
	_container.setAttribute("id", _uid);
	
	/**
	 * Input HTML element for the Uploader
	 * @private
	 */
	var _input = document.createElement("input");
	_input.classList.add("dauth-uploader-input");
	_input.setAttribute("type", "file");
	_input.setAttribute("accept", _accept);
	_input.addEventListener("change", function(e) {
		if (e.target.files.length > 0) {
			_handleFiles(e.target.files);
		}
	}, false);
	
	/**
	 * Image placeholder for File selection
	 * @private
	 */
	var _img = document.createElement("img");
	_img.src = "./lib/img/upload.png";
	_img.addEventListener("click", function(e) {
		_input.click();
	}, false);
	
	/**
	 * Button to upload content to server
	 * @private
	 */
	var _ok = new Button({ text : _translatable ? Utils.translate("defaults.btn.upload") : "Upload", uuid : "dauth-uploader-ok-btn" });
	
	var _filesPanel = document.createElement("div");
	_filesPanel.classList.add("dauth-uploader-files");
	_filesPanel.setAttribute("id", "dauth-uploader-files-" + _uid);
	_filesPanel.addEventListener("dragenter", function(e) {
		e.stopPropagation();
		e.preventDefault();
		
		e.target.classList.add("dauth-uploader-dragenter");
		e.dataTransfer.dropEffect = 'copy';
	}, false);
	_filesPanel.addEventListener("dragleave", function(e) {
		e.stopPropagation();
		e.preventDefault();
		
		e.target.classList.remove("dauth-uploader-dragenter");
	}, false);
	_filesPanel.addEventListener("dragover", function(e) {
		e.preventDefault();
		e.stopPropagation();
	}, false);
	_filesPanel.addEventListener("drop", function(e) {
		e.stopPropagation();
		e.preventDefault();
		
		if (e.dataTransfer.files.length > 0) {
			e.target.classList.remove("dauth-uploader-dragenter");
			_handleFiles(e.dataTransfer.files);
		}
	}, false);
	
	var _build = function() {
		var buttons = document.createElement("div");
		buttons.classList.add("dauth-uploader-buttons");
		
		_ok.setAction(function() {
			_ok.setEnabled(false);
			_upload(_url, _data);
		});
		buttons.appendChild(_ok.get());
		
		var cancel = new Button({ text : _translatable ? Utils.translate("defaults.btn.cancel") : "Cancel", uuid : "dauth-uploader-cancel-btn" });
		cancel.setAction(function() {
			_close();
		});
		buttons.appendChild(cancel.get());
		
		_container.appendChild(_input);
		_container.appendChild(_img);
		_container.appendChild(_filesPanel);
		_container.appendChild(buttons);
	};
	
	var _handleFiles = function(files) {
		for (var i = 0; i < files.length; i++) {
			var internalReader = new InternalReader(i, files[i]);
			internalReader.onRemove(function(index, file) {
				_readers.splice(index, 1);
			});
			
			_readers.push(internalReader);
		}
		
		_processFiles();
	};
	
	var _processFiles = function() {
		for (var i = 0; i < _readers.length; i++) {
			_readers[i].process();
		}
	};
	
	var _onUploadEnd = function(uploadEnd) {
		_uploadEnd = uploadEnd;
	};
	
	/**
	 * Upload all data and files to the server
	 * @private
	 */
	var _upload = function(url, data) {
		var uploaded = [];
		
		for (var i = 0; i < _readers.length; i++) {
			_readers[i].onUploadSuccess(function(data) {
				uploaded.push(data);
				
				if (uploaded.length == _readers.length) {
					_uploadEnd(uploaded);
					_close();
				}
			});
			
			_readers[i].upload(url, data, _transform);
		}
	};
	
	/**
	 * Build and show the Uploader
	 * @private
	 */
	var _show = function() {
		var overlay = document.createElement("div");
		overlay.setAttribute("id", "dauth-uploader-overlay");
		document.body.appendChild(overlay);
		
		_container.style.display = 'block';
		_ok.setEnabled(true);
	};
	
	/**
	 * Destroy and close Uploader
	 * @private
	 */
	var _close = function() {
		var overlay = document.getElementById("dauth-uploader-overlay");
		overlay.parentNode.removeChild(overlay);
		
		_container.style.display = 'none';
		
		for (var i = 0; i < _readers.length; i++) {
			_readers[i].close();
		}
		_readers = [];
	};
	
	/**
	 * Callback when File is uploaded
	 * @public
	 */
	this.onUploadEnd = function(uploadEnd) {
		_onUploadEnd(uploadEnd);
	};
	
	/**
	 * Build and show the Uploader
	 * @public
	 */
	this.show = function() {
		_show();
	};
	
	/**
	 * Destroy and close Uploader
	 * @public
	 */
	this.close = function() {
		_close();
	};
	
	/**
	 * Set data transformer
	 * @public
	 */
	this.setTransform = function(transform) {
		_transform = transform;
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