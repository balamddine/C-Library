<?php
require "conn.php";
require "helper.php";
$FriendID = $_POST["FriendID"];
$Name = $_POST["FriendName"];

if($_FILES["uploadedfile"]["name"]) {
	$target_path  = "./Library/files/";
	$target_path = $target_path . basename( $_FILES['uploadedfile']['name']);
	 if (move_uploaded_file($_FILES["uploadedfile"]["tmp_name"], $target_path)) {		 
		$mysql_qry ="select token from usersnotifications where UserID=$FriendID;";
		$result = mysqli_query($conn ,$mysql_qry);
		if(mysqli_num_rows($result) > 0) {
			while($row = mysqli_fetch_assoc($result)) {
				SendNotification($row["token"],$Name." Shared a file with you");
			}
		}
	} 
	else
	{
		echo "There was an error uploading the file, please try again!";
	}
}
else{
		echo "error from server, can't read file";
}
?>;

