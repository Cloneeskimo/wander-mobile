package com.jacoboaks.wandermobile.game.gameitem;

import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Texture;
import com.jacoboaks.wandermobile.util.Color;
import com.jacoboaks.wandermobile.util.Node;

/**
 * Represents a playable entity
 */
public class Player extends Entity {

    //Data
    private int experience;

    /**
     * Constructs this Player with the given info.
     * @param name this Player's name
     * @param font the font to use for this Player
     * @param symbol this Player's symbol
     * @param color this Player's symbol color
     * @param gx this Player's grid x
     * @param gy this Player's grid y
     */
    public Player(String name, Font font, char symbol, Color color, int gx, int gy) {
        super(name, font, symbol, color, gx, gy);
        this.experience = 0;
    }

    /**
     * Constructs this Player with the given info.
     * @param name this Player's name
     * @param texture the texture to use for this Plaeyr
     * @param gx this Player's grid x
     * @param gy this Plaery's grid y
     */
    public Player(String name, Texture texture, int gx, int gy) {
        super(name, texture, gx, gy);
        this.experience = 0;
    }

    /**
     * Constructs this Player by copying another one.
     * @param other the other Player to copy
     */
    public Player(Player other) {
        super(other);
        this.experience = other.experience;
    }

    /**
     * Constructs this Player with the given Node in symbol mode.
     * @param data the Node to use when constructing this Player
     * @param font the font to use
     */
    protected Player(Node data, Font font) {
        super(data, font);
        this.experience = Integer.parseInt(data.getChild("experience").getValue());
    }

    /**
     * Constructs this Player with the given Node in texture mode.
     * @param data the Node to use when constructing this Player
     */
    protected Player(Node data) {
        super(data);
        this.experience = Integer.parseInt(data.getChild("experience").getValue());
    }

    /**
     * Creates a Player with the given data.
     * @param data the data to use when constructing this Player
     * @param font the font to use if this Player is a symbol tile
     * @return the constructed Player
     */
    public static Player nodeToPlayer(Node data, Font font) {
        if (Boolean.parseBoolean(data.getChild("symbolTile").getValue())) {
            return new Player(data, font);
        } else {
            return new Player(data);
        }
    }

    //Node Converter
    public Node toNode() {
        Node data = super.toNode();
        data.setName("Player");
        data.addChild("experience", Integer.toString(this.experience));
        return data;
    }
}