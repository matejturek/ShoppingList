<?php
// Include the configuration file
require_once '../config.php';

// Check if data is sent via GET
if ($_SERVER["REQUEST_METHOD"] === "GET") {

    // Check if required query parameter is present
    if (isset($_GET['listId'])) {
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
        $stmt->bind_param("s", $_GET['listId']);
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
        echo "Invalid request. Please provide listId as a query parameter.";
    }
} else {
    echo "No data received.";
}
?>