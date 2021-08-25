<html>
<head>

<title>My SSO Dashboard</title>
<link rel="icon" type="image/png" href="img/gamma.png" sizes="16x16" />
<script src="https://kit.fontawesome.com/f33ecbb8a8.js" crossorigin="anonymous"></script>

<link rel="stylesheet" href="./lib/style/io-button-1.0.css" />
<link rel="stylesheet" href="./lib/style/io-checkbox-1.0.css" />
<link rel="stylesheet" href="./lib/style/io-field-1.0.css" />
<link rel="stylesheet" href="./lib/style/io-message-1.0.css" />
<link rel="stylesheet" href="./lib/style/io-ribbon-1.0.css" />
<link rel="stylesheet" href="./lib/style/io-select-1.0.css" />
<link rel="stylesheet" href="./lib/style/io-slide-1.0.css" />
<link rel="stylesheet" href="./lib/style/io-table-1.0.css" />
<link rel="stylesheet" href="./lib/style/io-upload-1.0.css" />

<script src="./lib/script/io-utils-1.0.js"></script>
<script src="./lib/script/io-button-1.0.js"></script>
<script src="./lib/script/io-checkbox-1.0.js"></script>
<script src="./lib/script/io-field-1.0.js"></script>
<script src="./lib/script/io-message-1.0.js"></script>
<script src="./lib/script/io-request-1.0.js"></script>
<script src="./lib/script/io-ribbon-1.0.js"></script>
<script src="./lib/script/io-select-1.0.js"></script>
<script src="./lib/script/io-slide-1.0.js"></script>
<script src="./lib/script/io-table-1.0.js"></script>
<script src="./lib/script/io-upload-1.0.js"></script>

<link rel="stylesheet" href="./style/dashboard.css" />
<script src="./script/dashboard.js"></script>

</head>
<body>

<!-- Webpage header -->
<header>
	<img src="./img/gamma.png" title="My SSO" />
	<span id="header-title">My SSO Dashboard</span>
</header>

<!-- Navigation elements -->
<nav></nav>

<div id="org">
	<div id="org-main">
		<span>${dashboardOrganizationInformation}</span>
	</div>
	<div id="org-claims">
		<span>${dashboardOrganizationClaims}</span>
	</div>
	<div id="org-roles">
		<span>${dashboardOrganizationRoles}</span>
	</div>
</div>
<div id="users">
	<div id="user-main">
		<span>${dashboardUsersInformation}</span>
	</div>
	<div id="user-claims">
		<span>${dashboardUsersClaims}</span>
	</div>
</div>
<div id="apps">
	<div id="app-main">
		<span>${dashboardApplicationsInformation}</span>
	</div>
	<div id="app-users">
		<span>${dashboardApplicationsAccess}</span>
	</div>
	<div id="app-claims">
		<span>${dashboardApplicationsClaims}</span>
	</div>
</div>

</body>
</html>