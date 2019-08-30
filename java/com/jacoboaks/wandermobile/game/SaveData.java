package com.jacoboaks.wandermobile.game;

import com.jacoboaks.wandermobile.game.gameitem.Entity;
import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.util.Node;

/**
 * Encompasses all useful data for game saving/loading.
 */
public class SaveData {

    //Data
    private Entity player;
    private int saveSlot;

    /**
     * Constructs this SaveData with the given information.
     * @param player the player to save
     * @param saveSlot the slot that this data corresponds to
     */
    public SaveData(Entity player, int saveSlot) {
        this.player = player;
        this.saveSlot = saveSlot;
    }

    /**
     * Constructs this SaveData with the given Node.
     * @param node the node to use for constructing this SaveData
     */
    public SaveData(Node node, Font font) {
        this.saveSlot = Integer.parseInt(node.getChild("saveSlot").getValue());
        this.player = Entity.nodeToEntity(node.getChild("Entity"), font);
    }

    /**
     * Converts this SaveData into a Node.
     * @return
     */
    public Node toNode() {
        Node data = new Node("savedata");
        data.addChild("saveSlot", Integer.toString(this.saveSlot));
        data.addChild(player.toNode());
        return data;
    }

    /**
     * Saves the data of this SaveData into the appropriate slot.
     */
    public void save() {

    }

    //Accessors
    public Entity getPlayer() { return this.player; }
    public int getSaveSlot() { return this.saveSlot; }
}
