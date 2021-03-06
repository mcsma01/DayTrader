CREATE TABLE BUSINESS_ENTITY
(
  BUSINESS_KEY VARCHAR(41) NOT NULL,
  AUTHORIZED_NAME VARCHAR(255) NOT NULL,
  PUBLISHER_ID VARCHAR(20) ,
  OPERATOR VARCHAR(255) NOT NULL,
  LAST_UPDATE TIMESTAMP NOT NULL,
  PRIMARY KEY (BUSINESS_KEY)
);

CREATE TABLE BUSINESS_DESCR
(
  BUSINESS_KEY VARCHAR(41) NOT NULL,
  BUSINESS_DESCR_ID INT NOT NULL,
  LANG_CODE VARCHAR(5),
  DESCR VARCHAR(255) NOT NULL,
  PRIMARY KEY (BUSINESS_KEY,BUSINESS_DESCR_ID),
  FOREIGN KEY (BUSINESS_KEY)
    REFERENCES BUSINESS_ENTITY (BUSINESS_KEY)
);

CREATE TABLE BUSINESS_CATEGORY
(
  BUSINESS_KEY VARCHAR(41) NOT NULL,
  CATEGORY_ID INT NOT NULL,
  TMODEL_KEY_REF VARCHAR(41) ,
  KEY_NAME VARCHAR(255) ,
  KEY_VALUE VARCHAR(255) NOT NULL,
  PRIMARY KEY (BUSINESS_KEY,CATEGORY_ID),
  FOREIGN KEY (BUSINESS_KEY)
    REFERENCES BUSINESS_ENTITY (BUSINESS_KEY)
);

CREATE TABLE BUSINESS_IDENTIFIER
(
  BUSINESS_KEY VARCHAR(41) NOT NULL,
  IDENTIFIER_ID INT NOT NULL,
  TMODEL_KEY_REF VARCHAR(41) ,
  KEY_NAME VARCHAR(255) ,
  KEY_VALUE VARCHAR(255) NOT NULL,
  PRIMARY KEY (BUSINESS_KEY,IDENTIFIER_ID),
  FOREIGN KEY (BUSINESS_KEY)
    REFERENCES BUSINESS_ENTITY (BUSINESS_KEY)
);

CREATE TABLE BUSINESS_NAME
(
  BUSINESS_KEY VARCHAR(41) NOT NULL,
  BUSINESS_NAME_ID INT NOT NULL,
  LANG_CODE VARCHAR(5),
  NAME VARCHAR(255) NOT NULL,
  PRIMARY KEY (BUSINESS_KEY,BUSINESS_NAME_ID),
  FOREIGN KEY (BUSINESS_KEY)
    REFERENCES BUSINESS_ENTITY (BUSINESS_KEY)
);

CREATE TABLE CONTACT
(
  BUSINESS_KEY VARCHAR(41) NOT NULL,
  CONTACT_ID INT NOT NULL,
  USE_TYPE VARCHAR(255) ,
  PERSON_NAME VARCHAR(255) NOT NULL,
  PRIMARY KEY (BUSINESS_KEY,CONTACT_ID),
  FOREIGN KEY (BUSINESS_KEY)
    REFERENCES BUSINESS_ENTITY (BUSINESS_KEY)
);

CREATE TABLE CONTACT_DESCR
(
  BUSINESS_KEY VARCHAR(41) NOT NULL,
  CONTACT_ID INT NOT NULL,
  CONTACT_DESCR_ID INT NOT NULL,
  LANG_CODE VARCHAR(5),
  DESCR VARCHAR(255) NOT NULL,
  PRIMARY KEY (BUSINESS_KEY,CONTACT_ID,CONTACT_DESCR_ID),
  FOREIGN KEY (BUSINESS_KEY,CONTACT_ID)
    REFERENCES CONTACT (BUSINESS_KEY,CONTACT_ID)
);

