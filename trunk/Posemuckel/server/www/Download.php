<?php

require_once("lib/HTTP/Download.php");

/**
 * Statische Klasse, die Methoden für den Datei-Download zur VerfŸgung stellt.
 */
class Download {

	/**
	 * Hilfsmethode für die Methode getContent()
	 * @see getContent()
	 * @param array $file ein assoziatives Array, das den Pfad zur Konfigurationsdatei (inklusive des Dateinamens)
	 * als Schlüssel und die Beschreibung der Datei als Wert enthält
	 * @return string die Tabelle als HTML-Repräsentation angereichert um Größenangaben und Erstellungs-
	 * datum der Dateien
	 */
	function getTableRows($files) {
		$html = "";
		foreach ($files as $item => $desc) {
			if(file_exists("../../dist/".$item)){
				$date=date("j.n.Y",filemtime("../../dist/".$item));
				$fsize=filesize("../../dist/".$item);
				if ($fsize > 1048576)
					$size=number_format($fsize/(1048576),2)." MB";
				else if ($fsize > 1024)
					$size=number_format($fsize/(1024),2)." KB";
				else
					$size=$fsize." B";
		
		
				$description=$_SESSION['translate']->it($desc);	
				#var_dump($_SESSION['translate']);
				$html .= "<tr>\n\t";
				$html .= "<td><a href = \"index.php?getfile=".$item."\">".$item."</a></td>\n\t";
				$html .= "<td>";
				$html .= $description;
				$html .= "</td>\n\t";
				$html .= "<td>".$size."</td>\n\t";
				$html .= "<td>".$date."</td>\n\t";
				$html .= "</tr>\n\t";
							
			}
		}
		return $html;
	}

	/**
	 * Liefert unter zu Hilfenahme der Methode getContent() den Inhalt des Downloadbereiches in HTML
	 * @see getTableRows()
	 * @param object $template eine Referenz auf das Template
	 * @return string den Download-Bereich als HTML-Repräsentation
	 */
	function getContent(&$template) {
		$files = array("posemuckel-client-win32-0.4.exe" => "DOWNLOAD_CLIENT_WININST",
					"posemuckel-client-bin-win32-0.4.zip" => "DOWNLOAD_CLIENT_WIN",		
					"posemuckel-client-bin-linux-0.4.tgz" => "DOWNLOAD_CLIENT_LIN",
"posemuckel-sources-0.4.zip" => "DOWNLOAD_SOURCE"
		);
		$filename=$_SESSION['translate']->it('FILE');
		$filesize=$_SESSION['translate']->it('FILE_SIZE');
		$filedate=$_SESSION['translate']->it('FILE_DATE');
		$filedesc=$_SESSION['translate']->it('FILE_DESCRIPTION');
		$html = "<table width=\"100%\">\n\t";
		$html .= "<tr>\n\t";
		$html .= "<th>$filename</th><th>$filedesc</th><th>$filesize</th><th>$filedate</th>\n\t";
		$html .= "</tr>\n\t";
		
		$html .= Download::getTableRows($files);

		$html .= "</table>\n\t";
		return $html;
	}
	
	/**
	* Stellt die Funktionalität für den Datei-Download bereit
	* @param string $file der Pfad zur Datei inklusive des Dateinamens
	*/ 
	function download($file,$dir) {
		if(file_exists($dir.$file)){
			set_time_limit(1000);
			header("Content-Type: application/octet-stream");
			header("Content-Disposition: attachment; filename=\"".basename($file)."\"");
			header("Content-length: ".filesize($dir.$file));
			$fp = fopen($dir.$file, "r");
			while(!feof($fp)) {
				$line = fgets($fp, 512);
				print $line;
			}
			fclose($file);
			set_time_limit(30);
		}
	}
}
?>
