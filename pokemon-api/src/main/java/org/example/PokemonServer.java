package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PokemonServer {

    private static final int SERVER_PORT = 9090;

    public static void main(String[] args) {
        PokemonDataHandler dataHandler = new PokemonDataHandler("pokemons.json");
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Serwer Pokemonów uruchomiony. Oczekiwanie na połączenia...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nowe połączenie zaakceptowane.");

                PokedexClientHandler clientHandler = new PokedexClientHandler(clientSocket, dataHandler);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
