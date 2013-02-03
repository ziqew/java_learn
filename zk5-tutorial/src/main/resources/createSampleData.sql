/* create tables and demo data for the Zksample2 demo application */
/* Script for the H2 database */

DROP TABLE IF EXISTS filiale cascade;
DROP SEQUENCE IF EXISTS filiale_seq;

DROP TABLE IF EXISTS kunde cascade;
DROP SEQUENCE IF EXISTS kunde_seq;

DROP TABLE IF EXISTS artikel cascade;
DROP SEQUENCE IF EXISTS artikel_seq;

DROP TABLE IF EXISTS auftrag cascade;
DROP SEQUENCE IF EXISTS auftrag_seq;

DROP TABLE IF EXISTS auftragposition cascade;
DROP SEQUENCE IF EXISTS auftragposition_seq;

DROP TABLE IF EXISTS branche cascade;
DROP SEQUENCE IF EXISTS branche_seq;

/* Security */
DROP TABLE IF EXISTS sec_user cascade;
DROP SEQUENCE IF EXISTS sec_user_seq;

DROP TABLE IF EXISTS sec_userrole cascade;
DROP SEQUENCE IF EXISTS sec_userrole_seq;

DROP TABLE IF EXISTS sec_role cascade;
DROP SEQUENCE IF EXISTS sec_role_seq;

DROP TABLE IF EXISTS sec_rolegroup cascade;
DROP SEQUENCE IF EXISTS sec_rolegroup_seq;

DROP TABLE IF EXISTS sec_group cascade;
DROP SEQUENCE IF EXISTS sec_group_seq;

DROP TABLE IF EXISTS sec_groupright cascade;
DROP SEQUENCE IF EXISTS sec_groupright_seq;

DROP TABLE IF EXISTS sec_right cascade;
DROP SEQUENCE IF EXISTS sec_right_seq;
/* END: Security */

DROP TABLE IF EXISTS sys_countrycode cascade;
DROP SEQUENCE IF EXISTS sys_countrycode_seq;

DROP TABLE IF EXISTS sys_ip4country cascade;
DROP SEQUENCE IF EXISTS sys_ip4country_seq;

DROP TABLE IF EXISTS youtube_link cascade;
DROP SEQUENCE IF EXISTS youtube_link_seq;

DROP TABLE IF EXISTS ipc_ip2country cascade;
DROP SEQUENCE IF EXISTS ipc_ip2country_seq;
 
DROP TABLE IF EXISTS sec_loginlog cascade;
DROP SEQUENCE IF EXISTS sec_loginlog_seq;
 
DROP TABLE IF EXISTS log_ip2country cascade;
DROP SEQUENCE IF EXISTS log_ip2country_seq;
 
DROP TABLE IF EXISTS guestbook cascade;
DROP SEQUENCE IF EXISTS guestbook_seq;
 
DROP TABLE IF EXISTS calendar_event cascade;
DROP SEQUENCE IF EXISTS calendar_event_seq;

DROP TABLE IF EXISTS app_news cascade;
DROP SEQUENCE IF EXISTS app_news_seq;

/********** Hibernate DB Performance Logging ****************/
DROP TABLE IF EXISTS hibernate_entity_statistics cascade;
DROP SEQUENCE IF EXISTS hibernate_entity_statistics_seq;

DROP TABLE IF EXISTS hibernate_statistics cascade;
DROP SEQUENCE IF EXISTS hibernate_statistics_seq;

/*==============================================================*/
/* Table: Hibernate_Statistics                                  */
/*==============================================================*/
CREATE TABLE hibernate_statistics
(
  id INT8 NOT NULL,
  flushcount integer NOT NULL,
  preparestatementcount integer NOT NULL,
  entityloadcount integer NOT NULL,
  entityupdatecount integer NOT NULL,
  entityinsertcount integer NOT NULL,
  entitydeletecount integer NOT NULL,
  entityfetchcount integer NOT NULL,
  collectionloadcount integer NOT NULL,
  collectionupdatecount integer NOT NULL,
  collectionremovecount integer NOT NULL,
  collectionrecreatecount integer NOT NULL,
  collectionfetchcount integer NOT NULL,
  queryexecutioncount integer NOT NULL,
  queryexecutionmaxtime integer NOT NULL,
  optimisticfailurecount integer NOT NULL,
  queryexecutionmaxtimequerystring text,
  callmethod text NOT NULL,
  javafinishms bigint NOT NULL,
  finishtime TIMESTAMP NOT NULL,
  CONSTRAINT hibernatestatistics_pkey PRIMARY KEY (id)
)
;

/*==============================================================*/
/* Table: Hibernate_                                               */
/*==============================================================*/
CREATE TABLE hibernate_entity_statistics
(
  id INT8 NOT NULL,
  hibernateentitystatisticsid bigint NOT NULL,
  entityname text NOT NULL,
  loadcount integer NOT NULL,
  updatecount integer NOT NULL,
  insertcount integer NOT NULL,
  deletecount integer NOT NULL,
  fetchcount integer NOT NULL,
  optimisticfailurecount integer NOT NULL,
  CONSTRAINT hibernateentitystatistics_pkey PRIMARY KEY (id)
)
;

CREATE INDEX fki_
  ON hibernate_entity_statistics (
  hibernateentitystatisticsid
  );
/*************End Hibernate Statistics **********************/
  
  
/*==============================================================*/
/* Table: app_news                                              */
/*==============================================================*/
create table app_news (
   anw_id               INT8                 not null,
   anw_text             VARCHAR(1000)        null,
   anw_date             DATE                 not null,
   version              INT4                 not null default 0,
   constraint PK_APP_NEWS primary key (anw_id)
)
;


/*==============================================================*/
/* Index: Index_app_news_id                                     */
/*==============================================================*/
create unique index Index_app_news_id on app_news (
anw_id
);
    
  
/*==============================================================*/
/* Table: youtube_link                                          */
/*==============================================================*/
create table youtube_link (
   ytb_id               INT8                 not null,
   ytb_interpret        VARCHAR(50)          null,
   ytb_title            VARCHAR(50)          null,
   ytb_url              VARCHAR(250)         not null,
   version              INT4                 not null default 0,
   constraint PK_youtube_link primary key (ytb_id)
)
;


/*==============================================================*/
/* Table: Branche                                               */
/*==============================================================*/
create table Branche (
   bra_id               INT8                 not null,
   bra_nr               VARCHAR(20)          null,
   bra_bezeichnung      VARCHAR(30)          not null,
   version              INT4                 not null default 0,
   constraint PK_BRANCHE primary key (bra_id)
)
;
/*==============================================================*/
/* Index: idx_bra_id                                            */
/*==============================================================*/
create unique index idx_bra_id on Branche (
bra_id
);

/*==============================================================*/
/* Index: idx_bra_bezeichnung                                   */
/*==============================================================*/
create unique index idx_bra_bezeichnung on Branche (
bra_bezeichnung
);

/*==============================================================*/
/* Table: artikel                                               */
/*==============================================================*/
create table artikel (
   art_id               INT8                 not null,
   art_kurzbezeichnung  VARCHAR(50)          not null,
   art_langbezeichnung  TEXT                 null,
   art_nr               VARCHAR(20)          not null,
   art_preis            NUMERIC(12,2)        not null default 0.00,
   version              INT4                 not null default 0,
   constraint PK_ARTIKEL primary key (art_id)
)
;
/*==============================================================*/
/* Index: idx_art_id                                            */
/*==============================================================*/
create unique index idx_art_id on artikel (
art_id
);

/*==============================================================*/
/* Index: idx_art_nr                                            */
/*==============================================================*/
create unique index idx_art_nr on artikel (
art_nr
);

/*==============================================================*/
/* Index: idx_art_bezeichnung                                   */
/*==============================================================*/
create  index idx_art_bezeichnung on artikel (
art_kurzbezeichnung
);

/*==============================================================*/
/* Table: auftrag                                               */
/*==============================================================*/
create table auftrag (
   auf_id               INT8                 not null,
   auf_kun_id           INT8                 not null,
   auf_nr               varchar(20)          not null,
   auf_bezeichnung      varchar(50)          null,
   version              int4                 not null default 0,
   constraint pk_auftrag primary key (auf_id)
)
;
/*==============================================================*/
/* Index: ix_auf_id                                             */
/*==============================================================*/
create unique index ix_auf_id on auftrag (
auf_id
);

/*==============================================================*/
/* Index: ix_auf_kun_id                                         */
/*==============================================================*/
create  index ix_auf_kun_id on auftrag (
auf_kun_id
);

/*==============================================================*/
/* Index: ix_auf_nr                                             */
/*==============================================================*/
create unique index ix_auf_nr on auftrag (
auf_nr
);

/*==============================================================*/
/* Table: auftragposition                                       */
/*==============================================================*/
create table auftragposition (
   aup_id               INT8                 not null,
   aup_auf_id           INT8                 not null,
   art_id               INT8                 null,
   aup_position         int4                 null,
   aup_menge            numeric(12,2)        null,
   aup_einzelwert       numeric(12,2)        null,
   aup_gesamtwert       numeric(12,2)        null,
   version              int4                 not null default 0,
   constraint pk_auftragposition primary key (aup_id)
)
;
/*==============================================================*/
/* Index: ix_aup_auf_id                                         */
/*==============================================================*/
create  index ix_aup_auf_id on auftragposition (
aup_auf_id
);

/*==============================================================*/
/* Index: ix_aup_id                                             */
/*==============================================================*/
create unique index ix_aup_id on auftragposition (
aup_id
);

/*==============================================================*/
/* Table: calendar_event                                        */
/*==============================================================*/
create table calendar_event (
   cle_id               INT8                 not null,
   cle_title            VARCHAR(20)          null,
   cle_content          VARCHAR(300)         not null,
   cle_begin_date       TIMESTAMP            not null,
   cle_end_date         TIMESTAMP            not null,
   cle_header_color     VARCHAR(10)          null,
   cle_content_color    VARCHAR(10)          null,
   cle_usr_id           INT8                 not null,
   cle_locked           BOOL                 null default false,
   version              INT4                 not null default 0,
   constraint PK_CALENDAR_EVENT primary key (cle_id)
)
;
/*==============================================================*/
/* Index: idx_cle_id                                            */
/*==============================================================*/
create unique index idx_cle_id on calendar_event (
cle_id
);


/*==============================================================*/
/* Table: filiale                                               */
/*==============================================================*/
create table filiale (
   fil_id               INT8                 not null,
   fil_nr               varchar(20)          not null,
   fil_bezeichnung      varchar(50)          null,
   fil_name1            varchar(50)          null,
   fil_name2            varchar(50)          null,
   fil_ort              varchar(50)          null,
   version              int4                 not null default 0,
   constraint pk_filiale primary key (fil_id)
)
;
/*==============================================================*/
/* Index: ix_fil_id                                             */
/*==============================================================*/
create unique index ix_fil_id on filiale (
fil_id
);

/*==============================================================*/
/* Index: ix_fil_bezeichnung                                    */
/*==============================================================*/
create  index ix_fil_bezeichnung on filiale (
fil_bezeichnung
);

/*==============================================================*/
/* Index: ix_fil_nr                                             */
/*==============================================================*/
create unique index ix_fil_nr on filiale (
fil_nr
);

/*==============================================================*/
/* Table: guestbook                                             */
/*==============================================================*/
create table guestbook (
   gub_id               INT8                 not null,
   gub_subject          VARCHAR(40)          not null,
   gub_date             TIMESTAMP            not null,
   gub_usr_name         VARCHAR(40)          not null,
   gub_text             TEXT                 null,
   version              INT4                 null,
   constraint PK_GUESTBOOK primary key (gub_id)
)
;
/*==============================================================*/
/* Index: idx_gub_id                                            */
/*==============================================================*/
create unique index idx_gub_id on guestbook (
gub_id
);

/*==============================================================*/
/* Index: idx_gub_subject                                       */
/*==============================================================*/
create  index idx_gub_subject on guestbook (
gub_subject
);

/*==============================================================*/
/* Index: idx_gub_date                                          */
/*==============================================================*/
create  index idx_gub_date on guestbook (
gub_date
);

/*==============================================================*/
/* Index: idx_gub_usr_name                                      */
/*==============================================================*/
create  index idx_gub_usr_name on guestbook (
gub_usr_name
);

/*==============================================================*/
/* Table: ipc_ip2country                                        */
/*==============================================================*/
create table ipc_ip2country (
   ipc_id               INT8                 not null,
   ipc_ip_from          INT8                 null,
   ipc_ip_to            INT8                 null,
   ipc_country_code2    VARCHAR(2)           null,
   ipc_country_code3    VARCHAR(3)           null,
   ipc_country_name     VARCHAR(50)          null,
   version              INT4                 not null default 0,
   constraint PK_IPC_IP2COUNTRY primary key (ipc_id)
)
;
/*==============================================================*/
/* Index: idx_ipc_id                                            */
/*==============================================================*/
create unique index idx_ipc_id on ipc_ip2country (
ipc_id
);

/*==============================================================*/
/* Index: idx_ipc_ip_from                                       */
/*==============================================================*/
create  index idx_ipc_ip_from on ipc_ip2country (
ipc_ip_from
);

/*==============================================================*/
/* Index: idx_ipc_ip_to                                         */
/*==============================================================*/
create  index idx_ipc_ip_to on ipc_ip2country (
ipc_ip_to
);

/*==============================================================*/
/* Index: idx_ipc_country_code2                                 */
/*==============================================================*/
create  index idx_ipc_country_code2 on ipc_ip2country (
ipc_country_code2
);

/*==============================================================*/
/* Index: idx_ipc_country_code3                                 */
/*==============================================================*/
create  index idx_ipc_country_code3 on ipc_ip2country (
ipc_country_code3
);

/*==============================================================*/
/* Index: idx_ipc_country_name                                  */
/*==============================================================*/
create  index idx_ipc_country_name on ipc_ip2country (
ipc_country_name
);

/*==============================================================*/
/* Table: kunde                                                 */
/*==============================================================*/
create table kunde (
   kun_id               INT8                 not null,
   kun_fil_id           INT8                 not null,
   kun_bra_id           INT8                 null,
   kun_nr               VARCHAR(20)          not null,
   kun_matchcode        varchar(20)          null,
   kun_name1            varchar(50)          null,
   kun_name2            varchar(50)          null,
   kun_ort              varchar(50)          null,
   kun_mahnsperre       BOOL                 null,
   version              int4                 not null default 0,
   constraint pk_kunde primary key (kun_id)
)
;
/*==============================================================*/
/* Index: ix_kun_id                                             */
/*==============================================================*/
create unique index ix_kun_id on kunde (
kun_id
);

/*==============================================================*/
/* Index: ix_kun_fil_id                                         */
/*==============================================================*/
create  index ix_kun_fil_id on kunde (
kun_fil_id
);

/*==============================================================*/
/* Index: ix_kun_nr                                             */
/*==============================================================*/
create unique index ix_kun_nr on kunde (
kun_nr
);

/*==============================================================*/
/* Table: log_ip2country                                        */
/*==============================================================*/
create table log_ip2country (
   i2c_id               INT8                 not null,
   ccd_id               INT8                 null,
   i2c_city             VARCHAR(50)          null,
   i2c_latitude         FLOAT4               null,
   i2c_longitude        FLOAT4               null,
   version              INT4                 null default 0,
   constraint PK_LOG_IP2COUNTRY primary key (i2c_id)
)
;
/*==============================================================*/
/* Index: idx_i2c_id                                            */
/*==============================================================*/
create unique index idx_i2c_id on log_ip2country (
i2c_id
);

/*==============================================================*/
/* Index: idx_i2c_ccd_id                                        */
/*==============================================================*/
create  index idx_i2c_ccd_id on log_ip2country (
ccd_id
);

/*==============================================================*/
/* Table: sec_group                                             */
/*==============================================================*/
create table sec_group (
   grp_id               INT8                 not null,
   grp_shortdescription VARCHAR(40)          not null,
   grp_longdescription  VARCHAR(1000)        null,
   version              INT4                 not null default 0,
   constraint PK_SEC_GROUP primary key (grp_id)
)
;
/*==============================================================*/
/* Index: idx_grp_id                                            */
/*==============================================================*/
create unique index idx_grp_id on sec_group (
grp_id
);

/*==============================================================*/
/* Index: idx_grp_shortdescription                              */
/*==============================================================*/
create unique index idx_grp_shortdescription on sec_group (
grp_shortdescription
);

/*==============================================================*/
/* Table: sec_groupright                                        */
/*==============================================================*/
create table sec_groupright (
   gri_id               INT8                 not null,
   grp_id               INT8                 not null,
   rig_id               INT8                 not null,
   version              INT4                 not null default 0,
   constraint PK_SEC_GROUPRIGHT primary key (gri_id)
)
;
/*==============================================================*/
/* Index: idx_gri_id                                            */
/*==============================================================*/
create unique index idx_gri_id on sec_groupright (
gri_id
);

/*==============================================================*/
/* Index: idx_gri_grprig                                        */
/*==============================================================*/
create unique index idx_gri_grprig on sec_groupright (
grp_id,
rig_id
);

/*==============================================================*/
/* Table: sec_loginlog                                          */
/*==============================================================*/
create table sec_loginlog (
   lgl_id               INT8                 not null,
   i2c_id               INT8                 null,
   lgl_loginname        VARCHAR(50)          not null,
   lgl_logtime          TIMESTAMP            not null,
   lgl_ip               VARCHAR(19)          null,
   lgl_browsertype      VARCHAR(40)          null,
   lgl_status_id        INT4                 not null,
   lgl_sessionid        VARCHAR(50)          null,
   version              INT4                 not null default 0,
   constraint PK_SEC_LOGINLOG primary key (lgl_id)
)
;
/*==============================================================*/
/* Index: idx_lgl_id                                            */
/*==============================================================*/
create unique index idx_lgl_id on sec_loginlog (
lgl_id
);

