package sk.ukf.shoppinglist.Utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NetworkManager {

    private static final String TAG = NetworkManager.class.getSimpleName();

    // Base URL of your API
    private static final String BASE_URL = "http://192.168.9.241/shopping_list/";

    // Executor for background tasks
    private static final Executor executor = Executors.newSingleThreadExecutor();

    // Handler for posting results to the main thread
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Method to perform a POST request with JSON data
    public static void performPostRequest(String endpoint, JSONObject requestData, ResultCallback callback) {
        executor.execute(() -> {
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

                    // Post the result to the main thread
                    mainHandler.post(() -> callback.onSuccess(response.toString()));
                } else {
                    Log.e(TAG, "HTTP error code: " + responseCode);
                    // Post the error to the main thread
                    mainHandler.post(() -> callback.onError("HTTP error code: " + responseCode));
                }

            } catch (IOException e) {
                Log.e(TAG, "Error during POST request", e);
                // Post the error to the main thread
                mainHandler.post(() -> callback.onError("Error during POST request"));
            }
        });
    }

    // Callback interface for handling the result on the main thread
    public interface ResultCallback {
        void onSuccess(String result);

        void onError(String error);
    }
}