CREATE TABLE ADDRESS
(
  BUSINESS_KEY VARCHAR(41) NOT NULL,
  CONTACT_ID INT NOT NULL,
  ADDRESS_ID INT NOT NULL,
  USE_TYPE VARCHAR(255) ,
  SORT_CODE VARCHAR(10) ,
  TMODEL_KEY VARCHAR(41) ,
  PRIMARY KEY (BUSINESS_KEY,CONTACT_ID,ADDRESS_ID),
  FOREIGN KEY (BUSINESS_KEY,CONTACT_ID)
    REFERENCES CONTACT (BUSINESS_KEY,CONTACT_ID)
);

CREATE TABLE ADDRESS_LINE
(
  BUSINESS_KEY VARCHAR(41) NOT NULL,
  CONTACT_ID INT NOT NULL,
  ADDRESS_ID INT NOT NULL,
  ADDRESS_LINE_ID INT NOT NULL,
  LINE VARCHAR(80) NOT NULL,
  KEY_NAME VARCHAR(255) ,
  KEY_VALUE VARCHAR(255) ,
  PRIMARY KEY (BUSINESS_KEY,CONTACT_ID,ADDRESS_ID,ADDRESS_LINE_ID),
  FOREIGN KEY (BUSINESS_KEY,CONTACT_ID,ADDRESS_ID)
    REFERENCES ADDRESS (BUSINESS_KEY,CONTACT_ID,ADDRESS_ID)
);

CREATE TABLE EMAIL
(
  BUSINESS_KEY VARCHAR(41) NOT NULL,
  CONTACT_ID INT NOT NULL,
  EMAIL_ID INT NOT NULL,
  USE_TYPE VARCHAR(255) ,
  EMAIL_ADDRESS VARCHAR(255) NOT NULL,
  PRIMARY KEY (BUSINESS_KEY,CONTACT_ID,EMAIL_ID),
  FOREIGN KEY (BUSINESS_KEY,CONTACT_ID)
    REFERENCES CONTACT (BUSINESS_KEY,CONTACT_ID)
);

CREATE TABLE PHONE
(
  BUSINESS_KEY VARCHAR(41) NOT NULL,
  CONTACT_ID INT NOT NULL,
  PHONE_ID INT NOT NULL,
  USE_TYPE VARCHAR(255) ,
  PHONE_NUMBER VARCHAR(50) NOT NULL,
  PRIMARY KEY (BUSINESS_KEY,CONTACT_ID,PHONE_ID),
  FOREIGN KEY (BUSINESS_KEY,CONTACT_ID)
    REFERENCES CONTACT (BUSINESS_KEY,CONTACT_ID)
);

CREATE TABLE DISCOVERY_URL
(
  BUSINESS_KEY VARCHAR(41) NOT NULL,
  DISCOVERY_URL_ID INT NOT NULL,
  USE_TYPE VARCHAR(255) NOT NULL,
  URL VARCHAR(255) NOT NULL,
  PRIMARY KEY (BUSINESS_KEY,DISCOVERY_URL_ID),
  FOREIGN KEY (BUSINESS_KEY)
    REFERENCES BUSINESS_ENTITY (BUSINESS_KEY)
);

CREATE TABLE BUSINESS_SERVICE
(
  BUSINESS_KEY VARCHAR(41) NOT NULL,
  SERVICE_KEY VARCHAR(41) NOT NULL,
  LAST_UPDATE TIMESTAMP NOT NULL,
  PRIMARY KEY (SERVICE_KEY),
  FOREIGN KEY (BUSINESS_KEY)
    REFERENCES BUSINESS_ENTITY (BUSINESS_KEY)
);

CREATE TABLE SERVICE_DESCR
(
  SERVICE_KEY VARCHAR(41) NOT NULL,
  SERVICE_DESCR_ID INT NOT NULL,
  LANG_CODE VARCHAR(5),
  DESCR VARCHAR(255) NOT NULL,
  PRIMARY KEY (SERVICE_KEY,SERVICE_DESCR_ID),
  FOREIGN KEY (SERVICE_KEY)
    REFERENCES BUSINESS_SERVICE (SERVICE_KEY)
);

