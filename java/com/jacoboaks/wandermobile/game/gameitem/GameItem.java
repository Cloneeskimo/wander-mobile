package com.jacoboaks.wandermobile.game.gameitem;

import android.opengl.GLES20;

import com.jacoboaks.wandermobile.graphics.Model;
import com.jacoboaks.wandermobile.graphics.ShaderProgram;

public class GameItem {

    //Static Data
    private static final float IMPENDING_MOVEMENT_TIME = 180;

    //Data
    Model model;
    float x, y; //position
    float vx, vy; //velocity
    float ivx, ivy; //impending velocity
    float impendingMovementTime; //time until impending velocity kicks in

    //Constructor
    public GameItem(Model model, float x, float y) {
        this.model = model;
        this.x = x;
        this.y = y;
        this.vx = this.vy = this.ivx = this.ivy = this.impendingMovementTime = 0;
    }

    //Update Method
    public void update(float dt) {

        //movement
        this.x += this.vx;
        this.y += this.vy;

        //impending movement
        if (this.impendingMovementTime > 0) {
            this.impendingMovementTime -= dt;
            if (this.impendingMovementTime <= 0) {
                this.vx = this.ivx;
                this.vy = this.ivy;
                this.resetImpendingMovement();
            }
        }
    }

    //Draw Method
    public void draw(ShaderProgram shaderProgram) {

        //set x and y uniforms
        GLES20.glUniform1fv(shaderProgram.getUniformIndex("x"), 1,
                new float[] { x }, 0);
        GLES20.glUniform1fv(shaderProgram.getUniformIndex("y"), 1,
                new float[] { y }, 0);

        //draw model
        this.model.draw(shaderProgram);
    }

    /**
     * @purpose is to reset any impending movement for this game item
     */
    public void resetImpendingMovement() {
        this.ivx = this.ivy = this.impendingMovementTime = 0;
    }

    //Accessors
    public float getX() { return this.x; }
    public float getY() { return this.y; }

    //Mutators
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void moveX(float dx) { this.x += dx; }
    public void moveY(float dy) { this.y += dy; }
    public void setVx(float vx) { this.vx = vx; }
    public void setVy(float vy) { this.vy = vy; }
    public void setIvx(float ivx) { this.ivx = ivx; this.impendingMovementTime = IMPENDING_MOVEMENT_TIME; }
    public void setIvy(float ivy) { this.ivy = ivy; this.impendingMovementTime = IMPENDING_MOVEMENT_TIME; }
    public void stopMoving(boolean stopImpendingMovement) {
        this.vx = this.vy = 0;
        if (stopImpendingMovement) this.resetImpendingMovement();
    }
}
