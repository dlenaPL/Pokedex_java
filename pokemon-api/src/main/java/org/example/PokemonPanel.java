package org.example;

import javax.swing.*;
import java.awt.*;

public class PokemonPanel extends JPanel {

    private int yOffset = 20;
    private int fontSize = 20;

    public PokemonPanel(int width, int height, String pokeName, Image image, int imgWidth, int imgHeight, Color color){

        setBackground(color);
        setPreferredSize(new Dimension(width, height));
        setLayout(null);

        JLabel pokemonNameLabel = new JLabel(pokeName);
        pokemonNameLabel.setFont(new Font("Helvetica", Font.BOLD, fontSize));
        pokemonNameLabel.setSize(new Dimension(imgWidth, fontSize + fontSize/4));
        pokemonNameLabel.setHorizontalAlignment(JLabel.CENTER);
        pokemonNameLabel.setBounds((width/2)-(imgWidth/2), yOffset, imgWidth, fontSize + fontSize/4);

        JLabel pokeMiniLabel = new JLabel(new ImageIcon(image.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH)));
        pokeMiniLabel.setSize(new Dimension(imgWidth, imgHeight));
        pokeMiniLabel.setBounds((width/2)-(imgWidth/2), yOffset, imgWidth, imgHeight );


        this.add(pokemonNameLabel);
        this.add(pokeMiniLabel);

    }

}
