<?php
require_once '../config.php';

if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $rawPostData = file_get_contents("php://input");
    $jsonData = json_decode($rawPostData, true);

    if (isset($jsonData['itemId']) && isset($jsonData['status'])) {
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        $query = "UPDATE items SET status = ? WHERE itemId = ?";
        $stmt = $mysqli->prepare($query);

        $stmt->bind_param("ss", $jsonData['status'], $jsonData['itemId']);
        $stmt->execute();

        $response = ($stmt->affected_rows > 0) ? ['status' => 'success', 'message' => 'Item status updated successfully.'] : ['status' => 'error', 'message' => 'Failed to update item status.'];

        $stmt->close();
        $mysqli->close();

        echo json_encode($response);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Invalid request. Please provide itemId and status in the JSON data.']);
    }
} else {
    echo json_encode(['status' => 'error', 'message' => 'No data received.']);
}
?>