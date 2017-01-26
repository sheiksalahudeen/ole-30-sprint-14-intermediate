CREATE TABLE `OLE_DS_BIB_S` (
  `BIB_ID` INT  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`BIB_ID`)
)
/

ALTER TABLE OLE_DS_BIB_S AUTO_INCREMENT = 10000001
/

CREATE  TABLE `OLE_DS_BIB_T` (
  `BIB_ID` INT NOT NULL,
  `FORMER_ID` VARCHAR(45) DEFAULT NULL ,
  `FAST_ADD` CHAR(1) DEFAULT NULL ,
  `STAFF_ONLY` CHAR(1) DEFAULT NULL ,
  `UPDATED_BY` VARCHAR(40),
  `CREATED_BY` VARCHAR(40),
  `DATE_ENTERED` DATETIME DEFAULT NULL ,
  `DATE_CREATED` DATETIME DEFAULT NULL ,
  `CONTENT` LONGTEXT DEFAULT NULL ,
  `STATUS` VARCHAR(20) DEFAULT NULL ,
  `STATUS_UPDATED_BY` VARCHAR(40),
  `STATUS_UPDATED_DATE` DATETIME DEFAULT NULL ,
  `UNIQUE_ID_PREFIX` VARCHAR(10) DEFAULT NULL ,
  PRIMARY KEY (`BIB_ID`)
)
/

CREATE TABLE `OLE_DS_CALL_NUMBER_TYPE_S` (
  `CALL_NUMBER_TYPE_ID` INT  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`CALL_NUMBER_TYPE_ID`)
)
/

CREATE  TABLE `OLE_DS_CALL_NUMBER_TYPE_T` (
  `CALL_NUMBER_TYPE_ID` INT NOT NULL ,
  `CODE` VARCHAR(100) DEFAULT NULL ,
  `NAME` VARCHAR(500) DEFAULT NULL ,
  PRIMARY KEY (`CALL_NUMBER_TYPE_ID`)
)
/

CREATE TABLE `OLE_DS_RECEIPT_STATUS_S` (
  `RECEIPT_STATUS_ID` INT  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`RECEIPT_STATUS_ID`)
)
/

CREATE  TABLE `OLE_DS_RECEIPT_STATUS_T` (
  `RECEIPT_STATUS_ID` INT NOT NULL,
  `CODE` VARCHAR(100)  DEFAULT NULL ,
  `NAME` VARCHAR(500)  DEFAULT NULL ,
  PRIMARY KEY (`RECEIPT_STATUS_ID`)
)
/

CREATE TABLE `OLE_DS_HOLDINGS_S` (
  `HOLDINGS_ID` INT  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`HOLDINGS_ID`)
)
/



