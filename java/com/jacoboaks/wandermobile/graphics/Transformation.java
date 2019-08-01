package com.jacoboaks.wandermobile.graphics;

import com.jacoboaks.wandermobile.util.Coord;

/**
 * @purpose is to provide methods for converting coordinates between different spaces. The spaces
 * are defined in the following order:
 *  - screen
 *  - normalized
 *  - aspect ratio
 *  - world
 *  - grid
 */
public class Transformation {

    /**
     * @purpose is to convert screen coordinates to normalized coordinates
     * @param coords the coordinates to convert
     * @param width the width of the screen
     * @param height the height of the screen
     */
    public static void screenToNormalized(Coord coords, int width, int height) {

        //convert x
        coords.x /= width;
        coords.x *= 2;
        coords.x -= 1;

        //convert y
        coords.y /= height;
        coords.y *= 2;
        coords.y -= 1;
        coords.y *= -1;
    }

    /**
     * @purpose is to convert normalized coordinates to aspect ratio coordinates
     * @param coords the coordinates to convert
     * @param aspectRatio the aspect ratio of the screen
     */
    public static void normalizedToAspected(Coord coords, float aspectRatio) {
        if (aspectRatio >= 1.0f) {
            coords.x *= aspectRatio;
        } else {
            coords.y /= aspectRatio;
        }
    }

    /**
     * @purpose is to convert aspect ratio coordinates to world coordinates
     * @param coords the coordinates to convert
     * @param camera the camera whose zoom and position to be taken into account
     */
    public static void aspectedToWorld(Coord coords, Camera camera) {

        //convert x
        coords.x /= camera.getZoom();
        coords.x += camera.getX();

        //convert y
        coords.y /= camera.getZoom();
        coords.y += camera.getY();
    }

    /**
     * @purpose is to convert screen coordinates to world coordinates
     * @param coords the coordinates to convert
     * @param width the width of the screen
     * @param height the height of the screen
     * @param camera the camera whose zoom and position to be taken account of
     */
    public static void screenToWorld(Coord coords, int width, int height, Camera camera) {
        screenToNormalized(coords, width, height);
        normalizedToAspected(coords, (float)width / (float)height);
        aspectedToWorld(coords, camera);
    }

    /**
     * @purpose is to convert screen coordinates to grid coordinates
     * @param coords the coordinates to convert
     * @param width the width of the screen
     * @param height the height of the screen
     * @param camera the camera whose zoom and position to be taken account of
     */
    public static void screenToGrid(Coord coords, int width, int height, Camera camera) {
        screenToWorld(coords, width, height, camera);
        worldToGrid(coords);
    }

    /**
     * @purpose is to convert world coordinates to grid coordinates
     * @param coords the coordinates to convert
     */
    public static void worldToGrid(Coord coords) {
        if (coords.x < 0) coords.x -= (Model.STD_SQUARE_SIZE / 2);
        else coords.x += (Model.STD_SQUARE_SIZE / 2);
        if (coords.y < 0) coords.y -= (Model.STD_SQUARE_SIZE / 2);
        else coords.y += (Model.STD_SQUARE_SIZE / 2);
        coords.x = (int)(coords.x / Model.STD_SQUARE_SIZE);
        coords.y = (int)(coords.y
                / Model.STD_SQUARE_SIZE);
    }

    /**
     * @purpose is to convert grid coordinates to world coordinates
     * @param coords the coordinates to convert
     */
    public static void gridToWorld(Coord coords) {
        coords.x *= Model.STD_SQUARE_SIZE;
        coords.y *= Model.STD_SQUARE_SIZE;
    }

    /**
     * @purpose is to convert world coordinates to aspect ratio coordinates
     * @param coords the coordinates to convert
     * @param camera the camera whose zoom and position to be taken into account
     */
    public static void worldToAspected(Coord coords, Camera camera) {

        //convert x
        coords.x -= camera.getX();
        coords.x *= camera.getZoom();

        //convert y
        coords.y -= camera.getY();
        coords.y *= camera.getZoom();
    }

    /**
     * @purpose is to convert aspect ratio coordinates to normalized coordinates
     * @param coords the coordinates to convert
     * @param aspectRatio the aspect ratio of the screen
     */
    public static void aspectedToNormalized(Coord coords, float aspectRatio) {
        if (aspectRatio >= 1.0f) {
            coords.x /= aspectRatio;
        } else {
            coords.y *= aspectRatio;
        }
    }

    /**
     * @purpose is to convert normalized coordinates to screen coordinates
     * @param coords the coordinates to convert
     * @param width the width of the screen
     * @param height the height of the screen
     */
    public static void normalizedToScreen(Coord coords, int width, int height) {

        //convert x
        coords.x += 1;
        coords.x /= 2;
        coords.x *= width;

        //convert y
        coords.y += 1;
        coords.y /= 2;
        coords.x *= height;
    }

    /**
     * @purpose is to convert world coordinates to screen coordinates
     * @param coords the coordinates to convert
     * @param width the width of the screen
     * @param height the height of the screen
     * @param camera the camera whose zoom and position to be taken account of
     */
    public static void worldToScreen(Coord coords, int width, int height, Camera camera) {
        worldToAspected(coords, camera);
        aspectedToNormalized(coords, (float)width / (float)height);
        normalizedToScreen(coords, width, height);
    }

    /**
     * @purpose is to convert grid coordinates to screen coordinates
     * @param coords the coordinates to convert
     * @param width the width of the screen
     * @param height the height of the screen
     * @param camera the camera whose zoom and position to be taken account of
     */
    public static void gridToScreen(Coord coords, int width, int height, Camera camera) {
        gridToWorld(coords);
        worldToScreen(coords, width, height, camera);
    }
}
