package org.example;



public class Ability {
    private String name;
    private Integer requiredLevel;

    public Ability(String name, Integer requiredLevel) {
        this.name = name;
        this.requiredLevel = requiredLevel;
    }

    public Ability() {
    }

    public String getName() {
        return name;
    }

    public Integer getRequiredLevel() {
        return requiredLevel;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRequiredLevel(Integer requiredLevel) {
        this.requiredLevel = requiredLevel;
    }
}
