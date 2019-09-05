package com.jacoboaks.wandermobile.game.gameitem;

import android.view.MotionEvent;

import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.graphics.ShaderProgram;
import com.jacoboaks.wandermobile.util.Color;
import com.jacoboaks.wandermobile.util.Coord;

/**
 * Represents a pressable TextItem.
 */
public class ButtonTextItem extends TextItem {

    //Data
    private Color deselectedColor, selectedColor;
    protected int actionCode;
    protected boolean selected = false;

    /**
     * Constructs this ButtonTextItem with the given information.
     * @param font the font to use for the text
     * @param text the text to display
     * @param deselectedColor the color to use when the button is not selected
     * @param selectedColor the color to use when the button is selected
     * @param actionCode the action code to return when this button is pressed
     */
    public ButtonTextItem(Font font, String text, Color deselectedColor, Color selectedColor, int actionCode) {
        super(font, text, new Material(font.getFontSheet(), deselectedColor, true), 0f, 0f);
        this.deselectedColor = deselectedColor;
        this.selectedColor = selectedColor;
        this.actionCode = actionCode;
    }

    /**
     * Constructs this ButtonTextItem by copying another ButtonTextItem.
     * @param other the ButtonTextItem to copy from
     */
    public ButtonTextItem(ButtonTextItem other) {
        super(other);
        this.actionCode = other.actionCode;
        this.deselectedColor = other.deselectedColor;
        this.selectedColor = other.selectedColor;
        this.selected = other.selected;
    }

    /**
     * Updates this ButtonTextItem based on the user's input.
     * @param e the MotionEvent generated by the user's actions
     * @param touchPos the position of the user's touch in aspected space
     * @return this ButtonTextItem's action code if the user has pressed this ButtonTextItem, and
     * -1 otherwise
     */
    public int updateSelection(MotionEvent e, Coord touchPos) {

        //see if finger is over this button
        boolean fingerOver = this.getBounds().intersects(touchPos);

        //set to appropriate color
        if (fingerOver) {
            if (e.getAction() == MotionEvent.ACTION_UP) {
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
    protected void deselect() {
        this.model.getMaterial().setColor(this.deselectedColor);
        this.selected = false;
    }

    /**
     * Undergoes the necessary actions to select this ButtonItem.
     */
    protected void select() {
        this.model.getMaterial().setColor(this.selectedColor);
        this.selected = true;
    }

    /**
     * Renders this ButtonTextItem.
     * @param shaderProgram the ShaderProgram to render this ButtonTextItem with
     */
    @Override
    public void render(ShaderProgram shaderProgram) {
        super.render(shaderProgram);
    }

    /**
     * Scales this ButtonTextItem by the given factor.
     * @param factor the factor to scale by
     */
    @Override
    public void scale(float factor) {
        super.scale(factor);
    }

    /**
     * Sets the action code of this ButtonTextItem to the given code.
     * @param actionCode the action code to assign to this ButtonTextItem.
     */
    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }
}
