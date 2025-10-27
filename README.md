Wichtig zum Kompilieren und Starten des Programmes:

Kompilieren: 
Da das Chatsystem jetzt mit der Datenbank gekoppelt ist, muss mit folgendem Befehl kompiliert werden: 
javac -cp ".:mysql-connector-j-9.5.0.jar" Main.java

Starten des Programmes: 
Gestartet wird das Programm mit folgendem Befehl: 
java -cp ".:mysql-connector-j-9.5.0.jar" Main

Danach muss der Nutzer noch 'start' eingeben, damit der Server auch wirklich gestartet wird.

Im Anschluss können ClientTest und ClientTest2 wie gewohnt kompiliert und ausgeführt werden. 

Der Chatnutzer wird gefragt, ob er sich anmelden oder registrieren möchte.
Als Antwort werden nur 'anmelden' oder 'registrieren' akzeptiert. 

# Update (Max)

Ablauf: 

du erstellst dir bitte zuerst ein neuen Ordner "out":

mkdir out

Wenn du kompilierst machst du 

javac -d out -cp "out:mysql-connector-j-9.5.0.jar" $(git ls-files '*.java')
(falls du alles kompilieren musst)

oder 

javac -d out -cp "out:mysql-connector-j-9.5.0.jar" Klasse.java
(für ein einzelnes)

Ansonsten startest du das Programm mit

java -cp "out:mysql-connector-j-9.5.0.jar" Main

# To-Do's

Dringend:
- Beenden eines CLientThreads führt zu "null null null..." Ausgabe bei den anderen Clients.
- Wenn kein Client mehr drin, wirft der Server nur noch "Connection reset..."

Braucht man eventuell zum fix von dringend, vielleicht auch nicht:
- vernünftiges schließen aller Connections
- Fehlerbehebung: Falls der Server beendet wird, sollte auch der Anmeldevorgang bzw. Registrierungsvorgang abgebrochen werden
- Nach eintreten der CLients wird die Nachricht, das jemand beigetreten ist auch an die Person selbst geschickt (soll nicht so)
- anpassen der mitteilung, wer im Raum ist (String überarbeiten) (zeigt es immer mit Komma an)