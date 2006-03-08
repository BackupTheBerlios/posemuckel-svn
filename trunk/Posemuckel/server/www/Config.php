<?php

/**
 * Statischen Klasse zur Konfiguration der Webseite durch Einlesen der Konfigurationsdatei
 * des Posemuckel-Servers
 */
class Config {
	
	/**
	* Eine Funktion, die das Einlesen der DB-Konfiguration aus
	* der Konfigurationsdatei des Posemuckel-Servers ermöglicht.
	* @author: Jens-D. Neppe
	* @param string $file Der Pfad zur Konfigurationsdatei inklusive des Dateinamens
	* @return array Ein assoziatives Array mit den entsprechenden
	*          Konfigurationswerten und -schlüsselwörtern
	*          aus der Datei. Existiert die Konfigugationsdatei nicht wird eine Fehlermeldung
	* 		  im array verpackt zurückgegeben.
	*/ 
	function getConfig($file) {
		$configarray = array();
		if(!file_exists($file)) {
			print "Die Datei $file existiert nicht!\n";
			return $configarray;
		}
		# Die Datei ist da, dann schauen wir mal, ob
		# dir Datei wirklich lesbar ist.
		if(!is_readable($file)) {
			print "Die Datei $file ist nicht lesbar!\n";
			return $configarray;
		}
		# Jetzt mal schauen, ob es auch wirklich eine Datei ist!
		if(!is_file($file)) {
			print "$file ist keine reguläre Datei!\n";
			return $configarray;
		}	
		# Jetzt kann es erst losgehen!
		# Datei zum Lesen öffnen:
		if( $fp = fopen($file,'r') ) {
			while(!feof($fp)) {
				$line = fgets($fp,1024);
				$parts = split("#",$line);
				if( strlen($parts[0]) != 0 ){
					# Vor dem Kommentarzeichen steht was
					# Versuche, ein Gleichheitszeichen zu finden
					if(strpos($parts[0],"=")) {
						$parts2 = split("=",$parts[0]);
						if($parts2[0] == "DB_HOST") {
							$configarray['DB_HOST']=chop($parts2[1]);
						} 
						if($parts2[0] == "DB_NAME") {
							$configarray['DB_NAME']=chop($parts2[1]);
						}
						if($parts2[0] == "DB_USER") {
							$configarray['DB_USER']=chop($parts2[1]);
						}
						if($parts2[0] == "DB_PASS") {
							$configarray['DB_PASS']=chop($parts2[1]);
						}
					} 
				} 
			}
			fclose($fp);
			return $configarray;
		} else {
			print "Fehler beim Öffnen der Datei $file!\n";
			return $configarray;
		}
		
	}
} 
?>