package sk.ukf.shoppinglist.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String PREF_NAME = "MyPreferences";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private SharedPreferencesManager() {
        // Private constructor to prevent instantiation
    }

    // Save userId
    public static void saveUserId(Context context, String userId) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    // Retrieve userId
    public static String getUserId(Context context) {
        return getSharedPreferences(context).getString(KEY_USER_ID, "");
    }

    // Save email
    public static void saveEmail(Context context, String email) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    // Retrieve email
    public static String getEmail(Context context) {
        return getSharedPreferences(context).getString(KEY_EMAIL, "");
    }

    // Save password
    public static void savePassword(Context context, String password) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    // Retrieve password
    public static String getPassword(Context context) {
        return getSharedPreferences(context).getString(KEY_PASSWORD, "");
    }

    // Clear all stored data
    public static void clearData(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}