<?php 


if($_SERVER['REQUEST_METHOD']=='POST'){

	require 'config.php';
	$JResponse=array();
	$dataArray=array();

	$searchCol=$connection->real_escape_string($_POST['search_col']);
	$value=$connection->real_escape_string($_POST['value']);

	$sql="SELECT 
	nyala_farmer.name,
	nyala_collection.capacity,
	nyala_collection.id,
	nyala_collection.time,
	nyala_collection.day,
	nyala_collection.month,
	nyala_collection.year,
	nyala_collection.farmer_nid 
	FROM `nyala_collection` INNER JOIN `nyala_farmer` ON nyala_farmer.nid = nyala_collection.farmer_nid WHERE  nyala_collection.".$searchCol."='$value'";
	
	$result=$connection->query($sql);
	if ($result->num_rows>0) {
		while ($row=$result->fetch_assoc()) {
			array_push($dataArray, $row);
		}

		$JResponse['responseCode']="1";
		$JResponse['responseData']=$dataArray;
		echo json_encode($JResponse);
	}else{
		$JResponse['responseCode']="0";
		$JResponse['responseMessage']="No collections found";
		echo json_encode($JResponse);
	}

}

?>