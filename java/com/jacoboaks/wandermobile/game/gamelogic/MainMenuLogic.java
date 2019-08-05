package com.jacoboaks.wandermobile.game.gamelogic;

import android.opengl.GLES20;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;

import com.jacoboaks.wandermobile.MainActivity;
import com.jacoboaks.wandermobile.R;
import com.jacoboaks.wandermobile.game.HUD;
import com.jacoboaks.wandermobile.game.gameitem.ButtonTextItem;
import com.jacoboaks.wandermobile.game.gameitem.TextItem;
import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.util.Color;
import com.jacoboaks.wandermobile.util.Node;
import com.jacoboaks.wandermobile.util.Util;

/**
 * Contains the logic for the MainMenu of the game.
 */
public class MainMenuLogic implements GameLogic {

    //Data
    private HUD hud;
    private Font font;

    //Button Data
    private static final int PLAY_BUTTON_ACTION_CODE = 1;

    //Initialization Method
    @Override
    public void init() {

        //set clear color
        GLES20.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);

        //create font and hud
        this.font = new Font(R.drawable.font, R.raw.fontcutoffs,10, 10, ' ');
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
        title.scale(0.4f);
        this.hud.addItem(title, HUD.Placement.TOP_MIDDLE, 0.19f);

        //add version and build tag
        TextItem bvTag = new TextItem(this.font, "v" + MainActivity.WANDER_VERSION + "b" + MainActivity.WANDER_BUILD,
                title.getModel().getMaterial(), 0f, 0f);
        bvTag.scale(0.14f);
        this.hud.addItem(bvTag, HUD.Placement.BELOW_LAST, 0.05f);

        //create button and add to hud
        ButtonTextItem playButton = new ButtonTextItem(this.font, "Play",
                new Color(0.0f, 0.0f, 0.0f, 1.0f), new Color(1.0f, 1.0f, 1.0f, 1.0f),
                MainMenuLogic.PLAY_BUTTON_ACTION_CODE);
        playButton.scale(0.24f);
        this.hud.addItem(playButton, HUD.Placement.MIDDLE, 0f);
    }

    //Data Loading Method
    @Override
    public void loadData(Bundle savedInstanceData) {

    }

    //Input Handling Method
    @Override
    public boolean input(MotionEvent e) {

        //apply input data to play button
        ButtonTextItem playButton = (ButtonTextItem)this.hud.getItem(2);
        int actionCode = playButton.updateSelection(e);

        //check if button was pressed and switch to world logic if so
        if (actionCode == MainMenuLogic.PLAY_BUTTON_ACTION_CODE) {
            LogicChangeData lgd = new LogicChangeData(Util.WORLD_LOGIC_TAG, true, false);
            MainActivity.initLogicChange(lgd);
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
