;Posemuckel Client Installation für Win32
;Diese Datei hab ich mit freundlicher Unterstützung von anderen
;OpenSource-Projekten zusammengebaut, deren Installationen
;auch mit diesem Tool laufen, allen voran audacity
;
;Features:
;Es kann in ein beliebiges Verzeichnis des Users installiert
;werden - keine Admin-Rechte nötig.
;Es wird ein Desktopicon angelegt.
;Kein Eingriff in die Registry.
;Eine Verknüpfung mit dem uninstall binary.
;Eintrag ins Startmenü.
;
;  Jens-D. Neppe
;

[Setup]
OutputDir=..\dist
OutputBaseFilename=posemuckel-client-win32-0.4
AppName=Posemuckel Client
AppVerName=Posemuckel Client Version 0.4 "Fuchur"
AppPublisherURL=http://posemuckel.no-ip.org/
AppSupportURL=http://posemuckel.no-ip.org/
AppUpdatesURL=http://posemuckel.no-ip.org/
DirExistsWarning=yes
DefaultDirName={pf}\Posemuckel
DefaultGroupName=Posemuckel
;UninstallDisplayIcon={app}\posemuckel-client-win32-nolibs.jar
LicenseFile=LICENSE.txt
; min versions: Win95, NT 4.0
MinVersion=4.0,4.0
WizardImageFile=Wizard.bmp
WizardSmallImageFile=WizardKlein.bmp

[Languages]
Name: en; MessagesFile: "compiler:Default.isl"
Name: de; MessagesFile: "compiler:Languages\German.isl"

[Files]
; Erstmal unsere eigenen Sachen rüberschieben:
Source: "..\dist\posemuckel-client-0.4.jar"; DestDir: "{app}"
Source: "posemuckel-64x64.ico"; DestDir: "{app}"
Source: "posemuckel-32x32.ico"; DestDir: "{app}"
Source: "LICENSE.txt"; DestDir: "{app}"
Source: "AUTHORS.txt"; DestDir: "{app}"
; Jetzt kommen die fremden Bibliotheken:
Source: "..\lib\3RD-PARTY-LICENSES.txt"; DestDir: "{app}\lib"
Source: "..\lib\Apache-License.txt"; DestDir: "{app}\lib"
Source: "..\lib\Eclipse-Public-License.txt"; DestDir: "{app}\lib"
Source: "..\lib\swt-win32-3139.dll"; DestDir: "{app}\lib"
Source: "..\lib\swt-gdip-win32-3139.dll"; DestDir: "{app}\lib"
Source: "..\lib\log4j-1.2.12.jar"; DestDir: "{app}\lib"
Source: "..\lib\org.eclipse.swt_3.1.0.jar"; DestDir: "{app}\lib"
Source: "..\lib\org.eclipse.swt.win32.win32.x86_3.1.1.jar"; DestDir: "{app}\lib"
Source: "..\lib\org.eclipse.jface_3.1.1.jar"; DestDir: "{app}\lib"
Source: "..\lib\org.eclipse.core.runtime_3.1.1.jar"; DestDir: "{app}\lib"
Source: "..\lib\commons-codec-1.3.jar"; DestDir: "{app}\lib"
; Jetzt die Doku
Source: "..\doc\userdoc\*"; DestDir: "{app}\doc\userdoc"
Source: "..\doc\userdoc\Images\*"; DestDir: "{app}\doc\userdoc\Images"

[Icons]
Name: {userdesktop}\Posemuckel; FileName: "javaw.exe"; Parameters: "-jar -Djava.library.path="".\lib"" ""{app}\posemuckel-client-0.4.jar"""; WorkingDir: {app}; IconFileName: {app}\posemuckel-64x64.ico
Name: "{group}\Posemuckel"; Filename: "javaw.exe"; Parameters: "-jar -Djava.library.path="".\lib"" ""{app}\posemuckel-client-0.4.jar"""; WorkingDir: {app}; IconFileName: {app}\posemuckel-32x32.ico
Name: "{group}\Posemuckel uninstall"; Filename: "{uninstallexe}"


