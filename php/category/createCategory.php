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
    if (isset($jsonData['name'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Insert new category into the categories table
        $query = "INSERT INTO categories (name) VALUES (?)";

        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("s", $jsonData['name']);
        $stmt->execute();

        // Get the ID of the newly created category
        $categoryId = $stmt->insert_id;

        $stmt->close();

        // Close the connection
        $mysqli->close();

        // Respond with success message and the ID of the created category
        echo json_encode(array("status" => "success", "message" => "Category created successfully", "categoryId" => $categoryId));

    } else {
        // Respond with an error message
        echo json_encode(array("status" => "error", "message" => "Invalid request. Please provide name in the JSON data."));
    }
} else {
    echo json_encode(array("status" => "error", "message" => "No data received."));
}
?>