CREATE  TABLE `OLE_DS_HOLDINGS_T` (
  `HOLDINGS_ID` INT NOT NULL ,
  `HOLDINGS_TYPE` VARCHAR(10) DEFAULT NULL ,
  `BIB_ID` INT NOT NULL ,
  `FORMER_HOLDINGS_ID` VARCHAR(45)  DEFAULT NULL ,
  `STAFF_ONLY` CHAR(1) DEFAULT NULL ,
  `LOCATION` VARCHAR(300)  DEFAULT NULL ,
  `LOCATION_LEVEL` VARCHAR(300)  DEFAULT NULL ,
  `SOURCE_HOLDINGS_CONTENT` LONGTEXT DEFAULT NULL ,
  `CALL_NUMBER_PREFIX` VARCHAR(100) DEFAULT NULL ,
  `CALL_NUMBER` VARCHAR(300) DEFAULT NULL ,
  `SHELVING_ORDER` VARCHAR(300) DEFAULT NULL ,
  `CALL_NUMBER_TYPE_ID` INT DEFAULT NULL ,
  `RECEIPT_STATUS_ID` INT DEFAULT NULL ,
  `HOLDINGS_STAT_SEARCH_ID` VARCHAR(40)  DEFAULT NULL ,
  `HOLDINGS_ACCESS_LOCATION_ID` VARCHAR(40)  DEFAULT NULL ,
  `COPY_NUMBER` VARCHAR(20) DEFAULT NULL ,
  `PUBLISHER` VARCHAR(100)  DEFAULT NULL,
  `ACC_STATUS` VARCHAR(40)  DEFAULT NULL,
  `PLATFORM` VARCHAR(100)  DEFAULT NULL,
  `IMPRINT` VARCHAR(100)  DEFAULT NULL,
  `STAT_SRC_CD` VARCHAR(40)  DEFAULT NULL,
  `SUB_STATUS` VARCHAR(40)  DEFAULT NULL,
  `LOCAL_PERSISTENT_LINK` VARCHAR(400)  DEFAULT NULL,
  `ILL_ALLOW` CHAR(1) ,
  `AUTHENTICATION_TYPE_ID` INT  DEFAULT NULL,
  `PROXIED_RESOURCE` VARCHAR(10)  DEFAULT NULL,
  `NO_SIMULT_USERS` VARCHAR(40)  DEFAULT NULL,
  `ACC_LOC` VARCHAR(40)  DEFAULT NULL,
  `ACC_USERNM` VARCHAR(40)  DEFAULT NULL,
  `ACC_PWD` VARCHAR(100)  DEFAULT NULL,
  `STATUS_DATE` DATETIME  DEFAULT NULL,
  `ADMIN_USERNM` VARCHAR(400)  DEFAULT NULL,
  `ADMIN_PWD` VARCHAR(400)  DEFAULT NULL,
  `ADMIN_URL` VARCHAR(400)  DEFAULT NULL,
  `LINK` VARCHAR(400)  DEFAULT NULL,
  `LINK_TEXT` VARCHAR(400)  DEFAULT NULL,
  `E_RES_ID` VARCHAR(400)  DEFAULT NULL,
  `DATE_ENTERED` DATETIME DEFAULT NULL ,
  `DATE_LAST_UPDATED` DATETIME DEFAULT NULL ,
  `UPDATED_BY` VARCHAR(40),
  `CREATED_BY` VARCHAR(40),
  `UNIQUE_ID_PREFIX` VARCHAR(10)  DEFAULT NULL ,
  PRIMARY KEY (`HOLDINGS_ID`),
  KEY `HOLDINGS_BIB_CONSTRAINT` (`BIB_ID`),
  CONSTRAINT `HOLDINGS_BIB_CONSTRAINT` FOREIGN KEY (`BIB_ID`) REFERENCES `OLE_DS_BIB_T` (`BIB_ID`),
  KEY `CALL_NUMBER_TYPE_CONSTRAINT` (`CALL_NUMBER_TYPE_ID`),
  CONSTRAINT `CALL_NUMBER_TYPE_CONSTRAINT` FOREIGN KEY (`CALL_NUMBER_TYPE_ID`) REFERENCES `OLE_DS_CALL_NUMBER_TYPE_T` (`CALL_NUMBER_TYPE_ID`) ,
   KEY `RECEIPT_CONSTRAINT` (`RECEIPT_STATUS_ID`),
  CONSTRAINT `RECEIPT_CONSTRAINT` FOREIGN KEY (`RECEIPT_STATUS_ID`) REFERENCES `OLE_DS_RECEIPT_STATUS_T` (`RECEIPT_STATUS_ID`)
)
/


CREATE TABLE `OLE_DS_BIB_HOLDINGS_S` (
  `BIB_HOLDINGS_ID` INT  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`BIB_HOLDINGS_ID`)
)
/

CREATE TABLE `OLE_DS_BIB_HOLDINGS_T` (
  `BIB_HOLDINGS_ID` INT ,
  `HOLDINGS_ID` INT NOT NULL ,
  `BIB_ID` INT NOT NULL ,
  PRIMARY KEY (`BIB_HOLDINGS_ID`) ,
  UNIQUE(`BIB_ID`,`HOLDINGS_ID`),
  KEY `BIB_HOLD_BIB_CNSTRT` (`BIB_ID`),
   CONSTRAINT `BIB_HOL_BIB_CNSTRT` FOREIGN KEY (`BIB_ID`) REFERENCES `OLE_DS_BIB_T` (`BIB_ID`),
  KEY `BIB_HOLD_HOLD_CNSTRT` (`HOLDINGS_ID`),
    CONSTRAINT `BIB_HOLD_HOLD_CNSTRT` FOREIGN KEY (`HOLDINGS_ID`) REFERENCES `OLE_DS_HOLDINGS_T` (`HOLDINGS_ID`)
)
/




