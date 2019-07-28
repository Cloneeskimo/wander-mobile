package com.jacoboaks.wandermobile;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.jacoboaks.wandermobile.game.gamelogic.GameLogic;
import com.jacoboaks.wandermobile.game.gamelogic.WorldLogic;
import com.jacoboaks.wandermobile.graphics.GameRenderer;
import com.jacoboaks.wandermobile.util.Node;

/**
 * MainActivity Class
 * @purpose is to serve as the main activity for the program
 */
public class MainActivity extends AppCompatActivity {

    //Game Version/Build
    public final static String WANDER_VERSION = "0.0";
    public final static int WANDER_BUILD = 4;

    //Static Data
    public static Context context; //public reference to context for resource loading
    private static Bundle savedBundle;

    //Data
    private GameLogic logic;
    private GameView view;

    //Creation Method
    /**
     * @called every time the activity is created or recreated
     * @param savedInstanceState any saved information from the previous activity lifecycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set context reference
        MainActivity.context = this;

        //initialize the logic and view
        initGameLogic(savedInstanceState);
        initGameView();
    }

    /**
     * @purpose is to create and setup the game logic
     */
    private void initGameLogic(Bundle savedInstanceState) {
        this.logic = new WorldLogic();
        this.logic.loadData(savedInstanceState);
    }

    /**
     * @purpose is to create and setup the game view
     */
    private void initGameView() {
        this.view = new GameView(this, this.logic);
        setContentView(this.view);
    }

    /**
     * @called when the user no longer interacts with the activity
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (this.view != null) this.view.onPause();
    }

    /**
     * @called when the user resumes interactivity with the activity
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (this.view != null) this.view.onResume();
        if (MainActivity.savedBundle != null) this.logic.loadData(MainActivity.savedBundle);
    }

    /**
     * @called whenever the activity lifecycle is over and data needs to be saved
     * @param outState the bundle to save data in for reloading during the next onCreate()
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //get data from game logic and convert it to a bundle
        Node.nodeToBundle(outState, this.logic.requestData());
        MainActivity.savedBundle = outState;
    }

    /**
     * GameView Inner Class
     * @purpose is to extend from GLSurfaceView to allow touch operations and more flexibility
     */
    class GameView extends GLSurfaceView {

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
         * @purpose is to handle a touch event on this view
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
         * @purpose is to listen for any scale gestures
         */
        public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                return renderer.scaleInput(detector.getScaleFactor());
            }
        }
    }
}
