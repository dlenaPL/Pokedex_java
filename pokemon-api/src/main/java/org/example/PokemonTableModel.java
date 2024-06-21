package org.example;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class PokemonTableModel extends AbstractTableModel {

    private final List<Pokemon> pokemons;
    private final String[] columnNames = {"Name", "Types"};

    public PokemonTableModel(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }

    @Override
    public int getRowCount() {
        return pokemons.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Pokemon pokemon = pokemons.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return pokemon.getName();
            case 1:
                return pokemon.getTypes().toString();
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public void addPokemon(Pokemon pokemon) {
        pokemons.add(pokemon);
        fireTableRowsInserted(pokemons.size() - 1, pokemons.size() - 1);
    }

    public void setPokemons(List<Pokemon> pokemons) {
        this.pokemons.clear();
        this.pokemons.addAll(pokemons);
        fireTableDataChanged();
    }
}
