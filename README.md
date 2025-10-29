Wichtig zum Kompilieren und Starten des Programmes:

Wenn nur eine Klasse kompiliert werden soll:
Da das Chatsystem jetzt mit der Datenbank gekoppelt ist, muss mit folgendem Befehl kompiliert werden:
javac -d out -cp "out:mysql-connector-j-9.5.0.jar" Main.java

Wenn alles auf einen Schlag kompiliert werden soll:
javac -d out -cp "out:mysql-connector-j-9.5.0.jar" $(git ls-files '\*.java')

Starten des Programmes:
Gestartet wird das Programm mit folgendem Befehl:
java -cp ".:mysql-connector-j-9.5.0.jar" Main

Danach muss der Nutzer noch 'start' eingeben, damit der Server auch wirklich gestartet wird.

Im Anschluss können ClientTest und ClientTest2 wie gewohnt kompiliert und ausgeführt werden.

Der Chatnutzer wird gefragt, ob er sich anmelden oder registrieren möchte.
Als Antwort werden nur 'anmelden' oder 'registrieren' akzeptiert.