CREATE TABLE `OLE_DS_HOLDINGS_ACCESS_URI_S` (
  `ACCESS_INFO_ID` INT  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`ACCESS_INFO_ID`)
)
/

CREATE  TABLE `OLE_DS_HOLDINGS_ACCESS_URI_T` (
  `ACCESS_URI_ID` INT NOT NULL ,
  `TEXT` VARCHAR(500) DEFAULT NULL ,
  `URI` VARCHAR(500) DEFAULT NULL ,
  `HOLDINGS_ID` INT NOT NULL ,
  PRIMARY KEY (`ACCESS_URI_ID`),
   KEY `ACCESS_URI_CONSTRAINT` (`HOLDINGS_ID`),
  CONSTRAINT `ACCESS_URI_CONSTRAINT` FOREIGN KEY (`HOLDINGS_ID`) REFERENCES `OLE_DS_HOLDINGS_T` (`HOLDINGS_ID`)
)
/

CREATE TABLE `OLE_DS_EXT_OWNERSHIP_TYPE_S` (
  `EXT_OWNERSHIP_TYPE_ID` INT  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`EXT_OWNERSHIP_TYPE_ID`)
)
/

CREATE  TABLE `OLE_DS_EXT_OWNERSHIP_TYPE_T` (
  `EXT_OWNERSHIP_TYPE_ID` INT NOT NULL,
  `CODE` VARCHAR(100)  DEFAULT NULL ,
  `NAME` VARCHAR(500)  DEFAULT NULL ,
  PRIMARY KEY (`EXT_OWNERSHIP_TYPE_ID`)
)
/

CREATE TABLE `OLE_DS_EXT_OWNERSHIP_S` (
  `EXT_OWNERSHIP_ID` INT  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`EXT_OWNERSHIP_ID`)
)
/

CREATE  TABLE `OLE_DS_EXT_OWNERSHIP_T` (
  `EXT_OWNERSHIP_ID` INT NOT NULL,
  `EXT_OWNERSHIP_TYPE_ID` INT DEFAULT NULL,
  `TEXT` TEXT DEFAULT NULL ,
  `HOLDINGS_ID` INT  DEFAULT NULL ,
  `ORD` INT  DEFAULT NULL ,
   PRIMARY KEY (`EXT_OWNERSHIP_ID`),
   KEY `EXT_OWNERSHIP_CONSTRAINT` (`HOLDINGS_ID`),
   CONSTRAINT `EXT_OWNERSHIP_CONSTRAINT` FOREIGN KEY (`HOLDINGS_ID`) REFERENCES `OLE_DS_HOLDINGS_T` (`HOLDINGS_ID`),
   KEY `EXT_OWNERSHIP_TYPE_CONSTRAINT` (`EXT_OWNERSHIP_TYPE_ID`),
   CONSTRAINT `EXT_OWNERSHIP_TYPE_CONSTRAINT` FOREIGN KEY (`EXT_OWNERSHIP_TYPE_ID`) REFERENCES `OLE_DS_EXT_OWNERSHIP_TYPE_T` (`EXT_OWNERSHIP_TYPE_ID`)
)
/

CREATE TABLE `OLE_DS_EXT_NOTE_S` (
  `EXT_NOTE_ID` INT  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`EXT_NOTE_ID`)
)
/

CREATE  TABLE `OLE_DS_EXT_NOTE_T` (
  `EXT_NOTE_ID` INT NOT NULL,
  `TYPE` VARCHAR(20)  DEFAULT NULL ,
  `NOTE` TEXT DEFAULT NULL ,
  `EXT_OWNERSHIP_ID` INT NOT NULL ,
  PRIMARY KEY (`EXT_NOTE_ID`),
  KEY `EXT_NOTE_CONSTRAINT` (`EXT_OWNERSHIP_ID`),
  CONSTRAINT `EXT_NOTE_CONSTRAINT` FOREIGN KEY (`EXT_OWNERSHIP_ID`) REFERENCES `OLE_DS_EXT_OWNERSHIP_T` (`EXT_OWNERSHIP_ID`)
)
/

CREATE TABLE `OLE_DS_HOLDINGS_NOTE_S` (
  `HOLDINGS_NOTE_ID` INT  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`HOLDINGS_NOTE_ID`)
)
/

