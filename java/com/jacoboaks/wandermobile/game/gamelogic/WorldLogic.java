package com.jacoboaks.wandermobile.game.gamelogic;

import android.opengl.GLES20;
import android.os.Bundle;
import android.view.MotionEvent;

import com.jacoboaks.wandermobile.R;
import com.jacoboaks.wandermobile.game.HUD;
import com.jacoboaks.wandermobile.game.gamecontrol.WorldControl;
import com.jacoboaks.wandermobile.game.gameitem.GameItem;
import com.jacoboaks.wandermobile.game.gameitem.TextItem;
import com.jacoboaks.wandermobile.graphics.Camera;
import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.graphics.Model;
import com.jacoboaks.wandermobile.graphics.ShaderProgram;
import com.jacoboaks.wandermobile.graphics.Texture;
import com.jacoboaks.wandermobile.util.Color;
import com.jacoboaks.wandermobile.util.Node;

import java.util.Random;

public class WorldLogic implements GameLogic {

    //Surface Data
    private int width, height;
    private float aspectRatio;

    //Graphical Data
    private boolean aspectRatioAction; //true (ratio < 1) -> multiply y by aspect ratio; false (ratio >= 1) -> divide x by aspect ratio
    private ShaderProgram shaderProgram;
    private Camera camera;
    private Font font;
    private HUD hud;

    //Other Data
    private WorldControl control;
    private GameItem[] gameItems;
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
        this.initObjects();

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
        this.camera = new Camera(0.0f, 0.0f, 1.0f);

        //initialize shader program, set clear color, create font
        this.initShaderProgram();
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.font = new Font(R.drawable.letters, R.raw.lettercutoffs,10, 10, ' ');

