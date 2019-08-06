package com.jacoboaks.wandermobile.game.gameitem;

import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Texture;
import com.jacoboaks.wandermobile.util.Color;
import com.jacoboaks.wandermobile.util.Node;

/**
 * Represents a Tile which would not normally move (although it may). Collision with
 * them depends on their maneuverability factor, explained below.
 */
public class StaticTile extends Tile {

    //Data
    private int maneuverability; //how maneuverable the tile is - 0 -> non-maneuverable, 1 -> maneuverable

    /**
     * Constructs this StaticTile using a colored character.
     * @param name the name of the static tile
     * @param font the font_default to draw the character from
     * @param symbol the character to represent this tile
     * @param color the color of the character
     * @param gx the grid x coordinate
     * @param gy the grid y coordinate
     * @param maneuverability how maneuverable this tile is (explained in data)
     */
    public StaticTile(String name, Font font, char symbol, Color color, int gx, int gy, int maneuverability) {
        super(name, font, symbol, color, gx, gy);
        this.maneuverability = maneuverability;
    }

    /**
     * Constructs this StaticTile using a texture.
     * @param name the name of the static tile
     * @param texture the texture to use
     * @param gx the grid x coordinate
     * @param gy the grid y coordinate
     * @param maneuverability how maneuverable this tile is (explained in data)
     */
    public StaticTile(String name, Texture texture, int gx, int gy, int maneuverability) {
        super(name, texture, gx, gy);
        this.maneuverability = maneuverability;
    }

    /**
     * Constructs this StaticTile by copying from another one.
     * @param other the StaticTile to copy from.
     */
    public StaticTile(StaticTile other) {
        super(other);
        this.maneuverability = other.maneuverability;
    }

    /**
     * Constructs this StaticTile by using a normal Tile and adding a maneuverability value
     * @param other the normal Tile to use
     * @param maneuverability the maneuverability value to add
     */
    public StaticTile(Tile other, int maneuverability) {
        super(other);
        this.maneuverability = maneuverability;
    }

    /**
     * Constructs this StaticTile using a given Node. This constructor assumes that this StaticTile
     * is a symbol tile.
     * @param data the node to use when constructing this Tile
     * @param font the font to draw the character from
     */
    protected StaticTile(Node data, Font font) {
        super(data, font);
        this.maneuverability = Integer.parseInt(data.getChild("maneuverability").getValue());
    }

    /**
     * Constructs this StaticTile using a given Node. This constructor assume that this StaticTile
     * is not a symbol tile.
     * @param data the node to use when constructing this Tile
     */
    protected StaticTile(Node data) {
        super(data);
        this.maneuverability = Integer.parseInt(data.getChild("maneuverability").getValue());
    }

    /**
     * Creates a StaticTile with the given data.
     * @param data the data to use when constructing this StaticTile
     * @param font the font to use if this StaticTile is a symbol tile
     * @return the constructed StaticTile
     */
    public static StaticTile nodeToStaticTile(Node data, Font font) {
        if (Boolean.parseBoolean(data.getChild("symbolTile").getValue())) {
            return new StaticTile(data, font);
        } else {
            return new StaticTile(data);
        }
    }

    //Accessor
    public int getManeuverability() { return this.maneuverability; }

    //Node Converter
    @Override
    public Node toNode() {
        Node data = super.toNode();
        data.setName("StaticTile");
        data.addChild(new Node("maneuverability", Integer.toString(this.maneuverability)));
        return data;
    }
}
