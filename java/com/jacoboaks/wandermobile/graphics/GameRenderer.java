package com.jacoboaks.wandermobile.graphics;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.jacoboaks.wandermobile.MainActivity;
import com.jacoboaks.wandermobile.game.gamelogic.GameLogic;
import com.jacoboaks.wandermobile.game.gamelogic.LogicChangeData;
import com.jacoboaks.wandermobile.game.gamelogic.MainMenuLogic;
import com.jacoboaks.wandermobile.game.gamelogic.WorldLogic;
import com.jacoboaks.wandermobile.util.Node;
import com.jacoboaks.wandermobile.util.Util;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * GameRenderer Class
 * Controls what gets rendered in the GameView.
 */
public class GameRenderer implements GLSurfaceView.Renderer {

    //Static Surface Data
    public static int surfaceWidth = 0, surfaceHeight = 0;
    public static float surfaceAspectRatio = 0;
    public static boolean surfaceAspectRatioAction = false;

    //Timekeeping Properties
    private long lastSecond;
    private long lastCycle;
    private int frameCount;
    private int FPS;

    //GameLogic
    private GameLogic logic;

    /**
     * Constructs this GameRenderer and starting it with the given GameLogic.
     * @param logic the GameLogic to begin rendering
     */
    public GameRenderer(GameLogic logic) {
        this.lastSecond = this.lastCycle = System.currentTimeMillis();
        this.FPS = 0;
        this.logic = logic;
    }

    /**
     * Is called once to set up the OpenGL ES environment.
     * @param unused ios unused after GLES 1.0
     */
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        //log creation of surface
        if (Util.DEBUG) Log.i(Util.getLogTag("GameRenderer.java",
                "onSurfaceCreated(GL10, EGLConfig)"),
                "the surface was created");

        //enable gl transparencies
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
    }

    /**
     * Is called for each redraw of the view.
     * @param unused is unused after GLES 1.0
     */
    @Override
    public void onDrawFrame(GL10 unused) {

        //update
        this.update();

        //draw
        this.render();
    }

    /**
     * Handles any MotionEvents that occur within the GameView.
     * @param e the event to handle
     * @return whether or not the event was handled
     */
    public boolean input(MotionEvent e) { return this.logic.input(e); }

    /**
     * Handles scale events specifically that occur within the GameView.
     * @param scaleFactor the factor by which the user has scaled
     * @return whether or not the event was handled
     */
    public boolean scaleInput(float scaleFactor) { return this.logic.scaleInput(scaleFactor); }

    /**
     * Updates timekeeping and logic components.
     */
    private void update() {

        //timekeeping/FPS calculations
        this.frameCount++;
        while (System.currentTimeMillis() - this.lastSecond >= 1000) {
            this.FPS = this.frameCount;
            this.onFPSUpdate();
            this.frameCount = 0;
            this.lastSecond += 1000;
        }

        //check for logic change
        if (MainActivity.changeLogic) {
            LogicChangeData logicChangeData = MainActivity.getLogicChangeData();
            this.changeLogic(logicChangeData.getLogicTag(), logicChangeData.doesLoadNewLogicData(),
                    logicChangeData.doesSaveOldLogicData());
        }

        //update logic
        this.logic.update(System.currentTimeMillis() - this.lastCycle);
        this.lastCycle = System.currentTimeMillis();
    }

    /**
     * Reacts to FPS updates.
     */
    private void onFPSUpdate() {
        if (this.logic instanceof WorldLogic) {
            WorldLogic wlogic = (WorldLogic)this.logic;
            wlogic.onFPSUpdate(this.FPS);
        }
    }

    /**
     * Clears the screen and subsequently renders the GameLogic.
     */
    private void render() {

        //clear the screen
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //draw any objects pertaining to the game logic
        this.logic.render();
    }

    /**
     * Changes the current GameLogic being operated on under this GameRenderer.
     * @param logicTag the tag of the logic to switch to
     * @param loadNewLogicData whether or not to load the data from the new logic
     * @param savePreviousLogicData whether or not to save the data from the previous logic
     */
    private void changeLogic(String logicTag, boolean loadNewLogicData, boolean savePreviousLogicData) {

        //get new logic data
        Bundle newLogicData = loadNewLogicData ? MainActivity.getPreviousLogicData(logicTag) : null;

        //save previous logic data
        if (savePreviousLogicData) {
            Bundle previousLogicData = new Bundle();
            Node previousLogicNode = this.logic.requestData();
            Node.nodeToBundle(previousLogicData, previousLogicNode);
            MainActivity.saveLogicData(previousLogicNode.getValue(), previousLogicData);
        }

        //switch logic
        this.logic.cleanup();
        if (logicTag.equals(Util.MAIN_MENU_LOGIC_TAG)) {
            this.logic = new MainMenuLogic();
        } else if (logicTag.equals(Util.WORLD_LOGIC_TAG)) {
            this.logic = new WorldLogic();
        } else {
            if (Util.DEBUG) Log.i(Util.getLogTag("GameRenderer.java", "changeLogic(String, boolean)"),
                    "unable to discern logic tag: " + logicTag + ", defaulting to MainMenuLogic");
            this.logic = new MainMenuLogic();
        }
        MainActivity.currentLogic = this.logic;
        MainActivity.logicChangeProcessed();

        //load data
        if (newLogicData != null) this.logic.loadData(newLogicData);

        //initialize new logic
        this.logic.init();
    }

    /**
     * Is called if geometry of the view changes (ex: device orientation changes) and directly
     * after onSurfaceCreated.
     * @param unused is unused after GLES 1.0
     * @param width width of the new screen geometry
     * @param height height of the new screen geometry
     */
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {

        //save width, height, and aspect ratio
        GameRenderer.surfaceWidth = width;
        GameRenderer.surfaceHeight = height;
        GameRenderer.surfaceAspectRatio = (float)width / (float)height;
        GameRenderer.surfaceAspectRatioAction = (surfaceAspectRatio < 1.0f);

        //log surface data
        if (Util.DEBUG) Log.i(Util.getLogTag("GameRenderer.java",
                "onSurfaceChanged(GL10, int, int)"), "surface changed - w: " + width
                + ", h: " + height + "aspect ratio: " + GameRenderer.surfaceAspectRatio);

        //update viewport
        GLES20.glViewport(0, 0, width, height);

        //initialize logic
        this.logic.init();

        //log initialization of logic
        if (Util.DEBUG) Log.i(Util.getLogTag("GameRenderer.java",
                "onSurfaceChanged(GL10, int, int)"), "logic initialized");
    }
}