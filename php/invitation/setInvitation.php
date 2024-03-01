<?php
// Include the configuration file
require_once '../config.php';

// Check if data is sent via POST
if ($_SERVER["REQUEST_METHOD"] === "POST") {

    // Read raw POST data
    $rawPostData = file_get_contents("php://input");

    // Decode JSON data
    $jsonData = json_decode($rawPostData, true);

    // Check if required fields are present
    if (isset($jsonData['listId']) && isset($jsonData['userId']) && isset($jsonData['status'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Update the status of the invite record
        $query = "UPDATE invites SET status = ? WHERE listId = ? AND userId = ?";

        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("sss", $jsonData['status'], $jsonData['listId'], $jsonData['userId']);
        $stmt->execute();

        // Check if the update was successful
        if ($stmt->affected_rows > 0) {
            $response = array('status' => 'success', 'message' => 'Status updated successfully.');
        } else {
            $response = array('status' => 'error', 'message' => 'No matching record found for update.');
        }

        $stmt->close();

        // Close the connection
        $mysqli->close();

        // Respond with the JSON response
        echo json_encode($response);

    } else {
        // Respond with an error message
        $response = array('status' => 'error', 'message' => 'Invalid request. Please provide listId, userId, and status in the JSON data.');
        echo json_encode($response);
    }
} else {
    echo "No data received.";
}
?>