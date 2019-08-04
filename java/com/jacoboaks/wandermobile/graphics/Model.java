package com.jacoboaks.wandermobile.graphics;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Model Class
 * Holds a collection of vertices, a draw path, and a material for drawing a single model.
 */
public class Model {

    //Static Data
    private static final int COORDS_PER_VERTEX = 3;

    //Data
    private Material material;
    private float[] modelCoords;
    private float[] textureCoords;
    private int[] drawPath;

    //Buffer Data
    private FloatBuffer vertexBuffer;
    private FloatBuffer textureCoordsBuffer;
    private IntBuffer drawPathBuffer;

    /**
     * Constructs this Model with the given information.
     * @param modelCoords the model coordinates for this Model
     * @param textureCoords the texture coordinates for this Model
     * @param drawPath the draw path (or indices) for this Model
     * @param material the material to use when rendering this Model
     */
    public Model(float[] modelCoords, float[] textureCoords, int[] drawPath, Material material) {
        this.modelCoords = modelCoords;
        this.textureCoords = textureCoords;
        this.drawPath = drawPath;
        this.material = material;
        this.updateBuffers();
    }

    //Draw Method
    public void render(ShaderProgram shaderProgram) {

        //enable texture if model is textured
        if (this.isTextured()) {

            //set texture flag
            GLES20.glUniform1i(shaderProgram.getUniformIndex("isTextured"), 1);

            //get handles
            int textureSamplerHandle = shaderProgram.getUniformIndex("textureSampler");

            //activate texture bank
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.material.getTexture().getID());
            GLES20.glUniform1i(textureSamplerHandle, 0);

            //otherwise disable texture usage
        } else GLES20.glUniform1i(shaderProgram.getUniformIndex("isTextured"), 0);

        //set position attribute data
        int positionHandle = shaderProgram.getAttributeIndex("position");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                COORDS_PER_VERTEX * 4, this.vertexBuffer);

        //set texture coordinate attribute data
        int textureCoordHandle = shaderProgram.getAttributeIndex("texCoord");
        GLES20.glEnableVertexAttribArray(textureCoordHandle);
        GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 8, this.textureCoordsBuffer);

        //set color data
        GLES20.glUniform4fv(shaderProgram.getUniformIndex("color"), 1, this.material.getColor().getAsArr(), 0);
        GLES20.glUniform1i(shaderProgram.getUniformIndex("colorOverride"), this.material.isColorOverrided() ? 1 : 0);

        //draw the object
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawPath.length, GLES20.GL_UNSIGNED_INT, this.drawPathBuffer);

        //disable attribute arrays
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureCoordHandle);
    }

    /**
     * Scales this Model by the given factor.
     * @param factor the factor by which to scale the model
     */
    public void scale(float factor) {
        for (int i = 0; i < this.modelCoords.length; i++) this.modelCoords[i] = this.modelCoords[i] * factor;
        this.updateBuffers();
    }

    /**
     * @return whether or not this model is textured
     */
    public boolean isTextured() {
        return this.material.isTextured();
    }

    /**
     * Updates the vertex buffer. This is important to do after scaling, or after any data
     * of this Model has been changed.
     */
    private void updateBuffers() {

        //create byte buffer for positions
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(this.modelCoords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        //convert to float buffer and store vertices
        this.vertexBuffer = byteBuffer.asFloatBuffer();
        this.vertexBuffer.put(this.modelCoords);
        this.vertexBuffer.position(0);

        //create byte buffer for texture coordinates
        byteBuffer = ByteBuffer.allocateDirect(this.textureCoords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        //convert to float buffer and store vertices
        this.textureCoordsBuffer = byteBuffer.asFloatBuffer();
        this.textureCoordsBuffer.put(this.textureCoords);
        this.textureCoordsBuffer.position(0);

        //create byte buffer for draw list
        byteBuffer = ByteBuffer.allocateDirect(this.drawPath.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        //convert to int buffer and store vertcies
        this.drawPathBuffer = byteBuffer.asIntBuffer();
        this.drawPathBuffer.put(this.drawPath);
        this.drawPathBuffer.position(0);
    }

    //Accessor
    public Material getMaterial() { return this.material; }

    //Standard Square Data
    public static final float STD_SQUARE_SIZE = 0.5f;
    public static float[] STD_SQUARE_MODEL_COORDS() {
        return new float[]{
                -STD_SQUARE_SIZE / 2f, -STD_SQUARE_SIZE / 2f, 0f, //top left
                -STD_SQUARE_SIZE / 2f, STD_SQUARE_SIZE / 2f, 0f,  //bottom left
                STD_SQUARE_SIZE / 2f, -STD_SQUARE_SIZE / 2f, 0f,  //top right
                STD_SQUARE_SIZE / 2f, STD_SQUARE_SIZE / 2f, 0f};  //bottom right
    }
    public static float[] STD_SQUARE_TEX_COORDS() {
        return new float[]{
                0.0f, 1.0f, //top left
                0.0f, 0.0f, //bottom left
                1.0f, 1.0f, //top right
                1.0f, 0.0f}; //bottom right
    }
    public static int[] STD_SQUARE_DRAW_ORDER() {
        return new int[]{0, 1, 2, 2, 1, 3};
    }
}
