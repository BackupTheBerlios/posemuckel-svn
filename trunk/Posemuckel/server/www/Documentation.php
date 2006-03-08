<?php


/**
 * Statische Klasse, die Methoden bereithält, um den Dokumentationsbereich darzustellen
 */
class Documentation {

	/**
	 * Hilfsmethode für die Methode getContent()
	 * @see getContent()
	 * @return string die Tabelle als HTML-Repräsentation angereichert um Größenangaben und Erstellungs-
	 * datum der Dateien
	 */
	function getPresentations() {
		$files = array("Posemuckel_Initial-Vortrag.pdf" => "INITIAL_PRESENTATION",
		);		
		$filename=$_SESSION['translate']->it('FILE');
		$filesize=$_SESSION['translate']->it('FILE_SIZE');
		$filedate=$_SESSION['translate']->it('FILE_DATE');
		$filedesc=$_SESSION['translate']->it('FILE_DESCRIPTION');
		$presentations=$_SESSION['translate']->it('PRESENTATIONS');
		$html = "<br><b>$presentations</b><br><br>";
		$html .= "<table width=\"100%\">\n\t";		
		$html .= "<tr>\n\t";
		$html .= "<th>$filename</th><th>$filedesc</th><th>$filesize</th><th>$filedate</th>\n\t";
		$html .= "</tr>\n\t";
		foreach ($files as $item => $desc) {
			if(file_exists("../../doc/".$item)){
				$date=date("j.n.Y",filemtime("../../doc/".$item));
				$fsize=filesize("../../doc/".$item);
				if ($fsize > 1048576)
					$size=number_format($fsize/(1048576),2)." MB";
				else if ($fsize > 1024)
					$size=number_format($fsize/(1024),2)." KB";
				else
					$size=$fsize." B";
		
		
				$description=$_SESSION['translate']->it($desc);	
				$html .= "<tr>\n\t";
				$html .= "<td><a href = \"index.php?getdoc=".$item."\">".$item."</a></td>\n\t";
				$html .= "<td>";
				$html .= $description;
				$html .= "</td>\n\t";
				$html .= "<td>".$size."</td>\n\t";
				$html .= "<td>".$date."</td>\n\t";
				$html .= "</tr>\n\t";
							
			}
		}
		$html .= "</table>\n\t";
		return $html;
	}

	/**
	 * Hilfsmethode für die Methode getContent() um die Admin-Sektion anzuzeigen
	 * @see getContent()
	 * als Schlüssel und die Beschreibung der Datei als Wert enthält
	 * @return string die Tabelle als HTML-Repräsentation der Admin-Sektion
	 */
	function getAdmindoc() {
		$files = array("posemuckel_sag_de.pdf" => "SAG_DESCRIPTION",
		);		
		$filename=$_SESSION['translate']->it('FILE');
		$filesize=$_SESSION['translate']->it('FILE_SIZE');
		$filedate=$_SESSION['translate']->it('FILE_DATE');
		$filedesc=$_SESSION['translate']->it('FILE_DESCRIPTION');
		$admindoc=$_SESSION['translate']->it('ADMIN_DOC');
		$html = "<br><b>$admindoc</b><br><br>";
		$html .= "<table width=\"100%\">\n\t";		
		$html .= "<tr>\n\t";
		$html .= "<th>$filename</th><th>$filedesc</th><th>$filesize</th><th>$filedate</th>\n\t";
		$html .= "</tr>\n\t";
		foreach ($files as $item => $desc) {
			if(file_exists("../../doc/".$item)){
				$date=date("j.n.Y",filemtime("../../doc/".$item));
				$fsize=filesize("../../doc/".$item);
				if ($fsize > 1048576)
					$size=number_format($fsize/(1048576),2)." MB";
				else if ($fsize > 1024)
					$size=number_format($fsize/(1024),2)." KB";
				else
					$size=$fsize." B";
		
		
				$description=$_SESSION['translate']->it($desc);	
				$html .= "<tr>\n\t";
				$html .= "<td><a href = \"index.php?getdoc=".$item."\">".$item."</a></td>\n\t";
				$html .= "<td>";
				$html .= $description;
				$html .= "</td>\n\t";
				$html .= "<td>".$size."</td>\n\t";
				$html .= "<td>".$date."</td>\n\t";
				$html .= "</tr>\n\t";
							
			}
		}
		$html .= "</table>\n\t";
		return $html;
	}
	