/*==============================================================*/
/* Index: idx_lgl_logtime                                       */
/*==============================================================*/
create  index idx_lgl_logtime on sec_loginlog (
lgl_logtime
);

/*==============================================================*/
/* Index: idx_lgl_i2c_id                                        */
/*==============================================================*/
create  index idx_lgl_i2c_id on sec_loginlog (
i2c_id
);

/*==============================================================*/
/* Table: sec_right                                             */
/*==============================================================*/
create table sec_right (
   rig_id               INT8                 not null,
   rig_type             INT4                 null default 1,
   rig_name             VARCHAR(50)          not null,
   version              INT4                 not null default 0,
   constraint PK_SEC_RIGHT primary key (rig_id)
)
;
/*==============================================================*/
/* Index: idx_rig_id                                            */
/*==============================================================*/
create unique index idx_rig_id on sec_right (
rig_id
);

/*==============================================================*/
/* Index: idx_rig_type                                          */
/*==============================================================*/
create  index idx_rig_type on sec_right (
rig_type
);

/*==============================================================*/
/* Index: idx_rig_name                                          */
/*==============================================================*/
create unique index idx_rig_name on sec_right (
rig_name
);

/*==============================================================*/
/* Table: sec_role                                              */
/*==============================================================*/
create table sec_role (
   rol_id               INT8                 not null,
   rol_shortdescription VARCHAR(30)          not null,
   rol_longdescription  VARCHAR(1000)        null,
   version              INT4                 not null default 0,
   constraint PK_SEC_ROLE primary key (rol_id)
)
;
/*==============================================================*/
/* Index: idx_role_id                                           */
/*==============================================================*/
create unique index idx_role_id on sec_role (
rol_id
);

/*==============================================================*/
/* Index: idx_role_shortdescription                             */
/*==============================================================*/
create unique index idx_role_shortdescription on sec_role (
rol_shortdescription
);

/*==============================================================*/
/* Table: sec_rolegroup                                         */
/*==============================================================*/
create table sec_rolegroup (
   rlg_id               INT8                 not null,
   grp_id               INT8                 not null,
   rol_id               INT8                 not null,
   version              INT4                 not null default 0,
   constraint PK_SEC_ROLEGROUP primary key (rlg_id)
)
;
/*==============================================================*/
/* Index: idx_rlg_id                                            */
/*==============================================================*/
create unique index idx_rlg_id on sec_rolegroup (
rlg_id
);

/*==============================================================*/
/* Index: idx_rlg_grprol                                        */
/*==============================================================*/
create unique index idx_rlg_grprol on sec_rolegroup (
grp_id,
rol_id
);

/*==============================================================*/
/* Table: sec_user                                              */
/*==============================================================*/
create table sec_user (
   usr_id               INT8                 not null,
   usr_loginname        VARCHAR(50)          not null,
   usr_password         VARCHAR(50)          not null,
   usr_lastname         VARCHAR(50)          null,
   usr_firstname        VARCHAR(50)          null,
   usr_email            VARCHAR(200)         null,
   usr_locale           VARCHAR(5)           null,
   usr_enabled          BOOL                 not null default FALSE,
   usr_accountNonExpired BOOL                 not null default TRUE,
   usr_credentialsNonExpired BOOL                 not null default TRUE,
   usr_accountNonLocked BOOL                 not null default TRUE,
   usr_token            VARCHAR(20)          null,
   version              INT4                 not null default 0,
   constraint PK_SEC_USER primary key (usr_id)
)
;
/*==============================================================*/
/* Index: idx_usr_id                                            */
/*==============================================================*/
create unique index idx_usr_id on sec_user (
usr_id
);

/*==============================================================*/
/* Index: idx_usr_loginname                                     */
/*==============================================================*/
create unique index idx_usr_loginname on sec_user (
usr_loginname
);

/*==============================================================*/
/* Table: sec_userrole                                          */
/*==============================================================*/
create table sec_userrole (
   urr_id               INT8                 not null,
   usr_id               INT8                 not null,
   rol_id               INT8                 not null,
   version              INT4                 not null default 0,
   constraint PK_SEC_USERROLE primary key (urr_id)
)
;
/*==============================================================*/
/* Index: idx_urr_id                                            */
/*==============================================================*/
create unique index idx_urr_id on sec_userrole (
urr_id
);

/*==============================================================*/
/* Index: idx_urr_usrrol                                        */
/*==============================================================*/
create unique index idx_urr_usrrol on sec_userrole (
usr_id,
rol_id
);

/*==============================================================*/
/* Table: sys_countrycode                                       */
/*==============================================================*/
create table sys_countrycode (
   ccd_id               INT8                 not null,
   ccd_name             VARCHAR(48)          null,
   ccd_code2            VARCHAR(2)           not null,
   version              INT4                 null default 0,
   constraint PK_SYS_COUNTRYCODE primary key (ccd_id)
)
;
/*==============================================================*/
/* Index: idx_ccd_id                                            */
/*==============================================================*/
create unique index idx_ccd_id on sys_countrycode (
ccd_id
);

/*==============================================================*/
/* Index: idx_ccd_code2                                         */
/*==============================================================*/
create unique index idx_ccd_code2 on sys_countrycode (
ccd_code2
);

/*==============================================================*/
/* Table: sys_ip4country                                        */
/*==============================================================*/
create table sys_ip4country (
   i4co_id              INT8                 not null,
   i4co_ip              INT8                 null,
   i4co_ccd_id          INT8                 null,
   version              INT4                 null default 0,
   constraint PK_SYS_IP4COUNTRY primary key (i4co_id)
)
;
/*==============================================================*/
/* Index: idx_i4co_id                                           */
/*==============================================================*/
create unique index idx_i4co_id on sys_ip4country (
i4co_id
);

/*==============================================================*/
/* Index: idx_i4co_ip                                           */
/*==============================================================*/
create  index idx_i4co_ip on sys_ip4country (
i4co_ip
);

/*==============================================================*/
/* Index: idx_i4co_ccd_id                                       */
/*==============================================================*/
create  index idx_i4co_ccd_id on sys_ip4country (
i4co_ccd_id
);


alter table auftrag
   add constraint ref_auf_to_kun foreign key (auf_kun_id)
      references kunde (kun_id)
      on delete cascade on update cascade;

alter table auftragposition
   add constraint ref_aup_to_art foreign key (art_id)
      references artikel (art_id)
      on delete restrict on update restrict;

alter table auftragposition
   add constraint ref_aup_to_auf foreign key (aup_auf_id)
      references auftrag (auf_id)
      on delete cascade on update cascade;

alter table kunde
   add constraint ref_kun_to_bra foreign key (kun_bra_id)
      references Branche (bra_id)
      on delete restrict on update restrict;

alter table kunde
   add constraint ref_kun_to_fil foreign key (kun_fil_id)
      references filiale (fil_id)
      on delete cascade on update cascade;

alter table log_ip2country
   add constraint ref_i2c_to_ccd foreign key (ccd_id)
      references sys_countrycode (ccd_id)
      on delete restrict on update restrict;

alter table sec_groupright
   add constraint ref_gri_to_grp foreign key (grp_id)
      references sec_group (grp_id)
      on delete restrict on update restrict;

alter table sec_groupright
   add constraint ref_gri_to_rig foreign key (rig_id)
      references sec_right (rig_id)
      on delete restrict on update restrict;

alter table sec_loginlog
   add constraint ref_lgl_to_i2c foreign key (i2c_id)
      references log_ip2country (i2c_id)
      on delete cascade on update cascade;

alter table sec_rolegroup
   add constraint ref_rlg_to_grp foreign key (grp_id)
      references sec_group (grp_id)
      on delete restrict on update restrict;

alter table sec_rolegroup
   add constraint ref_rlg_to_rol foreign key (rol_id)
      references sec_role (rol_id)
      on delete restrict on update restrict;

alter table sec_userrole
   add constraint ref_aut_to_rol foreign key (rol_id)
      references sec_role (rol_id)
      on delete restrict on update restrict;

alter table sec_userrole
   add constraint ref_aut_to_usr foreign key (usr_id)
      references sec_user (usr_id)
      on delete restrict on update restrict;

      
/******************** TEST DATA ********************/



/******************** Filiale Daten ********************/
INSERT INTO FILIALE (FIL_ID, FIL_NR, FIL_BEZEICHNUNG,FIL_NAME1,FIL_NAME2,FIL_ORT,VERSION) values
(1,'0001','Filiale Muenchen','Hoermann Gmbh','Personaldienstleistungen','Muenchen',0),
(2,'0002','Filiale Berlin',  'Hoermann Gmbh','Personaldienstleistungen','Berlin',  0);

/******************** Security: USERS ********************/  
INSERT INTO SEC_USER (USR_ID, USR_LOGINNAME, USR_PASSWORD, USR_LASTNAME, USR_FIRSTNAME, USR_EMAIL, USR_LOCALE, USR_ENABLED, USR_ACCOUNTNONEXPIRED, USR_CREDENTIALSNONEXPIRED, USR_ACCOUNTNONLOCKED, USR_TOKEN,  VERSION) values 
(10, 'guest', 'guest', 'guestFirstname', 'guestlastname', 'guest@web.de', NULL, true, true, true, true, null, 0),
(11, 'admin', 'admin', 'Visor', 'Super', 'admin@web.de', NULL, true, true, true, true, null, 0),
(12, 'user1', 'user1', 'Kingsley', 'Ben', 'B.Kingsley@web.de', NULL, true, true, true, true, null, 0),
(13, 'headoffice', 'headoffice', 'Willis', 'Bruce', 'B.Willis@web.de', NULL, true, true, true, true, null, 0),
(14, 'user2', 'user2', 'Kingdom', 'Marta', 'M.Kingdom@web.de', NULL, true, true, true, true, null, 0);

/******************** Security: ROLES ********************/  
INSERT INTO SEC_ROLE (ROL_ID, ROL_SHORTDESCRIPTION, ROL_LONGDESCRIPTION, VERSION) values
(10000,'ROLE_ADMIN','Administrator Role', 0),
(10002,'ROLE_OFFICE_ALL_RIGHTS','Office User role with all rights granted.', 0),
(10003,'ROLE_GUEST','Guest Role', 0),
(10004,'ROLE_OFFICE_ONLY_VIEW','Office user with rights that granted only view of data.', 0),
(10005,'ROLE_HEADOFFICE_USER','Headoffice User Role', 0);


/******************** Security: USER-ROLES ********************/  
/* Guest account authorities */
INSERT INTO SEC_USERROLE (URR_ID, USR_ID, ROL_ID, VERSION) values
(11000, 10, 10003, 0),
/* User1 Usr-Id: 12 */
(11001, 12, 10002, 0),
/* Headoffice user account authorities */
(11010, 13, 10005, 0);

/* Admin Usr-ID: 11 */
INSERT INTO SEC_USERROLE (URR_ID, USR_ID, ROL_ID, VERSION) values
(11003, 11, 10000, 0),
(11005, 11, 10002, 0),
(11006, 11, 10003, 0),
(11008, 11, 10004, 0),
(11009, 11, 10005, 0),
/* User2 Usr-ID: 14 */
(11007, 14, 10004, 0);

/******************** Security: SEC_GROUPS ********************/  
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION) values
(13001, 'Headoffice Supervisor Group', 'kjhf ff hgfd', 0),
(13002, 'Admin Group - user accounts', 'create/modify user accounts', 0),
(13003, 'Guest Group', 'Minimal Rights for the guests', 0),
(13004, 'Admin Group - user rights', 'edit/modify user rights', 0),
/* Customers */
(13000, 'Customers_View',      'Allow to  view customers data', 0),
(13008, 'Customers_New',       'Allow create new customers', 0),
(13006, 'Customers_Edit',      'Allow editing of customers', 0),
(13007, 'Customers_Delete',    'Allow deleting of customers', 0),
/* Orders */
(13010, 'Orders_View',         'Allow to view orders data', 0),
(13011, 'Orders_New',          'Allow create new orders', 0),
(13012, 'Orders_Edit',         'Allow editing of orders', 0),
(13013, 'Orders_Delete',       'Allow deleting of orders', 0),
/* Branches */
(13020, 'Branch_View',         'Allow to view branches data', 0),
(13021, 'Branch_New',          'Allow create new branches', 0),
(13022, 'Branch_Edit',         'Allow editing of branches', 0),
(13023, 'Branch_Delete',       'Allow deleting of branches', 0),
/* Articles */
(13030, 'Articles_View',       'Allow to view articles data', 0),
(13031, 'Articles_New',        'Allow create new articles', 0),
(13032, 'Articles_Edit',       'Allow editing of articles', 0),
(13033, 'Articles_Delete',     'Allow deleting of articles', 0),
/* Offices */
(13040, 'Offices_View',        'Allow to view offices data', 0),
(13041, 'Offices_New',         'Allow create new offices', 0),
(13042, 'Offices_Edit',        'Allow editing of offices', 0),
(13043, 'Offices_Delete',      'Allow deleting of offices', 0),
/* Users */
(13060, 'User_View_UsersOnly', 'Allow to view own user data.', 0),
(13061, 'User_Edit_UsersOnly', 'Allow to edit own user data.', 0),
(13062, 'Users_View',          'Allow to view all users data.', 0),
(13063, 'Users_New',           'Allow create new users', 0),
(13064, 'Users_Edit',          'Allow editing of users', 0),
(13065, 'Users_Delete',        'Allow deleting of users', 0),
(13066, 'Users_Search',        'Allow searching of users', 0),
/* secGroup */
(13070, 'Security_Groups',     'Allow to view the securityGroups Dialog', 0),
/* secRole */
(13071, 'Security_Roles',      'Allow to view the securityRoles Dialog', 0),
/* secRight */
(13072, 'Security_Rights',     'Allow to view the securityRights Dialog', 0);


/******************** Security: SEC_ROLE-GROUPS ********************/  
/* ROLE_OFFICE_ALL_RIGHTS */
/* Group: Customers_View */
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION) values 
(12000, 13000, 10002, 0),
/* Group: Customers_New */
(12001, 13008, 10002, 0),
/*  Group: Customers_Edit */
(12002, 13006, 10002, 0),
/*  Group: Customers_Delete */
(12003, 13007, 10002, 0),
/*  Group: Orders_View */
(12004, 13010, 10002, 0),
/*  Group: Orders_New */
(12005, 13011, 10002, 0),
/*  Group: Orders_Edit */
(12006, 13012, 10002, 0),
/*  Group: Orders_Delete */
(12007, 13013, 10002, 0),
/*  Group: User_View_UsersOnly */
(12008, 13060, 10002, 0),
/*  Group: User_Edit_UsersOnly */
(12009, 13061, 10002, 0),
/* ROLE_OFFICE_ONLY_VIEW */
/* Group: Customers_View */
(12010, 13000, 10004, 0),
/*  Group: Orders_View */
(12011, 13010, 10004, 0),
/*  Group: User_View_UsersOnly */
(12012, 13060, 10004, 0),
/* ROLE_GUEST */
(12020, 13003, 10003, 0),
/* ROLE_ADMIN */
(12050, 13002, 10000, 0),
(12051, 13000, 10000, 0),
(12052, 13001, 10000, 0),
(12053, 13003, 10000, 0),
(12054, 13004, 10000, 0),
(12055, 13006, 10000, 0),
(12056, 13007, 10000, 0),
(12057, 13008, 10000, 0),
(12058, 13010, 10000, 0),
(12059, 13011, 10000, 0),
(12060, 13012, 10000, 0),
(12061, 13013, 10000, 0),
(12062, 13020, 10000, 0),
(12063, 13021, 10000, 0),
(12064, 13022, 10000, 0),
(12065, 13023, 10000, 0),
(12066, 13030, 10000, 0),
(12067, 13031, 10000, 0),
(12068, 13032, 10000, 0),
(12069, 13033, 10000, 0),
(12070, 13040, 10000, 0),
(12071, 13041, 10000, 0),
(12072, 13042, 10000, 0),
(12073, 13043, 10000, 0),
/* Group: Users_View */
(12074, 13062, 10000, 0),
/* Group: Users_New */
(12075, 13063, 10000, 0),
/* Group: Users_Edit */
(12076, 13064, 10000, 0),
/* Group: Users_Delete */
(12077, 13065, 10000, 0),
/* Group: Users_Search */
(12078, 13066, 10000, 0),

/* ROLE_HEADOFFICE_USER */
/* Group: Branch_View */
(12100, 13020, 10005, 0),
/* Group: Branch_New */
(12101, 13021, 10005, 0),
/* Group: Branch_Edit */
(12102, 13022, 10005, 0),
/* Group: Branch_Delete */
(12103, 13023, 10005, 0),
/* Group: Articles_View */
(12104, 13030, 10005, 0),
/* Group: Articles_New */
(12105, 13031, 10005, 0),
/* Group: Articles_Edit */
(12106, 13032, 10005, 0),
/* Group: Articles_Delete */
(12107, 13033, 10005, 0),
/* Group: Offices_View */
(12108, 13040, 10005, 0),
/* Group: Offices_New */
(12109, 13041, 10005, 0),
/* Group: Offices_Edit */
(12110, 13042, 10005, 0),
/* Group: Offices_Delete */
(12111, 13043, 10005, 0),
/*  Group: User_View_UsersOnly */
(12115, 13060, 10005, 0),
/*  Group: User_Edit_UsersOnly */
(12116, 13061, 10005, 0),
     
/* ROLE_ADMIN */
/* Group: Security_Groups */
(12117, 13070, 10000, 0),
/* Group: Security_Roles */
(12118, 13071, 10000, 0),
/* Group: Security_Rights */
(12119, 13072, 10000, 0);


