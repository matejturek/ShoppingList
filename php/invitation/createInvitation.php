<?php
require_once '../config.php';

if ($_SERVER["REQUEST_METHOD"] === "POST") {

    $rawPostData = file_get_contents("php://input");

    $jsonData = json_decode($rawPostData, true);

    if (isset($jsonData['listId']) && isset($jsonData['email'])) {
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        $email = $jsonData['email'];
        $query = "SELECT userId FROM users WHERE email = ?";
        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            $userId = $row['userId'];

            $query = "INSERT INTO invitations (listId, userId, status) VALUES (?, ?, 0)";
            $stmt = $mysqli->prepare($query);
            $stmt->bind_param("ss", $jsonData['listId'], $userId);
            $stmt->execute();

            if ($stmt->affected_rows > 0) {
                $response = array('status' => 'success', 'message' => 'Invite created successfully.');
            } else {
                $response = array('status' => 'error', 'message' => 'Failed to create invite.');
            }
        } else {
            $response = array('status' => 'error', 'message' => 'User with the provided email does not exist.');
        }

        $stmt->close();
        $mysqli->close();
        echo json_encode($response);
    } else {
        $response = array('status' => 'error', 'message' => 'Invalid request. Please provide listId and email in the JSON data.');
        echo json_encode($response);
    }
} else {
    echo "No data received.";
}
?>