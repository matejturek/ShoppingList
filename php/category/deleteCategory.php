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

        // Delete items where categoryId matches the provided value
        $categoryId = $jsonData['categoryId'];
        $deleteQuery = "DELETE FROM items WHERE categoryId = ?";

        // Prepare and execute the delete query
        $stmtDelete = $mysqli->prepare($deleteQuery);
        $stmtDelete->bind_param("s", $categoryId);
        $deleteExecuted = $stmtDelete->execute();

        // Check if the deletion was successful
        if ($deleteExecuted) {
            $message = 'Items deleted successfully.';
        } else {
            $message = 'Failed to delete items.';
        }

        $stmtDelete->close();

        // Close the connection
        $mysqli->close();

        // Construct the response
        $response = array(
            'status' => $deleteExecuted ? 'success' : 'error',
            'message' => $message
        );

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