package com.jacoboaks.wandermobile.game.gameitem;

import android.view.MotionEvent;

import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.graphics.Model;
import com.jacoboaks.wandermobile.graphics.ShaderProgram;
import com.jacoboaks.wandermobile.graphics.Texture;
import com.jacoboaks.wandermobile.util.Bounds;
import com.jacoboaks.wandermobile.util.Color;
import com.jacoboaks.wandermobile.util.Coord;

/**
 * Extends from ButtonTextItem by providing support for a physical button texture underneath the text.
 */
public class ButtonItem extends ButtonTextItem {

    //Data
    private Texture deselectedTexture; //the texture this ButtonItem takes when deselected
    private Texture selectedTexture; //the texture this ButtonItem takes when selected
    private GameItem underItem; //the background GameItem underneath the text of this ButtonItem
    private float bwidth, bheight; /* the width and height of this entire ButtonItem (including
                            the texture underneath */
    private float padding; /* the amount of padding between the text and the edge of the texture
                            of this ButtonItem. */

    /**
     * Constructs this ButtonItem with the given information.
     * @param text the text to show on this ButtonItem
     * @param font the font to use for the text
     * @param deselectedTexture the texture to use for this ButtonItem when it is not selected
     * @param selectedTexture the texture to use for this ButtonItem when it is selected
     * @param unselectedC the color to use for the text of this ButtonItem when it is not selected
     * @param selectedC the color to use for the text of this ButtonItem when it is selected
     * @param actionCode the action code to return when this ButtonItem is pressed
     * @param padding how much space to put between the text and the edge of the button
     */
    public ButtonItem(String text, Font font, Texture deselectedTexture, Texture selectedTexture, Color unselectedC,
                      Color selectedC, int actionCode, float padding) {
        super(font, text, unselectedC, selectedC, actionCode);
        this.deselectedTexture = deselectedTexture;
        this.selectedTexture = selectedTexture;
        this.padding = padding;
        this.scale = 1.0f;
        this.underItem = new GameItem(new Model(Model.getRectangleModelCoords(this.getTextWidth()
                + (this.padding * 2 * this.scale), this.getTextHeight() + (this.padding * 2 * this.scale)),
                Model.STD_SQUARE_TEX_COORDS(), Model.STD_SQUARE_DRAW_ORDER(), new Material(this.deselectedTexture)),
                this.x, this.y);
        this.bwidth = this.underItem.getWidth();
        this.bheight = this.underItem.getHeight();
    }

    /**
     * Constructs this ButtonItem with the given information.
     * @param text the text to show on this ButtonItem
     * @param font the font to use for the text
     * @param unselectedTexture the texture to use for this ButtonItem when it is not selected
     * @param selectedTexture the texture to use for this ButtonItem when it is selected
     * @param unselectedC the color to use for the text of this ButtonItem when it is not selected
     * @param selectedC the color to use for the text of this ButtonItem when it is selected
     * @param actionCode the action code to return when this ButtonItem is pressed
     * @param padding how much space to put between the text and the edge of the button
     * @param width how wide to make the button
     * @param height how tall to make the button
     */
    public ButtonItem(String text, Font font, Texture unselectedTexture, Texture selectedTexture, Color unselectedC,
                      Color selectedC, int actionCode, float padding, float width, float height) {
        super(font, text, unselectedC, selectedC, actionCode);
        this.deselectedTexture = unselectedTexture;
        this.selectedTexture = selectedTexture;
        this.padding = padding;
        this.underItem = new GameItem(new Model(Model.getRectangleModelCoords(width, height), Model.STD_SQUARE_TEX_COORDS(),
                Model.STD_SQUARE_DRAW_ORDER(), new Material(this.deselectedTexture)), this.x, this.y);

        //apply scale to text to fit within given box dimensions
        float sh = (height - (padding * 2)) / this.getTextHeight();
        float sw = (width - (padding * 2)) / this.getTextWidth();
        super.scale(Math.min(sh, sw));
        this.bwidth = width;
        this.bheight = height;
    }

    /**
     * Renders this ButtonItem using the given ShaderProgram.
     * @param shaderProgram the ShaderProgram to render this ButtonItem with
     */
    @Override
    public void render(ShaderProgram shaderProgram) {
        this.underItem.render(shaderProgram);
        super.render(shaderProgram);
    }

    /**
     * Will update this ButtonItemem based on the user's input.
     * @param e the MotionEvent generated by the user's actions
     * @param touchPos the position of the user's touch in aspected space
     * @return this ButtonTextItem's action code if the user has pressed this ButtonTextItem, and
     * -1 otherwise
     */
    @Override
    public int updateSelection(MotionEvent e, Coord touchPos) {

        //see if finger is over this button
        boolean fingerOver = this.getBounds().intersects(touchPos);

        //set to appropriate selection setting
        if (fingerOver) {
            if (e.getAction() == MotionEvent.ACTION_UP) { //check if they released on this finger
                this.deselect();
                return this.actionCode;
            } else if (!this.selected) this.select();
        } else if (this.selected) this.deselect();

        //return -1 if the user did not press the button
        return -1;
    }

    /**
     * Undergoes the necessary actions to deselect this ButtonItem.
     */
    @Override
    protected void deselect() {
        super.deselect();
        this.underItem.getModel().getMaterial().setTexture(this.deselectedTexture);
    }

    /**
     * Undergoes the necessary actions to select this ButtonItem.
     */
    @Override
    protected void select() {
        super.select();
        this.underItem.getModel().getMaterial().setTexture(this.selectedTexture);
    }

    /**
     * @return the width of just the text portion of this ButtonItem
     */
    public float getTextWidth() {
        return super.getWidth();
    }

    /**
     * @return the height of just the text portion of this ButtonItem
     */
    public float getTextHeight() {
        return super.getHeight();
    }

    /**
     * @return the bounds of this ButtonItem
     */
    @Override
    public Bounds getBounds() {
        return new Bounds(new Coord(this.x, this.y),
                this.getWidth(), this.getHeight());
    }

    /**
     * Scales this ButtonItem by the given factor.
     * @param factor the factor to scale the model by
     */
    @Override
    public void scale(float factor) {
        super.scale(factor);
        this.underItem.scale(factor);
        this.bwidth *= factor;
        this.bheight *= factor;
    }

    /**
     * @return the width of this ButtonItem
     */
    @Override
    public float getWidth() {
        return this.bwidth;
    }

    /**
     * @return the height of this ButtonItem
     */
    @Override
    public float getHeight() {
        return this.bheight;
    }

    /**
     * Sets the x value in world/aspected coordinates of this ButtonItem.
     * @param x the x value to set this ButtonItem to
     */
    @Override
    public void setX(float x) {
        super.setX(x);
        this.underItem.setX(x);
    }

    /**
     * Moves the x value in world/aspected coordinates of this ButtonItem
     * @param x the y value to move this ButtonItem by
     */
    @Override
    public void moveX(float x) {
        super.moveX(x);
        this.underItem.moveX(x);
    }

    /**
     * Sets the y value in world/aspected coordinates of this ButtonItem.
     * @param y the y value to set this ButtonItem to
     */
    @Override
    public void setY(float y) {
        super.setY(y);
        this.underItem.setY(y);
    }

    /**
     * Moves the y value in world/aspected coordinates of this ButtonItem
     * @param y the y value to move this ButtonItem by
     */
    @Override
    public void moveY(float y) {
        super.moveY(y);
        this.underItem.moveY(y);
    }
}
