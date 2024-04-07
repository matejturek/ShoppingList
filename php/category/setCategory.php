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
    if (isset($jsonData['itemId'])) {
        // Your processing logic here

        // Establish a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Construct the base query
        $query = "UPDATE items SET";

        // Initialize array to store bind parameters
        $bindParams = "";
        $bindValues = array();

        // Check and add parameters to the query
        if (isset($jsonData['name'])) {
            $query .= " name = ?,";
            $bindParams .= "s";
            $bindValues[] = &$jsonData['name'];
        }
        if (isset($jsonData['categoryId'])) {
            $query .= " categoryId = ?,";
            $bindParams .= "i";
            $bindValues[] = &$jsonData['categoryId'];
        }
        if (isset($jsonData['quantity'])) {
            $query .= " quantity = ?,";
            $bindParams .= "i";
            $bindValues[] = &$jsonData['quantity'];
        }
        if (isset($jsonData['shelf'])) {
            $query .= " shelf = ?,";
            $bindParams .= "s";
            $bindValues[] = &$jsonData['shelf'];
        }
        if (isset($jsonData['link'])) {
            $query .= " link = ?,";
            $bindParams .= "s";
            $bindValues[] = &$jsonData['link'];
        }

        // Remove trailing comma
        $query = rtrim($query, ',');

        // Add the WHERE clause
        $query .= " WHERE itemId = ?";

        // Append itemId to bind parameters
        $bindParams .= "i";
        $bindValues[] = &$jsonData['itemId'];

        // Prepare the statement
        $stmt = $mysqli->prepare($query);

        // Dynamically bind parameters
        call_user_func_array(array($stmt, 'bind_param'), array_merge(array($bindParams), $bindValues));

        // Execute the query
        $stmt->execute();

        // Check if the update was successful
        if ($stmt->affected_rows > 0) {
            $response = array('status' => 'success', 'message' => 'Item values updated successfully.');
        } else {
            $response = array('status' => 'error', 'message' => 'Failed to update item values.');
        }

        // Respond with the JSON response
        echo json_encode($response);

        // Close the statement and the database connection
        $stmt->close();
        $mysqli->close();

    } else {
        // Respond with an error message
        echo json_encode(array('status' => 'error', 'message' => 'Invalid request. Please provide itemId in the JSON data.'));
    }
} else {
    // Respond with an error message
    echo json_encode(array('status' => 'error', 'message' => 'No data received.'));
}
?>