package com.jacoboaks.wandermobile.game;

import android.opengl.GLES20;
import android.os.Bundle;

import com.jacoboaks.wandermobile.R;
import com.jacoboaks.wandermobile.game.gameitem.GameItem;
import com.jacoboaks.wandermobile.graphics.Camera;
import com.jacoboaks.wandermobile.graphics.FollowingCamera;
import com.jacoboaks.wandermobile.graphics.ShaderProgram;
import com.jacoboaks.wandermobile.util.Node;
import com.jacoboaks.wandermobile.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @purpose is to hold many GameItems to be rendered under the HUD
 */
public class World {

    //Surface Data
    float aspectRatio;
    boolean aspectRatioAction; //true (ratio < 1) -> multiply y by aspect ratio; false (ratio >= 1) -> divide x by aspect ratio

    //Data
    private List<GameItem> gameItems;
    private ShaderProgram shaderProgram;
    private FollowingCamera camera;

    /**
     * @purpose is to construct this World
     * @param aspectRatio the aspect ratio of the surface
     * @param aspectRatioAction the aspect ration action given the current aspect ratio (explained in data)
     */
    public World(float aspectRatio, boolean aspectRatioAction, GameItem cameraFollowee) {
        this.aspectRatio = aspectRatio;
        this.aspectRatioAction = aspectRatioAction;
        this.initShaderProgram();
        this.initObjects(cameraFollowee);
    }

    /**
     * @purpose is to instate any saved world data
     * @param data
     */
    public void instateLoadedData(Bundle data) {
        this.camera.setX(Float.parseFloat(data.getString("worldlogic_camerax")));;
        this.camera.setY(Float.parseFloat(data.getString("worldlogic_cameray")));;
        this.camera.setZoom(Float.parseFloat(data.getString("worldlogic_camerazoom")));
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
     * @purpose is to initialize the game objects
     */
    private void initObjects(GameItem cameraFollowee) {

        //create gameitems array
        this.camera = new FollowingCamera(0.2f, cameraFollowee, false);
        this.gameItems = new ArrayList<>();
    }

    //Update Method
    public void update(float dt) {
        for (GameItem gameItem : this.gameItems) gameItem.update(dt);
        this.camera.update(dt);
    }

    //Render Method
    public void render() {

        //bind shader program
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
    }

    /**
     * @purpose is to return a game item based off of an index
     * @param index the index of the game item
     * @return the game item at the given index
     */
    public GameItem getItem(int index) {
        if (index < 0 || index >= this.gameItems.size()) Util.fatalError("World.java",
                "getGameItem(int)", "invalid index ;" + index + "' given");
        return this.gameItems.get(index);
    }

    //Accessors
    public List<GameItem> getGameItems() { return this.gameItems; }
    public FollowingCamera getCamera() { return this.camera; }

    //Mutator
    public void addGameItem(GameItem item) { this.gameItems.add(item); }

    /**
     * @purpose is to compile all important data into a node to be put into a bundle before
     * terminating the logic - this will be reloaded in the next instance after the interruption has
     * ceased
     * @return the node containing all of the compiled important information
     */
    public void requestData(Node data) {
        data.addChild(new Node("camerax", Float.toString(this.camera.getX())));
        data.addChild(new Node("cameray", Float.toString(this.camera.getY())));
        data.addChild(new Node("camerazoom", Float.toString(this.camera.getZoom())));
    }
}
