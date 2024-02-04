package sk.ukf.shoppinglist.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

    // Method to create a JSON request for user registration
    public static JSONObject createRegistrationJson(String email, String name, String password) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("email", email);
            jsonRequest.put("name", name);
            jsonRequest.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    public static JSONObject createLoginJson(String email, String password) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("email", email);
            jsonRequest.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }
}
