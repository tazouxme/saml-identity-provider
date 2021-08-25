-- Organization
ALTER SEQUENCE Organization_sequence RESTART WITH 1
insert into tz_organization
(organization_id, external_id, domain, code, name, description, enabled, federation, certificate, creation_date)
values
(NEXT VALUE FOR Organization_sequence, 'ORG_test', 'test.com', 'TEST', 'Test', 'Test Organization', 1, 1, 'xxx', 1627398003);

-- User
ALTER SEQUENCE User_sequence RESTART WITH 1
insert into tz_user
(user_id, external_id, username, email, password, enabled, administrator, creation_date, organization_id)
values
(NEXT VALUE FOR User_sequence, 'USE_user1', 'user1', 'user1@test.com', '$2a$06$2LBQG1NSsF66DBkEFc5Csuw1UyXQWU2j7Wv1AGXfWc9FQmk1Gd322', 1, 1, 1627398003, 1); -- password= "pass"

-- Application
ALTER SEQUENCE Application_sequence RESTART WITH 1
insert into tz_application
(application_id, external_id, urn, name, description, assertion_url, logout_url, organization_id)
values
(NEXT VALUE FOR Application_sequence, 'APP_test1', 'urn:com:tazouxme:test1', 'Test 1', 'Test 1 Application', 'http://localhost/test1/acs', 'http://localhost/test1/logout', 1),
(NEXT VALUE FOR Application_sequence, 'APP_test2', 'urn:com:tazouxme:test2', 'Test 2', 'Test 2 Application', 'http://localhost/test2/acs', 'http://localhost/test2/logout', 1);

-- Role
ALTER SEQUENCE Role_sequence RESTART WITH 1
insert into tz_role
(role_id, external_id, name, uri, organization_id)
values
(NEXT VALUE FOR Role_sequence, 'ROL_user', 'USER', 'http://schemas.tazouxme.com/identity/role/user', 1),
(NEXT VALUE FOR Role_sequence, 'ROL_admin', 'ADMIN', 'http://schemas.tazouxme.com/identity/role/admin', 1);

-- Claim
ALTER SEQUENCE Claim_sequence RESTART WITH 1
insert into tz_claim
(claim_id, description, external_id, name, uri, organization_id)
values
(NEXT VALUE FOR Claim_sequence, 'Organization', 'CLA_org', 'ORG', 'http://schemas.tazouxme.com/identity/claims/organization', 1),
(NEXT VALUE FOR Claim_sequence, 'E-Mail', 'CLA_mail', 'MAIL', 'http://schemas.tazouxme.com/identity/claims/email', 1),
(NEXT VALUE FOR Claim_sequence, 'Firstname', 'CLA_first', 'FIRSTNAME', 'http://schemas.tazouxme.com/identity/claims/firstname', 1),
(NEXT VALUE FOR Claim_sequence, 'Lastname', 'CLA_last', 'LASTNAME', 'http://schemas.tazouxme.com/identity/claims/lastname', 1),
(NEXT VALUE FOR Claim_sequence, 'Date of Birth', 'CLA_birth', 'BIRTHDATE', 'http://schemas.tazouxme.com/identity/claims/birthdate', 1),
(NEXT VALUE FOR Claim_sequence, 'Country', 'CLA_country', 'COUNTRY', 'http://schemas.tazouxme.com/identity/claims/country', 1),
(NEXT VALUE FOR Claim_sequence, 'City', 'CLA_city', 'CITY', 'http://schemas.tazouxme.com/identity/claims/city', 1);

-- Access
ALTER SEQUENCE Access_sequence RESTART WITH 1
insert into tz_access
(access_id, external_id, enabled, creation_date, organization_id, user_id, application_id, role_id)
values
(NEXT VALUE FOR Access_sequence, 'ACC_1', 1, 1627398003, 1, 1, 1, 1),
(NEXT VALUE FOR Access_sequence, 'ACC_2', 1, 1627398003, 1, 1, 2, 2);

-- Access
ALTER SEQUENCE Federation_sequence RESTART WITH 1
insert into tz_federation
(federation_id, external_id, enabled, creation_date, organization_id, user_id, application_id)
values
(NEXT VALUE FOR Federation_sequence, 'FED_1', 1, 1627398003, 1, 1, 1),
(NEXT VALUE FOR Federation_sequence, 'FED_2', 1, 1627398003, 1, 1, 2);

-- User Details
ALTER SEQUENCE UserDetails_sequence RESTART WITH 1
insert into tz_user_details
(user_details_id, claim_id, claim_value, creation_date, user_id)
values
(NEXT VALUE FOR UserDetails_sequence, 1, 'Test', 1627644307940, 1),
(NEXT VALUE FOR UserDetails_sequence, 2, 'user1@test.com', 1627644307940, 1),
(NEXT VALUE FOR UserDetails_sequence, 3, 'Joel', 1627644307940, 1),
(NEXT VALUE FOR UserDetails_sequence, 4, 'Tazzari', 1627644307940, 1);

-- Application Claims
insert into tz_application_claims
(application_id, claim_id)
values
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(2, 1),
(2, 2),
(2, 4);