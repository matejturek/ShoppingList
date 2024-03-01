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
    if (isset($jsonData['categoryId'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Delete category from the categories table
        $query = "DELETE FROM categories WHERE categoryId = ?";

        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("s", $jsonData['categoryId']);
        $stmt->execute();

        $stmt->close();

        // Close the connection
        $mysqli->close();

        // Check if the deletion was successful
        if ($mysqli->affected_rows > 0) {
            $response = array('status' => 'success', 'message' => 'Category deleted successfully.');
        } else {
            $response = array('status' => 'error', 'message' => 'Failed to delete category.');
        }

        // Respond with the JSON response
        echo json_encode($response);

    } else {
        // Respond with an error message
        echo json_encode(array('status' => 'error', 'message' => 'Invalid request. Please provide categoryId in the JSON data.'));
    }
} else {
    echo json_encode(array('status' => 'error', 'message' => 'No data received.'));
}
?>