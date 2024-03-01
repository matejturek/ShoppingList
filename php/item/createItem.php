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
    if (isset($jsonData['listId']) && isset($jsonData['name'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Insert a new record into the items table
        $query = "INSERT INTO items (listId, name, quantity, status) VALUES (?, ?, 1, 0)";

        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("ss", $jsonData['listId'], $jsonData['name']);
        $stmt->execute();

        // Get the ID of the newly created item
        $itemId = $stmt->insert_id;

        $stmt->close();

        // Close the connection
        $mysqli->close();

        // Respond with success message and the ID of the created item
        echo json_encode(array("status" => "success", "message" => "Item created successfully", "itemId" => $itemId));

    } else {
        // Respond with an error message
        echo json_encode(array("status" => "error", "message" => "Invalid request. Please provide listId and name in the JSON data."));
    }
} else {
    echo json_encode(array("status" => "error", "message" => "No data received."));
}
?>