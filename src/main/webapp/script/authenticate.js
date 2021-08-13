document.addEventListener("DOMContentLoaded", (e) => {
	
var usernameField = document.getElementById('email');
var passwordField = document.getElementById('password');
var sumbitButton = document.getElementById('submit-username');

var form = document.getElementsByTagName('form')[0];
form.addEventListener('submit', async function(e) {
	e.preventDefault();
	
	sumbitButton.classList.add("disabled");
	sumbitButton.setAttribute("disabled", "disabled");
	
	if (await login(usernameField.value, passwordField.value)) {
		form.submit();
	}
	
	sumbitButton.classList.remove("disabled");
	sumbitButton.removeAttribute("disabled");
}, false);

/**
 * Do an HTTP Request
 * @private
 */
var _makeRequest = function(opts) {
	return new Promise(function(resolve, reject) {
		var xhr = new XMLHttpRequest();
		xhr.open(opts.method, opts.url);
		xhr.responseType = 'json';
		xhr.onload = function() {
			if (this.status == opts.status) {
				resolve(xhr);
			} else {
				reject({
					status: this.status,
					error: xhr.getResponseHeader('x-error')
				});
			}
		};
		xhr.onerror = function() {
			reject({
				status: this.status,
				error: xhr.getResponseHeader('x-error')
			});
		};
		
		if (opts.headers) {
			Object.keys(opts.headers).forEach(function(key) {
				xhr.setRequestHeader(key, opts.headers[key]);
			});
		}

		xhr.send(JSON.stringify(opts.params));
	});
};
	
/**
 * Convert String to Base64
 * @private
 */
var _toBase64 = function(str) {
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
var _toArrayBuffer = function(str) {
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

/**
 * Get the entered password and encrypt it
 * @private
 */
var _getPassword = async function(password, secretKey) {
	const iv = window.crypto.getRandomValues(new Uint8Array(16));
	const cipherText = await window.crypto.subtle.encrypt({ name: "AES-CBC", iv: iv }, secretKey, _toArrayBuffer(password));
	
	return { iv: _toBase64(_fromArrayBuffer(iv)), password : _toBase64(_fromArrayBuffer(cipherText)) };
};

var login = async function(username, password) {
	// On first run, generate a KeyPair
	const keys = await window.crypto.subtle.generateKey({ name: "ECDH", namedCurve: "P-384" }, true, ["deriveKey"]);
	
	// Export the generated PublicKey as a Base64 encoded String
	const clientPublicKey = await window.crypto.subtle.exportKey("spki", keys.publicKey);
	const clientPublicKeyExported = _toBase64(_fromArrayBuffer(clientPublicKey));
	
	// Tell the server to send a generated PublicKey, convert to ArrayBuffer and import as a PublicKey
	var result;
	try {
		result = await _makeRequest({
			method: 'HEAD',
			url: './login',
			status: 202,
			headers: {
				'x-csrf': clientPublicKeyExported,
				'x-username': username,
				'x-public-key': clientPublicKeyExported
			}
		});
	} catch (e) {
		document.getElementById('username-error').innerHTML = e.error;
		return false;
	}
	
	document.getElementById('organization').value = result.getResponseHeader('x-organization');
	document.getElementById('username').value = result.getResponseHeader('x-username');
	
	const serverPublicKeyExported = _toArrayBuffer(_fromBase64(result.getResponseHeader('x-public-key')));
	const serverPublicKeyImported = await window.crypto.subtle.importKey("spki", serverPublicKeyExported, { name: "ECDH", namedCurve: "P-384" }, true, []);
	
	// Generate the SecretKey for data transmission
	const secretKey = await window.crypto.subtle.deriveKey({ name: "ECDH", public: serverPublicKeyImported }, keys.privateKey, { name: "AES-CBC", length: 256 }, true, ["encrypt", "decrypt"]);
	
	document.getElementById('password').value = JSON.stringify(await _getPassword(password, secretKey));
	return true;
}
	
});