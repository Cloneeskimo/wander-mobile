package com.jacoboaks.wandermobile.game.gamelogic;

import android.opengl.GLES20;
import android.os.Bundle;
import android.view.MotionEvent;

import com.jacoboaks.wandermobile.MainActivity;
import com.jacoboaks.wandermobile.R;
import com.jacoboaks.wandermobile.game.HUD;
import com.jacoboaks.wandermobile.game.World;
import com.jacoboaks.wandermobile.game.gamecontrol.WorldControl;
import com.jacoboaks.wandermobile.game.gameitem.GameItem;
import com.jacoboaks.wandermobile.game.gameitem.TextItem;
import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.graphics.Model;
import com.jacoboaks.wandermobile.graphics.Texture;
import com.jacoboaks.wandermobile.util.Color;
import com.jacoboaks.wandermobile.util.Node;

import java.util.Random;

public class WorldLogic implements GameLogic {

    //Surface Data
    private int width, height;
    private float aspectRatio;
    private boolean aspectRatioAction; //true (ratio < 1) -> multiply y by aspect ratio; false (ratio >= 1) -> divide x by aspect ratio

    //Logic Data
    private World world;
    private HUD hud;
    private WorldControl control;
    private Font font;

    //Saved Data
    private Bundle savedInstanceData;

    /**
     * @called whenever the surface is created
     * @param width width of the new surface
     * @param height height of the new surface
     */
    @Override
    public void init(int width, int height) {

        //initialize graphics and objects
        this.initGraphics(width, height);
        this.initWorld();
        this.initHUD();

        //create controls
        this.control = new WorldControl();

        //load data if there is any to load
        if (this.savedInstanceData != null) this.instateLoadedData();
    }

    /**
     * @purpose is to initialize all of the graphical components of the logic
     * @param width the width of the surface
     * @param height the height of the surface
     */
    private void initGraphics(int width, int height) {

        //save width, height, and aspect ratio, create camera
        this.width = width;
        this.height = height;
        this.aspectRatio = (float) width / (float) height;
        this.aspectRatioAction = (aspectRatio < 1.0f);

        //set clear color and create font
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.font = new Font(R.drawable.letters, R.raw.lettercutoffs,10, 10, ' ');
    }

    /**
     * @purpose is to create and populate the world
     */
    private void initWorld() {

        //create world
        this.world = new World(this.aspectRatio, this.aspectRatioAction);

        //create player game item
        Material material = new Material(new Texture(R.drawable.obama));
        Model square = new Model(Model.STD_SQUARE_MODEL_COORDS(),
                Model.STD_SQUARE_TEX_COORDS(), Model.STD_SQUARE_DRAW_ORDER(), material);
        this.world.addGameItem(new GameItem(square, 0f, 0f));

        //create characters
        Random rand = new Random();
        for (int i = 33; i < 127; i++) {

            float[] textureCoordinates = this.font.getCharacterTextureCoordinates((char)i, false);
            Material mat = new Material(this.font.getFontSheet(), new Color(
                    (float)rand.nextInt(100) / 100,
                    (float)rand.nextInt(100) / 100,
                    (float)rand.nextInt(100) / 100, 1.0f), true);
            Model mod = new Model(Model.STD_SQUARE_MODEL_COORDS(), textureCoordinates,
                    Model.STD_SQUARE_DRAW_ORDER(), mat);
            mod.scale(0.5f);
            this.world.addGameItem(new GameItem(mod, (float)(i - 33) * Model.STD_SQUARE_SIZE / 2, 1f));
        }
    }

    /**
     * @purpose is to create and populate the HUD for this logic
     */
    private void initHUD() {

        //create HUD
        this.hud = new HUD(this.aspectRatio, this.aspectRatioAction);

        //create hud text material
        Material textMaterial = new Material(font.getFontSheet(), new Color(0.6f, 0.6f, 0.6f, 1.0f), true);

        //fps label
        TextItem fpsLabel = new TextItem(this.font, "FPS: ", textMaterial, 0f, -1.0f);
        fpsLabel.scale(0.15f);
        fpsLabel.moveY(0.02f + fpsLabel.getHeight() / 2);
        this.hud.addItem(fpsLabel, 0.02f, -1f, true);

        //wander title
        TextItem title = new TextItem(this.font, "WANDER MOBILE " + MainActivity.WANDER_VERSION
                + "B" + MainActivity.WANDER_BUILD, textMaterial, 0f, 1.0f);
        title.scale(0.25f);
        title.moveY(-title.getHeight() / 2);
        this.hud.addItem(title, 0.02f, -1, true);
    }

    /**
     * @purpose is to instate and saved bundle data from a previous instance of this logic
     */
    private void instateLoadedData() {

        //load saved data
        this.world.instateLoadedData(this.savedInstanceData);
        this.world.getItem(0).setX(Float.parseFloat(this.savedInstanceData.getString("worldlogic_squarex")));
        this.world.getItem(0).setY(Float.parseFloat(this.savedInstanceData.getString("worldlogic_squarey")));
    }

    /**
     * @purpose is to update any FPS tracker or any logic based on FPS
     * @param FPS the current FPS
     */
    public void onFPSUpdate(float FPS) {
        TextItem fpsCounter = (TextItem)this.hud.getItem(0);
        fpsCounter.setText("FPS: " + Float.toString(FPS));
    }

    /**
     * @purpose is to save any bundle data from a previous instance of this logic for loading after
     * initialization
     * @param savedInstanceData the bundle data to save for instating later
     */
    @Override
    public void loadData(Bundle savedInstanceData) {
        this.savedInstanceData = savedInstanceData;
    }

    /**
     * @purpose is to handle any input events that occur in the GameView
     * @param e the input event to handle
     * @return whether or not the MotionEvent was handled in any way
     */
    @Override
    public boolean input(MotionEvent e) { return this.control.input(e, this.world.getGameItems(),
            this.world.getCamera(), this.width, this.height); }

    /**
     * @purpose is to handle specifically scale events
     * @param factor the factor by which the user has scaled
     */
    @Override
    public boolean scaleInput(float factor) { return this.control.scaleInput(factor,
            this.world.getCamera(), this.world.getGameItems()); }

    /**
     * @purpose is the update the components of this logic
     * @param dt the time, in milliseconds, since the last update
     */
    @Override
    public void update(float dt) { this.world.update(dt); }

    /**
     * @purpose is to draw any graphical components to the screen.
     * @called after update every cycle
     */
    @Override
    public void render() {
        this.world.render();
        this.hud.render();
    }

    /**
     * @purpose is to compile all important data into a node to be put into a bundle before
     * terminating this instance of the logic - this will be reloaded in the next instance
     * after the interruption has ceased
     * @return the node containing all of the compiled important information
     */
    @Override
    public Node requestData() {

        //add data to node and return it
        Node data = new Node("worldlogic");
        data.addChild(new Node("squarex", Float.toString(this.world.getItem(0).getX())));
        data.addChild(new Node("squarey", Float.toString(this.world.getItem(0).getY())));
        this.world.requestData(data);
        return data;
    }
}
