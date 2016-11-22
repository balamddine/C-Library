<?php 

if (!isset($_POST["call"])) { echo "no call detected"; return; }
$fct = $_POST["call"];
switch ($fct) {
	case "GetUser":
		GetUser($_POST["name"],$_POST["email"],$_POST["password"],$_POST["image"],$_POST["profession"],$_POST["notificationtocken"],$_POST["FBuserID"]); 
		break;
    case "login":
		login($_POST["email"],$_POST["password"],$_POST["notificationtocken"]); 
		break;
	case "register":
		register($_POST["name"],$_POST["email"],$_POST["password"],"","",$_POST["notificationtocken"],"","register"); 
		break;
	case "GetUserFriend":
		GetUserFriend($_POST["ID"]); 
		break;
	case "GetAllUserExceptID":
		GetAllUserExceptID($_POST["ID"]); 
	break;
	case "AddAsAFriend":
		AddAsAFriend($_POST["ID"],$_POST["FriendID"],$_POST["Name"]); 
	break;
	case "CancelFriendRequest":
		CancelFriendRequest($_POST["ID"],$_POST["FriendID"],$_POST["Name"]); 
	break;
	case "updateUserImage":
		updateUserImage($_POST["Image"],$_POST["ID"]);
	break;
	case "Editprofile":
		Editprofile($_POST["name"],$_POST["email"],$_POST["profession"]);	
	break;
	case "GetFriendsRequest":
		GetFriendsRequest($_POST["ID"]);
	break;
	case "AcceptFriendRequest":
		AcceptFriendRequest($_POST["ID"],$_POST["FriendID"],$_POST["Name"]); 
	break;
	case "DeclineFriendRequest":
		DeclineFriendRequest($_POST["ID"],$_POST["FriendID"]); 
	break;
	case "DeleteFriend":
		DeleteFriend($_POST["ID"],$_POST["FriendID"]); 
	break;
	case "GetUserCategories" :
		GetUserCategories($_POST["UserID"]);
	break;
	case "AddNewCategory":
		AddNewCategory($_POST["UserID"],$_POST["Name"]);
	break;
	case "RemoveCategory":
		RemoveCategory($_POST["CatID"],$_POST["UserID"]);
	break;
	case "GetUserByEmail":
		GetUserByEmail($_POST["FriendEmail"]);
	break;
	case "GetUserFiles":
		GetUserFiles($_POST["UserID"],$_POST["CatID"]);
	break;
	case "ShareFile":
		ShareFile($_POST["UserID"],$_POST["FriendID"],$_POST["Name"],$_POST["GroupID"]);
	break;
	case "SaveFileToCategory" :
		SaveFileToCategory($_POST["ID"],$_POST["CategoryID"]);
	break;
	case "GetUserRepository":
		GetUserRepository($_POST["UserID"]);
	break;
	case "UpdateFile":
		UpdateFile($_POST["ID"]);
	break;	
	case  "GetUserGroups":
		GetUserGroups($_POST["UserID"]);
	break;
	case  "AddNewGroup":
		AddNewGroup($_POST["AdminID"],$_POST["AdminName"],$_POST["usersIDs"],$_POST["Name"]);
	break;
	case "GetGroupUsersListWithoutCurrentUser":
		GetGroupUsersListWithoutCurrentUser($_POST["GroupID"]);
	break;
}
function GetGroupUsersListWithoutCurrentUser($GroupID)
{
	require "conn.php";
		$mysql_qry1 = "select u.* from  groups g JOIN users u ON (find_in_set(u.id,g.usersIDs) > 0) where g.id='".$GroupID."'";
		$result = mysqli_query($conn ,$mysql_qry1);
			if(mysqli_num_rows($result) > 0) {
				while($row = mysqli_fetch_assoc($result)) {
					$arr['user'][] = array( 
					"ID" => $row["ID"],
					"Name" => $row["Name"],
					"Email" => $row["Email"],
					"Password" => $row["Password"],
					"Image" => $row["Image"],
					"Profession" => $row["Profession"],
					"Accepted" => "0",					
					"status" => "1",
					"call" => "GetGroupUsersListWithoutCurrentUser"
					);					
				}
				echo json_encode($arr);
			}	
			else{
				$arr = array("user" => array(array("status" => "0","message"=>"no user found","call" => "GetGroupUsersListWithoutCurrentUser")));
				echo json_encode($arr);		
			}		
}
function AddNewGroup($AdminID,$AdminName,$usersIDs,$Name)
{
	require "conn.php";
	$mysql_qry = "INSERT INTO groups (Name,AdminID,usersIDs) VALUES ('".$Name."', '".$AdminID."', '".$usersIDs."');";
	if ($conn->query($mysql_qry) === TRUE) {
		$mysql_qry1 = "SELECT * FROM usersnotifications where userID in (".$usersIDs.")" ;
		$result = mysqli_query($conn ,$mysql_qry1);
			if(mysqli_num_rows($result) > 0) {
				while($row = mysqli_fetch_assoc($result)) {
					SendNotification($row["token"],$AdminName." Added you to a group");
				}
				
			}
				$arr = array("groups" => array(array("status" => "1","message"=>"Group added successfully","call" => "AddNewGroup")));
			echo json_encode($arr);
	}
	else{
		$arr = array("groups" => array(array("status" => "0","message"=>"Could not add new group","call" => "AddNewGroup")));
			echo json_encode($arr);
	}
	
}
function GetUserGroups($userID)
{
	require "conn.php";
	
	$mysql_qry = "SELECT u.Name as AdminName,g.* FROM groups g inner join users u on g.AdminID=u.ID WHERE AdminID = '".$userID."' or g.usersIDs like '%".$userID."%'  order by g.Name " ;
	$result = mysqli_query($conn ,$mysql_qry);
	$arr = array('groups' => array());
	if(mysqli_num_rows($result) > 0) {
		while($row = mysqli_fetch_assoc($result)) {
			$arr['groups'][] = array( 
				"ID" => $row["ID"],
				"Name" => $row["Name"],
				"AdminID" => $row["AdminID"],
				"AdminName" => $row["AdminName"],
				"usersIDs" => $row["usersIDs"],				
				"call" => "GetUserGroups"
        );
		}
		echo json_encode($arr);
	}
	else {
			$arr = array("groups" => array(array("status" => "0","message"=>"No groups available","call" => "GetUserGroups")));
			echo json_encode($arr);
	}
	$conn->close();
}