/******************** Security: SEC_RIGHTS ********************/  
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(15000, 1, 'menuCat_OfficeData', 0),
(15001, 2, 'menuItem_OfficeData_Customers', 0),
(15002, 2, 'menuItem_OfficeData_Orders', 0),
(15003, 1, 'menuCat_MainData', 0),
(15004, 2, 'menuItem_MainData_ArticleItems', 0),
(15005, 2, 'menuItem_MainData_Branch', 0),
(15006, 2, 'menuItem_MainData_Office', 0),
(15007, 1, 'menuCat_Administration', 0),
(15008, 2, 'menuItem_Administration_Users', 0),
(15009, 2, 'menuItem_Administration_UserRoles', 0),
(15010, 2, 'menuItem_Administration_Roles', 0),
(15011, 2, 'menuItem_Administration_RoleGroups', 0),
(15012, 2, 'menuItem_Administration_Groups', 0),
(15013, 2, 'menuItem_Administration_GroupRights', 0),
(15014, 2, 'menuItem_Administration_Rights', 0),
(15015, 1, 'menuCat_UserRights', 0),
(15016, 2, 'menuItem_Administration_LoginsLog', 0),
(15017, 2, 'menuItem_Administration_HibernateStats', 0),
(15018, 2, 'menu_Item_Calendar', 0),

/* Pages = Type(0) */
/* --> Page Customer */
(15100, 0, 'window_customerList', 0),
(15101, 0, 'window_customerDialog', 0),
/* --> Page Orders */
(15102, 0, 'orderListWindow', 0),
(15103, 0, 'orderDialogWindow', 0),
/* --> Page Articles */
(15104, 0, 'windowArticlesList', 0),
(15105, 0, 'window_ArticlesDialog', 0),
/* --> Page Branches */
(15106, 0, 'window_BranchesList', 0),
(15107, 0, 'window_BranchesDialog', 0),
/* --> Page Offices */
(15108, 0, 'window_OfficeList', 0),
(15109, 0, 'window_OfficeDialog', 0),
/* --> Page Admin - Users */
(15110, 0, 'page_Admin_UserList', 0),
(15111, 0, 'page_Admin_UserDialog', 0),
/* --> Page Admin - UserRoles */
(15112, 0, 'page_Security_UserRolesList', 0),
(15113, 0, 'page_Security_RolesList', 0),
/* --> Page Admin - Roles */
(15114, 0, 'page_Security_RolesDialog', 0),
/* --> Page Admin - RoleGroups */
(15115, 0, 'page_Security_RoleGroupsList', 0),
/* --> Page Admin - Groups */
(15116, 0, 'page_Security_GroupsList', 0),
(15117, 0, 'page_Security_GroupsDialog', 0),
/* --> Page Admin - GroupRights */
(15118, 0, 'page_Security_GroupRightsList', 0),
/* --> Page Admin - Rights */
(15119, 0, 'page_Security_RightsList', 0),
(15120, 0, 'page_Security_RightsDialog', 0),
/* --> Page Login Log */
(15121, 0, 'page_Admin_LoginLogList', 0),
/* --> Nachtrag Page Orders */
(15122, 0, 'orderPositionDialogWindow', 0),

/* Tabs = Type(5) */
(15200, 5, 'tab_CustomerDialog_Address', 0),
(15201, 5, 'tab_CustomerDialog_Chart', 0),
(15202, 5, 'tab_CustomerDialog_Orders', 0),
(15203, 5, 'tab_CustomerDialog_Memos', 0),

/* Components = Type(6) */
/* --> CustomerList BUTTON */
(15300, 6, 'button_CustomerList_btnHelp', 0),
(15301, 6, 'button_CustomerList_NewCustomer', 0),
(15302, 6, 'button_CustomerList_CustomerFindDialog', 0),
(15303, 6, 'button_CustomerList_PrintList', 0),
/* --> CustomerDialog BUTTON */
(15305, 6, 'button_CustomerDialog_btnHelp', 0),
(15306, 6, 'button_CustomerDialog_btnNew', 0),
(15307, 6, 'button_CustomerDialog_btnEdit', 0),
(15308, 6, 'button_CustomerDialog_btnDelete', 0),
(15309, 6, 'button_CustomerDialog_btnSave', 0),
(15310, 6, 'button_CustomerDialog_btnClose', 0),
(15311, 6, 'button_CustomerDialog_btnCancel', 0),
/* --> OrderList BUTTON */
(15400, 6, 'button_OrderList_btnHelp', 0),
(15401, 6, 'button_OrderList_NewOrder', 0),
/* --> OrderDialog BUTTON */
(15410, 6, 'button_OrderDialog_btnHelp', 0),
(15411, 6, 'button_OrderDialog_btnNew', 0),
(15412, 6, 'button_OrderDialog_btnEdit', 0),
(15413, 6, 'button_OrderDialog_btnDelete', 0),
(15414, 6, 'button_OrderDialog_btnSave', 0),
(15415, 6, 'button_OrderDialog_btnClose', 0),
(15416, 6, 'button_OrderDialog_PrintOrder', 0),
(15417, 6, 'button_OrderDialog_NewOrderPosition', 0),
/* --> OrderPositionDialog BUTTON */
(15430, 6, 'button_OrderPositionDialog_btnHelp', 0),
(15431, 6, 'button_OrderPositionDialog_PrintOrderPositions', 0),
(15432, 6, 'button_OrderPositionDialog_btnNew', 0),
(15433, 6, 'button_OrderPositionDialog_btnEdit', 0),
(15434, 6, 'button_OrderPositionDialog_btnDelete', 0),
(15435, 6, 'button_OrderPositionDialog_btnSave', 0),
(15436, 6, 'button_OrderPositionDialog_btnClose', 0),
/* USERS */
/* --> userListWindow */
(15470, 0, 'userListWindow', 0),
/* --> userListWindow BUTTONS*/
(15471, 6, 'button_UserList_btnHelp', 0),
(15472, 6, 'button_UserList_NewUser', 0),
(15473, 6, 'button_UserList_PrintUserList', 0),
(15474, 6, 'button_UserList_SearchLoginname', 0),
(15475, 6, 'button_UserList_SearchLastname', 0),
(15476, 6, 'button_UserList_SearchEmail', 0),
(15477, 6, 'checkbox_UserList_ShowAll', 0),
/* --> Mehode onDoubleClick Listbox */
(15778, 3, 'UserList_listBoxUser.onDoubleClick', 0),
/* --> userDialogWindow */
(15480, 0, 'userDialogWindow', 0),
/* --> userDialogWindow BUTTONS*/
(15481, 6, 'button_UserDialog_btnHelp', 0),
(15482, 6, 'button_UserDialog_btnNew', 0),
(15483, 6, 'button_UserDialog_btnEdit', 0),
(15484, 6, 'button_UserDialog_btnDelete', 0),
(15485, 6, 'button_UserDialog_btnSave', 0),
(15486, 6, 'button_UserDialog_btnClose', 0),
(15492, 6, 'button_UserDialog_btnCancel', 0),
/* --> userDialogWindow Special Admin Panels */
(15487, 6, 'panel_UserDialog_Status', 0),
(15488, 6, 'panel_UserDialog_SecurityToken', 0),
/* --> userListWindow Search panel */
(15489, 6, 'hbox_UserList_SearchUsers', 0),
/* Tab Details */
(15490, 6, 'tab_UserDialog_Details', 0),
(15491, 3, 'data_SeeAllUserData', 0),

/* BRANCHES */
/* branchListWindow Buttons*/
/* --> button_BranchList_btnHelp */
(15502, 0, 'button_BranchMain_btnPrint', 0),
(15503, 0, 'button_BranchMain_Search_BranchName', 0),
/* branchDialogWindow BUTTONS */
(15510, 6, 'button_BranchMain_btnHelp', 0),
(15511, 6, 'button_BranchMain_btnNew', 0),
(15512, 6, 'button_BranchMain_btnEdit', 0),
(15513, 6, 'button_BranchMain_btnDelete', 0),
(15514, 6, 'button_BranchMain_btnSave', 0),
(15515, 6, 'button_BranchMain_btnClose', 0),
/* new: sge:07/18/2011  navigation buttons */
(15516, 6, 'button_BranchMain_btnCancel', 0),
(15517, 6, 'button_BranchMain_btnFirst', 0),
(15518, 6, 'button_BranchMain_btnPrevious', 0),
(15519, 6, 'button_BranchMain_btnNext', 0),
(15520, 6, 'button_BranchMain_btnLast', 0),
/* ARTICLES */
/* window_ArticlesList Buttons*/
(15530, 6, 'button_ArticlesList_btnHelp', 0),
(15531, 6, 'button_ArticleList_NewArticle', 0),
(15533, 6, 'button_ArticleList_SearchArticleID', 0),
(15534, 6, 'button_ArticleList_SearchName', 0),
/* window_ArticlesDialog Buttons*/
/* (15532, 6, 'button_ArticleList_PrintList', 0), */
(15532, 6, 'button_ArticlesDialog_btnPrint', 0),
(15540, 6, 'button_ArticlesDialog_btnHelp', 0),
(15541, 6, 'button_ArticlesDialog_btnNew', 0),
(15542, 6, 'button_ArticlesDialog_btnEdit', 0),
(15543, 6, 'button_ArticlesDialog_btnDelete', 0),
(15544, 6, 'button_ArticlesDialog_btnSave', 0),
(15545, 6, 'button_ArticlesDialog_btnClose', 0),
/* new: sge:07/18/2011  navigation buttons */
(15546, 6, 'button_ArticlesDialog_btnFirst', 0),
(15547, 6, 'button_ArticlesDialog_btnPrevious', 0),
(15548, 6, 'button_ArticlesDialog_btnNext', 0),
(15549, 6, 'button_ArticlesDialog_btnLast', 0),
(15550, 6, 'button_ArticlesDialog_btnCancel', 0),

/* OFFICES */
/* window_OfficeList Buttons*/
/* --> button_BranchList_btnHelp */
(15602, 6, 'button_OfficeMain_btnPrint', 0),
(15603, 6, 'button_OfficeList_SearchNo', 0),
(15604, 6, 'button_OfficeList_SearchName', 0),
(15605, 6, 'button_OfficeList_SearchCity', 0),
/* window_OfficeDialog BUTTONS */
(15611, 6, 'button_OfficeMain_btnHelp', 0),
(15612, 6, 'button_OfficeMain_btnNew', 0),
(15613, 6, 'button_OfficeMain_btnEdit', 0),
(15614, 6, 'button_OfficeMain_btnDelete', 0),
(15615, 6, 'button_OfficeMain_btnSave', 0),
(15616, 6, 'button_OfficeMain_btnClose', 0),
/* new: sge:07/18/2011  navigation buttons */
(15617, 6, 'button_OfficeMain_btnCancel', 0),
(15618, 6, 'button_OfficeMain_btnFirst', 0),
(15619, 6, 'button_OfficeMain_btnPrevious', 0),
(15620, 6, 'button_OfficeMain_btnNext', 0),
(15621, 6, 'button_OfficeMain_btnLast', 0),

/* Method/Event = Type(3) */
/* --> CustomerList BUTTON */
(15700, 3, 'CustomerList_listBoxCustomer.onDoubleClick', 0),
/* --> secRoleDialogWindow */
(15750, 0, 'secRoleDialogWindow', 0),
/* --> secRoleDialogWindow BUTTONS*/
(15751, 6, 'button_SecRoleDialog_btnHelp', 0),
(15752, 6, 'button_SecRoleDialog_btnNew', 0),
(15753, 6, 'button_SecRoleDialog_btnEdit', 0),
(15754, 6, 'button_SecRoleDialog_btnDelete', 0),
(15755, 6, 'button_SecRoleDialog_btnSave', 0),
(15756, 6, 'button_SecRoleDialog_btnClose', 0),
(15757, 6, 'button_SecRoleDialog_btnCancel', 0),
/* --> secGroupDialogWindow */
(15760, 0, 'secGroupDialogWindow', 0),
/* --> secGroupDialogWindow BUTTONS*/
(15761, 6, 'button_SecGroupDialog_btnHelp', 0),
(15762, 6, 'button_SecGroupDialog_btnNew', 0),
(15763, 6, 'button_SecGroupDialog_btnEdit', 0),
(15764, 6, 'button_SecGroupDialog_btnDelete', 0),
(15765, 6, 'button_SecGroupDialog_btnSave', 0),
(15766, 6, 'button_SecGroupDialog_btnClose', 0),
(15767, 6, 'button_SecGroupDialog_btnCancel', 0),
/* --> secRightDialogWindow */
(15770, 0, 'secRightDialogWindow', 0),
/* --> secRightDialogWindow BUTTONS*/
(15771, 6, 'button_SecRightDialog_btnHelp', 0),
(15772, 6, 'button_SecRightDialog_btnNew', 0),
(15773, 6, 'button_SecRightDialog_btnEdit', 0),
(15774, 6, 'button_SecRightDialog_btnDelete', 0),
(15775, 6, 'button_SecRightDialog_btnSave', 0),
(15776, 6, 'button_SecRightDialog_btnClose', 0),
(15777, 6, 'button_SecRightDialog_btnCancel', 0);
/******************** Security: SEC_GROUP-RIGHTS ********************/  
/* Headoffice Supervisor Group*/
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(14003, 13001, 15003, 0),
(14004, 13001, 15004, 0),
(14005, 13001, 15005, 0),
(14006, 13001, 15006, 0),
/* Administration Group*/
(14007, 13002, 15007, 0),
(14008, 13002, 15008, 0),
(14009, 13002, 15009, 0),
(14010, 13002, 15010, 0),
(14011, 13002, 15011, 0),
(14012, 13002, 15012, 0),
(14013, 13002, 15013, 0),
(14014, 13002, 15014, 0),
(14015, 13002, 15015, 0),
(14016, 13002, 15016, 0),
(14017, 13002, 15017, 0),

/* New */
/* Group: Customers_View */
/* Right: menuCat_OfficeData */
(14200, 13000, 15000, 0),
/* Right: menuItem_OfficeData_Customers */
(14201, 13000, 15001, 0),
/* Right: customerListWindow */
(14202, 13000, 15100, 0),
/* Right: button_CustomerList_Help */
(14203, 13000, 15305, 0),
/* Right: CustomerList_listBoxCustomer.onDoubleClick */
(14204, 13000, 15700, 0),

/* Right: customerDialogWindow */
(14205, 13000, 15101, 0),
/* Right: button_CustomerDialog_btnClose */
(14206, 13000, 15310, 0),
/* Right: button_CustomerList_Help */
(14207, 13000, 15300, 0),
/* Right: button_CustomerList_CustomerFindDialog */
(14208, 13000, 15302, 0),
/* Right: button_CustomerList_PrintList */
(14209, 13000, 15303, 0),

/* Tab tab_CustomerDialog_Address */
(14210, 13000, 15200, 0),
/* Tab tab_CustomerDialog_Addition */
(14211, 13000, 15201, 0),
/* Tab tab_CustomerDialog_Orders */
(14212, 13000, 15202, 0),
/* Tab tab_CustomerDialog_Memos */
(14213, 13000, 15203, 0),

/* Group: Customers_New */
/* Right: customerListWindow */
(14230, 13008, 15100, 0),
/* Right: button_CustomerList_NewCustomer */
(14231, 13008, 15301, 0),
/* Right: customerDialogWindow */
(14232, 13008, 15101, 0),
/* Right: button_CustomerDialog_btnClose */
(14233, 13008, 15310, 0),
/* Right: button_CustomerDialog_btnNew */
(14234, 13008, 15306, 0),
/* Right: button_CustomerDialog_btnEdit */
(14235, 13008, 15307, 0),
/* Right: button_CustomerDialog_btnSave */
(14236, 13008, 15309, 0),
/* Right: button_CustomerDialog_btnCancel */
(14237, 13008, 15311, 0),

/* Group: Customers_Edit */
/* Right: customerListWindow */
(14240, 13006, 15100, 0),
/* Right: customerDialogWindow */
(14241, 13006, 15101, 0),
/* Right: button_CustomerDialog_btnClose */
(14242, 13006, 15310, 0),
/* Right: button_CustomerDialog_btnEdit */
(14243, 13006, 15307, 0),
/* Right: button_CustomerDialog_btnSave */
(14244, 13006, 15309, 0),
/* Right: button_CustomerDialog_btnCancel */
(14245, 13006, 15311, 0),

/* Group: Customers_Delete */
/* Right: customerListWindow */
(14250, 13007, 15100, 0),
/* Right: customerDialogWindow */
(14251, 13007, 15101, 0),
/* Right: button_CustomerDialog_btnClose */
(14252, 13007, 15310, 0),
/* Right: button_CustomerDialog_btnDelete */
(14253, 13007, 15308, 0),

/*----------------------------------------------------------*/
/* Group: Orders_View */
/* Right: menuCat_OfficeData */
(14300, 13010, 15000, 0),
/* Right: menuItem_OfficeData_Orders */
(14301, 13010, 15002, 0),
/* Right: orderListWindow */
(14302, 13010, 15102, 0),
/* Right: button_OrderList_btnHelp */
(14303, 13010, 15400, 0),
/* Right: CustomerList_listBoxCustomer.onDoubleClick */
(14304, 13010, 15700, 0),
/* Right: orderDialogWindow */
(14305, 13010, 15103, 0),
/* Right: button_OrderDialog_btnClose */
(14306, 13010, 15415, 0),
/* Right: button_OrderDialog_btnHelp */
(14307, 13010, 15410, 0),
/* Right: button_OrderDialog_PrintOrder */
(14308, 13010, 15416, 0),
/* Right: orderPositionDialogWindow */
(14309, 13010, 15122, 0),
/* Right: button_OrderPositionDialog_btnClose */
(14310, 13010, 15436, 0),
/* Right: button_OrderPositionDialog_btnHelp */
(14311, 13010, 15430, 0),
/* Right: button_OrderPositionDialog_PrintOrder */
(14312, 13010, 15431, 0),

