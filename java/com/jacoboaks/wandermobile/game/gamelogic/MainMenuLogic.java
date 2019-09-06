package com.jacoboaks.wandermobile.game.gamelogic;

import android.opengl.GLES20;
import android.os.Bundle;
import android.view.MotionEvent;

import com.jacoboaks.wandermobile.MainActivity;
import com.jacoboaks.wandermobile.R;
import com.jacoboaks.wandermobile.game.HUD;
import com.jacoboaks.wandermobile.game.gameitem.ButtonTextItem;
import com.jacoboaks.wandermobile.game.gameitem.GameItem;
import com.jacoboaks.wandermobile.game.gameitem.TextItem;
import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.graphics.Model;
import com.jacoboaks.wandermobile.util.Color;
import com.jacoboaks.wandermobile.util.Global;
import com.jacoboaks.wandermobile.util.Node;
import com.jacoboaks.wandermobile.util.Util;

/**
 * Contains the logic for the MainMenu of the game.
 */
public class MainMenuLogic implements GameLogic {

    //Data
    private Font font;
    private HUD hud;
    private Bundle savedInstanceData;
    private int chosenAction;

    //Action Codes
    private static final int NEW_GAME_BUTTON_ACTION_CODE = 1;
    private static final int LOAD_GAME_BUTTON_ACTION_CODE = 2;
    private static final int EXIT_BUTTON_ACTION_CODE = 3;

    //Initialization Method
    @Override
    public void init() {

        //set clear color
        GLES20.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);

        //create font and hud
        this.font = new Font(Global.defaultFontID, Global.defaultFontCuttoffsID, 10, 10, ' ');
        this.initHUD();
    }

    /**
     * Initializes and populates this logic's HUD.
     */
    private void initHUD() {
        this.hud = new HUD(true);

        //create title and add to hud
        TextItem title = new TextItem(this.font, "Wander Mobile", new Material(this.font.getFontSheet(),
                Global.white, true), 0f, 0f);
        title.scale(0.42f);
        this.hud.addItem("TITLE", title, HUD.Placement.TOP_MIDDLE, 0.19f);

        //add version and build tag
        TextItem bvTag = new TextItem(this.font, "v" + MainActivity.WANDER_VERSION + "b" + MainActivity.WANDER_BUILD,
                title.getModel().getMaterial(), 0f, 0f);
        bvTag.scale(0.16f);
        this.hud.addItem("BUILD_VERSION_TAG", bvTag, HUD.Placement.BELOW_LAST, 0.0f);
        bvTag.moveX(-(title.getWidth() / 2 - bvTag.getWidth() / 2));

        //create new game button and add to hud
        ButtonTextItem newGameButton = new ButtonTextItem(this.font, "New Game",
                Global.black, Global.white, MainMenuLogic.NEW_GAME_BUTTON_ACTION_CODE);
        newGameButton.scale(0.26f);
        this.hud.addItem("NEW_GAME_BUTTON", newGameButton, HUD.Placement.MIDDLE, 0.0f);

        //create load game button and add to hud
        ButtonTextItem loadGameButton = new ButtonTextItem(this.font, "Load Game",
                Global.black, Global.white, MainMenuLogic.LOAD_GAME_BUTTON_ACTION_CODE);
        loadGameButton.scale(0.26f);
        this.hud.addItem("LOAD_GAME_BUTTON", loadGameButton, HUD.Placement.BELOW_LAST, 0.1f);

        //create exit button and add to hud
        ButtonTextItem exitButton = new ButtonTextItem(this.font, "Exit",
                Global.black, Global.white, MainMenuLogic.EXIT_BUTTON_ACTION_CODE);
        exitButton.scale(0.26f);
        this.hud.addItem("EXIT_BUTTON", exitButton, HUD.Placement.BELOW_LAST, 0.1f);
    }

    //Data Loading Method
    @Override
    public void loadData(Bundle savedInstanceData) { this.savedInstanceData = savedInstanceData; }

    //Saved Instance Data Instating Method
    public void instateSavedInstanceData() {
        if (this.savedInstanceData != null) {
            this.hud.instateSavedInstanceData(savedInstanceData);
            if (this.hud.fadingOut()) this.chosenAction = Integer.parseInt(this.savedInstanceData.getString("logic_chosenAction"));
        }
    }

    //Input Handling Method
    @Override
    public boolean input(MotionEvent e) {

        //figure out the action code
        int actionCode = this.hud != null ? this.hud.updateButtonSelections(e) : -1;

        //check if button was pressed and switch to world logic if so
        switch(actionCode) {
            case MainMenuLogic.NEW_GAME_BUTTON_ACTION_CODE:
                this.hud.fadeOut();
                this.chosenAction = actionCode;
                break;
            case MainMenuLogic.LOAD_GAME_BUTTON_ACTION_CODE:
                this.hud.fadeOut();
                this.chosenAction = actionCode;
                break;
            case MainMenuLogic.EXIT_BUTTON_ACTION_CODE:
                System.exit(0);
                break;
            default: //return false if no action codes were returned
                return false;
        }

        //return true if button was pressed
        return true;
    }

    //Scale Input Method
    @Override
    public boolean scaleInput(float factor) {
        return false;
    }

    //Update Method
    @Override
    public void update(float dt) {

        //update hud
        this.hud.update(dt);

        //switch logic if fade completed
        if (this.hud.fadeOutCompleted()) {
            LogicChangeData lgd = null;
            Node transferData = new Node("transferData");
            if (this.chosenAction == MainMenuLogic.NEW_GAME_BUTTON_ACTION_CODE)
                lgd = new LogicChangeData(Util.NEW_GAME_LOGIC_TAG, true, false);
            else if (this.chosenAction == MainMenuLogic.LOAD_GAME_BUTTON_ACTION_CODE) {
                lgd = new LogicChangeData(Util.SAVE_SLOT_CHOICE_LOGIC_TAG, true, false);
                transferData.addChild("chosenName", "");
                transferData.addChild("neworload", "load");
            }
            MainActivity.initLogicChange(lgd, transferData);
        }
    }

    //Render Method
    @Override
    public void render() {
        this.hud.render();
    }

    //Data Requesting Method
    @Override
    public Node requestData() {
        Node node = new Node("logic", Util.MAIN_MENU_LOGIC_TAG);
        if (this.hud.fadingOut()) node.addChild("chosenAction", Integer.toString(this.chosenAction));
        node.addChild(this.hud.requestData());
        return node;
    }

    //Cleanup Method
    @Override
    public void cleanup() { this.hud.cleanup(); }
}
