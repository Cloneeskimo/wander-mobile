package com.jacoboaks.wandermobile.game.gamelogic;

import android.opengl.GLES20;
import android.os.Bundle;
import android.view.MotionEvent;

import com.jacoboaks.wandermobile.MainActivity;
import com.jacoboaks.wandermobile.R;
import com.jacoboaks.wandermobile.game.Area;
import com.jacoboaks.wandermobile.game.HUD;
import com.jacoboaks.wandermobile.game.SaveData;
import com.jacoboaks.wandermobile.game.World;
import com.jacoboaks.wandermobile.game.gamecontrol.WorldControl;
import com.jacoboaks.wandermobile.game.gameitem.ButtonTextItem;
import com.jacoboaks.wandermobile.game.gameitem.Entity;
import com.jacoboaks.wandermobile.game.gameitem.GameItem;
import com.jacoboaks.wandermobile.game.gameitem.Player;
import com.jacoboaks.wandermobile.game.gameitem.TextItem;
import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.graphics.Model;
import com.jacoboaks.wandermobile.util.Color;
import com.jacoboaks.wandermobile.util.Global;
import com.jacoboaks.wandermobile.util.Node;
import com.jacoboaks.wandermobile.util.Util;

/**
 * Contains the logic for the world navigation of the game.
 */
public class WorldLogic implements GameLogic {

    //Static Data
    private static final int SAVE_BUTTON_ACTION_CODE = 1;
    private static final int EXIT_BUTTON_ACTION_CODE = 2;

    //Instance Data
    private WorldControl control;
    private Font font;
    private HUD hud;
    private World world;
    private float saveNotificationTime = -1f;

    //Saved Data
    private SaveData saveData;
    private Bundle savedInstanceData;

    //Initialization Method
    @Override
    public void init() {

        //create font
        this.font = new Font(Global.defaultFontID, Global.defaultFontCuttoffsID, 10, 10, ' ');

        //set save data reference
        this.saveData = new SaveData(MainActivity.getLogicTransferData().getChild("savedata"), this.font);

        //initialize graphics and objects
        this.initGraphics();
        this.initHUD();
        this.initWorld();

        //create controls
        this.control = new WorldControl();
        this.control.addNullInputBounds(this.hud.getItem("SAVE_GAME_BUTTON").getBounds());
        this.control.addNullInputBounds(this.hud.getItem("EXIT_GAME_BUTTON").getBounds());
    }

    /**
     * Initializes the graphical components of the logic.
     */
    private void initGraphics() {

        //set clear color and create font_default
        GLES20.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
    }

    /**
     * Initializes the HUD of the logic.
     */
    private void initHUD() {

        //create HUD
        this.hud = new HUD(true);

        //create hud text material
        Material textMaterial = new Material(this.font.getFontSheet(), Global.black, true);

        //fps label
        TextItem fpsLabel = new TextItem(this.font, "FPS: ", textMaterial, 0f, 0f);
        fpsLabel.scale(0.13f);
        this.hud.addItem("FPS_LABEL", fpsLabel, HUD.Placement.BOTTOM_LEFT, 0.02f);

        //fps counter
        TextItem fpsCounter = new TextItem(this.font, "calculating...", textMaterial, 0f, 0f);
        fpsCounter.scale(0.13f);
        this.hud.addItem("FPS_COUNTER", fpsCounter, HUD.Placement.RIGHT_OF_LAST, 0f);

        //wander title
        TextItem title = new TextItem(this.font, "v" + MainActivity.WANDER_VERSION
                + "b" + MainActivity.WANDER_BUILD, textMaterial, 0f, 0f);
        title.scale(0.2f);
        this.hud.addItem("TITLE", title, HUD.Placement.BOTTOM_RIGHT, 0.02f);

        //selection
        TextItem selection = new TextItem(this.font, "Selection:", textMaterial, 0f, 0f);
        selection.scale(0.19f);
        selection.setVisibility(false);
        this.hud.addItem("SELECTION", selection, HUD.Placement.TOP_MIDDLE, 0.13f);

        //selection name
        Material selectionInfoMaterial = new Material(this.font.getFontSheet(), Global.black, true);
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

        //save game button
        ButtonTextItem saveGameButton = new ButtonTextItem(this.font, "Save", Global.white,
                Global.black, WorldLogic.SAVE_BUTTON_ACTION_CODE);
        saveGameButton.scale(0.20f);
        this.hud.addItem("SAVE_GAME_BUTTON", saveGameButton, HUD.Placement.TOP_LEFT, 0.04f);

        //exit game button
        ButtonTextItem exitGameButton = new ButtonTextItem(this.font, "Exit", Global.white,
                Global.black, WorldLogic.EXIT_BUTTON_ACTION_CODE);
        exitGameButton.scale(0.20f);
        this.hud.addItem("EXIT_GAME_BUTTON", exitGameButton, HUD.Placement.BELOW_LAST, 0.04f);

        //game saved notification
        TextItem saveNotification = new TextItem(this.font, "Game Saved!", textMaterial, 0f, 0f);
        saveNotification.scale(0.15f);
        saveNotification.setVisibility(false);
        this.hud.addItem("SAVE_NOTIFICATION", saveNotification, 0f, 0f);
    }

