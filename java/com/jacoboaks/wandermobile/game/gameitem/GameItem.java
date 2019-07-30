package com.jacoboaks.wandermobile.game.gameitem;

import android.opengl.GLES20;

import com.jacoboaks.wandermobile.graphics.Model;
import com.jacoboaks.wandermobile.graphics.ShaderProgram;

public class GameItem {

    //Data
    Model model;
    float x, y; //position
    float vx = 0, vy = 0; //velocity

    //Constructor
    public GameItem(Model model, float x, float y) {
        this.model = model;
        this.x = x;
        this.y = y;
    }

    //Update Method
    public void update(float dt) {

        //movement
        this.x += this.vx;
        this.y += this.vy;
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
    public void stopMoving() {
        this.vx = this.vy = 0;
    }
    public void scale(float factor) { this.model.scale(factor); }
}
