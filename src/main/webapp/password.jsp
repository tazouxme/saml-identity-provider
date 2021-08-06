<html>
<head>

<title>SSO Password change</title>

<link rel="stylesheet" href="./style/password.css" />
<link rel="icon" type="image/png" href="img/gamma.png" sizes="16x16" />

</head>
<body>

<div id="password">

	<div id="password-header">SSO Password change</div>

	<div id="password-form">
		
		<form action="./activate" method="post">
			<input type="password" id="enterPassword" name="password" placeholder="****" />
			<input type="password" id="checkPassword" name="checkPassword" placeholder="****" />
			<input type="submit" id="submit-password" name="submit-password" value="Change" />
			
			<input type="hidden" id="action" name="action" value="${action}" />
			<input type="hidden" id="username" name="username" value="${username}" />
			<input type="hidden" id="organization" name="organization" value="${organization}" />
			
			<span id="password-error">${wrongPass}</span>
		</form>
		
	</div>

</div>

</body>
</html>