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
    if (isset($jsonData['userId']) && isset($jsonData['name']) && isset($jsonData['notes'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Insert new record into the lists table
        $query = "INSERT INTO lists (userId, name, notes) VALUES (?, ?, ?)";

        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("sss", $jsonData['userId'], $jsonData['name'], $jsonData['notes']);
        $stmt->execute();

        // Get the ID of the newly created list
        $listId = $stmt->insert_id;

        $stmt->close();

        // Close the connection
        $mysqli->close();

        // Respond with success message and the ID of the created list
        echo json_encode(array("status" => "success", "message" => "List created successfully", "listId" => $listId));

    } else {
        // Respond with an error message
        echo json_encode(array("status" => "error", "message" => "Invalid request. Please provide userId, name, and notes in the JSON data."));
    }
} else {
    echo json_encode(array("status" => "error", "message" => "No data received."));
}
?>