function GetUserRepository($userID)
{
	require "conn.php";
	
	$mysql_qry = "SELECT uf.*,u.Name as SharedWithName FROM `user_files` uf inner join users u on u.ID = uf.UserID WHERE IsDownloaded=0 
	and (CASE uf.FriendID WHEN '-1' then uf.GroupID in (SELECT ID from groups where usersIDs like '%".$userID."%' or AdminID=".$userID." ) else uf.FriendID=".$userID." End ) " ;
	$result = mysqli_query($conn ,$mysql_qry);
	$arr = array('files' => array());
	if(mysqli_num_rows($result) > 0) {
		while($row = mysqli_fetch_assoc($result)) {
			$arr['files'][] = array( 
				"ID" => $row["ID"],
				"UserID" => $row["UserID"],
				"Name" => $row["Name"],
				"FriendID" => $row["FriendID"],
				"SharedWithName" => $row["SharedWithName"],
				"IsDownloaded"=> $row["IsDownloaded"],
				"call" => "GetUserRepository"
        );
		}
		echo json_encode($arr);
	}
	else {
			$arr = array("files" => array(array("status" => "0","message"=>"No files available","call" => "GetUserRepository")));
			echo json_encode($arr);
	}
	$conn->close();
}

function UpdateFile($ID)
{
	require "conn.php";
		$mysql_qry ="update user_files set IsDownloaded='1' where ID='$ID' ";
		if ($conn->query($mysql_qry) === TRUE) {
			  $arr = array("categories" => array(array("status" => "1","message" => "Record Updated","call" => "UpdateFile")));
		}else{
			$arr = array("categories" => array(array("status" => "0","message" => "Error in Updating record","call" => "UpdateFile")));
		}			
		echo json_encode($arr);
	$conn->close();
}
function SaveFileToCategory($ID,$CategoryID)
{
	require "conn.php";
		$mysql_qry ="update user_files set CategoryID='$CategoryID' where ID='$ID' ";
		if ($conn->query($mysql_qry) === TRUE) {
			  $arr = array("categories" => array(array("status" => "1","message" => "Saved successfully","call" => "SaveFileToCategory")));
		}else{
			$arr = array("categories" => array(array("status" => "0","message" => "Error in saving to category","call" => "SaveFileToCategory")));
		}			
		echo json_encode($arr);
	$conn->close();
}
function ShareFile($UserID,$FriendID,$Name,$GroupID){
	require "conn.php";
	if($_FILES["uploadedfile"]["name"]) {
		$target_path  = "./Library/files/";
		$Filename = basename( $_FILES['uploadedfile']['name']);
		$target_path = $target_path . $Filename;
		if (move_uploaded_file($_FILES["uploadedfile"]["tmp_name"], $target_path)) {		 
			$mysql_qry ="";
			if($FriendID!="-1"){
				$mysql_qry ="select token from usersnotifications where UserID=$FriendID;";	
			}
			else{
				$mysql_qry ="select token from usersnotifications where UserID in (select usersIDs from groups where ID=$GroupID)";	
			}			
			$last_id2 =0;
			$result = mysqli_query($conn ,$mysql_qry);
			if(mysqli_num_rows($result) > 0) {
				$mysql_qry1 = "INSERT INTO user_files (CategoryID,UserID,FriendID,GroupID,Name,IsDownloaded) 
				VALUES ('-1','".$UserID."','".$FriendID."','".$GroupID."','".$Filename."',0);";					 
				if ($conn->query($mysql_qry1) === TRUE) {
					$last_id2 = $conn->insert_id;
					while($row = mysqli_fetch_assoc($result)) {
						SendNotification($row["token"],$Name." Shared a file with you");
					}	
				   echo $last_id2;	
				}
				else{
					echo mysqli_error($conn)."\n";
					//echo "error";
				}														
			}
			else{
				echo "error occured";
			}			
		} 
		else{
			echo "There was an error uploading the file, please try again!";
		}
	}
	else{
		echo "error from server, can't read file";
	}
	
}

