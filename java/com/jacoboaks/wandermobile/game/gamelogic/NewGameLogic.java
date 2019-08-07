package com.jacoboaks.wandermobile.game.gamelogic;

import android.os.Bundle;
import android.view.MotionEvent;

import com.jacoboaks.wandermobile.MainActivity;
import com.jacoboaks.wandermobile.util.Node;
import com.jacoboaks.wandermobile.util.Util;

/**
 * The logic which occurs when a new game is being created.
 */
public class NewGameLogic implements GameLogic {

    //Initialization Method
    @Override
    public void init() {
        LogicChangeData lgd = new LogicChangeData(Util.WORLD_LOGIC_TAG, true, false);
        MainActivity.initLogicChange(lgd, new Node("new game", "new game"));
    }

    //Data Loading Method
    @Override
    public void loadData(Bundle savedInstanceData) {

    }

    //Input Method
    @Override
    public boolean input(MotionEvent e) {
        return false;
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

    }
}
