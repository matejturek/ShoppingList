<?php
// Include the configuration file
require_once '../config.php';

// Check if userId is provided
if (isset($_GET['userId'])) {
    // Assuming you have a MySQLi connection
    $mysqli = new mysqli($servername, $username, $password, $dbname);

    // Check for connection errors
    if ($mysqli->connect_error) {
        die("Connection failed: " . $mysqli->connect_error);
    }

    // Prepare and execute the query to retrieve invitations with list names
    $query = "SELECT invitations.invitationId, invitations.listId, invitations.userId, invitations.status, lists.name AS listName 
              FROM invitations 
              INNER JOIN lists ON invitations.listId = lists.listId 
              WHERE invitations.userId = ? AND status = 0";

    $stmt = $mysqli->prepare($query);
    $stmt->bind_param("i", $_GET['userId']);
    $stmt->execute();
    $result = $stmt->get_result();

    // Check if there are any invitations
    if ($result->num_rows > 0) {
        // Array to store invitations
        $invitations = array();

        // Fetching and storing each invitation
        while ($row = $result->fetch_assoc()) {
            $invitations[] = $row;
        }

        // Respond with JSON data
        echo json_encode(array('status' => 'success', 'invitations' => $invitations));
    } else {
        // Respond with a message if there are no invitations
        echo json_encode(array('status' => 'error', 'message' => 'No invitations found for the provided userId.'));
    }

    // Close the statement and connection
    $stmt->close();
    $mysqli->close();
} else {
    // Respond with an error message if userId is not provided
    echo json_encode(array('status' => 'error', 'message' => 'No userId provided.'));
}
?>