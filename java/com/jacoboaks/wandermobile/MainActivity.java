package com.jacoboaks.wandermobile;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.jacoboaks.wandermobile.game.gamelogic.DeleteSlotLogic;
import com.jacoboaks.wandermobile.game.gamelogic.GameLogic;
import com.jacoboaks.wandermobile.game.gamelogic.LogicChangeData;
import com.jacoboaks.wandermobile.game.gamelogic.MainMenuLogic;
import com.jacoboaks.wandermobile.game.gamelogic.NewGameLogic;
import com.jacoboaks.wandermobile.game.gamelogic.SaveSlotChoiceLogic;
import com.jacoboaks.wandermobile.game.gamelogic.WorldLogic;
import com.jacoboaks.wandermobile.graphics.GameRenderer;
import com.jacoboaks.wandermobile.util.Node;
import com.jacoboaks.wandermobile.util.Util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Serves as the main Activity for Wander.
 */
public class MainActivity extends AppCompatActivity {

    //Game Version/Build
    public final static String WANDER_STARTING_LOGIC = Util.MAIN_MENU_LOGIC_TAG;
    public final static String WANDER_VERSION = "0.0";
    public final static int WANDER_BUILD = 35;

    //Public Static Data
    public static File appDir; //reference to the file directory of the app
    public static GameLogic currentLogic; //reference to current running logic
    public static boolean[] saveSlots; //which save slots are in use
    public static boolean changeLogic = false; //flag for changing logic

    //Private Static Data
    private static LogicChangeData logicChangeData; //saved static data for logic changing
    private static Node logicTransferData; //data to transfer between logic when switching
    private static Resources resources; //reference to resources for resource loading
    private static Bundle savedBundle; //saved data from when app is paused
    private static Map<String, Bundle> savedLogics; //data from saved previous logic instances

    //Instance Data
    private GameView view; //view

    /**
     * Is called every time the activity is created or recreated.
     * @param savedInstanceState any saved information from the previous activity lifecycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //find app directory and check for files
        MainActivity.appDir = this.getFilesDir();
        this.checkSaveSlots();

        //set context reference
        if (MainActivity.resources == null) MainActivity.resources = this.getResources();

        //create saved logic map
        if (MainActivity.savedLogics == null) MainActivity.savedLogics = new HashMap<>();

        //initialize the logic and view
        MainActivity.currentLogic = initGameLogic(MainActivity.savedBundle);
        initGameView(MainActivity.currentLogic);
    }

    /**
     * Creates and sets up the GameLogic.
     */
    private GameLogic initGameLogic(Bundle withData) {

        //create logic and figure out with logic to start with
        GameLogic logic = null;
        String logicToLoad = MainActivity.WANDER_STARTING_LOGIC;
        if (withData != null) logicToLoad = withData.getString("logic");

        //create appropriate logic instance
        if (logicToLoad.equals(Util.MAIN_MENU_LOGIC_TAG)) {
            logic = new MainMenuLogic();
        } else if (logicToLoad.equals(Util.NEW_GAME_LOGIC_TAG)) {
            logic = new NewGameLogic();
        } else if (logicToLoad.equals(Util.WORLD_LOGIC_TAG)) {
            logic = new WorldLogic();
        } else if (logicToLoad.equals(Util.SAVE_SLOT_CHOICE_LOGIC_TAG)) {
            logic = new SaveSlotChoiceLogic();
        } else if (logicToLoad.equals(Util.DELETE_SLOT_LOGIC_TAG)) {
            logic = new DeleteSlotLogic();
        } else {
            if (Util.DEBUG) Log.i(Util.getLogTag("MainActivity.java", "initGameLogic(Bundle)"),
                    "failed to load previous logic - reverting to main menu logic");
            logic = new MainMenuLogic();
        }

        //load data into logic
        logic.loadData(withData);

        //return initialized logic
        return logic;
    }

    /**
     * Creates and sets up the GameView.
     */
    private void initGameView(GameLogic logic) {
        this.view = new GameView(this, logic);
        setContentView(this.view);
    }

    /**
     * Is called when the user no longer interacts with the Activity.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (this.view != null) this.view.onPause();
    }

    /**
     * Is called when the user resumes interactivity with the Activity.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (this.view != null) this.view.onResume();
        if (MainActivity.savedBundle != null) MainActivity.currentLogic.loadData(MainActivity.savedBundle);
    }

    /**
     * Is called whenever the Activity lifecycle is over and data needs to be saved.
     * @param outState the bundle to save data in for reloading during the next onCreate()
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //get data from game logic and convert it to a bundle
        Node.nodeToBundle(outState, MainActivity.currentLogic.requestData());
        MainActivity.savedBundle = outState;
    }

    /**
     * Returns saved data from a previously encountered logic.
     * @param logicTag the tag of the logic whose data to retrieve
     * @return a bundle containing the given logic's data, or null if there is none
     */
    public static Bundle getPreviousLogicData(String logicTag) {
        return MainActivity.savedLogics.get(logicTag);
    }

    /**
     * Saves previous logic data into the saved logic map.
     */
    public static void saveLogicData(String logicTag, Bundle data) {
        MainActivity.savedLogics.put(logicTag, data);
    }

    /**
     * Sets the data for a logic change to be read by the GameRenderer.
     */
    public static void initLogicChange(LogicChangeData logicChangeData, Node transferData) {
        MainActivity.changeLogic = true;
        MainActivity.logicChangeData = logicChangeData;
        MainActivity.logicTransferData = transferData;
    }

    /**
     * Flags that the logic change has been processed.
     */
    public static void logicChangeProcessed() {
        MainActivity.changeLogic = false;
    }

    /**
     * Sees which save slots are in use
     */
    private void checkSaveSlots() {
        MainActivity.saveSlots = new boolean[] { false, false, false };
        for (int i = 0; i < 3; i++) {
            File savefile = new File(MainActivity.appDir, "data/saves/saveslot" + i + "/savedata.wdr");
            MainActivity.saveSlots[i] = savefile.exists();
        }
    }

    //Static Accessors
    public static Resources getAppResources() { return MainActivity.resources; }
    public static LogicChangeData getLogicChangeData() { return MainActivity.logicChangeData; }
    public static Node getLogicTransferData() { return MainActivity.logicTransferData; }

    /**
     * GameView Inner Class
     * Extends from GLSurfaceView to allow touch operations, more flexibility, and use of a
     * custom Renderer.
     */
    private class GameView extends GLSurfaceView {

        //Data
        private final GameRenderer renderer;
        private ScaleGestureDetector scaleDetector;

        //Constructor
        public GameView(Context context, GameLogic logic) {
            super(context);

            //setup scale detector
            this.scaleDetector = new ScaleGestureDetector(context, new ScaleListener());

            //create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2);

            //enabled preserving context on pause
            setPreserveEGLContextOnPause(true);

            //create renderer
            this.renderer = new GameRenderer(logic);

            //set the renderer for drawing on the GLSurfaceView
            setRenderer(this.renderer);
        }

        /**
         * Handles a touch event on this view.
         * @param e touch event to handle
         * @return whether the event was handled
         */
        @Override
        public boolean onTouchEvent(MotionEvent e) {

            //handle event
            boolean scaleDetector = this.scaleDetector.onTouchEvent(e);
            boolean baseInput = this.renderer.input(e);
            return (scaleDetector || baseInput);
        }

        /**
         * ScaleListener Inner Class
         * Listens for scale gestures.
         */
        public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                return renderer.scaleInput(detector.getScaleFactor());
            }
        }
    }
}
