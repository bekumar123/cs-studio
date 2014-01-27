-- SQL-Skript zum Erzeugen der AMS-Datenbank in HSQLDB (In-Memory)
-- Aktuelle Version vom 03.12.2013
-- �nderungen von C1-WPS

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
    iUserId INT NOT NULL, 
    iGroupRef INT DEFAULT -1 NOT NULL, 
    cUserName VARCHAR(128), 
    cEmail VARCHAR(128), 
    CMOBILEPHONE VARCHAR(64), 
    CPHONE VARCHAR(64), 
    CSTATUSCODE VARCHAR(32), 
    CCONFIRMCODE VARCHAR(32), 
    SACTIVE SMALLINT, 
    SPREFERREDALARMINGTYPERR SMALLINT, 
    PRIMARY KEY (iUserId)
);

CREATE TABLE AMS_UserGroup 
(
    iUserGroupId INT NOT NULL, 
    iGroupRef INT DEFAULT -1 NOT NULL, 
    cUserGroupName VARCHAR(128), 
    sMinGroupMember SMALLINT, 
    iTimeOutSec INT, 
    sActive SMALLINT, 
    PRIMARY KEY (iUserGroupId)
);

CREATE TABLE AMS_UserGroup_User 
(
    iUserGroupRef INT NOT NULL, 
    iUserRef INT NOT NULL, 
    iPos INT NOT NULL, 
    sActive SMALLINT, 
    cActiveReason VARCHAR(128), 
    tTimeChange BIGINT, 
    PRIMARY KEY (iUserGroupRef, iUserRef, iPos)
);

CREATE TABLE AMS_FilterConditionType 
(
    iFilterConditionTypeID INT, 
    cName VARCHAR(128), 
    cClass VARCHAR(256), 
    cClassUI VARCHAR(256), 
    PRIMARY KEY (iFilterConditionTypeID)
);

CREATE TABLE AMS_FilterCondition 
(
    iFilterConditionID INT NOT NULL, 
    iGroupRef INT DEFAULT -1 NOT NULL, 
    cName VARCHAR(128), 
    cDesc VARCHAR(256), 
    iFilterConditionTypeRef INT, 
    PRIMARY KEY (iFilterConditionID)
);

CREATE TABLE AMS_FilterCond_Junction
(
   iFilterConditionRef INT NOT NULL,
   operator VARCHAR(3) NOT NULL
);

CREATE TABLE AMS_FilterCondition_String 
(   
    iFilterConditionRef INT NOT NULL, 
    cKeyValue VARCHAR(16), 
    sOperator SMALLINT, 
    cCompValue VARCHAR(128)
);

CREATE TABLE AMS_FilterCond_ArrStr 
(
    iFilterConditionRef INT NOT NULL, 
    cKeyValue VARCHAR(16), 
    sOperator SMALLINT
);

CREATE TABLE AMS_FilterCond_ArrStrVal 
(   
    iFilterConditionRef INT NOT NULL, 
    cCompValue VARCHAR(128)
);

CREATE TABLE AMS_FilterCond_TimeBased 
(   
    iFilterConditionRef INT NOT NULL, 
    cStartKeyValue VARCHAR(16), 
    sStartOperator SMALLINT, 
    cStartCompValue VARCHAR(128), 
    cConfirmKeyValue VARCHAR(16), 
    sConfirmOperator SMALLINT, 
    cConfirmCompValue VARCHAR(128), 
    sTimePeriod SMALLINT, 
    sTimeBehavior SMALLINT
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
    cPvChannelName VARCHAR(128), 
    sSuggestedPvTypeId SMALLINT, 
    sOperatorId SMALLINT, 
    cCompValue VARCHAR(128)
);

CREATE TABLE AMS_FilterCond_Conj_Common 
(
    iFilterConditionRef INT NOT NULL, 
    iFirstFilterConditionRef INT NOT NULL, 
    iSecondFilterConditionRef INT NOT NULL, 
    iOperand SMALLINT
);

CREATE TABLE AMS_Filter 
(
    iFilterID INT, 
    iGroupRef INT DEFAULT -1 NOT NULL, 
    cName VARCHAR(128), 
    cDefaultMessage VARCHAR(1024), 
    cFilterType VARCHAR(100) DEFAULT 'default' NOT NULL, 
    PRIMARY KEY (iFilterID)
);

CREATE TABLE AMS_Filter_Timebased 
(
    iFilterRef INT NOT NULL, 
    iTimeout INT NOT NULL, 
    iStartFilterConditionRef INT DEFAULT -1 NOT NULL, 
    iStopFilterConditionRef INT DEFAULT -1 NOT NULL, 
    iSendOnTimeout INT DEFAULT 1 NOT NULL, 
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
    iFilterRef INT, 
    iFilterConditionRef INT, 
    iPos INT, 
    PRIMARY KEY (iFilterRef, iFilterConditionRef)
);

CREATE TABLE AMS_TbFilter_FilterCond
(
    iFilterRef INT NOT NULL,
    iFilterConditionRef INT NOT NULL,
    iTimebasedCondType INT NOT NULL
);

CREATE TABLE AMS_Topic 
(
    iTopicId INT NOT NULL, 
    iGroupRef INT DEFAULT -1 NOT NULL, 
    cTopicName VARCHAR(128), 
    cName VARCHAR(128), 
    cDescription VARCHAR(256), 
    PRIMARY KEY (iTopicId)
);

CREATE TABLE AMS_FilterActionType 
(
    iFilterActionTypeID INT NOT NULL, 
    cName VARCHAR(128), 
    iTopicRef INT, 
    PRIMARY KEY (iFilterActionTypeID)
);

CREATE TABLE AMS_FilterAction 
(
    iFilterActionID INT NOT NULL, 
    iFilterActionTypeRef INT NOT NULL, 
    iReceiverRef INT, 
    cMessage VARCHAR(1024), 
    PRIMARY KEY (iFilterActionID)
);

CREATE TABLE AMS_Filter_FilterAction 
(
    iFilterRef INT NOT NULL, 
    iFilterActionRef INT NOT NULL, 
    iPos INT NOT NULL
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
    iMessageID      INTEGER GENERATED BY DEFAULT AS IDENTITY,
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

CREATE TABLE  AMS_Msg_Ext_Pvs 
(
    id BIGINT, 
    iGroupRef BIGINT, 
    cPVName VARCHAR(128), 
    PRIMARY KEY (id)
);

CREATE TABLE  AMS_Msg_Extensions
(
    idRef BIGINT, 
    cMessageKey VARCHAR(4000), 
    cMessageValue VARCHAR(4000)
);

CREATE TABLE AMS_Flag 
(
    CFLAGNAME VARCHAR(32) NOT NULL, 
    SFLAGVALUE SMALLINT NOT NULL, 
    PRIMARY KEY (CFLAGNAME)
);
