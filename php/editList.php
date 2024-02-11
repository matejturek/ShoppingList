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
    if (isset($jsonData['listId']) && isset($jsonData['listName'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Update list name in the lists table
        $query = "UPDATE lists SET listName = ? WHERE listId = ?";

        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("ss", $jsonData['listName'], $jsonData['listId']);
        $stmt->execute();

        $stmt->close();

        // Close the connection
        $mysqli->close();

        // Respond with success message
        echo "List name updated successfully.";

    } else {
        // Respond with an error message
        echo "Invalid request. Please provide listId and listName in the JSON data.";
    }
} else {
    echo "No data received.";
}
?>