CREATE  TABLE `OLE_DS_HOLDINGS_NOTE_T` (
  `HOLDINGS_NOTE_ID` INT NOT NULL ,
  `TYPE` VARCHAR(100) DEFAULT NULL ,
  `NOTE` TEXT DEFAULT NULL ,
  `HOLDINGS_ID` INT NOT NULL ,
  PRIMARY KEY (`HOLDINGS_NOTE_ID`),
  KEY `HOLDINGS_NOTE_CONSTRAINT` (`HOLDINGS_ID`),
  CONSTRAINT `HOLDINGS_NOTE_CONSTRAINT` FOREIGN KEY (`HOLDINGS_ID`) REFERENCES `OLE_DS_HOLDINGS_T` (`HOLDINGS_ID`)
)
/





CREATE TABLE `OLE_DS_STAT_SEARCH_CODE_S` (
  `STAT_SEARCH_CODE_ID` INT  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`STAT_SEARCH_CODE_ID`)
)
/

CREATE  TABLE `OLE_DS_STAT_SEARCH_CODE_T` (
  `STAT_SEARCH_CODE_ID` INT NOT NULL,
  `CODE` VARCHAR(100) DEFAULT NULL ,
  `NAME` VARCHAR(500) DEFAULT NULL ,
  PRIMARY KEY (`STAT_SEARCH_CODE_ID`)
)
/

CREATE TABLE `OLE_DS_ITEM_TYPE_S` (
  `ITEM_TYPE_ID` INT  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`ITEM_TYPE_ID`)
)
/

CREATE  TABLE `OLE_DS_ITEM_TYPE_T` (
  `ITEM_TYPE_ID` INT NOT NULL ,
  `CODE` VARCHAR(100) NULL ,
  `NAME` VARCHAR(500) NULL ,
  PRIMARY KEY (`ITEM_TYPE_ID`)
)
/

CREATE TABLE `OLE_DS_ITEM_STATUS_S` (
  `ITEM_STATUS_ID` INT(19)  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`ITEM_STATUS_ID`)
)
/

CREATE  TABLE `OLE_DS_ITEM_STATUS_T` (
  `ITEM_STATUS_ID` INT NOT NULL  ,
  `CODE` VARCHAR(100)  DEFAULT NULL ,
  `NAME` VARCHAR(500)  DEFAULT NULL ,
  PRIMARY KEY (`ITEM_STATUS_ID`)
)
/


CREATE TABLE `OLE_DS_HIGH_DENSITY_STORAGE_S` (
  `HIGH_DENSITY_STORAGE_ID` INT(19)  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`HIGH_DENSITY_STORAGE_ID`)
)
/

CREATE  TABLE `OLE_DS_HIGH_DENSITY_STORAGE_T` (
  `HIGH_DENSITY_STORAGE_ID` INT NOT NULL,
  `HIGH_DENSITY_ROW` VARCHAR(30)  DEFAULT NULL ,
  `HIGH_DENSITY_MODULE` VARCHAR(30)  DEFAULT NULL ,
  `HIGH_DENSITY_SHELF` VARCHAR(30)  DEFAULT NULL ,
  `HIGH_DENSITY_TRAY` VARCHAR(30)  DEFAULT NULL ,
  PRIMARY KEY (`HIGH_DENSITY_STORAGE_ID`)
)
/

CREATE TABLE `OLE_DS_ITEM_S` (
  `ITEM_ID` INT  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`ITEM_ID`)
)
/

