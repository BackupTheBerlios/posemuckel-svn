<?php

/**
 * @Statische Klasse zur Darstellung der Order der Projekte auf der Webseite 
 */
 class Folder {
	
	/**
	 * Erzeugt die HTML-Repäsentation der derzeitig betrachteten Ordnerebene
	 * @return String $html die HTML-Repräsentation der derzeitig betrachteten Ordnerebene
	 */ 
	 function showContent() {
	 	if(!empty($_SESSION['parent_folder'])) {
		 	$sql = "SELECT folder_id, name FROM folders WHERE project_id =".$_SESSION['current_project']."" .
		 			" AND parent_folder =".$_SESSION['parent_folder'] . " ORDER BY name";
			$dbresult = $_SESSION['db']->getAll($sql);
	 	} else {
		 	$sql = "SELECT folder_id, name FROM folders WHERE project_id =".$_SESSION['current_project']."" .
		 			" AND (parent_folder IS NULL OR parent_folder ='0') AND unsorted_folder ='0' ORDER BY name";
			$dbresult = $_SESSION['db']->getAll($sql);
		 	$sql = "SELECT folder_id, name FROM folders WHERE project_id =".$_SESSION['current_project']."" .
		 			" AND (parent_folder IS NULL OR parent_folder ='0') AND unsorted_folder ='1' ORDER BY name";
			$dbresult = array_merge($dbresult, $_SESSION['db']->getAll($sql));
	 	}
		$message = Folder::folderMessage($dbresult);
		if(!is_null($message))
			return $message;
		foreach($dbresult as $row){
			if($row[0] == $_SESSION['current_folder']) {
				$html .= "<tr><td><img src=\"images/open_folder.gif\">\n\t";
			} else {
				$html .= "<tr><td><img src=\"images/folder.gif\">\n\t";
			}
			$html .= "<a href =\"./index.php?op=folder&amp;id=".$row['0']."\">".$row['1']."</a></td></tr>\n\t";
		}
		return $html;
	 	
	 }
 	
	/**
	 * Erzeugt die HTML-Repräsentation des Inhaltes des derzeitig geöffneten Ordners
	 * @return String $html die HTML-Repräsentation des Inhaltes des derzeitig geöffneten Ordners
	 */ 
	 function showSubContent() {
	 	$sql = "SELECT folder_id, name FROM folders WHERE parent_folder =".$_SESSION['current_folder']."" .
	 			" ORDER BY name";
		$dbresult = $_SESSION['db']->getAll($sql);
		$message = Folder::folderMessage($dbresult);
		if(!is_null($message))
			return $message;
		foreach($dbresult as $row){
			$html .= "<tr><td><img src=\"images/folder.gif\">\n\t";
			$html .= "<a href =\"./index.php?op=subfolder&amp;id=".$row['0']."\">".$row['1']."</a></td></tr>\n\t";
		}
		return $html;
	 }
	 
	 /** 
	  * Hilfsmethode: Bei eventuellen Fehlermeldungen der Datendank, wird diese zurückgegeben. Solle ein Ordner leer sein wird
	  * eine entsprechende Meldung zurückgegeben.
	  * @param mixed $dbresult ein Verweis auf das Resultat der Datenbankabfragen, die showContent() und showSubContent() liefern
	  * @see showContent(), showSubContent()
	  * @return String die Fehlermeldung bei einem Datenbankfehler, bei einem leeren Ordner einen entsprechenden
	  * Hinweis, NULL sonst
	  */
	 function folderMessage(&$dbresult) {
		if(is_object($dbresult))
			return $dbresult->message."<br>".$dbresult->userinfo;
		if(empty($dbresult) & !$_SESSION['content_status']) {
			return "<tr><td>".$_SESSION['translate']->it('EMPTY_FOLDER').".</td></tr>\n\t";
		}
		return NULL;
	 }
 }
?>