function GetUserFiles($userID,$CategoryID)
{
	require "conn.php";
	
	$mysql_qry = "SELECT user_files.*, (CASE user_files.FriendID WHEN '-1' then ( select Name from groups where ID=user_files.GroupID ) 
				  ELSE(select Name from users where ID=user_files.FriendID) END) as SharedWithName FROM user_files where UserID='".$userID."' and CategoryID='".$CategoryID."' 
				  Order by ID DESC " ;
	$result = mysqli_query($conn ,$mysql_qry);
	$arr = array('files' => array());
	if(mysqli_num_rows($result) > 0) {
		while($row = mysqli_fetch_assoc($result)) {
			$arr['files'][] = array( 
				"ID" => $row["ID"],
				"Name" => $row["Name"],
				"SharedWithName"=> $row["SharedWithName"],
				"call" => "GetUserFiles"
        );
		}
		echo json_encode($arr);
	}
	else {
			$arr = array("files" => array(array("status" => "0","message"=>"No files available","call" => "GetUserFiles")));
			echo json_encode($arr);
	}
	$conn->close();
}
function GetUserByEmail($FriendEmail)
{			
	require "conn.php";
	$mysql_qry = "select * from users where users.Email='".$FriendEmail."'; ";
	$result = mysqli_query($conn ,$mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row = mysqli_fetch_assoc($result)) {
			$arr['user'][] = array( 
				"ID" => $row["ID"],
				"Name" => $row["Name"],
				"Email" => $row["Email"],
				"Password" => $row["Password"],
				"Image" => $row["Image"],
				"Profession" => $row["Profession"],				
				"status" => "1",
				"call" => "GetUserByEmail"
        );
		}
		echo json_encode($arr);
	}
	else{
		$arr = array("user" => array(array("status" => "0","message"=>"no user found","call" => "GetUserByEmail")));
		echo json_encode($arr);		
	}
	
}
function RemoveCategory($catID,$UserID)
{
	require "conn.php";
	$mysql_qry = "DELETE FROM user_categories WHERE CategoryID='$catID' and UserID='$UserID'";
	if ($conn->query($mysql_qry) === TRUE) {
		$arr = array("categories" => array(array("status" => "1","message"=>"Removed successfully","call" => "RemoveCategory")));
		echo json_encode($arr);		
	}
	else{
		$arr = array("categories" => array(array("status" => "0","message"=>"Unable to remove category","call" => "RemoveCategory")));
		echo json_encode($arr);		
	}
	
}
function AddNewCategory($UserID,$Name)
{
	require "conn.php";
	$mysql_qry = "select * from categories where Name = '".$Name."';";
	$result = mysqli_query($conn ,$mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		$arr = array("categories" => array(array("status" => "0","message"=>"Category allready exists","call" => "AddNewCategory")));
		echo json_encode($arr);
	}
	else{
		$mysql_qry1 = "INSERT INTO categories (Name) VALUES ('".$Name."');";					 
		if ($conn->query($mysql_qry1) === TRUE) {
			$last_id = $conn->insert_id;
			$mysql_qry2 = "INSERT INTO user_categories (CategoryID,UserID) VALUES ('".$last_id."','".$UserID."');";
			if ($conn->query($mysql_qry2) === TRUE) {
				$arr = array("categories" => array(array("status" => "1","message"=>"Added successfully","call" => "AddNewCategory")));
				echo json_encode($arr);
			}
			else{
				$arr = array("categories" => array(array("status" => "0","message"=>"Unable to add new category","call" => "AddNewCategory")));
				echo json_encode($arr);		
			}	
		}
		else{
				$arr = array("categories" => array(array("status" => "0","message"=>"Unable to add new category","call" => "AddNewCategory")));
				echo json_encode($arr);		
		}		
	}
	
}