CREATE  TABLE `OLE_DS_ITEM_T` (
  `ITEM_ID` INT NOT NULL ,
  `STAFF_ONLY` CHAR(1) DEFAULT NULL ,
  `BARCODE` VARCHAR(20) DEFAULT NULL ,
  `BARCODE_ARSL` VARCHAR(300) DEFAULT NULL ,
  `TEMP_ITEM_TYPE_ID` INT  DEFAULT NULL ,
  `ENUMERATION` VARCHAR(100)  DEFAULT NULL ,
  `CHRONOLOGY` VARCHAR(100)  DEFAULT NULL ,
  `COPY_NUMBER` VARCHAR(20)  DEFAULT NULL ,
  `URI` VARCHAR(400)  DEFAULT NULL ,
  `ITEM_TYPE_ID` INT  DEFAULT NULL ,
  `NUM_PIECES` VARCHAR(10)  DEFAULT NULL ,
  `UNIQUE_ID_PREFIX` VARCHAR(10)  DEFAULT NULL ,
  `HOLDINGS_ID` INT NOT NULL ,
  `PURCHASE_ORDER_LINE_ITEM_ID` VARCHAR(45)  DEFAULT NULL ,
  `VENDOR_LINE_ITEM_ID` VARCHAR(45)  DEFAULT NULL ,
  `FUND` VARCHAR(300)  DEFAULT NULL ,
  `PRICE` VARCHAR(20) DEFAULT NULL ,
  `ITEM_STATUS_ID` INT  DEFAULT NULL ,
  `ITEM_STAT_SEARCH_ID` VARCHAR(40) DEFAULT NULL,
  `CHECK_IN_NOTE` VARCHAR(255)  DEFAULT NULL ,
  `EFFECTIVE_DATE` DATE DEFAULT NULL ,
  `FAST_ADD` CHAR(1) DEFAULT NULL ,
  `LOCATION` VARCHAR(600)  DEFAULT NULL ,
  `LOCATION_LEVEL` VARCHAR(600)  DEFAULT NULL ,
  `CALL_NUMBER_PREFIX` VARCHAR(100) DEFAULT NULL ,
  `CALL_NUMBER` VARCHAR(300) DEFAULT NULL ,
  `SHELVING_ORDER` VARCHAR(300) DEFAULT NULL ,
  `CALL_NUMBER_TYPE_ID` INT DEFAULT NULL ,
  `HIGH_DENSITY_STORAGE_ID` INT  DEFAULT NULL ,
  `CLMS_RET_FLAG` CHAR(1) ,
  `CLMS_RET_FLAG_CRE_DATE` DATETIME  DEFAULT NULL ,
  `CLMS_RET_NOTE` VARCHAR(255) DEFAULT  NULL,
  `CURRENT_BORROWER` VARCHAR(30) DEFAULT NULL,
  `PROXY_BORROWER` VARCHAR(30)  DEFAULT NULL,
  `DUE_DATE_TIME` DATETIME  DEFAULT NULL,
  `ITEM_DMG_STATUS` CHAR(1) DEFAULT NULL ,
  `ITEM_MISING_PICS_FLAG` CHAR(1) DEFAULT NULL ,
  `MISING_PICS_NOTE` VARCHAR(600)  DEFAULT NULL ,
  `MISING_PICS_EFFECTIVE_DATE` DATE DEFAULT NULL ,
  `MISING_PICS_COUNT` INT,
  `ITEM_DMG_NOTE` VARCHAR(600)  DEFAULT NULL ,
  `DATE_ENTERED` DATETIME DEFAULT NULL ,
  `DATE_LAST_UPDATED` DATETIME DEFAULT NULL ,
  `UPDATED_BY` VARCHAR(40),
  `CREATED_BY` VARCHAR(40),
  PRIMARY KEY (`ITEM_ID`) ,
  KEY `ITEM_STATUS_CONSTRAINT` (`ITEM_STATUS_ID`),
  CONSTRAINT `ITEM_STATUS_CONSTRAINT` FOREIGN KEY (`ITEM_STATUS_ID`) REFERENCES `OLE_DS_ITEM_STATUS_T` (`ITEM_STATUS_ID`),
  KEY `ITEM_HOLDINGS_CONSTRAINT` (`HOLDINGS_ID`),
  CONSTRAINT `ITEM_HOLDINGS_CONSTRAINT` FOREIGN KEY (`HOLDINGS_ID`) REFERENCES `OLE_DS_HOLDINGS_T` (`HOLDINGS_ID`),
  KEY `ITEM_TYPE_CONSTRAINT` (`ITEM_TYPE_ID`),
  CONSTRAINT `ITEM_TYPE_CONSTRAINT` FOREIGN KEY (`ITEM_TYPE_ID`) REFERENCES `OLE_DS_ITEM_TYPE_T` (`ITEM_TYPE_ID`),
  KEY `ITEM_TEMP_TYPE_CONSTRAINT` (`TEMP_ITEM_TYPE_ID`),
  CONSTRAINT `ITEM_TEMP_TYPE_CONSTRAINT` FOREIGN KEY (`TEMP_ITEM_TYPE_ID`) REFERENCES `OLE_DS_ITEM_TYPE_T` (`ITEM_TYPE_ID`),
  KEY `CALLNUMBER_TYPE_CONSTRAINT` (`CALL_NUMBER_TYPE_ID`),
  CONSTRAINT `CALLNUMBER_TYPE_CONSTRAINT` FOREIGN KEY (`CALL_NUMBER_TYPE_ID`) REFERENCES `OLE_DS_CALL_NUMBER_TYPE_T` (`CALL_NUMBER_TYPE_ID`) ,
   KEY `HIGH_DENSITY_STORAGE_CONSTRAINT` (`HIGH_DENSITY_STORAGE_ID`),
  CONSTRAINT `HIGH_DENSITY_STORAGE_CONSTRAINT` FOREIGN KEY (`HIGH_DENSITY_STORAGE_ID`) REFERENCES `OLE_DS_HIGH_DENSITY_STORAGE_T` (`HIGH_DENSITY_STORAGE_ID`)
)
/