CREATE TABLE SERVICE_CATEGORY
(
  SERVICE_KEY VARCHAR(41) NOT NULL,
  CATEGORY_ID INT NOT NULL,
  TMODEL_KEY_REF VARCHAR(41) ,
  KEY_NAME VARCHAR(255) ,
  KEY_VALUE VARCHAR(255) NOT NULL,
  PRIMARY KEY (SERVICE_KEY,CATEGORY_ID),
  FOREIGN KEY (SERVICE_KEY)
    REFERENCES BUSINESS_SERVICE (SERVICE_KEY)
);

CREATE TABLE SERVICE_NAME
(
  SERVICE_KEY VARCHAR(41) NOT NULL,
  SERVICE_NAME_ID INT NOT NULL,
  LANG_CODE VARCHAR(5) ,
  NAME VARCHAR(255) NOT NULL,
  PRIMARY KEY (SERVICE_KEY,SERVICE_NAME_ID),
  FOREIGN KEY (SERVICE_KEY)
    REFERENCES BUSINESS_SERVICE (SERVICE_KEY)
);

CREATE TABLE BINDING_TEMPLATE
(
  SERVICE_KEY VARCHAR(41) NOT NULL,
  BINDING_KEY VARCHAR(41) NOT NULL,
  ACCESS_POINT_TYPE VARCHAR(20) ,
  ACCESS_POINT_URL VARCHAR(255) ,
  HOSTING_REDIRECTOR VARCHAR(255) ,
  LAST_UPDATE TIMESTAMP NOT NULL,
  PRIMARY KEY (BINDING_KEY),
  FOREIGN KEY (SERVICE_KEY)
    REFERENCES BUSINESS_SERVICE (SERVICE_KEY)
);

CREATE TABLE BINDING_CATEGORY
(
  BINDING_KEY VARCHAR(41) NOT NULL,
  CATEGORY_ID INT NOT NULL,
  TMODEL_KEY_REF VARCHAR(41),
  KEY_NAME VARCHAR(255),
  KEY_VALUE VARCHAR(255) NOT NULL,
  PRIMARY KEY (BINDING_KEY,CATEGORY_ID),
  FOREIGN KEY (BINDING_KEY)
    REFERENCES BINDING_TEMPLATE (BINDING_KEY)
);

CREATE TABLE BINDING_DESCR
(
  BINDING_KEY VARCHAR(41) NOT NULL,
  BINDING_DESCR_ID INT NOT NULL,
  LANG_CODE VARCHAR(5),
  DESCR VARCHAR(255) NOT NULL,
  PRIMARY KEY (BINDING_KEY,BINDING_DESCR_ID),
  FOREIGN KEY (BINDING_KEY)
    REFERENCES BINDING_TEMPLATE (BINDING_KEY)
);

CREATE TABLE TMODEL_INSTANCE_INFO
(
  BINDING_KEY VARCHAR(41) NOT NULL,
  TMODEL_INSTANCE_INFO_ID INT NOT NULL,
  TMODEL_KEY VARCHAR(41) NOT NULL,
  OVERVIEW_URL VARCHAR(255) ,
  INSTANCE_PARMS VARCHAR(255) ,
  PRIMARY KEY (BINDING_KEY,TMODEL_INSTANCE_INFO_ID),
  FOREIGN KEY (BINDING_KEY)
    REFERENCES BINDING_TEMPLATE (BINDING_KEY)
);

CREATE TABLE TMODEL_INSTANCE_INFO_DESCR
(
  BINDING_KEY VARCHAR(41) NOT NULL,
  TMODEL_INSTANCE_INFO_ID INT NOT NULL,
  TMODEL_INSTANCE_INFO_DESCR_ID INT NOT NULL,
  LANG_CODE VARCHAR(5),
  DESCR VARCHAR(255) NOT NULL,
  PRIMARY KEY (BINDING_KEY,TMODEL_INSTANCE_INFO_ID,TMODEL_INSTANCE_INFO_DESCR_ID),
  FOREIGN KEY (BINDING_KEY,TMODEL_INSTANCE_INFO_ID)
    REFERENCES TMODEL_INSTANCE_INFO (BINDING_KEY,TMODEL_INSTANCE_INFO_ID)
);

