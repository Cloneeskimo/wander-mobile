package com.jacoboaks.wandermobile.game;

import android.opengl.GLES20;
import android.os.Bundle;

import com.jacoboaks.wandermobile.R;
import com.jacoboaks.wandermobile.game.gameitem.Entity;
import com.jacoboaks.wandermobile.game.gameitem.GameItem;
import com.jacoboaks.wandermobile.game.gameitem.StaticTile;
import com.jacoboaks.wandermobile.game.gameitem.TextItem;
import com.jacoboaks.wandermobile.game.gameitem.Tile;
import com.jacoboaks.wandermobile.graphics.FollowingCamera;
import com.jacoboaks.wandermobile.graphics.GameRenderer;
import com.jacoboaks.wandermobile.graphics.ShaderProgram;
import com.jacoboaks.wandermobile.graphics.Texture;
import com.jacoboaks.wandermobile.graphics.Transformation;
import com.jacoboaks.wandermobile.util.Coord;
import com.jacoboaks.wandermobile.util.Node;

/**
 * Holds many GameItems to be rendered under a HUD.
 */
public class World {

    //Data
    private Entity player;
    private Area area;
    private ShaderProgram shaderProgram;
    private FollowingCamera camera;
    private boolean tileSelected = false;
    private Tile selectionTile;
    private HUD hud;

    /**
     * Constructs this World.
     * @param area the area to be rendered in this World
     * @param player the player to be rendered in this World
     * @param hud a reference to the corresponding HUD for this World
     */
    public World(Area area, Entity player, HUD hud) {

        //initialize graphics and shader program
        this.initGraphics(player);
        this.initShaderProgram();

        //set area and player references
        this.area = area;
        this.player = player;
        this.selectionTile = new Tile("Selection", new Texture(R.drawable.texture_selected), 0, 0);
        Coord spawn = this.area.getSpawn();
        this.player.setGridPosition((int)spawn.x, (int)spawn.y);

        //set hud reference, update area name in hud
        this.hud = hud;
        TextItem areaName = (TextItem)this.hud.getItem("AREA_NAME");
        areaName.setText(this.area.getName());
    }

    /**
     * Reinstates any saved world data.
     * @param data the data to reinstate
     */
    public void instateLoadedData(Bundle data) {
        this.camera.setX(Float.parseFloat(data.getString("logic_world_camerax")));;
        this.camera.setY(Float.parseFloat(data.getString("logic_world_cameray")));;
        this.camera.setZoom(Float.parseFloat(data.getString("logic_world_camerazoom")));
    }

    /**
     * Initializes this World's ShaderProgram.
     */
    private void initShaderProgram() {

        //create shader program, load shaders, and link them.
        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.loadShader(R.raw.shader_worldvertex, GLES20.GL_VERTEX_SHADER);
        this.shaderProgram.loadShader(R.raw.shader_worldfragment, GLES20.GL_FRAGMENT_SHADER);
        this.shaderProgram.link();

        //register shader program uniforms
        this.shaderProgram.registerUniform("aspectRatio");
        this.shaderProgram.registerUniform("aspectRatioAction");
        this.shaderProgram.registerUniform("x");
        this.shaderProgram.registerUniform("y");
        this.shaderProgram.registerUniform("camx");
        this.shaderProgram.registerUniform("camy");
        this.shaderProgram.registerUniform("camzoom");
        this.shaderProgram.registerUniform("color");
        this.shaderProgram.registerUniform("textureSampler");
        this.shaderProgram.registerUniform("colorOverride");
        this.shaderProgram.registerUniform("isTextured");
    }

    /**
     * Initializes the graphical components of this World.
     */
    private void initGraphics(GameItem cameraFollowee) {
        this.camera = new FollowingCamera(0.2f, cameraFollowee, false);
    }

    //Update Method
    public void update(float dt) {
        this.area.update(dt);
        this.player.update(dt);
        this.camera.update(dt);
    }

