package com.jacoboaks.wandermobile.game.gameitem;

import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.graphics.Material;
import com.jacoboaks.wandermobile.graphics.Model;
import com.jacoboaks.wandermobile.graphics.Texture;
import com.jacoboaks.wandermobile.util.Color;
import com.jacoboaks.wandermobile.util.Coord;

/**
 * @purpose is to represent a specific type of GameItem which is a tile existing in the grid world.
 * Coordinates should be manipulated using world position rather than direction position as it
 * represents the place in the grid seperate of the model sizes.
 */
public class Tile extends GameItem {

    //Static Data
    public static final float STD_MOVE_ANIMATION_SPEED = 0.03f;
    private static final float IMPENDING_MOVEMENT_TIME = 180f;

    //Data
    private int wx, wy; //world position
    private int iwx, iwy; //delta impending world position
    private float impendingMovementTime; //time until and impending move is undergone
    private float tgtX = 0, tgtY = 0; //target x and y (of movement animation)
    private boolean symbolTile; //whether or not this tile is a symbol tile (true) or texture (false)
    private boolean movementAnimation = false; //whether or not the tile is currently undergoing a moving animation
    private char symbol; //symbol of the tile

    //Symbol Constructor
    public Tile(Font font, char symbol, Color color, int x, int y) {
        super(new Model(Model.STD_SQUARE_MODEL_COORDS(), font.getCharacterTextureCoordinates(symbol, false),
                Model.STD_SQUARE_DRAW_ORDER(), new Material(font.getFontSheet(), color, true)),
                (float)x * Model.STD_SQUARE_SIZE, (float)y * Model.STD_SQUARE_SIZE);
        this.wx = wx;
        this.wy = wy;
        this.symbolTile = true;
        this.symbol = symbol;
    }

    //Texture Constructor
    public Tile(Texture texture, int x, int y) {
        super(new Model(Model.STD_SQUARE_MODEL_COORDS(), Model.STD_SQUARE_TEX_COORDS(), Model.STD_SQUARE_DRAW_ORDER(),
                new Material(texture)), (float)x * Model.STD_SQUARE_SIZE, (float)y * Model.STD_SQUARE_SIZE);
        this.wx = wx;
        this.wy = wy;
        this.symbolTile = false;
        this.symbol = 0;
    }

    //Update Method
    @Override
    public void update(float dt) {
        super.update(dt);

        //impending movement
        if (this.impendingMovementTime > 0) {
            this.impendingMovementTime -= dt;
            if (this.impendingMovementTime <= 0) {
                this.moveWorldPos(this.iwx, this.iwy);
                this.resetImpendingMovement();
            }
        }

        //stop move animations
        if (this.movementAnimation) {

            //check if destination reached
            boolean destinationReached = false;
            if (this.vx > 0 && this.x >= this.tgtX) { //moving right
                destinationReached = true;
            } else if (this.vx < 0 && this.x <= this.tgtX) { //moving left
                destinationReached = true;
            } else if (this.vy > 0 && this.y >= this.tgtY) { //moving down
                destinationReached = true;
            } else if (this.vy < 0 && this.y <= this.tgtY) { //moving up
                destinationReached = true;
            }

            //stop movement if so
            if (destinationReached) {
                this.vx = this.vy = 0;
                this.x = this.tgtX;
                this.y = this.tgtY;
                this.movementAnimation = false;
            }
        }
    }

    //Single-Axis Moving Animation Methods
    public void moveWorldX(int dwx) { this.moveWorldPos(dwx, 0); }
    public void moveWorldY(int dwy) { this.moveWorldPos(0, dwy); }

    /**
     * @purpose is to begin a movement animation to the given world position
     * @param dwx the amount of world x to move by
     * @param dwy the amount of world y to move by
     */
    public void moveWorldPos(int dwx, int dwy) {

        //set target x and target y
        this.tgtX = this.x + (dwx * Model.STD_SQUARE_SIZE);
        this.tgtY = this.y + (dwy * Model.STD_SQUARE_SIZE);

        //set velocity
        this.vx = Tile.STD_MOVE_ANIMATION_SPEED * (float)dwx;
        this.vy = Tile.STD_MOVE_ANIMATION_SPEED * (float)dwy;

        //set moving flag to true
        this.movementAnimation = true;
    }

    /**
     * @purpose is to register an impending movement
     * @param dwx the delta world x for the impending movement
     * @param dwy the delta world y for the impending movement
     */
    public void impendingMove(int dwx, int dwy) {
        this.iwx = dwx;
        this.iwy = dwy;
        this.impendingMovementTime = Tile.IMPENDING_MOVEMENT_TIME;
    }

    /**
     * @purpose is to reset any impending movement for this game item
     */
    public void resetImpendingMovement() {
        this.iwx = this.iwy = 0;
        this.impendingMovementTime = 0f;
    }

    /**
     * @purpose is to stop any movement or impending movement
     */
    @Override
    public void stopMoving() {
        if (this.movementAnimation) return; //ignore if already undergoing a movement
        super.stopMoving();
        this.resetImpendingMovement();
    }

    //Mutators
    public void setWorldX(int wx) { this.wx = wx; }
    public void setWorldY(int wy) { this.wy = wy; }
    public void setWorldPosition(int wx, int wy) { this.wx = (int)wx; this.wy = (int)wy; }

    //Accessors
    public int getWorldX() { return this.wx; }
    public int getWorldY() { return this.wy; }
    public Coord getWorldPosition() { return new Coord(this.wx, this.wy); }
    public char getSymbol() { return this.symbol; }
    public boolean isMoving() { return this.movementAnimation; }
}