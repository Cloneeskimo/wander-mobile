package com.jacoboaks.wandermobile.game.gamelogic;

import android.opengl.GLES20;
import android.os.Bundle;
import android.view.MotionEvent;

import com.jacoboaks.wandermobile.MainActivity;
import com.jacoboaks.wandermobile.R;
import com.jacoboaks.wandermobile.game.Area;
import com.jacoboaks.wandermobile.game.HUD;
import com.jacoboaks.wandermobile.game.World;
import com.jacoboaks.wandermobile.game.gamecontrol.WorldControl;
import com.jacoboaks.wandermobile.game.gameitem.Entity;
import com.jacoboaks.wandermobile.game.gameitem.StaticTile;
import com.jacoboaks.wandermobile.game.gameitem.TextItem;
import com.jacoboaks.wandermobile.game.gameitem.Tile;
import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.util.Color;
import com.jacoboaks.wandermobile.util.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * @purpose is to implement the logic for Wander when in the main game world
 */
public class WorldLogic implements GameLogic {

    //Surface Data
    private int width, height;
    private float aspectRatio;
    private boolean aspectRatioAction; //true (ratio < 1) -> multiply y by aspect ratio; false (ratio >= 1) -> divide x by aspect ratio

    //Logic Data
    private World world;
    private HUD hud;
    private WorldControl control;
    private Font font;

    //Saved Data
    private Bundle savedInstanceData;

    /**
     * @called whenever the surface is created
     * @param width width of the new surface
     * @param height height of the new surface
     */
    @Override
    public void init(int width, int height) {

        //initialize graphics and objects
        this.initGraphics(width, height);
        this.initWorld();
        this.initHUD();

        //create controls
        this.control = new WorldControl();

        //load data if there is any to load
        if (this.savedInstanceData != null) this.instateLoadedData();
    }

    /**
     * @purpose is to initialize all of the graphical components of the logic
     * @param width the width of the surface
     * @param height the height of the surface
     */
    private void initGraphics(int width, int height) {

        //save width, height, and aspect ratio, create camera
        this.width = width;
        this.height = height;
        this.aspectRatio = (float) width / (float) height;
        this.aspectRatioAction = (aspectRatio < 1.0f);

        //set clear color and create font
        GLES20.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
        this.font = new Font(R.drawable.font, R.raw.fontcutoffs,10, 10, ' ');
    }

    /**
     * @purpose is to create and populate the world
     */
    private void initWorld() {

        //create player
        Entity player = new Entity("Player", this.font, 'J', new Color(0.62f, 0.0f, 0.1f, 1.0f), 0, 0);

        //create area
        List<StaticTile> staticTiles = new ArrayList<>();
        for (int x = -5; x < 6; x++) {
            staticTiles.add(new StaticTile("Trees", this.font, '#', new Color(0.0f, 0.6f, 0.0f, 1.0f), x, -5, 0));
            staticTiles.add(new StaticTile("Trees", this.font, '#', new Color(0.0f, 0.6f, 0.0f, 1.0f), x, 5, 0));
        }
        for (int y = -4; y < 5; y++) {
            staticTiles.add(new StaticTile("Trees", this.font, '#', new Color(0.0f, 0.6f, 0.0f, 1.0f), -5, y, 0));
            staticTiles.add(new StaticTile("Trees", this.font, '#', new Color(0.0f, 0.6f, 0.0f, 1.0f), 5, y, 0));
        }
        Area area = new Area("Deep Woods", staticTiles, new ArrayList<Entity>());

        //create world
        this.world = new World(this.aspectRatio, this.aspectRatioAction, area, player);
    }

    /**
     * @purpose is to create and populate the HUD for this logic
     */
    private void initHUD() {

        //create HUD
        this.hud = new HUD(this.aspectRatio, this.aspectRatioAction);

        //create hud text material
        Material textMaterial = new Material(font.getFontSheet(), new Color(1.0f, 1.0f, 1.0f, 1.0f), true);

        //wander title
        TextItem title = new TextItem(this.font, "Wander Mobile v" + MainActivity.WANDER_VERSION
                + "b" + MainActivity.WANDER_BUILD, textMaterial, 0f, 0f);
        title.scale(0.2f);
        this.hud.addItem(title, HUD.Placement.TOP_LEFT, 0.01f);

        //fps label
        TextItem fpsLabel = new TextItem(this.font, "FPS: ", textMaterial, 0f, 0f);
        fpsLabel.scale(0.13f);
        this.hud.addItem(fpsLabel, HUD.Placement.BOTTOM_LEFT, 0.01f);

        //fps counter
        TextItem fpsCounter = new TextItem(this.font, "calculating...", textMaterial, 0f, 0f);
        fpsCounter.scale(0.13f);
        this.hud.addItem(fpsCounter, HUD.Placement.RIGHT_OF_LAST, 0f);
    }

    /**
     * @purpose is to instate and saved bundle data from a previous instance of this logic
     */
    private void instateLoadedData() {

        //load saved data
        this.world.instateLoadedData(this.savedInstanceData);
        this.world.getPlayer().setX(Float.parseFloat(this.savedInstanceData.getString("worldlogic_playerx")));
        this.world.getPlayer().setY(Float.parseFloat(this.savedInstanceData.getString("worldlogic_playery")));
    }

    /**
     * @purpose is to update any FPS tracker or any logic based on FPS
     * @param FPS the current FPS
     */
    public void onFPSUpdate(float FPS) {
        TextItem fpsCounter = (TextItem)this.hud.getItem(2);
        fpsCounter.setText(Float.toString(FPS));
    }

    /**
     * @purpose is to save any bundle data from a previous instance of this logic for loading after
     * initialization
     * @param savedInstanceData the bundle data to save for instating later
     */
    @Override
    public void loadData(Bundle savedInstanceData) {
        this.savedInstanceData = savedInstanceData;
    }

    /**
     * @purpose is to handle any input events that occur in the GameView
     * @param e the input event to handle
     * @return whether or not the MotionEvent was handled in any way
     */
    @Override
    public boolean input(MotionEvent e) { return this.control.input(e, this.world, this.width, this.height); }

    /**
     * @purpose is to handle specifically scale events
     * @param factor the factor by which the user has scaled
     */
    @Override
    public boolean scaleInput(float factor) { return this.control.scaleInput(factor,
            this.world.getCamera(), this.world.getPlayer()); }

    /**
     * @purpose is the update the components of this logic
     * @param dt the time, in milliseconds, since the last update
     */
    @Override
    public void update(float dt) { this.world.update(dt); }

    /**
     * @purpose is to draw any graphical components to the screen.
     * @called after update every cycle
     */
    @Override
    public void render() {
        this.world.render();
        this.hud.render();
    }

    /**
     * @purpose is to compile all important data into a node to be put into a bundle before
     * terminating this instance of the logic - this will be reloaded in the next instance
     * after the interruption has ceased
     * @return the node containing all of the compiled important information
     */
    @Override
    public Node requestData() {

        //add data to node and return it
        Node data = new Node("worldlogic");
        data.addChild(new Node("playerx", Float.toString(this.world.getPlayer().getX())));
        data.addChild(new Node("playery", Float.toString(this.world.getPlayer().getY())));
        this.world.requestData(data);
        return data;
    }
}
