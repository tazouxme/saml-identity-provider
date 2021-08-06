document.addEventListener('DOMContentLoaded', function() {

var updateOrgBtn = new Button({ id: 'update-org-btn', text: 'Edit Org.', icon: './lib/img/edit.png' });
var claimsBtn = new Button({ id: 'claims-btn', text: 'Claims', icon: './lib/img/claims.png' });
var rolesBtn = new Button({ id: 'roles-btn', text: 'Roles', icon: './lib/img/roles.png' });
var keyBtn = new Button({ id: 'key-btn', text: 'Public Key', icon: './lib/img/key.png' });

var orgIdField = new Field({ id: "org-id-field", title: "ID", name: "org-id", type: "text", icon: "", maxLength: "16", width: "250" });
orgIdField.setEnabled(false);
orgIdField.set(document.getElementById("org-main"));

var orgCodeField = new Field({ id: "org-code-field", title: "Code", name: "org-code", type: "text", icon: "", maxLength: "16", width: "250" });
orgCodeField.setEnabled(false);
orgCodeField.set(document.getElementById("org-main"));

var orgNameField = new Field({ id: "org-name-field", title: "Name", name: "org-name", type: "text", icon: "", maxLength: "50", width: "250" });
orgNameField.setEnabled(false);
orgNameField.set(document.getElementById("org-main"));

var orgDomainField = new Field({ id: "org-domain-field", title: "Domain", name: "org-domain", type: "text", icon: "", maxLength: "128", width: "250" });
orgDomainField.setEnabled(false);
orgDomainField.set(document.getElementById("org-main"));

var orgDescriptionField = new Field({ id: "org-description-field", title: "Description", name: "org-description", type: "text", icon: "", maxLength: "200", width: "500" });
orgDescriptionField.setEnabled(false);
orgDescriptionField.set(document.getElementById("org-main"));

var orgCreationDateField = new Field({ id: "org-date-field", title: "Creation Date", name: "org-date", type: "date", icon: "", maxLength: "16", width: "250" });
orgCreationDateField.setEnabled(false);
orgCreationDateField.set(document.getElementById("org-main"));

var addUserBtn = new Button({ id: 'add-user-btn', text: 'Add User', icon: './lib/img/add_user.png' });
addUserBtn.setAction(function() {
	userSlide.setTitle("Create User");
	userSlide.show({ show : ["slide-user-save-btn", "slide-user-cancel-btn"], hide : ["slide-user-update-btn"] });
});

var updateUserBtn = new Button({ id: 'update-user-btn', text: 'Edit User', icon: './lib/img/edit_user.png' });
updateUserBtn.setEnabled(false);
updateUserBtn.setAction(function() {
	userSlide.setValues(usersTable.getSelectedData());
	userSlide.setTitle("Edit User");
	userSlide.show({ show : ["slide-user-update-btn", "slide-user-cancel-btn"], hide : ["slide-user-save-btn"] });
});

var removeUserBtn = new Button({ id: 'remove-user-btn', text: 'Remove User', icon: './lib/img/delete_user.png' });
removeUserBtn.setEnabled(false);
removeUserBtn.setAction(function() {
	var message = new Message({ id : 'remove-user-message', type : 'question', title : 'Delete User' });
	message.setText("Delete the User " + usersTable.getSelectedData().username + "?");
	message.onValidate(function() {
		var user = usersTable.getSelectedData();
		console.log(user);
	});
	
	message.show();
});

var saveUserBtn = new Button({ id: 'slide-user-save-btn', text: 'Save', icon: './lib/img/add.png' });
saveUserBtn.setAction(function() {
	if (userSlide.validate()) {
		createUser(userSlide.toObject());
	}
});
var modifyUserBtn = new Button({ id: 'slide-user-update-btn', text: 'Modify', icon: './lib/img/edit.png' });
modifyUserBtn.setAction(function() {
	if (userSlide.validate()) {
		console.log(userSlide.toObject());
		//updateUser(userSlide.toObject());
	}
});
var cancelUserBtn = new Button({ id: 'slide-user-cancel-btn', text: 'Cancel', icon: './lib/img/cancel.png' });
cancelUserBtn.setAction(function() {
	if (userSlide.isLoaded()) {
		userSlide.clear();
		userSlide.hide();
	}
});

var activateUserBtn = new Button({ id: 'activate-user-btn', text: 'Unlock User', icon: './lib/img/unlock.png' });
activateUserBtn.setEnabled(false);

var deactivateUserBtn = new Button({ id: 'deactivate-user-btn', text: 'Lock User', icon: './lib/img/lock.png' });
deactivateUserBtn.setEnabled(false);

var settingsUserBtn = new Button({ id: 'settings-user-btn', text: 'Data', icon: './lib/img/claims_user.png' });
settingsUserBtn.setEnabled(false);

var addAppBtn = new Button({ id: 'add-app-btn', text: 'Add App.', icon: './lib/img/add_app.png' });
addAppBtn.setAction(function() {
	applicationSlide.setTitle("Create Application");
	applicationSlide.show({ show : ["slide-app-save-btn", "slide-app-cancel-btn"], hide : ["slide-app-update-btn"] });
});

var updateAppBtn = new Button({ id: 'update-app-btn', text: 'Edit App.', icon: './lib/img/edit_app.png' });
updateAppBtn.setEnabled(false);
updateAppBtn.setAction(function() {
	applicationSlide.setValues(appsTable.getSelectedData());
	applicationSlide.setTitle("Edit Application");
	applicationSlide.show({ show : ["slide-app-update-btn", "slide-app-cancel-btn"], hide : ["slide-app-save-btn"] });
});

var removeAppBtn = new Button({ id: 'remove-app-btn', text: 'Remove App.', icon: './lib/img/delete_app.png' });
removeAppBtn.setEnabled(false);
removeAppBtn.setAction(function() {
	var message = new Message({ id : 'remove-app-message', type : 'question', title : 'Delete Application' });
	message.setText("Delete the Application " + appsTable.getSelectedData().name + " ("+ appsTable.getSelectedData().urn +") ?");
	message.onValidate(function() {
		var app = appsTable.getSelectedData();
		deleteApplication(app);
	});
	
	message.show();
});

var saveAppBtn = new Button({ id: 'slide-app-save-btn', text: 'Save', icon: './lib/img/add.png' });
saveAppBtn.setAction(function() {
	if (applicationSlide.validate()) {
		createApplication(applicationSlide.toObject());
	}
});
var modifyAppBtn = new Button({ id: 'slide-app-update-btn', text: 'Modify', icon: './lib/img/edit.png' });
modifyAppBtn.setAction(function() {
	if (applicationSlide.validate()) {
		updateApplication(applicationSlide.toObject());
	}
});
var cancelAppBtn = new Button({ id: 'slide-app-cancel-btn', text: 'Cancel', icon: './lib/img/cancel.png' });
cancelAppBtn.setAction(function() {
	if (applicationSlide.isLoaded()) {
		applicationSlide.clear();
		applicationSlide.hide();
	}
});

var accessAppBtn = new Button({ id: 'access-app-btn', text: 'Add Access', icon: './lib/img/access_app.png' });
accessAppBtn.setEnabled(false);

var lockUserAppBtn = new Button({ id: 'lock-user-app-btn', text: 'Lock Access', icon: './lib/img/lock_app.png' });
lockUserAppBtn.setEnabled(false);
lockUserAppBtn.setAction(function() {
	var message = new Message({ id : 'lock-user-app-message', type : 'question', title : 'Lock User Access' });
	message.setText("Lock access for User " + usersTable.getSelectedData().username + "?");
	message.onValidate(function() {
		
	});
	
	message.show();
});

var unlockUserAppBtn = new Button({ id: 'unlock-user-app-btn', text: 'Unlock Access', icon: './lib/img/unlock_app.png' });
unlockUserAppBtn.setEnabled(false);
unlockUserAppBtn.setAction(function() {
	var message = new Message({ id : 'unlock-user-app-message', type : 'question', title : 'Unlock User Access' });
	message.setText("Unlock access for User " + usersTable.getSelectedData().username + "?");
	message.onValidate(function() {
		
	});
	
	message.show();
});

var revokeUserAppBtn = new Button({ id: 'revoke-app-btn', text: 'Revoke', icon: './lib/img/revoke_app.png' });
revokeUserAppBtn.setEnabled(false);
revokeUserAppBtn.setAction(function() {
	var message = new Message({ id : 'revoke-user-app-message', type : 'question', title : 'Revoke User Access' });
	message.setText("Revoke access for User " + usersTable.getSelectedData().username + "?");
	message.onValidate(function() {
		
	});
	
	message.show();
});

var ribbon = new Ribbon({ id: 'dashboard-ribbon' });
ribbon.addMenu('dashboard-ribbon-organization', 'Organization', function() {
	document.getElementById("org").style.display = 'block';
	document.getElementById("users").style.display = 'none';
	document.getElementById("apps").style.display = 'none';
	
	usersTable.clear();
	userClaimsTable.clear();
	appsTable.clear();
	appUsersTable.clear();
	appClaimsTable.clear();
	
	loadOrganization();
});
ribbon.addMenu('dashboard-ribbon-user', 'Users', function() {
	document.getElementById("org").style.display = 'none';
	document.getElementById("users").style.display = 'block';
	document.getElementById("apps").style.display = 'none';
	
	claimsTable.clear();
	rolesTable.clear();
	appsTable.clear();
	appUsersTable.clear();
	appClaimsTable.clear();

	loadUsers();
});
ribbon.addMenu('dashboard-ribbon-application', 'Applications', function() {
	document.getElementById("org").style.display = 'none';
	document.getElementById("users").style.display = 'none';
	document.getElementById("apps").style.display = 'block';
	
	claimsTable.clear();
	rolesTable.clear();
	usersTable.clear();
	userClaimsTable.clear();
	
	loadApplications();
});

ribbon.getMenu('dashboard-ribbon-organization').addSection('Data', [ updateOrgBtn ]);
ribbon.getMenu('dashboard-ribbon-organization').addSection('Globals', [ claimsBtn, rolesBtn, keyBtn ]);
ribbon.getMenu('dashboard-ribbon-user').addSection('Data', [ addUserBtn, updateUserBtn, removeUserBtn ]);
ribbon.getMenu('dashboard-ribbon-user').addSection('Actions', [ activateUserBtn, deactivateUserBtn ]);
ribbon.getMenu('dashboard-ribbon-user').addSection('Settings', [ settingsUserBtn ]);
ribbon.getMenu('dashboard-ribbon-application').addSection('Data', [ addAppBtn, updateAppBtn, removeAppBtn ]);
ribbon.getMenu('dashboard-ribbon-application').addSection('Access', [ accessAppBtn, unlockUserAppBtn, lockUserAppBtn, revokeUserAppBtn ]);

ribbon.set(document.body);
ribbon.setSelected('dashboard-ribbon-organization');

var userSlide = new Slide({ id: 'user-slide', title: 'User', size: 500 });
userSlide.addComponent({ mapTo: 'id', component: new Field({ id: "slide-user-id-field", title: "ID", type: "text", icon: "", maxLength: "32", width: "250", enabled: false }) });
userSlide.addComponent({ mapTo: 'username', component: new Field({ id: "slide-user-username-field", title: "Username", type: "text", icon: "", maxLength: "32", width: "250" }) });
userSlide.addComponent({ mapTo: 'administrator', component: new Checkbox({ id: "slide-user-admin-field", title: "Administrator" }) });
userSlide.addComponent({ mapTo: '', component: saveUserBtn });
userSlide.addComponent({ mapTo: '', component: modifyUserBtn });
userSlide.addComponent({ mapTo: '', component: cancelUserBtn });
userSlide.set(document.body);

var applicationSlide = new Slide({ id: 'app-slide', title: 'Application', size: 500 });
applicationSlide.addComponent({ mapTo: 'id', component: new Field({ id: "slide-app-id-field", title: "ID", type: "text", icon: "", maxLength: "32", width: "250", enabled: false }) });
applicationSlide.addComponent({ mapTo: 'urn', component: new Field({ id: "slide-app-urn-field", title: "URN", type: "text", icon: "", maxLength: "32", width: "250", mandatory: true }) });
applicationSlide.addComponent({ mapTo: 'name', component: new Field({ id: "slide-app-name-field", title: "Name", type: "text", icon: "", maxLength: "32", width: "250", mandatory: true }) });
applicationSlide.addComponent({ mapTo: 'description', component: new Field({ id: "slide-app-description-field", title: "Description", type: "text", icon: "", maxLength: "32", width: "450" }) });
applicationSlide.addComponent({ mapTo: 'acsUrl', component: new Field({ id: "slide-app-acs-field", title: "Assertion URL", type: "text", icon: "", maxLength: "32", width: "450", mandatory: true }) });
applicationSlide.addComponent({ mapTo: '', component: saveAppBtn });
applicationSlide.addComponent({ mapTo: '', component: modifyAppBtn });
applicationSlide.addComponent({ mapTo: '', component: cancelAppBtn });
applicationSlide.set(document.body);

var claimsTable = new Table({
	id : 'org-claims-table', 
	columnPk : 3, 
	columns : ["URI", "Name", "Description"], 
	mimeTypes : [{ code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }], 
	sizes : [25, 25, 25]
});
claimsTable.setSort(function(a, b) {
	return a[0].value.localeCompare(b[0].value);
});
claimsTable.set(document.getElementById("org-claims"));

var rolesTable = new Table({
	id : 'org-roles-table', 
	columnPk : 2, 
	columns : ["URI", "Name"], 
	mimeTypes : [{ code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }], 
	sizes : [25, 25]
});
rolesTable.setSort(function(a, b) {
	return a[0].value.localeCompare(b[0].value);
});
rolesTable.set(document.getElementById("org-roles"));

var usersTable = new Table({
	id : 'users-table', 
	columnPk : 4, 
	columns : ["Username", "Email", "Enabled", "Administrator"], 
	mimeTypes : [{ code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }, { code: "BOOLEAN", name: "Boolean" }, { code: "BOOLEAN", name: "Boolean" }], 
	sizes : [25, 25, 25, 25]
});
usersTable.setSelectionListener({
	onSelect : function(e, pk) {
		updateUserBtn.setEnabled(true);
		removeUserBtn.setEnabled(true);
		activateUserBtn.setEnabled(!usersTable.getSelectedData().enabled);
		deactivateUserBtn.setEnabled(usersTable.getSelectedData().enabled);
		
		loadUser(pk);
	},
	onUnselect : function(pk) {
		updateUserBtn.setEnabled(false);
		removeUserBtn.setEnabled(false);
		activateUserBtn.setEnabled(false);
		deactivateUserBtn.setEnabled(false);
		
		userClaimsTable.clear();
	}
});
usersTable.setSort(function(a, b) {
	return a[0].value.localeCompare(b[0].value);
});
usersTable.set(document.getElementById("user-main"));

var userClaimsTable = new Table({
	id : 'user-claims-table', 
	columnPk : 1, 
	columns : ["URI", "Name", "Value", "Creation Date"], 
	mimeTypes : [{ code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }, { code: "DATE", name: "Date" }], 
	sizes : [25, 25, 25, 25]
});
userClaimsTable.setSort(function(a, b) {
	return a[0].value.localeCompare(b[0].value);
});
userClaimsTable.set(document.getElementById("user-claims"));
	
loadOrganization();

var appsTable = new Table({
	id : 'apps-table', 
	columnPk : 4, 
	columns : ["Name", "URN", "Description", "ACS URL"], 
	mimeTypes : [{ code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }], 
	sizes : [25, 25, 25, 25]
});
appsTable.setSelectionListener({
	onSelect : function(e, pk) {
		updateAppBtn.setEnabled(true);
		removeAppBtn.setEnabled(true);
		accessAppBtn.setEnabled(true);

		loadApplication(appsTable.getSelectedData().urn);
	},
	onUnselect : function(pk) {
		updateAppBtn.setEnabled(false);
		removeAppBtn.setEnabled(false);
		accessAppBtn.setEnabled(false);
		
		unlockUserAppBtn.setEnabled(false);
		lockUserAppBtn.setEnabled(false);
		revokeUserAppBtn.setEnabled(false);
		
		appUsersTable.clear();
		appClaimsTable.clear();
	}
});
appsTable.setSort(function(a, b) {
	return a[0].value.localeCompare(b[0].value);
});
appsTable.set(document.getElementById("app-main"));

var appUsersTable = new Table({
	id : 'app-users-table', 
	columnPk : 3, 
	columns : ["Email", "Role", "Enabled"], 
	mimeTypes : [{ code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }, { code: "BOOLEAN", name: "Boolean" }], 
	sizes : [25, 25, 25]
});
appUsersTable.setSelectionListener({
	onSelect : function(e, pk) {
		unlockUserAppBtn.setEnabled(!appUsersTable.getSelectedData().enabled);
		lockUserAppBtn.setEnabled(appUsersTable.getSelectedData().enabled);
		revokeUserAppBtn.setEnabled(true);
	},
	onUnselect : function(pk) {
		unlockUserAppBtn.setEnabled(false);
		lockUserAppBtn.setEnabled(false);
		revokeUserAppBtn.setEnabled(false);
	}
});
appUsersTable.setSort(function(a, b) {
	return a[0].value.localeCompare(b[0].value);
});
appUsersTable.set(document.getElementById("app-users"));

var appClaimsTable = new Table({
	id : 'app-claims-table', 
	columnPk : 3, 
	columns : ["URI", "Name", "Description"], 
	mimeTypes : [{ code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }], 
	sizes : [25, 25, 25]
});
appClaimsTable.setSort(function(a, b) {
	return a[0].value.localeCompare(b[0].value);
});
appClaimsTable.set(document.getElementById("app-claims"));

// ---

function loadOrganization() {
	new Request({
		method: "GET", 
		url: "./api/v1/organization", 
		successCode: 200, 
		onSuccess: function(response) {
			orgIdField.setValue(response.data.id);
			orgCodeField.setValue(response.data.code);
			orgDomainField.setValue(response.data.domain);
			orgNameField.setValue(response.data.name);
			orgDescriptionField.setValue(response.data.description);
			orgCreationDateField.setValue(response.data.creationDate);
			
			var p = document.getElementById("org-public-key").getElementsByTagName("p")[0];
			p.innerHTML = response.data.publicKey;
			
			var claims = [];
			for (var i = 0; i < response.data.claims.length; i++) {
				claims.push([
					{ column: "uri", value: response.data.claims[i].uri }, 
					{ column: "name", value: response.data.claims[i].name }, 
					{ column: "description", value: response.data.claims[i].description }, 
					{ column: "id", value: response.data.claims[i].id }
				]);
			}
			
			claimsTable.setData(claims);
			
			var roles = [];
			for (var i = 0; i < response.data.roles.length; i++) {
				roles.push([
					{ column: "uri", value: response.data.roles[i].uri }, 
					{ column: "name", value: response.data.roles[i].name }, 
					{ column: "id", value: response.data.roles[i].id }
				]);
			}
			
			rolesTable.setData(roles);
		}
	}).send();
}

function loadUsers() {
	new Request({
		method: "GET", 
		url: "./api/v1/users", 
		successCode: 200, 
		onSuccess: function(response) {
			var apps = [];
			for (var i = 0; i < response.data.length; i++) {
				apps.push([
					{ column: "username", value: response.data[i].username }, 
					{ column: "email", value: response.data[i].email }, 
					{ column: "enabled", value: response.data[i].enabled }, 
					{ column: "administrator", value: response.data[i].administrator }, 
					{ column: "id", value: response.data[i].id }
				]);
			}
			
			usersTable.setData(apps);
		}
	}).send();
}

function createUser(data) {
	new Request({
		method: "POST", 
		url: "./api/v1/user", 
		successCode: 201,
		data: data,
		onSuccess: function(response) {
			usersTable.addLine([
				{ column: 'username', value: response.data.username }, 
				{ column: 'email', value: response.data.email }, 
				{ column: 'enabled', value: response.data.enabled }, 
				{ column: 'administrator', value: response.data.administrator }, 
				{ column: 'id', value: response.data.id }
			]);
			usersTable.setSelectedLine(response.data.id);
			
			if (userSlide.isLoaded()) {
				userSlide.clear();
				userSlide.hide();
			}
		}
	}).send();
}

function loadUser(pk) {
	new Request({
		method: "GET", 
		url: "./api/v1/user/" + pk, 
		successCode: 200, 
		onSuccess: function(response) {
			var claims = [];
			for (var i = 0; i < response.data.details.length; i++) {
				claims.push([
					{ column: "uri", value: response.data.details[i].claim.uri }, 
					{ column: "name", value: response.data.details[i].claim.name }, 
					{ column: "value", value: response.data.details[i].claimValue }, 
					{ column: "date", value: response.data.details[i].creationDate }
				]);
			}
			
			userClaimsTable.setData(claims);
		}
	}).send();
}

function loadApplications() {
	new Request({
		method: "GET", 
		url: "./api/v1/applications", 
		successCode: 200, 
		onSuccess: function(response) {
			var apps = [];
			for (var i = 0; i < response.data.length; i++) {
				apps.push([
					{ column: "name", value: response.data[i].name }, 
					{ column: "urn", value: response.data[i].urn }, 
					{ column: "description", value: response.data[i].description }, 
					{ column: "acsUrl", value: response.data[i].acsUrl }, 
					{ column: "id", value: response.data[i].id }
				]);
			}
			
			appsTable.setData(apps);
		}
	}).send();
}

function createApplication(data) {
	new Request({
		method: "POST", 
		url: "./api/v1/application", 
		successCode: 201,
		data: data,
		onSuccess: function(response) {
			appsTable.addLine([
				{ column: 'name', value: response.data.name }, 
				{ column: 'urn', value: response.data.urn }, 
				{ column: 'description', value: response.data.description }, 
				{ column: 'acsUrl', value: response.data.acsUrl }, 
				{ column: 'id', value: response.data.id }
			]);
			appsTable.setSelectedLine(response.data.id);
			
			if (applicationSlide.isLoaded()) {
				applicationSlide.clear();
				applicationSlide.hide();
			}
		}
	}).send();
}

function updateApplication(data) {
	new Request({
		method: "PATCH", 
		url: "./api/v1/application", 
		successCode: 202,
		data: data,
		onSuccess: function(response) {
			appsTable.updateLine([
				{ column: 'name', value: response.data.name }, 
				{ column: 'urn', value: response.data.urn }, 
				{ column: 'description', value: response.data.description }, 
				{ column: 'acsUrl', value: response.data.acsUrl }, 
				{ column: 'id', value: response.data.id }
			]);
			
			if (applicationSlide.isLoaded()) {
				applicationSlide.clear();
				applicationSlide.hide();
			}
		}
	}).send();
}

function deleteApplication(data) {
	new Request({
		method: "DELETE", 
		url: "./api/v1/application", 
		successCode: 204,
		data: data,
		onSuccess: function(response) {
			appsTable.removeLine(data.id);
		}
	}).send();
}

function loadApplication(urn) {
	new Request({
		method: "GET", 
		url: "./api/v1/application/" + urn, 
		successCode: 200, 
		onSuccess: function(response) {
			var accesses = [];
			for (var i = 0; i < response.data.accesses.length; i++) {
				accesses.push([
					{ column: "email", value: response.data.accesses[i].email }, 
					{ column: "role", value: response.data.accesses[i].role }, 
					{ column: "enabled", value: response.data.accesses[i].enabled }, 
					{ column: "id", value: response.data.accesses[i].id }
				]);
			}
			
			appUsersTable.setData(accesses);
			
			var claims = [];
			for (var i = 0; i < response.data.claims.length; i++) {
				claims.push([
					{ column: "uri", value: response.data.claims[i].uri }, 
					{ column: "name", value: response.data.claims[i].name }, 
					{ column: "description", value: response.data.claims[i].description }, 
					{ column: "id", value: response.data.claims[i].id }
				]);
			}
			
			appClaimsTable.setData(claims);
		}
	}).send();
}

});