CREATE TABLE `OLE_DS_FORMER_IDENTIFIER_S` (
  `FORMER_IDENTIFIER_ID` INT  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`FORMER_IDENTIFIER_ID`)
)
/

CREATE  TABLE `OLE_DS_FORMER_IDENTIFIER_T` (
  `FORMER_IDENTIFIER_ID` INT NOT NULL  ,
  `VALUE` VARCHAR(100)  DEFAULT NULL ,
  `TYPE` VARCHAR(500)  DEFAULT NULL ,
  `ITEM_ID` INT  NOT NULL ,
  PRIMARY KEY (`FORMER_IDENTIFIER_ID`),
  KEY `FORMER_CONSTRAINT` (`ITEM_ID`),
  CONSTRAINT `FORMER_CONSTRAINT` FOREIGN KEY (`ITEM_ID`) REFERENCES `OLE_DS_ITEM_T` (`ITEM_ID`)
)
/

CREATE TABLE `OLE_DS_ITEM_NOTE_S` (
  `ITEM_NOTE_ID` INT  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`ITEM_NOTE_ID`)
)
/

CREATE  TABLE `OLE_DS_ITEM_NOTE_T` (
  `ITEM_NOTE_ID` INT NOT NULL,
  `TYPE` VARCHAR(50)  DEFAULT NULL ,
  `NOTE` TEXT DEFAULT NULL ,
  `ITEM_ID` INT  DEFAULT NULL ,
  PRIMARY KEY (`ITEM_NOTE_ID`) ,
   KEY `ITEM_NOTE_CONSTRAINT` (`ITEM_ID`),
  CONSTRAINT `ITEM_NOTE_CONSTRAINT` FOREIGN KEY (`ITEM_ID`) REFERENCES `OLE_DS_ITEM_T` (`ITEM_ID`)
)
/


CREATE TABLE `OLE_DS_LOC_CHECKIN_COUNT_S` (
  `CHECK_IN_LOCATION_ID` INT  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`CHECK_IN_LOCATION_ID`)
)
/



