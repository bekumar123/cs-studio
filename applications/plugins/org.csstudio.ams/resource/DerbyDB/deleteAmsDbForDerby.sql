-- SQL-Skript zum Löschen der Einträge in der AMS-Datenbank (Derby)
-- Aktuelle Version vom 21.10.2013

CONNECT 'jdbc:derby://localhost/amsdb;create=true';

DELETE FROM AMS_FilterCond_FilterCond;
DELETE FROM AMS_FilterCond_Negation;
DELETE FROM AMS_Filter_Watchdog;
DELETE FROM AMS_Filter_Timebased;
DELETE FROM AMS_TbFilter_FilterCond;
DELETE FROM AMS_FilterCond_PropCompare;
DELETE FROM AMS_Filter_FilterAction;
DELETE FROM AMS_FilterAction;
DELETE FROM AMS_FilterActionType;
DELETE FROM AMS_Topic;
DELETE FROM AMS_Filter_FilterCondition;
DELETE FROM AMS_Filter;
DELETE FROM AMS_FilterCond_Conj_Common;
DELETE FROM AMS_FilterCondition_PV;
DELETE FROM AMS_FilterCond_TimeBased;
DELETE FROM AMS_FilterCond_ArrStrVal;
DELETE FROM AMS_FilterCond_ArrStr;
DELETE FROM AMS_FilterCondition_String;
DELETE FROM AMS_FilterConditionType;
DELETE FROM AMS_FilterCond_TimeBasedItems;
DELETE FROM AMS_UserGroup_User;
DELETE FROM AMS_UserGroup;
DELETE FROM AMS_User;
DELETE FROM AMS_FilterCond_Junction;
DELETE FROM AMS_FilterCondition;
DELETE FROM AMS_Message;
DELETE FROM AMS_MessageChain;

DISCONNECT;