/* Group: Orders_New */
/* Right: button_OrderList_NewOrder */
(14320, 13011, 15401, 0),
/* Right: button_OrderDialog_btnClose */
(14321, 13011, 15415, 0),
/* Right: button_OrderDialog_btnNew */
(14322, 13011, 15411, 0),
/* Right: button_OrderDialog_btnEdit */
(14323, 13011, 15412, 0),
/* Right: button_CustomerDialog_btnSave */
(14324, 13011, 15414, 0),
/* Right: button_OrderDialog_NewOrderPosition */
(14325, 13011, 15417, 0),
/* OrderPositions -------------------*/
/* Right: button_OrderPositionDialog_btnClose */
(14326, 13011, 15436, 0),
/* Right: button_OrderPositionDialog_btnNew */
(14327, 13011, 15432, 0),
/* Right: button_OrderPositionDialog_btnEdit */
(14328, 13011, 15433, 0),
/* Right: button_OrderPositionDialog_btnSave */
(14329, 13011, 15435, 0),

/* Group: Orders_Edit */
/* Right: button_OrderDialog_btnClose */
(14330, 13012, 15415, 0),
/* Right: button_OrderDialog_btnEdit */
(14331, 13012, 15412, 0),
/* Right: button_CustomerDialog_btnSave */
(14332, 13012, 15414, 0),
/* Right: button_OrderDialog_NewOrderPosition */
(14333, 13012, 15417, 0),

/* OrderPositions -------------------*/
/* Right: button_OrderPositionDialog_btnClose */
(14334, 13012, 15436, 0),
/* Right: button_OrderPositionDialog_btnEdit */
(14335, 13012, 15433, 0),
/* Right: button_OrderPositionDialog_btnSave */
(14336, 13012, 15435, 0),

/* Group: Orders_Delete */
/* Right: button_OrderDialog_btnClose */
(14340, 13013, 15415, 0),
/* Right: button_OrderDialog_btnDelete */
(14341, 13013, 15413, 0),
/* Right: button_OrderDialog_NewOrderPosition */
(14342, 13013, 15417, 0),

/* OrderPositions -------------------*/
/* Right: button_OrderPositionDialog_btnClose */
(14343, 13013, 15436, 0),
/* Right: button_OrderPositionDialog_btnDelete */
(14344, 13013, 15434, 0),

/* USERS ----------------- */
/* Group: User_View_UsersOnly */
/* Right: menuCat_Administration */
(14360, 13060, 15007, 0),
/* Right: menuItem_Administration_Users */
(14361, 13060, 15008, 0),
/* Right: userListWindow */
(14362, 13060, 15470, 0),
/* Right: button_UserList_btnHelp */
(14363, 13060, 15471, 0),
/* Right: UserList_listBoxUser.onDoubleClick */
(14364, 13060, 15778, 0),
/* Right: userDialogWindow */
(14365, 13060, 15480, 0),
/* Right: tab_UserDialog_Details */
(14366, 13060, 15490, 0),
/* Right: button_UserDialog_btnHelp */
(14367, 13060, 15481, 0),
/* Right: button_Dialog_btnClose */
(14368, 13060, 15486, 0),
/* Right: button_Dialog_btnCancel */
(14369, 13060, 15492, 0),

/* Group: User_Edit_UsersOnly */
/* Right: button_UserDialog_btnEdit */
(14370, 13061, 15483, 0),
/* Right: button_Dialog_btnSave */
(14371, 13061, 15485, 0),

/* Group: Users_View */
/* Right: menuCat_Administration */
(14380, 13062, 15007, 0),
/* Right: menuItem_Administration_Users */
(14381, 13062, 15008, 0),
/* Right: userListWindow */
(14382, 13062, 15470, 0),
/* Right: button_UserList_btnHelp */
(14383, 13062, 15471, 0),
/* Right: button_UserList_PrintUserList */
(14384, 13062, 15473, 0),
/* Right: UserList_listBoxUser.onDoubleClick */
(14385, 13062, 15778, 0),
/* Right: userDialogWindow */
(14386, 13062, 15480, 0),
/* Right: tab_UserDialog_Details */
(14387, 13062, 15490, 0),
/* Right: button_UserDialog_btnHelp */
(14388, 13062, 15481, 0),
/* Right: button_UserDialog_btnClose */
(14389, 13062, 15486, 0),
/* Right: panel_UserDialog_Status */
(14390, 13062, 15487, 0),
/* Right: panel_UserDialog_SecurityToken */
(14391, 13062, 15488, 0),
/* Right: data_SeeAllUserData */
(14392, 13062, 15491, 0),
/* Right: button_UserDialog_btnCancel */
(14393, 13062, 15492, 0),

/* Group: Users_New */
/* Right: button_UserList_NewUser */
(14395, 13063, 15472, 0),
/* Right: button_UserDialog_btnNew */
(14396, 13063, 15482, 0),
/* Right: button_UserDialog_btnEdit */
(14397, 13063, 15483, 0),
/* Right: button_UserDialog_btnSave */
(14398, 13063, 15485, 0),

/* Group: Users_Edit */
/* Right: button_UserDialog_btnEdit */
(14400, 13064, 15483, 0),
/* Right: button_UserDialog_btnSave */
(14401, 13064, 15485, 0),
/* Right: button_UserDialog_btnCancel */
(14402, 13064, 15492, 0),
/* Right: button_UserDialog_btnClose */
(14403, 13064, 15486, 0),

/* Group: Users_Delete */
/* Right: button_UserDialog_btnDelete */
(14410, 13065, 15484, 0),

/* Group: Users_Search */
/* Right: hbox_UserList_SearchUsers */
(14420, 13066, 15489, 0),

/* B r a n c h e s */
/* Group: Branch_View */
/* Right: menuCat_MainData */
(14500, 13020, 15003, 0),
/* Right: menuItem_MainData_Branch */
(14501, 13020, 15005, 0),
/* Right: page_BranchesList */
(14502, 13020, 15106, 0),
/* Right: button_BranchMain_btnPrint */
(14504, 13020, 15502, 0),
/* Right: button_BranchList_Search_BranchName */
(14505, 13020, 15503, 0),
/* Right: page_BranchesDialog */
(14507, 13020, 15107, 0),
/* Right: button_BranchDialog_btnHelp */
(14508, 13020, 15510, 0),
/* Right: button_BranchMain_btnClose */
(14509, 13020, 15515, 0),
/* Right: button_BranchMain_btnCancel */
(14510, 13020, 15516, 0),
/* new: sge:07/18/2011  navigation buttons */
/* Right: button_BranchMain_btnFirst */
(14496, 13020, 15517, 0),
/* Right: button_BranchMain_btnPrevious */
(14497, 13020, 15518, 0),
/* Right: button_BranchMain_btnNext */
(14498, 13020, 15519, 0),
/* Right: button_BranchMain_btnLast */
(14499, 13020, 15520, 0),

/* Group: Branch_New */
/* Right: button_BranchDialog_btnNew */
(14511, 13021, 15511, 0),
/* Right: button_BranchDialog_btnSave */
(14512, 13021, 15514, 0),
/* Right: button_BranchMain_btnClose */
(14513, 13021, 15515, 0),
/* Right: button_BranchMain_btnCancel */
(14514, 13021, 15516, 0),

/* Group: Branch_Edit */
/* Right: button_BranchDialog_btnEdit */
(14520, 13022, 15512, 0),
/* Right: button_BranchDialog_btnSave */
(14521, 13022, 15514, 0),
/* Right: button_BranchMain_btnClose */
(14522, 13022, 15515, 0),
/* Right: button_BranchMain_btnCancel */
(14523, 13022, 15516, 0),

/* Group: Branch_Delete */
/* Right: button_BranchDialog_btnDelete */
(14530, 13023, 15513, 0),

/* A r t i c l e s */
/* Group: Articles_View */
/* Right: menuCat_MainData */
(14540, 13030, 15003, 0),
/* Right: menuItem_MainData_ArticleItems */
(14541, 13030, 15004, 0),
/* Right: window_ArticlesList */
(14542, 13030, 15104, 0),
/* Right: button_ArticlesList_btnHelp */
(14543, 13030, 15530, 0),
/* Right: button_ArticlesDialog_btnPrint */
(14544, 13030, 15532, 0),
/* Right: window_ArticlesDialog */
(14545, 13030, 15105, 0),
/* Right: button_ArticlesDialog_btnHelp */
(14546, 13030, 15540, 0),
/* Right: button_ArticlesDialog_btnClose */
(14547, 13030, 15545, 0),
/* Right: button_ArticleList_SearchArticleID */
(14548, 13030, 15533, 0),
/* Right: button_ArticleList_SearchName */
(14549, 13030, 15534, 0),
/* new: sge:07/18/2011  navigation buttons */
/* Right: button_ArticlesDialog_btnCancel */
(14535, 13030, 15550, 0),
/* Right: button_ArticlesDialog_btnFirst */
(14536, 13030, 15546, 0),
/* Right: button_ArticlesDialog_btnPrevious */
(14537, 13030, 15547, 0),
/* Right: button_ArticlesDialog_btnNext */
(14538, 13030, 15548, 0),
/* Right: button_ArticlesDialog_btnLast */
(14539, 13030, 15549, 0),

/* Group: Articles_New */
/* Right: button_ArticleList_NewArticle */
(14550, 13031, 15531, 0),
/* Right: button_ArticlesDialog_btnNew */
(14551, 13031, 15541, 0),
/* Right: button_ArticlesDialog_btnSave */
(14552, 13031, 15544, 0),
/* Right: button_ArticlesDialog_btnCancel */
(14553, 13031, 15550, 0),
/* Right: button_ArticlesDialog_btnClose */
(14554, 13031, 15545, 0),

/* Group: Articles_Edit */
/* Right: button_ArticlesDialog_btnEdit */
(14555, 13032, 15542, 0),
/* Right: button_ArticlesDialog_btnSave */
(14556, 13032, 15544, 0),
/* Right: button_ArticlesDialog_btnCancel */
(14557, 13032, 15550, 0),
/* Right: button_ArticlesDialog_btnClose */
(14558, 13032, 15545, 0),

/* Group: Articles_Delete */
/* Right: button_ArticlesDialog_btnDelete */
(14560, 13033, 15543, 0),

/* O F F I C E S */
/* Group: Offices_View */
/* Right: menuCat_MainData */
(14570, 13040, 15003, 0),
/* Right: menuItem_MainData_Office */
(14571, 13040, 15006, 0),
/* Right: window_OfficesList */
(14572, 13040, 15108, 0),
/* Right: button_OfficeMain_btnPrint */
(14574, 13040, 15602, 0),
/* Right: button_OfficeList_SearchNo */
(14575, 13040, 15603, 0),
/* Right: button_OfficeList_SearchName */
(14576, 13040, 15604, 0),
/* Right: button_OfficeList_SearchCity */
(14577, 13040, 15605, 0),
/* Right: window_OfficesDialog */
(14578, 13040, 15109, 0),
/* Right: button_OfficeDialog_btnHelp */
(14579, 13040, 15611, 0),
/* Right: button_OfficeDialog_btnClose */
(14580, 13040, 15616, 0),
/* Right: button_OfficeMain_btnCancel */
(14581, 13040, 15617, 0),
/* new: sge:07/18/2011  navigation buttons */
/* Right: button_BranchMain_btnFirst */
(14582, 13040, 15618, 0),
/* Right: button_BranchMain_btnPrevious */
(14583, 13040, 15619, 0),
/* Right: button_BranchMain_btnNext */
(14584, 13040, 15620, 0),
/* Right: button_BranchMain_btnLast */
(14585, 13040, 15621, 0),

/* Group: Offices_New */
/* Right: button_OfficeDialog_btnNew */
(14586, 13041, 15612, 0),
/* Right: button_OfficeDialog_btnSave */
(14587, 13041, 15615, 0),
/* Right: button_OfficeDialog_btnClose */
(14588, 13041, 15616, 0),
/* Right: button_OfficeMain_btnCancel */
(14589, 13041, 15617, 0),

/* Group: Offices_Edit */
/* Right: button_OfficeDialog_btnEdit */
(14590, 13042, 15613, 0),
/* Right: button_OfficeDialog_btnSave */
(14591, 13042, 15615, 0),
/* Right: button_OfficeDialog_btnClose */
(14592, 13042, 15616, 0),
/* Right: button_OfficeMain_btnCancel */
(14593, 13042, 15617, 0),

/* Group: Offices_Delete */
/* Right: button_OfficeDialog_btnDelete */
(14595, 13043, 15614, 0),

/* Group: Security_Groups */
/* Right: secGroupDialogWindow */
(14600, 13070, 15760, 0),
/* Right: button_SecGroupDialog_btnHelp */
(14601, 13070, 15761, 0),
/* Right: button_SecGroupDialog_btnNew */
(14602, 13070, 15762, 0),
/* Right: button_SecGroupDialog_btnEdit */
(14603, 13070, 15763, 0),
/* Right: button_SecGroupDialog_btnDelete */
(14604, 13070, 15764, 0),
/* Right: buton_SecGroupDialog_btnSave */
(14605, 13070, 15765, 0),
/* Right: button_SecGroupDialog_btnClose */
(14606, 13070, 15766, 0),
/* Right: button_SecGroupDialog_btnCancel */
(14607, 13070, 15767, 0),

/* Group: Security_Roles */
/* Right: secRoleDialogWindow */
(14610, 13071, 15750, 0),
/* Right: button_SecRoleDialog_btnHelp */
(14611, 13071, 15751, 0),
/* Right: button_SecRoleDialog_btnNew */
(14612, 13071, 15752, 0),
/* Right: button_SecRoleDialog_btnEdit */
(14613, 13071, 15753, 0),
/* Right: button_SecRoleDialog_btnDelete */
(14614, 13071, 15754, 0),
/* Right: button_SecRoleDialog_btnSave */
(14615, 13071, 15755, 0),
/* Right: button_SecRoleDialog_btnClose */
(14616, 13071, 15756, 0),
/* Right: button_SecRoleDialog_btnCancel */
(14617, 13071, 15757, 0),

/* Group: Security_Rights */
/* Right: secRightDialogWindow */
(14620, 13072, 15770, 0),
/* Right: button_SecRightDialog_btnHelp */
(14621, 13072, 15771, 0),
/* Right: button_SecRightDialog_btnNew */
(14622, 13072, 15772, 0),
/* Right: button_SecRightDialog_btnEdit */
(14623, 13072, 15773, 0),
/* Right: button_SecRightDialog_btnDelete */
(14624, 13072, 15774, 0),
/* Right: button_SecRightDialog_btnSave */
(14625, 13072, 15775, 0),
/* Right: button_SecRightDialog_btnClose */
(14626, 13072, 15776, 0),
/* Right: button_SecRightDialog_btnCancel */
(14627, 13072, 15777, 0);


/******************** Branche Daten ********************/
INSERT INTO BRANCHE (BRA_ID,BRA_BEZEICHNUNG, VERSION) VALUES
(1000,  'Elektro',0),
(1001,  'Maler',0),
(1002,  'Holzverabeitung',0),
(1003,  'Kaufmaennisch',0),
(1004,  'Versicherung',0),
(1005,  'Mess- und Regeltechnik',0),
(1006,  'Industriemontagen',0),
(1007,  'KFZ',0),
(1008,  'Banken',0),
(1009,  'Grosshandel',0),
(1010,  'Einzelhandel',0),
(1011,  'Werbung',0),
(1012,  'Gastronomie',0),
(1014,  'Pflegedienste',0),
(1015,  'Transportwesen',0),
(1016,  'Metallverarbeitung',0),
(1017,  'Schlosserei',0),
(1018,  'Sanitaer',0),
(1019,  'Heizungsbau',0),
(1020,  'Wasserwirtschaft',0),
(1021,  'Schiffsbau',0),
(1022,  'Laermschutz',0),
(1023,  'Geruestbau',0),
(1024,  'Fassadenbau',0),
(1025,  'Farbherstellung',0),
(1026,  'Kieswerk',0),
(1027,  'Blechnerei',0),
(1028,  'Geruestverleih',0),
(1029,  'Pflasterarbeiten',0),
(1030,  'Trockenbau',0),
(1031,  'Trockenbau- und Sanierung',0),
(1032,  'Huehnerfarm',0),
(1033,  '.',0),
(1034,  'Transportwesen allgemein',0),
(1035,  'Schwertransport',0),
(1036,  'Gefahrgut Transport',0),
(1037,  'Spedition',0);

