package com.tech.flavor.haveasafej.util;

/**
 * Created by etbdefi on 12/31/2015.
 */
public interface HsjConstant {

    String APP_NAME = "HAVE A SAFE JOURNEY";
    int PLACE_PICKER_REQUEST = 1;
    int ALERT_ALARM_ID = 2;
    int PICK_CONTACT = 3;

    String TRIP_IMG_URI = "ImageUri";
    String TRIP_TITLE = "Title";
    String TRIP_PICKUP = "Pickup";
    String TRIP_DROP = "Drop";
    String TRIP_EST_TIME = "EstimatedTime";
    String TRIP_START_TIME = "StartTime";
    String TRIP_END_TIME = "EndTime";
    String TRIP_STATUS = "Status";

    long LOCATION_REFRESH_TIME = 0;
    float LOCATION_REFRESH_DISTANCE = 0;



    String ProfileName = "PName";
    String ProfileMail = "PMail";
    String ProfilePass = "PPassword";
    String ProfileEmMail = "PEmName";
    String ProfileMobile = "PMobile";
    String ProfileEmMobile = "PEmMobile";
    String ProfileAddress = "PAddress";

    String SettingEmAlertMail = "SEmAlertMail";
    String SettingEmAlertSms = "SEmAlertSms";
    String SettingEmAlertCall= "SEmAlertCall";
    String SettingGnAlertMail = "SGnAlertMail";
    String SettingGnAlertSms = "SGnAlertSms";
    String SettingGnSaveHistory = "SGnSaveHistory";
    String SettingSnoozeNo = "SSnoozeNo";
    String SettingSnoozeInterval = "SSnoozeInterval";
    String SettingUpdateInterval = "SoUpdateInterval";

    String TABLE_COMMENTS = "alertmetripdb";
    String COLUMN_ID = "_id";
    String COLUMN_Title = "Title";
    String COLUMN_Drop = "DropLoc";
    String COLUMN_Pickup = "PickupLoc";
    String COLUMN_ImgUri = "ImgUri";
    String COLUMN_Duration = "Duration";
    String COLUMN_Status = "Status";

    String DATABASE_NAME = "alertmetripdb.db";
    int DATABASE_VERSION = 1;


}
