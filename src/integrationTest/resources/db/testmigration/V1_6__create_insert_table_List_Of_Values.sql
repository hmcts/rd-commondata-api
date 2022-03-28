CREATE TABLE List_Of_Values (
 categorykey varchar(64) NOT NULL,
 serviceid varchar(16),
 key varchar(64) NOT NULL UNIQUE,
 value_en varchar(128) NOT NULL,
 value_cy varchar(128),
 hinttext_en varchar(512),
 hinttext_cy varchar(512),
 lov_order bigint,
 parentcategory varchar(64),
 parentkey varchar(64),
 active varchar(1)
);

INSERT INTO list_of_values (CategoryKey,ServiceID,Key,Value_EN,Value_CY,HintText_EN,HintText_CY,Lov_Order,ParentCategory,ParentKey,Active)
VALUES ('HearingChannel','BBA3','telephone','Telephone',null,null,null,2,null,null,'Y'),
('HearingChannel','BBA3','video','Video',null,null,null,3,null,null,'Y'),
('HearingChannel','BBA3','faceToFace','Face To Face',null,null,null,1,null,null,'Y'),
('HearingChannel','BBA3','notAttending','Not Attending',null,null,null,4,null,null,'Y'),
('HearingSubChannel','BBA3','telephone-btMeetMe','Telephone - BTMeetme',null,null,null,null,'HearingChannel','telephone','Y');
