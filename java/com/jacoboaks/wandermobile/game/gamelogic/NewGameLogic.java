package com.jacoboaks.wandermobile.game.gamelogic;

import android.os.Bundle;
import android.view.MotionEvent;

import com.jacoboaks.wandermobile.R;
import com.jacoboaks.wandermobile.game.HUD;
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

    //Initialization Method
    @Override
    public void init() {

        //create hud and font
        this.hud = new HUD();
        this.font = new Font(R.drawable.font_default, R.raw.fontcuttoffs_default,10, 10, ' ');

        //create keyboard
        Keyboard keyboard = new Keyboard(this.font, Keyboard.LETTER_ONLY_CHARACTER_SET, new Texture(R.drawable.texture_keyboardbutton),
                new Texture(R.drawable.texture_keyboardbuttonpress), new Texture(R.drawable.texture_keyboardspacebutton),
                new Texture(R.drawable.texture_keyboardspacebuttonpress), 3, 3, 0f, 0f, 1.9f, 1.0f, 0.03f);

        this.hud.addItem("KEYBOARD", keyboard, HUD.Placement.BOTTOM_MIDDLE, 0.05f);

        //create inout text
        TextItem inputText = new TextItem(font, "", new Material(font.getFontSheet(), new Color(0.0f, 0.0f, 0.0f, 1.0f), true),
                0f, 0f);
        inputText.scale(0.2f);
        inputText.setText("");
        this.hud.addItem("INPUT_TEXT", inputText, HUD.Placement.TOP_MIDDLE, 0.15f);
    }

    //Data Loading Method
    @Override
    public void loadData(Bundle savedInstanceData) {

    }

    //Input Method
    @Override
    public boolean input(MotionEvent e) {
        int actionCode = this.hud.updateButtonSelections(e);
        if (actionCode != -1) {
            if (actionCode == Keyboard.DELETE_ACTION_CODE) {
                ((TextItem)this.hud.getItem("INPUT_TEXT")).removeLastChar();
            } else {
                char c = (char) actionCode;
                ((TextItem) this.hud.getItem("INPUT_TEXT")).appendText(Character.toString(c));
            }
        }
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
        return data;
    }

    //Cleanup Method
    @Override
    public void cleanup() {
        this.hud.cleanup();
    }
}
