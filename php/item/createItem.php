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
    if (isset($jsonData['listId']) && isset($jsonData['name'])) {

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Define the SQL query
        $query = "INSERT INTO items (listId, name";

        // Define parameters
        $params = "ss"; // Assuming listId and name are strings
        $paramValues = array($jsonData['listId'], $jsonData['name']);

        // Check if categoryId is set
        if (isset($jsonData['categoryId'])) {
            $query .= ", categoryId";
            $params .= "s";
            $paramValues[] = $jsonData['categoryId'];
        }

        // Complete the query and parameter definition
        $query .= ", quantity, status) VALUES (" . rtrim(str_repeat("?,", count($paramValues)), ',') . ", 1, 0)";

        // Prepare the query
        $stmt = $mysqli->prepare($query);

        // Check if the query was prepared successfully
        if ($stmt === false) {
            die("Error preparing query: " . $mysqli->error);
        }

        // Bind parameters
        if (!$stmt->bind_param($params, ...$paramValues)) {
            die("Error binding parameters: " . $stmt->error);
        }

        // Execute the statement
        if (!$stmt->execute()) {
            die("Error executing query: " . $stmt->error . $query . json_encode($paramValues));
        }

        // Get the ID of the newly created item
        $itemId = $stmt->insert_id;

        // Close the statement
        $stmt->close();

        // Close the connection
        $mysqli->close();

        // Respond with success message and the ID of the created item
        echo json_encode(array("status" => "success", "message" => "Item created successfully", "itemId" => $itemId));
    } else {
        // Respond with an error message
        echo json_encode(array("status" => "error", "message" => "Invalid request. Please provide listId and name in the JSON data."));
    }
} else {
    // Respond with an error message
    echo json_encode(array("status" => "error", "message" => "No data received."));
}
?>