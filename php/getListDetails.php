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
    if (isset($jsonData['listId'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Retrieve the list associated with the specified listId
        $query = "SELECT * FROM lists WHERE listId = ?";

        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("s", $jsonData['listId']);
        $stmt->execute();

        $result = $stmt->get_result();

        // Fetch the row
        $row = $result->fetch_assoc();

        $stmt->close();

        // Close the connection
        $mysqli->close();

        // Respond with the row in JSON format
        echo json_encode($row);

    } else {
        // Respond with an error message
        echo "Invalid request. Please provide listId in the JSON data.";
    }
} else {
    echo "No data received.";
}
?>