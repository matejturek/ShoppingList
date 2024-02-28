<?php
// Include the configuration file
require_once '../config.php';

// Check if data is sent via GET
if ($_SERVER["REQUEST_METHOD"] === "GET") {

    // Check if required query parameter is present
    if (isset($_GET['userId'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Retrieve lists associated with the user through the ownerId field
        $query = "SELECT listId, name FROM lists WHERE userId = ?";

        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("s", $_GET['userId']);
        $stmt->execute();

        $result = $stmt->get_result();

        // Fetch the lists
        $lists = array();
        while ($row = $result->fetch_assoc()) {
            $lists[] = $row;
        }

        $stmt->close();

        // Close the connection
        $mysqli->close();

        // Respond with the lists in JSON format
        echo json_encode($lists);

    } else {
        // Respond with an error message
        echo "Invalid request. Please provide userId as a query parameter.";
    }
} else {
    echo "No data received.";
}
?>