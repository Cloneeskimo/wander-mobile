package com.jacoboaks.wandermobile.graphics;

import com.jacoboaks.wandermobile.util.Coord;

/**
 * Provides methods for converting coordinates between different spaces. The important spaces
 * are defined in the following order:
 *  - screen - the coordinates of the screen's pixels
 *  - normalized - the coordinates of the screen from -1 to 1
 *  - aspect ratio - the normalized coordinates adjusted for aspect ratio
 *  - world - the coordinates of a world where a camera is taken into account
 *  - grid - the coordinates of a world where items are locked into a Tile grid
 */
public class Transformation {

    /**
     * Converts screen coordinates to normalized coordinates.
     * @param coords the coordinates to convert
     */
    public static void screenToNormalized(Coord coords) {

        //convert x
        coords.x /= GameRenderer.surfaceWidth;
        coords.x *= 2;
        coords.x -= 1;

        //convert y
        coords.y /= GameRenderer.surfaceHeight;
        coords.y *= 2;
        coords.y -= 1;
        coords.y *= -1;
    }

    /**
     * Converts normalized coordinates to aspect ratio coordinates.
     * @param coords the coordinates to convert
     */
    public static void normalizedToAspected(Coord coords) {
        if (GameRenderer.surfaceAspectRatio >= 1.0f) {
            coords.x *= GameRenderer.surfaceAspectRatio;
        } else {
            coords.y /= GameRenderer.surfaceAspectRatio;
        }
    }

    /**
     * Converts aspect ratio coordinates to world coordinates.
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
     * Converts screen coordinates to world coordinates.
     * @param coords the coordinates to convert
     * @param camera the camera whose zoom and position to be taken account of
     */
    public static void screenToWorld(Coord coords, Camera camera) {
        screenToNormalized(coords);
        normalizedToAspected(coords);
        aspectedToWorld(coords, camera);
    }

    /**
     * Converts screen coordinates to grid coordinates.
     * @param coords the coordinates to convert
     * @param camera the camera whose zoom and position to be taken account of
     */
    public static void screenToGrid(Coord coords, Camera camera) {
        screenToWorld(coords, camera);
        worldToGrid(coords);
    }

    /**
     * Converts world coordinates to grid coordinates.
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
     * Converts grid coordinates to world coordinates.
     * @param coords the coordinates to convert
     */
    public static void gridToWorld(Coord coords) {
        coords.x *= Model.STD_SQUARE_SIZE;
        coords.y *= Model.STD_SQUARE_SIZE;
    }

    /**
     * Converts world coordinates to aspect ratio coordinates.
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
     * Converts aspect ratio coordinates to normalized coordinates.
     * @param coords the coordinates to convert
     */
    public static void aspectedToNormalized(Coord coords) {
        if (GameRenderer.surfaceAspectRatio >= 1.0f) {
            coords.x /= GameRenderer.surfaceAspectRatio;
        } else {
            coords.y *= GameRenderer.surfaceAspectRatio;
        }
    }

    /**
     * Converts normalized coordinates to screen coordinates.
     * @param coords the coordinates to convert
     */
    public static void normalizedToScreen(Coord coords) {

        //convert x
        coords.x += 1;
        coords.x /= 2;
        coords.x *= GameRenderer.surfaceWidth;

        //convert y
        coords.y += 1;
        coords.y /= 2;
        coords.x *= GameRenderer.surfaceHeight;
    }

    /**
     * Converts world coordinates to screen coordinates.
     * @param coords the coordinates to convert
     * @param camera the camera whose zoom and position to be taken account of
     */
    public static void worldToScreen(Coord coords, Camera camera) {
        worldToAspected(coords, camera);
        aspectedToNormalized(coords);
        normalizedToScreen(coords);
    }

    /**
     * Converts grid coordinates to screen coordinates.
     * @param coords the coordinates to convert
     * @param camera the camera whose zoom and position to be taken account of
     */
    public static void gridToScreen(Coord coords, Camera camera) {
        gridToWorld(coords);
        worldToScreen(coords, camera);
    }
}
