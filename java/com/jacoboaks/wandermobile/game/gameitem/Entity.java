package com.jacoboaks.wandermobile.game.gameitem;

import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Texture;
import com.jacoboaks.wandermobile.util.Color;
import com.jacoboaks.wandermobile.util.Node;

/**
 * Represents a living Tile - one that can move, has a health value, and has a level.
 */
public class Entity extends Tile {

    //Data
    private int health, maxHealth;
    private int level;

    /**
     * Constructs this StaticTile using a colored character.
     * @param name the name of the static tile
     * @param font the font_default to draw the character from
     * @param symbol the character to represent this tile
     * @param color the color of the character
     * @param gx the grid x coordinate
     * @param gy the grid y coordinate
     */
    public Entity(String name, Font font, char symbol, Color color, int gx, int gy) {
        super(name, font, symbol, color, gx, gy);
        this.health = this.maxHealth = this.level = 1;
    }

    /**
     * Constructs this StaticTile using a texture.
     * @param name the name of the static tile
     * @param texture the texture to use
     * @param gx the grid x coordinate
     * @param gy the grid y coordinate
     */
    public Entity(String name, Texture texture, int gx, int gy) {
        super(name, texture, gx, gy);
        this.health = this.maxHealth = this.level = 1;
    }

    /**
     * Constructvs this Entity by copying another one.
     * @param other the Entity to copy from
     */
    public Entity(Entity other) {
        super(other);
        this.health = other.health;
        this.maxHealth = other.maxHealth;
        this.level = other.level;
    }

    /**
     * Constructs this Entity using a given Node. This constructor assumes that this Entity is a symbol
     * tile.
     * @param data the node to use when constructing this Tile
     * @param font the font to draw the character from
     */
    protected Entity(Node data, Font font) {
        super(data, font);
        this.health = Integer.parseInt(data.getChild("health").getValue());
        this.maxHealth = Integer.parseInt(data.getChild("maxHealth").getValue());
        this.level = Integer.parseInt(data.getChild("level").getValue());
    }

    /**
     * Constructs this Entity using a given Node. This constructor assume that this Entity is not a
     * symbol tile.
     * @param data the node to use when constructing this Tile
     */
    protected Entity(Node data) {
        super(data);
        this.health = Integer.parseInt(data.getChild("health").getValue());
        this.maxHealth = Integer.parseInt(data.getChild("maxHealth").getValue());
        this.level = Integer.parseInt(data.getChild("level").getValue());
    }

    /**
     * Creates a Entity with the given data.
     * @param data the data to use when constructing this Entity
     * @param font the font to use if this Entity is a symbol tile
     * @return the constructed Entity
     */
    public static Entity nodeToEntity(Node data, Font font) {
        if (Boolean.parseBoolean(data.getChild("symbolTile").getValue())) {
            return new Entity(data, font);
        } else {
            return new Entity(data);
        }
    }

    /**
     * Sets the basic entity info.
     * @param health the health this entity has
     * @param maxHealth the maximum health this entity can have
     * @param level the level of this entity
     */
    public void setEntityInto(int health, int maxHealth, int level) {
        this.health = health;
        this.maxHealth = maxHealth;
        this.level = level;
    }

    /**
     * Changes the health of this entity.
     * @param health the value to change the health to - will not heal over maximum health
     */
    public void setHealth(int health) {
        if (health > this.maxHealth) this.health = this.maxHealth;
        else this.health = health;
    }

    /**
     * Deals a certain amount of damage to this entity.
     * @param damage the amount of damage to do to the entity
     */
    public void dealDamage(int damage) { this.health -= damage; }

    //Accessors
    public boolean isDead() { return this.health <= 0; }
    public int getHealth() { return this.health; }
    public int getMaxHealth() { return this.maxHealth; }
    public int getLevel() { return this.level; }

    //Node Converter
    @Override
    public Node toNode() {
        Node data = super.toNode();
        data.setName("Entity");
        data.addChild(new Node("health", Integer.toString(this.health)));
        data.addChild(new Node("maxHealth", Integer.toString(this.maxHealth)));
        data.addChild(new Node("level", Integer.toString(this.level)));
        return data;
    }
}
