-- NAMS --

create table AMS_FilterCond_Junction
(
   iFilterConditionRef INT NOT NULL,
   operator varchar(3) NOT NULL
);

create table AMS_FilterCond_FilterCond
(
   iFilterConditionId INT NOT NULL,
   iFilterConditionRef INT NOT NULL
);

create table AMS_FilterCond_Negation
(
   iFilterConditionRef INT NOT NULL,
   iNegatedFCRef INT NOT NULL
);

-- AMS --

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


create table AMS_FilterConditionType
(
	iFilterConditionTypeID	INT,
	cName			VARCHAR(128),
	cClass			VARCHAR(256),
	cClassUI		VARCHAR(256),
	PRIMARY KEY(iFilterConditionTypeID)
);

create table AMS_FilterCondition
(
	iFilterConditionID	INT NOT NULL,
	iGroupRef		INT default -1 NOT NULL,
	cName			VARCHAR(128),
	cDesc			VARCHAR(256),
	iFilterConditionTypeRef INT,
	PRIMARY KEY(iFilterConditionID)
);

create table AMS_FilterCondition_String
(
	iFilterConditionRef	INT NOT NULL,
	cKeyValue		VARCHAR(16),
	sOperator		SMALLINT,
	cCompValue		VARCHAR(128)
);

create table AMS_FilterCond_ArrStr
(
	iFilterConditionRef	INT NOT NULL,
	cKeyValue		VARCHAR(16),
	sOperator		SMALLINT
);

create table AMS_FilterCond_ArrStrVal
(
	iFilterConditionRef	INT NOT NULL,
	cCompValue		VARCHAR(128)
);

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

create table AMS_FilterCondition_PV
(
	iFilterConditionRef	INT NOT NULL,
	cPvChannelName		VARCHAR(128),
	sSuggestedPvTypeId	SMALLINT,
	sOperatorId			SMALLINT,
	cCompValue			VARCHAR(128)
);

create table AMS_FilterCond_Conj_Common
(
	iFilterConditionRef			INT NOT NULL,
	iFirstFilterConditionRef	INT NOT NULL,
	iSecondFilterConditionRef   INT NOT NULL,
	iOperand                    SMALLINT
);

create table AMS_Filter
(
	iFilterID		INT,
	iGroupRef		INT default -1 NOT NULL,
	cName			VARCHAR(128),
	cDefaultMessage		VARCHAR(1024),
	cFilterType		VARCHAR(200) default 'default' NOT NULL,	
	PRIMARY KEY (iFilterID)
);

create table AMS_Filter_FilterCondition
(
	iFilterRef		INT,
	iFilterConditionRef	INT,
	iPos			INT,
	PRIMARY KEY (iFilterRef,iFilterConditionRef)
);

create table AMS_Topic
(
	iTopicId 		INT NOT NULL,
	iGroupRef		INT default -1 NOT NULL,
	cTopicName 		VARCHAR(128),
	cName	 		VARCHAR(128),
	cDescription	VARCHAR(256),
	PRIMARY KEY (iTopicId)						
);

create table AMS_FilterActionType
(
	iFilterActionTypeID	INT NOT NULL,
	cName			VARCHAR(128),
	iTopicRef		INT,
	PRIMARY KEY(iFilterActionTypeID)
);

create table AMS_FilterAction
(
	iFilterActionID			INT NOT NULL,
	iFilterActionTypeRef	INT NOT NULL,
	iReceiverRef			INT,
	cMessage				VARCHAR(1024),
	PRIMARY KEY(iFilterActionID)
);

create table AMS_Filter_FilterAction
(
	iFilterRef			INT NOT NULL,
	iFilterActionRef	INT NOT NULL,
	iPos				INT NOT NULL
);

create table AMS_Message
(
	iMessageID		INTEGER GENERATED BY DEFAULT AS IDENTITY,
	cProperty		VARCHAR(16),
	cValue			VARCHAR(256)
);

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

create table AMS_History
(
	iHistoryID		Integer GENERATED BY DEFAULT AS IDENTITY,
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

create table AMS_Flag
(
	cFlagName		VARCHAR(32)	NOT NULL,
	sFlagValue		SMALLINT	NOT NULL,
	PRIMARY KEY(cFlagName)
);

insert into AMS_Flag (cFlagName, sFlagValue) values ('ReplicationState', 0);