/******************** Kunden Daten ********************/
INSERT INTO KUNDE (KUN_ID,KUN_FIL_ID,KUN_BRA_ID, KUN_NR, KUN_MATCHCODE,KUN_NAME1,KUN_NAME2,KUN_ORT,KUN_MAHNSPERRE,VERSION) VALUES 
(20,1,1000, '20', 'MUELLER','--> MUEller','Elektroinstallationen','Freiburg',true,0),
(21,1,1000, '21', 'HUBER','--> Huber','Elektroinstallationen','Oberursel',true,0),
(22,1,1000, '22', 'SIEMENS','Siemens AG','Elektroinstallationen','Braunschweig',false,0),
(23,1,1000, '23', 'AEG','AEG','Elektroinstallationen','Stuttgart',false,0),
(24,1,1019, '24', 'BUDERUS','Buderus Heizungsbau GmbH','Elektroinstallationen','Rastatt',true,0),
(25,1,1000, '25', 'MEILER','Elektro Meiler','Inhaber W. Erler','Karlsruhe',true,0),
(26,1,1000, '26', 'BADER','Bader GmbH','Elektroinstallationen','Berlin',false,0),
(27,1,1000, '27', 'HESKENS','Heskens GmbH','Elektroinstallationen','Badenweiler',false,0),
(28,1,1000, '28', 'MAIER','Maier GmbH','Elektroinstallationen','Friedberg',false,0),
(29,1,1000, '29', 'SCHULZE','Schulze GmbH','Elektroinstallationen','Freiburg',true,0),
(30,1,1004, '30', 'SCHMIERFINK','Schmierfink AG','Versicherungen','Freiburg',true,0),
(31,1,1005, '31', 'SCHULZE','Schulze Ltd.','Anlagenbau','Buxtehude',true,0),
(32,1,1005, '32', 'SCHREINER','Schreiner','SPS-Steuerungsbau','Hamburg',true,0),
(33,1,1004, '33', 'GUTE RUHE','Gute Ruhe AG','Versicherungen','Berlin',true,0),
(34,1,1003, '34', 'FREIBERGER','Freiberger GmbH','In- und Export','Aachen',true,0),
(35,1,1002, '35', 'BERGMANN','Saegewerk Bergmann','Holzverarbeitung','Neustadt',true,0),
(2000,1,1002, '2000', 'SEILER','Saegewerk Seiler','Holzverarbeitung','Freiburg',true,0),
(2001,1,1002, '2001', 'BAUER','Hermann Bauer','Sgerwerk','Titisee-Neustadt',true,0),
(2002,1,1000, '2002', 'BEINHARD','Gebrueder Beinhard GbR','Elektroinstallationen','Muenchen',true,0),
(2003,1,1000, '2003', 'ADLER','Reiner Adler','Elektro Montagen','Dreieich',true,0),
(2004,1,1000, '2004', 'FINK','Hartmut Fink GmbH','Elektro- und Industriemontagen','Stuttgart',true,0),
(2005,1,1000, '2005', 'GERHARD','Huber u. Gerhard GbR','Elektroinstallationen','Stuttgart',true,0),
(2006,1,1004, '2006', 'BELIANZ','BELIANZ','Versicherungs AG','Berlin',true,0),
(2007,1,1004, '2007', 'KINTERTHUR','Kinterthur','Versicherungs AG','Rastatt',true,0),
(2008,1,1004, '2008', 'WOLFRAM','Peter Wolfram','Freier Versicherungsvertreter','Norderstedt',true,0),
(2009,1,1000, '2009', 'HESSING','Mario Hessing GmbH','Elektroinstallationen','Rheinweiler',false,0),
(2010,1,1000, '2010', 'FREIBERG','Werner Freiberg GmbH','Elektroinstallationen','Rheinstetten',false,0);

/******************** Auftrag Daten ********************/
INSERT INTO AUFTRAG (AUF_ID,AUF_KUN_ID, AUF_NR, AUF_BEZEICHNUNG, VERSION) VALUES
(40, 20, 'AUF-40', 'EGRH Modul1',0),
(41, 20, 'AUF-41', 'OMEGA Modul2',0),
(42, 20, 'AUF-42', 'Keller',0),
(43, 20, 'AUF-43', 'Schlossallee 23',0),
(44, 21, 'AUF-44', 'Renovierung Keller',0),
(45, 21, 'AUF-45', 'Renovierung Hochhaus Ilsestrasse 5',0),
(46, 21, 'AUF-46', 'Verteilerschrank Umbau; Fa. Kloeckner EHG',0);

/******************** Artikel Daten ********************/
INSERT INTO ARTIKEL (ART_ID,ART_KURZBEZEICHNUNG,ART_LANGBEZEICHNUNG,ART_NR,ART_PREIS,VERSION) VALUES 
(3000,'Kabelverschraubung DN 27','Kabelverschraubung Messing verchromt DN 27','KS3000',4.23,0),
(3001,'3-adriges Kabel 1,5mm ','mehradriges Kabel, 3 Adern, Farbe=grau, Querschnitt= 1,5 mm','MK3001',0.12,0),
(3002,'Luesterklemmen 10-fach, bis 1.5mm','Luesterklemmen, grau, bis Querschnitt 1.5mm','LUESTER1002',0.78,0),
(3003,'Euro-Platine','Euro-Platine fuer Versuchsaufbauten','PLAT3003',2.34,0),
(3004,'Leuchtmittel 22 Watt','Leuchtmittel 22 Watt Sparlampe weiss, mittlere Lebensdauer ca. 6000 Std.','SPARLA3004',2.84,0),
(3005,'Leuchte einzel bis 100 Watt','Haengeleuchte einzel, Farbe=grau, bis 100 Watt','LEU3005',32.00,0),
(3006,'5-adriges Kabel 1,5mm','mehradriges Kabel, 5 Adern, Farbe=grau, Querschnitt= 1,5 mm','MK3006',0.22, 0),
(3007,'Kabelbinder 12cm','Kabelbinder, Menge: 100 Stk. Lnge: 12 cm, Farbe: weiss','KB3007',1.34, 0),
(3008,'Kabelverschraubung DN 17','Kabelverschraubung Messing verchromt DN 17','KS3008',2.90,0),
(3009,'Kabelverschraubung DN 18','Kabelverschraubung Messing verchromt DN 18','KS3009',3.20,0),
(3010,'Kabelverschraubung DN 22','Kabelverschraubung Messing verchromt DN 22','KS3010',3.40,0),
(3011,'Luesterklemmen 10-fach, bis 2.5mm','Luesterklemmen, grau, bis Querschnitt 1.5mm','LUESTER3011',1.68,0),
(3012,'Luesterklemmen 10-fach, bis 4.5mm','Luesterklemmen, grau, bis Querschnitt 1.5mm','LUESTER3012',3.10,0);



/******************** Auftrag Positionen Daten ********************/
/* Auftrag: 40 */
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION) VALUES 
(60,40, 3000, 1, 240, 1.20, 288.00, 0),
(61,40, 3001, 2, 1200, 0.45, 540.00, 0),
(62,40, 3002, 3, 40, 0.20, 8.00, 0),
(63,40, 3003, 4, 15, 4.20, 63.00, 0),
(64,40, 3004, 5, 20, 4.30, 86.00, 0),
(65,40, 3005, 6, 15, 40.00, 600.00, 0);
/* Auftrag: 41 */
INSERT INTO AUFTRAGPOSITION (AUP_ID, AUP_AUF_ID, ART_ID, AUP_POSITION, AUP_MENGE, AUP_EINZELWERT, AUP_GESAMTWERT, VERSION) VALUES
(66,41, 3005, 1, 18, 40.00, 720.00, 0),
(67,41, 3006, 2, 800 , 0.45, 360.00, 0),
(68,41, 3002, 3, 40, 0.20, 8.00, 0),
(69,44, 3007, 1, 3, 2.10, 6.30, 0),
(70,44, 3001, 2, 1200, 0.45, 540.00, 0),
(71,44, 3002, 3, 40, 0.20, 8.00, 0),
(72,44, 3003, 4, 15, 4.20, 63.00, 0),
(73,44, 3004, 5, 20, 4.30, 86.00, 0),
(74,44, 3005, 6, 15, 40.00, 600.00, 0),
(75,45, 3008, 0, 240, 3.20, 768.00, 0),
(76,45, 3009, 1, 444, 3.80, 1687.20, 0),
(77,45, 3010, 2, 240, 4.10, 984.00, 0),
(78,46, 3011, 0, 40, 2.20, 88.00, 0),
(79,46, 3012, 1, 80, 3.60, 288.00, 0),
(80,46, 3002, 2, 90, 1.50, 135.00, 0),
(81,46, 3010, 3, 100, 4.10, 410.00, 0),
(82,46, 3011, 4, 400, 2.20, 880.00, 0),
(83,46, 3006, 5, 60.00, 0.45, 27.00, 0);



 /* fill the countrycodes */
INSERT INTO sys_countrycode(CCD_ID, CCD_NAME, CCD_CODE2, VERSION) VALUES 
(-1,'UNROUTABLE ADDRESS','xx', 0),
(1, 'AFGHANISTAN','AF', 0),
(2, 'ALBANIA','AL', 0),
(3, 'ALGERIA','DZ', 0),
(4, 'AMERICAN SAMOA','AS', 0),
(5, 'ANDORRA','AD', 0),
(6, 'ANGOLA','AO', 0),
(7, 'ANGUILLA','AI', 0),
(8, 'ANTARCTICA','AQ', 0),
(9, 'ANTIGUA AND BARBUDA','AG', 0),
(10,'ARGENTINA','AR', 0),
(11,'ARMENIA','AM', 0),
(12,'ARUBA','AW', 0),
(13,'AUSTRALIA','AU', 0),
(14,'AUSTRIA','AT', 0),
(15,'AZERBAIJAN','AZ', 0),
(16,'BAHAMAS','BS', 0),
(17,'BAHRAIN','BH', 0),
(18,'BANGLADESH','BD', 0),
(19,'BARBADOS','BB', 0),
(20,'BELARUS','BY', 0),
(21,'BELGIUM','BE', 0),
(22,'BELIZE','BZ', 0),
(23,'BENIN','BJ', 0),
(24,'BERMUDA','BM', 0),
(25,'BHUTAN','BT', 0),
(26,'BOLIVIA','BO', 0),
(27,'BOSNIA AND HERZEGOVINA','BA', 0),
(28,'BOTSWANA','BW', 0),
(29,'BOUVET ISLAND','BV', 0),
(30,'BRAZIL','BR', 0),
(31,'BRITISH INDIAN OCEAN TERRITORY','IO', 0),
(32,'BRUNEI DARUSSALAM','BN', 0),
(33,'BULGARIA','BG', 0),
(34,'BURKINA FASO','BF', 0),
(35,'BURUNDI','BI', 0),
(36,'CAMBODIA','KH', 0),
(37,'CAMEROON','CM', 0),
(38,'CANADA','CA', 0),
(39,'CAPE VERDE','CV', 0),
(40,'CAYMAN ISLANDS','KY', 0),
(41,'CENTRAL AFRICAN REPUBLIC','CF', 0),
(42,'CHAD','TD', 0),
(43,'CHILE','CL', 0),
(44,'CHINA','CN', 0),
(45,'CHRISTMAS ISLAND','CX', 0),
(46,'COCOS (KEELING) ISLANDS','CC', 0),
(47,'COLOMBIA','CO', 0),
(48,'COMOROS','KM', 0),
(49,'CONGO','CG', 0),
(50,'CONGO, THE DEMOCRATIC REPUBLIC OF THE','CD', 0),
(51,'COOK ISLANDS','CK', 0),
(52,'COSTA RICA','CR', 0),
(53,'COTE D IVOIRE','CI', 0),
(54,'CROATIA','HR', 0),
(55,'CUBA','CU', 0),
(56,'CYPRUS','CY', 0),
(57,'CZECH REPUBLIC','CZ', 0),
(58,'DENMARK','DK', 0),
(59,'DJIBOUTI','DJ', 0),
(60,'DOMINICA','DM', 0),
(61,'DOMINICAN REPUBLIC','DO', 0),
(62,'ECUADOR','EC', 0),
(63,'EGYPT','EG', 0),
(64,'EL SALVADOR','SV', 0),
(65,'EQUATORIAL GUINEA','GQ', 0),
(66,'ERITREA','ER', 0),
(67,'ESTONIA','EE', 0),
(68,'ETHIOPIA','ET', 0),
(69,'FALKLAND ISLANDS','FK', 0),
(70,'FAROE ISLANDS','FO', 0),
(71,'FIJI','FJ', 0),
(72,'FINLAND','FI', 0),
(73,'FRANCE','FR', 0),
(74,'FRENCH GUIANA','GF', 0),
(75,'FRENCH POLYNESIA','PF', 0),
(76,'FRENCH SOUTHERN TERRITORIES','TF', 0),
(77,'GABON','GA', 0),
(78,'GAMBIA','GM', 0),
(79,'GEORGIA','GE', 0),
(80,'GERMANY','DE', 0),
(81,'GHANA','GH', 0),
(82,'GIBRALTAR','GI', 0),
(83,'GREECE','GR', 0),
(84,'GREENLAND','GL', 0),
(85,'GRENADA','GD', 0),
(86,'GUADELOUPE','GP', 0),
(87,'GUAM','GU', 0),
(88,'GUATEMALA','GT', 0),
(89,'GUINEA','GN', 0),
(90,'GUINEA-BISSAU','GW', 0),
(91,'GUYANA','GY', 0),
(92,'HAITI','HT', 0),
(93,'HEARD ISLAND AND MCDONALD ISLANDS','HM', 0),
(94,'HOLY SEE (VATICAN CITY STATE)','VA', 0),
(95,'HONDURAS','HN', 0),
(96,'HONG KONG','HK', 0),
(97,'HUNGARY','HU', 0),
(98,'ICELAND','IS', 0),
(99,'INDIA','IN', 0),
(100,'INDONESIA','ID', 0),
(101,'IRAN, ISLAMIC REPUBLIC OF','IR', 0),
(102,'IRAQ','IQ', 0),
(103,'IRELAND','IE', 0),
(104,'ISRAEL','IL', 0),
(105,'ITALY','IT', 0),
(106,'JAMAICA','JM', 0),
(107,'JAPAN','JP', 0),
(108,'JORDAN','JO', 0),
(109,'KAZAKHSTAN','KZ', 0),
(110,'KENYA','KE', 0),
(111,'KIRIBATI','KI', 0),
(112,'KOREA, DEMOCRATIC REPUBLIC OF','KP', 0),
(113,'KOREA, REPUBLIC OF','KR', 0),
(114,'KUWAIT','KW', 0),
(115,'KYRGYZSTAN','KG', 0),
(116,'LAO DEMOCRATIC REPUBLIC','LA', 0),
(117,'LATVIA','LV', 0),
(118,'LEBANON','LB', 0),
(119,'LESOTHO','LS', 0),
(120,'LIBERIA','LR', 0),
(121,'LIBYAN ARAB JAMAHIRIYA','LY', 0),
(122,'LIECHTENSTEIN','LI', 0),
(123,'LITHUANIA','LT', 0),
(124,'LUXEMBOURG','LU', 0),
(125,'MACAO','MO', 0),
(126,'MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF','MK', 0),
(127,'MADAGASCAR','MG', 0),
(128,'MALAWI','MW', 0),
(129,'MALAYSIA','MY', 0),
(130,'MALDIVES','MV', 0),
(131,'MALI','ML', 0),
(132,'MALTA','MT', 0),
(133,'MARSHALL ISLANDS','MH', 0),
(134,'MARTINIQUE','MQ', 0),
(135,'MAURITANIA','MR', 0),
(136,'MAURITIUS','MU', 0),
(137,'MAYOTTE','YT', 0),
(138,'MEXICO','MX', 0),
(139,'MICRONESIA, FEDERATED STATES OF','FM', 0),
(140,'MOLDOVA, REPUBLIC OF','MD', 0),
(141,'MONACO','MC', 0),
(142,'MONGOLIA','MN', 0),
(143,'MONTSERRAT','MS', 0),
(144,'MOROCCO','MA', 0),
(145,'MOZAMBIQUE','MZ', 0),
(146,'MYANMAR','MM', 0),
(147,'NAMIBIA','NA', 0),
(148,'NAURU','NR', 0),
(149,'NEPAL','NP', 0),
(150,'NETHERLANDS','NL', 0),
(151,'NETHERLANDS ANTILLES','AN', 0),
(152,'NEW CALEDONIA','NC', 0),
(153,'NEW ZEALAND','NZ', 0),
(154,'NICARAGUA','NI', 0),
(155,'NIGER','NE', 0),
(156,'NIGERIA','NG', 0),
(157,'NIUE','NU', 0),
(158,'NORFOLK ISLAND','NF', 0),
(159,'NORTHERN MARIANA ISLANDS','MP', 0),
(160,'NORWAY','NO', 0),
(161,'OMAN','OM', 0),
(162,'PAKISTAN','PK', 0),
(163,'PALAU','PW', 0),
(164,'PALESTINIAN TERRITORY, OCCUPIED','PS', 0),
(165,'PANAMA','PA', 0),
(166,'PAPUA NEW GUINEA','PG', 0),
(167,'PARAGUAY','PY', 0),
(168,'PERU','PE', 0),
(169,'PHILIPPINES','PH', 0),
(170,'PITCAIRN','PN', 0),
(171,'POLAND','PL', 0),
(172,'PORTUGAL','PT', 0),
(173,'PUERTO RICO','PR', 0),
(174,'QATAR','QA', 0),
(175,'REUNION','RE', 0),
(176,'ROMANIA','RO', 0),
(177,'RUSSIAN FEDERATION','RU', 0),
(178,'RWANDA','RW', 0),
(179,'SAINT HELENA','SH', 0),
(180,'SAINT KITTS AND NEVIS','KN', 0),
(181,'SAINT LUCIA','LC', 0),
(182,'SAINT PIERRE AND MIQUELON','PM', 0),
(183,'SAINT VINCENT AND THE GRENADINES','VC', 0),
(184,'SAMOA','WS', 0),
(185,'SAN MARINO','SM', 0),
(186,'SAO TOME AND PRINCIPE','ST', 0),
(187,'SAUDI ARABIA','SA', 0),
(188,'SENEGAL','SN', 0),
(189,'SERBIA','RS', 0),
(190,'SEYCHELLES','SC', 0),
(191,'SIERRA LEONE','SL', 0),
(192,'SINGAPORE','SG', 0),
(193,'SLOVAKIA','SK', 0),
(194,'SLOVENIA','SI', 0),
(195,'SOLOMON ISLANDS','SB', 0),
(196,'SOMALIA','SO', 0),
(197,'SOUTH AFRICA','ZA', 0),
(198,'SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS','GS', 0),
(199,'SPAIN','ES', 0),
(200,'SRI LANKA','LK', 0),
(201,'SUDAN','SD', 0),
(202,'SURINAME','SR', 0),
(203,'SVALBARD AND JAN MAYEN','SJ', 0),
(204,'SWAZILAND','SZ', 0),
(205,'SWEDEN','SE', 0),
(206,'SWITZERLAND','CH', 0),
(207,'SYRIAN ARAB REPUBLIC','SY', 0),
(208,'TAIWAN','TW', 0),
(209,'TAJIKISTAN','TJ', 0),
(210,'TANZANIA, UNITED REPUBLIC OF','TZ', 0),
(211,'THAILAND','TH', 0),
(212,'TIMOR-LESTE','TL', 0),
(213,'TOGO','TG', 0),
(214,'TOKELAU','TK', 0),
(215,'TONGA','TO', 0),
(216,'TRINIDAD AND TOBAGO','TT', 0),
(217,'TUNISIA','TN', 0),
(218,'TURKEY','TR', 0),
(219,'TURKMENISTAN','TM', 0),
(220,'TURKS AND CAICOS ISLANDS','TC', 0),
(221,'TUVALU','TV', 0),
(222,'UGANDA','UG', 0),
(223,'UKRAINE','UA', 0),
(224,'UNITED ARAB EMIRATES','AE', 0),
(225,'UNITED KINGDOM','GB', 0),
(226,'UNITED STATES','US', 0),
(227,'UNITED STATES MINOR OUTLYING ISLANDS','UM', 0),
(228,'URUGUAY','UY', 0),
(229,'UZBEKISTAN','UZ', 0),
(230,'VANUATU','VU', 0),
(231,'VENEZUELA','VE', 0),
(232,'VIET NAM','VN', 0),
(233,'VIRGIN ISLANDS, BRITISH','VG', 0),
(234,'VIRGIN ISLANDS, U.S.','VI', 0),
(235,'WALLIS AND FUTUNA','WF', 0),
(236,'WESTERN SAHARA','EH', 0),
(237,'YEMEN','YE', 0),
(238,'ZAMBIA','ZM', 0),
(239,'ZIMBABWE','ZW', 0),
(240,'UNITED KINGDOM','UK', 0),
(241,'EUROPEAN UNION','EU', 0),
(242,'YUGOSLAVIA','YU', 0),
(244,'ARIPO','AP', 0),
(245,'ASCENSION ISLAND','AC', 0),
(246,'GUERNSEY','GG', 0),
(247,'ISLE OF MAN','IM', 0),
(248,'JERSEY','JE', 0),
(249,'EAST TIMOR','TP', 0),
(250,'MONTENEGRO','ME', 0);

