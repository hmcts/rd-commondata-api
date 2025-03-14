ALTER TABLE List_Of_Values
DROP CONSTRAINT list_of_values_key_key;


ALTER TABLE List_Of_Values
ADD CONSTRAINT compKey UNIQUE (categorykey,key,serviceid);

INSERT INTO List_Of_Values (CategoryKey,ServiceID,Key,Value_EN,Value_CY,HintText_EN,HintText_CY,Lov_Order,ParentCategory,ParentKey,Active,external_reference,external_reference_type)
VALUES ('HearingChannel','BBA3','telephone','Telephone',null,null,null,2,null,null,'Y','',''),
('HearingChannel','BBA3','video','Video',null,null,null,3,null,null,'Y','',''),
('HearingChannel','BBA3','faceToFace','Face To Face',null,null,null,1,null,null,'Y','',''),
('HearingChannel','BBA3','notAttending','Not Attending',null,null,null,4,null,null,'Y','',''),
('HearingChannel','','notAttending','Not Attending',null,null,null,5,null,null,'Y','',''),
('HearingChannel','','telephone','telephone',null,null,null,6,null,null,'Y','',''),
('HearingSubChannel','BBA3','telephone-btMeetMe','Telephone - BTMeetme',null,null,null,null,'HearingChannel','telephone','Y','',''),
('HearingSubChannel','','telephone-btMeetMe','Telephone - BTMeetme',null,null,null,null,'HearingChannel','telephone','Y','',''),
('CaseLinkingReasonCode','ABA3','CLRC002','Related proceedings',null,null,null,null,null,null,'Y','',''),
('CaseLinkingReasonCode','ABA3','CLRC017','Linked for a hearing',null,null,null,null,null,null,'Y','',''),
('CaseLinkingReasonCode','','CLRC006','Guardian',null,null,null,null,null,null,'Y','',''),
('CaseLinkingReasonCode','','CLRC004','Guardian',null,null,null,null,null,null,'Y','',''),
('CaseLinkingReasonCode','','CLRC003','Guardian',null,null,null,null,null,null,'Y','',''),
('caseSubType','BHA1','BHA1-EMT','Employment',null,null,null,null,'caseType','BFA1-EAD','Y','',''),
('caseSubType','','CLRC017','Linked for a hearing',null,null,null,null,null,null,'Y','',''),
('ListingStatus','','test','test',null,null,null,null,null,null,'Y','',''),
('EmptySubCategory','','test','test',null,null,null,null,null,null,'Y','',''),
('ListingStatusSubChannel','','test','test',null,null,null,null,'ListingStatus','test','Y','',''),
('InterpreterLanguage','BBA1','test2','test2','test2',null,null,null,'HearingChannel','test','Y','',''),
('SignLanguage','BBA1','test4','test4','test4',null,null,null,'HearingChannel','test','Y','',''),
('caseSubType','BBA3','BBA3-088OC','OVERPAYMENT',null,null,null,null,'caseType','BBA3-088','Y','',''),
('panelCategory','BBA3','BBA3-panelCategory-001','Panel Category description',null,null,null,null,null,null,'Y','',''),
('panelCategoryMember','BBA3','PC1-01-94','Financial office holder',null,null,null,null,'panelCategory','BBA3-panelCategory-001','Y','94','FinancialRole'),
('panelCategoryMember','BBA3','PC1-01-84','Judicial office holder',null,null,null,null,'panelCategory','BBA3-panelCategory-001','Y','84','JudicialRole'),
('panelCategoryMember','BBA3','PC1-01-74','Medical office holder',null,null,null,null,'panelCategory','BBA3-panelCategory-001','Y','74','MedicalRole');
