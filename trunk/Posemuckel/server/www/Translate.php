<?php

/**
 * Statische Klasse, die f�r die �bersetzung einzelner Strings zust�ndig ist zust�ndig ist.
 * Die �bersetzungen werden aus einer Datei eingelesen und in einem Array gespeichert
 */
class Translate {
	
	/**
	 * Methode zum Einlesen der �bersetzungsdatei. Sie bedient sich der Hilfmethode load()
	 * @see load()
	 * @param string $file der Pfad zur �bersetzungsdatei inklusive des Dateinamens
	 */
	function Translate($file) {
		$this->langs = array("en", "de");
		foreach($this->langs as $lang) {
			$part = split("\.properties", $file);
			$lang_file = $part[0]."_".$lang.$part[1].".properties";
			$this->load($lang_file, $lang);
		}
	}

	/**
	* Eine Methode, die das Einlesen der �bersetzungen aus
	* den Messages.properties-Dateien des Posemuckel-Servers erm�glicht.
	* @param string $file die Recourcendatei f�r die �bersetzungen inkl. des Pfades
	* @param string $lang das L�nderk�rzel (z.B. "de")
	* @return array Ein assoziatives Array mit den entsprechenden
	*          Konfigurationswerten und -schl�sselw�rtern
	*          aus der Datei. 
	*/
	function load($file, $lang) {
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
			print "$file ist keine regul�re Datei!\n";
			return $configarray;
		}	
		# Jetzt kann es erst losgehen!
		# Datei zum Lesen �ffnen:
		if( $fp = fopen($file,'r') ) {
			while(!feof($fp)) {
				$line = fgets($fp,2048);
				$parts = split("#",$line);
				if( strlen($parts[0]) != 0 ){
					# Vor dem Kommentarzeichen steht was
					# Versuche, ein Gleichheitszeichen zu finden
					if(strpos($parts[0],"=")) {
						$parts2 = split("=",$parts[0]);
#						$trans	= ereg_replace("([^=]*)(=)(.*)","\\3",$line);					
						$trans = str_replace($parts2[0]."=","",$line);
						$this->translation[$lang][$parts2[0]] = chop($trans);
					} 
				} 
			}
			fclose($fp);
			return $configarray;
		} else {
			print "Fehler beim �ffnen der Datei $file!\n";
			return $configarray;
		}
		
	}
	
	/**
	 * Die Methode sucht aus dem assoziativen Array, der mit Hilfe der Methode load angelegt wurde
	 * die ben�tigte �bersetzung f�r die derzeitige Spache heraus und gibt diese zur�ck.
	 * @param string $translation der Name der Phrase, f�r die eine �bersetzung ben�tigt wird.
	 * @return die �bersetzung, wenn diese gefunden wurde, den Namen der Phrase sonst.
	 */
	function it($translation) {
		$current_translation = $this->translation[$_SESSION['current_lang']][$translation];		
		if(!empty($current_translation)) {
			return $current_translation;
		} else {
			return $translation;
		}		
	}
} 
?>