/******************** YouTube Music Links ********************/
INSERT INTO youtube_link(ytb_id, ytb_interpret, ytb_title, ytb_url, version) VALUES 
(  1, 'Loquat',                                   'Swing Set Chain',                      'http://www.youtube.com/embed/51G24IVfcaI',   0),
(  2, 'Empire of the Sun',                        'We Are The People',                    'http://www.youtube.com/embed/1uPL5twyQOw',   0),
(  3, 'Loquat',                                   'Harder Hit',                           'http://www.youtube.com/watch?v=aoHUb2r8q-g', 0),
(  4, 'THIN LIZZY',                               'Still in Love With You',               'http://www.youtube.com/embed/oHUWXjNU0aM',   0),
(  5, 'THIN LIZZY',                               'Whiskey in the jar (1973)',            'http://www.youtube.com/embed/-M2jSzLBzK4',   0),
(  6, 'Gary Moore with Phil Lynnot',              'Parisienne Walkways (live)',           'http://www.youtube.com/embed/18FgnFVm5k0',   0),
(  7, 'Talking Heads',                            'This must be the place',               'http://www.youtube.com/embed/TTPqPZzH-LA',   0),
(  8, 'John Cale and Brian Eno',                  'Spinning away',                        'http://www.youtube.com/embed/-INeMspNSQ0',   0),
(  9, 'Metric',                                   'Joyride',                              'http://www.youtube.com/embed/F0ZL5YWP5I8',   0),
( 10, 'Medina',                                   'Kun For Mig + Ensome',                 'http://www.youtube.com/embed/5Gf004et0SI',   0),
( 11, 'Paris',                                    'Captain Morgan',                       'http://www.youtube.com/embed/o6Eq1bH-qA0',   0),
( 12, 'Al Corley',                                'Square Rooms',                         'http://www.youtube.com/embed/6VgR8XT1w8I',   0),
( 13, 'Al Corley',                                'Cold Dresses',                         'http://www.youtube.com/embed/lY3prM3e4xk',   0),
( 14, 'Victoria S',                               'One in a Million',                     'http://www.youtube.com/embed/3YdGVDvrmQ0',   0),
( 15, 'Unknown Cases',                            'MaSimBaBelle (Final Mix)',             'http://www.youtube.com/embed/WhXnEWNEflA',   0),
( 16, 'Heli Deinboek',                            'Oh Suzy du! ',                         'http://www.youtube.com/embed/qBzTINSsj_Q',   0),
( 17, 'Stefanie Heinzmann',                       'Unforgiven ',                          'http://www.youtube.com/embed/AOQG5CyiOkg',   0),
( 18, 'DJ Tiesto feat. Andain',                   'Beautiful Things',                     'http://www.youtube.com/embed/5OhaQ2ej63Q',   0),
( 19, 'DJ Tiesto feat. Kane',                     'Rain Down On Me',                      'http://www.youtube.com/embed/wZHCocBkZFo',   0),
( 20, 'DJ Tiesto',                                'Tiesto Power Mix',                     'http://www.youtube.com/embed/BZmE3fUKU5U',   0),
( 21, 'Dj Rui Da Silva vs. Dj Tiesto',            'Touch Me',                             'http://www.youtube.com/embed/bjla29Y1I5g',   0),
( 22, 'DJ Tiesto feat. Calvin Harris',            'Century',                              'http://www.youtube.com/embed/XdRk_lbR5fk',   0),
( 23, 'Supergrass',                               'Alright',                              'http://www.youtube.com/embed/h9nY9axjaWo',   0),
( 24, 'Mott the Hoople, David Bowie (Live)',      'All the Young Dudes',                  'http://www.youtube.com/embed/N-9F_z0B2TA',   0),
( 25, 'Vanilla Fudge',                            'You Keep Me Hangin On',                'http://www.youtube.com/embed/ifpmXmsecrU',   0),
( 26, 'Procol Harum',                             'A Whiter Shade Of Pale',               'http://www.youtube.com/embed/Mb3iPP-tHdA',   0),
( 27, 'Joe Cocker  (Live Dortmund `92)',          'Nightcalls ',                          'http://www.youtube.com/embed/q8RC01FsDtg',   0),
( 28, 'September',                                'Cry for you',                          'http://www.youtube.com/embed/pxu6iQ28arw',   0),
( 29, 'Depeche Mode (LIVE - Paris 2001)',         'Enjoy The Silence',                    'http://www.youtube.com/embed/GnFWXPM7Rno',   0),
( 30, 'DJ Tiesto feat. Cary Brothers',            'Here on earth',                        'http://www.youtube.com/embed/nlh72zBy5Nw',   0),
( 31, 'DJ Tiesto feat. Emily Haines',             'Knock you out',                        'http://www.youtube.com/embed/fM8zIyspZ24',   0),
( 32, 'DJ Tiesto feat. Christian Burns',          'In the dark',                          'http://www.youtube.com/embed/McWPLj6h4Fk',   0),
( 33, 'Armin van Buuren feat. Christian Burns',   'This Light Between Us',                'http://www.youtube.com/embed/T8GHg9v5CeA',   0),
( 34, 'Armin van Buuren feat. Sharon den Adel',   'In and Out of Love',                   'http://www.youtube.com/embed/TxvpctgU_s8',   0),
( 35, 'MIA (Live)',                               'Tanz der Moleküle',                    'http://www.youtube.com/embed/1uBaPOoQc8Q',   0),
( 36, 'MIA (Live Austria 2008)',                  '100 Prozent',                          'http://www.youtube.com/embed/sbSF4GRv56o',   0),
( 37, 'MIA (Live Austria 2008)',                  'Hungriges Herz',                       'http://www.youtube.com/embed/EQX2g-XSy6I',   0),
( 38, 'Klee',                                     'Erinner Dich',                         'http://www.youtube.com/embed/2y_mmfxT6qI',   0),
( 39, 'Freur (Live 1983)',                        'Doot Doot',                            'http://www.youtube.com/embed/u5QRT1CbMN0',   0),
( 40, 'David Bowie (Live Paris 2002)',            'Ashes to Ashes',                       'http://www.youtube.com/embed/81_Jm-P83FA',   0),
( 41, 'David Bowie (Live Paris 2002)',            'China Girl',                           'http://www.youtube.com/embed/OW7DFXq-cdg',   0),
( 42, 'David Bowie (Live Paris 2002)',            'Heroes',                               'http://www.youtube.com/embed/dTOppbFKWko',   0),
( 43, 'David Bowie (Live Germany 1978)',          'Rebel Rebel',                          'http://www.youtube.com/embed/pUAAU5g_ZEc',   0),
( 44, 'Kate Havnevik',                            'Kaleidoscope',                         'http://www.youtube.com/embed/1C8GobS1WE8',   0),
( 45, 'Joe Satriani (Live San Franciso 2006)',    'Until we say goodbye',                 'http://www.youtube.com/embed/hAY3WeZovGk',   0),
( 46, 'Joe Satriani (Live San Franciso 2006)',    'The Crush of Love',                    'http://www.youtube.com/embed/dB1WsWi4EEY',   0),
( 47, 'French Kiss (Live aux Francofolies 2009)', 'Le Soupir',                            'http://www.youtube.com/embed/YziA45xILWU',   0),
( 48, 'CHAKA KHAN (Live)',                        'Ain''t nobody',                        'http://www.youtube.com/embed/6eDSIj_iozA',   0),
( 49, 'Madita',                                   'Ceylon',                               'http://www.youtube.com/embed/6qzMxqzS-bg',   0),
( 50, 'The Crystal Method',                       'Starting over. Play it loud :-)',      'http://www.youtube.com/embed/pMGJ3cVvIrY',   0),
( 51, 'Jessie J. feat. B.o.B.',                   'Price Tag',                            'http://www.wat.tv/video/jessie-price-tag-feat-o-3bosb_2zicp_.html',   0),
( 52, 'THE Heavy (Live 2009)',                    'Short Change Hero',                    'http://www.youtube.com/embed/Kqxe31ICFZk',   0),
( 53, 'Grace Potter and Joe Satriani (Live)',     'Cortez the Killer',                    'http://www.youtube.com/embed/paeNnR33i5Q',   0),
( 54, 'Edie Brickell (Live)',                     'what i am',                            'http://www.youtube.com/embed/uGjh6duUPXc',   0),
( 55, 'Dire Strait & Eric Clapton (Live Wembley 1988)', 'Brothers in arms',               'http://www.youtube.com/embed/kAl5jucOgro',   0),
( 56, 'Buckethead live',                          'Padmasana',                            'http://www.youtube.com/embed/C-2w9b8i7GU',   0),
( 57, 'Brian Ferry - Roxy Music',                 'More than this',                       'http://www.outube.com/embed/UrtRYmJ9u_8',    0),
( 58, 'Brian Ferry - Roxy Music live 1979',       'Dance away',                           'http://www.youtube.com/embed/OA99t2PZbxg',   0),
( 59, 'Brian Ferry - Roxy Music 1985',            'Slave to love',                        'http://www.youtube.com/embed/lkN6l764NT8',   0),
( 60, 'Neil Young  (Live Rust)',                  'Like A Hurricane',                     'http://www.youtube.com/embed/7KxiEjPCXA8',   0),
( 61, 'Neil Young  (Live Rust)',                  'Cortez The Killer',                    'http://www.youtube.com/embed/6GDIkb5CDUY',   0),
( 62, 'Neil Young  (Live Rust)',                  'Southern man',                         'http://www.youtube.com/embed/kVRxdPWV3RM',   0),
( 63, 'Nina Hagen (1979 - Cha Cha Soundtrack)',   'Herrman''s Door',                      'http://www.youtube.com/embed/tn5RVU8b7IE',   0),
( 64, 'Keith Richards',                           'Hate It When You Leave',               'http://www.youtube.com/embed/lAJUt1C923Q',   0),
( 65, 'Vargo',                                    'Talking one language',                 'http://www.youtube.com/embed/fER_7R686Vc',   0),
( 66, 'Vargo',                                    'The Moment',                           'http://www.youtube.com/embed/fYqEn10U6vM',   0),
( 67, 'Vargo',                                    'Infinity',                             'http://www.youtube.com/embed/jJ2Yhqezmho',   0),
( 68, 'Frou Frou (is 100% better on CD)',         'Let go',                               'http://www.youtube.com/embed/MX8UYHGbndY',   0),
( 69, 'Frou Frou live (is 100% better on CD)',    'Breate in',                            'http://www.youtube.com/embed/F4vTjoPFGbA',   0),
( 70, 'Frou Frou live (is 100% better on CD)',    'It''s good to be in love',             'http://www.youtube.com/embed/xRwwlsevNLs',   0),
( 71, 'Frou Frou Cover  (please hear the original CD)', 'Hear me out',                    'http://v.youku.com/v_show/id_XMjMxMzUzNzI4.html',   0),
( 72, 'Bullmeister',                              'Girls Beautiful',                      'http://www.youtube.com/embed/LGatc0CDw0w',   0),
( 73, 'Lady Gaga vs. Ne-Yo (Preliminary Mix) Mashup 2010',  'Beautiful Monster',          'http://www.youtube.com/embed/zSjclviKmK0',   0),
( 74, 'Ne-yo ft Lady Gaga (Craig Vanity FIXED Mash 2.0)',   'Beautiful Monster',          'http://www.youtube.com/embed/rzQj3NSGXT0',   0),
( 75, 'Andy Timmons',                             'Cry for you',                          'http://www.youtube.com/embed/WMJD_c-WxPg',   0),
( 76, 'Old stars',                                'While my guitar gently weeps',         'http://www.youtube.com/embed/ifp_SVrlurY',   0),
( 77, 'Peter gabriel & Kate Bush',                'Don''t give up',                       'http://www.youtube.com/embed/uiCRZLr9oRw',   0),
( 78, 'Genesis',                                  'Super''s ready Part I',                'http://www.youtube.com/embed/xtHClRu1DrE',   0),
( 79, 'Genesis',                                  'Super''s ready Part II',               'http://www.youtube.com/embed/mTQBr9bIzJU',   0),
( 80, 'Genesis',                                  'Super''s ready Part III',              'http://www.youtube.com/embed/upi6wpANBh4',   0),
( 81, 'Billy Idol (Live)',                        'Rebel Yell',                           'http://www.youtube.com/embed/fv0_BoXt3kU',   0),
( 82, 'Billy Idol',                               'Eyes Without A Face',                  'http://www.youtube.com/embed/BKmldYSDJaM',   0),
( 83, 'Billy Idol',                               'White wedding',                        'http://www.youtube.com/embed/tgFh4RHgn0A',   0),
( 84, 'Billy Idol (Live 1981)',                   'Dancing with myself',                  'http://www.youtube.com/embed/pOpnyv6JqWc',   0),
( 85, 'Carlos Santana feat. Everlast (Live 2008))', 'Put my lights on',                   'http://www.youtube.com/embed/9eJgTUUw4Pw',   0),
( 86, 'David Bowie (Live 2000)',                  'This is not America',                  'http://www.youtube.com/embed/3osvs63aPUs',   0),
( 87, 'David Bowie - Gail Ann Dorsey (Live 1995)', 'Under Pressure',                      'http://www.youtube.com/embed/XXaAabOib9E',   0),
( 88, 'Gwen Stefanie',                            'Early Winter',                         'http://www.youtube.com/embed/iZR8oxifrpc',   0),
( 90, 'Tubeway Army',                             'Are friends electric?',                'http://www.youtube.com/embed/-0WNbm1jz6A',   0),
( 91, 'Lovers Electric',                          'Honey',                                'http://www.youtube.com/embed/y5DV4BJDdx8',   0),
( 92, 'Lovers Electric',                          'Beating like a drum',                  'http://www.youtube.com/embed/zgNuA3bR6Uw',   0),
( 93, 'Lovers Electric (Live at Cambridge)',      'Morning Sun',                          'http://www.youtube.com/embed/7bODdrpRRRU',   0),
( 94, 'Jennifer Rostock',                         'Mein Mikrofon',                        'http://www.youtube.com/embed/QW3Lbn0QiTk',   0),
( 95, 'Lady JAVA',                                'Java Zone',                            'http://www.youtube.com/embed/Mk3qkQROb_k',   0),
( 96, 'Angus and Julia Stone',                    'Big Jet Plane',                        'http://www.youtube.com/embed/165RVtUhEvk',   0),
( 97, 'Angus and Julia Stone',                    'I''m not yours',                       'http://www.youtube.com/embed/XV3DOf1CS74',   0),
( 98, 'Niki & The Dove (Mylo Remix)',             'Gentle Roar',                          'http://www.youtube.com/embed/BHZNJAfpsAU',   0),
( 99, 'Moby (Mylo Remix)',                        'Lift me up',                           'http://www.youtube.com/embed/ehRATN0fOMw',   0),
(100, 'Medina (Live DR''s Juleshow 2010)',        'Kun For Mig',                          'http://www.youtube.com/embed/TOJqwlSdDuw',   0),
(101, 'Medina (Live DR''s Sport 2010)',           'Addiction',                            'http://www.youtube.com/embed/JngMNq6iyz8',   0),
(102, 'Medina (Live 2009)',                       'kun for mig',                          'http://www.youtube.com/embed/LNPt0yYsKN0',   0),
(103, 'William Fitzsimmons (Pink Ganter Remix)',  'So This Is Goodbye',                   'http://www.youtube.com/embed/zwB56HQLFJY',   0),
(104, 'William Fitzsimmons',                      'I Kissed A Girl (Katy Perry Cover)',   'http://www.youtube.com/embed/eYOCJn5xvTE',   0),
(105, 'Animal Kingdom',                           'Two By Two',                           'http://www.youtube.com/embed/kTRGUNXNJVo',   0),
(106, 'Animal Kingdom',                           'Bright Lights',                        'http://www.youtube.com/embed/IJyI8BPShCs',   0),
(107, 'Animal Kingdom',                           'Tin Man',                              'http://www.youtube.com/embed/x8vO7r86py8',   0),
(108, 'Lady Antebellum (LIVE AOL Sessions)',      'Need You Now ',                        'http://www.youtube.com/embed/zG16eqK9LL0',   0),
(109, 'Gary Moore Phil Lynott (live)',            'Out in the fields',                    'http://www.youtube.com/embed/o7kx5Y42Hqo',   0),
(110, 'The Cure',                                 '17 seconds',                           'http://www.youtube.com/embed/x_qzWmm66Ak',   0),
(111, 'The Cure',                                 'Secrets',                              'http://www.youtube.com/embed/lAyQ7J_C-fI',   0),
(112, 'The Cure',                                 'In your house',                        'http://www.youtube.com/embed/nqQoYJXo1Hk',   0),
(113, 'The Cure',                                 'M',                                    'http://www.youtube.com/embed/Uij9XyBizgI',   0),
(114, 'The Cure',                                 'Play for today',                       'http://www.youtube.com/embed/0x7vX2nBxFo',   0),
(115, 'The Cure',                                 'A forest',                             'http://www.youtube.com/embed/7ZwVgQ4Wq7E',   0),
(116, 'The Cure',                                 'Lovesong',                             'http://www.youtube.com/embed/-eDSzL0774Q',   0),
(117, 'The Cure',                                 'Lullaby',                              'http://www.youtube.com/embed/g-YiLU0SkIo',   0),
(118, 'The Cure',                                 'Three imaginary boys',                 'http://www.youtube.com/embed/Eh2Ye5It6Jg',   0),
(119, 'The Cure (Live 2008)',                     'Friday I''m in love',                  'http://www.youtube.com/embed/kFfMPIulF6Q',   0),
(120, 'Gorillaz Vs. The Cure',                    'Lullaby for Gorillaz',                 'http://www.youtube.com/embed/NV3CVuhJmOY',   0),
(121, 'Rheingold',                                'Dreiklangsdimensionen',                'http://www.youtube.com/embed/XEC1i0zoZ5k',   0),
(122, 'Supermax',                                 'LOVEMACHINE',                          'http://www.youtube.com/embed/yplE0HezKpE',   0),
(123, 'Supermax (Live 1978)',                     'World of today',                       'http://www.youtube.com/embed/OcqIJ81Hy24',   0),
(124, 'Ian Dury & The Blockheads (Live 1978)',    'Hit Me With Your Rhythm Stick',        'http://www.youtube.com/embed/NduKrNAZp4I',   0),
(125, 'Masterboy',                                'Everybody Needs Somebody',             'http://www.youtube.com/embed/fnKbBsnfv8I',   0),
(126, 'Masterboy',                                'Mister Feeling',                       'http://www.youtube.com/embed/EDaCETfIKK0',   0),
(127, 'Masterboy',                                'Land Of Dreaming',                     'http://www.youtube.com/embed/auUGXOM6LwE',   0),
(128, 'Masterboy (1990)',                         'Dance To The Beat',                    'http://www.youtube.com/embed/lo9KrRvrRMU',   0),
(129, 'Haddaway',                                 'What is love',                         'http://youtu.be/k_U6mWu1XQA',   0),
(130, 'IIO',                                      'Smooth',                               'http://www.youtube.com/embed/K2B7KU9F6M0',   0),
(131, 'Nadia Ali (Avicii Remix) Official Video',  'Rapture',                              'http://www.youtube.com/embed/7BgwW_WVfW4',   0),
(132, 'Nadia Ali',                                'Fantasy',                              'http://www.youtube.com/embed/sZ_TXUBIYV8',   0),
(133, 'Little Boots (2009)',                      'Every Little Earthquake',              'http://www.youtube.com/embed/S260p7Qv_k8',   0),
(134, 'Amy Diamond',                              'What''s in it for me',                 'http://www.youtube.com/embed/JZhfGyRlSro',   0),
(135, 'Stephanie De Monaco (1986)',               'Young Ones Everywhere',                'http://www.youtube.com/embed/qpLjb7_NaJw',   0),
(136, 'Danger Mouse and Sparklehorse (2009)',     'Revenge',                              'http://www.youtube.com/embed/FSgneDqybjU',   0),
(137, 'Paris',                                    'Hey Sailor',                           'http://www.youtube.com/embed/sijQsg08kAU',   0),
(138, 'Paris',                                    '60 minutes',                           'http://www.youtube.com/embed/XXMVdWugpOA',   0),
(139, 'Paris',                                    'Streetlights',                         'http://www.youtube.com/embed/HPB-t_y-blQ',   0),
(140, 'Sven Väth feat. Miss Kitty',               'Je t''aime moi non plus',              'http://www.youtube.com/embed/BVtHTFWcjYA',   0),
(141, 'Lauri (The Rasmus)',                       'In the city',                          'http://www.youtube.com/embed/k-9tQhDfxJI',   0),
(142, 'Lauri (The Rasmus)',                       'Heavy',                                'http://www.youtube.com/embed/Xl_FO-_wb_I',   0),
(143, 'Lil Wayne feat. Major Static',             'Lollipop',                             'http://www.youtube.com/embed/1ZotzfTAaNI',   0),
(144, 'Lil Wayne (Female Version)',               'Lollipop',                             'http://www.youtube.com/embed/wkpMh-NLWvc',   0),
(145, 'The sounds',                               'Painted by numbers',                   'http://www.youtube.com/embed/--CzFYB92Zc',   0),
(146, 'Metric',                                   'Sick Muse',                            'http://www.youtube.com/embed/ytG2TaSzOys',   0),
(147, 'Metric',                                   'Twilight Galaxy',                      'http://www.youtube.com/embed/R7U8wz78fIM',   0),
(148, 'Donots',                                   'Stop the clocks',                      'http://www.youtube.com/embed/7bLgGYFLhgQ',   0),
(149, 'Eartha Kitt',                              'I love men',                           'http://www.youtube.com/embed/hbg5i6TI5xg',   0),
(150, 'Somebody that i used to know',             'Walk off the earth',                   'http://www.youtube.com/embed/d9NF2edxy-M',   0),
(151, 'The Raveonettes',                          'Dead Sound',                           'http://www.youtube.com/embed/h2BUXwr80qA',   0),
(152, 'Carly Simon (1972)',                       'You''re so vain',                      'http://www.youtube.com/embed/b6UAYGxiRwU',   0),
(153, 'A Flock Of Seagulls (80'')',               'I ran',                                'http://www.youtube.com/embed/yMY6QJiIJtY',   0),
(154, 'TOYAH (Full Concert 1981) !!!!!',          'Good morning universe',                'http://www.youtube.com/embed/ZvCVFngzRYQ',   0),
(155, 'Shakespears Sister',                       'Hello',                                'http://www.youtube.com/embed/U8nibIN7i6M',   0),
(156, 'Fleetwood Mac (Live 1977)',                'Go your own way',                      'http://www.youtube.com/embed/p8Ojjn35kP8',   0),
(157, 'Fleetwood Mac (Live 1977)',                'Dreams',                               'http://www.youtube.com/embed/BJ818Xvup74',   0),
(158, 'Fleetwood Mac (Live)',                     'Gipsy',                                'http://www.youtube.com/embed/c5j8In4wu3g',   0),
(159, 'Fleetwood Mac (Live 1979)',                'Sara',                                 'http://www.youtube.com/embed/hesy7GBjr1Y',   0),
(160, 'Stevie Nicks (Live 1981) !!!!!',           'Edge of Seventeen',                    'http://www.youtube.com/embed/VyTBBbJ4GOo',   0),
(161, 'Stevie Nicks (Live 1986) All 15 minutes !','Edge of Seventeen',                    'http://www.youtube.com/embed/9Ido5yBD_xM',   0),
(162, 'Stevie Nicks  !!!!!',                      'Stand Back',                           'http://www.youtube.com/embed/-gSKeCvSCpw',   0),
(163, 'Stevie Nicks  !!!!!',                      'Rooms On Fire',                        'http://www.youtube.com/embed/hwnS_cGfaj4',   0),
(164, 'Bjork',                                    'All is full of love',                  'http://www.youtube.com/embed/tvoEZXop4zM',   0),
(165, 'Alanis Morissette',                        'Thank you',                            'http://www.youtube.com/embed/iCxBvGpCtGA',   0),
(166, 'Kaiser Chiefs (Live 2010)',                'Ruby',                                 'http://www.youtube.com/embed/8PDRJ7HrzjM',   0),
(167, 'Vassy',                                    'Desire',                               'http://www.youtube.com/embed/RY-yUFpXTnM',   0),
(168, 'Rihanna (Live 2012)',                      'Radio 1''s Hackney Weekend',           'http://www.youtube.com/embed/5VQcgxzHfTI',   0),
(169, 'Fashion (80'')',                           'Dressed to kill',                      'http://www.youtube.com/embed/xlFRdSmfy-8',   0),
(170, 'U2 & Mary J. Blige (Live 2012 Las Vegas)', 'One',                                  'http://www.youtube.com/embed/OqLHnWyY64s',   0),
(171, 'Wyclef Jean ft. Mary J. Blige (Live)',     '911',                                  'http://www.youtube.com/embed/G-tZ4NUddWg',   0),
(172, 'Rory Gallagher (Live 1974)',               'Tattoo''d Lady',                       'http://www.youtube.com/embed/JdkuHgSzNhg',   0),
(173, 'Rory Gallagher tribute Tokyo jam session (Live)', 'Whiskey in the jar',            'http://www.youtube.com/embed/ajnda165hiQ',   0),
(174, 'Joe Louis Walker',                         'While my guitare geently weeps',       'http://www.youtube.com/embed/CdOe_RMmyYA',   0),
(175, 'Clapton, McCartney, Starr (Live) 2002',    'While my guitare geently weeps',       'http://www.youtube.com/embed/FC1EZcrZEIs',   0),
(176, 'Jimmy Ponder',                             'While my guitare geently weeps',       'http://www.youtube.com/embed/rZ6MNfbHJMg',   0),
(177, 'Hindi Zahra',                              'Stand Up',                             'http://www.youtube.com/embed/YI2XuIOW3gM',   0),
(178, 'Hindi Zahra',                              'Beautiful Tango',                      'http://www.youtube.com/embed/2-8n6rTH6Ns',   0),
(179, 'Skye',                                     'Exhale',                               'http://www.youtube.com/embed/6CVgqC3-M_U',   0),
(180, 'Skye',                                     'Not broken',                           'http://www.youtube.com/embed/AwuuuaKlXJ4',   0),
(181, 'Skye',                                     'Boo Hoo',                              'http://www.youtube.com/embed/M3ReYBSi2zc',   0),
(182, 'Skye',                                     'I believe',                            'http://www.youtube.com/embed/mV84f0-tDM8',   0),
(183, 'Skye',                                     'Maybe To Spain',                       'http://www.youtube.com/embed/8gPc5vpZkf0',   0),
(184, 'Skye',                                     'Clock To Stop',                        'http://www.youtube.com/embed/vHjre711Gh0',   0),
(185, 'Skye',                                     'Whats Wrong With Me',                  'http://www.youtube.com/embed/DA6g0dp13Zk',   0),
(186, 'Skye',                                     'All The Promises',                     'http://www.youtube.com/embed/-m3jKVyJ2JM',   0),
(187, 'Moby',                                     'Extreme Ways',                         'http://www.youtube.com/embed/VTIkDmNVJeA',   0),
(188, 'Angus and Julia Stone (Live accoustic)',   'You''re the one that I want',          'http://www.youtube.com/embed/oTbObag1r0I',   0),
(189, 'Angus and Julia Stone (Live accoustic)',   'Big Jet Plane',                        'http://www.youtube.com/embed/kvD7a2fRV7A',   0);




 
/* fill sample logins */
INSERT INTO sec_loginlog(lgl_id, i2c_id, lgl_loginname,lgl_logtime, lgl_ip, lgl_status_id,lgl_sessionid, VERSION) VALUES 
( 1, NULL, 'admin', '2009-01-01 13:52:33', '87.118.90.17', 1, 'hjfjgfdfggzgzufuzfuzdfgfgfdvfv', 0),
( 2, NULL, 'user1', '2009-01-01 10:12:33', '203.237.141.216', 1, 'hjfjgfdfggzgzufuzfuzdfgfgfdvfv', 0),
( 3, NULL, 'admin', '2009-01-01 11:12:33', '202.96.188.101', 1, 'hjfjgfdfggzgzufuzfuzdfgfgfdvfv', 0),
( 4, NULL, 'aaaa', '2009-01-01 12:22:33', '84.234.27.179', 0, 'hjfjgfdfggzgzufuzfuzdfgfgfdvfv', 0),
( 5, NULL, 'admin', '2009-01-01 12:32:33', '84.139.11.102', 1, 'hjfjgfdfggzgzufuzfuzdfgfgfdvfv', 0),
( 6, NULL, 'user2', '2009-01-01 13:52:33', '87.118.90.17', 1, 'hjfjgfdfggzgzufuzfuzdfgfgfdvfv', 0),
( 7, NULL, 'admin', '2009-01-01 14:45:33', '212.227.148.189', 1, 'hjfjgfdfggzgzufuzfuzdfgfgfdvfv', 0),
( 8, NULL, 'admin', '2009-01-01 15:33:33', '84.185.153.21', 1, 'hjfjgfdfggzgzufuzfuzdfgfgfdvfv', 0),
( 9, NULL, 'admin', '2009-01-01 17:22:33', '212.156.5.254', 1, 'hjfjgfdfggzgzufuzfuzdfgfgfdvfv', 0),
(10, NULL, 'user1', '2009-01-01 17:22:33', '87.118.90.17', 1, 'hjfjgfdfggzgzufuzfuzdfgfgfdvfv', 0),
(11, NULL, 'admin', '2009-01-01 17:22:33', '121.242.65.131', 1, 'hjfjgfdfggzgzufuzfuzdfgfgfdvfv', 0),
(12, NULL, 'admin', '2009-01-01 17:22:33', '202.96.188.101', 1, 'hjfjgfdfggzgzufuzfuzdfgfgfdvfv', 0),
(13, NULL, 'headoffice', '2009-01-01 17:22:33', '118.68.97.90', 1, 'hjfjgfdfggzgzufuzfuzdfgfgfdvfv', 0),
(14, NULL, 'test', '2009-01-01 17:22:33', '125.160.32.182', 0, 'hjfjgfdfggzgzufuzfuzdfgfgfdvfv', 0),
(15, NULL, 'headoffice', '2009-01-01 17:22:33', '70.171.254.160', 1, 'hjfjgfdfggzgzufuzfuzdfgfgfdvfv', 0),
(16, NULL, 'headoffice', '2009-01-01 17:22:33', '89.218.26.20', 1, 'hjfjgfdfggzgzufuzfuzdfgfgfdvfv', 0),
(17, NULL, 'headoffice', '2009-01-01 17:22:33', '118.68.97.45', 1, 'hjfjgfdfggzgzufuzfuzdfgfgfdvfv', 0),
(18, NULL, 'admin', '2009-01-01 17:22:33', '87.118.90.17', 1, 'hjfjgfdfggzgzufuzfuzdfgfgfdvfv', 0);

