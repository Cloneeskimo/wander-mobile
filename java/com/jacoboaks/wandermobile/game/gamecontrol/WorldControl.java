package com.jacoboaks.wandermobile.game.gamecontrol;

import android.view.MotionEvent;

import com.jacoboaks.wandermobile.game.World;
import com.jacoboaks.wandermobile.game.gameitem.Entity;
import com.jacoboaks.wandermobile.game.gameitem.GameItem;
import com.jacoboaks.wandermobile.game.gameitem.Tile;
import com.jacoboaks.wandermobile.graphics.Camera;
import com.jacoboaks.wandermobile.graphics.FollowingCamera;
import com.jacoboaks.wandermobile.util.Coord;

import java.util.List;

/**
 * WorldControl Class
 * @purpose is to handle any input given in a WorldLogic
 */
public class WorldControl {

    //Static Data
    private static final int SUBSTANTIAL_PAN_THRESHOLD = 15; /**
            defines the amount of screen coordinates a finger needs to move within one cycle
            in order for a substantial pan to be detected
        */

    //Data
    private Coord fingerPos; //the current finger position. set to null if the finger leaves the screen
    private boolean substantialPanningDetected; //whether or not substantial panning has been detected
    private boolean listenForPanning; //whether or not panning should be listened for
    private boolean currentlyScaling; //whether or not the user is currently scaling
    private boolean justScaledOrPanned; //whether or not the user has just scaled or panned
    private float touchDownTime = 0;

    //Default Constructor
    public WorldControl() {
        this.substantialPanningDetected = false;
        this.listenForPanning = true;
        this.currentlyScaling = false;
    }

    /**
     * @purpose is to act off of any input given to WorldLogic
     * @param e the input event to respond to
     * @param world the world to change with the input
     * @param width the width of the surface
     * @param height the height of the surface
     * @return whether the input was handled in any way
     */
    public boolean input(MotionEvent e, World world, int width, int height) {

        //handle touch
        if (e.getAction() == MotionEvent.ACTION_DOWN) {

            //get touch location
            float x = e.getX();
            float y = e.getY();

            //calculate absolute value of x and y of touch in normalized coords
            x /= ((float)width / 2);
            x -= 1f;
            y /= ((float)height / 2);
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
                world.registerTap(e.getX(), e.getY(), width, height);
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
                    world.getCamera().pan(width, height, previousPos, this.fingerPos);
                }
            }

            //return that input was handled
            return true;
        }

        //return that input was not handled
        return false;
    }

    /**
     * @purpose is to reset panning and any movement when a finger is lifted from the screen
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
     * @purpose is to specifically handle and scale input events
     * @param factor the factor by which scaling has occured
     * @param camera the camera in us by the WorldLogic
     * @param player the player
     * @return
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
