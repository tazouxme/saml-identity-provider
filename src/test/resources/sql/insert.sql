-- Organization
insert into tz_organization
(organization_id, external_id, domain, code, name, description, enabled, public_key, creation_date)
values
(1, 'ORG_test', 'test.com', 'TEST', 'Test', 'Test Organization', 1, 'xxx', 1627398003);

-- User
insert into tz_user
(user_id, external_id, username, email, password, enabled, creation_date, organization_id)
values
(1, 'USE_user1', 'user1', 'user1@test.com', '$2a$06$2LBQG1NSsF66DBkEFc5Csuw1UyXQWU2j7Wv1AGXfWc9FQmk1Gd322', 1, 1627398003, 1); -- password= "pass"

-- Application
insert into tz_application
(application_id, external_id, urn, name, description, assertion_url)
values
(1, 'APP_test1', 'urn:com:tazouxme:test1', 'Test 1', 'Test 1 Application', 'http://localhost/test1/acs'),
(2, 'APP_test2', 'urn:com:tazouxme:test2', 'Test 2', 'Test 2 Application', 'http://localhost/test2/acs');

-- Access
insert into tz_access
(access_id, external_id, urn, access_type, access_key, role, enabled, creation_date)
values
(1, 'ACC_org', 'urn:com:tazouxme:test1', 'ORG', 'ORG_test', 'ORGANIZATION', 1, 1627398003),
(2, 'ACC_user', 'urn:com:tazouxme:test1', 'USER', 'USE_user1', 'USER', 1, 1627398003),
(3, 'ACC_org', 'urn:com:tazouxme:test2', 'ORG', 'ORG_test', 'ORGANIZATION', 1, 1627398003),
(4, 'ACC_user', 'urn:com:tazouxme:test2', 'USER', 'USE_user1', 'USER', 1, 1627398003);