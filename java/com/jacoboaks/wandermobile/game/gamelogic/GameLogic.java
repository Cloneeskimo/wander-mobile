package com.jacoboaks.wandermobile.game.gamelogic;

import android.os.Bundle;
import android.view.MotionEvent;

import com.jacoboaks.wandermobile.util.Node;

/**
 * GameLogic Interface
 * Generalizes various forms of game logic into one interface
 */
public interface GameLogic {

    /**
     * Is called whenever the surface is created. After components (especially graphical ones) have
     * been initialized, any saved data from loadData(Bundle) should be reinstated in this method.
     */
    void init();

    /**
     * Is called whenever data needs to be saved upon creation of this GameLogic to be reinstated
     * after initialization has completed. This reinstating should be done in init(int, int).
     * @param savedInstanceData the data to save for reinstating after initialization
     */
    void loadData(Bundle savedInstanceData);

    /**
     * Will be called after init() to load any saved instance data that was saved during loadData().
     * Should re-implement any important data after initialization has occurred. It is the responsibility
     * of the logic to check if the savedInstanceData is null in this method before trying to
     * instate it.
     */
    void instateSavedInstanceData();

    /**
     * Is called whenever a MotionEvent from the GameView has occurred.
     * @param e the MotionEvent to handle
     * @return whether or not the MotionEvent was handled
     */
    boolean input(MotionEvent e);

    /**
     * Is called specifically for scale input received in the GameView.
     * @param factor the factor by which the user scaled
     * @return whether or not the scale input was handled
     */
    boolean scaleInput(float factor);

    /**
     * Is called every cycle.
     * @param dt the amount of time that has passed since the last cycle (in seconds)
     */
    void update(float dt);

    /**
     * Is called every cycle after update(float).
     */
    void render();

    /**
     * Is called whenever the logic will need to be restored later. The returned node should contain
     * all important information to be reinstated when init(int, int) is called later on.
     * @return the Node containing any important information to return
     */
    Node requestData();

    /**
     * Is called whenever the logic is about to be switched away from, after requestData(). GL
     * resources such as ShaderPrograms should be cleaned up in this method.
     */
    void cleanup();
}