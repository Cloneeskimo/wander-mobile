package com.jacoboaks.wandermobile.graphics;

import com.jacoboaks.wandermobile.util.Node;
import com.jacoboaks.wandermobile.util.Util;

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
            this.letterCutoffs.put(child.getName().charAt(0), Integer.parseInt(child.getValue()));
        }
    }

    /**
     * @param toGet the character whose texture coordinates to receive
     * @return the texture coordinates for given character if using this font (for a standard square model)
     */
    public float[] getCharacterTextureCoordinates(char toGet) {

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
        return new float[] {
                (float)column / charsPerRow, (float)row / charsPerColumn + fractionOfCol,
                (float)column / charsPerRow + fractionOfRow, (float)row / charsPerColumn + fractionOfCol,
                (float)column / charsPerRow, (float)row / charsPerColumn,
                (float)column / charsPerRow + fractionOfRow, (float)row / charsPerColumn
        };
    }

    //Accessor
    public Texture getFontSheet() { return this.fontSheet; }
}
