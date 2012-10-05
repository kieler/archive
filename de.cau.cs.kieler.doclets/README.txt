DOCLET INSTALLIEREN
===================

Das Doclet muss gebaut und installiert werden um mit dem nächtlichen Rating Build Job zu
funktionieren. Und das macht man folgendermaßen:

1. Lokal mit Maven bauen:

     mvn clean package install

   Damit das funktioniert müssen gewisse Umgebungsvariablen für Maven gesetzt sein, was
   man auf Aeon folgendermaßen erreicht:

     . /home/java/java-env

2. Auf attosec einloggen und www-data werden:

     ssh attosec
     sudo su - www-data

3. Der www-data User hat standardmäßig noch keine Shell offen, daher erstmal noch bash
   starten bevor es weiter geht:

     bash

4. Doclet vom eigenen Maven-Reposiroty ins Maven-Repository von www-data kopieren:

     cp /home/<user>/.m2/repository/de/cau/cs/kieler/de.cau.cs.kieler.doclets/0.0.1-SNAPSHOT/de.cau.cs.kieler.doclets-0.0.1-SNAPSHOT.jar /var/www/.m2/repository/de/cau/cs/kieler/de.cau.cs.kieler.doclets/0.0.1-SNAPSHOT/

5. Wieder ausloggen:

     exit
     exit
     exit

