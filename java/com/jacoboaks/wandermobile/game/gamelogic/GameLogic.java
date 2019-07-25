package com.jacoboaks.wandermobile.game.gamelogic;

import android.os.Bundle;
import android.view.MotionEvent;

import com.jacoboaks.wandermobile.util.Node;

/**
 * GameLogic Interface
 * @purpose is to generalize various forms of game logic into one interface
 * @method init(int, int) is called whenever the surface is created
 * @method loadData(Bundle) is called whenever data needs to be saved for reinstating from the
 * last instance of this logic
 * @method input(MotionEvent) is called whenever a MotionEvent from the GameView needs handled
 * @method scaleInput(float) is called whenever a specifically scale event needs handled
 * @method update(float) is called every cycle
 * @method draw() is called every cycle after update
 * @method requestData() is called whenever the logic will need to be restored later -
 * the returned node should contain all important information to be reinstated after init(int, int)
 * is called later on.
 */
public interface GameLogic {

    //Methods
    void init(int width, int height);
    void loadData(Bundle savedInstanceData);
    boolean input(MotionEvent e);
    boolean scaleInput(float factor);
    void update(float dt);
    void draw();
    Node requestData();
}