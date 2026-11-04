# Verteilte Systeme - Aufgabe zwei

Voraussetzung: JDK 26. Alle Kommandos im Projektverzeichnis ausführen.

Projekt bauen (macOS/Linux):

```bash
./mvnw clean package
```

Unter Windows stattdessen `mvnw.cmd clean package` verwenden.

Server starten:

```bash
java -jar target/aufgabe2-0.0.1-SNAPSHOT-server.jar
```

Der Server läuft unter `http://localhost:8080`.

Client in einem zweiten Terminal starten:

```bash
java -jar target/aufgabe2-0.0.1-SNAPSHOT-client.jar
```

Kommandos im Client:

- `p Nachricht` – Nachricht veröffentlichen.
- `l` – Alle Nachrichten anzeigen.
- `l 10` – Nachricht mit der ID 10 anzeigen.
- `h` – Hilfe anzeigen.
- `q` – Client beenden.