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

        // Retrieve lists associated with the user through the ownerId field
        $query = "SELECT itemId, categoryId, name, quantity, status, link, shelf FROM items WHERE listId = ?";

        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("s", $_GET['listId']);
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