/* fill application history for changes */
INSERT INTO app_news (
   anw_id,
   anw_date,
   anw_text,
   version) values
(    1, '2009-05-02', 'All listboxes are now working with paging and sorting described in our smalltalk. ([Performance] Paging and Sorting with a Filter Object). Link: http://docs.zkoss.org/wiki/Paging_Sorting_with_a_filter_object',  0),
(    2, '2009-05-15', 'Updated to ZKoss version 3.6.1.',  0),
(    3, '2009-06-11', 'Updated to ZKoss version 3.6.2.',  0),
(    4, '2009-06-29', 'Updated to new gray theme "plomo.jar" from here: (thanks to jlcasas).  http://www.zkoss.org/forum/listComment/6927/ ',  0),
(    5, '2009-06-30', 'Added a guestbook. Please leave your comments here. ',  0),
(    6, '2009-08-02', 'Listheaders now fixed by scrolling through a list. ',  0),
(    7, '2009-08-04', 'Update to ZKoss version 3.6.3. (compiled from source). ',  0),
(    8, '2009-08-25', 'Update to ZKoss version 3.6.3. FL-2009-08-20 ',  0),
(    9, '2009-09-10', 'Added a pie chart to the customer dialog. See tab "Chart". Data created for custoNo "20" and "21" ',  0),
(   10, '2009-09-17', 'Added several new chart typs and icons. ',  0),
(   11, '2009-09-29', 'Added a timer in the main borderlayout south area for date/time.',  0),
(   12, '2009-10-12', 'allow changing between treeMenu and PullDownMenu (Thanks to Björn for codes !). Modifications ',  0),
(   13, '2009-10-21', 'updating to ZK version 3.6.3 FL 2009-10-16 ',  0),
(   14, '2009-10-23', 'Updating to ZK version 3.6.3 FL 2009-10-23 ',  0),
(   15, '2009-10-23', '======>> LAST checkin of the project code in Subversion on code.google as "zk_sample_gui" ',  0),
(   16, '2009-11-13', 'ZkSample2 is now the new project name. ',  0),
(   17, '2009-11-14', 'Refactoring to a spring-managed frontend. Spring-Security can now work with the Annotation @Secured() for securing methods.  Added a simulation of a one-time password tokenizer. (Thanks to Björn for codes !) Added ListFooters in ORDERS module. Clearing some code. Bugfixes. ',  0),
(   18, '2009-11-20', 'Ip2Country locator new in the Login log.',  0),
(   19, '2009-11-22', 'Second Ip2Country locator from a webService implemented.',  0),
(   20, '2009-12-03', 'Refactoring all Domain/DAO/Service Classes to english names.',  0),
(   21, '2009-12-07', 'Moved in all dialog windows the CRUD buttons as imageButtons to the toolbar. ',  0),
(   22, '2009-12-08', 'Replaced the last digits from the Users IP with "xxx" in the gui loginLog list because privacy. ',  0),
(   23, '2009-12-13', 'Tree Menu entries are now openend in Tabs.',  0),
(   24, '2009-12-15', 'Clearing code from old 3.5.x stuff. doOnCreateCommon() in base controllers not used anymore.',  0),
(   25, '2009-12-19', 'Splitted the LoginLog module to 3 zul''s + 3 controllers. Added some statistics',  0),
(   26, '2009-12-22', 'Finished the basic Login Statistic. Hard work!!! Björn writes a mapper that maps the HQL aggregate fields to domain bean properties. Many thanks. ',  0),
(   27, '2009-12-27', 'Changed the complete update mechanism for the ip geo data, because problems on the linux ''server with too many open files'', when calling the Locator Web Service in a loop. Now we imported the geo data from a cvs into a table and can use it from there early by the login process.',  0),
(   28, '2010-01-04', 'We fix the ''too many open files'' on server. Bad boys attack it! Re-using the webservice for getting the geo-data',  0),
(   29, '2010-01-12', 'We work hardly on the last pages of the documentation. The countdown for checkin is running.',  0),
(   30, '2010-01-17', 'Today we checked in this Zksample2 application on SourceForge.net. See thread under http://zkoss.org/forum/listComment/10986 ',  0),
(   31, '2010-01-29', 'Changed the style of the address tab in customers dialog. Changed to comboboxes with icons.',  0),
(   32, '2010-02-04', 'Implementation of the missing Cancel Button in all dialogs.',  0),
(   33, '2010-02-06', 'Updated to the Spring3 framework.',  0),
(   34, '2010-03-02', 'Several design changes. Bugfixes.',  0),
(   35, '2010-03-09', 'Added on all listpages a refresh button (next help button ) for resizing the listbox and recalculate their pagesize for adjusting if the browser size is changed.',  0),
(   36, '2010-03-20', 'Changed the design for the menu and module content area. Hope it''s nice.',  0),
(   37, '2010-04-20', 'Improved speed for the entry page by done the counting of all table recordCounts in one transaction.',  0),
(   38, '2010-05-02', 'Added a first Grid in Grid View for the Hibernate Statistic DB transactions, which is implemented by a Spring Aspect. The view will following in the  next days. The HibernateStatistics measures the time that a DB call needs and shows all dependend model beans.',  0),
(   39, '2010-05-04', 'Zksample2 v2.1 is available on sourceForge.net as ready to use war-file.',  0),
(   40, '2010-05-18', 'Added a PagedBindingListWrapper ( paging/sorting/searching on DB-side ) for the listbox component that can work with zk''s DataBinding mechanism. So the Branch module works now complete databinded. The same for the grid will follow next times.',  0),
(   41, '2010-05-20', 'Added ECHO EVENTs. The long running process of inserting sample customer records now running in an echo event and shows a localized busy message during the process. DOCUMENTATION updated.',  0),
(   42, '2010-05-21', 'Fixing bug in the flow logic of the echo events and an additional modal message.',  0),
(   43, '2010-06-03', 'Replacement of the old serverPush mechanism working with a WorkerThread new to work with a timer. Because this application is spring managed. So our Database transactions are spring managed too. So we are not able to start the WorkerThread in such a session context for calling DB methods. Further it''s limit to 5 calls and self stopped, as long as we logged all DB calls (Hibernate Statistic).',  0),
(   44, '2010-06-29', 'Added a new modal SearchListboxDialog that gives back an OBJECT. Called SimpleSearchBox. We implemented it in the Customer AdressModule for selecting a branch (BranchSimpleSearchBox.java).',  0),
(   45, '2010-06-30', 'Added TWO more SearchListDialog skeletons. Now we have: 1.) SimpleSearchListBox: ZK paging on ServerSide.  2.) AdvancedSearchListBox: DB Paging on Database;  3.) ExtendedSearchListBox: DB Paging + one data limiting feature. All three SearchDialogs are implemented in the CustomerDialog module for selecting a branch. ',  0),
(   46, '2010-06-30', '====== Last Zksample2 version who''s based on zk 3.6.3 =======',  0),
(   47, '2010-07-01', 'Updated the zk framework version to 5.0.3. ',  0),
(   48, '2010-07-02', 'Added a new StatusBarController with the new EventQueues in zk5 that works like a global listener.  Added a new UserBarController with the new EventQueues in zk5 that works like a global listener.',  0),
(   49, '2010-07-04', 'Begin to modifying some modules to work completely with Annotated Databinding mechanism. Decreasing absolut the LoC. These annotated databinded modules have an (adb) after the menu text. All modules who have more than one TAB became a mainModuleController for holding the shared models and beans for all related tabControllers. Read the docu for best Practice on sharing the models and not the binder. Working with an additional css file, declared in the web.xml. ',  0),
(   50, '2010-07-06', 'Checkin of the new Zksample2 based on zk 5.0.3 in /trunk. More changes comes in the next days.',  0),
(   51, '2010-07-09', 'First JasperReport build with the great DynamicJasper framework. Only pure Java, no XML. Report is placed in the Article module. More advanced reports are coming in the next days.',  0),
(   52, '2010-07-10', 'Added two more reports ( OfficeList and BranchesList ), every report needs only 10 min. for adopting from an existing one. A lot of design changes in the Administrator sections for the next coming databinding restructuring.',  0),
(   53, '2010-08-11', 'Added new reports for printing: User-List / Security: Rights-List; Group-List; Roles-List. Some of these report uses the DynamicReportBuilder from DynamicJasper wich allows more control over the report. Additionally we shows in it how to use a CustomExpression. ',  0),
(   54, '2010-08-18', 'Added a new report for printing an Order with it''s positions. Shows how to add a grandTotal sum. ',  0),
(   55, '2010-08-23', 'Begin implementation of the ZK Calendar component.',  0),
(   56, '2010-08-25', 'Updated the doc for the StatusBarController and begin with DynamicJasper. Some code refactorings.',  0),
(   57, '2010-08-27', 'Implemented a first simple messaging system. You can send system wide messages. Incoming messages are visualized with a blinking icon in the left bottom corner. ',  0),
(   58, '2010-09-01', 'Added a modified script file for runing zksample2 with a mySQL 5.1 database. Thanks to ''Andyx''. Read about needed modifications in project zkoss ~/db/mySQL.readme. ',  0),
(   59, '2010-09-07', 'Updated to zk version 5.0.4 ',  0),
(   60, '2010-09-12', 'Updated libs to zk calendar 2.1 (Freshly 12.09.2010) because a init problem. ',  0),
(   61, '2010-09-15', 'Preparing the calendar for create/edit/update events and implemented a DateFormatter for this.',  0),
(   62, '2010-09-17', 'Adding the backend classes for the calendar. Calendar is now Database driven.',  0),
(   63, '2010-09-25', 'Adding a new menu entry for opening our blog. ',  0),
(   64, '2010-10-20', 'SourceForge have changed the documentation address. ',  0),
(   65, '2010-11-02', 'Informing the users, that loading the documentation in an iFrame is in progress. Thanks to Tomek. http://www.zkoss.org/forum/listComment/13738. ',  0),
(   66, '2010-11-05', 'Added a YouTube video iFrame where we would stream weekely our most loved music.',  0),
(   67, '2010-11-08', 'For guys who would build this app from source, we have added a workaround for the JasperReport failure by the maven-build process for this project. See /src/test/resources/readme.txt. Seems this failure comes from the maven3 conversion of JasperReports. Hope the guys repair the corrupt file expressly.',  0),
(   68, '2010-12-15', 'Added the first DashBoard Module. The welcome.zul is renamed to dashboard.zul . This DashBoard module is for selecting a youTube video from a db list. Have fun.',  0),
(   69, '2010-12-16', 'Added the missing methode for randomly selecting a song by the first creation of the Dashboard.  ',  0),
(   70, '2010-12-20', 'Added the second Dashboard module for showing the data inventory. This module have an build in timer for self refreshing which can be activated at creation time. Setup a new zul skeleton for the dashboard. Thanks Madruga and SimonPay for ''pack/stretch'' help. ',  0),
(   71, '2011-01-04', 'Extend the menu taglib used in the mainmenu.xml for the tag ''open''. Now we can open/close a menuItem at creation time. Splash out an ''about'' screen.',  0),
(   72, '2011-01-05', 'Refactoring of the backend. Adding missing comments (english/german). Deleting the old stuff for generating the PrimaryKey for different databases independent from hibernate. Now it works with hibernate controlled sequences or autoIncremented fields.',  0),
(   73, '2011-01-06', 'Bugfixes in the annotated databinding modules. Sorry. ',  0),
(   74, '2011-01-07', 'Thanks to AndyX for making the changes in the script-files for mySQL 5.1. ',  0),
(   75, '2011-01-15', 'Updated zk version to 5.0.6.FL20110112 ',  0),
(   76, '2011-01-15', 'First basic Implementation of the zk spreadsheet 2.0 . ',  0),
(   77, '2011-01-27', 'Updated the Hibernate-Generic-DAO lib to version 0.5.1 . ',  0),
(   78, '2011-01-28', 'Separate Fields for interpret / song title in the youTube Dashboard module. ',  0),
(   79, '2011-02-03', 'Updated zk version to 5.0.6.FL20110202  ',  0),
(   80, '2011-02-22', 'Modifications in the CRUD ButtonController.',  0),
(   81, '2011-03-19', 'Removing the spreadsheet. We will waiting until it''s offered for a maven build system. ',  0),
(   82, '2011-03-24', 'Extended the DAOImpl classes for the @Repository Annotation in conjunction with a new entry in the application-context-db.xml configuration file. These let spring convert the vendor dependent Hibernate Exceptions in common dataAccessExceptions. ',  0),
(   83, '2011-03-29', 'Some code refactorings about the localized DataAccessExceptions and correcting the DAO and Service bean scopes in the backend. Writing a new Chapter for scopes in the Zksample2 documentation. ',  0),
(   84, '2011-04-08', 'Added the counter for the HibernateStatistic table records and new youTube musics. So the Hibernate Performance Stats runs us away, we maximized the possible customers too up to 250.000  ',  0),
(   85, '2011-04-14', 'Inspired by the BusinessPortalLayout we spend a new DashboardModule for BBCNews. ',  0),
(   86, '2011-04-17', 'Added a new DashboardModule for the applications history of changes. Therefore added new model/dao/service stuff. ',  0),
(   87, '2011-04-20', 'Added much more music (vids). Give ''Bullmeister'' a change. Ne-Yo''s original song ''Beautiful Monster'' is not available in my country. So there are re-mixes. Seems we must implement a search for interprets or titles.  Feedback in the Guestbook please :-). ',  0),
(   88, '2011-04-28', 'Added a JavaScript function for let the MessageWindow(Chat window) from the MessageBar scroll automatically to the last entry for every new incoming message. Thanks to dagarwal82. http://www.zkoss.org/forum/listComment/16011 ',  0),
(   89, '2011-05-06', 'Modifications in the CRUD Button Controller. Now it accepts ''Null'' for special Buttons. UseCase is handling an ONE record base entry for special app or firm parameters that needs only the ''edit, save, cancel'' buttons. ',  0),
(   90, '2011-05-18', 'Added the missing ''mySettings'' module for changing the allowed data of the logged in user. The validation of the re-typed password works with an internal created bean for holding the retyped string and is validated at serverside.',  0),
(   91, '2011-05-26', 'Update the zk framework to 5.0.7.1 ',  0),
(   92, '2011-05-30', 'Added a button/method for closing all open tabs except the home/dashboard tab.',  0),
(   93, '2011-06-07', 'Added a new Dashboard module for starting the google translator. Thanks to ''gekkio'' for helping with the correct script components.',  0),
(   94, '2011-06-18', 'Refactoring of the CRUD button-controller. Cleaned up from old stuff and added navigation buttons. See them working in the ''branch'', ''article'' and ''office'' modules.',  0),
(   95, '2011-06-25', 'Lot of design changes for look more equal in chrome and IE.',  0),
(   96, '2011-08-04', 'Changes in the hibernate performance statistic module.',  0),
(   97, '2011-08-11', 'Fixed a bug in the CustomerModule/BranchModule while not checking if special demo data are deleted. Thanks to Reiner.',  0),
(   98, '2011-08-25', 'Fixed a few bugs in old code for orders and customers. Thanks to Thomas for the hint. If i had time i will rewrite the customer and orders modules ',  0),
(   99, '2011-09-23', 'Added a lot of new YouTube music vid links ',  0),
(  100, '2011-09-26', 'Because the often changed BBC News URL. We have add a new Dashboard module for ''The Wall Street Journal''. ',  0),
(  101, '2011-10-17', 'Updated to zk version 5.0.9.FL.20111017. ',  0),
(  102, '2011-11-03', 'Added new YouTube Links. ',  0),
(  103, '2011-11-19', 'Bug fixed in order modul. Hope we can rewrite customer/orders modules new before Christmas. ',  0),
(  104, '2011-12-09', 'Added a few new YouTube music video links. ',  0),
(  105, '2011-12-14', 'Added more new YouTube music video links. Press the button on the right side in the YouTube Dashboard module for the list.',  0),
(  106, '2012-01-04', 'Today we have become a free JRebel licence for Zksample2 from the guys behind ZeroTurnaraound. Many thanks for that.',  0),
(  107, '2012-02-01', 'Updated to zk 5.0.10.',  0),
(  108, '2012-02-03', 'Added an image slideshow to the entry page.',  0),
(  109, '2012-02-08', 'Added a few new YouTube Music Video links. Have a look on the "Donots - Stop the clocks" clip :-) Love that song. What a sexy underwear!',  0),
(  110, '2012-03-17', 'Modifications in the table scripts for let run Zksample2 against a mySQL Database. Many thanks to AndyX',  0),
(  111, '2012-03-30', 'Adding a paging mechanism to DashboardModule Application News.',  0),
(  112, '2012-04-06', 'Extend the documentation a little bit about a chapter ''dashboard modules''.',  0),
(  113, '2012-04-17', 'Added JUnit tests for the backend DAOs. These tests are running against the same sample data used in the main application. The used H2 database was created and filled with data at every test startup. This should be running out of the box. Updated the documentation',  0),
(  114, '2012-04-18', 'Updated Zksample2 to zk version 5.0.11.',  0),
(  115, '2012-04-22', 'Added three new DashboardModules, ''Google News'' and ''Stock Quotes'' and ''German Soccer Stats''.',  0),
(  116, '2012-04-24', 'Added the missing fonts for the report engine. Updated the H2 database from v1.2.126 to v1.3.165 .',  0),
(  117, '2012-07-07', 'Added a few new YouTube music video links. TOYAH  full concert from 1981. See Stevie Nicks entries too.',  0),
(  118, '2012-07-08', 'Added a data paging mechanism to the YouTube Dashboard module.',  0),
(  119, '2012-08-18', 'Added a few new YouTube music video links.',  0),
(  120, '2012-09-04', 'Added lovely music video links for the YouTube Dashboard module.',  0),
(  121, '2012-10-10', 'Added a scheduler who is started at application start. This scheduler runs in background on server side and calls periodically a db cleaning dao method for resetting the admin name/password for the guys who playing with this and do forget to reset to the origins. For the technical guys these method runs outside our web request transaction management via spring AOP who works package name oriented, so there is a new package in the backend sub-project. ',  0),
(  122, '2012-10-18', 'Added a notification system based on gekkio''s ZK-Gritter. Notifications timer are started in IndexCtrl.java ',  0),
(  123, '2012-10-19', 'Modified the notification system so it''s been called from an application scoped ZK Event Queue. See ApplicationMessageQueue.java',  0),
(  124, '2012-10-27', 'Re-animate the button for changing the mode of the main menu between ''TreeMenu'' and ''BarMenu''',  0),
(  125, '2012-11-12', 'Added a lot of YouTube video links.',  0),
(  126, '2012-11-14', 'Code refactoring for the dashboard.zul. Now it''s only a skeleton where the DashBoard Main Controller handels all the modules. ',  0);



/* create the sequences */
CREATE SEQUENCE filiale_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE kunde_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE artikel_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE auftrag_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE auftragposition_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE branche_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE sec_user_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE sec_userrole_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE sec_role_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE sec_rolegroup_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE sec_group_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE sec_groupright_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE sec_right_seq  INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE sys_countrycode_seq INCREMENT BY 1 START WITH 300;
CREATE SEQUENCE sys_ip4country_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE youtube_link_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE app_news_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE hibernate_entity_statistics_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE hibernate_statistics_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE ipc_ip2country_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE sec_loginlog_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE log_ip2country_seq INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE guestbook_seq  INCREMENT BY 1 START WITH 100000;
CREATE SEQUENCE calendar_event_seq INCREMENT BY 1 START WITH 100000;

commit;
