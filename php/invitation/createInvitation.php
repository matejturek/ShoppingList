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
    if (isset($jsonData['listId']) && isset($jsonData['userId'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Insert record into the invites table
        $query = "INSERT INTO invites (listId, userId, status) VALUES (?, ?, 0)";

        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("ss", $jsonData['listId'], $jsonData['userId']);
        $stmt->execute();

        // Check if the insert was successful
        if ($stmt->affected_rows > 0) {
            $response = array('status' => 'success', 'message' => 'Invite created successfully.');
        } else {
            $response = array('status' => 'error', 'message' => 'Failed to create invite.');
        }

        $stmt->close();

        // Close the connection
        $mysqli->close();

        // Respond with the JSON response
        echo json_encode($response);

    } else {
        // Respond with an error message
        $response = array('status' => 'error', 'message' => 'Invalid request. Please provide listId and userId in the JSON data.');
        echo json_encode($response);
    }
} else {
    echo "No data received.";
}
?>