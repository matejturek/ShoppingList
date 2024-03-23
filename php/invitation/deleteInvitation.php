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
    if (isset($jsonData['invitationId'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Delete invitation from the invitations table
        $query = "DELETE FROM invitations WHERE invitationId = ?";

        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("i", $jsonData['invitationId']);

        // Execute the deletion query
        if ($stmt->execute()) {
            $response = array('status' => 'success', 'message' => 'Invitation deleted successfully.');
        } else {
            $response = array('status' => 'error', 'message' => 'Failed to delete invitation.');
        }

        $stmt->close();

        // Close the connection
        $mysqli->close();

        // Respond with the JSON response
        echo json_encode($response);

    } else {
        // Respond with an error message
        echo json_encode(array('status' => 'error', 'message' => 'Invalid request. Please provide invitationId in the JSON data.'));
    }
} else {
    echo json_encode(array('status' => 'error', 'message' => 'No data received.'));
}
?>