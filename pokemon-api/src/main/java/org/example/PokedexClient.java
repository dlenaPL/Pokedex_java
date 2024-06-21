package org.example;

import javax.swing.*;

public class PokedexClient {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 9090;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PokedexClientGUI clientGUI = new PokedexClientGUI(SERVER_ADDRESS, SERVER_PORT);
            clientGUI.createAndShowGUI();
        });
    }
}