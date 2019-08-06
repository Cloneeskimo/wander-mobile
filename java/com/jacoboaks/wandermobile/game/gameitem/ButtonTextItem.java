package com.jacoboaks.wandermobile.game.gameitem;

import android.view.MotionEvent;

import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.graphics.Transformation;
import com.jacoboaks.wandermobile.util.Color;
import com.jacoboaks.wandermobile.util.Coord;
import com.jacoboaks.wandermobile.util.Util;

/**
 * Represents a pressable TextItem.
 */
public class ButtonTextItem extends TextItem {

    //Data
    private int actionCode;
    private Color unselectedColor, selectedColor;
    private boolean selected = false;

    /**
     * Constructs this ButtonTextItem with the given information.
     * @param font the font_default to use for the text
     * @param text the text to display
     * @param unselected the color to use when the button is not texture_selected
     * @param selected the color to use when the button is texture_selected
     * @param actionCode the action code to return when this button is pressed
     */
    public ButtonTextItem(Font font, String text, Color unselected, Color selected, int actionCode) {
        super(font, text, new Material(font.getFontSheet(), unselected, true), 0f, 0f);
        this.unselectedColor = unselected;
        this.selectedColor = selected;
        this.actionCode = actionCode;
    }

    /**
     * Constructs this ButtonTextItem by copying another ButtonTextItem.
     * @param other the ButtonTextItem to copy from
     */
    public ButtonTextItem(ButtonTextItem other) {
        super(other);
        this.actionCode = other.actionCode;
        this.unselectedColor = other.unselectedColor;
        this.selectedColor = other.selectedColor;
        this.selected = other.selected;
    }

    /**
     * Will update this ButtonTextITem based on the user's input.
     * @param e the MotionEvent generated by the user's actions
     * @return this ButtonTextItem's action code if the user has pressed this ButtonTextItem, and
     * -1 otherwise
     */
    public int updateSelection(MotionEvent e) {

        //see if finger is over this button
        Coord touchPos = new Coord(e.getX(), e.getY());
        Transformation.screenToNormalized(touchPos);
        Transformation.normalizedToAspected(touchPos);
        boolean fingerOver = false;
        if (touchPos.x > this.x && touchPos.x < (this.x + this.width)) {
            if (touchPos.y < this.y && touchPos.y > (this.y - this.height)) fingerOver = true;
        }

        //set to appropriate color
        if (fingerOver) {
            if (e.getAction() == MotionEvent.ACTION_UP) { //check if they released on this finger
                this.selected = false;
                this.model.getMaterial().setColor(this.unselectedColor);
                return this.actionCode;
            } else if (!this.selected) { //otherwise make sure appropriate color is texture_selected
                this.model.getMaterial().setColor(this.selectedColor);
                this.selected = true;
            }
        } else {
            if (this.selected) {
                this.model.getMaterial().setColor(this.unselectedColor);
                this.selected = false;
            }
        }

        //return -1 if the user did not press the button
        return -1;
    }
}
