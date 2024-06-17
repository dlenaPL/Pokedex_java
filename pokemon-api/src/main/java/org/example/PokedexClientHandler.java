package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class PokedexClientHandler implements Runnable {

    private Socket clientSocket;
    private PokemonDataHandler dataHandler;

    public PokedexClientHandler(Socket clientSocket, PokemonDataHandler dataHandler) {
        this.clientSocket = clientSocket;
        this.dataHandler = dataHandler;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            // Odbieranie wiadomości powitalnej od klienta
            String initialMessage = in.readLine();
            if (initialMessage != null && initialMessage.equals("HELLO_SERVER")) {
                out.println("Witaj, kliencie! Połączenie zostało nawiązane.");
            } else {
                out.println("Niepoprawna wiadomość powitalna. Oczekiwano: HELLO_SERVER");
                return; // Zakończenie obsługi klienta w przypadku błędnej wiadomości powitalnej
            }

            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("Komenda od klienta: " + request);

                // Obsługa komendy GET_ALL
                if (request.equalsIgnoreCase("GET_ALL")) {
                    handleGetAll(out);

                    // Obsługa komendy SEARCH <name>
                } else if (request.startsWith("SEARCH")) {
                    handleSearch(request, out);

                    // Obsługa komendy EXIT
                } else if (request.equalsIgnoreCase("EXIT")) {
                    break;

                    // Obsługa nieznanej komendy
                } else {
                    out.println("Nieznana komenda: " + request);
                }
            }

            System.out.println("Zakończono obsługę klienta.");

        } catch (SocketException e) {
            System.out.println("Klient zakończył połączenie.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Metoda do obsługi komendy GET_ALL
    private void handleGetAll(PrintWriter out) {
        String allPokemonJson = dataHandler.getAllPokemonAsJson();
        out.println(allPokemonJson);
    }

    // Metoda do obsługi komendy SEARCH <name>
    private void handleSearch(String request, PrintWriter out) {
        String[] tokens = request.split("\\s+", 2); // Dzieli komendę na części
        if (tokens.length == 2) {
            String name = tokens[1].trim();
            String pokemonJson = dataHandler.searchPokemonByNameAsJson(name);
            out.println(pokemonJson);
        } else {
            out.println("Niepoprawna komenda SEARCH. Użyj: SEARCH <name>");
        }
    }
}
