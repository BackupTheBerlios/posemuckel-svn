<?php

/**
 * @File:  index.php
 *
 * Die Hauptsteuerungsdatei der Webseite
 *
 * Das Umschalten auf eine andere Sprache funktioniert folgendermaßen:
 * Der Benutzer klickt die gewŸünschte Sprache an, woraufhin die Sprache mit dem Aufruf von
 * $_SESSION['current_lang'] = $_REQUEST['id'] eingestellt wird.
 * Zu diesem Zeitpunkt steht der Content der Seite aber schon in entsprechenden Content-Variablen.
 * Daher muss die Seite mittels des header-Befehls neu geladen werden. Bei der Gelegenheit wird auch gleich noch
 * aus $_SESSION['http_referer_cache'] die url rekonsruiert, die der Benutzer gesehen hat, bevor er die Sprache
 * wechselte. Dies ist notwendig, da sich hinter dem Link zur gewŸünschten Sprache ein statischer Link
 * ("index.php?lang=XX" XX=das SprachenkŸrzel) verbirgt. 
 *
 * @version $Id: index.php,v 1.39 2006/02/28 20:56:10 neppe Exp $
 * @author Holger Bach
 */
if (PHP_OS=="WINNT")
    /* This line is for windows environments */
	ini_set("include_path", ini_get("include_path")."\\;" . "lib\\;");
else 
    /* This line is for *nix/linux environments */
	ini_set("include_path", ini_get("include_path")."/:" . "lib/:");

require_once ("Template/IT.php");
require_once ("Auth/Frontend/Html.php");
require_once ("Auth/Auth.php");
require_once ("User.php");
require_once ("Project.php");
require_once ("Folder.php");
require_once ("Url.php");
require_once ("Config.php");
require_once ("Download.php");
require_once ("Documentation.php");
require_once ("Translate.php");
require_once ("FAQ.php");

session_start();
$dsn = Config::getConfig("../../src/posemuckel/server/posemuckel_server.cfg");
define(TEMPLATE_DIR, "./");

$referrer = urlencode($_SERVER['PHP_SELF']."?".$_SERVER['QUERY_STRING']);

//    $db = DB::connect(_DNS);
if(empty($_SESSION)){
	define ('_SCRIPT', $_SERVER['SCRIPT_NAME']); 
	define ('_DBTYPE', 'mysql'); 
	define ('_DBUSER', $dsn['DB_USER']); 
	define ('_DBPASS', $dsn['DB_PASS']); 
	define ('_DBHOST', $dsn['DB_HOST']); 
	define ('_DBNAME', $dsn['DB_NAME']);  
	define ('_DSN',_DBTYPE.'://'._DBUSER.':'._DBPASS.'@'._DBHOST.'/'._DBNAME); 
	$_SESSION['dsn'] = _DSN;
  	$_SESSION['current_lang'] = $_REQUEST['lang'];
	if(substr($_SERVER['HTTP_ACCEPT_LANGUAGE'], 6, 2) !="de") {
		$_SESSION['current_lang'] = "en";
	} else {
		$_SESSION['current_lang'] = "de";		
	}
}
$_SESSION['translate'] = new Translate("../conf/lang/Messages.properties");
$_SESSION['template'] = new HTML_Template_IT(TEMPLATE_DIR);
$_SESSION['template']->loadTemplateFile(TEMPLATE_DIR . "template.tpl");
$template = &$_SESSION['template'];

User::checkLogin($template, $_SESSION['dsn']);  
//$template->setVariable("PROJECTS", Project::getList($template, $_SESSION['dsn']));
Project::getList($template, $_SESSION['dsn']);


if(!empty($_REQUEST['getfile'])) {
	$file=$_REQUEST['getfile'];
	if(!stristr ($file, ".." )) {
		Download::download($file,"../../dist/");
	}
}

if(!empty($_REQUEST['getdoc'])) {
	$file=$_REQUEST['getdoc'];
	if(!stristr ($file, ".." )) {
		Download::download($file,"../../doc/");
	}
}

switch($_REQUEST['lang']) {
  case "de":
  	$_SESSION['current_lang'] = $_REQUEST['lang'];
  	setLocale(LC_ALL, $_REQUEST['lang']);
	unset($_REQUEST['lang']);
    header("location:".$_SESSION['http_referer_cache']);
    break;
    
  case "en":
  	$_SESSION['current_lang'] = $_REQUEST['lang'];
  	setLocale(LC_ALL, $_REQUEST['lang']);
	unset($_REQUEST['lang']);
    header("location:".$_SESSION['http_referer_cache']);
    break;
}

$_SESSION['http_referer_cache'] = urldecode($referrer);

#Diese Links sind nur zu sehen, wenn der User sich nicht
#gerade anmeldet. Denn dann ist kein Platz dafür da.
if($_REQUEST['op'] != "login") {
	$template->setVariable("DOWNLOAD_LINK", "<a href = \"index.php?op=download\">Downloads</a>");
	$template->setVariable("DOCS", "<a href=\"index.php?op=docs\">Docs</a>");  	
	$template->setVariable("SCREENSHOTS","<a href=\"index.php?op=screenshots\">Screenshots</a>");
	$template->setVariable("FAQS","<a href=\"index.php?op=faq\">FAQs</a>");
    $template->setVariable("HALL_OF_FAME","<a href=\"index.php?op=hall_of_fame\">".$_SESSION['translate']->it('HALL_OF_FAME_TITLE')."</a>");
}

