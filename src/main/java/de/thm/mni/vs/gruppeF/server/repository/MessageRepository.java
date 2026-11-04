package de.thm.mni.vs.gruppeF.server.repository;


import de.thm.mni.vs.gruppeF.server.model.Message;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Implementierung einer einfachen Repository mittels einer HashMap.
// Die Speicherung erfolgt ausschließlich im Arbeitsspeicher; bei einem Serverneustart gehen alle Nachrichten verloren.
@Repository
public class MessageRepository {

    private Map<Long, Message> messageMap = new HashMap<>();
    private long nextId = 1L;

    // Gängige save-Methode von Repositorys in Spring Boot.
    // Speichert den Text als Message in der HashMap.
    // Vergibt automatisch eine fortlaufende ID ab 1 und gibt die gespeicherte Message zurück.
    public Message save(String text) {
        Message m = new Message(nextId++, text);
        messageMap.put(m.id(), m);

        return m;
    }

    // Gängige findById-Methode von Repositorys in Spring Boot.
    // Gibt eine Nachricht anhand der ID, sofern gefunden, aus.
    // Bei unbekannter ID wird Optional.empty() zurückgegeben.
    public Optional<Message> findById(long id) {
        return Optional.ofNullable(messageMap.get(id));
    }

    // Gängige findAll-Methode von Repositorys in Spring Boot.
    // Gibt alle Nachrichten, die sich gerade in der HashMap befinden, aus.
    public List<Message> findAll() {
        return List.copyOf(messageMap.values());
    }
}
