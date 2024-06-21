package org.example;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PokemonDataHandler {

    private List<Pokemon> pokemons;
    private String jsonFilePath;

    public PokemonDataHandler(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
        System.out.println("-----------------datahandler constructor");
        loadData();
    }

    public void loadData() {
        Gson gson = new Gson();
        System.out.println("-----------loading data in data handler");
        try {
            FileReader reader = new FileReader(jsonFilePath);
                // Użycie TypeToken do obsługi listy typu Person
            Type pokemonListType = new TypeToken<List<Pokemon>>(){}.getType();
            pokemons = gson.fromJson(reader, pokemonListType);
            System.out.println("Ilosc pokemonow wczytana z pliku: " + pokemons.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Pokemon> getAllPokemons() {
        System.out.println("getAllPokemons Command from DataHandler");
        return pokemons;
    }

    public List<Pokemon> searchPokemonByName(String nameFragment) {
        List<Pokemon> matchingPokemons = pokemons.stream()
                .filter(p -> p.getName().toLowerCase().contains(nameFragment.toLowerCase()))
                .toList();

        if (matchingPokemons.isEmpty()) {
            System.out.println("Nie znaleziono Pokemonów o nazwie zawierającej: " + nameFragment);
            return null;
        }

        return matchingPokemons;
    }

    public List<Pokemon> searchPokemonByTypeName(String typeName) {
        PokemonType type;
        try {
            type = PokemonType.fromName(typeName);
            System.out.println("type z funkcji: " + type);
        } catch (IllegalArgumentException e) {
            return null;
        }

        List<Pokemon> matchingPokemons = pokemons.stream()
                .filter(p -> p.getTypes().contains(type))
                .toList();

        if (matchingPokemons.isEmpty()) {
            return null;
        }

        return matchingPokemons;
    }




//koniec klasy
}

