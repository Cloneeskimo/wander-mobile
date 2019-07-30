package com.jacoboaks.wandermobile.game.gameitem;

import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Texture;
import com.jacoboaks.wandermobile.util.Color;

/**
 * @purpose is to represent a Tile which would not normally move (although it may). Collision with
 * them depends on their maneuverability factor, explained below
 */
public class StaticTile extends Tile {

    //Data
    int maneuverability; //how maneuverable the tile is - 0 -> non-maneuverable, 1 -> maneuverable

    /**
     * @purpose is to construct this StaticTile using a colored character
     * @param name the name of the static tile
     * @param font the font to draw the character from
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
     * @purpose is to construct this StaticTile using a texture
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

    //Accessor
    public int getManeuverability() { return this.maneuverability; }
}
