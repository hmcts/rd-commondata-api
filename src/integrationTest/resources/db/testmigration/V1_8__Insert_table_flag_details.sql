INSERT INTO flag_details (id, flag_code, value_en, value_cy, category_id, mrd_created_time, mrd_updated_time, mrd_deleted_time)
 VALUES
    (83, 'CF0014', 'Welsh forms and communications', '', 1, '2022-06-17 13:33:00', '2022-06-17 13:33:00', NULL);


--Inserting values for testing
INSERT INTO flag_details (id, flag_code, value_en, value_cy, category_id, mrd_created_time, mrd_updated_time, mrd_deleted_time)
 VALUES
    (84,'PF0003','Potentially suicidal','',2, '2022-06-17 13:33:00', '2022-06-17 13:33:00', NULL);

insert into flag_service(id, service_id, hearing_relevant, request_reason, flag_code) values (1, 'XXXX', false, false, 'RA0004');
insert into flag_service(id, service_id, hearing_relevant, request_reason, flag_code) values (2, 'XXXX', false, false, 'RA0042');
insert into flag_service(id, service_id, hearing_relevant, request_reason, flag_code) values (3, 'XXXX', false, false, 'PF0015');
insert into flag_service(id, service_id, hearing_relevant, request_reason, flag_code) values (4,	'AAA1',	true,	true,	'CF0002');
insert into flag_service(id, service_id, hearing_relevant, request_reason, flag_code) values (5,	'AAA1',	true,	false, 'PF0003');

