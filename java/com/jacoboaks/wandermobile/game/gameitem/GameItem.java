package com.jacoboaks.wandermobile.game.gameitem;

import android.opengl.GLES20;

import com.jacoboaks.wandermobile.graphics.Model;
import com.jacoboaks.wandermobile.graphics.ShaderProgram;

/**
 * @purpose is to represent a single game item with a model, position, and velocity
 */
public abstract class GameItem {

    //Data
    Model model; //model
    float x, y; //world position
    float vx = 0, vy = 0; //velocity

    /**
     * @purpose is to construct this GameItem
     * @param model the model to represent the GameItem
     * @param x the world x position
     * @param y the world y position
     */
    public GameItem(Model model, float x, float y) {
        this.model = model;
        this.x = x;
        this.y = y;
    }

    //Update Method
    public void update(float dt) {

        //update position with velocity
        this.x += this.vx;
        this.y += this.vy;
    }

    //Draw Method
    public void render(ShaderProgram shaderProgram) {

        //set x and y uniforms
        GLES20.glUniform1fv(shaderProgram.getUniformIndex("x"), 1,
                new float[] { x }, 0);
        GLES20.glUniform1fv(shaderProgram.getUniformIndex("y"), 1,
                new float[] { y }, 0);

        //draw model
        this.model.render(shaderProgram);
    }

    //Accessors
    public float getX() { return this.x; }
    public float getY() { return this.y; }
    public abstract float getWidth();
    public abstract float getHeight();

    //Mutators
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void moveX(float dx) { this.x += dx; }
    public void moveY(float dy) { this.y += dy; }
    public void setVx(float vx) { this.vx = vx; }
    public void setVy(float vy) { this.vy = vy; }
    public void stopMoving() {
        this.vx = this.vy = 0;
    }
    public void scale(float factor) { this.model.scale(factor); }
}
