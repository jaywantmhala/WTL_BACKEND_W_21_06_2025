<?php
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Get the form data
    $name = $_POST['name'];
    $email = $_POST['email'];
    $phone = $_POST['phone'];
    $cabId = $_POST['cabId'];
    $modelName = $_POST['modelName'];
    $modelType = $_POST['modelType'];
    $seats = $_POST['seats'];
    $fuelType = $_POST['fuelType'];
    $availability = $_POST['availability'];
    $price = $_POST['price'];
    $pickupLocation = $_POST['pickupLocation'];
    $dropLocation = $_POST['dropLocation'];
    $date = $_POST['date'];
    $returndate = $_POST['returndate'];
    $time = $_POST['time'];
    $tripType = $_POST['tripType'];
    $distance = $_POST['distance'];
    $service = $_POST['service'];
    $gst = $_POST['gst'];
    $total = $_POST['total'];
    $days = $_POST['days'];
    $driverrate = $_POST['driverrate'];

    // Email settings
    $to = "vishalwtl@gmail.com"; // Replace with your email address
    $subject = "New Cab Booking Request";

    // Create the message body
    $message = "You have received a new cab booking request.\n\n";
    $message .= "Name: $name\n";
    $message .= "Email: $email\n";
    $message .= "Phone: $phone\n";
    $message .= "Cab ID: $cabId\n";
    $message .= "Model Name: $modelName\n";
    $message .= "Model Type: $modelType\n";
    $message .= "Seats: $seats\n";
    $message .= "Fuel Type: $fuelType\n";
    $message .= "Availability: $availability\n";
    $message .= "Price: $price\n";
    $message .= "Pickup Location: $pickupLocation\n";
    $message .= "Drop Location: $dropLocation\n";
    $message .= "Date: $date\n";
    $message .= "Return Date: $returndate\n";
    $message .= "Time: $time\n";
    $message .= "Trip Type: $tripType\n";
    $message .= "Distance: $distance\n";
    $message .= "Service: $service\n";
    $message .= "GST: $gst\n";
    $message .= "Total: $total\n";
    $message .= "Days: $days\n";
    $message .= "Driver Rate: $driverrate\n";

    // Headers
    $headers = "From: $email";

    // Send the email
    if (mail($to, $subject, $message, $headers)) {
        echo "Booking request sent successfully!";
    } else {
        echo "There was an error sending your booking request.";
    }
}
?>
