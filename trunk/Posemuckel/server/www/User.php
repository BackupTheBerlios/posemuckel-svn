<?php

/**
 * Statische Klasse, die für die Nutzerverwaltung unter Zuhilfenahme der Auth-Bibliothek aus dem pear-Repository
 * zuständig ist.
 */
class User {


	/**
	 * Liefert die HTML-Repräsentation des Logins
	 * @param boolean $disp true, wenn das Login direkt per print ausgegeben werden soll, false sonst
	 * @return string die HTML-Repräsentation des Logins
	 */
	function loginFunction($disp=true) 
	{ 
		$login = $_SESSION['translate']->it("LOGIN");
	    $html  = "<form method=\"post\" action=\"index.php\">\n"; 
	    $html .= "<td class=\"oben\">\n"; 
	    $html .= $_SESSION['translate']->it('USERNAME')."</td><td><input type=\"text\" name=\"username\"></td>\n"; 
	    $html .= "<td class=\"oben\">".$_SESSION['translate']->it('PASSWD')."</td><td><input type=\"password\" name=\"password\"></td>\n"; 
	    $html .= "<td> </td><td><input type=\"submit\" value=\"".$login."\"></td>\n"; 
	    $html .= "</form>\n"; 
	    if ($disp === true) print $html; 
	    return $html; 
	}
	
	/**
	 * Methode zur Prüfung ob der Nutzer eingeloggt ist. Ist dies nicht der Fall, wird der link zum einloggen
	 * andernfalls wird der zum Ausloggen angezeigt. 
	 * @param object $template Verweis auf das Template-Objekt, das in der index.php erzeugt wird.
	 * @param string $dns der DNS-String, der für die Datenbankabfrage benötigt wird.
	 */
	function checkLogin(&$template, $dsn) {
		if(empty($_SESSION['_authsession'])) {		
			$authParams = array( 
			    'dsn'         => $dsn,    # DSN String 
			    'table'       => 'user',    # wie hei§t die Tabelle 
			    'usernamecol' => 'nickname',    # Feld: Username 
			    'passwordcol' => 'password',    # Feld: Pa§wort 
			    'cryptType'   => '',    # VerschlŸsselungstyp 
			    'db_fields'   => '*');    # welche Felder sollen in $_SESSION[auth][data] gespeichert werden; ich will alle <g> 
			$auth = new Auth ('DB', # DB Klasse; mu§ nicht eingefŸgt werden 
			        $authParams, # das Parameter Array 
			        'User::loginFunction(false)', # Name der Funktion, die dann aufgerufen wird, wenn der User nicht authentifiziert ist 
			        false    # show login: Aufruf der Pear-eigenen Login Funktion 
		     );
			$auth->start();
		}
		if(is_null($_SESSION["_authsession"]) & is_null($_REQUEST['username'])) {
			$login = $_SESSION['translate']->it("LOGIN");
			$template->setVariable("LOGIN", "<td><a href=\"index.php?op=login\">".$login."</a></td>");
		}
//		if (is_null($_SESSION["_authsession"]) & !is_null($_REQUEST['username'])) {
//			User::loginFunction(false);
//			$template->setVariable("LOGIN_FORM", User::loginFunction(false));
//			$template->setVariable("CONTENT", "Login failed");
//		}
		if (!is_null($_SESSION["_authsession"])) {
			$logout = $_SESSION['translate']->it("LOG_OFF");
			$template->setVariable("LOGOUT", "<td><a href=\"index.php?op=logout\">".$logout." (".$_SESSION['_authsession']['username'].")</a></td>");
		}
	}

}
?>