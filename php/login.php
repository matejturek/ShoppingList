<?php
// Include the configuration file
require_once 'config.php';

// Check if data is sent via POST
if ($_SERVER["REQUEST_METHOD"] === "POST") {

    // Read raw POST data
    $rawPostData = file_get_contents("php://input");

    // Decode JSON data
    $jsonData = json_decode($rawPostData, true);

    // Check if required fields are present
    if (isset($jsonData['email']) && isset($jsonData['password'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Retrieve id and hashed password from the database based on the provided email
        $stmt = $mysqli->prepare("SELECT userId, password FROM users WHERE email = ?");
        $stmt->bind_param("s", $jsonData['email']);
        $stmt->execute();
        $stmt->bind_result($userId, $hashedPassword);
        $stmt->fetch();
        $stmt->close();

        // Verify the provided password against the stored hashed password
        if (password_verify($jsonData['password'], $hashedPassword)) {
            // Return a JSON response with the user's ID
            echo json_encode(array("status" => "success", "message" => "Login successful", "userId" => $userId));
        } else {
            // Return a JSON response for invalid email or password
            echo json_encode(array("status" => "error", "message" => "Invalid email or password"));
        }

        // Close the connection
        $mysqli->close();
    } else {
        // Return a JSON response for invalid request
        echo json_encode(array("status" => "error", "message" => "Invalid request. Please provide email and password in the JSON data."));
    }
} else {
    // Return a JSON response for no data received
    echo json_encode(array("status" => "error", "message" => "No data received."));
}
?>