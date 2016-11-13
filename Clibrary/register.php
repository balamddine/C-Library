<?php 
require "conn.php";
$name = $_POST["name"];
$email = $_POST["email"];
$password = $_POST["password"];
$image = $_POST["image"];
$profession = $_POST["profession"];
$mysql_qry = "INSERT INTO 'users' ('Name','Email','Password','Image','Profession') VALUES ('$name', '$email', '$password', '$image', '$profession');";
if ($conn->query($mysql_qry) === TRUE) {
   $arr = array("user" => array(array("registerStatus" => "1")));
		echo json_encode($arr);
} else {
   $arr = array("user" => array(array("registerStatus" => "0")));
		echo json_encode($arr);
}
$conn->close();

?>