function GetUserCategories($userID)
{
	require "conn.php";
	$mysql_qry = "select categories.* from categories INNER JOIN user_categories on categories.ID=user_categories.CategoryID WHERE user_categories.UserID=".$userID." order by categories.ID ASC";
	$result = mysqli_query($conn ,$mysql_qry);
	$arr = array('categories' => array());
	if(mysqli_num_rows($result) > 0) {
		while($row = mysqli_fetch_assoc($result)) {
			$arr['categories'][] = array( 
				"ID" => $row["ID"],
				"Name" => $row["Name"],
				"call" => "GetUserCategories"
        );
		}
		echo json_encode($arr);
	}
	else {
			$arr = array("categories" => array(array("status" => "0","message"=>"No categories available","call" => "GetUserCategories")));
			echo json_encode($arr);
	}
	$conn->close();
}




function DeleteFriend($ID,$FriendID)
{
    require "conn.php";
	$mysql_qry ="delete from friends where UserID='$ID' and FriendID = '$FriendID' ; 
	             delete from friends where UserID='$FriendID' and FriendID = '$ID'; ";
	if (mysqli_multi_query($conn,$mysql_qry)){
	   $arr = array("user" => array(array("status" => "1","message"=>"Unfriend successfull","call" => "DeleteFriend")));
	   echo json_encode($arr);
	}else{
	     $arr = array("user" => array(array("status" => "0","message"=>"Error deleting friend","call" => "CancelFriendRequest")));
	     echo json_encode($arr);
	}
	$conn->close();
}
function AcceptFriendRequest($ID,$FriendID,$Name)
{
    require "conn.php";
	
	$mysql_qry1 ="UPDATE user_requestes set Accepted=1 where UserID='$FriendID' and RequestedUserID='$ID' and Accepted=0; 
						 INSERT INTO friends (UserID,FriendID,Accepted) VALUES ('$ID', '$FriendID', '1');";
	if (mysqli_multi_query($conn,$mysql_qry1)){
		$arr = array("user" => array(array("status" => "1","call" => "AcceptFriendRequest")));
		$mysql_qry ="select token from usersnotifications where UserID=$FriendID;";
		$result = mysqli_query($conn ,$mysql_qry);
		if($result!=null)
		{
			if(mysqli_num_rows($result) > 0) {
				while($row = mysqli_fetch_assoc($result)) {
					try
					{
						SendNotification($row["token"],$Name." accepted your friend request");			
					}
					catch(Exception $e)
					{	
				
					}				
				}
			}			
		}
		echo json_encode($arr);
	}
	else
	{
		$arr = array("user" => array(array("status" => "0","message"=>"error","call" => "AcceptFriendRequest")));
		echo json_encode($arr);
	}	
	$conn->close();
}


