package com.jacoboaks.wandermobile.game.gamelogic;

import android.os.Bundle;
import android.view.MotionEvent;

import com.jacoboaks.wandermobile.MainActivity;
import com.jacoboaks.wandermobile.util.Node;
import com.jacoboaks.wandermobile.util.Util;

/**
 * Contains the logic for the MainMenu of the game.
 */
public class MainMenuLogic implements GameLogic {

    //Initialization Method
    @Override
    public void init() {

    }

    //Data Loading Method
    @Override
    public void loadData(Bundle savedInstanceData) {

    }

    //Input Handling Method
    @Override
    public boolean input(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
            LogicChangeData logicChangeData = new LogicChangeData(Util.WORLD_LOGIC_TAG,
                    true, false);
            MainActivity.initLogicChange(logicChangeData);
            return true;
        }
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

    }
}