    /**
     * Initialized the world of the logic.
     */
    private void initWorld() {

        //create player
        Player player = this.saveData.getPlayer();

        //create area
        Area area = Area.loadArea(R.raw.area_deepwoods, this.font);

        //create world
        this.world = new World(area, player, this.hud);
    }

    //Data Loading Method
    @Override
    public void loadData(Bundle savedInstanceData) {
        this.savedInstanceData = savedInstanceData;
    }

    /**
     * Reinstates saved bundle data from a previous instance of this logic.
     */
    public void instateSavedInstanceData() {
        if (this.savedInstanceData != null) {
            this.world.instateLoadedData(this.savedInstanceData);
            this.world.getPlayer().setX(Float.parseFloat(this.savedInstanceData.getString("logic_playerx")));
            this.world.getPlayer().setY(Float.parseFloat(this.savedInstanceData.getString("logic_playery")));
            this.hud.instateSavedInstanceData(this.savedInstanceData);
        }
    }

    //Input Method
    @Override
    public boolean input(MotionEvent e) {
        int actionCode = this.hud != null ? this.hud.updateButtonSelections(e) : -1;
        if (actionCode == -1) {
            if (this.control != null) return this.control.input(e, this.world);
        } else if (actionCode == WorldLogic.SAVE_BUTTON_ACTION_CODE) {
            this.saveData.updatePlayer(this.world.getPlayer());
            this.saveData.save(this.world.getArea());
            this.hud.getItem("SAVE_NOTIFICATION").setVisibility(true);
            this.saveNotificationTime = 1000f;
        } else if (actionCode == WorldLogic.EXIT_BUTTON_ACTION_CODE) {
            this.hud.fadeOut();
        }
        return actionCode != -1;
    }

    //Scale Input Method
    @Override
    public boolean scaleInput(float factor) { return this.control.scaleInput(factor,
            this.world.getCamera(), this.world.getPlayer()); }

    //Update Method
    @Override
    public void update(float dt) {

        //fade in
        this.hud.update(dt);

        //update notification text
        if (this.saveNotificationTime >= 0f) {
            this.saveNotificationTime -= dt;
            if (this.saveNotificationTime < 0f) this.hud.getItem("SAVE_NOTIFICATION").setVisibility(false);
        }

        //check for exit
        if (this.hud.fadeOutCompleted()) {
            LogicChangeData lcd = new LogicChangeData(Util.MAIN_MENU_LOGIC_TAG, true, false);
            MainActivity.initLogicChange(lcd, null);
        }

        //update world
        this.world.update(dt);
    }

    /**
     * Updates any FPS tracker or any logic based on FPS
     * @param FPS the current FPS
     */
    public void onFPSUpdate(float FPS) {
        TextItem fpsCounter = (TextItem)this.hud.getItem("FPS_COUNTER");
        fpsCounter.setText(Float.toString(FPS));
    }

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
        data.addChild(this.hud.requestData());
        data.addChild(this.saveData.toNode(this.world.getArea()));
        return data;
    }

    //Cleanup Method
    @Override
    public void cleanup() {
        this.world.cleanup();
        this.hud.cleanup();
    }
}
