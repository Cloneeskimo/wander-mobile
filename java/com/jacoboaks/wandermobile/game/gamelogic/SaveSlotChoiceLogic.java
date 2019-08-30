package com.jacoboaks.wandermobile.game.gamelogic;

import android.opengl.GLES20;
import android.os.Bundle;
import android.view.MotionEvent;

import com.jacoboaks.wandermobile.MainActivity;
import com.jacoboaks.wandermobile.game.HUD;
import com.jacoboaks.wandermobile.game.SaveData;
import com.jacoboaks.wandermobile.game.gameitem.ButtonTextItem;
import com.jacoboaks.wandermobile.game.gameitem.Entity;
import com.jacoboaks.wandermobile.game.gameitem.TextItem;
import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.util.Color;
import com.jacoboaks.wandermobile.util.Global;
import com.jacoboaks.wandermobile.util.Node;
import com.jacoboaks.wandermobile.util.Util;

/**
 * Allows the user to select a save slot (1, 2, or 3) either for creating a new game in or for
 * loading a previous game.
 */
public class SaveSlotChoiceLogic implements GameLogic {

    //Data
    private Bundle savedInstanceData;
    private Node transferData;
    private HUD hud;
    private Font font;
    private String chosenName;
    private boolean load;

    //Initialization Method
    @Override
    public void init() {

        //get transfer data
        Node transferData = MainActivity.getLogicTransferData();
        this.chosenName = transferData.getChild("chosenName").getValue();
        this.load = transferData.getChild("neworload").getValue().equals("load") ? true : false;

        //create font, hud, and set clear color
        GLES20.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
        this.font = new Font(Global.defaultFontID, Global.defaultFontCuttoffsID, 10, 10, ' ');
        this.initHUD();
    }

    /**
     * Initializes the HUd for this SaveSlotChoiceLogic.
     */
    private void initHUD() {

        //create hud and text material
        this.hud = new HUD(true);
        Material textMaterial = new Material(this.font.getFontSheet(), Global.white, true);

        //screen title
        TextItem screenTitle = new TextItem(this.font, "Choose a slot" + (this.chosenName.equals("null") ? "" : " for " + this.chosenName),
                textMaterial, 0f, 0f);
        screenTitle.scale(0.19f);
        this.hud.addItem("SCREEN_TITLE", screenTitle, HUD.Placement.TOP_MIDDLE, 0.2f);

        //create hud for each slot
        for (int i = 0; i < 3; i++) {

            //create slot title
            ButtonTextItem slotTitle = new ButtonTextItem(this.font, "Slot " + i + " (" +
                    (MainActivity.saveSlots[i] ? "in use)" : "empty)"), Global.black, Global.yellow, i);
            slotTitle.scale(0.16f);
            this.hud.addItem("SLOT_TITLE_" + i, slotTitle, 0f, 0f + ((i - 1) * 0.3f));
        }
    }

    //Data Loading Method
    @Override
    public void loadData(Bundle savedInstanceData) {
        this.savedInstanceData = savedInstanceData;
    }

    //Saved Data Instating Method
    @Override
    public void instateSavedInstanceData() {
        if (this.savedInstanceData != null) this.hud.instateSavedInstanceData(this.savedInstanceData);
    }

    //Input Method
    @Override
    public boolean input(MotionEvent e) {
        int actionCode = this.hud.updateButtonSelections(e);

        //player has selected a slot
        if (actionCode >= 0 && actionCode <= 2) {

            //load game
            if (this.load) {
                if (MainActivity.saveSlots[actionCode]) this.loadGame(actionCode);

            //new game
            } else this.newGame(actionCode);
        }
        return (actionCode != -1);
    }

    /**
     * Loads a previous game from the given save slot.
     * @param saveSlot the save slot to load the previous game from.
     */
    private void loadGame(int saveSlot) {
        Node saveDataNode = Node.readNode("saveslot" + saveSlot);
        this.transferData = new Node("transferdata");
        this.transferData.addChild(saveDataNode);
        this.hud.fadeOut();
    }

    /**
     * Creates a new game in the given save slot.
     * @param saveSlot the save slot to put the new game into.
     */
    private void newGame(int saveSlot) {
        Entity player = new Entity(this.chosenName, this.font, this.chosenName.charAt(0),
                new Color(0.62f, 0.0f, 0.1f, 1.0f), 0, 0);
        SaveData saveData = new SaveData(player, saveSlot);
        saveData.save();
        this.transferData = new Node("transferdata");
        this.transferData.addChild(saveData.toNode());
        this.hud.fadeOut();
    }

    //Scale Input Method
    @Override
    public boolean scaleInput(float factor) {
        return false;
    }

    //Update Method
    @Override
    public void update(float dt) {
        this.hud.update(dt);

        //switch logic if fade completed
        if (this.hud.fadeOutCompleted()) {
            LogicChangeData lcd = new LogicChangeData(Util.WORLD_LOGIC_TAG, true, false);
            MainActivity.initLogicChange(lcd, this.transferData);
        }
    }

    //Render Method
    @Override
    public void render() { this.hud.render(); }

    //Data Requesting Method
    @Override
    public Node requestData() {
        Node node = new Node("logic", Util.SAVE_SLOT_CHOICE_LOGIC_TAG);
        node.addChild(this.hud.requestData());
        return node;
    }

    //Cleanup Method
    @Override
    public void cleanup() { this.hud.cleanup(); }
}
