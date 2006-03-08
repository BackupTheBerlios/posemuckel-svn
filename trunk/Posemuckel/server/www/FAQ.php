<?php


/**
 * Statische Klasse, die Methoden bereithält, um den FAQs-Bereich darzustellen
 */
class FAQ {

	/**
	 * Erzeugt die HTML-Repäsentation der derzeitig betrachteten Ordnerebene
	 * @param string $template Verweis auf das Template der Seite.
	 * @return String $html die HTML-Repäsentation der derzeitig betrachteten Ordnerebene
	 */ 
	function getContent(&$template) {
		$html .= "<table width=\"100%\">\n\t";		
		$count = 1;
		$faq = str_replace('FAQ_Q_1', "", $_SESSION['translate']->it('FAQ_Q_1'));
		while(!empty($faq)) {
			$html .= "<tr>\n\t";
			$html .= "<td><b>".$_SESSION['translate']->it('QUESTION')." ".$count.":</b></td>";
			$html .= "</tr><tr><td>";
			$html .= $_SESSION['translate']->it('FAQ_Q_'.$count)."?";
			$html .= "</td>\n\t";
			$html .= "</tr>\n\t";
			$html .= "<tr>\n\t";
			$html .= "<td><b>".$_SESSION['translate']->it('ANSWER')." ".$count.":</b></td>";
			$html .= "</tr><tr><td>";
			$html .= $_SESSION['translate']->it('FAQ_A_'.$count).".";
			$html .= "</td>\n\t";
			$html .= "</tr>\n\t";
			$count++;
			$faq =  str_replace('FAQ_Q_'.$count, "", $_SESSION['translate']->it('FAQ_Q_'.$count));
		}
		$html .= "</table>\n\t";
		
		return $html;
	}

}
?>