	/**
	 * Hilfsmethode für die Methode getContent() um die Developer-Sektion anzuzeigen
	 * @see getContent()
	 * als Schlüssel und die Beschreibung der Datei als Wert enthält
	 * @return string die Tabelle als HTML-Repräsentation der Developer-Sektion
	 */
	function getDevelFiles() {
		$files = array("RFC0815.txt" => "RFC_DESCRIPTION",
					"posemuckel_devref_de.pdf" => "DEVREF_DESCRIPTION"
		);		
		$filename=$_SESSION['translate']->it('FILE');
		$filesize=$_SESSION['translate']->it('FILE_SIZE');
		$filedate=$_SESSION['translate']->it('FILE_DATE');
		$filedesc=$_SESSION['translate']->it('FILE_DESCRIPTION');
		$presentations=$_SESSION['translate']->it('PRESENTATIONS');
		$html .= "<table width=\"100%\">\n\t";		
		$html .= "<tr>\n\t";
		$html .= "<th>$filename</th><th>$filedesc</th><th>$filesize</th><th>$filedate</th>\n\t";
		$html .= "</tr>\n\t";
		foreach ($files as $item => $desc) {
			if(file_exists("../../doc/".$item)){
				$date=date("j.n.Y",filemtime("../../doc/".$item));
				$fsize=filesize("../../doc/".$item);
				if ($fsize > 1048576)
					$size=number_format($fsize/(1048576),2)." MB";
				else if ($fsize > 1024)
					$size=number_format($fsize/(1024),2)." KB";
				else
					$size=$fsize." B";
		
		
				$description=$_SESSION['translate']->it($desc);	
				$html .= "<tr>\n\t";
				$html .= "<td><a href = \"index.php?getdoc=".$item."\">".$item."</a></td>\n\t";
				$html .= "<td>";
				$html .= $description;
				$html .= "</td>\n\t";
				$html .= "<td>".$size."</td>\n\t";
				$html .= "<td>".$date."</td>\n\t";
				$html .= "</tr>\n\t";
							
			}
		}
		$html .= "</table>\n\t";
		return $html;
	}

	/**
	 * Erzeugt die HTML-Repäsentation des Dokumentationsbereichs
	 * @param string $template Verweis auf das Template der Seite.
	 * @return String $html die HTML-Repäsentation der derzeitig betrachteten Ordnerebene
	 */ 

	function getContent(&$template) {
		$devdoc=$_SESSION['translate']->it('DEVELOPER_DOC');
		$userdoc=$_SESSION['translate']->it('USER_DOC');
		$userdesc=$_SESSION['translate']->it('USER_DESC');
		$admindoc=$_SESSION['translate']->it('ADMIN_DOC');
		$javadoc=$_SESSION['translate']->it('JAVADOC');
		$phpdoc=$_SESSION['translate']->it('PHPDOC');


		$html = "<br><b>$userdoc</b><br><br>";
		$html .= "Online:<br>";
		$html .= "<table width=\"100%\">\n\t";		
		$html .= "<tr>\n\t";
		$html .= "<td><a href=\"doc/userdoc/\" target=\"_blank\">Userdocs</a></td>\n\t";
		$html .= "<td>";
		$html .= $userdesc;
		$html .= "</td>\n\t";
		$html .= "</tr>\n\t";
		$html .= "</table>\n\t";

		$html .= "<br><hr>";

		$html .= Documentation::getAdmindoc();

		$html .= "<br><hr>";

		$html .= Documentation::getPresentations();

		$html .= "<br><hr>";

		$html .= "<br><b>$devdoc</b><br><br>";
		$html .= "Online:<br>";
		$html .= "<table width=\"100%\">\n\t";		
		$html .= "<tr>\n\t";
		$html .= "<td><a href=\"doc/javadoc\" target=\"_blank\">Javadocs</a></td>\n\t";
		$html .= "<td>";
		$html .= $javadoc;
		$html .= "</td>\n\t";
		$html .= "</tr>\n\t";
		$html .= "<tr>\n\t";
		$html .= "<td><a href=\"doc/phpdoc\" target=\"_blank\">PHPdocs</a></td>\n\t";
		$html .= "<td>";
		$html .= $phpdoc;
		$html .= "</td>\n\t";
		$html .= "</tr>\n\t";
		$html .= "</table>\n\t";
		$html .= "<br><br>Download:<br>";
		$html .= Documentation::getDevelFiles();

		
		return $html;
	}
	

}
?>