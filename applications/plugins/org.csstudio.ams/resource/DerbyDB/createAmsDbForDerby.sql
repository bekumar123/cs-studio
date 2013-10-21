-- SQL-Skript zum Erzeugen der AMS-Datenbank in Derby
-- Aktuelle Version vom 21.10.2013
-- Änderungen von C1-WPS

CONNECT 'jdbc:derby://localhost/amsdb;create=true';

DROP TABLE AMS_FilterCond_FilterCond;
DROP TABLE AMS_FilterCond_Negation;
DROP TABLE AMS_Filter_Watchdog;
DROP TABLE AMS_Filter_Timebased;
DROP TABLE AMS_Flag;
DROP TABLE AMS_TbFilter_FilterCond;
DROP TABLE AMS_FilterCond_PropCompare;
DROP TABLE AMS_Filter_FilterAction;
DROP TABLE AMS_FilterAction;
DROP TABLE AMS_FilterActionType;
DROP TABLE AMS_Topic;
DROP TABLE AMS_Filter_FilterCondition;
DROP TABLE AMS_Filter;
DROP TABLE AMS_FilterCond_Conj_Common;
DROP TABLE AMS_FilterCondition_PV;
DROP TABLE AMS_FilterCond_TimeBased;
DROP TABLE AMS_FilterCond_ArrStrVal;
DROP TABLE AMS_FilterCond_ArrStr;
DROP TABLE AMS_FilterCondition_String;
DROP TABLE AMS_FilterConditionType;
DROP TABLE AMS_FilterCond_TimeBasedItems;
DROP TABLE AMS_UserGroup_User;
DROP TABLE AMS_UserGroup;
DROP TABLE AMS_User;
DROP TABLE AMS_FilterCond_Junction;
DROP TABLE AMS_FilterCondition;
DROP TABLE AMS_Message;
DROP TABLE AMS_MessageChain;
DROP TABLE AMS_History;

CREATE TABLE AMS_FilterCond_FilterCond
(
   iFilterConditionId INT NOT NULL,
   iFilterConditionRef INT NOT NULL
);

CREATE TABLE AMS_FilterCond_Negation
(
   iFilterConditionRef INT NOT NULL,
   iNegatedFCRef INT NOT NULL
);

CREATE TABLE AMS_User
(
    iUserId         INT NOT NULL,
    iGroupRef       INT DEFAULT -1 NOT NULL,
    cUserName       VARCHAR(128),
    cEmail          VARCHAR(128),
    cMobilePhone    VARCHAR(64),
    cPhone          VARCHAR(64),
    cStatusCode     VARCHAR(32),
    cConfirmCode    VARCHAR(32),
    sActive         SMALLINT,
    sPreferredAlarmingTypeRR SMALLINT,
    PRIMARY KEY (iUserId)
);

CREATE TABLE AMS_UserGroup
(
    iUserGroupId    INT NOT NULL,
    iGroupRef       INT default -1 NOT NULL,
    cUserGroupName  VARCHAR(128),
    sMinGroupMember SMALLINT,
    iTimeOutSec     INT,
    sActive         SMALLINT default 1,
    PRIMARY KEY (iUserGroupId)
);

CREATE TABLE AMS_UserGroup_User
(
    iUserGroupRef   INT NOT NULL,
    iUserRef        INT NOT NULL,
    iPos            INT NOT NULL,
    sActive         SMALLINT,
    cActiveReason   VARCHAR(128),
    tTimeChange     BIGINT,
    PRIMARY KEY(iUserGroupRef,iUserRef,iPos)
);

CREATE TABLE AMS_FilterConditionType
(
    iFilterConditionTypeID  INT,
    cName           VARCHAR(128),
    cClass          VARCHAR(256),
    cClassUI        VARCHAR(256),
    PRIMARY KEY(iFilterConditionTypeID)
);

CREATE TABLE AMS_FilterCondition
(
    iFilterConditionID  INT NOT NULL,
    iGroupRef       INT default -1 NOT NULL,
    cName           VARCHAR(128),
    cDesc           VARCHAR(256),
    iFilterConditionTypeRef INT,
    PRIMARY KEY(iFilterConditionID)
);

CREATE TABLE AMS_FilterCond_Junction
(
   iFilterConditionRef INT NOT NULL,
   operator VARCHAR(3) NOT NULL
);

CREATE TABLE AMS_FilterCondition_String
(
    iFilterConditionRef INT NOT NULL,
    cKeyValue       VARCHAR(16),
    sOperator       SMALLINT,
    cCompValue      VARCHAR(128)
);

CREATE TABLE AMS_FilterCond_ArrStr
(
    iFilterConditionRef INT NOT NULL,
    cKeyValue       VARCHAR(16),
    sOperator       SMALLINT
);

CREATE TABLE AMS_FilterCond_ArrStrVal
(
    iFilterConditionRef INT NOT NULL,
    cCompValue      VARCHAR(128)
);

CREATE TABLE AMS_FilterCond_TimeBased
(
    iFilterConditionRef INT NOT NULL,
    cStartKeyValue      VARCHAR(16),
    sStartOperator      SMALLINT,
    cStartCompValue     VARCHAR(128),
    cConfirmKeyValue    VARCHAR(16),
    sConfirmOperator    SMALLINT,
    cConfirmCompValue   VARCHAR(128),
    sTimePeriod         SMALLINT,
    sTimeBehavior       SMALLINT
);

