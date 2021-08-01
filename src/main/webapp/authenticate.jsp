<html>
<head>

<title>SSO Login</title>

<link rel="stylesheet" href="./style/authenticate.css" />
<script src="./script/authenticate.js"></script>
<script>

document.addEventListener("DOMContentLoaded", (e) => {
	var usernameField = document.getElementById('email');
	var passwordField = document.getElementById('password');
	
	var form = document.getElementsByTagName('form')[0];
	form.addEventListener('submit', async function(e) {
		e.preventDefault();
		
		if (await login(usernameField.value, passwordField.value)) {
			form.submit();
		}
	}, false);
})

</script>

</head>
<body>

<div id="login">

	<div id="login-header">My SSO</div>
	
	<div id="login-register">
		<span>No account? <a href="./register" title="Register">Register now</a></span>
	</div>

	<div id="username-form">
		
		<form action="./login" method="post">
			<input type="text" id="email" placeholder="username" />
			<input type="password" id="password" name="password" placeholder="****" />
			<input type="submit" id="submit-username" name="submit-username" value="Enter" />
			
			<input type="hidden" id="username" name="username" value="" />
			<input type="hidden" id="organization" name="organization" value="" />
			
			<span id="username-error">${wrongUserPass}</span>
		</form>
		
	</div>

</div>

</body>
</html>