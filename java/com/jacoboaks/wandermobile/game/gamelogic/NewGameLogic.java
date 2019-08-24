package com.jacoboaks.wandermobile.game.gamelogic;

import android.opengl.GLES20;
import android.os.Bundle;
import android.view.MotionEvent;

import com.jacoboaks.wandermobile.MainActivity;
import com.jacoboaks.wandermobile.R;
import com.jacoboaks.wandermobile.game.HUD;
import com.jacoboaks.wandermobile.game.gameitem.ButtonTextItem;
import com.jacoboaks.wandermobile.game.gameitem.GameItem;
import com.jacoboaks.wandermobile.game.gameitem.Keyboard;
import com.jacoboaks.wandermobile.game.gameitem.TextItem;
import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.graphics.Model;
import com.jacoboaks.wandermobile.graphics.Texture;
import com.jacoboaks.wandermobile.util.Color;
import com.jacoboaks.wandermobile.util.Global;
import com.jacoboaks.wandermobile.util.Node;
import com.jacoboaks.wandermobile.util.Util;

/**
 * The logic which occurs when a new game is being created.
 */
public class NewGameLogic implements GameLogic {

    //Data
    private HUD hud;
    private Bundle savedData;
    private float fadeTime;
    private String chosenName;

    //Static Data
    private static final int MAX_NAME_LENGTH = 13; //maximum length for a player name
    private static final int MIN_NAME_LENGTH = 3; //minimum length for a player name
    private static final int DONE_BUTTON_ACTION_CODE = 1000;

    //Initialization Method
    @Override
    public void init() {

        //set clear color
        GLES20.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);

        //initialize HUD
        this.initHUD();

