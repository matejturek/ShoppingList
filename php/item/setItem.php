<?php
require_once '../config.php';

if ($_SERVER["REQUEST_METHOD"] === "POST") {

    $rawPostData = file_get_contents("php://input");
    $jsonData = json_decode($rawPostData, true);
    if (!empty($jsonData['itemId']) && !empty($jsonData['name']) && !empty($jsonData['quantity']) && strlen($jsonData['itemId']) > 0 && strlen($jsonData['name']) > 0 && strlen($jsonData['quantity']) > 0) {
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        $query = "UPDATE items SET name = ?, quantity = ?";

        if (!empty($jsonData['shelf'])) {
            $query .= ", shelf = ?";
        }

        if (!empty($jsonData['link'])) {
            $query .= ", link = ?";
        }
        $query .= " WHERE itemId = ?";
        $stmt = $mysqli->prepare($query);

        $itemId = $jsonData['itemId'];
        $name = $jsonData['name'];
        $quantity = $jsonData['quantity'];

        if (!empty($jsonData['shelf']) && !empty($jsonData['link'])) {
            $shelf = $jsonData['shelf'];
            $link = $jsonData['link'];
            $stmt->bind_param("sssii", $name, $quantity, $shelf, $link, $itemId);
        } elseif (!empty($jsonData['shelf'])) {
            $shelf = $jsonData['shelf'];
            $stmt->bind_param("sssi", $name, $quantity, $shelf, $itemId);
        } elseif (!empty($jsonData['link'])) {
            $link = $jsonData['link'];
            $stmt->bind_param("sssi", $name, $quantity, $link, $itemId);
        } else {
            $stmt->bind_param("ssi", $name, $quantity, $itemId);
        }

        $stmt->execute();
        if ($stmt->error) {
            echo "SQL Error: " . $stmt->error . "<br>";
        }

        if ($stmt->affected_rows > 0) {
            $response = array('status' => 'success', 'message' => 'Item values updated successfully.');
        } else {
            $response = array('status' => 'error', 'message' => 'Failed to update item values.');
        }

        echo json_encode($response);
        $stmt->close();
        $mysqli->close();
    } else {
        echo json_encode(array('status' => 'error', 'message' => 'Invalid request. Please provide itemId, name, and quantity in the JSON data.'));
    }
} else {
    echo json_encode(array('status' => 'error', 'message' => 'No data received.'));
}
?>