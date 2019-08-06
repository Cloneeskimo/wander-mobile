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
import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.util.Color;
import com.jacoboaks.wandermobile.util.Node;
import com.jacoboaks.wandermobile.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the logic for the world navigation of the game.
 */
public class WorldLogic implements GameLogic {

    //Logic Data
    private World world;
    private HUD hud;
    private WorldControl control;
    private Font font;

    //Saved Data
    private Bundle savedInstanceData;

    //Initialization Method
    @Override
    public void init() {

        //initialize graphics and objects
        this.initGraphics();
        this.initHUD();
        this.initWorld();

        //create controls
        this.control = new WorldControl();

        //load data if there is any to load
        if (this.savedInstanceData != null) this.instateLoadedData();
    }

    /**
     * Initializes the graphical components of the logic.
     */
    private void initGraphics() {

        //set clear color and create font_default
        GLES20.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
        this.font = new Font(R.drawable.font_default, R.raw.fontcuttoffs_default,10, 10, ' ');
    }

    /**
     * Initializes the HUD of the logic.
     */
    private void initHUD() {

        //create HUD
        this.hud = new HUD();

        //create hud text material
        Material textMaterial = new Material(font.getFontSheet(), new Color(1.0f, 1.0f, 1.0f, 1.0f), true);

        //fps label
        TextItem fpsLabel = new TextItem(this.font, "FPS: ", textMaterial, 0f, 0f);
        fpsLabel.scale(0.13f);
        this.hud.addItem("FPS_LABEL", fpsLabel, HUD.Placement.BOTTOM_LEFT, 0.02f);

        //fps counter
        TextItem fpsCounter = new TextItem(this.font, "calculating...", textMaterial, 0f, 0f);
        fpsCounter.scale(0.13f);
        this.hud.addItem("FPS_COUNTER", fpsCounter, HUD.Placement.RIGHT_OF_LAST, 0f);

        //wander title
        TextItem title = new TextItem(this.font, "Wander Mobile v" + MainActivity.WANDER_VERSION
                + "b" + MainActivity.WANDER_BUILD, textMaterial, 0f, 0f);
        title.scale(0.2f);
        this.hud.addItem("TITLE", title, HUD.Placement.TOP_LEFT, 0.02f);

        //selection
        TextItem selection = new TextItem(this.font, "Selection:", textMaterial, 0f, 0f);
        selection.scale(0.19f);
        selection.setVisibility(false);
        this.hud.addItem("SELECTION", selection, HUD.Placement.BELOW_LAST, 0.13f);

        //selection name
        Material selectionInfoMaterial = new Material(font.getFontSheet(), new Color(1.0f, 1.0f, 1.0f, 1.0f), true);
        TextItem selectionName = new TextItem(this.font, "", selectionInfoMaterial, 0f, 0f);
        selectionName.scale(0.155f);
        selectionName.setVisibility(false);
        this.hud.addItem("SELECTION_NAME", selectionName, HUD.Placement.BELOW_LAST, 0.02f);

        //entity selection level
        TextItem selectionInfo = new TextItem(this.font, "", selectionInfoMaterial, 0f, 0f);
        selectionInfo.scale(0.126f);
        selectionInfo.setVisibility(false);
        this.hud.addItem("SELECTION_INFO", selectionInfo, HUD.Placement.BELOW_LAST, 0.02f);

        //entity selection health
        Material healthMaterial = new Material(this.font.getFontSheet(), new Color(0.8f, 0.1f, 0.1f, 1.0f), true);
        TextItem entitySelectionHealth = new TextItem(this.font, "", healthMaterial, 0f, 0f);
        entitySelectionHealth.scale(0.126f);
        entitySelectionHealth.setVisibility(false);
        this.hud.addItem("ENTITY_SELECTION_HEALTH", entitySelectionHealth, HUD.Placement.BELOW_LAST, 0.02f);

        //area name
        TextItem areaName = new TextItem(this.font, "", textMaterial, 0f, 0f);
        areaName.scale(0.20f);
        this.hud.addItem("AREA_NAME", areaName, HUD.Placement.BOTTOM_MIDDLE, 0.02f);
    }

    /**
     * Initialized the world of the logic.
     */
    private void initWorld() {

        //create player
        Entity player = new Entity("Svenske", this.font, 'S', new Color(0.62f, 0.0f, 0.1f, 1.0f), 0, 0);

        //create area
//        List<StaticTile> staticTiles = new ArrayList<>();
//        for (int x = -5; x < 6; x++) {
//            staticTiles.add(new StaticTile("Trees", this.font, '#', new Color(0.0f, 0.6f, 0.0f, 1.0f), x, -5, 0));
//            staticTiles.add(new StaticTile("Trees", this.font, '#', new Color(0.0f, 0.6f, 0.0f, 1.0f), x, 5, 0));
//        }
//        for (int y = -4; y < 5; y++) {
//            staticTiles.add(new StaticTile("Trees", this.font, '#', new Color(0.0f, 0.6f, 0.0f, 1.0f), -5, y, 0));
//            staticTiles.add(new StaticTile("Trees", this.font, '#', new Color(0.0f, 0.6f, 0.0f, 1.0f), 5, y, 0));
//        }
//        List<Entity> entities = new ArrayList<>();
//        Entity spider = new Entity("Forest Spider", this.font, 'f', new Color(0.9f, 0.1f, 0.1f, 1.0f), -2, 3);
//        spider.setEntityInto(27, 27, 4);
//        entities.add(spider);
//        Area area = new Area("Deep Woods", staticTiles, entities);
        Area area = Area.loadArea(R.raw.area_deepwoods, this.font);

        //create world
        this.world = new World(area, player, this.hud);
    }

    /**
     * Reinstates saved bundle data from a previous instance of this logic.
     */
    private void instateLoadedData() {

        //load saved data
        this.world.instateLoadedData(this.savedInstanceData);
        this.world.getPlayer().setX(Float.parseFloat(this.savedInstanceData.getString("logic_playerx")));
        this.world.getPlayer().setY(Float.parseFloat(this.savedInstanceData.getString("logic_playery")));
    }

    /**
     * Updates any FPS tracker or any logic based on FPS
     * @param FPS the current FPS
     */
    public void onFPSUpdate(float FPS) {
        TextItem fpsCounter = (TextItem)this.hud.getItem("FPS_COUNTER");
        fpsCounter.setText(Float.toString(FPS));
    }

    //Data Loading Method
    @Override
    public void loadData(Bundle savedInstanceData) {
        this.savedInstanceData = savedInstanceData;
    }

    //Input Method
    @Override
    public boolean input(MotionEvent e) {

        return this.control.input(e, this.world); }

    //Scale Input Method
    @Override
    public boolean scaleInput(float factor) { return this.control.scaleInput(factor,
            this.world.getCamera(), this.world.getPlayer()); }

    //Update Method
    @Override
    public void update(float dt) { this.world.update(dt); }

    //Render Method
    @Override
    public void render() {
        this.world.render();
        this.hud.render();
    }

    //Data Requesting Method
    @Override
    public Node requestData() {

        //add data to node and return it
        Node data = new Node("logic", Util.WORLD_LOGIC_TAG);
        this.world.requestData(data);
        data.addChild(new Node("playerx", Float.toString(this.world.getPlayer().getX())));
        data.addChild(new Node("playery", Float.toString(this.world.getPlayer().getY())));
        return data;
    }

    //Cleanup Method
    @Override
    public void cleanup() {
        this.world.cleanup();
        this.hud.cleanup();
    }
}
