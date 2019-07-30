package com.jacoboaks.wandermobile.game;

import android.opengl.GLES20;

import com.jacoboaks.wandermobile.R;
import com.jacoboaks.wandermobile.game.gameitem.GameItem;
import com.jacoboaks.wandermobile.graphics.ShaderProgram;
import com.jacoboaks.wandermobile.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @purpose is to hold many GameItems to be rendered over top of the world
 */
public class HUD {

    //Data
    private List<GameItem> gameItems;
    private float aspectRatio;
    private boolean aspectRatioAction; //true (ratio < 1) -> multiply y by aspect ratio; false (ratio >= 1) -> divide x by aspect ratio
    private ShaderProgram shaderProgram;

    /**
     * @purpose is to construct this HUD
     * @param aspectRatio the aspect ration of the surface
     * @param aspectRatioAction the aspect ratio action given the current aspect ratio (explained in data)
     */
    public HUD(float aspectRatio, boolean aspectRatioAction) {
        this.aspectRatio = aspectRatio;
        this.aspectRatioAction = aspectRatioAction;
        this.gameItems = new ArrayList<>();
        this.initShaderProgram();
    }

    /**
     * @purpose is to initialize the HUD's shader program
     */
    private void initShaderProgram() {

        //create shader program, load shaders, and link them.
        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.loadShader(R.raw.hudvshader, GLES20.GL_VERTEX_SHADER);
        this.shaderProgram.loadShader(R.raw.hudfshader, GLES20.GL_FRAGMENT_SHADER);
        this.shaderProgram.link();

        //register shader program uniforms
        this.shaderProgram.registerUniform("aspectRatio");
        this.shaderProgram.registerUniform("aspectRatioAction");
        this.shaderProgram.registerUniform("x");
        this.shaderProgram.registerUniform("y");
        this.shaderProgram.registerUniform("color");
        this.shaderProgram.registerUniform("textureSampler");
        this.shaderProgram.registerUniform("colorOverride");
        this.shaderProgram.registerUniform("isTextured");
    }

    /**
     * @purpose is to add a new game item as is
     * @param item the item to add
     */
    public void addItem(GameItem item) {
        this.gameItems.add(item);
    }

    /**
     * @purpose is to add a new game item at the provided normalized x and y values.
     * @param item the item to add
     * @param atNormalizedX the normalized x value to add the item at (0.0f to 1.0f). If a negative
     *                      value is given, only aspect ratio will be taken into account. If a
     *                      negative value below 2 is given, the value will be unchanged.
     * @param atNormalizedY the normalized y value to add the item at (0.0f to 1.0f). If a negative
     *      *                      value is given, only aspect ratio will be taken into account. If a
     *      *                      negative value below 2 is given, the value will be unchanged.
     */
    public void addItem(GameItem item, float atNormalizedX, float atNormalizedY, boolean aspectRatio) {

        //adjust position
        float x = (atNormalizedX < 0.0f) ? item.getX() : atNormalizedX * 2 - 1;
        float y = (atNormalizedY < 0.0f) ? item.getY() : atNormalizedY * 2 - 1;
        if (aspectRatio) {
            if (this.aspectRatioAction) y /= this.aspectRatio;
            else x *= this.aspectRatio;
        }
        item.setX(x);
        item.setY(y);

        //add item
        this.gameItems.add(item);
    }

    //Render Method
    public void render() {

        //draw shapes
        this.shaderProgram.bind();

        //update aspect ratio and aspect ratio action
        GLES20.glUniform1fv(this.shaderProgram.getUniformIndex("aspectRatio"), 1,
                new float[] { this.aspectRatio }, 0);
        GLES20.glUniform1iv(this.shaderProgram.getUniformIndex("aspectRatioAction"), 1,
                new int[] { this.aspectRatioAction ? 1 : 0 }, 0);

        //draw game items
        for (GameItem gameItem: this.gameItems) gameItem.draw(this.shaderProgram);

        //unbind shader program
        this.shaderProgram.unbind();
    }

    //Accessor
    public GameItem getItem(int index) {
        if (index >= gameItems.size() || index < 0) Util.fatalError("HUD.java", "getItem(int)",
                "index '" + index + "' is out of range.");
        return this.gameItems.get(index);
    }

}
