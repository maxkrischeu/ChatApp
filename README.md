Wichtig zum Kompilieren und Starten des Programmes:

Kompilieren: 
Da das Chatsystem jetzt mit der Datenbank gekoppelt ist, muss mit folgendem Befehl kompiliert werden: 
javac -cp ".:mysql-connector-j-9.5.0.jar" Main.java

Starten des Programmes: 
Gestartet wird das Programm mit folgendem Befehl: 
java -cp ".:mysql-connector-j-9.5.0.jar" Main

Danach muss der Nutzer noch 'start' eingeben, damit der Server auch wirklich gestartet wird.

Im Anschluss können ClientTest und ClientTest2 wie gewohnt kompiliert und ausgeführt werden. 