CREATE TABLE INSTANCE_DETAILS_DESCR
(
  BINDING_KEY VARCHAR(41) NOT NULL,
  TMODEL_INSTANCE_INFO_ID INT NOT NULL,
  INSTANCE_DETAILS_DESCR_ID INT NOT NULL,
  LANG_CODE VARCHAR(5),
  DESCR VARCHAR(255) NOT NULL,
  PRIMARY KEY (BINDING_KEY,TMODEL_INSTANCE_INFO_ID,INSTANCE_DETAILS_DESCR_ID),
  FOREIGN KEY (BINDING_KEY,TMODEL_INSTANCE_INFO_ID)
    REFERENCES TMODEL_INSTANCE_INFO (BINDING_KEY,TMODEL_INSTANCE_INFO_ID)
);

CREATE TABLE INSTANCE_DETAILS_DOC_DESCR
(
  BINDING_KEY VARCHAR(41) NOT NULL,
  TMODEL_INSTANCE_INFO_ID INT NOT NULL,
  INSTANCE_DETAILS_DOC_DESCR_ID INT NOT NULL,
  LANG_CODE VARCHAR(5),
  DESCR VARCHAR(255) NOT NULL,
  PRIMARY KEY (BINDING_KEY,TMODEL_INSTANCE_INFO_ID,INSTANCE_DETAILS_DOC_DESCR_ID),
  FOREIGN KEY (BINDING_KEY,TMODEL_INSTANCE_INFO_ID)
    REFERENCES TMODEL_INSTANCE_INFO (BINDING_KEY,TMODEL_INSTANCE_INFO_ID)
);

CREATE TABLE TMODEL
(
  TMODEL_KEY VARCHAR(41) NOT NULL,
  AUTHORIZED_NAME VARCHAR(255) NOT NULL,
  PUBLISHER_ID VARCHAR(20) ,
  OPERATOR VARCHAR(255) NOT NULL,
  NAME VARCHAR(255) NOT NULL,
  OVERVIEW_URL VARCHAR(255) ,
  DELETED VARCHAR(5),
  LAST_UPDATE TIMESTAMP NOT NULL,
  PRIMARY KEY (TMODEL_KEY)
);

CREATE TABLE TMODEL_DESCR
(
  TMODEL_KEY VARCHAR(41) NOT NULL,
  TMODEL_DESCR_ID INT NOT NULL,
  LANG_CODE VARCHAR(5),
  DESCR VARCHAR(255) NOT NULL,
  PRIMARY KEY (TMODEL_KEY,TMODEL_DESCR_ID),
  FOREIGN KEY (TMODEL_KEY)
    REFERENCES TMODEL (TMODEL_KEY)
);

CREATE TABLE TMODEL_CATEGORY
(
  TMODEL_KEY VARCHAR(41) NOT NULL,
  CATEGORY_ID INT NOT NULL,
  TMODEL_KEY_REF VARCHAR(255) ,
  KEY_NAME VARCHAR(255) ,
  KEY_VALUE VARCHAR(255) NOT NULL,
  PRIMARY KEY (TMODEL_KEY,CATEGORY_ID),
  FOREIGN KEY (TMODEL_KEY)
    REFERENCES TMODEL (TMODEL_KEY)
);

CREATE TABLE TMODEL_IDENTIFIER
(
  TMODEL_KEY VARCHAR(41) NOT NULL,
  IDENTIFIER_ID INT NOT NULL,
  TMODEL_KEY_REF VARCHAR(255) ,
  KEY_NAME VARCHAR(255) ,
  KEY_VALUE VARCHAR(255) NOT NULL,
  PRIMARY KEY (TMODEL_KEY,IDENTIFIER_ID),
  FOREIGN KEY (TMODEL_KEY)
    REFERENCES TMODEL (TMODEL_KEY)
);

