package de.thm.mni.vs.gruppeF.client;

import de.thm.mni.vs.gruppeF.server.model.Message;
import org.springframework.http.HttpStatusCode;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import tools.jackson.databind.json.JsonMapper;

public class Client {

    private static volatile boolean running = true;
    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    public static void main(String[] args) {

        // Anfangs die Hilfsausgabe drucken
        printHelp();


        // Hier folgt eine Scanner-REPL-Loop, die die jeweiligen Kommandos interpretiert
        Scanner scanner = new Scanner(System.in);

        while (running) {
            System.out.println();
            System.out.print("> ");
            interpretCommand(scanner.nextLine());
        }
    }

    public static void printHelp() {
        System.out.println("""
            Usable Commands:
                l - List all posted messages
                l <ID> - Show a certain message
                p <MESSAGE> - Post a message
                q - Quit program
                h - Print this help message""");
    }

    public static void interpretCommand(String cmd) {
        String[] cmds = cmd.split(" ");

        //Hier folgt ein switch-Statement, bei dem das erste Wort des Kommandos die Abfolge bestimmt.
        switch(cmds[0]) {
            //Hilfsbefehl
            case "h":
                printHelp();
                break;
            //Listen-Befehl
            case "l":
                if(cmds.length > 1) {
                    printMessage(Long.parseLong(cmds[1]));
                } else {
                    listMessages();
                }
                break;
            //Print-Befehl
            case "p":
                if(cmds.length == 1) {
                    System.out.println("Insufficient arguments. Please enter a message");
                    return;
                }
                String message = Arrays.stream(cmds).skip(1).collect(Collectors.joining(" "));
                postMessage(message);
                break;
            //Quit-Befehl -> beendet die while-Loop aus main
            case "q":
                running = false;
                break;
            default:
                System.out.println("Unknown command. Please try again");
        }
    }

    //Führt die Anfrage aus, um alle Nachrichten beim Backend anzufragen und als Liste zu drucken
    public static void listMessages() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/messages"))
                .GET()
                .build();

        Optional<Message[]> messageArr = sendAndDeserialize(request, Message[].class);

        messageArr.ifPresent(arr -> {
            for(Message msg : arr) {
                System.out.println("ID: " + msg.id() + " - " + msg.message());
            }

            System.out.println();
            System.out.println(arr.length + " message(s) total");
        });

    }

    //Führt die Anfrage aus, um eine Nachricht beim Backend zu posten. Gibt zudem ID und Nachricht vom Backend wieder aus
    public static void postMessage(String message) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/messages"))
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .build();

        Optional<Message> optional = sendAndDeserialize(request, Message.class);
        optional.ifPresent(msg -> System.out.println("ID: " + msg.id() + ", Message: " + msg.message()));
    }

    // Folgende Methode kümmert sich um das senden einer HttpRequest, mit einfachem Exception-Catching
    // Zudem werden Optionals verwendet, um implizite null-Werte zu vermeiden
    public static Optional<HttpResponse<String>> send(HttpRequest request) {
        HttpResponse<String> response = null;
        try {
            response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Exception happened while trying to send request: " + e.getMessage());
            return Optional.empty();
        }

        // Schließt alle 200er-Status-Codes mit ein.
        if(response.statusCode() / 100 != 2) {
            System.err.println("Request failed with status code " + response.statusCode() + ": " + response.body());
            return Optional.empty();
        }

        return Optional.of(response);
    }

    // Ruft die obere Methode auf und deserialisiert zudem den Body mit einem Jackson-JsonMapper, um mühseliges JSON-Parsing zu vermeiden
    public static <T> Optional<T> sendAndDeserialize(HttpRequest request, Class<T> clazz) {
        Optional<HttpResponse<String>> optionalResponse = send(request);
        if(optionalResponse.isEmpty()) {
            return Optional.empty();
        }
        HttpResponse<String> response = optionalResponse.get();

        try {
            T t = JSON_MAPPER.readValue(response.body(), clazz);
            return Optional.of(t);
        } catch (IllegalArgumentException e) {
            System.err.println("Malformed json: " + e.getMessage());
        }
        return Optional.empty();
    }

    //Führt die Anfrage aus, um eine Nachricht beim Backend anzufragen und zu drucken
    public static void printMessage(long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/messages/" + id))
                .GET()
                .build();

        Optional<Message> m = sendAndDeserialize(request, Message.class);

        m.ifPresent(msg -> System.out.println("Message: " + msg.message()));
    }


}
