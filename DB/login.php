<?php 
require "conn.php";
$email = $_POST["email"];
$password = $_POST["password"];
$mysql_qry = "select * from users where email like '$email' and password like '$password';";
$result = mysqli_query($conn ,$mysql_qry);
if(mysqli_num_rows($result) > 0) {
	while($row = mysqli_fetch_assoc($result)) {
		$arr = array("user" => array(array("ID"=>$row["ID"],"Name"=>$row["Name"],"Email"=>$row["Email"],"Password"=>$row["Password"],"Image"=>$row["Image"],"Profession"=>$row["Profession"],"status" => "1")));
		echo json_encode($arr);
	}
}
else {
		$arr = array("user" => array(array("status" => "0")));
		echo json_encode($arr);
}
$conn->close();
?>