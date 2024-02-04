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
    if (isset($jsonData['email']) && isset($jsonData['name']) && isset($jsonData['password'])) {
        // Your processing logic here

        // Assuming you have a MySQLi connection
        $mysqli = new mysqli($servername, $username, $password, $dbname);

        // Check for connection errors
        if ($mysqli->connect_error) {
            die("Connection failed: " . $mysqli->connect_error);
        }

        // Check if the email already exists
        $checkStmt = $mysqli->prepare("SELECT email FROM users WHERE email = ?");
        $checkStmt->bind_param("s", $jsonData['email']);
        $checkStmt->execute();
        $checkStmt->store_result();

        if ($checkStmt->num_rows > 0) {
            // Email already exists, respond with an error
            echo "ERROR_EMAIL_EXISTS";
        } else {
            // Email doesn't exist, proceed with registration

            // Hash the password
            $hashedPassword = password_hash($jsonData['password'], PASSWORD_DEFAULT);

            // Prepare a simple INSERT statement (modify as needed)
            $insertStmt = $mysqli->prepare("INSERT INTO users (email, name, password) VALUES (?, ?, ?)");

            // Check for statement preparation errors
            if (!$insertStmt) {
                die("Prepare failed: " . $mysqli->error);
            }

            // Bind parameters to the statement
            $insertStmt->bind_param("sss", $jsonData['email'], $jsonData['name'], $hashedPassword);

            // Execute the statement
            if ($insertStmt->execute()) {
                echo "Registration successful!";
            } else {
                echo "Error: " . $insertStmt->error;
            }

            // Close the statement for insertion
            $insertStmt->close();
        }

        // Close the statement for checking email existence and the connection
        $checkStmt->close();
        $mysqli->close();
    } else {
        // Respond with an error message
        echo "Invalid request. Please provide email, name, and password in the JSON data.";
    }
} else {
    echo "No data received.";
}
?>