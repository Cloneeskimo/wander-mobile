package com.jacoboaks.wandermobile.util;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * @purpose is to serve as a medium of data transfer using a tree-like structure
 */
public class Node {

    //Static Data
    private static char INDENT_CHAR = '\t';
    private static char DIVIDER_CHAR = ':';

    //Data
    private String name;
    private String value;
    private List<Node> children;

    //Full Constructor
    public Node(String name, String value, List<Node> children) {
        this.name = name;
        this.value = value;
        this.children = children;
    }

    //Single Child Constructor
    public Node(String name, String data, Node child) {
        this(name, data, new ArrayList<Node>());
        this.children.add(child);
    }

    //No Child Constructor
    public Node(String name, String value) {
        this.name = name;
        this.value = value;
    }

    //Name Constructor
    public Node(String name) {
        this.name = name;
    }

    //Default Constructor
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

    //Bundle Conversion Methods
    public static Bundle nodeToBundle(Bundle bundle, Node node) {
        nodeToBundleR(bundle, "", node);
        return bundle;
    }

    private static void nodeToBundleR(Bundle bundle, String preface, Node node) {
        bundle.putString(preface + node.getName(), node.getValue());
        if (node.hasChildren())
            for (Node child : node.getChildren())
                nodeToBundleR(bundle, preface + node.getName() + "_", child);
    }
}
