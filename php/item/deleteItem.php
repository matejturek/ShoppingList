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
    if (isset($jsonData['itemId'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Define the SQL query
        $query = "DELETE FROM items WHERE itemId = ?";

        // Prepare the query
        $stmt = $mysqli->prepare($query);

        // Bind parameters
        $stmt->bind_param("i", $jsonData['itemId']);

        // Execute the statement
        $stmt->execute();

        // Close the statement
        $stmt->close();

        // Close the connection
        $mysqli->close();

        // Respond with success message
        echo json_encode(array("status" => "success", "message" => "Item deleted successfully"));

    } else {
        // Respond with an error message
        echo json_encode(array("status" => "error", "message" => "Invalid request. Please provide itemId in the JSON data."));
    }
} else {
    // Respond with an error message
    echo json_encode(array("status" => "error", "message" => "No data received."));
}
?>