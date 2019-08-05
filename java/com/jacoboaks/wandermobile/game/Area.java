package com.jacoboaks.wandermobile.game;

import com.jacoboaks.wandermobile.game.gameitem.Entity;
import com.jacoboaks.wandermobile.game.gameitem.StaticTile;
import com.jacoboaks.wandermobile.game.gameitem.Tile;
import com.jacoboaks.wandermobile.graphics.ShaderProgram;
import com.jacoboaks.wandermobile.util.Coord;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a single area composed of StaticTiles and Entities.
 */
public class Area {

    //Data
    private String name;
    private List<StaticTile> staticTiles;
    private List<Entity> entities;

    /**
     * Constructs this Area with only a name.
     * @param name the name of the zone
     */
    public Area(String name) {
        this(name, new ArrayList<StaticTile>(), new ArrayList<Entity>());
    }

    /**
     * Constructs this Area with a name and a list of tiles and entities.
     * @param name the name of the zone
     * @param staticTiles the list of static tiles of the zone
     * @param entities the list of entities of the zone
     */
    public Area(String name, List<StaticTile> staticTiles, List<Entity> entities) {
        this.name = name;
        this.staticTiles = staticTiles;
        this.entities = entities;
    }

    //Update Method
    public void update(float dt) {
        for (StaticTile staticTile : this.staticTiles) staticTile.update(dt);
        for (Entity entity : this.entities) entity.update(dt);
    }

    //Render Method
    public void render(ShaderProgram shaderProgram) {
        for (StaticTile staticTile : this.staticTiles) staticTile.render(shaderProgram);
        for (Entity entity : this.entities) entity.render(shaderProgram);
    }

    /**
     * @return the StaticTile (or Entity if there is one) at the given grid position. Will return
     * null if there is none there
     */
    public Tile getTile(int gx, int gy) {

        //create tile and position coordinate
        Tile t = null;
        Coord pos;

        //search static tiles
        for (StaticTile st : this.staticTiles) {
            pos = st.getGridPosition();
            if ((int)pos.x == gx && (int)pos.y == gy) t = st;
        }

        //search entities
        for (Entity e : this.entities) {
            pos = e.getGridPosition();
            if ((int)pos.x == gx && (int)pos.y == gy) t = e;
        }

        //return tile
        return t;
    }

    //Accessor
    public String getName() { return this.name; }

    /**
     * Loads a brand new area from a given resource id
     * @param resourceID the resource id of the area to load
     * @return the loaded area
     */
    static Area loadArea(int resourceID) {

        //TODO: complete area loading from given resource IDs

        return new Area("");
    }
}
