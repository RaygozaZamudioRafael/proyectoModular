<?php

define('HOST','localHost');
define('USER','root');
define('PASS','');
define('DB','detectorenfermedadeshojastomate');

$con = mysqli_connect(HOST,USER,PASS,DB);

$tipoPeticion = $_POST['tipoPeticion'];

$enfermedad = $_POST['enfermedadNombre'];


$result = mysqli_query($con,"SELECT * from enfermedades WHERE nombreEnfermedad='$enfermedad'");
#$jsonresult = array();
$contador = 0;
while($row = mysqli_fetch_assoc($result)){
	$jsonresult[$contador]['idEnfermedad'] = $row['idEnfermedad'];
	$jsonresult[$contador]['descripcionEnfermedad'] = $row['descripcionEnfermedad'];
	$jsonresult[$contador]['tratamientoSugerido'] = $row['tratamientoSugerido'];
	$contador = $contador + 1;			
}
$jsondata = json_encode($jsonresult);

$json = json_decode($jsondata,true);

echo $json[0][$tipoPeticion];

mysqli_close($con);
?>
