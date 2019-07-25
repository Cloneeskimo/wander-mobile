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

public class Texture {

    //Data
    int id[];

    //Constructor
    public Texture(int resourceID) {

        //load texture into bitmap
        InputStream is = MainActivity.context.getResources().openRawResource(resourceID);
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
    }

    //Accessor
    public int getID() { return id[0]; }
}
