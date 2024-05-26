package org.example;

import lombok.Data;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Data
public class Pokemon {

        private String name;
        private Integer hp;
        private Integer attack;
        private Integer defense;
        private Integer specialAttack;
        private Integer specialDefense;
        private Integer speed;
        private List<PokemonType> types;
        private String pokeImageSrc;
        private String flavorText;
        private List<Ability> abilities;


}
