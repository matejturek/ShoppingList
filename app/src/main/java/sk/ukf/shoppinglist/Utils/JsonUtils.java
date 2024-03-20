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
    public static JSONObject createInviteJson(String listId, String email) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("listId", listId);
            jsonRequest.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    public static JSONObject deleteInvitation(int invitationId) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("invitationId", invitationId);
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

    // Method to create a JSON request for getting lists
    public static JSONObject getListsJson(String userId) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    // Method to create a JSON request for getting list details
    public static JSONObject getListDetailsJson(String listId) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("listId", listId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    // Method to create a JSON request for getting list details
    public static JSONObject editListJson(String listId, String name, String notes) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("listId", listId);
            jsonRequest.put("name", name);
            jsonRequest.put("notes", notes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    public static JSONObject deleteListJson(String listId) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("listId", listId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    // Method to create a JSON request for creating a list
    public static JSONObject createListJson(String userId, String name, String notes) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("userId", userId);
            jsonRequest.put("name", name);
            jsonRequest.put("notes", notes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    // Method to create a JSON request for editing user details
    public static JSONObject editUserJson(String userId, String name) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("userId", userId);
            jsonRequest.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    // Method to create a JSON request for getting lists
    public static JSONObject getUserDetailsJson(String userId) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    public static JSONObject createItem(String listId, String item, int categoryId) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("listId", listId);
            jsonRequest.put("name", item);
            if (categoryId >= 0) {
                jsonRequest.put("categoryId", categoryId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }


    public static JSONObject createCategory(String listId, String category, int parentCategoryId) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("listId", listId);
            jsonRequest.put("name", category);
            if (parentCategoryId >= 0) {
                jsonRequest.put("parentCategoryId", parentCategoryId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }


    public static JSONObject getItemsJson(String listId) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("listId", listId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    public static JSONObject createCategoryJson(String name, String parentCategoryId) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("name", name);
            jsonRequest.put(parentCategoryId, parentCategoryId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    public static JSONObject setCategoryJson(String categoryId, String name, String parentCategoryId) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("categoryId", categoryId);
            jsonRequest.put("name", name);
            jsonRequest.put("parentCategoryId", parentCategoryId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    public static JSONObject deleteCategoryJson(int categoryId) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("categoryId", categoryId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    public static JSONObject editCategoryJson(int categoryId, String name, int parentId ) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("categoryId", categoryId);
            jsonRequest.put("name", name);
            if (parentId >= 0) {
                jsonRequest.put("parentCategoryId", parentId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }


    public static JSONObject deleteItemJson(int itemId) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("itemId", itemId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    public static JSONObject setItemStatusJson(String itemId, Boolean status) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("itemId", itemId);
            jsonRequest.put("status", status ? "1" : "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }

    public static JSONObject setItem(int itemId, int quantity, String name, String shelf, String link) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("itemId", itemId);
            jsonRequest.put("quantity", quantity);
            jsonRequest.put("name", name);
            jsonRequest.put("shelf", shelf);
            jsonRequest.put("link", link);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }
}