CREATE TABLE TMODEL_DOC_DESCR
(
  TMODEL_KEY VARCHAR(41) NOT NULL,
  TMODEL_DOC_DESCR_ID INT NOT NULL,
  LANG_CODE VARCHAR(5),
  DESCR VARCHAR(255) NOT NULL,
  PRIMARY KEY (TMODEL_KEY,TMODEL_DOC_DESCR_ID),
  FOREIGN KEY (TMODEL_KEY)
    REFERENCES TMODEL (TMODEL_KEY)
);

CREATE TABLE PUBLISHER_ASSERTION
(
  FROM_KEY VARCHAR(41) NOT NULL,
  TO_KEY VARCHAR(41) NOT NULL,
  TMODEL_KEY VARCHAR(41) NOT NULL,
  KEY_NAME VARCHAR(255) NOT NULL,
  KEY_VALUE VARCHAR(255) NOT NULL,
  FROM_CHECK VARCHAR(5) NOT NULL,
  TO_CHECK VARCHAR(5) NOT NULL,
  FOREIGN KEY (FROM_KEY) 
    REFERENCES BUSINESS_ENTITY (BUSINESS_KEY),
  FOREIGN KEY (TO_KEY) 
    REFERENCES BUSINESS_ENTITY (BUSINESS_KEY) 
);

CREATE TABLE PUBLISHER
(
  PUBLISHER_ID VARCHAR(20) NOT NULL,
  PUBLISHER_NAME VARCHAR(255) NOT NULL,
  EMAIL_ADDRESS VARCHAR(255),
  IS_ADMIN VARCHAR(5),
  IS_ENABLED VARCHAR(5),
  PRIMARY KEY (PUBLISHER_ID)
);

CREATE TABLE AUTH_TOKEN
(
  AUTH_TOKEN VARCHAR(51) NOT NULL,
  PUBLISHER_ID VARCHAR(20) NOT NULL,
  PUBLISHER_NAME VARCHAR(255) NOT NULL,
  CREATED TIMESTAMP NOT NULL,
  LAST_USED TIMESTAMP NOT NULL,
  NUMBER_OF_USES INT NOT NULL,
  TOKEN_STATE INT NOT NULL,
  PRIMARY KEY (AUTH_TOKEN)
);

INSERT INTO TMODEL (TMODEL_KEY,AUTHORIZED_NAME,OPERATOR,NAME,OVERVIEW_URL,LAST_UPDATE)
VALUES ('uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','Administrator','jUDDI.org','uddi-org:types','http://www.uddi.org/taxonomies/UDDI_Taxonomy_tModels.htm#UDDItypes',CURRENT TIMESTAMP);

