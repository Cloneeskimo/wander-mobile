package com.jacoboaks.wandermobile.game.gamecontrol;

import android.view.MotionEvent;

import com.jacoboaks.wandermobile.game.World;
import com.jacoboaks.wandermobile.game.gameitem.Entity;
import com.jacoboaks.wandermobile.graphics.Camera;
import com.jacoboaks.wandermobile.graphics.GameRenderer;
import com.jacoboaks.wandermobile.graphics.Transformation;
import com.jacoboaks.wandermobile.util.Bounds;
import com.jacoboaks.wandermobile.util.Coord;

import java.util.ArrayList;
import java.util.List;

/**
 * WorldControl Class
 * Handles any input given in a WorldLogic.
 */
public class WorldControl {

    //Static Data
    private static final int SUBSTANTIAL_PAN_THRESHOLD = 15; /**
            defines the amount of screen coordinates a finger needs to move within one cycle
            in order for a substantial pan to be detected
        */

    //Data
    private Coord fingerPos; //the current finger position. set to null if the finger leaves the screen
    private List<Bounds> nullInputBounds; //collection of bounds to ignore input for (for buttons) in aspected space
    private boolean currentlyScaling; //whether or not the user is currently scaling
    private boolean justScaledOrPanned; //whether or not the user has just scaled or panned
    private boolean listenForPanning; //whether or not panning should be listened for
    private boolean substantialPanningDetected; //whether or not substantial panning has been detected

    //Default Constructor
    public WorldControl() {
        this.nullInputBounds = new ArrayList<>();
        this.currentlyScaling = false;
        this.listenForPanning = true;
        this.substantialPanningDetected = false;
    }

    /**
     * Acts off of any input given to WorldLogic.
     * @param e the input event to respond to
     * @param world the world to change with the input
     * @return whether the input was handled in any way
     */
    public boolean input(MotionEvent e, World world) {

        //check for null input bounds
        Coord touchPosAspected = new Coord(e.getX(), e.getY());
        Transformation.screenToNormalized(touchPosAspected);
        Transformation.normalizedToAspected(touchPosAspected);
        for (Bounds bounds : this.nullInputBounds) if (bounds.intersects(touchPosAspected)) return false;

        //handle touch
        if (e.getAction() == MotionEvent.ACTION_DOWN) {

            //get touch location
            float x = e.getX();
            float y = e.getY();

            //calculate absolute value of x and y of touch in normalized coords
            x /= ((float)GameRenderer.surfaceWidth / 2);
            x -= 1f;
            y /= ((float)GameRenderer.surfaceHeight / 2);
            y -= 1f;
            float absX = Math.abs(x);
            float absY = Math.abs(y);

            //ignore input if player is moving or camera is panning
            if (!world.getPlayer().isMoving() && !world.getCamera().isRepanning()) {

                //check which quadrant they touched and change square velocity accordingly
                if (absY > absX) { //up or down
                    if (y >= 0.0f) world.getPlayer().impendingMove(0, -1);
                    else world.getPlayer().impendingMove(0, 1);
                } else { //right or left
                    if (x >= 0.0f) world.getPlayer().impendingMove(1, 0);
                    else world.getPlayer().impendingMove(-1, 0);
                }
            }

            //return that input was handled
            return true;

        //handle release
        } else if (e.getAction() == MotionEvent.ACTION_UP) {

            //see if user was trying to tap
            if (this.justScaledOrPanned) {
                this.justScaledOrPanned = false;
            } else if (world.getPlayer().hasImpendingMovement()) { //register tap if user wasn't trying to move
                world.registerTap(e.getX(), e.getY());
            }

            //report finger lift
            this.fingerLifted(world.getPlayer());
            world.getCamera().fingerReleased();

            //return that input was handled
            return true;

        //handle move
        } else if (e.getAction() == MotionEvent.ACTION_MOVE) {

            //check if fingers still on screen from scaling
            if (this.currentlyScaling) this.listenForPanning = false;

            //if first touch
            if (this.fingerPos == null) {
                this.fingerPos = new Coord(e.getX(), e.getY()); //record first touch
                this.listenForPanning = false; //don't listen for panning until second touch received
            }

            //if listen for panning flag is fall
            if (!this.listenForPanning) {
                this.listenForPanning = true; //set it to true for next cycle
                this.substantialPanningDetected = false;
            }

            //listen for pan
            else {
                Coord previousPos = this.fingerPos;
                this.fingerPos = new Coord(e.getX(), e.getY());
                float dX = this.fingerPos.x - previousPos.x;
                float dY = this.fingerPos.y - previousPos.y;

                //check if a substantial pan is being made
                if (!this.substantialPanningDetected) {
                    if (Math.abs(dX) > SUBSTANTIAL_PAN_THRESHOLD || Math.abs(dY) > SUBSTANTIAL_PAN_THRESHOLD)
                        this.substantialPanningDetected = true;
                }

                //pan the camera and stop player movement if a substantial pan is being made
                if (this.substantialPanningDetected) {
                    this.justScaledOrPanned = true;
                    world.getPlayer().stopMoving();
                    world.getCamera().pan(previousPos, this.fingerPos);
                }
            }

            //return that input was handled
            return true;
        }

        //return that input was not handled
        return false;
    }

    /**
     * Adds a given bounds to the list of null input bounds to avoid when detecting control input.
     * @param bounds the bounds to add
     */
    public void addNullInputBounds(Bounds bounds) {
        this.nullInputBounds.add(bounds);
    }

    /**
     * Resets panning and any movement when a finger is lifted from the screen.
     * @param player the player
     */
    private void fingerLifted(Entity player) {

        //stop moving
        player.stopMoving();
        listenForPanning = false;
        this.fingerPos = null;
        this.substantialPanningDetected = false;

        //update scaling fingers
        if (this.currentlyScaling) this.currentlyScaling = false;
    }

    /**
     * Specifically handles and scales input events.
     * @param factor the factor by which scaling has occurred
     * @param camera the camera in us by the WorldLogic
     * @param player the player
     * @return whether or not the input was handled
     */
    public boolean scaleInput(float factor, Camera camera, Entity player) {
        camera.zoom(factor);
        player.stopMoving();
        this.listenForPanning = false;
        this.currentlyScaling = true;
        this.justScaledOrPanned = true;
        return true;
    }
}
