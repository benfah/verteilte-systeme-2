package de.thm.mni.vs.gruppeF.server.controller;


import de.thm.mni.vs.gruppeF.server.model.Message;
import de.thm.mni.vs.gruppeF.server.repository.MessageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

// Der REST-Controller des "/messages"-Endpunkt.
@RestController
@RequestMapping(value = "/messages")
public class MessageController {

    // Das MessageRepository wird mittels Dependency Injection in dieses Feld gespeist.
    private MessageRepository repo;

    public MessageController(MessageRepository repo) {
        this.repo = repo;
    }

    // Gibt die Liste aller veröffentlichten Nachrichten zurück.
    // Ruft die dazugehörige Repository-Methode auf und gibt das Resultat zurück.
    // Antwortet mit HTTP 200 und der Liste als JSON, auch wenn die Liste leer ist.
    @GetMapping
    public List<Message> getMessageList() {
        return repo.findAll();
    }

    // Gibt eine einzelne Nachricht zurück, identifiziert anhand der mitgelieferten ID.
    // Ruft die dazugehörige Repository-Methode auf und nutzt zudem ResponseEntity zum beantworten der Anfrage.
    // ResponseEntity gibt je nach Vorhandensein vom Optional des Repositorys den geeigneten HTTP-Statuscode zurück.
    // Bei vorhandener Nachricht wird HTTP 200 zurückgegeben, bei unbekannter ID HTTP 404.
    // Lässt sich die ID nicht in einen Long umwandeln, antwortet Spring vor dem Methodenaufruf mit HTTP 400.
    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessage(@PathVariable Long id) {
        return ResponseEntity.of(repo.findById(id));
    }

    // Empfängt eine Post-Anfrage zum Veröffentlichen einer Nachricht.
    // Der RequestBody wird hier als Klartext erwartet, welcher die gesamte Nachricht abbildet.
    // Antwortet mit HTTP 201, der gespeicherten Nachricht als JSON und ihrer URL im Location-Header.
    @PostMapping
    public ResponseEntity<Message> publishMessage(@RequestBody String text) {
        Message message = repo.save(text);

        // Baut den Location-Header, der unten beim ResponseEntity mitgegeben wird.
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(message.id())
                .toUri();

        return ResponseEntity.created(location).body(message);
    }
}
