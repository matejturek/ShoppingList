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
    if (isset($jsonData['email']) && isset($jsonData['newPassword'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Retrieve user information based on the provided email
        $stmt = $mysqli->prepare("SELECT userId, password FROM users WHERE email = ?");
        $stmt->bind_param("s", $jsonData['email']);
        $stmt->execute();
        $stmt->bind_result($userId, $hashedPassword);
        $stmt->fetch();
        $stmt->close();

        // Check if the user with the provided email exists
        if ($userId) {
            // Update the user's password
            $newHashedPassword = password_hash($jsonData['newPassword'], PASSWORD_DEFAULT);
            $updateStmt = $mysqli->prepare("UPDATE users SET password = ? WHERE userId = ?");
            $updateStmt->bind_param("si", $newHashedPassword, $userId);
            $updateStmt->execute();
            $updateStmt->close();

            // Check if the password update was successful
            if ($mysqli->affected_rows > 0) {
                $response = array('status' => 'success', 'message' => 'Password reset successfully.');
            } else {
                $response = array('status' => 'error', 'message' => 'Failed to reset password.');
            }
        } else {
            // User with the provided email does not exist
            $response = array('status' => 'error', 'message' => 'User not found.');
        }

        // Close the connection
        $mysqli->close();

        // Respond with the JSON response
        echo json_encode($response);

    } else {
        // Respond with an error message
        $response = array('status' => 'error', 'message' => 'Invalid request. Please provide email and newPassword in the JSON data.');
        echo json_encode($response);
    }
} else {
    echo "No data received.";
}
?>