function DeclineFriendRequest($ID,$FriendID)
{
	require "conn.php";
		$mysql_qry ="DELETE FROM user_requestes where userID=".$FriendID." and RequestedUserID=".$ID." and Accepted='0' ";
		if ($conn->query($mysql_qry) === TRUE) {
			  $arr = array("user" => array(array("status" => "1","message" => "","call" => "DeclineFriendRequest")));
		}else{
			$arr = array("user" => array(array("status" => "0","message" => "Failed to update request","call" => "DeclineFriendRequest")));
		}			
		echo json_encode($arr);
	$conn->close();
}


function GetFriendsRequest($userID)
{
	require "conn.php";
	$mysql_qry = "SELECT users.* FROM users inner join user_requestes on user_requestes.UserID=users.ID WHERE user_requestes.Accepted=0 and user_requestes.RequestedUserID=".$userID;
	$result = mysqli_query($conn ,$mysql_qry);
	$arr = array('user' => array());
	if(mysqli_num_rows($result) > 0) {
		while($row = mysqli_fetch_assoc($result)) {
			$arr['user'][] = array( 
				"ID" => $row["ID"],
				"Name" => $row["Name"],
				"Email" => $row["Email"],
				"Password" => $row["Password"],
				"Image" => $row["Image"],
				"Profession" => $row["Profession"],
				"Accepted" => '0',
				"status" => "1",
				"call" => "GetFriendsRequest"
        );
		}
		echo json_encode($arr);
	}
	else {
			$arr = array("user" => array(array("status" => "0","message"=>"No request available","call" => "GetFriendsRequest")));
			echo json_encode($arr);
	}
	$conn->close();
}


