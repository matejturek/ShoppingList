<?php
require_once '../config.php';

if ($_SERVER["REQUEST_METHOD"] === "GET") {

    if (isset ($_GET['userId'])) {
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        if ($mysqli->connect_error) {
            die ("Connection failed: " . $mysqli->connect_error);
        }

        $query = "SELECT DISTINCT lists.listId, lists.name 
                  FROM lists 
                  LEFT JOIN invitations ON lists.userId = invitations.userId 
                  WHERE lists.userId = ? OR (invitations.userId = ? AND invitations.status = 1)";

        $stmt = $mysqli->prepare($query);
        $stmt->bind_param("ss", $_GET['userId'], $_GET['userId']);
        $stmt->execute();

        $result = $stmt->get_result();

        $lists = array();
        while ($row = $result->fetch_assoc()) {
            $lists[] = $row;
        }

        $stmt->close();

        $mysqli->close();

        echo json_encode($lists);

    } else {
        echo "Invalid request. Please provide userId as a query parameter.";
    }
} else {
    echo "No data received.";
}
?>