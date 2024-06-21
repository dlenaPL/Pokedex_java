package org.example;

public enum PokemonType {
    Normal("#A8A77A"),
    Fire("#EE8130"),
    Water("#6390F0"),
    Electric("#F7D02C"),
    Grass("#7AC74C"),
    Ice("#96D9D6"),
    Fighting("#C22E28"),
    Poison("#A33EA1"),
    Ground("#E2BF65"),
    Flying("#A98FF3"),
    Psychic("#F95587"),
    Bug("#A6B91A"),
    Rock("#B6A136"),
    Ghost("#735797"),
    Dragon("#6F35FC"),
    Dark("#705746"),
    Steel("#B7B7CE"),
    Fairy("#D685AD");

    public final String label;

    PokemonType(String label) {
        this.label = label;
    }

    public String getLabel(){
        return label;
    }

    public static PokemonType fromName(String name) {
        for (PokemonType type : PokemonType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Nieznany typ: " + name);
    }
}
