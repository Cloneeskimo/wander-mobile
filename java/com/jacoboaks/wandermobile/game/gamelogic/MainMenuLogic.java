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
import com.jacoboaks.wandermobile.graphics.Transformation;
import com.jacoboaks.wandermobile.util.Color;
import com.jacoboaks.wandermobile.util.Coord;
import com.jacoboaks.wandermobile.util.Node;
import com.jacoboaks.wandermobile.util.Util;

/**
 * Contains the logic for the MainMenu of the game.
 */
public class MainMenuLogic implements GameLogic {

    //Data
    private HUD hud;
    private Font font;
    private float fadeOutTime = 0f;
    private int chosenAction;

    //Button Data
    private static final int NEW_GAME_BUTTON_ACTION_CODE = 1;
    private static final int LOAD_GAME_BUTTON_ACTION_CODE = 2;
    private static final int EXIT_BUTTON_ACTION_CODE = 3;

    //Initialization Method
    @Override
    public void init() {

        //set clear color
        GLES20.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);

        //create font_default and hud
        this.font = new Font(R.drawable.font_default, R.raw.fontcuttoffs_default,10, 10, ' ');
        this.initHUD();
    }

    /**
     * Initializes and populates this logic's HUD.
     */
    private void initHUD() {
        this.hud = new HUD();

        //create title and add to hud
        TextItem title = new TextItem(this.font, "Wander Mobile", new Material(this.font.getFontSheet(),
                new Color(1.0f, 1.0f, 1.0f, 1.0f), true), 0f, 0f);
        title.scale(0.42f);
        this.hud.addItem("TITLE", title, HUD.Placement.TOP_MIDDLE, 0.19f);

        //add version and build tag
        TextItem bvTag = new TextItem(this.font, "v" + MainActivity.WANDER_VERSION + "b" + MainActivity.WANDER_BUILD,
                title.getModel().getMaterial(), 0f, 0f);
        bvTag.scale(0.16f);
        this.hud.addItem("BUILD_VERSION_TAG", bvTag, HUD.Placement.BELOW_LAST, 0.05f);

        //create load game button and add to hud
        ButtonTextItem loadGameButton = new ButtonTextItem(this.font, "Load Game",
                new Color(0.0f, 0.0f, 0.0f, 1.0f), new Color(1.0f, 1.0f, 1.0f, 1.0f),
                MainMenuLogic.LOAD_GAME_BUTTON_ACTION_CODE);
        loadGameButton.scale(0.26f);
        this.hud.addItem("LOAD_GAME_BUTTON", loadGameButton, HUD.Placement.MIDDLE, 0f);

        //create new game button and add to hud
        ButtonTextItem newGameButton = new ButtonTextItem(this.font, "New Game",
                new Color(0.0f, 0.0f, 0.0f, 1.0f), new Color(1.0f, 1.0f, 1.0f, 1.0f),
                MainMenuLogic.NEW_GAME_BUTTON_ACTION_CODE);
        newGameButton.scale(0.26f);
        this.hud.addItem("NEW_GAME_BUTTON", newGameButton, HUD.Placement.ABOVE_LAST, 0.1f);
        this.hud.recenter("NEW_GAME_BUTTON");

        //create exit button and add to hud
        ButtonTextItem exitButton = new ButtonTextItem(this.font, "Exit",
                new Color(0.0f, 0.0f, 0.0f, 1.0f), new Color(1.0f, 1.0f, 1.0f, 1.0f),
                MainMenuLogic.EXIT_BUTTON_ACTION_CODE);
        exitButton.scale(0.26f);
        this.hud.addItem("EXIT_BUTTON", exitButton, HUD.Placement.BELOW_LAST, 0.2f + loadGameButton.getHeight());
        this.hud.recenter("EXIT_BUTTON");

        //create fading box
        GameItem fadingBox = new GameItem(new Model(Model.getScreenBoxModelCoords(), Model.STD_SQUARE_TEX_COORDS(),
                Model.STD_SQUARE_DRAW_ORDER(), new Material(new Color(0.6f, 0.6f, 0.6f, 0.0f))), 0f, 0f);
        fadingBox.scale(4.0f);
        this.hud.addItem("Z_FADING_BOX", fadingBox, HUD.Placement.MIDDLE, 0f);
    }

    //Data Loading Method
    @Override
    public void loadData(Bundle savedInstanceData) {

    }

    //Input Handling Method
    @Override
    public boolean input(MotionEvent e) {

        //figure out the action code
        ButtonTextItem button = (ButtonTextItem)this.hud.getItem("NEW_GAME_BUTTON");
        int actionCode = button.updateSelection(e);
        if (actionCode == -1) button = (ButtonTextItem)this.hud.getItem("LOAD_GAME_BUTTON");
        actionCode = button.updateSelection(e);
        if (actionCode == -1) button = (ButtonTextItem)this.hud.getItem("EXIT_BUTTON");
        actionCode = button.updateSelection(e);

        //check if button was pressed and switch to world logic if so
        if (actionCode == MainMenuLogic.NEW_GAME_BUTTON_ACTION_CODE) {
            this.fadeOutTime = Util.FADE_TIME;
            this.chosenAction = actionCode;
            return true;
        } else if (actionCode == MainMenuLogic.LOAD_GAME_BUTTON_ACTION_CODE) {
            this.fadeOutTime = Util.FADE_TIME;
            this.chosenAction = actionCode;
            return true;
        } else if (actionCode == MainMenuLogic.EXIT_BUTTON_ACTION_CODE) {
            System.exit(0);
            return true;
        }

        //return false if button was not pressed
        return false;
    }

    //Scale Input Method
    @Override
    public boolean scaleInput(float factor) {
        return false;
    }

    //Update Method
    @Override
    public void update(float dt) {

        //fade out at end
        if (this.fadeOutTime > 0f) {
            float alpha = 1f - (this.fadeOutTime / Util.FADE_TIME);
            this.hud.getItem("Z_FADING_BOX").getModel().getMaterial().getColor().setA(alpha);
            this.fadeOutTime -= dt;

            //switch logic if fade transition is over
            if (this.fadeOutTime < 0f) {
                LogicChangeData lgd = null;
                if (this.chosenAction == MainMenuLogic.NEW_GAME_BUTTON_ACTION_CODE)
                    lgd = new LogicChangeData(Util.NEW_GAME_LOGIC_TAG, true, false);
                else if (this.chosenAction == MainMenuLogic.LOAD_GAME_BUTTON_ACTION_CODE)
                    lgd = new LogicChangeData(Util.LOAD_GAME_LOGIC_TAG, true, false);
                MainActivity.initLogicChange(lgd, null);
            }
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
        Node data = new Node("logic", Util.MAIN_MENU_LOGIC_TAG);
        return data;
    }

    //Cleanup Method
    @Override
    public void cleanup() {
        this.hud.cleanup();;
    }
}
