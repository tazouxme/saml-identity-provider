<html>
<head>

<title>SSO Register</title>

<link rel="stylesheet" href="./style/register.css" />
<link rel="icon" type="image/png" href="img/gamma.png" sizes="16x16" />

</head>
<body>

<div id="register">

	<div id="register-header">Register to SSO</div>

	<div id="register-form">
		
		<form action="./register" method="post">
			<input type="text" class="organization" name="organization" placeholder="organization code" />
			<input type="text" class="organization" name="domain" placeholder="organization domain" />
			
			<input type="text" class="separator" name="username" placeholder="username" />
			<input type="password" class="user" name="password" placeholder="****" />
			
			<input type="submit" id="submit-username" name="submit-username" value="Register" />
			
			<span id="register-error">${registerError}</span>
			<span id="register-ok">${registerOk}</span>
		</form>
		
	</div>

</div>

</body>
</html>