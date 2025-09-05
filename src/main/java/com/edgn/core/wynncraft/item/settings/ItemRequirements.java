package com.edgn.core.wynncraft.item.settings;

import com.edgn.core.wynncraft.item.enumerations.ClassRequirement;

import java.util.Optional;

public class ItemRequirements {
    private final int level;
    private final Integer strength;
    private final Integer dexterity;
    private final Integer intelligence;
    private final Integer defence;
    private final Integer agility;
    private final ClassRequirement classRequirement;
    
    public ItemRequirements(int level, Integer strength, Integer dexterity, 
                           Integer intelligence, Integer defence, Integer agility, 
                           ClassRequirement classRequirement) {
        this.level = level;
        this.strength = strength;
        this.dexterity = dexterity;
        this.intelligence = intelligence;
        this.defence = defence;
        this.agility = agility;
        this.classRequirement = classRequirement;
    }
    
    public int level() { return level; }
    public Optional<Integer> strength() { return Optional.ofNullable(strength); }
    public Optional<Integer> dexterity() { return Optional.ofNullable(dexterity); }
    public Optional<Integer> intelligence() { return Optional.ofNullable(intelligence); }
    public Optional<Integer> defence() { return Optional.ofNullable(defence); }
    public Optional<Integer> agility() { return Optional.ofNullable(agility); }
    public Optional<ClassRequirement> classType() { return Optional.ofNullable(classRequirement); }
}