function Editprofile($name,$email,$profession)
{
	require "conn.php";
		$mysql_qry ="update users set Name='$name',Profession='$profession' where Email='$email' ";
		if ($conn->query($mysql_qry) === TRUE) {
			  $arr = array("user" => array(array("status" => "1","message" => "Information updated successfully","call" => "Editprofile")));
		}else{
			$arr = array("user" => array(array("status" => "0","message" => "Failed to updated information","call" => "Editprofile")));
		}			
		echo json_encode($arr);
	$conn->close();
}
function updateUserImage($Image,$ID)
{
	require "conn.php";
	$fname ='image-'.GUID().'.png';
	$res = UploadImage($Image,$fname);
	if($res==1){
		$mysql_qry ="update users set Image='$fname' where ID='$ID'";
		if ($conn->query($mysql_qry) === TRUE) {
			  $arr = array("user" => array(array("status" => "1","ImageName" => "$fname","message" => "Uploaded successfully","call" => "updateUserImage")));
	   
		}else{
			$arr = array("user" => array(array("status" => "0","message" => "Uploaded failed","call" => "updateUserImage")));
		}			
	}
	else{
	$arr = array("user" => array(array("status" => "0","message" => "Uploaded failed","call" => "updateUserImage")));
	}
	echo json_encode($arr);
	$conn->close();
}
function UploadImage($base,$fname){
	 try{
		// Decode Image
		$binary=base64_decode($base);
		header('Content-Type: bitmap; charset=utf-8');
		// Images will be saved under 'www/imgupload/uplodedimages' folder
		$file = fopen(__DIR__ .'/Library/images/'.$fname, 'wb');
		// Create File
		fwrite($file, $binary);
		fclose($file);
		return 1;	 			 
	 }
	 catch(Exception $e){
		 return 0;		 
	 }
    
	
}



function CancelFriendRequest($ID,$FriendID)
{
    require "conn.php";
	$mysql_qry ="delete from user_requestes where UserID='$ID' and RequestedUserID = '$FriendID' and Accepted='0';";
	if ($conn->query($mysql_qry) === TRUE) {
	   $arr = array("user" => array(array("status" => "1","call" => "CancelFriendRequest")));
	   echo json_encode($arr);
	}else{
	     $arr = array("user" => array(array("status" => "0","message"=>"error","call" => "CancelFriendRequest")));
	     echo json_encode($arr);
	}
	$conn->close();
}
function AddAsAFriend($ID,$FriendID,$Name)
{
    require "conn.php";
	$mysql_qry1 ="INSERT INTO user_requestes (UserID,RequestedUserID,Accepted) VALUES ('$ID', '$FriendID', '0');";
	if ($conn->query($mysql_qry1) === TRUE) {
		$arr = array("user" => array(array("status" => "1","call" => "AddAsAFriend")));
		$mysql_qry ="select token from usersnotifications where UserID=$FriendID;";
		$result = mysqli_query($conn ,$mysql_qry);
		if(mysqli_num_rows($result) > 0) {
			while($row = mysqli_fetch_assoc($result)) {
				SendNotification($row["token"],$Name." sent you a friend request");
			}
		}
		echo json_encode($arr);
	}else{
		$arr = array("user" => array(array("status" => "0","message"=>"error","call" => "AddAsAFriend")));
		echo json_encode($arr);
	}
	
	
	
	
	$conn->close();
}

