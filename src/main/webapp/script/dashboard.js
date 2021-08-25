document.addEventListener('I18nContentLoaded', function() {

var updateOrgBtn = new Button({ id: 'update-org-btn', text: 'organization.ribbon.data.btn.edit', icon: './lib/img/edit.png', translatable: true });
updateOrgBtn.setAction(function() {
	organizationSlide.setTitle("organization.slide.titles.update");
	organizationSlide.setValues({
		id: orgIdField.getValue(),
		code: orgCodeField.getValue(),
		domain: orgDomainField.getValue(),
		name: orgNameField.getValue(),
		creationDate: orgCreationDateField.getValue(),
		description: orgDescriptionField.getValue(),
		federation: orgFederationField.getValue()
	});
	organizationSlide.show({ show : ["slide-org-save-btn", "slide-org-cancel-btn"], hide : [] });
});

var saveOrgBtn = new Button({ id: 'slide-org-save-btn', text: 'organization.slide.btn.save', icon: './lib/img/edit.png', translatable: true });
saveOrgBtn.setAction(function() {
	if (organizationSlide.validate()) {
		updateOrganization(organizationSlide.toObject());
	}
});
var cancelOrgBtn = new Button({ id: 'slide-org-cancel-btn', text: 'organization.slide.btn.cancel', icon: './lib/img/cancel.png', translatable: true });
cancelOrgBtn.setAction(function() {
	if (organizationSlide.isLoaded()) {
		organizationSlide.clear();
		organizationSlide.hide();
	}
});

var claimsBtn = new Button({ id: 'claims-btn', text: 'organization.ribbon.globals.btn.claims', icon: './lib/img/claims.png', translatable: true });
var rolesBtn = new Button({ id: 'roles-btn', text: 'organization.ribbon.globals.btn.roles', icon: './lib/img/roles.png', translatable: true });
var addKeyBtn = new Button({ id: 'key-btn', text: 'organization.ribbon.certificate.btn.add', icon: './lib/img/add_key.png', translatable: true });
addKeyBtn.setAction(function() {
	uploader.show();
});
var removeKeyBtn = new Button({ id: 'key-btn', text: 'organization.ribbon.certificate.btn.delete', icon: './lib/img/remove_key.png', translatable: true });
removeKeyBtn.setAction(function() {
	var message = new Message({ id : 'remove-certificate-message', type : 'question', title : 'organization.messages.certificate.remove.title', translatable: true });
	message.setText("organization.messages.certificate.remove.text");
	message.onValidate(function() {
		deleteCertificate(orgIdField.getValue());
	});
	
	message.show();
});

var orgIdField = new Field({ id: "org-id-field", title: "organization.info.id", name: "org-id", type: "text", icon: "", maxLength: "16", width: "250", translatable: true });
orgIdField.setEnabled(false);
orgIdField.set(document.getElementById("org-main"));

var orgCodeField = new Field({ id: "org-code-field", title: "organization.info.code", name: "org-code", type: "text", icon: "", maxLength: "16", width: "250", translatable: true });
orgCodeField.setEnabled(false);
orgCodeField.set(document.getElementById("org-main"));

var orgNameField = new Field({ id: "org-name-field", title: "organization.info.name", name: "org-name", type: "text", icon: "", maxLength: "50", width: "250", translatable: true });
orgNameField.setEnabled(false);
orgNameField.set(document.getElementById("org-main"));

var orgDomainField = new Field({ id: "org-domain-field", title: "organization.info.domain", name: "org-domain", type: "text", icon: "", maxLength: "128", width: "250", translatable: true });
orgDomainField.setEnabled(false);
orgDomainField.set(document.getElementById("org-main"));

var orgDescriptionField = new Field({ id: "org-description-field", title: "organization.info.description", name: "org-description", type: "text", icon: "", maxLength: "200", width: "500", translatable: true });
orgDescriptionField.setEnabled(false);
orgDescriptionField.set(document.getElementById("org-main"));

var orgCreationDateField = new Field({ id: "org-date-field", title: "organization.info.date", name: "org-date", type: "date", icon: "", maxLength: "16", width: "250", translatable: true });
orgCreationDateField.setEnabled(false);
orgCreationDateField.set(document.getElementById("org-main"));

var orgCertificateField = new Checkbox({ id: "org-certificate-field", title: "organization.info.certificate", translatable: true });
orgCertificateField.setEnabled(false);
orgCertificateField.set(document.getElementById("org-main"));

var orgFederationField = new Checkbox({ id: "org-federation-field", title: "organization.info.federation", translatable: true });
orgFederationField.setEnabled(false);
orgFederationField.set(document.getElementById("org-main"));

var addUserBtn = new Button({ id: 'add-user-btn', text: 'users.ribbon.data.btn.add', icon: './lib/img/add_user.png', translatable: true });
addUserBtn.setAction(function() {
	userSlide.setTitle("users.slide.titles.create");
	userSlide.setEnabled('slide-user-username-field', true);
	userSlide.show({ show : ["slide-user-save-btn", "slide-user-cancel-btn"], hide : ["slide-user-update-btn"] });
});

var updateUserBtn = new Button({ id: 'update-user-btn', text: 'users.ribbon.data.btn.edit', icon: './lib/img/edit_user.png', translatable: true });
updateUserBtn.setEnabled(false);
updateUserBtn.setAction(function() {
	userSlide.setTitle("users.slide.titles.update");
	userSlide.setValues(usersTable.getSelectedData());
	userSlide.setEnabled('slide-user-username-field', false);
	userSlide.show({ show : ["slide-user-update-btn", "slide-user-cancel-btn"], hide : ["slide-user-save-btn"] });
});

var removeUserBtn = new Button({ id: 'remove-user-btn', text: 'users.ribbon.data.btn.delete', icon: './lib/img/delete_user.png', translatable: true });
removeUserBtn.setEnabled(false);
removeUserBtn.setAction(function() {
	var message = new Message({ id : 'remove-user-message', type : 'question', title : 'users.messages.data.delete.title', translatable: true });
	message.setText("users.messages.data.delete.text|" + usersTable.getSelectedData().username);
	message.onValidate(function() {
		var user = usersTable.getSelectedData();
		deleteUser(user);
	});
	
	message.show();
});

var saveUserBtn = new Button({ id: 'slide-user-save-btn', text: 'users.slide.btn.save', icon: './lib/img/add.png', translatable: true });
saveUserBtn.setAction(function() {
	if (userSlide.validate()) {
		createUser(userSlide.toObject());
	}
});
var modifyUserBtn = new Button({ id: 'slide-user-update-btn', text: 'users.slide.btn.save', icon: './lib/img/edit.png', translatable: true });
modifyUserBtn.setAction(function() {
	if (userSlide.validate()) {
		updateUser(userSlide.toObject());
	}
});
var cancelUserBtn = new Button({ id: 'slide-user-cancel-btn', text: 'users.slide.btn.cancel', icon: './lib/img/cancel.png', translatable: true });
cancelUserBtn.setAction(function() {
	if (userSlide.isLoaded()) {
		userSlide.clear();
		userSlide.hide();
	}
});

var activateUserBtn = new Button({ id: 'activate-user-btn', text: 'users.ribbon.actions.btn.activate', icon: './lib/img/unlock.png', translatable: true });
activateUserBtn.setEnabled(false);
activateUserBtn.setAction(function() {
	var message = new Message({ id: 'lock-user-message', type: 'question', title: 'users.messages.actions.activate.title', translatable: true });
	message.setText("users.messages.actions.activate.text|" + usersTable.getSelectedData().username);
	message.onValidate(function() {
		var user = usersTable.getSelectedData();
		updateUser({
			id: user.id,
			username: user.username,
			administrator: user.administrator,
			enabled: !user.enabled
		});
	});
	
	message.show();
});

var deactivateUserBtn = new Button({ id: 'deactivate-user-btn', text: 'users.ribbon.actions.btn.deactivate', icon: './lib/img/lock.png', translatable: true });
deactivateUserBtn.setEnabled(false);
deactivateUserBtn.setAction(function() {
	var message = new Message({ id: 'lock-user-message', type: 'question', title: 'users.messages.actions.deactivate.title', translatable: true });
	message.setText("users.messages.actions.deactivate.text|" + usersTable.getSelectedData().username);
	message.onValidate(function() {
		var user = usersTable.getSelectedData();
		updateUser({
			id: user.id,
			username: user.username,
			administrator: user.administrator,
			enabled: !user.enabled
		});
	});
	
	message.show();
});

var settingsUserBtn = new Button({ id: 'settings-user-btn', text: 'users.ribbon.globals.btn.data', icon: './lib/img/claims_user.png', translatable: true });
settingsUserBtn.setEnabled(false);

var addAppBtn = new Button({ id: 'add-app-btn', text: 'applications.ribbon.data.btn.add', icon: './lib/img/add_app.png', translatable: true });
addAppBtn.setAction(function() {
	applicationSlide.setTitle("applications.slide.titles.create");
	applicationSlide.show({ show : ["slide-app-save-btn", "slide-app-cancel-btn"], hide : ["slide-app-update-btn"] });
});

var updateAppBtn = new Button({ id: 'update-app-btn', text: 'applications.ribbon.data.btn.edit', icon: './lib/img/edit_app.png', translatable: true });
updateAppBtn.setEnabled(false);
updateAppBtn.setAction(function() {
	applicationSlide.setValues(appsTable.getSelectedData());
	applicationSlide.setTitle("applications.slide.titles.update");
	applicationSlide.show({ show : ["slide-app-update-btn", "slide-app-cancel-btn"], hide : ["slide-app-save-btn"] });
});

var removeAppBtn = new Button({ id: 'remove-app-btn', text: 'applications.ribbon.data.btn.delete', icon: './lib/img/delete_app.png', translatable: true });
removeAppBtn.setEnabled(false);
removeAppBtn.setAction(function() {
	var message = new Message({ id: 'remove-app-message', type: 'question', title: 'Delete Application', translatable: true });
	message.setText("Delete the Application " + appsTable.getSelectedData().name + " ("+ appsTable.getSelectedData().urn +") ?");
	message.onValidate(function() {
		var app = appsTable.getSelectedData();
		deleteApplication(app);
	});
	
	message.show();
});

var saveAppBtn = new Button({ id: 'slide-app-save-btn', text: 'applications.slide.btn.save', icon: './lib/img/add.png', translatable: true });
saveAppBtn.setAction(function() {
	if (applicationSlide.validate()) {
		createApplication(applicationSlide.toObject());
	}
});
var modifyAppBtn = new Button({ id: 'slide-app-update-btn', text: 'applications.slide.btn.save', icon: './lib/img/edit.png', translatable: true });
modifyAppBtn.setAction(function() {
	if (applicationSlide.validate()) {
		updateApplication(applicationSlide.toObject());
	}
});
var cancelAppBtn = new Button({ id: 'slide-app-cancel-btn', text: 'applications.slide.btn.cancel', icon: './lib/img/cancel.png', translatable: true });
cancelAppBtn.setAction(function() {
	if (applicationSlide.isLoaded()) {
		applicationSlide.clear();
		applicationSlide.hide();
	}
});

var accessAppBtn = new Button({ id: 'access-app-btn', text: 'applications.ribbon.access.btn.add', icon: './lib/img/access_app.png', translatable: true });
accessAppBtn.setEnabled(false);
accessAppBtn.setAction(function() {
	var users = [];
	for (var i = 0; i < usersTable.getDisplayedData().length; i++) {
		if (!appUsersTable.containsAt(0, usersTable.getDisplayedData()[i].email)) {
			users.push({
				id: usersTable.getDisplayedData()[i].id,
				value: usersTable.getDisplayedData()[i].email,
				translate: false
			});
		}
	}
	
	var roles = [];
	for (var i = 0; i < rolesTable.getDisplayedData().length; i++) {
		roles.push({
			id: rolesTable.getDisplayedData()[i].id,
			value: rolesTable.getDisplayedData()[i].name,
			translate: false
		});
	}
	
	accessSlide.setTitle("applications.slide.titles.access");
	accessSlide.setValues({
		urn: appsTable.getSelectedData().urn,
		user: users,
		role: roles
	});
	accessSlide.show({ show : ["slide-access-save-btn", "slide-access-cancel-btn"], hide : [] });
});

var lockUserAppBtn = new Button({ id: 'lock-user-app-btn', text: 'applications.ribbon.access.btn.deactivate', icon: './lib/img/lock_app.png', translatable: true });
lockUserAppBtn.setEnabled(false);
lockUserAppBtn.setAction(function() {
	var message = new Message({ id: 'lock-user-app-message', type: 'question', title: 'applications.messages.access.deactivate.title', translatable: true });
	message.setText("applications.messages.access.deactivate.text|" + appUsersTable.getSelectedData().email);
	message.onValidate(function() {
		var access = appUsersTable.getSelectedData();
		updateAccess({
			id: access.id,
			enabled: !access.enabled
		});
	});
	
	message.show();
});

var unlockUserAppBtn = new Button({ id: 'unlock-user-app-btn', text: 'applications.ribbon.access.btn.activate', icon: './lib/img/unlock_app.png', translatable: true });
unlockUserAppBtn.setEnabled(false);
unlockUserAppBtn.setAction(function() {
	var message = new Message({ id: 'unlock-user-app-message', type: 'question', title: 'applications.messages.access.activate.title', translatable: true });
	message.setText("applications.messages.access.activate.text|" + appUsersTable.getSelectedData().email);
	message.onValidate(function() {
		var access = appUsersTable.getSelectedData();
		updateAccess({
			id: access.id,
			enabled: !access.enabled
		});
	});
	
	message.show();
});

var revokeUserAppBtn = new Button({ id: 'revoke-app-btn', text: 'applications.ribbon.access.btn.revoke', icon: './lib/img/revoke_app.png', translatable: true });
revokeUserAppBtn.setEnabled(false);
revokeUserAppBtn.setAction(function() {
	var message = new Message({ id: 'revoke-user-app-message', type: 'question', title: 'applications.messages.access.revoke.title', translatable: true });
	message.setText("applications.messages.access.revoke.text|" + appUsersTable.getSelectedData().email);
	message.onValidate(function() {
		deleteAccess({ id: appUsersTable.getSelectedData().id });
	});
	
	message.show();
});

var appClaimsBtn = new Button({ id: 'app-claims-btn', text: 'applications.ribbon.claims.btn.claims', icon: './lib/img/claims.png', translatable: true });
appClaimsBtn.setEnabled(false);
appClaimsBtn.setAction(function() {
	var appClaimsSlide = new Slide({ id: 'app-claims-slide', title: 'applications.slide.titles.claims', size: 500, removeOnClick: true, translatable: true });
	var top = 50;
	
	for (var i = 0; i < claimsTable.getDisplayedData().length; i++) {
		var claim = claimsTable.getDisplayedData()[i];
		var box = new Checkbox({ id: "slide-app-claims-" + claim.name.toLowerCase() + "-field", title: claim.uri });
		box.setValue(appClaimsTable.containsAt(1, claim.name));
		
		box.get().style.top = top + "px";
		box.get().style.left = 20 + "px";
		
		appClaimsSlide.addComponent({ mapTo: claim.name.toLowerCase(), component: box });
		top += 50;
	}
	
	var saveAppClaimsBtn = new Button({ id: 'slide-access-save-btn', text: 'applications.slide.btn.save', icon: './lib/img/edit.png', translatable: true });
	saveAppClaimsBtn.get().style.top = (top + 10) + "px";
	saveAppClaimsBtn.setAction(function() {
		var selectedClaims = appClaimsSlide.toObject();
		var data = [];
		
		for (var i in selectedClaims) {
			if (selectedClaims[i]) {
				data.push({ name: i });
			}
		}
		
		new Request({
			method: "PATCH", 
			url: "./services/services/api/v1/application/" + appsTable.getSelectedData().id + "/claims", 
			successCode: 202,
			data: data,
			onSuccess: function(response) {
				var claims = [];
				for (var i = 0; i < response.data.length; i++) {
					claims.push([
						{ column: "uri", value: response.data[i].uri }, 
						{ column: "name", value: response.data[i].name }, 
						{ column: "description", value: response.data[i].description }, 
						{ column: "id", value: response.data[i].id }
					]);
				}
				
				appClaimsTable.setData(claims);
				appClaimsSlide.remove();
			},
			onError: function(response) {
				var message = new Message({ id : 'update-app-error-message', type : 'error', title : 'applications.errors.claims.title', translatable: true });
				message.setText("applications.errors.claims.text|" + response.data.message);
				message.show();
			}
		}).send();
	});
	
	var cancelAppClaimsBtn = new Button({ id: 'slide-access-cancel-btn', text: 'applications.slide.btn.cancel', icon: './lib/img/cancel.png', translatable: true });
	cancelAppClaimsBtn.get().style.top = (top + 10) + "px";
	cancelAppClaimsBtn.setAction(function() {
		appClaimsSlide.remove();
	});
	
	appClaimsSlide.addComponent({ mapTo: '', component: saveAppClaimsBtn });
	appClaimsSlide.addComponent({ mapTo: '', component: cancelAppClaimsBtn });
	
	appClaimsSlide.set(document.body);
	appClaimsSlide.show();
});

var saveAccessBtn = new Button({ id: 'slide-access-save-btn', text: 'applications.slide.btn.save', icon: './lib/img/add.png', translatable: true });
saveAccessBtn.setAction(function() {
	if (accessSlide.validate()) {
		var obj = accessSlide.toObject();
		var data = {
			user : { id: obj.user.id },
			role: { id: obj.role.id },
			application: { urn: obj.urn }
		}
		
		createAccess(data);
	}
});
var cancelAccessBtn = new Button({ id: 'slide-access-cancel-btn', text: 'applications.slide.btn.cancel', icon: './lib/img/cancel.png', translatable: true });
cancelAccessBtn.setAction(function() {
	if (accessSlide.isLoaded()) {
		accessSlide.clear();
		accessSlide.hide();
	}
});

var ribbon = new Ribbon({ id: 'dashboard-ribbon', translatable: true });
ribbon.addMenu('dashboard-ribbon-organization', 'organization.ribbon.name', function() {
	document.getElementById("org").style.display = 'block';
	document.getElementById("users").style.display = 'none';
	document.getElementById("apps").style.display = 'none';
});
ribbon.addMenu('dashboard-ribbon-user', 'users.ribbon.name', function() {
	document.getElementById("org").style.display = 'none';
	document.getElementById("users").style.display = 'block';
	document.getElementById("apps").style.display = 'none';
});
ribbon.addMenu('dashboard-ribbon-application', 'applications.ribbon.name', function() {
	document.getElementById("org").style.display = 'none';
	document.getElementById("users").style.display = 'none';
	document.getElementById("apps").style.display = 'block';
});

ribbon.getMenu('dashboard-ribbon-organization').addSection('organization.ribbon.data.name', [ updateOrgBtn ]);
ribbon.getMenu('dashboard-ribbon-organization').addSection('organization.ribbon.certificate.name', [ addKeyBtn, removeKeyBtn ]);
ribbon.getMenu('dashboard-ribbon-organization').addSection('organization.ribbon.globals.name', [ claimsBtn, rolesBtn ]);
ribbon.getMenu('dashboard-ribbon-user').addSection('users.ribbon.data.name', [ addUserBtn, updateUserBtn, removeUserBtn ]);
ribbon.getMenu('dashboard-ribbon-user').addSection('users.ribbon.actions.name', [ activateUserBtn, deactivateUserBtn ]);
ribbon.getMenu('dashboard-ribbon-user').addSection('users.ribbon.globals.name', [ settingsUserBtn ]);
ribbon.getMenu('dashboard-ribbon-application').addSection('applications.ribbon.data.name', [ addAppBtn, updateAppBtn, removeAppBtn ]);
ribbon.getMenu('dashboard-ribbon-application').addSection('applications.ribbon.claims.name', [ appClaimsBtn ]);
ribbon.getMenu('dashboard-ribbon-application').addSection('applications.ribbon.access.name', [ accessAppBtn, unlockUserAppBtn, lockUserAppBtn, revokeUserAppBtn ]);

ribbon.set(document.body);
ribbon.setSelected('dashboard-ribbon-organization');

var uploader = new Uploader({ id: 'org-certificate-uploader', accept: 'application/x-x509-ca-cert', url: './services/api/v1/certificate', translatable: true });
uploader.setTransform(function(file) {
	var org = {};
	org['id'] = orgIdField.getValue();
	org['certificate'] = file.data;
	
	return org;
});
uploader.onUploadEnd(function(uploaded) {
	orgCertificateField.setValue(uploaded[0].hasCertificate);
			
	addKeyBtn.setEnabled(!uploaded[0].hasCertificate);
	removeKeyBtn.setEnabled(uploaded[0].hasCertificate);
});
uploader.set(document.body);

var organizationSlide = new Slide({ id: 'organization-slide', title: 'organization.slide.title.default', size: 500, translatable: true });
organizationSlide.addComponent({ mapTo: 'id', component: new Field({ id: "slide-org-id-field", title: "organization.slide.id", type: "text", icon: "", maxLength: "32", width: "250", enabled: false, mandatory: true, translatable: true }) });
organizationSlide.addComponent({ mapTo: 'code', component: new Field({ id: "slide-org-code-field", title: "organization.slide.code", type: "text", icon: "", maxLength: "16", width: "250", enabled: false, mandatory: true, translatable: true }) });
organizationSlide.addComponent({ mapTo: 'domain', component: new Field({ id: "slide-org-domain-field", title: "organization.slide.domain", type: "text", icon: "", maxLength: "128", width: "250", enabled: false, mandatory: true, translatable: true }) });
organizationSlide.addComponent({ mapTo: 'name', component: new Field({ id: "slide-org-name-field", title: "organization.slide.name", type: "text", icon: "", maxLength: "50", width: "250", enabled: true, mandatory: true, translatable: true }) });
organizationSlide.addComponent({ mapTo: 'creationDate', component: new Field({ id: "slide-org-date-field", title: "organization.slide.date", type: "date", icon: "", maxLength: "200", width: "250", enabled: false, mandatory: true, translatable: true }) });
organizationSlide.addComponent({ mapTo: 'description', component: new Field({ id: "slide-org-description-field", title: "organization.slide.description", type: "text", icon: "", maxLength: "32", width: "450", enabled: true, translatable: true }) });
organizationSlide.addComponent({ mapTo: 'federation', component: new Checkbox({ id: "slide-org-federation-field", title: "organization.slide.federation", translatable: true }) });
organizationSlide.addComponent({ mapTo: '', component: saveOrgBtn });
organizationSlide.addComponent({ mapTo: '', component: cancelOrgBtn });
organizationSlide.set(document.body);

var userSlide = new Slide({ id: 'user-slide', title: 'users.slide.titles.default', size: 500, translatable: true });
userSlide.addComponent({ mapTo: 'id', component: new Field({ id: "slide-user-id-field", title: "users.slide.id", type: "text", icon: "", maxLength: "32", width: "250", enabled: false, mandatory: true, translatable: true }) });
userSlide.addComponent({ mapTo: 'username', component: new Field({ id: "slide-user-username-field", title: "users.slide.username", type: "text", icon: "", maxLength: "32", width: "250", mandatory: true, translatable: true }) });
userSlide.addComponent({ mapTo: 'administrator', component: new Checkbox({ id: "slide-user-admin-field", title: "users.slide.admin", translatable: true }) });
userSlide.addComponent({ mapTo: '', component: saveUserBtn });
userSlide.addComponent({ mapTo: '', component: modifyUserBtn });
userSlide.addComponent({ mapTo: '', component: cancelUserBtn });
userSlide.set(document.body);

var applicationSlide = new Slide({ id: 'app-slide', title: 'applications.slide.titles.default', size: 500, translatable: true });
applicationSlide.addComponent({ mapTo: 'id', component: new Field({ id: "slide-app-id-field", title: "applications.slide.id", type: "text", icon: "", maxLength: "32", width: "250", enabled: false, mandatory: true, translatable: true }) });
applicationSlide.addComponent({ mapTo: 'urn', component: new Field({ id: "slide-app-urn-field", title: "applications.slide.urn", type: "text", icon: "", maxLength: "32", width: "250", mandatory: true, mandatory: true, translatable: true }) });
applicationSlide.addComponent({ mapTo: 'name', component: new Field({ id: "slide-app-name-field", title: "applications.slide.name", type: "text", icon: "", maxLength: "32", width: "250", mandatory: true, mandatory: true, translatable: true }) });
applicationSlide.addComponent({ mapTo: 'description', component: new Field({ id: "slide-app-description-field", title: "applications.slide.description", type: "text", icon: "", maxLength: "32", width: "450", translatable: true }) });
applicationSlide.addComponent({ mapTo: 'acsUrl', component: new Field({ id: "slide-app-acs-field", title: "applications.slide.acs", type: "text", icon: "", maxLength: "32", width: "450", mandatory: true, mandatory: true, translatable: true }) });
applicationSlide.addComponent({ mapTo: 'logoutUrl', component: new Field({ id: "slide-app-logout-field", title: "applications.slide.logout", type: "text", icon: "", maxLength: "32", width: "450", mandatory: true, mandatory: true, translatable: true }) });
applicationSlide.addComponent({ mapTo: '', component: saveAppBtn });
applicationSlide.addComponent({ mapTo: '', component: modifyAppBtn });
applicationSlide.addComponent({ mapTo: '', component: cancelAppBtn });
applicationSlide.set(document.body);

var accessSlide = new Slide({ id: 'access-slide', title: 'Access', size: 500, translatable: true });
accessSlide.addComponent({ mapTo: 'id', component: new Field({ id: "slide-access-id-field", title: "applications.slide.id", type: "text", icon: "", maxLength: "32", width: "250", enabled: false, mandatory: true, translatable: true }) });
accessSlide.addComponent({ mapTo: 'urn', component: new Field({ id: "slide-access-urn-field", title: "applications.slide.urn", type: "text", icon: "", maxLength: "32", width: "250", mandatory: true, enabled: false, translatable: true }) });
accessSlide.addComponent({ mapTo: 'user', component: new Select({ id: "slide-access-user-field", defaultValue: "applications.slide.defaults.user", translatable: true }) });
accessSlide.addComponent({ mapTo: 'role', component: new Select({ id: "slide-access-role-field", defaultValue: "applications.slide.defaults.role", translatable: true }) });
accessSlide.addComponent({ mapTo: '', component: saveAccessBtn });
accessSlide.addComponent({ mapTo: '', component: cancelAccessBtn });
accessSlide.set(document.body);

var claimsTable = new Table({
	id : 'org-claims-table', 
	columnPk : 3, 
	columns : ["organization.claims.table.columns.uri", "organization.claims.table.columns.name", "organization.claims.table.columns.description"], 
	mimeTypes : [{ code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }], 
	sizes : [25, 25, 25], 
	translatable: true
});
claimsTable.setSort(function(a, b) {
	return a[0].value.localeCompare(b[0].value);
});
claimsTable.set(document.getElementById("org-claims"));

var rolesTable = new Table({
	id : 'org-roles-table', 
	columnPk : 2, 
	columns : ["organization.roles.table.columns.uri", "organization.roles.table.columns.name"], 
	mimeTypes : [{ code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }], 
	sizes : [25, 25], 
	translatable: true
});
rolesTable.setSort(function(a, b) {
	return a[0].value.localeCompare(b[0].value);
});
rolesTable.set(document.getElementById("org-roles"));

var usersTable = new Table({
	id : 'users-table', 
	columnPk : 4, 
	columns : ["users.registered.table.columns.username", "users.registered.table.columns.email", "users.registered.table.columns.enabled", "users.registered.table.columns.admin"], 
	mimeTypes : [{ code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }, { code: "BOOLEAN", name: "Boolean" }, { code: "BOOLEAN", name: "Boolean" }], 
	sizes : [25, 25, 25, 25], 
	translatable: true
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
	columnPk : 0,
	columns : ["users.claims.table.columns.uri", "users.claims.table.columns.name", "users.claims.table.columns.value", "users.claims.table.columns.date"], 
	mimeTypes : [{ code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }, { code: "DATE", name: "Date" }], 
	sizes : [25, 25, 25, 25], 
	translatable: true
});
userClaimsTable.setSort(function(a, b) {
	return a[0].value.localeCompare(b[0].value);
});
userClaimsTable.set(document.getElementById("user-claims"));

var appsTable = new Table({
	id : 'apps-table', 
	columnPk : 5, 
	columns : ["applications.registered.table.columns.name", "applications.registered.table.columns.urn", "applications.registered.table.columns.description", "applications.registered.table.columns.acs", "applications.registered.table.columns.logout"], 
	mimeTypes : [{ code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }], 
	sizes : [20, 20, 20, 20, 20], 
	translatable: true
});
appsTable.setSelectionListener({
	onSelect : function(e, pk) {
		updateAppBtn.setEnabled(true);
		removeAppBtn.setEnabled(true);
		accessAppBtn.setEnabled(true);
		
		unlockUserAppBtn.setEnabled(false);
		lockUserAppBtn.setEnabled(false);
		revokeUserAppBtn.setEnabled(false);
		appClaimsBtn.setEnabled(true);

		loadApplication(appsTable.getSelectedData().urn);
	},
	onUnselect : function(pk) {
		updateAppBtn.setEnabled(false);
		removeAppBtn.setEnabled(false);
		accessAppBtn.setEnabled(false);
		
		unlockUserAppBtn.setEnabled(false);
		lockUserAppBtn.setEnabled(false);
		revokeUserAppBtn.setEnabled(false);
		appClaimsBtn.setEnabled(false);
		
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
	columnPk : 5, 
	columns : ["applications.access.table.columns.email", "applications.access.table.columns.role", "applications.access.table.columns.enabled", "applications.access.table.columns.federation", "applications.access.table.columns.federationId"], 
	mimeTypes : [{ code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }, { code: "BOOLEAN", name: "Boolean" }, { code: "BOOLEAN", name: "Boolean" }, { code: "TEXT", name: "Text" }], 
	sizes : [20, 20, 20, 20, 20], 
	translatable: true
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
	columns : ["applications.claims.table.columns.uri", "applications.claims.table.columns.name", "applications.claims.table.columns.description"], 
	mimeTypes : [{ code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }, { code: "TEXT", name: "Text" }], 
	sizes : [25, 25, 25], 
	translatable: true
});
appClaimsTable.setSort(function(a, b) {
	return a[0].value.localeCompare(b[0].value);
});
appClaimsTable.set(document.getElementById("app-claims"));
	
loadOrganization();
loadUsers();
loadApplications();

// ---

function loadOrganization() {
	new Request({
		method: "GET", 
		url: "./services/api/v1/organization", 
		successCode: 200, 
		onSuccess: function(response) {
			orgIdField.setValue(response.data.id);
			orgCodeField.setValue(response.data.code);
			orgDomainField.setValue(response.data.domain);
			orgNameField.setValue(response.data.name);
			orgDescriptionField.setValue(response.data.description);
			orgCreationDateField.setValue(response.data.creationDate);
			orgCertificateField.setValue(response.data.hasCertificate);
			orgFederationField.setValue(response.data.federation);
			
			addKeyBtn.setEnabled(!response.data.hasCertificate);
			removeKeyBtn.setEnabled(response.data.hasCertificate);
			
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

function updateOrganization(data) {
	new Request({
		method: "PATCH",
		url: "./services/api/v1/organization",
		successCode: 202,
		data: data,
		onSuccess: function(response) {
			orgIdField.setValue(response.data.id);
			orgCodeField.setValue(response.data.code);
			orgDomainField.setValue(response.data.domain);
			orgNameField.setValue(response.data.name);
			orgDescriptionField.setValue(response.data.description);
			orgCreationDateField.setValue(response.data.creationDate);
			
			if (organizationSlide.isLoaded()) {
				organizationSlide.clear();
				organizationSlide.hide();
			}
		},
		onError: function(response) {
			var message = new Message({ id : 'update-org-error-message', type : 'error', title : 'organization.messages.errors.update.title' });
			message.setText("organization.messages.errors.update.text|" + response.data.message);
			message.show();
		}
	}).send();
}

function deleteCertificate(organizationId) {
	new Request({
		method: "DELETE", 
		url: "./services/api/v1/certificate", 
		successCode: 204,
		data: { id: organizationId },
		onSuccess: function() {
			orgCertificateField.setValue(false);
			addKeyBtn.setEnabled(true);
			removeKeyBtn.setEnabled(false);
		},
		onError: function(response) {
			var message = new Message({ id : 'delete-certificate-error-message', type : 'error', title : 'organization.messages.errors.delete.title' });
			message.setText("organization.messages.errors.delete.text|" + response.data.message);
			message.show();
		}
	}).send();
}

function loadUsers() {
	new Request({
		method: "GET", 
		url: "./services/api/v1/users", 
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
		url: "./services/api/v1/user", 
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
			
			if (response.headers["x-activation-token"] != null) {
				var message = new Message({ id : 'user-created-message', type : 'info', title : 'User Created' });
				message.setText("User " + response.data.username + " succesfully created! <a href='" + response.headers["x-activation-token"] + "'>Activation link</a>");
				message.show();
			} else {
				var message = new Message({ id : 'user-created-message', type : 'info', title : 'User Created' });
				message.setText("User " + response.data.username + " succesfully created! An activation email has been sent.");
				message.show();
			}
			
			if (userSlide.isLoaded()) {
				userSlide.clear();
				userSlide.hide();
			}
		},
		onError: function(response) {
			var message = new Message({ id : 'create-user-error-message', type : 'error', title : 'users.messages.errors.create.title' });
			message.setText("users.messages.errors.create.text|" + response.data.message);
			message.show();
		}
	}).send();
}

function updateUser(data) {
	new Request({
		method: "PATCH", 
		url: "./services/api/v1/user", 
		successCode: 202,
		data: data,
		onSuccess: function(response) {
			usersTable.updateLine([
				{ column: 'username', value: response.data.username }, 
				{ column: 'email', value: response.data.email }, 
				{ column: 'enabled', value: response.data.enabled }, 
				{ column: 'administrator', value: response.data.administrator }, 
				{ column: 'id', value: response.data.id }
			]);
			
			if (userSlide.isLoaded()) {
				userSlide.clear();
				userSlide.hide();
			}
		},
		onError: function(response) {
			var message = new Message({ id : 'update-user-error-message', type : 'error', title : 'users.messages.errors.update.title' });
			message.setText("users.messages.errors.update.text|" + response.data.message);
			message.show();
		}
	}).send();
}

function deleteUser(data) {
	new Request({
		method: "DELETE", 
		url: "./services/api/v1/user", 
		successCode: 204,
		data: data,
		onSuccess: function(response) {
			usersTable.removeLine(data.id);
			
			updateUserBtn.setEnabled(false);
			removeUserBtn.setEnabled(false);
			activateUserBtn.setEnabled(false);
			deactivateUserBtn.setEnabled(false);
			
			userClaimsTable.clear();
		},
		onError: function(response) {
			var message = new Message({ id : 'delete-user-error-message', type : 'error', title : 'users.messages.errors.delete.title' });
			message.setText("users.messages.errors.delete.text|" + response.data.message);
			message.show();
		}
	}).send();
}

function loadUser(pk) {
	new Request({
		method: "GET", 
		url: "./services/api/v1/user/" + pk, 
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
		url: "./services/api/v1/applications", 
		successCode: 200, 
		onSuccess: function(response) {
			var apps = [];
			for (var i = 0; i < response.data.length; i++) {
				apps.push([
					{ column: "name", value: response.data[i].name }, 
					{ column: "urn", value: response.data[i].urn }, 
					{ column: "description", value: response.data[i].description }, 
					{ column: "acsUrl", value: response.data[i].acsUrl },
					{ column: 'logoutUrl', value: response.data[i].logoutUrl },
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
		url: "./services/api/v1/application", 
		successCode: 201,
		data: data,
		onSuccess: function(response) {
			appsTable.addLine([
				{ column: 'name', value: response.data.name }, 
				{ column: 'urn', value: response.data.urn }, 
				{ column: 'description', value: response.data.description }, 
				{ column: 'acsUrl', value: response.data.acsUrl },
				{ column: 'logoutUrl', value: response.data.logoutUrl },
				{ column: 'id', value: response.data.id }
			]);
			appsTable.setSelectedLine(response.data.id);
			
			if (applicationSlide.isLoaded()) {
				applicationSlide.clear();
				applicationSlide.hide();
			}
		},
		onError: function(response) {
			var message = new Message({ id : 'create-app-error-message', type : 'error', title : 'applications.messages.errors.create.title' });
			message.setText("applications.messages.errors.create.text|" + response.data.message);
			message.show();
		}
	}).send();
}

function updateApplication(data) {
	new Request({
		method: "PATCH", 
		url: "./services/api/v1/application", 
		successCode: 202,
		data: data,
		onSuccess: function(response) {
			appsTable.updateLine([
				{ column: 'name', value: response.data.name }, 
				{ column: 'urn', value: response.data.urn }, 
				{ column: 'description', value: response.data.description }, 
				{ column: 'acsUrl', value: response.data.acsUrl },
				{ column: 'logoutUrl', value: response.data.logoutUrl },
				{ column: 'id', value: response.data.id }
			]);
			
			if (applicationSlide.isLoaded()) {
				applicationSlide.clear();
				applicationSlide.hide();
			}
		},
		onError: function(response) {
			var message = new Message({ id : 'update-app-error-message', type : 'error', title : 'applications.messages.errors.update.title' });
			message.setText("applications.messages.errors.update.text|" + response.data.message);
			message.show();
		}
	}).send();
}

function deleteApplication(data) {
	new Request({
		method: "DELETE", 
		url: "./services/api/v1/application", 
		successCode: 204,
		data: data,
		onSuccess: function(response) {
			appsTable.removeLine(data.id);
			
			updateAppBtn.setEnabled(false);
			removeAppBtn.setEnabled(false);
			accessAppBtn.setEnabled(false);
			
			unlockUserAppBtn.setEnabled(false);
			lockUserAppBtn.setEnabled(false);
			revokeUserAppBtn.setEnabled(false);
			appClaimsBtn.setEnabled(false);
		
			appUsersTable.clear();
			appClaimsTable.clear();
		},
		onError: function(response) {
			var message = new Message({ id : 'delete-app-error-message', type : 'error', title : 'applications.messages.errors.delete.title' });
			message.setText("applications.messages.errors.delete.text|" + response.data.message);
			message.show();
		}
	}).send();
}

function loadApplication(urn) {
	new Request({
		method: "GET", 
		url: "./services/api/v1/application/" + urn, 
		successCode: 200, 
		onSuccess: function(response) {
			var accesses = [];
			for (var i = 0; i < response.data.accesses.length; i++) {
				accesses.push([
					{ column: "email", value: response.data.accesses[i].user.email }, 
					{ column: "role", value: response.data.accesses[i].role.name }, 
					{ column: "enabled", value: response.data.accesses[i].enabled }, 
					{ column: "federation", value: response.data.accesses[i].federation.enabled }, 
					{ column: "federationId", value: response.data.accesses[i].federation.id }, 
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

function createAccess(data) {
	new Request({
		method: "POST", 
		url: "./services/api/v1/access", 
		successCode: 201,
		data: data,
		onSuccess: function(response) {
			appUsersTable.addLine([
				{ column: "email", value: response.data.user.email }, 
				{ column: "role", value: response.data.role.name }, 
				{ column: "enabled", value: response.data.enabled }, 
				{ column: "federation", value: response.data.federation.enabled }, 
				{ column: "federationId", value: response.data.federation.id }, 
				{ column: "id", value: response.data.id }
			]);
			appUsersTable.setSelectedLine(response.data.id);
			
			if (accessSlide.isLoaded()) {
				accessSlide.clear();
				accessSlide.hide();
			}
		},
		onError: function(response) {
			var message = new Message({ id : 'create-access-error-message', type : 'error', title : 'applications.messages.errors.create.title' });
			message.setText("applications.messages.errors.create.text|" + response.data.message);
			message.show();
		}
	}).send();
}

function updateAccess(data) {
	new Request({
		method: "PATCH", 
		url: "./services/api/v1/access", 
		successCode: 202,
		data: data,
		onSuccess: function(response) {
			appUsersTable.updateLine([
				{ column: "email", value: response.data.user.email }, 
				{ column: "role", value: response.data.role.name }, 
				{ column: "enabled", value: response.data.enabled }, 
				{ column: "federation", value: response.data.federation.enabled }, 
				{ column: "federationId", value: response.data.federation.id }, 
				{ column: "id", value: response.data.id }
			]);
			appUsersTable.setSelectedLine(response.data.id);
			
			unlockUserAppBtn.setEnabled(!response.data.enabled);
			lockUserAppBtn.setEnabled(response.data.enabled);
			
			if (accessSlide.isLoaded()) {
				accessSlide.clear();
				accessSlide.hide();
			}
		},
		onError: function(response) {
			var message = new Message({ id : 'update-access-error-message', type : 'error', title : 'applications.messages.errors.update.title' });
			message.setText("applications.messages.errors.update.text|" + response.data.message);
			message.show();
		}
	}).send();
}

function deleteAccess(data) {
	new Request({
		method: "DELETE", 
		url: "./services/api/v1/access", 
		successCode: 204,
		data: data,
		onSuccess: function(response) {
			appUsersTable.removeLine(data.id);
			
			unlockUserAppBtn.setEnabled(false);
			lockUserAppBtn.setEnabled(false);
			revokeUserAppBtn.setEnabled(false);
		},
		onError: function(response) {
			var message = new Message({ id : 'delete-access-error-message', type : 'error', title : 'applications.messages.errors.delete.title' });
			message.setText("applications.messages.errors.delete.text|" + response.data.message);
			message.show();
		}
	}).send();
}

});