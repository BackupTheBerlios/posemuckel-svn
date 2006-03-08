<?php

/**
 * Statische Klasse zur Darstellung der URLs eines Projektes auf der Webseite
 */
 class Url {
 
/**
 * Eigentliche Methode, die für die Darstellung der URLs eines Projektes auf der Webseite zuständig ist
 * @param boolean $project gibt an, ob die URLs gesucht werden sollen, die direkt un einem projekt abgelegt wurden und nicht in einem extra Ordner 
 * 		true, wenn die URLs eines Projektes angezeigt werden sollen, false, wenn die URLs eines Ordners gesucht werden.
 * @return die HTML-Repräsentation der URLs
 */
	 function showUrl($project=false) {
	 	if($project) {
			$sql = "SELECT u.title, u.address FROM folder_urls AS f, url AS u" .
					" WHERE f.folder_id =-1" .
					" AND f.url_id=u.url_id" .
					" ORDER BY u.title";
	 	} else {
		 	$sql = "SELECT unsorted_folder FROM folders WHERE folder_id =".$_SESSION['current_folder'].
		 			" AND project_id =".$_SESSION['current_project'];
			$isUnsortedFolder = $_SESSION['db']->getOne($sql);
			if(!$isUnsortedFolder) {
				$sql = "SELECT u.title, u.address FROM folder_urls AS f, url AS u" .
						" WHERE f.folder_id=".$_SESSION['current_folder'].
						" AND f.url_id=u.url_id" .
						" ORDER BY u.title";
			} else {
				$sql = "SELECT DISTINCT title, address FROM url" .
						" WHERE url_id IN (SELECT DISTINCT url_id FROM ratings WHERE rating >=3 AND project_id=" .$_SESSION['current_project'].
						" AND url_id NOT IN (SELECT url_id FROM folder_urls WHERE project_id =".$_SESSION['current_project']."))" .
						" ORDER BY title";
			}
	 	}
		$dbresult = $_SESSION['db']->getAll($sql);
		$message = Url::setContentStatus($dbresult);
		if(!is_null($message))
			return $message;
		foreach($dbresult as $row){
			$html .= "<tr><td><a title =\"".$row['1']."\" href =\"".$row['1']."\" target=\"_blank\">".$row['0']."</a></td></tr>\n\t";
		}
		return $html;
	 	
	 }
	 
	/**
	 * Hilfsmethode: Wenn der Inhalt eines Ordners aus der Datenbank zurückgegeben wurde, wird der Inhalt des Ergebnisse
	 * auf eventuelle Fehlermeldungen und darauf, ob das ergebnis leer ist. 
	 * @param mixed $dbresult Verweis auf das Ergebis, das die Datenbankabfrage aus showUrl() ergeben hat
	 * @see showUrl();
	 */
	 function setContentStatus(&$dbresult) {
		$_SESSION['content_status'] = false;
		if(is_object($dbresult))
			return $dbresult->message."<br>".$dbresult->userinfo;
		if(!empty($dbresult))
			$_SESSION['content_status'] = true;
	 }
 }
?>