switch($_REQUEST['op']) {
  case "logout":
  	session_destroy();
  	header("location:".$_SERVER['PHP_SELF']);
    break;
    
  case "login":
	$template->setCurrentBlock("LOGIN");
	$template->setVariable("LOGIN", User::loginFunction(false));
	$html = "Bitte geben Sie Ihren Benutzernamen und Ihr Passwort ein.";
	$template->setVariable("CONTENT", $html);
    break;
    
  case "pubproj":
	unset($_SESSION['parent_folder']);
  	$_SESSION['current_project'] = $_REQUEST['id'];
  	$template->setVariable("TITLE", $_SESSION['translate']->it('FOLDERS_OF_PROJECTS').":");
//	$template->setVariable("URL", Url::showUrl(true));  	
	$template->setVariable("CONTENT", Folder::showContent());  	
    break;
    
  case "privproj":
	unset($_SESSION['parent_folder']);
  	$_SESSION['current_project'] = $_REQUEST['id'];
  	$template->setVariable("TITLE", $_SESSION['translate']->it('FOLDERS_OF_PROJECTS').":");
	$template->setVariable("URL", Url::showUrl(true));  	
	$template->setVariable("CONTENT", Folder::showContent());  	
    break;
    
  case "folder":
//	$_SESSION['parent_folder'] = $_SESSION['current_folder'];
  	$_SESSION['current_folder'] = $_REQUEST['id'];
  	$template->setVariable("TITLE", $_SESSION['translate']->it('FOLDERS_OF_PROJECTS').":");
	$template->setVariable("SUBTITLE", $_SESSION['translate']->it('SUBFOLDERS_OF_FOLDER').":");  	
//	$template->setVariable("URL", Url::showUrl());  	
	$template->setVariable("CONTENT", Folder::showContent());  	
	$template->setVariable("SUBURL", Url::showUrl());  	
	$template->setVariable("SUBFOLDERS", Folder::showSubContent());  	
    break;
    
  case "subfolder":
	$_SESSION['parent_folder'] = $_SESSION['current_folder'];
  	$_SESSION['current_folder'] = $_REQUEST['id'];
  	$template->setVariable("TITLE", $_SESSION['translate']->it('FOLDERS_OF_PROJECTS').":");
	$template->setVariable("SUBTITLE", $_SESSION['translate']->it('SUBFOLDERS_OF_FOLDER').":");  	
	$template->setVariable("URL", Url::showUrl());  	
	$template->setVariable("CONTENT", Folder::showContent());  	
	$template->setVariable("SUBURL", Url::showUrl());  	
	$template->setVariable("SUBFOLDERS", Folder::showSubContent());  	
    break;
    
  case "download":
	unset($_SESSION['parent_folder']);
  	unset($_SESSION['current_folder']);
	$template->setVariable("TITLE", "Aktuelle Version des Posemuckel Client");  	
	$template->setVariable("CONTENT", Download::getContent($template));  	
    break;


  case "docs":
  		#$doc_links ="<a href=\"doc/javadoc\" target=\"_blank\">Javadocs</a><br>";
  		#$doc_links .="<a href=\"doc/phpdoc\" target=\"_blank\">PHPdocs</a>";
		#$template->setVariable("TITLE", "Dokumentation zur Software dieses Projektes");  	
		$template->setVariable("CONTENT", Documentation::getContent($template));  	
    break;
    
   case "screenshots":
	    $browsertitle=$_SESSION['translate']->it('SCREENSHOT_BROWSER_TITLE');
	    $browseralt=$_SESSION['translate']->it('SCREENSHOT_BROWSER_ALT');
	    $projectstitle=$_SESSION['translate']->it('SCREENSHOT_PROJECTS_TITLE');
	    $projectsalt=$_SESSION['translate']->it('SCREENSHOT_PROJECTS_ALT');

  		$html ="<b>$projectstitle</b><br><img src=\"images\Projects_Shot.jpg\" alt=\"$projectsalt\"><br><br>";
  		$html .= "<b>$browsertitle</b><br><img src=\"images\Browser_Shot.jpg\" alt=\"$browseralt\">";
		$template->setVariable("CONTENT", $html);  	
    break;
    
    case "faq":
	  	$template->setVariable("TITLE", $_SESSION['translate']->it('FAQs'));
	  	$template->setVariable("CONTENT", FAQ::getContent($template));
    break;
    
    
    case "hall_of_fame":
	  	$template->setVariable("TITLE", $_SESSION['translate']->it('HALL_OF_FAME_TITLE'));
	  	$template->setVariable("CONTENT", $_SESSION['translate']->it('HALL_OF_FAME_CONTENT'));
    break;
        
    
    default: //Welcome-Message
	  	$template->setVariable("TITLE", $_SESSION['translate']->it('MAIN_PAGE_TITLE'));
	  	$template->setVariable("CONTENT", $_SESSION['translate']->it('MAIN_PAGE_CONTENT'));
}
  /* end switch */

$template->parse();
$content = $template->show();

?>
