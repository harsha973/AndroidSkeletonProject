package com.in.sha.skeletonproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

/**
 * Class that provides helper methods to work with {@link android.content.SharedPreferences SharedPreferences}.
 */
public class PreferencesUtils {

	private static final String PREFERENCES_NAME = "Skeleton_Preferences";

	public static final String KEY_USER_GUID = "USER_GUID";
	public static final String KEY_USER_NAME = "USER_NAME";
	public static final String KEY_USER_PASSWORD = "KEY_USER_PASSWORD";
	public static final String KEY_NEW_SITE_ADDRESS = "NEW_SITE_ADDRESS";
	public static final String KEY_USER_MODEL = "LOGIN_OBJECT";
	public static final String KEY_CONTRACTOR_ON_SITE = "CONTRACTOR_ON_SITE";

	private static final String KEY_PUSH_NOTIFICATION_STATUS = "push_notification";

	//	Beware while using static variables. Access using a getter method and reinitialise when
	//	necessary. May be cleared by Android on Low memory situtations.
	private static SharedPreferences sSharedPreferences;

	private static Gson sGson;

	static {
		sGson = (new GsonBuilder()).create();
	}


	public static void init(Context context) {
		sSharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
	}

	public static void saveString(Context context, final String key, final String value) {
		getSharedPreferences(context).edit().putString(key, value).apply();
	}

	public static String getString(Context context, final String key) {
		return getSharedPreferences(context).getString(key, null);
	}

	public static void saveBoolean(Context context, final String key, final boolean value) {
		getSharedPreferences(context).edit().putBoolean(key, value).apply();
	}

	public static boolean getBoolean(Context context, final String key) {
		return  getSharedPreferences(context).getBoolean(key, false);
	}

	public static void saveLong(Context context, final String key, final Long value) {
		getSharedPreferences(context).edit().putLong(key, value).apply();
	}

	public static Long getLong(Context context, final String key, long defVal) {
		return  getSharedPreferences(context).getLong(key, defVal);
	}

	public static void saveObject(Context context, final String key, final Object object) {
		String json = null;
		if (object != null) {
			json = getGson(context).toJson(object);
		}

		getSharedPreferences(context).edit().putString(key, json).apply();
	}

	public static void removeKey(Context context, final String key) {
		getSharedPreferences(context).edit().remove(key).apply();
	}

	public static <T> T getObject(Context context, final String key, final Class<T> tClass) {
		final String value =  getSharedPreferences(context).getString(key, null);
		return (value != null) ? getGson(context).fromJson(value, tClass) : null;
	}

	public static <T> T getObject(Context context, final String key, final Class<T> tClass, T defaultState) {
		T result = getObject(context, key, tClass);

		if (result == null) {
			return defaultState;
		}

		return result;
	}

	/**
	 * Get setting for push notification.
	 *
	 * @return True if push notification is turned on in the settings, false otherwise.
	 */
	public static boolean getPushNotificationsStatus(Context context) {
		// Push notifications are on by default (set to true)
		return getObject(context, KEY_PUSH_NOTIFICATION_STATUS, Boolean.class, true);
	}

	/**
	 * Save setting for push notification.
	 *
	 * @param state
	 * 		True if push notification must be turned on, false otherwise.
	 */
	public static void saveKeyPushNotificationsStatus(Context context, boolean state) {
		saveObject(context, KEY_PUSH_NOTIFICATION_STATUS, state);
	}

	/**
	 * Get timestamp from preferences (UTC).
	 *
	 * @param timestampName
	 * 		Preferences key for the time stamp.
	 *
	 * @return Timestamp from preferences or null if not available.
	 */
	public static Date getTimestamp(Context context, String timestampName) {
		//Try to get the timestamp from the prefs
		Date timestamp = null;
		try {
			timestamp = PreferencesUtils.getObject(context, timestampName, Date.class);
		} catch (Exception e) {
			//Something went wrong: we don't have the timestamp
		}

		return timestamp;
	}

	/**
	 * This should be only method to directly interact with variable sSharedPreferences.
	 * @param context	The context it is accessed from.
	 * @return	The shared preference object. Initialises if it doesnot exist.
	 */
	private static SharedPreferences getSharedPreferences(Context context)
	{
		if(sSharedPreferences == null)
		{
			init(context);
		}

		return sSharedPreferences;
	}

	/**
	 * This should be only method to directly interact with variable sGson.
	 * @param context	The context it is accessed from.
	 * @return	The Gson object. Initialises if it doesnot exist.
	 */
	private static Gson getGson(Context context)
	{
		if(sGson == null)
		{
			sGson = (new GsonBuilder()).create();
		}

		return sGson;
	}

	/**
	 * Clears the preferences
	 * @param context The context it is accessed from.
	 */
	public static void clear(Context context)
	{
		getSharedPreferences(context).edit().clear().commit();
	}

	/**
	 * Clears static variables in this class.
	 * This method should be called on low memory situations.
	 */
	public static void onTrimMemory()
	{
		sSharedPreferences = null;
		sGson = null;
	}
}
