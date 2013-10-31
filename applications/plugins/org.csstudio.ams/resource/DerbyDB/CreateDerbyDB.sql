connect 'jdbc:derby://localhost/amsdb;create=true';
/*
for virtualbox vm
connect 'jdbc:derby://192.168.56.101/amsdb;create=true';
*/

drop table AMS_User;
create table AMS_User
(
	iUserId			INT NOT NULL,
	iGroupRef		INT default -1 NOT NULL,
	cUserName		VARCHAR(128),
	cEmail			VARCHAR(128),
	cMobilePhone	VARCHAR(64),
	cPhone			VARCHAR(64),
	cStatusCode		VARCHAR(32),
	cConfirmCode	VARCHAR(32),
	sActive			SMALLINT,
	sPreferredAlarmingTypeRR	SMALLINT,
	PRIMARY KEY (iUserId)
);

drop table AMS_UserGroup;
create table AMS_UserGroup
(
	iUserGroupId	INT NOT NULL,
	iGroupRef		INT default -1 NOT NULL,
	cUserGroupName	VARCHAR(128),
	sMinGroupMember	SMALLINT,
	iTimeOutSec		INT,
	sActive			SMALLINT default 1,
	PRIMARY KEY (iUserGroupId)
);

drop table AMS_UserGroup_User;
create table AMS_UserGroup_User
(
	iUserGroupRef	INT NOT NULL,
	iUserRef		INT NOT NULL,
	iPos			INT NOT NULL,
	sActive			SMALLINT,
	cActiveReason	VARCHAR(128),
	tTimeChange		BIGINT,
	PRIMARY KEY(iUserGroupRef,iUserRef,iPos)
);


drop table AMS_FilterConditionType;
create table AMS_FilterConditionType
(
	iFilterConditionTypeID	INT,
	cName			VARCHAR(128),
	cClass			VARCHAR(256),
	cClassUI		VARCHAR(256),
	PRIMARY KEY(iFilterConditionTypeID)
);

drop table AMS_FilterCondition;
create table AMS_FilterCondition
(
	iFilterConditionID	INT NOT NULL,
	iGroupRef		INT default -1 NOT NULL,
	cName			VARCHAR(128),
	cDesc			VARCHAR(256),
	iFilterConditionTypeRef INT,
	PRIMARY KEY(iFilterConditionID)
);

drop table AMS_FilterCondition_String;
create table AMS_FilterCondition_String
(
	iFilterConditionRef	INT NOT NULL,
	cKeyValue		VARCHAR(16),
	sOperator		SMALLINT,
	cCompValue		VARCHAR(128)
);

drop table AMS_FilterCond_PropCompare;
create table AMS_FilterCond_PropCompare
(
	iFilterConditionRef	INT NOT NULL,
	cMessageKeyValue	VARCHAR(16),
	sOperator			SMALLINT
);

drop table AMS_FilterCond_ArrStr;
create table AMS_FilterCond_ArrStr
(
	iFilterConditionRef	INT NOT NULL,
	cKeyValue		VARCHAR(16),
	sOperator		SMALLINT
);

drop table AMS_FilterCond_ArrStrVal;
create table AMS_FilterCond_ArrStrVal
(
	iFilterConditionRef	INT NOT NULL,
	cCompValue		VARCHAR(128)
);

drop table AMS_FilterCond_TimeBased;
create table AMS_FilterCond_TimeBased
(
	iFilterConditionRef	INT NOT NULL,
	cStartKeyValue		VARCHAR(16),
	sStartOperator		SMALLINT,
	cStartCompValue		VARCHAR(128),
	cConfirmKeyValue	VARCHAR(16),
	sConfirmOperator	SMALLINT,
	cConfirmCompValue	VARCHAR(128),
	sTimePeriod			SMALLINT,
	sTimeBehavior		SMALLINT
);

drop table AMS_FilterCond_TimeBasedItems;
create table AMS_FilterCond_TimeBasedItems
(
	iItemID			INT,
	iFilterConditionRef	INT NOT NULL,
	iFilterRef		INT NOT NULL,
	cIdentifier		VARCHAR(128) NOT NULL,
	sState			SMALLINT,
	tStartTime		BIGINT,
	tEndTime		BIGINT,
	sTimeOutAction	SMALLINT,
	iMessageRef		INT,
	PRIMARY KEY(iItemID)
);

drop table AMS_FilterCondition_PV;
create table AMS_FilterCondition_PV
(
	iFilterConditionRef	INT NOT NULL,
	cPvChannelName		VARCHAR(128),
	sSuggestedPvTypeId	SMALLINT,
	sOperatorId			SMALLINT,
	cCompValue			VARCHAR(128)
);

drop table AMS_FilterCond_Conj_Common;
create table AMS_FilterCond_Conj_Common
(
	iFilterConditionRef			INT NOT NULL,
	iFirstFilterConditionRef	INT NOT NULL,
	iSecondFilterConditionRef   INT NOT NULL
);

