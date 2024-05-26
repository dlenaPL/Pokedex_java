package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class App implements Runnable {

    private List<Pokemon> pokemons;

    public App(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }

    @Override
    public void run() {

        JFrame window = new JFrame("Pokedex");
        window.setSize(800,600);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

//        JPanel leftPanel = new JPanel();
//        BoxLayout leftBoxLayoutManager = new BoxLayout(leftPanel, BoxLayout.Y_AXIS);
//        leftPanel.setLayout(leftBoxLayoutManager);
        // wez ten label z nazwa pokemona i zrob go oddzielnie
        //co tam


        String path = pokemons.get(0).getPokeImageSrc();
        BufferedImage pokemonMini = null;
        try {
            URL imageUrl = new URL(path);
            System.out.println("image Link: " + pokemons.get(0).getPokeImageSrc() );
            pokemonMini = ImageIO.read(imageUrl);
            System.out.println("we got this");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Image newImage = pokemonMini.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JLabel pokeMiniLabel = new JLabel(new ImageIcon(newImage));
        pokeMiniLabel.setPreferredSize(new Dimension(240,240));
        pokeMiniLabel.setText(pokemons.get(0).getName());
        pokeMiniLabel.setHorizontalTextPosition(JLabel.CENTER);
        pokeMiniLabel.setVerticalTextPosition(JLabel.TOP);
        pokeMiniLabel.setHorizontalAlignment(JLabel.CENTER);
        pokeMiniLabel.setVerticalAlignment(JLabel.CENTER);


        JPanel display = new JPanel();
        display.setPreferredSize(new Dimension(600,600));
        display.add(pokeMiniLabel);



        window.add(display);



        window.setVisible(true);

    }
}
