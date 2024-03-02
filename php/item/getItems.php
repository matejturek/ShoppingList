<?php
// Include the configuration file
require_once '../config.php';

// Check if listId parameter is provided in the URL
if (isset($_GET['listId'])) {
    $listId = $_GET['listId'];

    // Assuming you have a MySQLi connection
    $mysqli = new mysqli($servername, $username, $password, $dbname);

    // Check for connection errors
    if ($mysqli->connect_error) {
        die("Connection failed: " . $mysqli->connect_error);
    }

    $query = "SELECT c.categoryId, COALESCE(c.name, '') AS categoryName, i.itemId, i.name AS itemName, i.quantity, i.status, i.link, i.shelf 
    FROM categories c
    RIGHT JOIN items i ON c.categoryId = i.categoryId
    WHERE i.listId = ? OR i.categoryId IS NULL
    ORDER BY c.categoryId IS NULL, c.categoryId, i.itemId";


    $stmt = $mysqli->prepare($query);
    $stmt->bind_param("s", $listId);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result) {
        $groupedItems = [];

        while ($row = $result->fetch_assoc()) {
            $categoryId = $row['categoryId'];
            $categoryName = $row['categoryName'];
            $itemName = $row['itemName'];
            $quantity = $row['quantity'];
            $status = $row['status'];
            $link = $row['link'];
            $shelf = $row['shelf'];

            if (!isset($groupedItems[$categoryId])) {
                $groupedItems[$categoryId] = ['categoryName' => $categoryName, 'items' => []];
            }

            $groupedItems[$categoryId]['items'][] = [
                'itemId' => $row['itemId'],
                'name' => $itemName,
                'quantity' => $quantity,
                'status' => $status,
                'link' => $link,
                'shelf' => $shelf,
            ];
        }

        // Close the connection
        $stmt->close();
        $mysqli->close();

        // Convert associative array to indexed array
        $groupedItems = array_values($groupedItems);

        // Respond with the grouped items
        echo json_encode($groupedItems);
    } else {
        // Handle query error
        echo json_encode(['status' => 'error', 'message' => 'Failed to fetch grouped items.']);
    }
} else {
    // Handle missing listId parameter
    echo json_encode(['status' => 'error', 'message' => 'Missing listId parameter.']);
}
?>