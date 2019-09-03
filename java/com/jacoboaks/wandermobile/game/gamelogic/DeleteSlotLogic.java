package com.jacoboaks.wandermobile.game.gamelogic;

import android.opengl.GLES20;
import android.os.Bundle;
import android.view.MotionEvent;

import com.jacoboaks.wandermobile.MainActivity;
import com.jacoboaks.wandermobile.game.HUD;
import com.jacoboaks.wandermobile.game.gameitem.ButtonTextItem;
import com.jacoboaks.wandermobile.game.gameitem.TextItem;
import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.util.Global;
import com.jacoboaks.wandermobile.util.Node;
import com.jacoboaks.wandermobile.util.Util;

import java.io.File;

/**
 * Confirms a user's intentions to delete a saved game in a certain slot
 */
public class DeleteSlotLogic implements GameLogic {

    //Data
    private HUD hud;
    private Font font;
    private Bundle savedInstanceData;
    private int slot;
    private boolean yesChosen = false;

    //Static Data
    private static final int YES_ACTION_CODE = 0;
    private static final int NO_ACTION_CODE = 1;

    //Initialization Method
    @Override
    public void init() {

        //set clear color and font
        GLES20.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
        this.font = new Font(Global.defaultFontID, Global.defaultFontCuttoffsID, 10, 10, ' ');

        //get transfer data
        this.slot = Integer.parseInt(MainActivity.getLogicTransferData().getChild("slot").getValue());

        //create hud
        this.hud = new HUD(true);
        Material textMaterial = new Material(this.font.getFontSheet(), Global.white, true);

        //Title
        TextItem title = new TextItem(this.font, "Are you sure?", textMaterial, 0f, 0f);
        title.scale(0.3f);
        this.hud.addItem("TITLE", title, HUD.Placement.TOP_MIDDLE, 0.05f);

        //Yes Button
        ButtonTextItem yes = new ButtonTextItem(this.font, "yes", Global.black, Global.white,
                YES_ACTION_CODE);
        yes.scale(0.23f);
        this.hud.addItem("YES_BUTTON", yes, HUD.Placement.MIDDLE, 0f);

        //No Button
        ButtonTextItem no = new ButtonTextItem(this.font, "no", Global.black, Global.white,
                NO_ACTION_CODE);
        no.scale(0.23f);
        this.hud.addItem("NO_BUTTON", no, HUD.Placement.BELOW_LAST, 0.2f);
    }

    //Data Loading Method
    @Override
    public void loadData(Bundle savedInstanceData) {
        this.savedInstanceData = savedInstanceData;
    }

    //Instance Data Instating Method
    @Override
    public void instateSavedInstanceData() {
        if (this.savedInstanceData != null) {
            this.hud.instateSavedInstanceData(this.savedInstanceData);
            if (this.hud.fadingOut()) this.yesChosen = Boolean.parseBoolean(this.savedInstanceData.getString("logic_yesChosen"));
        }
    }

    //Input Method
    @Override
    public boolean input(MotionEvent e) {

        //check if yes or no pressed
        int actionCode = this.hud.updateButtonSelections(e);
        if (actionCode == YES_ACTION_CODE) {
            this.yesChosen = true;
            this.hud.fadeOut();
        } else if (actionCode == NO_ACTION_CODE) {
            this.yesChosen = false;
            this.hud.fadeOut();
        } else return false;

        //return if handled
        return true;
    }

    //Scale Input Method
    @Override
    public boolean scaleInput(float factor) { return false; }

    //Update Method
    @Override
    public void update(float dt) {

        //update hud
        this.hud.update(dt);

        //change logic if fade completed
        if (this.hud.fadeOutCompleted()) {

            //delete
            if (this.yesChosen) {

                File saveFile = new File(MainActivity.appDir, "saveslot" + this.slot);
                saveFile.delete();
                MainActivity.saveSlots[this.slot] = false;
                LogicChangeData lcd = new LogicChangeData(Util.SAVE_SLOT_CHOICE_LOGIC_TAG, true, false);
                MainActivity.initLogicChange(lcd, null);

            //do not delete
            } else {
                LogicChangeData lcd = new LogicChangeData(Util.SAVE_SLOT_CHOICE_LOGIC_TAG, true, false);
                MainActivity.initLogicChange(lcd, null);
            }
        }
    }

    //Render Method
    @Override
    public void render() { this.hud.render(); }

    //Data Requesting Method
    @Override
    public Node requestData() {
        Node node = new Node("logic", Util.DELETE_SLOT_LOGIC_TAG);
        node.addChild(this.hud.requestData());
        if (this.hud.fadingOut()) node.addChild("yesChosen", Boolean.toString(this.yesChosen));
        return node;
    }

    //Cleanup Method
    @Override
    public void cleanup() { this.hud.cleanup(); }
}
