package com.jacoboaks.wandermobile.graphics;

import android.opengl.GLES20;

import com.jacoboaks.wandermobile.util.Color;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Model Class
 * @purpose is to hold a collection of vertices, a draw path, and a material for drawing the
 * model with
 */
public class Model {

    //Static Data
    private static final int COORDS_PER_VERTEX = 3;
    public static final float STANDARD_SQUARE_SIZE = 0.5f;

    //Data
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private Material material;
    private float[] modelCoords;
    private float[] textureCoords;
    private short[] drawPath;

    //Buffer Data
    private FloatBuffer vertexBuffer;
    private FloatBuffer textureCoordsBuffer;
    private ShortBuffer drawPathBuffer;

    //Full Constructor
    public Model(float[] modelCoords, float[] textureCoords, short[] drawPath, Material material) {
        this.modelCoords = modelCoords;
        this.textureCoords = textureCoords;
        this.drawPath = drawPath;
        this.material = material;
        this.updateBuffers();
    }

    //Draw Method
    public void draw(ShaderProgram shaderProgram) {

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
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, this.vertexBuffer);

        //set texture coordinate attribute data
        int textureCoordHandle = shaderProgram.getAttributeIndex("texCoord");
        GLES20.glEnableVertexAttribArray(textureCoordHandle);
        GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 8, this.textureCoordsBuffer);

        //set color data
        GLES20.glUniform4fv(shaderProgram.getUniformIndex("color"), 1, this.material.getColor().getAsArr(), 0);
        GLES20.glUniform1i(shaderProgram.getUniformIndex("colorOverride"), this.material.isColorOverrided() ? 1 : 0);

        //draw the object
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawPath.length, GLES20.GL_UNSIGNED_SHORT, this.drawPathBuffer);

        //disable attribute arrays
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureCoordHandle);
    }

    /**
     * @param x the x factor by which to translate the model
     * @param y the y factor by which to translate the model
     * @purpose is to translate the entire model. should not be used for moving objects regularly
     */
    public void translate(float x, float y, float z) {

        //reset world coordinates array
        float[] oldModelCoords = this.modelCoords;
        this.modelCoords = new float[oldModelCoords.length];

        //transform each according to x and y value
        for (int coord = 0; coord < this.modelCoords.length / COORDS_PER_VERTEX; coord++) {
            this.modelCoords[coord * 3] = oldModelCoords[coord * 3] + x;
            this.modelCoords[coord * 3 + 1] = oldModelCoords[coord * 3 + 1] + y;
            this.modelCoords[coord * 3 + 2] = oldModelCoords[coord * 3 + 2] + z;
        }

        //update buffers
        this.updateBuffers();
    }

    /**
     * @returns whether or not this model is textured;
     */
    public boolean isTextured() {
        return this.material.isTextured();
    }

    /**
     * @purpose is to update the vertex buffer
     * @called after a transformation to the model has been made and the vertices need updated
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
        byteBuffer = ByteBuffer.allocateDirect(this.drawPath.length * 2);
        byteBuffer.order(ByteOrder.nativeOrder());

        //conver to short buffer and store vertcies
        this.drawPathBuffer = byteBuffer.asShortBuffer();
        this.drawPathBuffer.put(this.drawPath);
        this.drawPathBuffer.position(0);
    }

    //Standard Square Data
    public static final float[] STD_SQUARE_MODEL_COORDS = new float[]{
            -STANDARD_SQUARE_SIZE / 2f, -STANDARD_SQUARE_SIZE / 2f, 0f,
            STANDARD_SQUARE_SIZE / 2f, -STANDARD_SQUARE_SIZE / 2f, 0f,
            -STANDARD_SQUARE_SIZE / 2f, STANDARD_SQUARE_SIZE / 2f, 0f,
            STANDARD_SQUARE_SIZE / 2f, STANDARD_SQUARE_SIZE / 2f, 0f};
    public static final float[] STD_SQUARE_TEX_COORDS = new float[]{0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 0.0f};
    public static final short[] STD_SQUARE_DRAW_ORDER = new short[]{0, 1, 2, 1, 3, 2};
}
