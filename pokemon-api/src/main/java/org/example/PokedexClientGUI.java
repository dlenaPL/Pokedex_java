package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PokedexClientGUI {

    private final String serverAddress;
    private final int serverPort;
    private PrintWriter out;
    private BufferedReader in;
    private JLabel statusLabel;
    private JTextArea responseArea;
    private JTable pokemonTable;
    private DefaultTableModel tableModel;
    private boolean isConnected = false;
    private Socket socket;

    private JComboBox<String> searchTypeCombo;
    private JLabel pokemonImageLabel;  // Pole do przechowywania etykiety obrazu
    private List<Pokemon> currentPokemonList = new ArrayList<>(); // Lista do przechowywania aktualnych Pokemonów
    private JTextField searchField; // Pole tekstowe dla nazwy Pokemona
    private JPanel imgContainer = new RoundedPanel(15);
    private JLabel pokemonNameLabel;
    private RoundedPanel pokemonDescriptionPanel;
    private RoundedLabel typeLabelA;
    private RoundedLabel typeLabelB;

    private RoundedPanel statsPanel = new RoundedPanel(15);
    private JLabel statsHeader;
    private ProgressLabel hpLabel;
    private ProgressLabel attackLabel;
    private ProgressLabel defenseLabel;
    private ProgressLabel spAtkLabel;
    private ProgressLabel spDefLabel;
    private ProgressLabel speedLabel;


    public PokedexClientGUI(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Pokedex Client");
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel window = new JPanel();
        window.setLayout(null);
        window.setSize(800,600);
        window.setBounds(0,0,800,600);
        window.setBackground(Color.decode("#beaed4"));
        frame.add(window);

        statusLabel = new JLabel("Nie połączono z serwerem", SwingConstants.CENTER);
        statusLabel.setSize(new Dimension(200,20));
        statusLabel.setBounds(300,0,200,20);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(Color.red);
        window.add(statusLabel);

        responseArea = new JTextArea();
        responseArea.setEditable(false);
        responseArea.setSize(new Dimension(700,96));
        responseArea.setBounds(50,460,700,96);

        window.add(responseArea);

        JScrollPane responseAreaScrollPane = new JScrollPane(responseArea);

        responseAreaScrollPane.setPreferredSize(new Dimension(700, 96));

        window.add(responseAreaScrollPane);

        responseAreaScrollPane.setBounds(50, 460, 700, 96);

        tableModel = new DefaultTableModel(new Object[]{"Nazwa", "Typy", "HP","ATK", "DEF", "Sp.ATK", "Sp.DEF", "Speed"}, 0){
            //zakaz edycji wierszy
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
            //zmiana sposobu traktowania kolumn na Integer, zeby sortowanie poprawnie dzialalo
            @Override
            public Class<?> getColumnClass(int columnIndex){

                if(columnIndex == 2){
                    return Integer.class;
                }
                if(columnIndex == 3){
                    return Integer.class;
                }
                if(columnIndex == 4){
                    return Integer.class;
                }
                if(columnIndex == 5){
                    return Integer.class;
                }
                if(columnIndex == 6){
                    return Integer.class;
                }
                if(columnIndex == 7){
                    return Integer.class;
                }
                return String.class;
            }
        };

        pokemonTable = new JTable(tableModel);
        pokemonTable.setSize(new Dimension(500,400));
        pokemonTable.setBounds(20,50,500,400);
        //zakaz przesuwania kolumn miedzy soba
        pokemonTable.getTableHeader().setReorderingAllowed(false);

        //sortowanie kolumn
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(pokemonTable.getModel());
        pokemonTable.setRowSorter(sorter);


        window.add(pokemonTable);
        pokemonTable.setBackground(Color.white);

        JScrollPane scrollPane = new JScrollPane(pokemonTable);

        scrollPane.setPreferredSize(new Dimension(500, 400));

        window.add(scrollPane);

        scrollPane.setBounds(20, 50, 500, 400);

        pokemonDescriptionPanel = new RoundedPanel(15);
        pokemonDescriptionPanel.setLayout(null);

        imgContainer.setLayout(null);
        imgContainer.setSize(220,120);

        imgContainer.setBounds(0,0,220,120);
        imgContainer.setBackground(Color.white);

        pokemonNameLabel = new JLabel("", SwingConstants.CENTER);
        pokemonNameLabel.setLayout(null);
        pokemonNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        pokemonNameLabel.setSize(160,30);
        pokemonNameLabel.setBounds(30,120,160,30);

        pokemonImageLabel = new JLabel();
        pokemonImageLabel.setSize(new Dimension(120,120));
        pokemonImageLabel.setBounds(50,0,120,120);

        imgContainer.add(pokemonImageLabel);

        typeLabelA = new RoundedLabel("", 10);
        typeLabelA.setSize(new Dimension(60,20));
        typeLabelA.setHorizontalAlignment(SwingConstants.CENTER);
        typeLabelA.setVisible(false);
        typeLabelB = new RoundedLabel("", 10);
        typeLabelB.setSize(new Dimension(60,20));
        typeLabelB.setHorizontalAlignment(SwingConstants.CENTER);
        typeLabelB.setVisible(false);

        statsPanel.setLayout(null);
        statsPanel.setSize(new Dimension(220,200));
        statsPanel.setBounds(0,180,220,200);
        statsPanel.setBackground(Color.white);
        statsPanel.setVisible(false);

        statsHeader = new JLabel("Performance", SwingConstants.CENTER);
        statsHeader.setSize(new Dimension(200,20));
        statsHeader.setFont(new Font("Arial", Font.BOLD, 16));
        statsHeader.setBounds(10,10,200,20);
        statsPanel.add(statsHeader);

        int statLabelWidth = 160;
        int statLabelHeight = 20;

        JLabel hpDesc = new JLabel("HP", SwingConstants.CENTER);
        hpDesc.setSize(new Dimension(40,20));
        hpDesc.setBounds(5,35,40,20);
        statsPanel.add(hpDesc);

        hpLabel = new ProgressLabel("", 0, Color.decode("#7fc97f"));
        hpLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hpLabel.setSize(new Dimension(statLabelWidth,statLabelHeight));
        hpLabel.setBounds(55,35,statLabelWidth,statLabelHeight);

        statsPanel.add(hpLabel);

        JLabel atkDesc = new JLabel("ATK", SwingConstants.CENTER);
        atkDesc.setSize(new Dimension(40,20));
        atkDesc.setBounds(5, 60,40,20);
        statsPanel.add(atkDesc);

        attackLabel = new ProgressLabel("", 0, Color.decode("#beaed4"));
        attackLabel.setHorizontalAlignment(SwingConstants.CENTER);
        attackLabel.setSize(new Dimension(statLabelWidth,statLabelHeight));
        attackLabel.setBounds(55,60,statLabelWidth,statLabelHeight);
        statsPanel.add(attackLabel);

        JLabel defDesc = new JLabel("DEF", SwingConstants.CENTER);
        defDesc.setSize(new Dimension(40,20));
        defDesc.setBounds(5, 85,40,20);
        statsPanel.add(defDesc);

        defenseLabel = new ProgressLabel("", 0, Color.decode("#fdc086"));
        defenseLabel.setHorizontalAlignment(SwingConstants.CENTER);
        defenseLabel.setSize(new Dimension(statLabelWidth,statLabelHeight));
        defenseLabel.setBounds(55,85,statLabelWidth,statLabelHeight);
        statsPanel.add(defenseLabel);

        JLabel spAtkDesc = new JLabel("SpATK", SwingConstants.CENTER);
        spAtkDesc.setSize(new Dimension(40,20));
        spAtkDesc.setBounds(5, 110,40,20);
        statsPanel.add(spAtkDesc);

        spAtkLabel = new ProgressLabel("", 0, Color.decode("#ffff99"));
        spAtkLabel.setHorizontalAlignment(SwingConstants.CENTER);
        spAtkLabel.setSize(new Dimension(statLabelWidth,statLabelHeight));
        spAtkLabel.setBounds(55,110,statLabelWidth,statLabelHeight);
        statsPanel.add(spAtkLabel);

        JLabel spDefDesc = new JLabel("SpDEF", SwingConstants.CENTER);
        spDefDesc.setSize(new Dimension(40,20));
        spDefDesc.setBounds(5, 135,40,20);
        statsPanel.add(spDefDesc);

        spDefLabel = new ProgressLabel("", 0, Color.decode("#386cb0"));
        spDefLabel.setHorizontalAlignment(SwingConstants.CENTER);
        spDefLabel.setSize(new Dimension(statLabelWidth,statLabelHeight));
        spDefLabel.setBounds(55,135,statLabelWidth,statLabelHeight);
        statsPanel.add(spDefLabel);

        JLabel speedDesc = new JLabel("SPEED", SwingConstants.CENTER);
        speedDesc.setSize(new Dimension(40,20));
        speedDesc.setBounds(5, 160,40,20);
        statsPanel.add(speedDesc);

        speedLabel = new ProgressLabel("", 0, Color.decode("#f0027f"));
        speedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        speedLabel.setSize(new Dimension(statLabelWidth,statLabelHeight));
        speedLabel.setBounds(55,160,statLabelWidth,statLabelHeight);
        statsPanel.add(speedLabel);

        pokemonDescriptionPanel.add(imgContainer);
        pokemonDescriptionPanel.add(pokemonNameLabel);
        pokemonDescriptionPanel.add(statsPanel);

        pokemonDescriptionPanel.setSize(new Dimension(220,400));
        pokemonDescriptionPanel.setBounds(540,50,220,400);
        pokemonDescriptionPanel.setBackground(Color.white);

        window.add(pokemonDescriptionPanel);

        pokemonTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = pokemonTable.getSelectedRow();
                    if (selectedRow != -1) {
                        int modelRow = pokemonTable.convertRowIndexToModel(selectedRow);
                        String pokemonName = (String) tableModel.getValueAt(modelRow, 0);
                        displayPokemonImage(pokemonName);
                    }
                }
            }
        });

        JPanel panel = new JPanel(null);
        panel.setBackground(Color.decode("#beaed4"));

        searchField = new JTextField(); // Pole tekstowe dla nazwy Pokemona
        searchField.setSize(new Dimension(480,20));
        searchField.setBounds(10,0,480,20);
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(searchTypeCombo.getSelectedItem() == "Nazwa"){
                    searchPokemonByName();
                }else if(searchTypeCombo.getSelectedItem() == "Typ"){
                    searchPokemonByType();
                }

            }
        });

        String[] searchOptions = new String[] {"Nazwa", "Typ"};
        searchTypeCombo = new JComboBox<>(searchOptions);
        searchTypeCombo.setSize(new Dimension(100,20));
        searchTypeCombo.setBounds(500,0,100,20);
        searchTypeCombo.setEditable(false);
        searchTypeCombo.setSelectedIndex(0);

        JButton searchButton = new JButton("Szukaj"); // Przycisk "Szukaj"
        searchButton.setSize(new Dimension(80,20));
        searchButton.setBounds(610,0,80,20);

        panel.add(searchField);
        panel.add(searchTypeCombo);
        panel.add(searchButton);

        panel.setSize(new Dimension(700,20));
        panel.setBounds(50,20,700,20);
        window.add(panel);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(searchTypeCombo.getSelectedItem() == "Nazwa"){
                    searchPokemonByName();
                }else if(searchTypeCombo.getSelectedItem() == "Typ"){
                    searchPokemonByType();
                }
            }
        });

        frame.setVisible(true);

        connectToServer();
    }

    private void searchPokemonByName() {
        String pokemonName = searchField.getText().trim();
        if (!pokemonName.isEmpty()) {
            sendCommand("SEARCH: " + pokemonName);
        } else {
            sendCommand("GET_ALL");
        }
    }

    private void searchPokemonByType(){
        String pokemonType = searchField.getText().trim();
        if (!pokemonType.isEmpty()) {
            sendCommand("SEARCH_TYPE_NAME: " + pokemonType);
        } else {
            sendCommand("GET_ALL");
        }
    }

    private void sendCommand(String command) {
        if (isConnected && out != null && !command.isEmpty()) {
            out.println(command);
        } else {
            responseArea.append("Brak połączenia z serwerem.\n");
        }
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
                socket = new Socket(serverAddress, serverPort);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println("HELLO_SERVER");

                String response = in.readLine();
                if (response != null && response.equals("Witaj, kliencie! Połączenie zostało nawiązane.")) {
                    isConnected = true;
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Połączono z serwerem");
                        statusLabel.setOpaque(true);
                        statusLabel.setBackground(Color.green);
                        responseArea.append("Połączono z serwerem.\n");
                        requestAllPokemons();
                    });
                } else {

                    closeConnection();
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Nie udało się połączyć z serwerem");


                    });
                }

                String serverResponse;
                while ((serverResponse = in.readLine()) != null) {
                    String finalServerResponse = serverResponse;
                    SwingUtilities.invokeLater(() -> handleServerResponse(finalServerResponse));
                }

            } catch (IOException e) {
                closeConnection();
                SwingUtilities.invokeLater(() -> statusLabel.setText("Nie udało się połączyć z serwerem"));
            }
        }).start();
    }

    private void handleServerResponse(String response) {
        String[] split = response.split(":");

        responseArea.append(split[0] + " command used.\n");

        if (response.startsWith("get_all:")) {
            String jsonData = response.substring("get_all:".length());
            Gson gson = new Gson();
            Type pokemonListType = new TypeToken<ArrayList<Pokemon>>() {}.getType();
            List<Pokemon> pokemons = gson.fromJson(jsonData, pokemonListType);
            if(pokemons != null){
                updatePokemonTable(pokemons);
            }else {
                responseArea.append("Pokemon not found or doesn't exist");
            }

        }
        if (response.startsWith("SEARCH_TYPE_NAME:")) {
            String jsonData = response.substring("SEARCH_TYPE_NAME:".length());
            Gson gson = new Gson();
            Type pokemonListType = new TypeToken<ArrayList<Pokemon>>() {}.getType();
            List<Pokemon> pokemons = gson.fromJson(jsonData, pokemonListType);
            if(pokemons != null){
                updatePokemonTable(pokemons);
            }else {
                responseArea.append("Pokemon not found or doesn't exist");
            }
        }

        if (response.startsWith("SEARCH:")) {
            String jsonData = response.substring("SEARCH:".length());
            Gson gson = new Gson();
            Type pokemonListType = new TypeToken<ArrayList<Pokemon>>() {}.getType();
            List<Pokemon> pokemons = gson.fromJson(jsonData, pokemonListType);
            if(pokemons != null){
                updatePokemonTable(pokemons);
            } else {
                responseArea.append("Pokemon not found or doesn't exist");
            }
        }
    }

    private void updatePokemonTable(List<Pokemon> pokemons) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0); // Wyczyść istniejące wiersze
            currentPokemonList = pokemons; // Aktualizuj bieżącą listę Pokemonów
            for (Pokemon pokemon : pokemons) {
                tableModel.addRow(new Object[]{
                        pokemon.getName(),
                        pokemon.getTypes().toString(),
                        pokemon.getHp(),
                        pokemon.getAttack(),
                        pokemon.getDefense(),
                        pokemon.getSpecialAttack(),
                        pokemon.getSpecialDefense(),
                        pokemon.getSpeed()});
            }
        });
    }

    private void displayPokemonImage(String pokemonName) {
        for (Pokemon pokemon : currentPokemonList) {
            if (pokemon.getName().equals(pokemonName)) {
                BufferedImage pokemonMini;
                try {
                    URL imageUrl = new URL(pokemon.getPokeImageSrc());
                    pokemonMini = ImageIO.read(imageUrl);
                    Image image = pokemonMini.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                    pokemonImageLabel.setIcon(new ImageIcon(image));
                    pokemonNameLabel.setText(pokemon.getName());  // Usuń tekst, jeśli istnieje
                    imgContainer.setBackground(Color.decode(pokemon.getTypes().get(0).getLabel()));

                    displayPokemonTypeLabel(pokemon, typeLabelA, typeLabelB);
                    showStats(pokemon);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
    }

    private void displayPokemonTypeLabel(Pokemon pokemon, RoundedLabel label1, RoundedLabel label2){

        if(pokemon.getTypes().size() == 1){
            label2.setVisible(false);
            label1.setBackground(Color.decode(pokemon.getTypes().get(0).getLabel()));
            label1.setText(pokemon.getTypes().get(0).name());
            label1.setBounds(80,155,60,20);
            pokemonDescriptionPanel.add(label1);
            label1.setVisible(true);

        } else if (pokemon.getTypes().size() == 2) {
            label1.setBackground(Color.decode(pokemon.getTypes().get(0).getLabel()));
            label1.setText(pokemon.getTypes().get(0).name());
            label1.setBounds(30,155,60,20);
            pokemonDescriptionPanel.add(label1);
            label2.setVisible(true);
            label2.setBackground(Color.decode(pokemon.getTypes().get(1).getLabel()));
            label2.setText(pokemon.getTypes().get(1).name());
            label2.setBounds(130,155,60,20);
            pokemonDescriptionPanel.add(label2);
            label2.setVisible(true);
        }
    }

    private void showStats(Pokemon pokemon){
        statsPanel.setVisible(true);

        hpLabel.setFillWidth((int) (pokemon.getHp() * ((double) pokemon.getHp() /160)));
        hpLabel.setText(pokemon.getHp().toString() + "/ 160");

        attackLabel.setFillWidth((int) (pokemon.getAttack() * ((double) pokemon.getAttack() /160)));
        attackLabel.setText(pokemon.getAttack().toString() + "/ 160");

        defenseLabel.setFillWidth((int) (pokemon.getDefense() * ((double) pokemon.getDefense() /160)));
        defenseLabel.setText(pokemon.getDefense().toString() + "/ 160");

        spAtkLabel.setFillWidth((int) (pokemon.getSpecialAttack() * ((double) pokemon.getSpecialAttack() /160)));
        spAtkLabel.setText(pokemon.getSpecialAttack().toString() + "/ 160");

        spDefLabel.setFillWidth((int) (pokemon.getSpecialDefense() * ((double) pokemon.getSpecialDefense() /160)));
        spDefLabel.setText(pokemon.getSpecialDefense().toString() + "/ 160");

        speedLabel.setFillWidth((int) (pokemon.getSpeed() * ((double) pokemon.getSpeed() /160)));
        speedLabel.setText(pokemon.getSpeed().toString() + "/ 160");
    }

    private void requestAllPokemons() {
        if (isConnected && out != null) {
            out.println("get_all");
        }
    }

    private void closeConnection() {
        isConnected = false;
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PokedexClientGUI gui = new PokedexClientGUI("localhost", 9090); // Ustaw adres i port serwera
            gui.createAndShowGUI();
        });
    }
}
