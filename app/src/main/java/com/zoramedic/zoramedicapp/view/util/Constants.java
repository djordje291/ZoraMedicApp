package com.zoramedic.zoramedicapp.view.util;

import java.util.HashMap;

public class Constants {

    public static final String KEY_USER_ID = "userId";
    public static final String KEY_NAME = "name";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static  HashMap<String, String> remoteMsgHeaders = null;
    public static  HashMap<String, String> getRemoteMsgHeaders() {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAEm63G8k:APA91bFqlZtAtxHxvOaCV8JU8vtdDgz-CY4-1x1TiEkxaRLkaKsX6sal8p2LAEDd4jIYTa0UwSkj6Lrut4IwKNbWUbhaH6LNoNf2jPElkzTyLesnKg8Orm5ygs-Upzx2MEiYPRnuIcDC"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }


    public static final String SENDER_ID = "79166905289";
    public static final String KEY_TYPE = "type";
    public static final String KEY_MINIMAL_LIMIT = "minimalLimit";
    public static final String KEY_QUANTITY = "quantity";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CREATOR_NAME = "creatorName";

    public static final int MESSAGES_COUNT = 100;
    public static final int MORE_MESSAGES_COUNT = 50;

    public static final String KEY_BILLS_COLLECTION = "Bills";
    public static final String KEY_MESSAGES_COLLECTION = "Messages";
    public static final String KEY_PATIENTS_COLLECTION = "Patients";
    public static final String KEY_PHARMACY_COLLECTION = "Pharmacy";
    public static final String KEY_USERS_COLLECTION = "Users";
    public static final String KEY_NOTES_COLLECTION = "Notes";
    public static final String KEY_PRICE = "price";
    public static final String KEY_CREATOR_ID = "creatorID";
    public static final String KEY_DATE_AND_TIME = "dateAndTime";
    public static final String KEY_DOC_REF = "docRef";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_AGE = "age";
    public static final String KEY_BIRTHDAY = "birthday";
    public static final String KEY_BLOOD_TYPE = "bloodType";
    public static final String KEY_HEIGHT = "height";
    public static final String KEY_WEIGHT = "weight";
    public static final String KEY_IS_MALE = "isMale";
    public static final String KEY_JMGB = "jmbg";
    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_ROOM_NUMBER = "roomNumber";
    public static final String KEY_SERVICES = "services";
    public static final String KEY_SATURATION = "saturation";
    public static final String KEY_BLOOD_PRESSURE_HISTORY = "bloodPressureHistory";
    public static final String KEY_WORD_PATH = "wordPath";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_CLEARANCE_LVL = "clearanceLvl";
    public static final String KEY_USER_FIREBASE_ID = "userFirebaseID";
    public static final String KEY_AVAILABILITY = "availability";
    public static final String KEY_HAS_LOW = "hasLow";
    public static final String KEY_PER_OS = "perOs";
    public static final String KEY_USED = "used";
    public static final String KEY_PHARMACY = "pharmacy";
    public static final String KEY_NOTES = "notes";
    public static final String KEY_SEEN_BY_LIST = "seenByList";
    public static final String KEY_PATIENT = "patient";
    public static final String KEY_SERVICE = "service";
    public static final String KEY_ID = "id";
    public static final String KEY_RESULTS = "results";
    public static final String KEY_FAILURE = "failure";
    public static final String DATE_AND_TIME_FORMAT = "HH:mm   dd MMMM";
    public static final String DATE_FROMAT = "dd MMMM";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String MINI_DATE_FORMAT = "dd.MM.";
    public static final String KEY_DATE_RANGE_PICKER = "dateRangePicker";
}