        //reload data
        if (this.savedData != null) this.instateLoadedData();
    }

    //HUD Initialization Method
    private void initHUD() {
        this.hud = new HUD();

        //create keyboard
        Keyboard keyboard = new Keyboard(Global.defaultFont, Keyboard.LETTER_ONLY_CHARACTER_SET, new Texture(R.drawable.texture_keyboardbutton),
                new Texture(R.drawable.texture_keyboardbuttonpress), new Texture(R.drawable.texture_keyboardspacebutton),
                new Texture(R.drawable.texture_keyboardspacebuttonpress), 2, 3, 0f, 0f, 1.9f, 0.8f, 0.025f);
        this.hud.addItem("KEYBOARD", keyboard, HUD.Placement.BOTTOM_MIDDLE, 0.05f);

        //create intro text
        Material textMaterial = new Material(Global.defaultFont.getFontSheet(), Global.white, true);
        TextItem introText = new TextItem(Global.defaultFont, "Enter a name:", textMaterial, 0f, 0f);
        introText.scale(0.3f);
        this.hud.addItem("INTRO_TEXT", introText, HUD.Placement.TOP_MIDDLE, 0.15f);

        //create input text
        TextItem inputText = new TextItem(Global.defaultFont, "", textMaterial, 0f, 0f);
        inputText.setText("");
        inputText.scale(0.2f);
        this.hud.addItem("INPUT_TEXT", inputText, HUD.Placement.BELOW_LAST, 0.12f);

        //create notification text
        TextItem notificationText = new TextItem(Global.defaultFont, "Your name should be longer.",
                textMaterial, 0f, 0f);
        notificationText.scale(0.15f);
        notificationText.setVisibility(false);
        this.hud.addItem("NOTIFICATION_TEXT", notificationText, HUD.Placement.BELOW_LAST, 0.12f);

        //create done button
        ButtonTextItem doneButton = new ButtonTextItem(Global.defaultFont, "Done", Global.black,
                Global.white, NewGameLogic.DONE_BUTTON_ACTION_CODE);
        doneButton.scale(0.2f);
        this.hud.addItem("DONE_BUTTON", doneButton, HUD.Placement.TOP_RIGHT, 0.07f);
        doneButton.setY(keyboard.getY() + keyboard.getHeight() / 2 + 0.07f + doneButton.getHeight() / 2);
        doneButton.setX(keyboard.getX() + keyboard.getWidth() / 2 - doneButton.getWidth() / 2);

        //create fading box
        GameItem fadingBox = new GameItem(new Model(Model.getScreenBoxModelCoords(), Model.STD_SQUARE_TEX_COORDS(),
                Model.STD_SQUARE_DRAW_ORDER(), new Material(new Color(0.6f, 0.6f, 0.6f, 1.0f))), 0f, 0f);
        fadingBox.scale(4.0f);
        this.hud.addItem("Z_FADING_BOX", fadingBox, HUD.Placement.MIDDLE, 0f);
        this.fadeTime = Util.FADE_TIME * 2;
    }

    /**
     * Loads any saved data.
     */
    private void instateLoadedData() {

        //load data
        ((TextItem)this.hud.getItem("INPUT_TEXT")).setText(this.savedData.getString("logic_inputText"));
        this.hud.getItem("NOTIFICATION_TEXT").setVisibility(this.savedData.getString("logic_notification").equals("true") ? true : false);
        this.fadeTime = Float.parseFloat(this.savedData.getString("logic_fadeTime"));
        String cn = this.savedData.getString("logic_chosenName");
        this.chosenName = (cn.equals("") ? null : cn);

        //set appropriate fading alpha
        if (this.fadeTime > Util.FADE_TIME) {
            float alpha = (this.fadeTime - Util.FADE_TIME) / Util.FADE_TIME;
            this.hud.getItem("Z_FADING_BOX").getModel().getMaterial().getColor().setA(alpha);
        } else if (this.fadeTime > 0f) {
            float alpha = 1f - (this.fadeTime / Util.FADE_TIME);
            this.hud.getItem("Z_FADING_BOX").getModel().getMaterial().getColor().setA(alpha);
        }
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

                //check if appropriate name, if so start fade
                String name = ((TextItem) this.hud.getItem("INPUT_TEXT")).getText();
                if (name.length() >= NewGameLogic.MIN_NAME_LENGTH) {
                    this.fadeTime = Util.FADE_TIME;
                    this.chosenName = name;
                } else {
                    this.hud.getItem("NOTIFICATION_TEXT").setVisibility(true);
                }

            //check for other button press
            } else {

                //add input if less than maximum length
                if (inputText.getText().length() < NewGameLogic.MAX_NAME_LENGTH) {
                    char c = (char) actionCode;
                    inputText.appendText(Character.toString(c));

                    //update minimum length requirement notification text
                    if (inputText.getText().length() >= NewGameLogic.MIN_NAME_LENGTH) {
                        this.hud.getItem("NOTIFICATION_TEXT").setVisibility(false);
                    }
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

        //if fading in
        if (this.fadeTime > Util.FADE_TIME) {
            float alpha = (this.fadeTime - Util.FADE_TIME) / Util.FADE_TIME;
            this.hud.getItem("Z_FADING_BOX").getModel().getMaterial().getColor().setA(alpha);
            this.fadeTime -= dt;

        //if fading out
        } else if (this.fadeTime > 0f && this.chosenName != null) {
            float alpha = 1f - (this.fadeTime / Util.FADE_TIME);
            this.hud.getItem("Z_FADING_BOX").getModel().getMaterial().getColor().setA(alpha);
            this.fadeTime -= dt;

            //change logics if fade over
            if (this.fadeTime < 0f) {
                LogicChangeData lgd = new LogicChangeData(Util.WORLD_LOGIC_TAG, true, false);
                MainActivity.initLogicChange(lgd, new Node(chosenName, chosenName));
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
        Node data = new Node("logic", Util.NEW_GAME_LOGIC_TAG);
        data.addChild(new Node("inputText", ((TextItem)this.hud.getItem("INPUT_TEXT")).getText()));
        data.addChild(new Node("notification", this.hud.getItem("NOTIFICATION_TEXT").isVisible() ? "true" : "false"));
        data.addChild(new Node("fadeTime", Float.toString(this.fadeTime)));
        data.addChild(new Node("chosenName", this.chosenName == null ? "" : this.chosenName));
        return data;
    }

    //Cleanup Method
    @Override
    public void cleanup() {
        this.hud.cleanup();
    }
}