function GetAllUserExceptID($ID)
{
require "conn.php";
	$mysql_qry = "SELECT u.*,CASE WHEN u.ID IN (select RequestedUserID from user_requestes where userID=3) THEN 1 ELSE 0 END AS Accepted FROM users u WHERE u.ID <>".$ID." and u.id not in (select friends.FriendID from friends where friends.UserID=".$ID." or friends.FriendID=".$ID." ) order by u.Name asc";
	$result = mysqli_query($conn ,$mysql_qry);
	$arr = array('user' => array());
	if(mysqli_num_rows($result) > 0) {
		while($row = mysqli_fetch_assoc($result)) {
			$arr['user'][] = array( 
				"ID" => $row["ID"],
				"Name" => $row["Name"],
				"Email" => $row["Email"],
				"Password" => $row["Password"],
				"Image" => $row["Image"],
				"Profession" => $row["Profession"],
				"Accepted" => $row["Accepted"],
				"status" => "1",
				"call" => "GetAllUserExceptID"
        );
		}
		echo json_encode($arr);
	}
	else {
			$arr = array("user" => array(array("status" => "0","message"=>"No users avilable","call" => "GetAllUserExceptID")));
			echo json_encode($arr);
	}
	$conn->close();
} 
function GetUserFriend($ID)
{
require "conn.php";
	$mysql_qry = "SELECT u.*,f.Accepted FROM friends f , users u WHERE u.ID = f.FriendID and f.UserID =$ID and f.Accepted='1' order by u.Name asc";
	$result = mysqli_query($conn ,$mysql_qry);
	$arr = array('user' => array());
if(mysqli_num_rows($result) > 0) {
    while($row = mysqli_fetch_assoc($result)) {
        $arr['user'][] = array( 
            "ID" => $row["ID"],
            "Name" => $row["Name"],
            "Email" => $row["Email"],
            "Password" => $row["Password"],
            "Image" => $row["Image"],
            "Profession" => $row["Profession"],
			"Accepted" => $row["Accepted"],
            "status" => "1",
            "call" => "GetUserFriend"
        );
    }
	echo json_encode($arr);
}

	else {
			$arr = array("user" => array(array("status" => "0","message"=>"You currently have no friends","call" => "GetUserFriend")));
			echo json_encode($arr);
	}
	$conn->close();
} 
function login($email,$password,$notificationtocken)
{
	require "conn.php";
	$mysql_qry = "select * from users where email = '$email' and password = '$password';";
	$result = mysqli_query($conn ,$mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row = mysqli_fetch_assoc($result)) {
			
			$mysql_qry1 = "delete from usersnotifications where userID = '".$row["ID"]."';
						  INSERT INTO usersnotifications(userID,token) VALUES ('".$row["ID"]."', '$notificationtocken');";
			if (mysqli_multi_query($conn,$mysql_qry1)){
				$arr = array("user" => array(array("ID"=>$row["ID"],"Name"=>$row["Name"],"Email"=>$row["Email"],"Password"=>$row["Password"],"Image"=>$row["Image"],"Profession"=>$row["Profession"],"location"=>$row["location"],"status" => "1","call" => "login")));
				echo json_encode($arr);
			}			
		}
	}
	else {
			$arr = array("user" => array(array("status" => "0","message"=>"email or password are incorrect","call" => "login")));
			echo json_encode($arr);
	}
	$conn->close();
}

