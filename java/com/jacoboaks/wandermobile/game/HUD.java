package com.jacoboaks.wandermobile.game;

import android.opengl.GLES20;

import com.jacoboaks.wandermobile.R;
import com.jacoboaks.wandermobile.game.gameitem.GameItem;
import com.jacoboaks.wandermobile.game.gameitem.TextItem;
import com.jacoboaks.wandermobile.graphics.GameRenderer;
import com.jacoboaks.wandermobile.graphics.ShaderProgram;
import com.jacoboaks.wandermobile.graphics.Transformation;
import com.jacoboaks.wandermobile.util.Coord;
import com.jacoboaks.wandermobile.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds many GameItems to be rendered over top of a World.
 */
public class HUD {

    //Data
    private List<GameItem> gameItems;
    private ShaderProgram shaderProgram;

    /**
     * Constructs this HUD.
     * @param aspectRatio the aspect ration of the surface
     * @param aspectRatioAction the aspect ratio action given the current aspect ratio (explained in data)
     */
    public HUD(float aspectRatio, boolean aspectRatioAction) {
        this.gameItems = new ArrayList<>();
        this.initShaderProgram();
    }

    /**
     * Initializes the HUD's shader program.
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
     * Adds a new GameItem to this HUD using the given information.
     * @param item the item to add
     * @param x the normalized x to place the item at
     * @param y the normalized y to place the item at
     */
    public void addItem(GameItem item, float x, float y) {
        Coord coord = new Coord(x, y);
        Transformation.normalizedToAspected(coord);
        item.setX(coord.x);
        item.setY(coord.y);
        this.gameItems.add(item);
    }

    /**
     * Adds a new GameItem to this HUD using the given information.
     * @param item the item to add
     * @param placement the placement for the item to go. can be relative to the last item added
     *                  or to the various corners of the screen.
     */
    public void addItem(GameItem item, Placement placement, float padding) {

        //create coordinate for new position
        Coord newPos = new Coord();

        //place item
        switch (placement) {
            case TOP_LEFT:
                newPos = new Coord(-1f, 1f);
                Transformation.normalizedToAspected(newPos);
                newPos.x += padding;
                newPos.y -= padding;
                break;
            case TOP_RIGHT:
                newPos = new Coord(1f, 1f);
                Transformation.normalizedToAspected(newPos);
                newPos.x -= (padding + item.getWidth());
                newPos.y -= padding;
                break;
            case BOTTOM_LEFT:
                newPos = new Coord(-1f, -1f);
                Transformation.normalizedToAspected(newPos);
                newPos.x += padding;
                newPos.y += (padding + item.getHeight());
                break;
            case BOTTOM_RIGHT:
                newPos = new Coord(1f, -1f);
                Transformation.normalizedToAspected(newPos);
                newPos.x -= (padding + item.getWidth());
                newPos.y += (padding + item.getHeight());
                break;
            case LEFT_OF_LAST:
                newPos = this.topLeftCoordsOfLastItem();
                newPos.x -= (padding + item.getWidth());
                break;
            case RIGHT_OF_LAST:
                newPos = this.topLeftCoordsOfLastItem();
                Coord size1 = this.sizeOfLastItem();
                newPos.x += (padding + size1.x);
                break;
            case ABOVE_LAST:
                newPos = this.topLeftCoordsOfLastItem();
                newPos.y += (padding + item.getHeight());
                break;
            case BELOW_LAST:
                newPos = this.topLeftCoordsOfLastItem();
                Coord size2 = this.sizeOfLastItem();
                newPos.y -= (padding + size2.y);
                break;
        }

        //adjust for GameItems that aren't TextItems
        if (!(item instanceof TextItem)) {
            newPos.x += (item.getWidth() / 2);
            newPos.y -= (item.getHeight() / 2);
        }

        //set position
        item.setX(newPos.x);
        item.setY(newPos.y);

        //add item
        this.gameItems.add(item);
    }

    //Render Method
    public void render() {

        //draw shapes
        this.shaderProgram.bind();

        //update aspect ratio and aspect ratio action
        GLES20.glUniform1fv(this.shaderProgram.getUniformIndex("aspectRatio"), 1,
                new float[] { GameRenderer.surfaceAspectRatio }, 0);
        GLES20.glUniform1iv(this.shaderProgram.getUniformIndex("aspectRatioAction"), 1,
                new int[] { GameRenderer.surfaceAspectRatioAction ? 1 : 0 }, 0);

        //draw game items
        for (GameItem gameItem: this.gameItems) gameItem.render(this.shaderProgram);

        //unbind shader program
        this.shaderProgram.unbind();
    }

    /**
     * @return the coordinates of the last item added. Will return (0, 0) if there are no items
     * added yet
     */
    private Coord topLeftCoordsOfLastItem() {
        Coord coords = new Coord(0f, 0f);
        if (this.gameItems.size() > 0) {
            GameItem item = this.gameItems.get(gameItems.size() - 1);
            coords.x = item.getX();
            coords.y = item.getY();
            if (!(item instanceof TextItem)) {
                coords.x -= (item.getWidth() / 2);
                coords.y += (item.getHeight() / 2);
            }
        }
        return coords;
    }

    /**
     * @return the size of the last item added. Will return (0, 0) if there are no items added yet
     */
    private Coord sizeOfLastItem() {
        Coord size = new Coord(0f, 0f);
        if (this.gameItems.size() > 0) {
            size.x = gameItems.get(gameItems.size() - 1).getWidth();
            size.y = gameItems.get(gameItems.size() - 1).getHeight();
        }
        return size;
    }

    //Accessor
    public GameItem getItem(int index) {
        if (index >= gameItems.size() || index < 0) Util.fatalError("HUD.java", "getItem(int)",
                "index '" + index + "' is out of range.");
        return this.gameItems.get(index);
    }

    /**
     * Represents some possible placements for an item to go when added to the HUD
     */
    public enum Placement {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        LEFT_OF_LAST,
        RIGHT_OF_LAST,
        ABOVE_LAST,
        BELOW_LAST
    }

    //Cleanup Method
    public void cleanup() {
        this.shaderProgram.cleanup();
    }
}
