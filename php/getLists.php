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
    if (isset($jsonData['userId'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Retrieve lists associated with the user through the invitations table
        $query = "SELECT l.* FROM lists l
                  INNER JOIN invitations i ON l.listId = i.listId
                  WHERE i.ownerId = ? OR (i.userId = ? AND i.status = 1 AND l.listId = i.listId)";

        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("ss", $jsonData['userId'], $jsonData['userId']);
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
        echo "Invalid request. Please provide userId in the JSON data.";
    }
} else {
    echo "No data received.";
}
?>