INSERT INTO TMODEL_DESCR (TMODEL_KEY,TMODEL_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4',0,'en','UDDI Type Taxonomy');
 
INSERT INTO TMODEL_DOC_DESCR (TMODEL_KEY,TMODEL_DOC_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4',0,'en','Taxonomy used to categorize Service Descriptions.');
 
INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4',0,'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','types','categorization');
 
INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4',1,'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','types','checked');

INSERT INTO TMODEL (TMODEL_KEY,AUTHORIZED_NAME,OPERATOR,NAME,OVERVIEW_URL,LAST_UPDATE)
VALUES ('uuid:DB77450D-9FA8-45D4-A7BC-04411D14E384','Administrator','jUDDI.org','unspsc-org:unspsc:3-1','http://www.uddi.org/taxonomies/UDDI_Taxonomy_tModels.htm#UNSPSC31',CURRENT TIMESTAMP);

INSERT INTO TMODEL_DESCR (TMODEL_KEY,TMODEL_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:DB77450D-9FA8-45D4-A7BC-04411D14E384',0,'en','Product Taxonomy: UNSPSC (Version 3.1)');

INSERT INTO TMODEL_DOC_DESCR (TMODEL_KEY,TMODEL_DOC_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:DB77450D-9FA8-45D4-A7BC-04411D14E384',0,'en','This tModel defines the UNSPSC product taxonomy.');

INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:DB77450D-9FA8-45D4-A7BC-04411D14E384',0,'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','types','categorization');

INSERT INTO TMODEL (TMODEL_KEY,AUTHORIZED_NAME,OPERATOR,NAME,OVERVIEW_URL,LAST_UPDATE)
VALUES ('uuid:CD153257-086A-4237-B336-6BDCBDCC6634','Administrator','jUDDI.org','unspsc-org:unspsc','http://www.uddi.org/taxonomies/UDDI_Taxonomy_tModels.htm#UNSPSC',CURRENT TIMESTAMP);

INSERT INTO TMODEL_DESCR (TMODEL_KEY,TMODEL_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:CD153257-086A-4237-B336-6BDCBDCC6634',0,'en','Product Taxonomy: UNSPSC (Version 7.3)');

INSERT INTO TMODEL_DOC_DESCR (TMODEL_KEY,TMODEL_DOC_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:CD153257-086A-4237-B336-6BDCBDCC6634',0,'en','This tModel defines Version 7.3 of the UNSPSC product taxonomy.');

INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:CD153257-086A-4237-B336-6BDCBDCC6634',0,'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','types','categorization');

INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:CD153257-086A-4237-B336-6BDCBDCC6634',1,'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','types','Checked');

INSERT INTO TMODEL (TMODEL_KEY,AUTHORIZED_NAME,OPERATOR,NAME,OVERVIEW_URL,LAST_UPDATE)
VALUES ('uuid:C0B9FE13-179F-413D-8A5B-5004DB8E5BB2','Administrator','jUDDI.org','ntis-gov:naics:1997','http://www.uddi.org/taxonomies/UDDI_Taxonomy_tModels.htm#NAICS',CURRENT TIMESTAMP);

INSERT INTO TMODEL_DESCR (TMODEL_KEY,TMODEL_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:C0B9FE13-179F-413D-8A5B-5004DB8E5BB2',0,'en','Business Taxonomy: NAICS(1997 Release)');

INSERT INTO TMODEL_DOC_DESCR (TMODEL_KEY,TMODEL_DOC_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:C0B9FE13-179F-413D-8A5B-5004DB8E5BB2',0,'en','This tModel defines the NAICS industry taxonomy.');

INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:C0B9FE13-179F-413D-8A5B-5004DB8E5BB2',0,'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','types','categorization');

INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:C0B9FE13-179F-413D-8A5B-5004DB8E5BB2',1,'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','types','checked');

INSERT INTO TMODEL (TMODEL_KEY,AUTHORIZED_NAME,OPERATOR,NAME,OVERVIEW_URL,LAST_UPDATE)
VALUES ('uuid:4E49A8D6-D5A2-4FC2-93A0-0411D8D19E88','Administrator','jUDDI.org','uddi-org:iso-ch:3166-1999','http://www.uddi.org/taxonomies/UDDI_Taxonomy_tModels.htm#ISO3166',CURRENT TIMESTAMP);

INSERT INTO TMODEL_DESCR (TMODEL_KEY,TMODEL_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:4E49A8D6-D5A2-4FC2-93A0-0411D8D19E88',0,'en','ISO 3166-1:1997 and 3166-2:1998. Codes for names of countries and their subdivisions. Part 1: Country codes. Part 2:Country subdivision codes. Update newsletters include ISO 3166-1 V-1 (1998-02-05), V-2 (1999-10-01), ISO 3166-2 I-1 (1998)');

INSERT INTO TMODEL_DOC_DESCR (TMODEL_KEY,TMODEL_DOC_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:4E49A8D6-D5A2-4FC2-93A0-0411D8D19E88',0,'en','Taxonomy used to categorize entries by geographic location.');

INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:4E49A8D6-D5A2-4FC2-93A0-0411D8D19E88',0,'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','types','categorization');

INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:4E49A8D6-D5A2-4FC2-93A0-0411D8D19E88',1,'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','types','checked');

INSERT INTO TMODEL (TMODEL_KEY,AUTHORIZED_NAME,OPERATOR,NAME,OVERVIEW_URL,LAST_UPDATE)
VALUES ('uuid:A035A07C-F362-44DD-8F95-E2B134BF43B4','Administrator','jUDDI.org','uddi-org:general_keywords','http://www.uddi.org/taxonomies/UDDI_Taxonomy_tModels.htm#GenKW',CURRENT TIMESTAMP);

INSERT INTO TMODEL_DESCR (TMODEL_KEY,TMODEL_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:A035A07C-F362-44DD-8F95-E2B134BF43B4',0,'en','Special taxonomy consisting of namespace identifiers and the keywords associated with the namespaces');

INSERT INTO TMODEL_DOC_DESCR (TMODEL_KEY,TMODEL_DOC_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:A035A07C-F362-44DD-8F95-E2B134BF43B4',0,'en','This tModel defines an unidentified taxonomy.');

INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:A035A07C-F362-44DD-8F95-E2B134BF43B4',0,'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','types','categorization');

INSERT INTO TMODEL (TMODEL_KEY,AUTHORIZED_NAME,OPERATOR,NAME,OVERVIEW_URL,LAST_UPDATE)
VALUES ('uuid:4064C064-6D14-4F35-8953-9652106476A9','Administrator','jUDDI.org','uddi-org:owningBusiness','http://www.uddi.org/taxonomies/UDDI_Taxonomy_tModels.htm#owningBusiness',CURRENT TIMESTAMP);

INSERT INTO TMODEL_DESCR (TMODEL_KEY,TMODEL_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:4064C064-6D14-4F35-8953-9652106476A9',0,'en','A pointer to a businessEntity that owns the tagged data.');

INSERT INTO TMODEL_DOC_DESCR (TMODEL_KEY,TMODEL_DOC_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:4064C064-6D14-4F35-8953-9652106476A9',0,'en','This tModel indicates the businessEntity that published or owns the tagged tModel. Used with tModels to establish an "owned" relationship with a registered businessEntity.');

INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:4064C064-6D14-4F35-8953-9652106476A9',0,'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','types','categorization');

INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:4064C064-6D14-4F35-8953-9652106476A9',1,'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','types','checked');

INSERT INTO TMODEL (TMODEL_KEY,AUTHORIZED_NAME,OPERATOR,NAME,OVERVIEW_URL,LAST_UPDATE)
VALUES ('uuid:807A2C6A-EE22-470D-ADC7-E0424A337C03','Administrator','jUDDI.org','uddi-org:relationships','http://www.uddi.org/taxonomies/UDDI_Taxonomy_tModels.htm#Relationships',CURRENT TIMESTAMP);

INSERT INTO TMODEL_DESCR (TMODEL_KEY,TMODEL_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:807A2C6A-EE22-470D-ADC7-E0424A337C03',0,'en','Starter set classifications of businessEntity relationships');

INSERT INTO TMODEL_DOC_DESCR (TMODEL_KEY,TMODEL_DOC_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:807A2C6A-EE22-470D-ADC7-E0424A337C03',0,'en','This tModel is used to describe business relationships. Used in the publisher assertion messages.');

INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:807A2C6A-EE22-470D-ADC7-E0424A337C03',0,'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','types','relationship');

INSERT INTO TMODEL (TMODEL_KEY,AUTHORIZED_NAME,OPERATOR,NAME,OVERVIEW_URL,LAST_UPDATE)
VALUES ('uuid:327A56F0-3299-4461-BC23-5CD513E95C55','Administrator','jUDDI.org','uddi-org:operators','http://www.uddi.org/taxonomies/UDDI_Taxonomy_tModels.htm#Operators',CURRENT TIMESTAMP);

INSERT INTO TMODEL_DESCR (TMODEL_KEY,TMODEL_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:327A56F0-3299-4461-BC23-5CD513E95C55',0,'en','Taxonomy for categorizing the businessEntity of an operator of a registry.');

INSERT INTO TMODEL_DOC_DESCR (TMODEL_KEY,TMODEL_DOC_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:327A56F0-3299-4461-BC23-5CD513E95C55',0,'en','This checked value set is used to identify UDDI operators.');

INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:327A56F0-3299-4461-BC23-5CD513E95C55',0,'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','types','categorization');

INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:327A56F0-3299-4461-BC23-5CD513E95C55',1,'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','types','checked');

INSERT INTO TMODEL (TMODEL_KEY,AUTHORIZED_NAME,OPERATOR,NAME,OVERVIEW_URL,LAST_UPDATE)
VALUES ('uuid:E59AE320-77A5-11D5-B898-0004AC49CC1E','Administrator','jUDDI.org','uddi-org:isReplacedBy','http://www.uddi.org/taxonomies/UDDI_Taxonomy_tModels.htm#IsReplacedBy',CURRENT TIMESTAMP);

INSERT INTO TMODEL_DESCR (TMODEL_KEY,TMODEL_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:E59AE320-77A5-11D5-B898-0004AC49CC1E',0,'en','An identifier system used to point (using UDDI keys) to the tModel (or businessEntity) that is the logical replacement for the one in which isReplacedBy is used');

INSERT INTO TMODEL_DOC_DESCR (TMODEL_KEY,TMODEL_DOC_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:E59AE320-77A5-11D5-B898-0004AC49CC1E',0,'en','This is a checked value set.');

INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:E59AE320-77A5-11D5-B898-0004AC49CC1E',0,'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','types','identifier');

INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:E59AE320-77A5-11D5-B898-0004AC49CC1E',1,'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','types','checked');

INSERT INTO TMODEL (TMODEL_KEY,AUTHORIZED_NAME,OPERATOR,NAME,OVERVIEW_URL,LAST_UPDATE)
VALUES ('uuid:8609C81E-EE1F-4D5A-B202-3EB13AD01823','Administrator','jUDDI.org','dnb-com:D-U-N-S','http://www.uddi.org/taxonomies/UDDI_Taxonomy_tModels.htm#D-U-N-S',CURRENT TIMESTAMP);

INSERT INTO TMODEL_DESCR (TMODEL_KEY,TMODEL_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:8609C81E-EE1F-4D5A-B202-3EB13AD01823',0,'en','Dun&Bradstreet D-U-N-S� Number');

INSERT INTO TMODEL_DOC_DESCR (TMODEL_KEY,TMODEL_DOC_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:8609C81E-EE1F-4D5A-B202-3EB13AD01823',0,'en','This tModel is used for the Dun&Bradstreet D-U-N-S� Number identifier.');

INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:8609C81E-EE1F-4D5A-B202-3EB13AD01823',0,'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','types','identifier');

INSERT INTO TMODEL (TMODEL_KEY,AUTHORIZED_NAME,OPERATOR,NAME,OVERVIEW_URL,LAST_UPDATE)
VALUES ('uuid:B1B1BAF5-2329-43E6-AE13-BA8E97195039','Administrator','jUDDI.org','thomasregister-com:supplierID','http://www.uddi.org/taxonomies/UDDI_Taxonomy_tModels.htm#Thomas',CURRENT TIMESTAMP);

INSERT INTO TMODEL_DESCR (TMODEL_KEY,TMODEL_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:B1B1BAF5-2329-43E6-AE13-BA8E97195039',0,'en','Thomas Registry Suppliers');

INSERT INTO TMODEL_DOC_DESCR (TMODEL_KEY,TMODEL_DOC_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:B1B1BAF5-2329-43E6-AE13-BA8E97195039',0,'en','This tModel is used for the Thomas Register supplier identifier codes.');

INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:B1B1BAF5-2329-43E6-AE13-BA8E97195039',0,'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4','types','identifier');

INSERT INTO PUBLISHER (PUBLISHER_ID,PUBLISHER_NAME,IS_ADMIN,IS_ENABLED)
VALUES ('juddi', 'jUDDI User', 'false', 'true');
