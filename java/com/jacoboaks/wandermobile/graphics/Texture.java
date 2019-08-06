package com.jacoboaks.wandermobile.graphics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.jacoboaks.wandermobile.MainActivity;
import com.jacoboaks.wandermobile.util.Util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents an OpenGL texture user for Materials.
 */
public class Texture {

    //Data
    private int id[];
    private int width, height;
    private int resourceID;

    /**
     * Constructs this texture.
     * @param resourceID the resource ID of the image to use for this texture
     */
    public Texture(int resourceID) {

        //load texture into bitmap
        InputStream is = MainActivity.getAppResources().openRawResource(resourceID);
        Bitmap bmp;
        try {
            bmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch(IOException e) {
                if (Util.DEBUG) Log.i(Util.getLogTag("Texture.java", "Texture(int)"),
                        "unable to close InputStream");
            }
        }

        //set width and height
        this.width = bmp.getWidth();
        this.height = bmp.getHeight();

        //generate gl texture and bind it
        this.id = new int[1];
        GLES20.glGenTextures(1, id, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id[0]);

        //set minification and magnification filter parameters
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);

        //set wrapping
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

        //bind bitmap to texture
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

        //recycle bitmap
        bmp.recycle();

        //save resource id
        this.resourceID = resourceID;
    }

    //Accessors
    public int getID() { return this.id[0]; }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
    public int getResourceID() { return this.resourceID; }
}
