<?php
require_once '../config.php';

if ($_SERVER["REQUEST_METHOD"] === "POST") {

    $rawPostData = file_get_contents("php://input");

    $jsonData = json_decode($rawPostData, true);

    if (isset ($jsonData['invitationId'])) {
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        if ($mysqli->connect_error) {
            die ("Connection failed: " . $mysqli->connect_error);
        }

        $query = "UPDATE invitations SET status = 1 WHERE invitationId = ?";

        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("i", $jsonData['invitationId']);

        if ($stmt->execute()) {
            $response = array('status' => 'success', 'message' => 'Invitation accepted successfully.');
        } else {
            $response = array('status' => 'error', 'message' => 'Failed to accept invitation.');
        }

        $stmt->close();

        $mysqli->close();

        echo json_encode($response);

    } else {
        echo json_encode(array('status' => 'error', 'message' => 'Invalid request. Please provide invitationId in the JSON data.'));
    }
} else {
    echo json_encode(array('status' => 'error', 'message' => 'No data received.'));
}
?>