package com.jacoboaks.wandermobile.graphics;

import android.opengl.GLES20;
import android.util.Log;

import com.jacoboaks.wandermobile.MainActivity;
import com.jacoboaks.wandermobile.R;
import com.jacoboaks.wandermobile.util.Util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * ShaderProgram Class
 * @purpose is to maintain a GLES SL shader program for use when rendering
 */
public class ShaderProgram {

    //Data
    private int programID;
    private final Map<String, Integer> uniforms;

    //Constructor
    public ShaderProgram() {

        //create shader program
        this.programID = GLES20.glCreateProgram();
        this.uniforms = new HashMap<>();
    }

    /**
     * @purpose is to load a shader into this shader program
     * @param resourceID the id of the resource to be used
     * @param type the type (vertex or fragment)
     */
    public void loadShader(int resourceID, int type) {

        //read resource data into string
        InputStream stream = MainActivity.context.getResources().openRawResource(resourceID);
        String sourceCode = Util.inputStreamToString(stream);

        //pass to other method for compilation
        this.loadShader(sourceCode, type);
    }

    /**
     * @purpose is to load a shader into this shader program
     * @param code the code to be compiled
     * @param type the type (vertex or fragment)
     */
    public void loadShader(String code, int type) {

        //create shader
        int id = GLES20.glCreateShader(type);

        //add source code and compile
        GLES20.glShaderSource(id, code);
        GLES20.glCompileShader(id);

        //check compile status
        IntBuffer status = IntBuffer.allocate(1);
        GLES20.glGetShaderiv(id, GLES20.GL_COMPILE_STATUS, status);
        if (status.get(0) == GLES20.GL_FALSE) {

            //figure out type of shader that failed
            String types = "UNKNOWN";
            if (type == GLES20.GL_VERTEX_SHADER) types = "vertex";
            else if (type == GLES20.GL_FRAGMENT_SHADER) types = "fragment";

            String statuss = GLES20.glGetShaderInfoLog(id);

            //throw failure error
            throw Util.fatalError("ShaderProgram.java", "loadShader(code, type)",
                    "could not compile shader of type: " + types + ": " + statuss);

            //log success if debug enabled
        } else if (Util.DEBUG) Log.i(Util.getLogTag("ShaderProgram.java",
                "loadShader(String, int)"), "shader compilation successful");

        //attach shader
        GLES20.glAttachShader(this.programID, id);
    }

    /**
     * links the shaders together - should be done after loading all the desired shaders
     */
    public void link() {

        //link program
        GLES20.glLinkProgram(this.programID);

        //check link status
        IntBuffer status = IntBuffer.allocate(1);
        GLES20.glGetShaderiv(this.programID, GLES20.GL_LINK_STATUS, status);
        if (status.get(0) == GLES20.GL_FALSE) {

            //get info log for linkage
            String info = GLES20.glGetShaderInfoLog(this.programID);

            //throw failure error if an actual error occured
            if (info.length() != 0) throw Util.fatalError("ShaderProgram.java", "link()",
                    "failed to link shader program: " + info);

            //log success if debug enabled
        } else if (Util.DEBUG) Log.i(Util.getLogTag("ShaderProgram.java", "link()"),
                "shader program link succesful");

    }

    //Bind/Unbind Method
    public void bind() { GLES20.glUseProgram(this.programID); }
    public void unbind() { GLES20.glUseProgram(0); }

    //Uniform Registering/Retrieving
    public void registerUniform(String name) { this.uniforms.put(name, this.getGLUniformIndex(name)); }
    public int getUniformIndex(String name) {
        Integer i = this.uniforms.get(name);
        if (i == null) throw Util.fatalError("ShaderProgram.java", "getUniformIndex(String)",
                "no uniform with name '" + name + "' is registered");
        return i;
    }

    //Accessors
    public int getAttributeIndex(String name) {
        int i = GLES20.glGetAttribLocation(this.programID, name);
        if (i < 0) throw Util.fatalError("ShaderProgram.java", "getAttributeIndex(String)",
                "could not find attribute with name '" + name + "'");
        return i;
    }

    /**
     * @purpose is to retrieve the index of the uniform with the provided name - shouldn't be used
     * to often to avoid too many gl calls
     * @param name name of the uniform whose index to find
     * @return the index of the uniform
     */
    private int getGLUniformIndex(String name) {
        int i = GLES20.glGetUniformLocation(this.programID, name);
        if (i < 0) throw Util.fatalError("ShaderProgram.java", "getUniformIndex(String)",
                "could not find uniform with name '" + name + "'");
        return i;
    }

    //Cleanup Method
    public void cleanup() { if (this.programID != 0) GLES20.glDeleteProgram(this.programID); }
}