    //Render Method
    public void render() {

        //bind shader program
        this.shaderProgram.bind();

        //update aspect ratio and aspect ratio action
        GLES20.glUniform1fv(this.shaderProgram.getUniformIndex("aspectRatio"), 1,
                new float[] {GameRenderer.surfaceAspectRatio }, 0);
        GLES20.glUniform1iv(this.shaderProgram.getUniformIndex("aspectRatioAction"), 1,
                new int[] { GameRenderer.surfaceAspectRatioAction ? 1 : 0 }, 0);

        //update camera properties
        GLES20.glUniform1fv(this.shaderProgram.getUniformIndex("camx"), 1,
                new float[] { this.camera.getX() }, 0);
        GLES20.glUniform1fv(this.shaderProgram.getUniformIndex("camy"), 1,
                new float[] { this.camera.getY() }, 0);
        GLES20.glUniform1fv(this.shaderProgram.getUniformIndex("camzoom"), 1,
                new float[] { this.camera.getZoom() }, 0);

        //render area and player
        this.area.render(this.shaderProgram);
        this.player.render(this.shaderProgram);

        //render selection if tile texture_selected
        if (this.tileSelected) this.selectionTile.render(this.shaderProgram);

        //unbind shader program
        this.shaderProgram.unbind();
    }

    /**
     * Registers a single tap on the world - user is either selecting or deselecting a tile.
     * @param x the screen x position of the tap
     * @param y the screen y position of the tap
     */
    public void registerTap(float x, float y) {

        //check if there is already a texture_selected tile
        if (this.tileSelected) {
            this.tileSelected = false;
            this.hud.getItem("SELECTION").setVisibility(false);
            this.hud.getItem("SELECTION_NAME").setVisibility(false);
            this.hud.getItem("SELECTION_INFO").setVisibility(false);
            this.hud.getItem("ENTITY_SELECTION_HEALTH").setVisibility(false);
        } else { //respond to tap

            Coord position = new Coord(x, y);
            Transformation.screenToGrid(position, this.camera);

            //set selection position
            this.tileSelected = true;
            this.selectionTile.setGridPosition((int)position.x, (int)position.y);
            this.registerSelection((int)position.x, (int)position.y);
        }
    }

    /**
     * Registers a texture_selected tile and updates the corresponding HUD.
     * @param gx the grid x of the tile
     * @param gy the grid y of the tile
     */
    private void registerSelection(int gx, int gy) {

        //get texture_selected tile
        Tile selectedTile = this.area.getTile(gx, gy);

        //check if player
        if (selectedTile == null) {
            Coord playerPos = this.player.getGridPosition();
            if (playerPos.x == gx && playerPos.y == gy) selectedTile = this.player;
        }

        //check if null
        if (selectedTile != null) {

            //set basic hud info
            this.hud.getItem("SELECTION").setVisibility(true);
            TextItem ti = (TextItem)this.hud.getItem("SELECTION_NAME");
            ti.setVisibility(true);
            ti.setText(selectedTile.getName());
            ti.getModel().getMaterial().setColor(selectedTile.getModel().getMaterial().getColor());
            ti = (TextItem)this.hud.getItem("SELECTION_INFO");
            ti.setVisibility(true);

            //check if Entity
            if (selectedTile instanceof Entity) {
                Entity e = (Entity)selectedTile;
                ti.setText("Level: " + e.getLevel());
                ti = (TextItem)this.hud.getItem("ENTITY_SELECTION_HEALTH");
                ti.setVisibility(true);
                ti.setText("HP: " + e.getHealth() + "/" + e.getMaxHealth());

            //check if StaticTile
            } else if (selectedTile instanceof StaticTile) {
                StaticTile st = (StaticTile)selectedTile;
                ti.setText("Maneuverability: " + st.getManeuverability());
            }
        }
    }

    //Accessors
    public Entity getPlayer() { return this.player; }
    public FollowingCamera getCamera() { return this.camera; }

    //Data Requesting Method
    public void requestData(Node data) {

        //approximate player position if moving
        Coord playerPos = this.player.getGridPosition();
        this.player.setGridPosition((int)playerPos.x, (int)playerPos.y);

        //save data
        data.addChild(new Node("world_camerax", Float.toString(this.camera.getX())));
        data.addChild(new Node("world_cameray", Float.toString(this.camera.getY())));
        data.addChild(new Node("world_camerazoom", Float.toString(this.camera.getZoom())));
    }

    //Cleanup Method
    public void cleanup() {
        this.shaderProgram.cleanup();
    }
}
