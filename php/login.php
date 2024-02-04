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

        // Retrieve hashed password from the database based on the provided email
        $stmt = $mysqli->prepare("SELECT password FROM users WHERE email = ?");
        $stmt->bind_param("s", $jsonData['email']);
        $stmt->execute();
        $stmt->bind_result($hashedPassword);
        $stmt->fetch();
        $stmt->close();

        // Verify the provided password against the stored hashed password
        if (password_verify($jsonData['password'], $hashedPassword)) {
            echo "Login successful!";
        } else {
            echo "Invalid email or password." . $hashedPassword . " " . $jsonData['password'];
        }

        // Close the connection
        $mysqli->close();
    } else {
        // Respond with an error message
        echo "Invalid request. Please provide email and password in the JSON data.";
    }
} else {
    echo "No data received.";
}
?>