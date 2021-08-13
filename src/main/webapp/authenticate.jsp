<html>
<head>

<title>SSO Login</title>

<link rel="stylesheet" href="./style/authenticate.css" />
<link rel="icon" type="image/png" href="img/gamma.png" sizes="16x16" />
<script src="./script/authenticate.js"></script>

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
			<input type="checkbox" id="keepalive" name="keepalive" />
			<label for="keepalive">Activate SSO</label>
			
			<input type="hidden" id="username" name="username" value="" />
			<input type="hidden" id="organization" name="organization" value="" />
			
			<span id="username-error">${wrongUserPass}</span>
		</form>
		
	</div>

</div>

</body>
</html>