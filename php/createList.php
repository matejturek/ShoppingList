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
    if (isset($jsonData['userId']) && isset($jsonData['listName'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Insert new record into the lists table
        $query = "INSERT INTO lists (userId, listName) VALUES (?, ?)";

        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("ss", $jsonData['userId'], $jsonData['listName']);
        $stmt->execute();

        $stmt->close();

        // Close the connection
        $mysqli->close();

        // Respond with success message
        echo "List created successfully.";

    } else {
        // Respond with an error message
        echo "Invalid request. Please provide userId and listName in the JSON data.";
    }
} else {
    echo "No data received.";
}
?>
