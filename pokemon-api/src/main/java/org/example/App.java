package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
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

        String path = pokemons.get(0).getPokeImageSrc();
        BufferedImage pokemonMini;
        try {
            URL imageUrl = new URL(path);
            pokemonMini = ImageIO.read(imageUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JPanel display = new JPanel();
        display.setPreferredSize(new Dimension(600,600));

        String pokeName = pokemons.get(0).getName();
        Color color = Color.decode(pokemons.get(0).getTypes().get(0).label);
        PokemonPanel pPanel = new PokemonPanel((int)(window.getWidth()/2.5), window.getHeight(), pokeName ,pokemonMini,200,200, color);


        display.add(pPanel);



        window.add(display);



        window.setVisible(true);

    }
}
