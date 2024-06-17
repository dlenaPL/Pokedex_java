package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PokedexClient {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 9090;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Wysyłanie wiadomości powitalnej do serwera
            out.println("HELLO_SERVER");

            // Odbieranie odpowiedzi od serwera
            String response = in.readLine();
            if (response != null) {
                System.out.println("Odpowiedź od serwera: " + response);
            } else {
                System.out.println("Brak odpowiedzi od serwera.");
            }

            // Pętla do wysyłania komend
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput); // Wysyłanie komendy do serwera

                // Odbieranie odpowiedzi od serwera
                response = in.readLine();
                if (response != null) {
                    System.out.println("Odpowiedź od serwera: " + response);
                } else {
                    System.out.println("Brak odpowiedzi od serwera.");
                }

                // Sprawdzenie warunku wyjścia z pętli
                if (userInput.equalsIgnoreCase("EXIT")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
