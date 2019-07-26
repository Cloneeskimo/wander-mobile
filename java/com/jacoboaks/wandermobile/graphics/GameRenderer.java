package com.jacoboaks.wandermobile.graphics;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

import com.jacoboaks.wandermobile.game.gamelogic.GameLogic;
import com.jacoboaks.wandermobile.game.gamelogic.WorldLogic;
import com.jacoboaks.wandermobile.util.Util;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * GameRenderer Class
 * @purpose is to control what gets rendered in the GameView
 */
public class GameRenderer implements GLSurfaceView.Renderer {

    //Timekeeping Properties
    private long lastSecond;
    private long lastCycle;
    private int frameCount;
    private int FPS;

    //Other Data
    GameLogic logic;

    //Constructor
    public GameRenderer(GameLogic logic) {
        this.lastSecond = this.lastCycle = System.currentTimeMillis();
        this.FPS = 0;
        this.logic = logic;
    }

    /**
     * @called once to set up the OpenGL ES environment
     * @param unused unused after GLES 1.0
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
     * @called for each redraw of the view
     * @param unused unused after GLES 1.0
     */
    @Override
    public void onDrawFrame(GL10 unused) {

        //update
        this.update();

        //draw
        this.draw();
    }

    /**
     * @purpose to handle any MotionEvents that occur within the GameView
     * @param e the event to handle
     * @return whether or not the event was handled
     */
    public boolean input(MotionEvent e) { return this.logic.input(e); }

    /**
     * @purpose is to handle specifically scale events that occur within the GameView
     * @param scaleFactor the factor by which the user has scaled
     * @return whether or not the event was handled
     */
    public boolean scaleInput(float scaleFactor) { return this.logic.scaleInput(scaleFactor); }

    /**
     * @purpose to to timekeep and update logic components
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

        //update logic
        this.logic.update(System.currentTimeMillis() - this.lastCycle);
        this.lastCycle = System.currentTimeMillis();
    }

    /**
     * @purpose is to react to any FPS updates
     */
    private void onFPSUpdate() {
        if (this.logic instanceof WorldLogic) {
            WorldLogic wlogic = (WorldLogic)this.logic;
            wlogic.onFPSUpdate(this.FPS);
        }
    }

    /**
     * @purpose is to clear the screen and subsequently draw the game logic elements
     */
    private void draw() {

        //clear the screen
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //draw any objects pertaining to the game logic
        this.logic.draw();
    }

    /**
     * @called if geometry of the view changes (ex: device orientation changes) and directly
     * after onSurfaceCreated
     * @param unused unused after GLES 1.0
     * @param width width of the new screen geometry
     * @param height height of the new screen geometry
     */
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {

        //log width and height
        if (Util.DEBUG) Log.i(Util.getLogTag("GameRenderer.java",
                "onSurfaceChanged(GL10, int, int)"), "surface changed - w: " + width + ", h: " + height);

        //update viewport
        GLES20.glViewport(0, 0, width, height);

        //initialize logic
        this.logic.init(width, height);

        //log initialization of logic
        if (Util.DEBUG) Log.i(Util.getLogTag("GameRenderer.java",
                "onSurfaceChanged(GL10, int, int)"), "logic initialized");
    }
}