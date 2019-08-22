package com.jacoboaks.wandermobile.game.gamelogic;

import android.opengl.GLES20;
import android.os.Bundle;
import android.view.MotionEvent;

import com.jacoboaks.wandermobile.MainActivity;
import com.jacoboaks.wandermobile.R;
import com.jacoboaks.wandermobile.game.HUD;
import com.jacoboaks.wandermobile.game.gameitem.ButtonTextItem;
import com.jacoboaks.wandermobile.game.gameitem.Keyboard;
import com.jacoboaks.wandermobile.game.gameitem.TextItem;
import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.graphics.Texture;
import com.jacoboaks.wandermobile.util.Color;
import com.jacoboaks.wandermobile.util.Node;
import com.jacoboaks.wandermobile.util.Util;

/**
 * The logic which occurs when a new game is being created.
 */
public class NewGameLogic implements GameLogic {

    //Data
    private HUD hud;
    private Font font;
    private Bundle savedData;

    //Static Data
    private static final int MAX_NAME_LENGTH = 12; //maximum length for a player name
    private static final int DONE_BUTTON_ACTION_CODE = 1000;

    //Initialization Method
    @Override
    public void init() {

        //set clear color
        GLES20.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);

        //create font and initialize HUD
        this.font = new Font(R.drawable.font_default, R.raw.fontcuttoffs_default,10, 10, ' ');
        this.initHUD();
    }

    //HUD Initialization Method
    private void initHUD() {
        this.hud = new HUD();

        //create keyboard
        Keyboard keyboard = new Keyboard(this.font, Keyboard.LETTER_ONLY_CHARACTER_SET, new Texture(R.drawable.texture_keyboardbutton),
                new Texture(R.drawable.texture_keyboardbuttonpress), new Texture(R.drawable.texture_keyboardspacebutton),
                new Texture(R.drawable.texture_keyboardspacebuttonpress), 3, 3, 0f, 0f, 1.9f, 1.0f, 0.03f);
        this.hud.addItem("KEYBOARD", keyboard, HUD.Placement.BOTTOM_MIDDLE, 0.05f);

        //create intro text
        Material textMaterial = new Material(this.font.getFontSheet(), new Color(0.0f, 0.0f, 0.0f, 1.0f), true);
        TextItem introText = new TextItem(this.font, "Enter a name:", textMaterial, 0f, 0f);
        introText.scale(0.3f);
        this.hud.addItem("INTRO_TEXT", introText, HUD.Placement.TOP_MIDDLE, 0.15f);

        //create input text
        TextItem inputText = new TextItem(this.font, "", textMaterial, 0f, 0f);
        inputText.scale(0.2f);
        inputText.setText(this.savedData == null ? "" : this.savedData.getString("logic_inputText"));
        this.hud.addItem("INPUT_TEXT", inputText, HUD.Placement.BELOW_LAST, 0.15f);

        //create done button
        ButtonTextItem doneButton = new ButtonTextItem(this.font, "Done", new Color(0.0f, 0.0f, 0.0f, 1.0f), new Color(1.0f, 1.0f, 1.0f, 1.0f),
                NewGameLogic.DONE_BUTTON_ACTION_CODE);
        doneButton.scale(0.2f);
        this.hud.addItem("DONE_BUTTON", doneButton, HUD.Placement.TOP_RIGHT, 0.07f);
    }

    //Data Loading Method
    @Override
    public void loadData(Bundle savedInstanceData) {
        this.savedData = savedInstanceData;
    }

    //Input Method
    @Override
    public boolean input(MotionEvent e) {

        //get action code input
        int actionCode = this.hud.updateButtonSelections(e);
        if (actionCode != -1) {

            //get input text handle
            TextItem inputText = (TextItem)this.hud.getItem("INPUT_TEXT");

            //check for delete press
            if (actionCode == Keyboard.DELETE_ACTION_CODE) {
                inputText.removeLastChar();

            //check for done button press
            } else if (actionCode == NewGameLogic.DONE_BUTTON_ACTION_CODE) {

                //done giving name
                String name = ((TextItem) this.hud.getItem("INPUT_TEXT")).getText();
                LogicChangeData lgd = new LogicChangeData(Util.WORLD_LOGIC_TAG, true, false);
                MainActivity.initLogicChange(lgd, new Node(name, name));


            //check for other button press
            } else {
                if (inputText.getText().length() < NewGameLogic.MAX_NAME_LENGTH) {
                    char c = (char) actionCode;
                    inputText.appendText(Character.toString(c));
                }
            }
        }

        //return whether input was handled
        return (actionCode == -1);
    }

    //Scale Input Method
    @Override
    public boolean scaleInput(float factor) {
        return false;
    }

    //Update Method
    public void update(float dt) {
    }

    //Render Method
    @Override
    public void render() {
        this.hud.render();
    }

    //Data Requesting Method
    @Override
    public Node requestData() {
        Node data = new Node("logic", Util.NEW_GAME_LOGIC_TAG);
        data.addChild(new Node("inputText", ((TextItem)this.hud.getItem("INPUT_TEXT")).getText()));
        return data;
    }

    //Cleanup Method
    @Override
    public void cleanup() {
        this.hud.cleanup();
    }
}
