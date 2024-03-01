<?php
// Include the configuration file
require_once '../config.php';

// Check if listId is provided in the URL
if (isset($_GET['listId'])) {

    // Assuming you have a MySQLi connection
    $mysqli = new mysqli($servername, $username, $password, $dbname);

    // Check for connection errors
    if ($mysqli->connect_error) {
        die("Connection failed: " . $mysqli->connect_error);
    }

    // Retrieve distinct categories associated with the specified listId
    $query = "SELECT DISTINCT categories.name 
              FROM items
              LEFT JOIN categories ON items.categoryId = categories.categoryId
              WHERE items.listId = ?";

    // Prepare the statement
    $stmt = $mysqli->prepare($query);

    // Check if the statement was prepared successfully
    if ($stmt === false) {
        die("Error in preparing statement: " . $mysqli->error);
    }

    // Bind parameters and execute the statement
    $stmt->bind_param("s", $_GET['listId']);
    $stmtExecuted = $stmt->execute();

    // Check if the execution was successful
    if ($stmtExecuted === false) {
        die("Error in executing statement: " . $stmt->error);
    }

    $result = $stmt->get_result();

    // Fetch the categories
    $categories = array();
    while ($row = $result->fetch_assoc()) {
        $categories[] = $row['name'];
    }

    $stmt->close();

    // Fetch items associated with the specified listId and group by category
    $query = "SELECT items.*, categories.name 
              FROM items
              LEFT JOIN categories ON items.categoryId = categories.categoryId
              WHERE items.listId = ?";

    // Prepare the statement
    $stmt = $mysqli->prepare($query);

    // Check if the statement was prepared successfully
    if ($stmt === false) {
        die("Error in preparing statement: " . $mysqli->error);
    }

    // Bind parameters and execute the statement
    $stmt->bind_param("s", $_GET['listId']);
    $stmtExecuted = $stmt->execute();

    // Check if the execution was successful
    if ($stmtExecuted === false) {
        die("Error in executing statement: " . $stmt->error);
    }

    $result = $stmt->get_result();

    // Organize items by categories
    $groupedItems = array();
    while ($row = $result->fetch_assoc()) {
        $category = $row['name'] ?? 'Other';
        $groupedItems[$category][] = $row;
    }

    $stmt->close();

    // Close the connection
    $mysqli->close();

    // Respond with the grouped items in JSON format
    echo json_encode($groupedItems);

} else {
    // Respond with an error message
    echo "Invalid request. Please provide listId as a query parameter.";
}
?>