CREATE  TABLE `OLE_DS_LOC_CHECKIN_COUNT_T` (
  `CHECK_IN_LOCATION_ID` INT NOT NULL  ,
  `LOCATION_NAME` VARCHAR(200)  DEFAULT NULL ,
  `LOCATION_COUNT` VARCHAR(20)  DEFAULT NULL ,
  `LOCATION_IN_HOUSE_COUNT` VARCHAR(20)  DEFAULT NULL ,
  `ITEM_ID` INT  NOT NULL ,
  PRIMARY KEY (`CHECK_IN_LOCATION_ID`),
  KEY `CHECK_IN_LOCATION_CONSTRAINT` (`ITEM_ID`),
  CONSTRAINT `LOCATION_COUNT_CONSTRAINT` FOREIGN KEY (`ITEM_ID`) REFERENCES `OLE_DS_ITEM_T` (`ITEM_ID`)
)
/
 CREATE TABLE `OLE_DS_COVERAGE_S` (
  `EHOLDINGS_COVERAGE_ID` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`EHOLDINGS_COVERAGE_ID`)
)
/
 CREATE TABLE `OLE_DS_COVERAGE_T` (
  `EHOLDINGS_COVERAGE_ID` INT  NOT NULL,
  `HOLDINGS_ID` INT  DEFAULT NULL,
  `COVERAGE_START_DATE` DATE DEFAULT NULL,
  `COVERAGE_START_VOLUME` VARCHAR(40)  DEFAULT NULL,
  `COVERAGE_START_ISSUE` VARCHAR(40)  DEFAULT NULL,
  `COVERAGE_END_DATE` DATE  DEFAULT NULL,
  `COVERAGE_END_VOLUME` VARCHAR(40)  DEFAULT NULL,
  `COVERAGE_END_ISSUE` VARCHAR(40)  DEFAULT NULL,
  PRIMARY KEY (`EHOLDINGS_COVERAGE_ID`),
  KEY `EHOLDINGS_COVERAGE_CONSTRAINT` (`HOLDINGS_ID`),
  CONSTRAINT `EHOLDINGS_COVERAGE_CONSTRAINT` FOREIGN KEY (`HOLDINGS_ID`) REFERENCES `OLE_DS_HOLDINGS_T` (`HOLDINGS_ID`)
)
 /
 CREATE TABLE `OLE_DS_PERPETUAL_ACCESS_S` (
  `EHOLDINGS_PERPETUAL_ACCESS_ID` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`EHOLDINGS_PERPETUAL_ACCESS_ID`)
)
/
 CREATE TABLE `OLE_DS_PERPETUAL_ACCESS_T` (
  `EHOLDINGS_PERPETUAL_ACCESS_ID` INT  NOT NULL,
  `HOLDINGS_ID` INT  DEFAULT NULL,
  `PERPETUAL_ACCESS_START_DATE` DATE  DEFAULT NULL,
  `PERPETUAL_ACCESS_START_VOLUME` VARCHAR(40)  DEFAULT NULL,
  `PERPETUAL_ACCESS_START_ISSUE` VARCHAR(40)  DEFAULT NULL,
  `PERPETUAL_ACCESS_END_DATE` DATE  DEFAULT NULL,
  `PERPETUAL_ACCESS_END_VOLUME` VARCHAR(40)  DEFAULT NULL,
  `PERPETUAL_ACCESS_END_ISSUE` VARCHAR(40)  DEFAULT NULL,
  PRIMARY KEY (`EHOLDINGS_PERPETUAL_ACCESS_ID`),
  KEY `EHOLDINGS_PER_ACC_CONSTRAINT` (`HOLDINGS_ID`),
  CONSTRAINT `EHOLDINGS_PER_ACC_CONSTRAINT` FOREIGN KEY (`HOLDINGS_ID`) REFERENCES `OLE_DS_HOLDINGS_T` (`HOLDINGS_ID`)
)
 /
 CREATE TABLE `OLE_DS_AUTHENTICATION_S` (
  `AUTHENTICATION_TYPE_ID` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`AUTHENTICATION_TYPE_ID`)
)
/
 CREATE TABLE `OLE_DS_AUTHENTICATION_T` (
  `AUTHENTICATION_TYPE_ID` INT  NOT NULL,
  `CODE` VARCHAR(100) DEFAULT NULL ,
  `NAME` VARCHAR(500) DEFAULT NULL ,
  PRIMARY KEY (`AUTHENTICATION_TYPE_ID`)
)
/
 CREATE TABLE `OLE_DS_ACCESS_LOCATION_S` (
  `ACCESS_LOCATION_ID` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`ACCESS_LOCATION_ID`)
)
/
 CREATE TABLE `OLE_DS_ACCESS_LOCATION_T` (
  `ACCESS_LOCATION_ID` INT  NOT NULL,
  `CODE` VARCHAR(100) DEFAULT NULL ,
  `NAME` VARCHAR(500) DEFAULT NULL ,
  PRIMARY KEY (`ACCESS_LOCATION_ID`)
)
/
 CREATE TABLE `OLE_DS_HOLDINGS_ACCESS_LOC_S` (
  `HOLDINGS_ACCESS_LOCATION_ID` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`HOLDINGS_ACCESS_LOCATION_ID`)
)
/
 CREATE TABLE `OLE_DS_HOLDINGS_ACCESS_LOC_T` (
  `HOLDINGS_ACCESS_LOCATION_ID` INT  NOT NULL,
  `HOLDINGS_ID` INT DEFAULT NULL ,
  `ACCESS_LOCATION_ID` INT DEFAULT NULL ,
  PRIMARY KEY (`HOLDINGS_ACCESS_LOCATION_ID`)
)
/
 CREATE TABLE `OLE_DS_HOLDINGS_STAT_SEARCH_S` (
  `HOLDINGS_STAT_SEARCH_T` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`HOLDINGS_STAT_SEARCH_T`)
)
/
 CREATE TABLE `OLE_DS_HOLDINGS_STAT_SEARCH_T` (
  `HOLDINGS_STAT_SEARCH_ID` INT  NOT NULL,
  `HOLDINGS_ID` INT DEFAULT NULL ,
  `STAT_SEARCH_CODE_ID` INT DEFAULT NULL ,
  PRIMARY KEY (`HOLDINGS_STAT_SEARCH_ID`),
  KEY `HOLDINGS_STAT_CONSTRAINT` (`STAT_SEARCH_CODE_ID`),
  CONSTRAINT `HOLDINGS_STAT_CONSTRAINT` FOREIGN KEY (`STAT_SEARCH_CODE_ID`) REFERENCES `OLE_DS_STAT_SEARCH_CODE_T` (`STAT_SEARCH_CODE_ID`)
)
/
CREATE TABLE `OLE_DS_ITEM_STAT_SEARCH_S` (
  `ITEM_STAT_SEARCH_T` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`ITEM_STAT_SEARCH_T`)
)
/
 CREATE TABLE `OLE_DS_ITEM_STAT_SEARCH_T` (
  `ITEM_STAT_SEARCH_ID` INT  NOT NULL,
  `ITEM_ID` INT DEFAULT NULL ,
  `STAT_SEARCH_CODE_ID` INT DEFAULT NULL ,
  PRIMARY KEY (`ITEM_STAT_SEARCH_ID`),
  KEY `ITEM_STAT_CONSTRAINT` (`STAT_SEARCH_CODE_ID`),
  CONSTRAINT `ITEM_STAT_CONSTRAINT` FOREIGN KEY (`STAT_SEARCH_CODE_ID`) REFERENCES `OLE_DS_STAT_SEARCH_CODE_T` (`STAT_SEARCH_CODE_ID`)
)
/
CREATE TABLE `OLE_DS_ITEM_DONOR_S` (
  `ITEM_DONOR_ID` INT  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`ITEM_DONOR_ID`)
)
/

