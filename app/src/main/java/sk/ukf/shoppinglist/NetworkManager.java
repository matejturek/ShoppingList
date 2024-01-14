package sk.ukf.shoppinglist;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkManager {

    private static final String TAG = NetworkManager.class.getSimpleName();

    // Base URL of your API
    private static final String BASE_URL = "https://your-api-base-url.com/";

    // Method to perform a POST request with JSON data
    public static String performPostRequest(String endpoint, JSONObject requestData) {
        try {
            URL url = new URL(BASE_URL + endpoint);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Set request method to POST
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            // Write JSON data to the output stream
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(requestData.toString().getBytes());
            outputStream.flush();
            outputStream.close();

            // Get the response from the server
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return response.toString();
            } else {
                Log.e(TAG, "HTTP error code: " + responseCode);
            }

        } catch (IOException e) {
            Log.e(TAG, "Error during POST request", e);
        }
        return null;
    }

    // Method to perform a GET request
    public static String performGetRequest(String endpoint) {
        try {
            URL url = new URL(BASE_URL + endpoint);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Set request method to GET
            urlConnection.setRequestMethod("GET");

            // Get the response from the server
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return response.toString();
            } else {
                Log.e(TAG, "HTTP error code: " + responseCode);
            }

        } catch (IOException e) {
            Log.e(TAG, "Error during GET request", e);
        }
        return null;
    }
}
