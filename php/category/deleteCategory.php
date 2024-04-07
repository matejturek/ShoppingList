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

        // Update items to set categoryId to NULL where categoryId matches a specific value
        $categoryId = $jsonData['categoryId'];
        $updateQuery = "UPDATE items SET categoryId = NULL WHERE categoryId = ?";
        $deleteQuery = "DELETE FROM categories WHERE categoryId = ?";

        // Prepare and execute the update query
        $stmtUpdate = $mysqli->prepare($updateQuery);
        $stmtUpdate->bind_param("s", $categoryId);
        $updateExecuted = $stmtUpdate->execute();

        // Check if the update was successful
        if ($updateExecuted) {
            // Prepare and execute the delete query only if update was successful
            $stmtDelete = $mysqli->prepare($deleteQuery);
            $stmtDelete->bind_param("s", $categoryId);
            $deleteExecuted = $stmtDelete->execute();

            if ($deleteExecuted) {
                $message = 'Category deleted successfully.';
            } else {
                $message = 'Failed to delete category.';
            }

            $stmtDelete->close();
        } else {
            $message = 'Failed to update items.';
        }

        $stmtUpdate->close();

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