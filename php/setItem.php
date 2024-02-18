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
    if (isset($jsonData['itemId']) && isset($jsonData['name']) && isset($jsonData['status']) && isset($jsonData['quantity']) && isset($jsonData['link']) && isset($jsonData['shelf'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Update values for the specified item in the items table
        $query = "UPDATE items SET name = ?, status = ?, quantity = ?, link = ?, shelf = ? WHERE itemId = ?";

        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("ssssss", $jsonData['name'], $jsonData['status'], $jsonData['quantity'], $jsonData['link'], $jsonData['shelf'], $jsonData['itemId']);
        $stmt->execute();

        $stmt->close();

        // Close the connection
        $mysqli->close();

        // Check if the update was successful
        if ($mysqli->affected_rows > 0) {
            $response = array('status' => 'success', 'message' => 'Item values updated successfully.');
        } else {
            $response = array('status' => 'error', 'message' => 'Failed to update item values.');
        }

        // Respond with the JSON response
        echo json_encode($response);

    } else {
        // Respond with an error message
        echo json_encode(array('status' => 'error', 'message' => 'Invalid request. Please provide itemId, name, status, quantity, link, and shelf in the JSON data.'));
    }
} else {
    echo json_encode(array('status' => 'error', 'message' => 'No data received.'));
}
?>