function register($name,$email,$password,$image,$profession,$notificationtocken,$FBuserid,$call)
{
	require "conn.php";
	
	if (strpos($image, 'http') !== false) {
		$fname ='image-'.GUID().'.png';
		$imgPath = __DIR__ .'/Library/images/'.$fname;
		if($FBuserid!="")
		{
			$ch = curl_init($image);
			$fp = fopen($imgPath, 'wb');
			curl_setopt($ch, CURLOPT_FILE, $fp);
			curl_setopt($ch, CURLOPT_HEADER, 0);
			curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
			curl_exec($ch);
			curl_close($ch);
			fclose($fp);
							
		}
		else{
			$img = file_get_contents($image); 
			$fp  = fopen($imgPath, 'w+'); 
			fputs($fp, $img); 
			fclose($fp); 
			unset($img);
		}
	}
	else{
		$fname ="noimage.png";
	}
	$mysql_qry = "INSERT INTO users (Name,Email,Password,Image,Profession,FBuserid,status) VALUES ('$name', '$email', '$password', '".$fname."', '$profession','$FBuserid',1);";
	if ($conn->query($mysql_qry) === TRUE) {
		$last_id= $conn->insert_id;
			if ($notificationtocken!=""){
				$mysql_qry1 = "INSERT INTO usersnotifications(userID,token) VALUES ('".$last_id."', '$notificationtocken');";
				$conn->query($mysql_qry1); 
			}
		$mysql_qry3 ="SELECT * FROM users where ID=".$last_id." ;";
		$result = mysqli_query($conn ,$mysql_qry3);
		if(mysqli_num_rows($result) > 0) {
			while($row = mysqli_fetch_assoc($result)) {				
				$mysql_qry4 = "INSERT INTO user_categories(CategoryID,UserID) VALUES (-1, '".$last_id."');";
				//$conn->query($mysql_qry4);
				if ($conn->query($mysql_qry4) === TRUE) {
					$arr['user'][] = array( 
					"ID" => $row["ID"],
					"Name" => $row["Name"],
					"Email" => $row["Email"],
					"Password" => $row["Password"],
					"Image" => $row["Image"],
					"Profession" => $row["Profession"],
					"FBuserid" => $row["FBuserid"],
					"location" => $row["location"],
					"status" => "1",
					"Message" => "User registered successfully",
					"call" => $call
					);
				}
				echo json_encode($arr);
			}
		}
		else{
			$arr = array("user" => array(array("status" => "0","message"=>"Can't fetch user data","call" => $call)));
			echo json_encode($arr);
		}		
	}
	else 
	{		
		$arr = array("user" => array(array("status" => "0","message"=>"Can't fetch user data","call" => $call)));
		echo json_encode($arr);
	}
	$conn->close();
}
function GUID()
{
    if (function_exists('com_create_guid') === true)
    {
        return trim(com_create_guid(), '{}');
    }

    return sprintf('%04X%04X-%04X-%04X-%04X-%04X%04X%04X', mt_rand(0, 65535), mt_rand(0, 65535), mt_rand(0, 65535), mt_rand(16384, 20479), mt_rand(32768, 49151), mt_rand(0, 65535), mt_rand(0, 65535), mt_rand(0, 65535));
}
function GetUser($name,$email,$password,$image,$profession,$notificationtocken,$FBuserid)
{
	require "conn.php";
	$mysql_qry = "select * from users where email = '$email'";
	
	if($FBuserid!="")
	{
		$mysql_qry = "select * from users where FBuserid = '$FBuserid'";
	}else{
	$FBuserid="";
	}
	$result = mysqli_query($conn ,$mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row = mysqli_fetch_assoc($result)) {
		$mysql_qry1 = "delete from usersnotifications where userID = '".$row["ID"]."';
						  INSERT INTO usersnotifications(userID,token) VALUES ('".$row["ID"]."', '$notificationtocken');";
			if (mysqli_multi_query($conn,$mysql_qry1)){
				$arr = array("user" => array(array("ID"=>$row["ID"],"Name"=>$row["Name"],"Email"=>$row["Email"],"Password"=>$row["Password"],"Image"=>$row["Image"],"Profession"=>$row["Profession"],"location"=>$row["location"],"status" => "1","call" => "GetUser")));
				echo json_encode($arr);
			}
		}
	}
	else {
		register($name,$email,$password,$image,$profession,$notificationtocken,$FBuserid,"GetUser");
	}
	$conn->close();
} 

function SendNotification($registrationIds,$message)
{
// API access key from Google API's Console
define('API_ACCESS_KEY', 'AIzaSyBtlYTK-7Zmi4lGPnyjGD_dAyLUaAzlitg');
$registrationIds = array($registrationIds);
// prep the bundle
$msg = array
(
	'message' 	=> $message,
	'title'		=> 'C-Library',
	'subtitle'	=> 'This is a subtitle. subtitle',
	'tickerText' => 'Ticker text here...Ticker text here...Ticker text here',
	'vibrate'	=> 1,
	'sound'		=> 1,
	'largeIcon'	=> 'large_icon',
	'smallIcon'	=> 'small_icon'
);
$fields = array
(
	'registration_ids' 	=> $registrationIds,
	'data'			=> $msg
);
 
$headers = array
(
	'Authorization: key=' . API_ACCESS_KEY,
	'Content-Type: application/json'
);
 
$ch = curl_init();
curl_setopt( $ch,CURLOPT_URL, 'https://android.googleapis.com/gcm/send' );
curl_setopt( $ch,CURLOPT_POST, true );
curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );
$result = curl_exec($ch );
curl_close( $ch );


}


?>