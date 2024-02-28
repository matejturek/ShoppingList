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
    if (isset($jsonData['userId']) && isset($jsonData['name'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Update the user's name
        $updateStmt = $mysqli->prepare("UPDATE users SET name = ? WHERE userId = ?");
        $updateStmt->bind_param("si", $jsonData['name'], $jsonData['userId']);
        $updateStmt->execute();

        // Check if the name update was successful
        if ($mysqli->affected_rows > 0) {
            $response = array('status' => 'success', 'message' => 'Name updated successfully.');
        } else {
            $response = array('status' => 'error', 'message' => 'Failed to update name.');
        }

        $updateStmt->close();

        // Close the connection
        $mysqli->close();

        // Respond with the JSON response
        echo json_encode($response);

    } else {
        // Respond with an error message
        $response = array('status' => 'error', 'message' => 'Invalid request. Please provide userId and name in the JSON data.');
        echo json_encode($response);
    }
} else {
    echo "No data received.";
}
?>