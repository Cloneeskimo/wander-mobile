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

    //Button Data
    private static final int PLAY_BUTTON_ACTION_CODE = 1;

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
        title.scale(0.4f);
        this.hud.addItem("TITLE", title, HUD.Placement.TOP_MIDDLE, 0.19f);

        //add version and build tag
        TextItem bvTag = new TextItem(this.font, "v" + MainActivity.WANDER_VERSION + "b" + MainActivity.WANDER_BUILD,
                title.getModel().getMaterial(), 0f, 0f);
        bvTag.scale(0.14f);
        this.hud.addItem("BUILD_VERSION_TAG", bvTag, HUD.Placement.BELOW_LAST, 0.05f);

        //create button and add to hud
        ButtonTextItem playButton = new ButtonTextItem(this.font, "Play",
                new Color(0.0f, 0.0f, 0.0f, 1.0f), new Color(1.0f, 1.0f, 1.0f, 1.0f),
                MainMenuLogic.PLAY_BUTTON_ACTION_CODE);
        playButton.scale(0.24f);
        this.hud.addItem("PLAY_BUTTON", playButton, HUD.Placement.MIDDLE, 0f);

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

        //apply input data to play button
        ButtonTextItem playButton = (ButtonTextItem)this.hud.getItem("PLAY_BUTTON");
        int actionCode = playButton.updateSelection(e);

        //check if button was pressed and switch to world logic if so
        if (actionCode == MainMenuLogic.PLAY_BUTTON_ACTION_CODE) {
            this.fadeOutTime = Util.FADE_TIME;
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
            if (this.fadeOutTime < 0f) {
                LogicChangeData lgd = new LogicChangeData(Util.WORLD_LOGIC_TAG, true, false);
                MainActivity.initLogicChange(lgd);

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
