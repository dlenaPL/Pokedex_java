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
        loadData();
    }

    private void loadData() {
        Gson gson = new Gson();
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

    public String searchPokemonByName(String nameFragment) {
        List<Pokemon> matchingPokemons = pokemons.stream()
                .filter(p -> p.getName().toLowerCase().contains(nameFragment.toLowerCase()))
                .toList();

        if (matchingPokemons.isEmpty()) {
            return "Nie znaleziono Pokemonów o nazwie zawierającej: " + nameFragment;
        }

        return matchingPokemons.toString();
    }

    public String searchPokemonByTypeName(String typeName) {
        PokemonType type;
        try {
            type = PokemonType.fromName(typeName);
            System.out.println("type z funkcji: " + type);
        } catch (IllegalArgumentException e) {
            return "Nieznany typ: " + typeName;
        }

        List<Pokemon> matchingPokemons = pokemons.stream()
                .filter(p -> p.getTypes().contains(type))
                .toList();

        if (matchingPokemons.isEmpty()) {
            return "Nie znaleziono Pokemonów o typie: " + typeName;
        }

        return matchingPokemons.toString();
    }
//
//    public String addPokemon(Pokemon pokemon) {
//        pokemons.add(pokemon);
//        return saveData();
//    }
//
//    public String removePokemon(String name) {
//        pokemons.removeIf(p -> p.getName().equalsIgnoreCase(name));
//        return saveData();
//    }
//
//    public String updatePokemon(String name, Pokemon updatedPokemon) {
//        for (int i = 0; i < pokemons.size(); i++) {
//            if (pokemons.get(i).getName().equalsIgnoreCase(name)) {
//                pokemons.set(i, updatedPokemon);
//                break;
//            }
//        }
//        return saveData();
//    }

    private void saveData() {
        Gson gson = new Gson();
        try {
            // Konwersja ArrayListy na JSON w postaci Stringa
            String json = gson.toJson(pokemons);

            // Zapis JSONa do pliku
            FileWriter fileWriter = new FileWriter("pokemons.json");
            fileWriter.write(json);
            fileWriter.close();

            System.out.println("Pomyślnie zapisano JSON do pliku output.json");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getAllPokemonAsJson() {
        return "getAllPokemonAsJson";
    }

    public String searchPokemonByNameAsJson(String name) {
        return "searchPokemonByNameAsJson";
    }
}

