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

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

//    public String getAllPokemons() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            return objectMapper.writeValueAsString(pokemons);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "ERROR";
//        }
//    }
//
//    public String searchPokemon(String name) {
//        List<Pokemon> result = pokemons.stream()
//                .filter(p -> p.getName().equalsIgnoreCase(name))
//                .collect(Collectors.toList());
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            return objectMapper.writeValueAsString(result);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "ERROR";
//        }
//    }
//
//    public String filterByType(String type) {
//        List<Pokemon> result = pokemons.stream()
//                .filter(p -> p.getType().equalsIgnoreCase(type))
//                .collect(Collectors.toList());
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            return objectMapper.writeValueAsString(result);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "ERROR";
//        }
//    }
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