CREATE TABLE AMS_FilterCond_TimeBasedItems
(
    iItemID         INT,
    iFilterConditionRef INT NOT NULL,
    iFilterRef      INT NOT NULL,
    cIdentifier     VARCHAR(128) NOT NULL,
    sState          SMALLINT,
    tStartTime      BIGINT,
    tEndTime        BIGINT,
    sTimeOutAction  SMALLINT,
    iMessageRef     INT,
    PRIMARY KEY(iItemID)
);

CREATE TABLE AMS_FilterCondition_PV
(
    iFilterConditionRef INT NOT NULL,
    cPvChannelName      VARCHAR(128),
    sSuggestedPvTypeId  SMALLINT,
    sOperatorId         SMALLINT,
    cCompValue          VARCHAR(128)
);

CREATE TABLE AMS_FilterCond_Conj_Common
(
    iFilterConditionRef         INT NOT NULL,
    iFirstFilterConditionRef    INT NOT NULL,
    iSecondFilterConditionRef   INT NOT NULL,
    iOperand                    SMALLINT
);

CREATE TABLE AMS_Filter
(
    iFilterID       INT,
    iGroupRef       INT default -1 NOT NULL,
    cName           VARCHAR(128),
    cDefaultMessage VARCHAR(1024),
    cFilterType     VARCHAR(128) default 'default' NOT NULL,
    PRIMARY KEY (iFilterID)
);

CREATE TABLE AMS_Filter_Timebased
(
    iFilterRef INT NOT NULL, 
    iTimeout INT NOT NULL, 
    iStartFilterConditionRef INT DEFAULT -1 NOT NULL, 
    iStopFilterConditionRef INT DEFAULT -1 NOT NULL, 
    iSendOnTimeout SMALLINT DEFAULT 1 NOT NULL, 
    PRIMARY KEY (iFilterRef)
);

CREATE TABLE AMS_Filter_Watchdog
(
    iFilterRef INT NOT NULL, 
    iTimeout INT NOT NULL, 
    iFilterConditionRef INT DEFAULT -1 NOT NULL, 
    PRIMARY KEY (iFilterRef)
);

CREATE TABLE AMS_Filter_FilterCondition
(
    iFilterRef      INT,
    iFilterConditionRef INT,
    iPos            INT,
    PRIMARY KEY (iFilterRef,iFilterConditionRef)
);

CREATE TABLE AMS_TbFilter_FilterCond
(
    iFilterRef INT NOT NULL,
    iFilterConditionRef INT NOT NULL,
    iTimebasedCondType INT NOT NULL
);

CREATE TABLE AMS_Topic
(
    iTopicId        INT NOT NULL,
    iGroupRef       INT default -1 NOT NULL,
    cTopicName      VARCHAR(128),
    cName           VARCHAR(128),
    cDescription    VARCHAR(256),
    PRIMARY KEY (iTopicId)                      
);

CREATE TABLE AMS_FilterActionType
(
    iFilterActionTypeID INT NOT NULL,
    cName           VARCHAR(128),
    iTopicRef       INT,
    PRIMARY KEY(iFilterActionTypeID)
);

CREATE TABLE AMS_FilterAction
(
    iFilterActionID         INT NOT NULL,
    iFilterActionTypeRef    INT NOT NULL,
    iReceiverRef            INT,
    cMessage                VARCHAR(1024),
    PRIMARY KEY(iFilterActionID)
);

CREATE TABLE AMS_Filter_FilterAction
(
    iFilterRef          INT NOT NULL,
    iFilterActionRef    INT NOT NULL,
    iPos                INT NOT NULL
);

CREATE TABLE AMS_FilterCond_PropCompare  
(
    iFilterConditionRef INT NOT NULL,
    cMessageKeyValue VARCHAR(16) NOT NULL,
    sOperator INT,
    PRIMARY KEY (iFilterConditionRef)
);

CREATE TABLE AMS_Message
(
    iMessageID      INT NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    cProperty       VARCHAR(16),
    cValue          VARCHAR(256)
);

CREATE TABLE AMS_MessageChain
(
    iMessageChainID     INT NOT NULL,
    iMessageRef         INT NOT NULL,
    iFilterRef          INT NOT NULL,
    iFilterActionRef    INT NOT NULL,
    iReceiverPos        INT NOT NULL,
    tSendTime           BIGINT,
    tNextActTime        BIGINT,
    sChainState         SMALLINT,   
    cReceiverAdress     VARCHAR(64),    
    PRIMARY KEY(iMessageChainID)
);

CREATE TABLE AMS_History
(
    iHistoryID      INT NOT NULL GENERATED ALWAYS AS IDENTITY,
    tTimeNew        BIGINT,
    cType           VARCHAR(16),
    cMsgHost        VARCHAR(64),
    cMsgProc        VARCHAR(64),
    cMsgName        VARCHAR(64),
    cMsgEventTime   VARCHAR(32),
    cDescription    VARCHAR(512),
    cActionType     VARCHAR(16),    
    iGroupRef       INT,
    cGroupName      VARCHAR(64),
    iReceiverPos    INT,
    iUserRef        INT,
    cUserName       VARCHAR(128),
    cDestType       VARCHAR(16),    
    cDestAdress     VARCHAR(128),
    PRIMARY KEY(iHistoryID)
);

CREATE TABLE AMS_Flag
(
    cFlagName       VARCHAR(32) NOT NULL,
    sFlagValue      SMALLINT    NOT NULL,
    PRIMARY KEY(cFlagName)
);

INSERT INTO AMS_Flag (cFlagName, sFlagValue) values ('ReplicationState', 0);

DISCONNECT;
