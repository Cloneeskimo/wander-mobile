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
    private int deletingSlot = -1;

    //Initialization Method
    @Override
    public void init() {

        //get transfer data
        this.chosenName = "";
        Node transferData = MainActivity.getLogicTransferData();
        boolean transferred = false;
        if (transferData != null) {
            Node chosenNameNode = transferData.getChild("chosenName");
            if (chosenNameNode != null) {
                transferred = true;
                this.chosenName = chosenNameNode.getValue();
                this.load = transferData.getChild("neworload").getValue().equals("load") ? true : false;
            }
        }

        //attempt to load from instance data if no transfer data present
        if (!transferred) {
            this.chosenName = this.savedInstanceData.getString("logic_chosenName");
            this.load = Boolean.parseBoolean(this.savedInstanceData.getString("logic_load"));
        }

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
        TextItem screenTitle = new TextItem(this.font, this.load ? "Choose a slot to load" :
                "Choose a slot for " + this.chosenName, textMaterial, 0f, 0f);
        screenTitle.scale(0.19f);
        this.hud.addItem("SCREEN_TITLE", screenTitle, HUD.Placement.TOP_MIDDLE, 0.2f);

        //create hud for each slot
        for (int i = 0; i < 3; i++) {

            //create slot title
            ButtonTextItem slotTitle = new ButtonTextItem(this.font, "Slot " + i + " (" +
                    (MainActivity.saveSlots[i] ? "in use)" : "empty)"), Global.black, Global.yellow, i);
            slotTitle.scale(0.17f);
            this.hud.addItem("SLOT_TITLE_" + i, slotTitle, 0f, 0f + ((i - 1) * 0.3f));

            //create extras
            if (MainActivity.saveSlots[i]) {

                //player info
                SaveData data = new SaveData(i, this.font);
                TextItem playerInfo = new TextItem(this.font, data.getPlayer().getName() + "(lv." +
                        data.getPlayer().getLevel() + ")", textMaterial, 0f, 0f);
                playerInfo.scale(0.14f);
                this.hud.addItem("PLAYER_INFO_" + i, playerInfo, HUD.Placement.BELOW_LAST, 0.02f);

                //delete button
                ButtonTextItem delete = new ButtonTextItem(this.font, "delete",  new Color(0.5f, 0.1f, 0.1f, 1.0f),
                        new Color(0.75f, 0.1f, 0.1f, 1.0f), i + 3);
                delete.scale(0.136f);
                this.hud.addItem("DELETE_" + i, delete, HUD.Placement.BELOW_LAST, 0.04f);
            }
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
        if (this.hud.fadingOut()) {
            this.loadGame(Integer.parseInt(this.savedInstanceData.getString("logic_chosenSlot")));
        }
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

        //player has chosen to delete a slot
        } else if (actionCode >= 3 && actionCode <= 5) {

            //fade out and flag for deletion
            this.hud.fadeOut();
            this.deletingSlot = actionCode - 3;
        }
        return (actionCode != -1);
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

            //if player chose to delete
            if (this.deletingSlot >= 0) {
                Node tData = new Node("transferData");
                tData.addChild("slot", Integer.toString( this.deletingSlot));
                LogicChangeData lcd = new LogicChangeData(Util.DELETE_SLOT_LOGIC_TAG, true, true);
                MainActivity.initLogicChange(lcd, tData);
            }

            //if player chose a slot instead
            else {
                LogicChangeData lcd = new LogicChangeData(Util.WORLD_LOGIC_TAG, true, false);
                MainActivity.initLogicChange(lcd, this.transferData);
            }
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
        if (this.hud.fadingOut()) node.addChild("chosenSlot", this.transferData.getChild("savedata").getChild("saveSlot").getValue());
        node.addChild("load", Boolean.toString(this.load));
        node.addChild("chosenName", this.chosenName);
        return node;
    }

    //Cleanup Method
    @Override
    public void cleanup() { this.hud.cleanup(); }
}
