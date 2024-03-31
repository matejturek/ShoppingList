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
    if (isset ($jsonData['listId']) && isset ($jsonData['name'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die ("Connection failed: " . $mysqli->connect_error);
        }

        // Define the SQL query
        $query = "INSERT INTO items (listId, categoryId, name, quantity, status) VALUES (?, ?, ?, 1, 0)";

        // Prepare the query
        $stmt = $mysqli->prepare($query);

        // Bind parameters
        if (isset ($jsonData['categoryId'])) {
            $stmt->bind_param("sss", $jsonData['listId'], $jsonData['categoryId'], $jsonData['name']);
        } else {
            $stmt->bind_param("ss", $jsonData['listId'], $jsonData['name']);
        }

        // Execute the statement
        $stmt->execute();

        // Get the ID of the newly created item
        $itemId = $stmt->insert_id;

        // Close the statement
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
    // Respond with an error message
    echo json_encode(array("status" => "error", "message" => "No data received."));
}
?>