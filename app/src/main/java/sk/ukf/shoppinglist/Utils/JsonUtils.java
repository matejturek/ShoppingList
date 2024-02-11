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

    // Method to create a JSON request for user login
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

    // Method to create a JSON request with a userId
    public static JSONObject createUserIdJson(String userId) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    // Method to create a JSON request for updating user name
    public static JSONObject createUpdateNameJson(String userId, String name) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("userId", userId);
            jsonRequest.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    // Method to create a JSON request for resetting user password
    public static JSONObject createResetPasswordJson(String email, String newPassword) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("email", email);
            jsonRequest.put("newPassword", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    // Method to create a JSON request for creating an invite
    public static JSONObject createInviteJson(String listId, String userId) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("listId", listId);
            jsonRequest.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    // Method to create a JSON request for changing invite status
    public static JSONObject createChangeInviteStatusJson(String listId, String userId, int status) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("listId", listId);
            jsonRequest.put("userId", userId);
            jsonRequest.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    // Method to create a JSON request for creating a list
    public static JSONObject createListJson(String userId) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    // Method to create a JSON request for editing user details
    public static JSONObject createEditUserJson(String userId, String name) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("userId", userId);
            jsonRequest.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }
}
