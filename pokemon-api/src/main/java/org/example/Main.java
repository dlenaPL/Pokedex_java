package org.example;


import com.google.gson.*;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;



public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {

        List<Pokemon> pokemons = new ArrayList<>();

        for(int i = 1; i <= 10; ++i) {
            String number = String.valueOf(i);
            String uri = String.format("https://pokeapi.co/api/v2/pokemon/%s/", number);

            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

            String jsonResponse = getResponse.body();
            JsonElement element = JsonParser.parseString(jsonResponse);
            JsonObject obj = element.getAsJsonObject();

            Pokemon tempPokemon = new Pokemon();
            tempPokemon.setAbilities(new ArrayList<>());
            tempPokemon.setTypes(new ArrayList<>());

            System.out.println("=================== Setting up name =================================");

            String pokemonName = String.valueOf(obj.get("name"));
            String name = pokemonName.replace("\"", "");
            tempPokemon.setName(name.substring(0, 1).toUpperCase() + name.substring(1));

            System.out.println("=================== setting up moves =======================================");

            JsonArray moves = obj.get("moves").getAsJsonArray();
            for (JsonElement mov : moves) {

                String learningMethodName = mov.getAsJsonObject().get("version_group_details").getAsJsonArray().get(0).getAsJsonObject().get("move_learn_method").getAsJsonObject().get("name").toString();
                if (learningMethodName.contains("level-up")) {

                    String moveName = String.valueOf(mov.getAsJsonObject().get("move").getAsJsonObject().get("name")).replace("\"", "");
                    String learnAtLevel = String.valueOf(mov.getAsJsonObject().get("version_group_details").getAsJsonArray().get(0).getAsJsonObject().get("level_learned_at"));
                    tempPokemon.getAbilities().add(new Ability(moveName, Integer.parseInt(learnAtLevel)));
                }
            }

            System.out.println("=================== setting up stats =======================================");
            JsonArray pokemonStats = obj.get("stats").getAsJsonArray();

            tempPokemon.setHp(pokemonStats.get(0).getAsJsonObject().get("base_stat").getAsInt());
            tempPokemon.setAttack(pokemonStats.get(1).getAsJsonObject().get("base_stat").getAsInt());
            tempPokemon.setDefense(pokemonStats.get(2).getAsJsonObject().get("base_stat").getAsInt());
            tempPokemon.setSpecialAttack(pokemonStats.get(3).getAsJsonObject().get("base_stat").getAsInt());
            tempPokemon.setSpecialDefense(pokemonStats.get(4).getAsJsonObject().get("base_stat").getAsInt());
            tempPokemon.setSpeed(pokemonStats.get(5).getAsJsonObject().get("base_stat").getAsInt());

            System.out.println("=================== setting up types =======================================");

            for(JsonElement type : obj.get("types").getAsJsonArray()){
                String typeText = String.valueOf(type.getAsJsonObject().get("type").getAsJsonObject().get("name"));
                String clearedType = typeText.replace("\"", "");
                System.out.println(clearedType);
                for (PokemonType elm : PokemonType.values()){
                    if(clearedType.equalsIgnoreCase(elm.name())){

                        tempPokemon.getTypes().add(elm);
                    }
                }
            }



            System.out.println("=================== setting up img =======================================");

            String imgString = String.valueOf(obj.get("sprites").getAsJsonObject().get("front_default"));
            tempPokemon.setPokeImageSrc(imgString.replace("\"", ""));


            System.out.println("=================== setting up flavor text =======================================");

            uri = String.format("https://pokeapi.co/api/v2/pokemon-species/%s/", number);

            getRequest = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .GET()
                    .build();

            client = HttpClient.newHttpClient();
            getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

            jsonResponse = getResponse.body();
            element = JsonParser.parseString(jsonResponse);
            obj = element.getAsJsonObject();

            String flavorText = String.valueOf(obj.getAsJsonObject().get("flavor_text_entries").getAsJsonArray().get(3).getAsJsonObject().get("flavor_text"));

            String flavorTextCleared = flavorText.replace("\\n", " ").replace("\\f", " ");
            tempPokemon.setFlavorText(flavorTextCleared);

            pokemons.add(tempPokemon);
        }


        System.out.println("pokemons length: " + pokemons.size());
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
        App pokedex = new App(pokemons);
        pokedex.run();
    }
}