<?php
// Include the configuration file
require_once 'config.php';

// Check if data is sent via POST
if ($_SERVER["REQUEST_METHOD"] === "POST") {

    // Read raw POST data
    $rawPostData = file_get_contents("php://input");

    // Decode JSON data
    $jsonData = json_decode($rawPostData, true);

    // Check if required fields are present
    if (isset($jsonData['listId']) && isset($jsonData['name']) && isset($jsonData['notes'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Update the existing record in the lists table
        $query = "UPDATE lists SET name = ?, notes = ? WHERE listId = ?";

        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("sss", $jsonData['name'], $jsonData['notes'], $jsonData['listId']);
        $stmt->execute();

        // Check if the update was successful
        if ($stmt->affected_rows > 0) {
            $response = array('status' => 'success', 'message' => 'List updated successfully.');
        } else {
            $response = array('status' => 'error', 'message' => 'Failed to update list.');
        }

        $stmt->close();

        // Close the connection
        $mysqli->close();

        // Respond with the JSON response
        echo json_encode($response);

    } else {
        // Respond with an error message
        echo json_encode(array('status' => 'error', 'message' => 'Invalid request. Please provide listId, name, and notes in the JSON data.'));
    }
} else {
    echo json_encode(array('status' => 'error', 'message' => 'No data received.'));
}
?>