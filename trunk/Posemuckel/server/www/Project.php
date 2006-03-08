<?php

include_once("DB.php");

/**
 * Statische Klasse, die für die Darstellung der Projekte zuständig ist.
 */
class Project {

/**
 * Stellt die Verbindung zur Datenbank her, wenn diese nicht schon besteht und
 * ruft dann die Methoden auf, die die privaten und die öffentlichen Projekte
 * darstellen
 * @param object $template Verweis auf das Template-Objekt, das in der index.php erzeugt wird.
 * @param string der dsn-String, der für die Verbindung zur Datenbank benötigt wird.
 * @return string die Projekte in einer HTML-Darstellung
 * @see getPrivateProjects(), getPublicProjects()
 */
	function getList(&$template, $dsn) {
		if(!DB::isConnection($_SESSION['db'])) {
			$_SESSION['db'] = DB::connect($dsn);			
		}
		if(!empty($_SESSION['_authsession']))
			$html = Project::getPrivateProjects($template);
		$html .= Project::getPublicProjects($template);
		return $html;
	}

/** 
 * Verantwortlich für die Darstellung der öffentlichen Projekte. Die Methode Ÿübergibt den Inhalt, den die Datenbank-
 * anfrage ergibt, als HTML-Repräsentation direkt an das Template
 * @param object $template Verweis auf das Template der Seite
 */	
	function getPublicProjects(&$template) {
		$sql = "SELECT project_id, project_title AS title, project_description FROM projects WHERE project_type ='PUBLIC' ORDER BY title";
		$dbresult = $_SESSION['db']->getAll($sql);
		$template->setVariable("PUBLIC_PROJECTS_TITLE", $_SESSION['translate']->it('PUBLIC_PROJECTS'));
		foreach($dbresult as $row){
			$html .= "<tr><td><a title = \"".$row['2']."\" href = \"./index.php?op=pubproj&amp;id=".$row['0']."\">".$row['1']."</a></td></tr>\n\t";
		}
		$template->setVariable("PUBLIC_PROJECTS", $html);
//		return $html;
	}
	

/** 
 * Verantwortlich für die Darstellung der privaten Projekte. Die Methode Ÿbergibt den Inhalt, den die Datenbank-
 * anfrage ergibt, als HTML-Repräsentation direkt an das Template
 * @param object $template Verweis auf das Template der Seite
 */	
	function getPrivateProjects(&$template) {
		$sql = "SELECT p.project_id, p.project_title, p.project_description FROM projects AS p, members AS m" .
				" WHERE m.user_nickname='".$_SESSION['_authsession']['username'].
				"' AND p.project_id=m.project_id" .
				" AND p.project_type ='PRIVATE' ORDER BY p.project_title";
		$template->setVariable("PRIVATE_PROJECTS_TITLE", $_SESSION['translate']->it('PRIVATE_PROJECTS'));
		$dbresult = $_SESSION['db']->getAll($sql);
		foreach($dbresult as $row){
			$html .= "<tr><td><a title = \"".$row['2']."\" href = \"./index.php?op=privproj&amp;id=".$row['0']."\">".$row['1']."</a></td></tr>";
		}
		$template->setVariable("PRIVATE_PROJECTS", $html);
//		return $html;
	}
	
}
?>