drop table AMS_Filter;
create table AMS_Filter
(
	iFilterID		INT,
	iGroupRef		INT default -1 NOT NULL,
	cName			VARCHAR(128),
	cDefaultMessage	VARCHAR(1024),
	cFilterType		VARCHAR(128) default 'default' NOT NULL,
	PRIMARY KEY (iFilterID)
);

drop table AMS_Filter_Timebased;
CREATE TABLE AMS_FILTER_TIMEBASED
(	
	IFILTERREF 					INT 			NOT NULL, 
	ITIMEOUT 					INT 			NOT NULL, 
	ISTARTFILTERCONDITIONREF 	INT DEFAULT -1 	NOT NULL, 
	ISTOPFILTERCONDITIONREF 	INT DEFAULT -1 	NOT NULL,
	ISENDONTIMEOUT				INT	DEFAULT 1	NOT NULL 
);

drop table AMS_Filter_WatchDog;
CREATE TABLE AMS_FILTER_WATCHDOG
(	
	IFILTERREF 					INT 			NOT NULL, 
	ITIMEOUT 					INT 			NOT NULL, 
	IFILTERCONDITIONREF 		INT DEFAULT -1 	NOT NULL 
);

drop table AMS_Filter_FilterCondition;
create table AMS_Filter_FilterCondition
(
	iFilterRef		INT,
	iFilterConditionRef	INT,
	iPos			INT,
	PRIMARY KEY (iFilterRef,iFilterConditionRef)
);

drop table AMS_Topic;
create table AMS_Topic
(
	iTopicId 		INT NOT NULL,
	iGroupRef		INT default -1 NOT NULL,
	cTopicName 		VARCHAR(128),
	cName	 		VARCHAR(128),
	cDescription	VARCHAR(256),
	PRIMARY KEY (iTopicId)						
);

drop table AMS_FilterActionType;
create table AMS_FilterActionType
(
	iFilterActionTypeID	INT NOT NULL,
	cName			VARCHAR(128),
	iTopicRef		INT,
	PRIMARY KEY(iFilterActionTypeID)
);

drop table AMS_FilterAction;
create table AMS_FilterAction
(
	iFilterActionID			INT NOT NULL,
	iFilterActionTypeRef	INT NOT NULL,
	iReceiverRef			INT,
	cMessage				VARCHAR(1024),
	PRIMARY KEY(iFilterActionID)
);

drop table AMS_Filter_FilterAction;
create table AMS_Filter_FilterAction
(
	iFilterRef			INT NOT NULL,
	iFilterActionRef	INT NOT NULL,
	iPos				INT NOT NULL
);

drop table AMS_Message;
create table AMS_Message
(
	iMessageID		INT NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	cProperty		VARCHAR(16),
	cValue			VARCHAR(256)
);

drop table AMS_MessageChain;
create table AMS_MessageChain
(
	iMessageChainID		INT NOT NULL,
	iMessageRef			INT NOT NULL,
	iFilterRef			INT NOT NULL,
	iFilterActionRef	INT NOT NULL,
	iReceiverPos		INT NOT NULL,
	tSendTime			BIGINT,
	tNextActTime		BIGINT,
	sChainState			SMALLINT,	
	cReceiverAdress		VARCHAR(64),	
	PRIMARY KEY(iMessageChainID)
);

drop table AMS_History;
create table AMS_History
(
	iHistoryID		INT NOT NULL GENERATED ALWAYS AS IDENTITY,
	tTimeNew		BIGINT,
	cType			VARCHAR(16),
	cMsgHost		VARCHAR(64),
	cMsgProc		VARCHAR(64),
	cMsgName		VARCHAR(64),
	cMsgEventTime	VARCHAR(32),
	cDescription	VARCHAR(512),
	cActionType		VARCHAR(16),	
	iGroupRef		INT,
	cGroupName		VARCHAR(64),
	iReceiverPos	INT,
	iUserRef 		INT,
	cUserName 		VARCHAR(128),
	cDestType		VARCHAR(16),	
	cDestAdress		VARCHAR(128),
	PRIMARY KEY(iHistoryID)
);

drop table AMS_MSG_EXTENSIONS;
create table AMS_MSG_EXTENSIONS
(
    CPVNAME			VARCHAR(4000) NOT NULL,
    CMESSAGEKEY   VARCHAR(4000) NOT NULL,
    CMESSAGEVALUE VARCHAR(4000) NOT NULL
);

drop table AMS_Flag;
create table AMS_Flag
(
	cFlagName		VARCHAR(32)	NOT NULL,
	sFlagValue		SMALLINT	NOT NULL,
	PRIMARY KEY(cFlagName)
);

insert into AMS_Flag (cFlagName, sFlagValue) values ('ReplicationState', 0);

disconnect;
