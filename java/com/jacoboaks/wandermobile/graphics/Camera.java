package com.jacoboaks.wandermobile.graphics;

import com.jacoboaks.wandermobile.util.Coord;

/**
 * Camera Class
 * @purpose is to simulate a Camera by maintaining a position and a zoom
 */
public class Camera {

    //Data
    float x, y; //position
    float vx, vy; //velocity
    float zoom;

    //Full Constructor
    public Camera(float x, float y, float zoom) {
        this.x = x;
        this.y = y;
        this.vx = this.vy = 0;
        this.zoom = zoom;
    }

    //Partial Constructor
    public Camera(float x, float y) {
        this(x, y, 1.0f);
    }

    //Default Constructor
    public Camera() {
        this(0.0f, 0.0f, 1.0f);
    }

    //Update Method
    public void update() {
        this.x += this.vx;
        this.y += this.vy;
    }

    /**
     * @purpose is to pan the factor based a deltaX and deltaY defined in screen space
     * @param width the width of the screen
     * @param height the height of the screen
     */
    public void pan(float width, float height, Coord oldPos, Coord newPos) {

        //copy coordinates
        Coord oldp = new Coord(oldPos);
        Coord newp = new Coord(newPos);

        //convert to world coordinates
        oldp = this.screenToWorldCoords(width, height, oldp);
        newp = this.screenToWorldCoords(width, height, newp);

        //move camera
        this.moveX(oldp.x - newp.x);
        this.moveY(newp.y - oldp.y);
    }

    public Coord screenToWorldCoords(float width, float height, Coord screen) {

//        System.out.println("screen coords: " + screen.x + ", " + screen.y);

        //convert to normalized device coordinates
        screen.x /= width / 2;
        screen.y /= height / 2;
        screen.x--;
        screen.y--;

//        System.out.println("normalized coords: " + screen.x + ", " + screen.y);

        //convert to projected coordinates by adjusting for aspect ratio
        float aspectRatio = width / height;
        if (aspectRatio < 1) screen.y /= aspectRatio;
        else screen.x *= aspectRatio;

//        System.out.println("projected coords: " + screen.x + ", " + screen.y);

        //convert to world coordinates by adjusting for zoom and adding camera position
        screen.x /= this.zoom;
        screen.y /= this.zoom;
        screen.x += this.x;
        screen.y -= this.y;

//        System.out.println("world coords: " + screen.x + ", " + screen.y);

        //return adjusted coord
        return screen;
    }

    //Accessors
    public float getX() { return this.x; }
    public float getY() { return this.y; }
    public float getZoom() { return this.zoom; }

    //Mutators
    public void setVx(float vx) { this.vx = vx; }
    public void setVy(float vy) { this.vy = vy; }
    public void zoom(float vz) { this.zoom *= vz; }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void moveX(float x) { this.x += x; }
    public void moveY(float y) { this.y += y; }
    public void setZoom(float zoom) { this.zoom = zoom; }
}