CREATE  TABLE `OLE_DS_ITEM_DONOR_T` (
  `ITEM_DONOR_ID` INT NOT NULL,
  `DONOR_CODE` VARCHAR(10)  DEFAULT NULL ,
  `DONOR_PUBLIC_DISPLAY` VARCHAR(50)  DEFAULT NULL ,
  `DONOR_NOTE` VARCHAR(50)  DEFAULT NULL ,
  `ITEM_ID` INT  DEFAULT NULL ,
  PRIMARY KEY (`ITEM_DONOR_ID`) ,
   KEY `ITEM_DONOR_CONSTRAINT` (`ITEM_ID`),
  CONSTRAINT `ITEM_DONOR_CONSTRAINT` FOREIGN KEY (`ITEM_ID`) REFERENCES `OLE_DS_ITEM_T` (`ITEM_ID`)
)
/
CREATE TABLE `OLE_DS_HOLDINGS_DONOR_S` (
  `HOLDINGS_DONOR_ID` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`HOLDINGS_DONOR_ID`)
)
/
 CREATE TABLE `OLE_DS_HOLDINGS_DONOR_T` (
  `HOLDINGS_DONOR_ID` INT  NOT NULL,
  `DONOR_CODE` VARCHAR(10)  DEFAULT NULL ,
  `DONOR_PUBLIC_DISPLAY` VARCHAR(50)  DEFAULT NULL ,
  `DONOR_NOTE` VARCHAR(50)  DEFAULT NULL ,
  `HOLDINGS_ID` INT DEFAULT NULL ,
  PRIMARY KEY (`HOLDINGS_DONOR_ID`),
  CONSTRAINT `HOLDINGS_DONOR_CONSTRAINT` FOREIGN KEY (`HOLDINGS_ID`) REFERENCES `OLE_DS_HOLDINGS_T` (`HOLDINGS_ID`)
)
/