        //create HUD
        this.hud = new HUD(this.aspectRatio, this.aspectRatioAction);
    }

    /**
     * @purpose is to initialize game items and the camera
     */
    private void initObjects() {

        //create player game item
        Material material = new Material(new Texture(R.drawable.obama));
        Model square = new Model(Model.STD_SQUARE_MODEL_COORDS(),
                Model.STD_SQUARE_TEX_COORDS(), Model.STD_SQUARE_DRAW_ORDER(), material);
        this.gameItems = new GameItem[95];
        this.gameItems[0] = new GameItem(square, 0f, 0f);

        //create characters
        Random rand = new Random();
        float offset = this.gameItems.length * Model.STD_SQUARE_SIZE / 2;
        for (int i = 33; i < 127; i++) {

            float[] textureCoordinates = this.font.getCharacterTextureCoordinates((char)i, false);
            Material mat = new Material(this.font.getFontSheet(), new Color(
                    (float)rand.nextInt(100) / 100,
                    (float)rand.nextInt(100) / 100,
                    (float)rand.nextInt(100) / 100, 1.0f), true);
            Model mod = new Model(Model.STD_SQUARE_MODEL_COORDS(), textureCoordinates,
                    Model.STD_SQUARE_DRAW_ORDER(), mat);
            mod.scale(0.5f);
            this.gameItems[i - 32] = new GameItem(mod, (float)(i - 33) * Model.STD_SQUARE_SIZE / 2, 1f);
        }

        //add hud elements
        Material textMaterial = new Material(this.font.getFontSheet(), new Color(0.6f, 0.6f, 0.6f, 1.0f), true);

        //fps label
        TextItem fpsLabel = new TextItem(this.font, "FPS: ", textMaterial, 0f, -1.0f);
        fpsLabel.scale(0.15f);
        fpsLabel.moveY(0.02f + fpsLabel.getHeight() / 2);
        this.hud.addItem(fpsLabel, 0.02f, -1f, true);

        //wander title
        TextItem title = new TextItem(this.font, "WANDER MOBILE", textMaterial, 0f, 1.0f);
        title.scale(0.25f);
        title.moveY(-title.getHeight() / 2);
        this.hud.addItem(title, 0.02f, -1, true);
    }

    /**
     * @purpose is to initialize the shader program
     */
    private void initShaderProgram() {

        //create shader program, load shaders, and link them.
        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.loadShader(R.raw.worldvshader, GLES20.GL_VERTEX_SHADER);
        this.shaderProgram.loadShader(R.raw.worldfshader, GLES20.GL_FRAGMENT_SHADER);
        this.shaderProgram.link();

        //register shader program uniforms
        this.shaderProgram.registerUniform("aspectRatio");
        this.shaderProgram.registerUniform("aspectRatioAction");
        this.shaderProgram.registerUniform("x");
        this.shaderProgram.registerUniform("y");
        this.shaderProgram.registerUniform("camx");
        this.shaderProgram.registerUniform("camy");
        this.shaderProgram.registerUniform("camzoom");
        this.shaderProgram.registerUniform("color");
        this.shaderProgram.registerUniform("textureSampler");
        this.shaderProgram.registerUniform("colorOverride");
        this.shaderProgram.registerUniform("isTextured");
    }

    /**
     * @purpose is to instate and saved bundle data from a previous instance of this logic
     */
    private void instateLoadedData() {

        //load saved data
        this.gameItems[0].setX(Float.parseFloat(this.savedInstanceData.getString("worldlogic_squarex")));
        this.gameItems[0].setY(Float.parseFloat(this.savedInstanceData.getString("worldlogic_squarey")));
        this.camera.setX(Float.parseFloat(this.savedInstanceData.getString("worldlogic_camerax")));;
        this.camera.setY(Float.parseFloat(this.savedInstanceData.getString("worldlogic_cameray")));;
        this.camera.setZoom(Float.parseFloat(this.savedInstanceData.getString("worldlogic_camerazoom")));
    }

    /**
     * @purpose is to update any FPS tracker or any logic based on FPS
     * @param FPS
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
    public boolean input(MotionEvent e) { return this.control.input(e, this.gameItems, this.camera, this.width, this.height); }

    /**
     * @purpose is to handle specifically scale events
     * @param factor the factor by which the user has scaled
     */
    @Override
    public boolean scaleInput(float factor) { return this.control.scaleInput(factor, camera, gameItems); }

    /**
     * @purpose is the update the components of this logic
     * @param dt the time, in milliseconds, since the last update
     */
    @Override
    public void update(float dt) {

        //update game items
        for (GameItem gameItem : this.gameItems) gameItem.update(dt);
    }

    /**
     * @purpose is to draw any graphical components to the screen.
     * @called after update every cycle
     */
    @Override
    public void draw() {

        //draw shapes
        this.shaderProgram.bind();

        //update aspect ratio and aspect ratio action
        GLES20.glUniform1fv(this.shaderProgram.getUniformIndex("aspectRatio"), 1,
                new float[] { aspectRatio }, 0);
        GLES20.glUniform1iv(this.shaderProgram.getUniformIndex("aspectRatioAction"), 1,
                new int[] { this.aspectRatioAction ? 1 : 0 }, 0);

        //update camera properties
        GLES20.glUniform1fv(this.shaderProgram.getUniformIndex("camx"), 1,
                new float[] { this.camera.getX() }, 0);
        GLES20.glUniform1fv(this.shaderProgram.getUniformIndex("camy"), 1,
                new float[] { this.camera.getY() }, 0);
        GLES20.glUniform1fv(this.shaderProgram.getUniformIndex("camzoom"), 1,
                new float[] { this.camera.getZoom() }, 0);

        //draw game items
        for (GameItem gameItem: this.gameItems) gameItem.draw(this.shaderProgram);

        //unbind shader program
        this.shaderProgram.unbind();

        //render hud
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
        data.addChild(new Node("squarex", Float.toString(this.gameItems[0].getX())));
        data.addChild(new Node("squarey", Float.toString(this.gameItems[0].getY())));
        data.addChild(new Node("camerax", Float.toString(this.camera.getX())));
        data.addChild(new Node("cameray", Float.toString(this.camera.getY())));
        data.addChild(new Node("camerazoom", Float.toString(this.camera.getZoom())));
        return data;
    }
}
