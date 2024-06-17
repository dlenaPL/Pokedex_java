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

        public Pokemon() {
        }

        public Pokemon(String name, Integer hp, Integer attack, Integer defense, Integer specialAttack, Integer specialDefense, Integer speed, List<PokemonType> types, String pokeImageSrc, String flavorText, List<Ability> abilities) {
                this.name = name;
                this.hp = hp;
                this.attack = attack;
                this.defense = defense;
                this.specialAttack = specialAttack;
                this.specialDefense = specialDefense;
                this.speed = speed;
                this.types = types;
                this.pokeImageSrc = pokeImageSrc;
                this.flavorText = flavorText;
                this.abilities = abilities;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public Integer getHp() {
                return hp;
        }

        public void setHp(Integer hp) {
                this.hp = hp;
        }

        public Integer getAttack() {
                return attack;
        }

        public void setAttack(Integer attack) {
                this.attack = attack;
        }

        public Integer getDefense() {
                return defense;
        }

        public void setDefense(Integer defense) {
                this.defense = defense;
        }

        public Integer getSpecialAttack() {
                return specialAttack;
        }

        public void setSpecialAttack(Integer specialAttack) {
                this.specialAttack = specialAttack;
        }

        public Integer getSpecialDefense() {
                return specialDefense;
        }

        public void setSpecialDefense(Integer specialDefense) {
                this.specialDefense = specialDefense;
        }

        public Integer getSpeed() {
                return speed;
        }

        public void setSpeed(Integer speed) {
                this.speed = speed;
        }

        public List<PokemonType> getTypes() {
                return types;
        }

        public void setTypes(List<PokemonType> types) {
                this.types = types;
        }

        public String getPokeImageSrc() {
                return pokeImageSrc;
        }

        public void setPokeImageSrc(String pokeImageSrc) {
                this.pokeImageSrc = pokeImageSrc;
        }

        public String getFlavorText() {
                return flavorText;
        }

        public void setFlavorText(String flavorText) {
                this.flavorText = flavorText;
        }

        public List<Ability> getAbilities() {
                return abilities;
        }

        public void setAbilities(List<Ability> abilities) {
                this.abilities = abilities;
        }
}
