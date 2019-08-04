package com.jacoboaks.wandermobile.graphics;

import com.jacoboaks.wandermobile.util.Coord;

/**
 * Camera Class
 * Simulate a Camera by maintaining a position, velocity, and zoom.
 */
public class Camera {

    //Static Data
    private final static float MIN_ZOOM = 0.07f;
    private final static float MAX_ZOOM = 1.8f;
    private final static float DEFAULT_ZOOM = 0.7f;

    //Data
    float x, y; //position
    float vx, vy; //velocity
    float zoom;

    /**
     * Constructs this Camera with all of its information.
     * @param x the world x of this Camera
     * @param y the world y of this Camera
     * @param zoom the zoom of this Camera
     */
    public Camera(float x, float y, float zoom) {
        this.x = x;
        this.y = y;
        this.vx = this.vy = 0;
        this.zoom = Math.max(Camera.MIN_ZOOM, Math.min(Camera.MAX_ZOOM, zoom));
    }

    /**
     * Constructs this Camera by only defining its position.
     * @param x the world x of this Camera
     * @param y the world y of this Camera
     */
    public Camera(float x, float y) {
        this(x, y, Camera.DEFAULT_ZOOM);
    }

    /**
     * Constructs this Camera at the (0, 0) and with the default zoom
     */
    public Camera() {
        this(0.0f, 0.0f, Camera.DEFAULT_ZOOM);
    }

    //Update Method
    public void update(float dt) {
        this.x += this.vx;
        this.y += this.vy;
    }

    /**
     * Pans this Camera based on a deltaX and deltaY defined in screen space.
     * @param oldPos the old screen position
     * @param newPos the new screen position the user has dragged to from oldPos
     */
    public void pan(Coord oldPos, Coord newPos) {

        //copy coordinates
        Coord oldp = new Coord(oldPos);
        Coord newp = new Coord(newPos);

        //convert to world coordinates
        Transformation.screenToWorld(oldp, this);
        Transformation.screenToWorld(newp, this);

        //move camera
        this.moveX(oldp.x - newp.x);
        this.moveY(-(newp.y - oldp.y));
    }

    //Accessors
    public float getX() { return this.x; }
    public float getY() { return this.y; }
    public float getZoom() { return this.zoom; }

    //Mutators
    public void setVx(float vx) { this.vx = vx; }
    public void setVy(float vy) { this.vy = vy; }
    public void zoom(float vz) { this.setZoom(this.zoom * vz); }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void moveX(float x) { this.x += x; }
    public void moveY(float y) { this.y += y; }
    public void setZoom(float zoom) { this.zoom = Math.min(Camera.MAX_ZOOM, Math.max(Camera.MIN_ZOOM, zoom)); }
}
