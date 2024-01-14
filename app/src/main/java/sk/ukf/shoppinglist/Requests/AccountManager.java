package sk.ukf.shoppinglist.Requests;

import org.json.JSONException;
import org.json.JSONObject;

import sk.ukf.shoppinglist.NetworkManager;

public class AccountManager {
    public static String login(String email, String password) {
        try {
            // Create JSON object for user registration
            JSONObject registrationData = new JSONObject();
            registrationData.put("email", email);
            registrationData.put("password", password);

            // Perform POST request using NetworkManager
            return NetworkManager.performPostRequest("login", registrationData);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static String register(String email, String password) {
        try {
            // Create JSON object for user registration
            JSONObject registrationData = new JSONObject();
            registrationData.put("email", email);
            registrationData.put("password", password);

            // Perform POST request using NetworkManager
            return NetworkManager.performPostRequest("register", registrationData);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}