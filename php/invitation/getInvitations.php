<?php
require_once '../config.php';

if (isset ($_GET['listId'])) {
    $mysqli = new mysqli($servername, $username, $password, $dbname);

    if ($mysqli->connect_error) {
        die ("Connection failed: " . $mysqli->connect_error);
    }

    $query = "SELECT invitations.invitationId, lists.name ,invitations.userId, users.email, invitations.status 
              FROM invitations 
              INNER JOIN users ON invitations.userId = users.userId 
              INNEr JOIN lists ON invitations.listId = lists.listId
              WHERE invitations.listId = ?";

    $stmt = $mysqli->prepare($query);
    $stmt->bind_param("i", $_GET['listId']);
    $stmt->execute();
    $result = $stmt->get_result();

    $invitations = array();
    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $invitations[] = $row;
        }
    }
    echo json_encode($invitations);

    $stmt->close();
    $mysqli->close();
} else {
    echo json_encode('No listId provided.');
}
?>