package com.jacoboaks.wandermobile.graphics;

import com.jacoboaks.wandermobile.util.Node;
import com.jacoboaks.wandermobile.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Font Class
 * @purpose is to represent a font by having a font sheet texture which contains all the characters
 * of the font in ASCII order. While they may start at a custom character, they may NOT skip characters.
 */
public class Font {

    //Data
    Texture fontSheet;
    char startingChar;
    int charsPerRow, charsPerColumn;
    int standardLetterCutoff;
    Map<Character, Integer> letterCutoffs;

    /**
     * Constructor
     * @param fontResourceID the resource ID of the fontsheet
     * @param letterCutoffResourceID the resource ID of the letter cutoff file
     * @param charsPerRow how many characters there are per row of the font sheet
     * @param charsPerColumn how many characters there are per column of the font sheet
     * @param startingChar the character at the beginning of the font sheet. The least this value will
     *                     be is 0. The max this value will be is 127.
     */
    public Font(int fontResourceID, int letterCutoffResourceID, int charsPerRow, int charsPerColumn, char startingChar) {
        this.fontSheet = new Texture(fontResourceID);
        this.charsPerRow = charsPerRow;
        this.charsPerColumn = charsPerColumn;
        this.startingChar = (char)Math.min(Math.max(0, startingChar), 127);
        this.letterCutoffs = new HashMap<>();
        this.initLetterCutoffs(letterCutoffResourceID);
    }

    /**
     * @purpose is to read and parse the letetr cutoffs file
     * @param letterCutoffResourceID the resource ID of the letter cutoff file
     */
    private void initLetterCutoffs(int letterCutoffResourceID) {

        //read data and set default standard cutoff
        Node data = Node.readNode(letterCutoffResourceID);
        this.standardLetterCutoff = 0;

        //parse children
        for (Node child : data.getChildren()) {
            if (child.getName().toUpperCase().equals("STANDARD")) this.standardLetterCutoff = Integer.parseInt(child.getValue());
            else if (child.getName().toUpperCase().equals("COLON")) this.letterCutoffs.put(':', Integer.parseInt(child.getValue()));
            else this.letterCutoffs.put(child.getName().charAt(0), Integer.parseInt(child.getValue()));
        }
    }

    /**
     * @param toGet the character whose texture coordinates to receive
     * @param cutoff whether to incorporate cutoff into the texture coordinates
     * @return the texture coordinates for given character if using this font (for a standard square model)
     */
    public float[] getCharacterTextureCoordinates(char toGet, boolean cutoff) {

        //check if invalid char
        if (toGet < 0 || toGet > 127) Util.fatalError("Font.java",
                "getCharacterTextureCoordinates(char)", "invalid char '" + toGet + "' given");

        //calculate row and column
        int character = (toGet - this.startingChar);
        int row = character / this.charsPerRow; //zero-indexed
        int column = character - (row * this.charsPerRow); //zero-indexed
        float fractionOfRow = 1 / (float)charsPerRow;
        float fractionOfCol = 1 / (float)charsPerColumn;

        //calculate texture coordinates
        float texCoords[] = new float[] {
                (float)column / charsPerRow, (float)row / charsPerColumn + fractionOfCol, //top left
                (float)column / charsPerRow, (float)row / charsPerColumn, //bottom left
                (float)column / charsPerRow + fractionOfRow, (float)row / charsPerColumn + fractionOfCol, //top right
                (float)column / charsPerRow + fractionOfRow, (float)row / charsPerColumn //bottom right
        };

        //account for cutoff
        if (cutoff) {
            float cutoffFactor = (float)getCharacterCutoff(toGet) / (float)this.fontSheet.getWidth();
            texCoords[0] += cutoffFactor;
            texCoords[2] += cutoffFactor;
            texCoords[4] -= cutoffFactor;
            texCoords[6] -= cutoffFactor;
        }

        //return final coordinates
        return texCoords;
    }

    //Accessor
    public Texture getFontSheet() { return this.fontSheet; }
    public float getCharacterHeight() { return (float)this.fontSheet.getHeight() / (float)this.charsPerColumn; }
    public float getCharacterWidth() { return (float)this.fontSheet.getWidth() / (float)this.charsPerRow; }
    public int getCharacterCutoff(char c) {
        Integer cutoff = this.letterCutoffs.get(c);
        if (cutoff == null) cutoff = this.standardLetterCutoff;
        return cutoff;
    }
}
