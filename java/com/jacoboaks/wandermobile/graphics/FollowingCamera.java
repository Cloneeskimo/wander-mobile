package com.jacoboaks.wandermobile.graphics;

import com.jacoboaks.wandermobile.game.gameitem.GameItem;
import com.jacoboaks.wandermobile.util.Coord;

/**
 * Extends the Camera class with the ability to lock onto a given GameItem.
 */
public class FollowingCamera extends Camera {

    //Static Data
    private static final float STD_TIME_UNTIL_RETURN = 750; /* the standard amount of time after
        the user pans the camera to wait before returning to the followee */
    private static final float STD_REPAN_ANIMATION_SPEED = 0.093f; /* the speed at which the
        repanning animation should occur */

    //Data
    private boolean panLocked; //whether panning is locked or not
    private boolean recentlyPanned; //whether panning has recently occurred or not
    private boolean isRepanning; //whether re-panning is occurring currently
    private boolean brokeAway; //whether the player has broken away the camera following
    private GameItem followee; //the game item to follow
    private float timeUntilReturn; //the time after the last pan until the camera should return to its followee

    /**
     * Construct this FollowingCamera with the given information.
     * @param zoom the camera's zoom
     * @param followee the GameItem to follow
     * @param panLocked whether panning should be locked or not
     */
    public FollowingCamera(float zoom, GameItem followee, boolean panLocked) {
        super(followee.getX(), followee.getY(), zoom);
        this.followee = followee;
        this.panLocked = panLocked;
        this.isRepanning = false;
        this.brokeAway = false;
    }

    /**
     * Pans the camera if panning is not locked.
     * @param oldPos the old world position
     * @param newPos the new world position to pan to
     */
    @Override
    public void pan(Coord oldPos, Coord newPos) {
        if (!this.panLocked) super.pan(oldPos, newPos);
        this.recentlyPanned = true;
        this.isRepanning = false;
        this.brokeAway = true;
        this.vx = this.vy = 0;
    }

    /**
     * Handles the user's finger release. If they have recently panned, will start
     * the countdown to when the camera re-pans over to the followee.
     */
    public void fingerReleased() {
        if (this.recentlyPanned) this.timeUntilReturn = FollowingCamera.STD_TIME_UNTIL_RETURN;
        this.recentlyPanned = false;
    }

    //Update Method
    @Override
    public void update(float dt) {
        super.update(dt);

        //update re-pan countdown
        if (this.timeUntilReturn > 0f) {
            this.timeUntilReturn -= dt;

            //check if re-panning should begin
            if (this.timeUntilReturn <= 0.01f) {

                //figure out distance between camera and followee
                float dx = this.followee.getX() - this.x;
                float dy = this.followee.getY() - this.y;

                //set velocity and repanning flag
                this.isRepanning = true;
                this.vx = dx * FollowingCamera.STD_REPAN_ANIMATION_SPEED;
                this.vy = dy * FollowingCamera.STD_REPAN_ANIMATION_SPEED;
            }
        }

        //check if re-panning destination has been reached
        if (this.isRepanning) {

            //check if destination reached
            boolean destinationReached = false;

            if (this.vx > 0 && this.x >= this.followee.getX()) { //moving right
                destinationReached = true;
            } else if (this.vx < 0 && this.x <= this.followee.getX()) { //moving left
                destinationReached = true;
            } else if (this.vy > 0 && this.y >= this.followee.getY()) { //moving down
                destinationReached = true;
            } else if (this.vy < 0 && this.y <= this.followee.getY()) { //moving up
                destinationReached = true;
            }

            //stop movement if so
            if (destinationReached) {
                this.vx = this.vy = 0;
                this.x = this.followee.getX();
                this.y = this.followee.getY();
                this.isRepanning = false;
                this.brokeAway = false;
            }
        }

        //follow followee
        if (!this.brokeAway) {
            this.x = this.followee.getX();
            this.y = this.followee.getY();
        }
    }

    //Accessor
    public boolean isRepanning() { return this.isRepanning; }

    //Mutators
    public void lockPanning() { this.panLocked = true; }
    public void unlockPanning() { this.panLocked = false; }
    public void setPanLocked(boolean panLocked) { this.panLocked = panLocked; }
}
