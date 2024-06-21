package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

                } else if (request.startsWith("SEARCH_TYPE_NAME")) {
                    handleSearchByTypeName(request, out);

                    // Obsługa komendy SEARCH <name>
                } else if (request.startsWith("SEARCH")) {
                    handleSearchByName(request, out);

                    // Obsługa komendy EXIT
                } else if (request.startsWith("EXIT")) {
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
    public void handleGetAll(PrintWriter out) {
        Gson gson = new Gson(); // Użyj prostszego Gson bez ustawiania pretty printing
        String json = "get_all:" + gson.toJson(dataHandler.getAllPokemons());
        System.out.println("------------------pokedex client handler handle get all");
        out.println(json);
    }


    public void handleSearchByName(String request, PrintWriter out) {
        String[] tokens = request.split("\\s+", 2); // Dzieli komendę na części
        Gson gson = new Gson();
        if (tokens.length == 2) {
            String nameFragment = tokens[1].trim();
            String json = "SEARCH:" + gson.toJson(dataHandler.searchPokemonByName(nameFragment));

            out.println(json);
        } else {
            out.println("Niepoprawna komenda SEARCH. Użyj: SEARCH <name>");
        }
    }

    public void handleSearchByTypeName(String request, PrintWriter out) {

        String[] tokens = request.split("\\s+", 2); // Dzieli komendę na części
        Gson gson = new Gson();
        if (tokens.length == 2) {
            String typeName = tokens[1].trim();
            String foundPokemons = "SEARCH_TYPE_NAME:" + gson.toJson(dataHandler.searchPokemonByTypeName(typeName));
            out.println(foundPokemons);
        } else {
            out.println("Niepoprawna komenda SEARCH_TYPE_NAME. Użyj: SEARCH_TYPE_NAME <typeName>");
        }
    }





    //koniec klasy
}
