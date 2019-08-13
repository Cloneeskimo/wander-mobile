package com.jacoboaks.wandermobile.util;

/**
 * Represents a set of Bounds in aspected or world space
 */
public class Bounds {

    //Data
    public float blx; //bottom-left (aspected or world space)
    public float bly; //bottom-left (aspected or world space)
    public float w; //width (aspected or world space)
    public float h; //height (aspected or world space)

    /**
     * Constructs these Bounds using the given information.
     * @param blx the bottom-left x coordinate of the bounds
     * @param bly the bottom-left y coordinate of the bounds
     * @param w how wide the bounds are
     * @param h how high the bounds are
     */
    public Bounds(float blx, float bly, float w, float h) {
        this.blx = blx;
        this.bly = bly;
        this.w = w;
        this.h = h;
    }

    /**
     * Constructs these Bounds by plugging in zero into the above full constructor.
     */
    public Bounds() {
        this(0f, 0f, 0f, 0f);
    }

    /**
     * Constructs these Bounds when given a center point and a width/height.
     * @param center the center point
     * @param w the width
     * @param h the height
     */
    public Bounds(Coord center, float w, float h) {
        this(center.x - w / 2, center.y - h / 2, w, h);
    }

    /**
     * @param other the Bounds to check for intersection
     * @return whether this Bounds intersects the other given bounds
     */
    public boolean intersects(Bounds other) {
        boolean intersects = false;
        if (this.intersects(other.topLeft())) intersects = true;
        else if (this.intersects(other.topRight())) intersects = true;
        else if (this.intersects(other.bottomLeft())) intersects = true;
        else if (this.intersects(other.bottomRight())) intersects = true;
        return intersects;
    }

    /**
     * @param point the Coord to check for intersection
     * @return whether the given Coord intersects this bounds
     */
    public boolean intersects(Coord point) {
        if (point.x > this.blx && point.x < (this.blx + this.w))
            return point.y > this.bly && point.y < (this.bly + this.h);
        return false;
    }

    /**
     * @return the top left coordinate of these bounds
     */
    public Coord topLeft() {
        return new Coord(this.blx, this.bly + this.h);
    }

    /**
     * @return the top right coordinate of these bounds
     */
    public Coord topRight() {
        return new Coord(this.blx + this.w, this.bly + this.h);
    }

    /**
     * @return the bottom left coordinate of these bounds
     */
    public Coord bottomLeft() {
        return new Coord(this.blx, this.bly);
    }

    /**
     * @return the bottom right coordinate of these bounds
     */
    public Coord bottomRight() {
        return new Coord(this.blx + this.w, this.bly);
    }
}
