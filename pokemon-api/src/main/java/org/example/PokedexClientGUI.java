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

    private JPanel searchPanel;
    private JComboBox<String> searchTypeCombo;
    private JLabel pokemonImageLabel;
    private List<Pokemon> currentPokemonList = new ArrayList<>();
    private JTextField searchField;
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

    private final int WINDOW_WIDTH = 1000;
    private final int WINDOW_HEIGHT = 800;
    private final int STATUS_LABEL_WIDTH = 200;
    private final int LABEL_HEIGHT = 20;
    private final int MARGIN = 10;
    private final int RESPONSE_AREA_WIDTH = (int) (WINDOW_WIDTH * 0.8);
    private final int RESPONSE_AREA_HEIGHT = 5*LABEL_HEIGHT;
    private final int TABLE_WIDTH = (int) (WINDOW_WIDTH*0.6);
    private final int TABLE_HEIGHT = (int) (WINDOW_HEIGHT*0.7);
    private final int SEARCH_PANEL_WIDTH = (int) (WINDOW_WIDTH*0.8);
    private final int POKEMON_PANEL_WIDTH = (int) (WINDOW_WIDTH*0.3);
    private final int POKEMON_PANEL_HEIGHT = (int) (WINDOW_HEIGHT*0.7);
    private final int IMAGE_SIZE = 120;
    private final int TYPE_LABEL_WIDTH = 80;
    private final int STATS_PANEL_WIDTH = POKEMON_PANEL_WIDTH;
    private final int STATS_PANEL_HEIGHT = (int) (0.6*POKEMON_PANEL_HEIGHT);
    private final int STAT_LABEL_WIDTH = (int) (STATS_PANEL_WIDTH*0.7);
    private final int STAT_DESC_LABEL_WIDTH = 40;


    public PokedexClientGUI(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Pokedex Client");
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel window = new JPanel();
        window.setLayout(null);
        window.setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
        window.setBounds(0,0,WINDOW_WIDTH,WINDOW_HEIGHT);
        window.setBackground(Color.decode("#beaed4"));
        frame.add(window);

        statusLabel = new JLabel("Nie połączono z serwerem", SwingConstants.CENTER);
        statusLabel.setSize(new Dimension(STATUS_LABEL_WIDTH,LABEL_HEIGHT));
        statusLabel.setBounds((int) (WINDOW_WIDTH*0.5 - 0.5*STATUS_LABEL_WIDTH),MARGIN,STATUS_LABEL_WIDTH,LABEL_HEIGHT);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(Color.red);
        window.add(statusLabel);

        responseArea = new JTextArea();
        responseArea.setEditable(false);
        responseArea.setSize(new Dimension(RESPONSE_AREA_WIDTH, RESPONSE_AREA_HEIGHT));
        System.out.println(WINDOW_HEIGHT - RESPONSE_AREA_HEIGHT - MARGIN);
        responseArea.setBounds((int) (WINDOW_WIDTH*0.5 - RESPONSE_AREA_WIDTH*0.5), (int) (WINDOW_HEIGHT - 1.5*RESPONSE_AREA_HEIGHT - MARGIN),RESPONSE_AREA_WIDTH,RESPONSE_AREA_HEIGHT);

        window.add(responseArea);

        JScrollPane responseAreaScrollPane = new JScrollPane(responseArea);

        responseAreaScrollPane.setPreferredSize(new Dimension(RESPONSE_AREA_WIDTH, RESPONSE_AREA_HEIGHT));

        window.add(responseAreaScrollPane);

        responseAreaScrollPane.setBounds((int) (WINDOW_WIDTH*0.5 - RESPONSE_AREA_WIDTH*0.5), (int) (WINDOW_HEIGHT - 1.5*RESPONSE_AREA_HEIGHT - MARGIN), RESPONSE_AREA_WIDTH, RESPONSE_AREA_HEIGHT);


        searchPanel = new JPanel(null);
        searchPanel.setBackground(Color.decode("#beaed4"));
        searchPanel.setSize(new Dimension(SEARCH_PANEL_WIDTH,LABEL_HEIGHT));
        System.out.println("search panel: " + SEARCH_PANEL_WIDTH);
        searchPanel.setBounds((int) (0.5*WINDOW_WIDTH - 0.5*SEARCH_PANEL_WIDTH),statusLabel.getY()+statusLabel.getHeight()+MARGIN,SEARCH_PANEL_WIDTH,LABEL_HEIGHT);

        searchField = new JTextField();
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

        JButton searchButton = new JButton("Szukaj");
        searchButton.setSize(new Dimension(80,20));
        searchButton.setBounds(610,0,80,20);

        searchPanel.add(searchField);
        searchPanel.add(searchTypeCombo);
        searchPanel.add(searchButton);


        window.add(searchPanel);

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
        pokemonTable.setSize(new Dimension(TABLE_WIDTH,TABLE_HEIGHT));
        pokemonTable.setBounds(20,searchPanel.getY()+searchPanel.getHeight()+MARGIN,TABLE_WIDTH,TABLE_HEIGHT);
        //zakaz przesuwania kolumn miedzy soba
        pokemonTable.getTableHeader().setReorderingAllowed(false);

        //sortowanie kolumn
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(pokemonTable.getModel());
        pokemonTable.setRowSorter(sorter);


        window.add(pokemonTable);
        pokemonTable.setBackground(Color.white);

        JScrollPane scrollPane = new JScrollPane(pokemonTable);

        scrollPane.setPreferredSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));

        window.add(scrollPane);

        scrollPane.setBounds(20, searchPanel.getY()+searchPanel.getHeight()+MARGIN, TABLE_WIDTH, TABLE_HEIGHT);

        pokemonDescriptionPanel = new RoundedPanel(15);
        pokemonDescriptionPanel.setLayout(null);
        pokemonDescriptionPanel.setSize(new Dimension(POKEMON_PANEL_WIDTH,POKEMON_PANEL_HEIGHT));
        pokemonDescriptionPanel.setBounds(
                WINDOW_WIDTH-POKEMON_PANEL_WIDTH-50,
                searchPanel.getY()+searchPanel.getHeight()+MARGIN,
                POKEMON_PANEL_WIDTH,
                POKEMON_PANEL_HEIGHT);

        pokemonDescriptionPanel.setBackground(Color.white);

        imgContainer.setLayout(null);
        imgContainer.setSize(POKEMON_PANEL_WIDTH, (int) (POKEMON_PANEL_HEIGHT*0.3));

        imgContainer.setBounds(0,0,POKEMON_PANEL_WIDTH, (int) (POKEMON_PANEL_HEIGHT*0.3));
        imgContainer.setBackground(Color.white);

        pokemonNameLabel = new JLabel("", SwingConstants.CENTER);
        pokemonNameLabel.setLayout(null);
        pokemonNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        pokemonNameLabel.setSize(160,LABEL_HEIGHT);
        pokemonNameLabel.setBounds((int) (pokemonDescriptionPanel.getWidth()*0.5 - pokemonNameLabel.getWidth()*0.5), (int) (POKEMON_PANEL_HEIGHT*0.3 + MARGIN),160,LABEL_HEIGHT);

        pokemonImageLabel = new JLabel();
        pokemonImageLabel.setSize(new Dimension(IMAGE_SIZE, IMAGE_SIZE));
        pokemonImageLabel.setBounds((int) (imgContainer.getWidth()*0.5 - IMAGE_SIZE*0.5), (int) (imgContainer.getHeight()*0.5 - IMAGE_SIZE*0.5),IMAGE_SIZE,IMAGE_SIZE);

        imgContainer.add(pokemonImageLabel);

        typeLabelA = new RoundedLabel("", 10);
        typeLabelA.setSize(new Dimension(TYPE_LABEL_WIDTH,LABEL_HEIGHT));
        typeLabelA.setHorizontalAlignment(SwingConstants.CENTER);
        typeLabelA.setVisible(false);
        typeLabelB = new RoundedLabel("", 10);
        typeLabelB.setSize(new Dimension(TYPE_LABEL_WIDTH,LABEL_HEIGHT));
        typeLabelB.setHorizontalAlignment(SwingConstants.CENTER);
        typeLabelB.setVisible(false);

        statsPanel.setLayout(null);
        statsPanel.setSize(new Dimension(STATS_PANEL_WIDTH,STATS_PANEL_HEIGHT));
        statsPanel.setBounds(0, (int) (STATS_PANEL_HEIGHT*0.7),STATS_PANEL_WIDTH,STATS_PANEL_HEIGHT);
        statsPanel.setBackground(Color.white);
        statsPanel.setVisible(false);

        statsHeader = new JLabel("Performance", SwingConstants.CENTER);
        statsHeader.setSize(new Dimension(200,LABEL_HEIGHT));
        statsHeader.setFont(new Font("Arial", Font.BOLD, 16));

        statsHeader.setBounds((int) (STATS_PANEL_WIDTH*0.5 - statsHeader.getWidth()*0.5),MARGIN,200,LABEL_HEIGHT);
        statsPanel.add(statsHeader);

        JLabel hpDesc = new JLabel("HP", SwingConstants.CENTER);
        hpDesc.setSize(new Dimension(STAT_DESC_LABEL_WIDTH,LABEL_HEIGHT));
        hpDesc.setBounds(MARGIN,statsHeader.getY()+LABEL_HEIGHT+MARGIN,STAT_DESC_LABEL_WIDTH,LABEL_HEIGHT);
        statsPanel.add(hpDesc);

        hpLabel = new ProgressLabel("", 0, Color.decode("#7fc97f"));
        hpLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hpLabel.setSize(new Dimension(STAT_LABEL_WIDTH,LABEL_HEIGHT));
        hpLabel.setBounds(STAT_DESC_LABEL_WIDTH + MARGIN+ MARGIN,statsHeader.getY()+LABEL_HEIGHT+MARGIN,STAT_LABEL_WIDTH,LABEL_HEIGHT);

        statsPanel.add(hpLabel);

        JLabel atkDesc = new JLabel("ATK", SwingConstants.CENTER);
        atkDesc.setSize(new Dimension(STAT_DESC_LABEL_WIDTH,LABEL_HEIGHT));
        atkDesc.setBounds(MARGIN, hpLabel.getY()+LABEL_HEIGHT+MARGIN,STAT_DESC_LABEL_WIDTH,LABEL_HEIGHT);
        statsPanel.add(atkDesc);

        attackLabel = new ProgressLabel("", 0, Color.decode("#beaed4"));
        attackLabel.setHorizontalAlignment(SwingConstants.CENTER);
        attackLabel.setSize(new Dimension(STAT_LABEL_WIDTH,LABEL_HEIGHT));
        attackLabel.setBounds(STAT_DESC_LABEL_WIDTH + MARGIN+ MARGIN,hpLabel.getY()+LABEL_HEIGHT+MARGIN,STAT_LABEL_WIDTH,LABEL_HEIGHT);
        statsPanel.add(attackLabel);

        JLabel defDesc = new JLabel("DEF", SwingConstants.CENTER);
        defDesc.setSize(new Dimension(STAT_DESC_LABEL_WIDTH,LABEL_HEIGHT));
        defDesc.setBounds(MARGIN, attackLabel.getY()+LABEL_HEIGHT+MARGIN,STAT_DESC_LABEL_WIDTH,LABEL_HEIGHT);
        statsPanel.add(defDesc);

        defenseLabel = new ProgressLabel("", 0, Color.decode("#fdc086"));
        defenseLabel.setHorizontalAlignment(SwingConstants.CENTER);
        defenseLabel.setSize(new Dimension(STAT_LABEL_WIDTH,LABEL_HEIGHT));
        defenseLabel.setBounds(STAT_DESC_LABEL_WIDTH+ MARGIN + MARGIN,attackLabel.getY()+LABEL_HEIGHT+MARGIN,STAT_LABEL_WIDTH,LABEL_HEIGHT);
        statsPanel.add(defenseLabel);

        JLabel spAtkDesc = new JLabel("SpATK", SwingConstants.CENTER);
        spAtkDesc.setSize(new Dimension(STAT_DESC_LABEL_WIDTH,LABEL_HEIGHT));
        spAtkDesc.setBounds(MARGIN, defenseLabel.getY()+LABEL_HEIGHT+MARGIN,STAT_DESC_LABEL_WIDTH,LABEL_HEIGHT);
        statsPanel.add(spAtkDesc);

        spAtkLabel = new ProgressLabel("", 0, Color.decode("#ffff99"));
        spAtkLabel.setHorizontalAlignment(SwingConstants.CENTER);
        spAtkLabel.setSize(new Dimension(STAT_LABEL_WIDTH,LABEL_HEIGHT));
        spAtkLabel.setBounds(STAT_DESC_LABEL_WIDTH + MARGIN+ MARGIN,defenseLabel.getY()+LABEL_HEIGHT+MARGIN,STAT_LABEL_WIDTH,LABEL_HEIGHT);
        statsPanel.add(spAtkLabel);

        JLabel spDefDesc = new JLabel("SpDEF", SwingConstants.CENTER);
        spDefDesc.setSize(new Dimension(STAT_DESC_LABEL_WIDTH,LABEL_HEIGHT));
        spDefDesc.setBounds(MARGIN, spAtkLabel.getY()+LABEL_HEIGHT+MARGIN,STAT_DESC_LABEL_WIDTH,LABEL_HEIGHT);
        statsPanel.add(spDefDesc);

        spDefLabel = new ProgressLabel("", 0, Color.decode("#386cb0"));
        spDefLabel.setHorizontalAlignment(SwingConstants.CENTER);
        spDefLabel.setSize(new Dimension(STAT_LABEL_WIDTH,LABEL_HEIGHT));
        spDefLabel.setBounds(STAT_DESC_LABEL_WIDTH + MARGIN+ MARGIN,spAtkLabel.getY()+LABEL_HEIGHT+MARGIN,STAT_LABEL_WIDTH,LABEL_HEIGHT);
        statsPanel.add(spDefLabel);

        JLabel speedDesc = new JLabel("SPEED", SwingConstants.CENTER);
        speedDesc.setSize(new Dimension(STAT_DESC_LABEL_WIDTH,LABEL_HEIGHT));
        speedDesc.setBounds(MARGIN, spDefLabel.getY()+LABEL_HEIGHT+MARGIN,STAT_DESC_LABEL_WIDTH,LABEL_HEIGHT);
        statsPanel.add(speedDesc);

        speedLabel = new ProgressLabel("", 0, Color.decode("#f0027f"));
        speedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        speedLabel.setSize(new Dimension(STAT_LABEL_WIDTH,LABEL_HEIGHT));
        speedLabel.setBounds(STAT_DESC_LABEL_WIDTH + MARGIN+ MARGIN,spDefLabel.getY()+LABEL_HEIGHT+MARGIN,STAT_LABEL_WIDTH,LABEL_HEIGHT);
        statsPanel.add(speedLabel);

        pokemonDescriptionPanel.add(imgContainer);
        pokemonDescriptionPanel.add(pokemonNameLabel);
        pokemonDescriptionPanel.add(statsPanel);

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
                responseArea.append("Pokemon not found or doesn't exist\n");
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
                responseArea.append("Pokemon not found or doesn't exist\n");
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
                responseArea.append("Pokemon not found or doesn't exist\n");
            }
        }
    }

    private void updatePokemonTable(List<Pokemon> pokemons) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            currentPokemonList = pokemons;
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
                    Image image = pokemonMini.getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH);
                    pokemonImageLabel.setIcon(new ImageIcon(image));
                    pokemonNameLabel.setText(pokemon.getName());
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
            label1.setBounds(
                    (int) (POKEMON_PANEL_WIDTH*0.5-TYPE_LABEL_WIDTH*0.5),
                    pokemonNameLabel.getY()+pokemonNameLabel.getHeight() + MARGIN,
                    TYPE_LABEL_WIDTH,
                    LABEL_HEIGHT);
            pokemonDescriptionPanel.add(label1);
            label1.setVisible(true);

        } else if (pokemon.getTypes().size() == 2) {
            label1.setBackground(Color.decode(pokemon.getTypes().get(0).getLabel()));
            label1.setText(pokemon.getTypes().get(0).name());
            label1.setBounds(30,pokemonNameLabel.getY()+pokemonNameLabel.getHeight() + MARGIN,TYPE_LABEL_WIDTH,LABEL_HEIGHT);
            pokemonDescriptionPanel.add(label1);
            label2.setVisible(true);
            label2.setBackground(Color.decode(pokemon.getTypes().get(1).getLabel()));
            label2.setText(pokemon.getTypes().get(1).name());
            label2.setBounds(POKEMON_PANEL_WIDTH -TYPE_LABEL_WIDTH - 30,pokemonNameLabel.getY()+pokemonNameLabel.getHeight() + MARGIN,TYPE_LABEL_WIDTH,LABEL_HEIGHT);
            pokemonDescriptionPanel.add(label2);
            label2.setVisible(true);
        }
    }

    private void showStats(Pokemon pokemon){
        statsPanel.setVisible(true);

        hpLabel.setFillWidth((int) (STAT_LABEL_WIDTH * ((double) pokemon.getHp() /160)));
        hpLabel.setText(pokemon.getHp().toString() + "/ 160");

        attackLabel.setFillWidth((int) (STAT_LABEL_WIDTH* ((double) pokemon.getAttack() /160)));
        attackLabel.setText(pokemon.getAttack().toString() + "/ 160");

        defenseLabel.setFillWidth((int) (STAT_LABEL_WIDTH * ((double) pokemon.getDefense() /160)));
        defenseLabel.setText(pokemon.getDefense().toString() + "/ 160");

        spAtkLabel.setFillWidth((int) (STAT_LABEL_WIDTH * ((double) pokemon.getSpecialAttack() /160)));
        spAtkLabel.setText(pokemon.getSpecialAttack().toString() + "/ 160");

        spDefLabel.setFillWidth((int) (STAT_LABEL_WIDTH * ((double) pokemon.getSpecialDefense() /160)));
        spDefLabel.setText(pokemon.getSpecialDefense().toString() + "/ 160");

        speedLabel.setFillWidth((int) (STAT_LABEL_WIDTH * ((double) pokemon.getSpeed() /STAT_LABEL_WIDTH)));
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
            PokedexClientGUI gui = new PokedexClientGUI("localhost", 9090);
            gui.createAndShowGUI();
        });
    }
}