<?php
// API access key from Google API's Console
define( 'API_ACCESS_KEY', 'AIzaSyC9Qix6L-IFgKTV0rDUDHufARrARy7Nq0g' );

if (isset($_POST["id"])){

$registrationIds = array($_POST["id"]);
// prep the bundle
$msg = array
(
	'message' 	=> 'bbb sent u a file',
	'title'		=> 'C-Library',
	'subtitle'	=> 'This is a subtitle. subtitle',
	'tickerText'	=> 'Ticker text here...Ticker text here...Ticker text here',
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
echo $result;
}
?>
<html>
    <head>
        <title>Google Cloud Messaging (GCM) Server in PHP</title>
    </head>
	<body>
		<h1>Google Cloud Messaging (GCM) Server in PHP</h1>	
		<form method="post" action="PushNotificationHelper.php">					                             
			<div>                                
				<textarea rows="2" name="id" cols="23" placeholder="device token id"></textarea>
			</div>
			<div><input type="submit"  value="Send Push Notification via GCM" /></div>
		</form>
		       
    </body>
</html>