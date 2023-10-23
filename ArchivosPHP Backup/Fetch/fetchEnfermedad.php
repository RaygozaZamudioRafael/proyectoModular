<?php

define('HOST','localHost');
define('USER','root');
define('PASS','');
define('DB','detectorenfermedadeshojastomate');

$con = mysqli_connect(HOST,USER,PASS,DB);

$enfermedad = $_POST['enfermedadNombre'];

$result = mysqli_query($con,"SELECT * from enfermedades WHERE nombreEnfermedad='$enfermedad'");

while($row = mysqli_fetch_assoc($result)){
	$jsonresult[] = $row;	
}

echo json_encode($jsonresult);

mysqli_close($con);
?>
