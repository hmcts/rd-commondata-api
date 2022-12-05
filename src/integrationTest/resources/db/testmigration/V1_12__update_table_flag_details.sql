--Delete the record id=50 and update with id=85
UPDATE flag_details SET id='85', value_en='Explanation of the court or tribunal and who''s in the room at the hearing',
      mrd_created_time='2022-10-27 12:33',mrd_updated_time='2022-10-27 12:33' where id = 50 and flag_code = 'RA0047' and category_id=11;

INSERT INTO flag_details (id,flag_code,value_en,value_cy,category_id,mrd_created_time,mrd_updated_time,mrd_deleted_time) VALUES
(110,'PF0027','Test27','Test27',2,'2022-07-04 12:43:00','2022-10-05 10:16:12',NULL);

insert into flag_service(id, service_id, hearing_relevant, request_reason, flag_code, available_externally, default_status) values (11, 'AAA1', false, false, 'RA0004', true, 'Active');
insert into flag_service(id, service_id, hearing_relevant, request_reason, flag_code, available_externally, default_status) values (9,	'AAA1',	true,	true,	'PF0011', true, 'Active');
insert into flag_service(id, service_id, hearing_relevant, request_reason, flag_code, available_externally, default_status) values (10,	'AAA1',	true,	false, 'PF0012', true, 'Active');
insert into flag_service(id, service_id, hearing_relevant, request_reason, flag_code, available_externally, default_status) values (12,	'AAA1',	true,	false, 'PF0027', true, 'Active');
COMMIT;
