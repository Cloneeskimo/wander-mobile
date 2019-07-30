package com.jacoboaks.wandermobile.util;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * @purpose is to serve as a medium of data transfer using a tree-like data structure
 */
public class Node {

    //Static Data
    private static char INDENT_CHAR = '\t';
    private static char DIVIDER_CHAR = ':';

    //Data
    private String name;
    private String value;
    private List<Node> children;

    /**
     * @purpose is to construct this Node by giving it all of its properties upfront
     * @param name the name of the node
     * @param value the value of the node
     * @param children the list of the node's children
     */
    public Node(String name, String value, List<Node> children) {
        this.name = name;
        this.value = value;
        this.children = children;
    }

    /**
     * @purpose is to construct this Node by giving it a name, its data, and a single child
     * @param name the name of the node
     * @param value the value of the node
     * @param child the single child of the node
     */
    public Node(String name, String value, Node child) {
        this(name, value, new ArrayList<Node>());
        this.children.add(child);
    }

    /**
     * @purpose is to construct this Node without giving it any children
     * @param name the name of the node
     * @param value the value of the node
     */
    public Node(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @purpose is to construct this Node by solely giving it a name
     * @param name the name of the node
     */
    public Node(String name) {
        this.name = name;
    }

    /**
     * @purpose is to construct this Node without setting any of its properties initiailly
     */
    public Node() {}

    //Children Manipulation Methods
    public List<Node> getChildren() { return this.children; }
    public int getChildCount() { return this.children.size(); }
    public void addChild(Node child) {
        if (this.children == null) this.children = new ArrayList<>();
        this.children.add(child);
    }
    public void addChildren(List<Node> children) {
        if (this.children == null) this.children = new ArrayList<>();
        if (children == null) return;
        for (Node child : children) this.children.add(child);
    }
    public Node getChild(int index) {
        if (index > this.children.size()) {
            throw Util.fatalError("Node.java", "getChild(int",
                    "Unable to access index " + index + " in child array of size " + this.children.size());
        }
        return this.children.get(index);
    }
    public Node getChild(String name) {
        for (Node child : this.children) if (child.getName().equals(name)) return child;
        throw Util.fatalError("Node.java", "getChild(String)",
                "Unable to access child with name '" + name + "'");
    }

    //Accessors
    public String getName() { return this.name; }
    public String getValue() { return this.value; }
    public boolean hasName() { return this.name != null; }
    public boolean hasValue() { return this.value != null; }
    public boolean hasChildren() {
        if (this.children == null) return false;
        if (this.children.size() < 1) return false;
        return true;
    }

    //Mutators
    public void setValue(String value) { this.value = value; }
    public void setName(String name) { this.name = name; }

    /**
     * @purpose is convert a node to an Android Bundle
     * @param bundle the bundle to store the node data in
     * @param node the node to convert
     * @return the bundle with the node data now in it
     */
    public static Bundle nodeToBundle(Bundle bundle, Node node) {
        nodeToBundleR(bundle, "", node);
        return bundle;
    }

    /**
     * @purpose is to recursively traverse a node and put its contents into the given Android Bundle
     * @param bundle the bundle to put the node data in
     * @param preface the preface for the bundle data naming (maintains the structure of the node)
     * @param node the node whose data to deposit into the bundle
     */
    private static void nodeToBundleR(Bundle bundle, String preface, Node node) {
        bundle.putString(preface + node.getName(), node.getValue());
        if (node.hasChildren())
            for (Node child : node.getChildren())
                nodeToBundleR(bundle, preface + node.getName() + "_", child);
    }

    /**
     * @purpose is to read a node from a file
     * @param resourceID the resourceID to read the node from
     * @return the read Node
     */
    public static Node readNode(int resourceID) {

        //create node
        Node node = new Node();

        //read data from resource
        List<String> data = Util.readResourceFile(resourceID);

        //parse node recursively
        readNodeR(node, data, 0, 0);

        //return node
        return node;
    }

    /**
     * @purpose is to recursively reads a Node from a file
     * @param node the current Node in focus
     * @param fileContents the recursively static file contents
     * @param i the current line of fileContents in focus
     * @param indent the current indent in terms of number of characters
     * @return the node in focus and its recursively read children
     */
    private static int readNodeR(Node node, List<String> fileContents, int i, int indent) {

        //format next line and find dividing point
        String nextLine = fileContents.get(i); //get line
        nextLine = nextLine.substring(indent, nextLine.length()); //remove indent
        int dividerLocation = -1; //location of the divider in line
        for (int j = 0; j < nextLine.length() && dividerLocation == -1; j++)
            if (nextLine.charAt(j) == Node.DIVIDER_CHAR) dividerLocation = j; //find divider

        //throw error if no divider found
        if (dividerLocation == -1)
            throw Util.fatalError("Node.java", "readNodeR(Node, List<String>, int, int",
                    "could not find divider in line: '" + nextLine + "'");

        //create node and set name
        Node curr = new Node();
        String possibleName = nextLine.substring(0, dividerLocation);
        if (!possibleName.equals("")) curr.setName(nextLine.substring(0, dividerLocation)); //create node with name

        //set node value if there is one
        String possibleValue = nextLine.substring(dividerLocation + 1, nextLine.length()); //grab possible value
        if (!possibleValue.equals(" ") && !possibleValue.equals("")) { //if possible value has substance
            curr.setValue(possibleValue.substring(1, possibleValue.length())); //set value (remove first space space)
        }

        //check for more file
        if (i + 1 <= fileContents.size()) { //if not eof

            //check for child nodes
            if (fileContents.get(i + 1).contains("{")) { //if the node has children
                i += 2; //iterate twice
                indent++; //iterate indent
                while (!fileContents.get(i).contains("}")) { //while there are more children

                    //add child
                    Node child = new Node(); //create child node
                    i = readNodeR(child, fileContents, i, indent); //recursively read child, keep track of file position
                    curr.addChild(child); //add child

                    //throw error if file suddenly stops
                    if ((i + 1) > fileContents.size())
                        throw Util.fatalError("Node.java", "readNodeR(Node, List<String>, int, int",
                                "unexpected stop in file at line " + i);

                    //iterate i
                    i += 1;
                }
            }
        }

        //set node, return current position in file
        node.setName(curr.getName());
        node.setValue(curr.getValue());
        node.addChildren(curr.getChildren());
        return i;
    }
}
