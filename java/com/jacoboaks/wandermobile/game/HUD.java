package com.jacoboaks.wandermobile.game;

import android.opengl.GLES20;
import android.view.MotionEvent;
import android.widget.Button;

import com.jacoboaks.wandermobile.R;
import com.jacoboaks.wandermobile.game.gameitem.ButtonTextItem;
import com.jacoboaks.wandermobile.game.gameitem.GameItem;
import com.jacoboaks.wandermobile.graphics.GameRenderer;
import com.jacoboaks.wandermobile.graphics.ShaderProgram;
import com.jacoboaks.wandermobile.graphics.Transformation;
import com.jacoboaks.wandermobile.util.Coord;
import com.jacoboaks.wandermobile.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds many GameItems to be rendered over top of a World.
 */
public class HUD {

    //Data
    private Map<String, GameItem> gameItems;
    private ShaderProgram shaderProgram;
    private GameItem lastAdded;

    /**
     * Constructs this HUD.
     */
    public HUD() {
        this.gameItems = new HashMap<>();
        this.initShaderProgram();
    }

    /**
     * Initializes the HUD's shader program.
     */
    private void initShaderProgram() {

        //create shader program, load shaders, and link them.
        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.loadShader(R.raw.shader_hudvertex, GLES20.GL_VERTEX_SHADER);
        this.shaderProgram.loadShader(R.raw.shader_hudfragment, GLES20.GL_FRAGMENT_SHADER);
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
     * @param tag the tag to identify this item with
     * @param item the item to add
     * @param x the normalized x to place the item at
     * @param y the normalized y to place the item at
     */
    public void addItem(String tag, GameItem item, float x, float y) {
        Coord coord = new Coord(x, y);
        Transformation.normalizedToAspected(coord);
        item.setX(coord.x);
        item.setY(coord.y);
        this.gameItems.put(tag, item);
        this.lastAdded = item;
    }

    /**
     * Adds a new GameItem to this HUD using the given information.
     * @param tag the tag to identify this item with
     * @param item the item to add
     * @param placement the placement for the item to go. can be relative to the last item added
     *                  or to the various corners of the screen.
     * @param padding the amount of padding between the given placement and the item
     */
    public void addItem(String tag, GameItem item, Placement placement, float padding) {

        //create coordinate for new position
        Coord newPos = new Coord();

        //place item
        switch (placement) {
            case TOP_LEFT:
                newPos = new Coord(-1f, -1f);
                Transformation.normalizedToAspected(newPos);
                newPos.x += (padding + item.getWidth() / 2);
                newPos.y -= (padding + item.getHeight() / 2);
                break;
            case TOP_MIDDLE:
                newPos = new Coord(0f, -1f);
                Transformation.normalizedToAspected(newPos);
                newPos.y -= (padding + item.getHeight() / 2);
                break;
            case TOP_RIGHT:
                newPos = new Coord(1f, -1f);
                Transformation.normalizedToAspected(newPos);
                newPos.x -= (padding + item.getWidth() / 2);
                newPos.y -= (padding + item.getHeight() / 2);
                break;
            case MIDDLE:
                newPos = new Coord(0f, 0f);
                break;
            case BOTTOM_LEFT:
                newPos = new Coord(-1f, 1f);
                Transformation.normalizedToAspected(newPos);
                newPos.x += (padding + item.getWidth() / 2);
                newPos.y += (padding + item.getHeight() / 2);
                break;
            case BOTTOM_MIDDLE:
                newPos = new Coord(0f, 1f);
                Transformation.normalizedToAspected(newPos);
                newPos.y += (padding + item.getHeight() / 2);
                break;
            case BOTTOM_RIGHT:
                newPos = new Coord(1f, 1f);
                Transformation.normalizedToAspected(newPos);
                newPos.x -= (padding + item.getWidth() / 2);
                newPos.y += (padding + item.getHeight() / 2);
                break;
            case LEFT_OF_LAST:
                newPos = this.coordsOfLastItem();
                newPos.x -= (padding + item.getWidth());
                break;
            case RIGHT_OF_LAST:
                newPos = this.coordsOfLastItem();
                Coord size1 = this.sizeOfLastItem();
                newPos.x += (padding + size1.x);
                break;
            case ABOVE_LAST:
                newPos = this.coordsOfLastItem();
                newPos.y += (padding + item.getHeight());
                break;
            case BELOW_LAST:
                newPos = this.coordsOfLastItem();
                Coord size2 = this.sizeOfLastItem();
                newPos.y -= (padding + size2.y);
                break;
        }

        //set position
        item.setX(newPos.x);
        item.setY(newPos.y);

        //add item
        this.gameItems.put(tag, item);
        this.lastAdded = item;
    }

    /**
     * Loops through any ButtonTextItems in this HUD and updates their selections.
     * @param e the MotionEvent to consider
     * @return -1 if no ButtonTextItems are selected, the action code of the selected ButtonTextItem if
     * one is selected
     */
    public int updateButtonSelections(MotionEvent e) {

        //loop through buttons
        int actionCode = -1;
        for (String tag : this.gameItems.keySet()) {
            if (actionCode == -1) {
                GameItem item = this.gameItems.get(tag);
                if (item instanceof ButtonTextItem) actionCode = ((ButtonTextItem)item).updateSelection(e);
            }
        }

        //return the found action code
        return actionCode;
    }

    /**
     * Will render the GameItems in this HUD. If there is an object whose tag starts with a capital
     * Z, it will be rendered last.
     */
    public void render() {

        //draw shapes
        this.shaderProgram.bind();

        //update aspect ratio and aspect ratio action
        GLES20.glUniform1fv(this.shaderProgram.getUniformIndex("aspectRatio"), 1,
                new float[] { GameRenderer.surfaceAspectRatio }, 0);
        GLES20.glUniform1iv(this.shaderProgram.getUniformIndex("aspectRatioAction"), 1,
                new int[] { GameRenderer.surfaceAspectRatioAction ? 1 : 0 }, 0);

        //draw game items
        GameItem lastRender = null;
        for (String tag: this.gameItems.keySet()) {
            if (tag.equals("AREA_NAME")) {
                System.out.println("area name");
            }
            if (tag.charAt(0) == 'Z') lastRender = this.gameItems.get(tag);
            else this.gameItems.get(tag).render(this.shaderProgram);
        }
        if (lastRender != null) lastRender.render(this.shaderProgram);

        //unbind shader program
        this.shaderProgram.unbind();
    }

    /**
     * @return the coordinates of the last item added. Will return (0, 0) if there are no items
     * added yet
     */
    private Coord coordsOfLastItem() {
        Coord coords = new Coord(0f, 0f);
        if (this.lastAdded != null) {
            coords.x = this.lastAdded.getX();
            coords.y = this.lastAdded.getY();
        }
        return coords;
    }

    /**
     * @return the size of the last item added. Will return (0, 0) if there are no items added yet
     */
    private Coord sizeOfLastItem() {
        Coord size = new Coord(0f, 0f);
        if (this.lastAdded != null) {
            size.x = this.lastAdded.getWidth();
            size.y = this.lastAdded.getHeight();
        }
        return size;
    }

    //Accessor
    public GameItem getItem(String tag) { return this.gameItems.get(tag); }

    /**
     * Represents some possible placements for an item to go when added to the HUD
     */
    public enum Placement {
        TOP_LEFT,
        TOP_MIDDLE,
        TOP_RIGHT,
        MIDDLE,
        BOTTOM_LEFT,
        BOTTOM_MIDDLE,
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
