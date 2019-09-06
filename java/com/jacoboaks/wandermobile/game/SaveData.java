package com.jacoboaks.wandermobile.game;

import com.jacoboaks.wandermobile.game.gameitem.Entity;
import com.jacoboaks.wandermobile.game.gameitem.Player;
import com.jacoboaks.wandermobile.graphics.Font;
import com.jacoboaks.wandermobile.util.Node;

import java.util.List;

/**
 * Encompasses all useful data for game saving/loading.
 */
public class SaveData {

    //Data
    private Player player;
    private int saveSlot;

    /**
     * Constructs this SaveData with the given information.
     * @param player the player to save
     * @param saveSlot the slot that this data corresponds to
     */
    public SaveData(Player player, int saveSlot) {
        this.player = player;
        this.saveSlot = saveSlot;
    }

    /**
     * Constructs this SaveData with the given Node.
     * @param node the node to use for constructing this SaveData
     * @param font the font to use for construction
     */
    public SaveData(Node node, Font font) {
        this.saveSlot = Integer.parseInt(node.getChild("saveSlot").getValue());
        this.player = Player.nodeToPlayer(node.getChild("Player"), font);
    }

    /**
     * Constructs this SaveData by loading the data at the given slot.
     * @param saveSlot the slot to load the data from.
     * @param font the font to use for construction
     */
    public SaveData(int saveSlot, Font font) {
        this(Node.readNode(SaveData.getSaveSlotDir(saveSlot)), font);
    }

    /**
     * Converts this SaveData into a Node.
     * @return
     */
    public Node toNode(Area currentArea) {
        Node data = new Node("savedata");
        data.addChild("saveSlot", Integer.toString(this.saveSlot));
        data.addChild(player.toNode());
        data.addChild("currentArea", currentArea.getName());
        return data;
    }

    /**
     * Saves the data of this SaveData into the appropriate slot.
     * @param currentArea the current Area in use
     */
    public void save(Area currentArea) {
        Node node = this.toNode(currentArea);
        Node.writeNode(node, SaveData.getSaveSlotDir(this.saveSlot));
        Node currentAreaNode = currentArea.toNode();
        Node.writeNode(currentAreaNode, SaveData.getSaveSlotSlotAreaDir(this.saveSlot, currentArea.getFilename()));
    }

    /**
     * Saves the given Area data to this save slot.
     * @param area the Area to save
     */
    public void saveArea(Area area) {
        Node node = area.toNode();
        Node.writeNode(node, SaveData.getSaveSlotDir(this.saveSlot));
    }

    /**
     * Finds and returns the directory for the save data of the given save slot.
     * @param slot the slot whose directory to retrieve
     * @return the directory for the given save slot
     */
    public static String getSaveSlotDir(int slot) {
        return SaveData.getSaveSlotFolderDir(slot) + "/savedata.wdr";
    }

    /**
     * Finds and returns the directory for the areas of the save data of the given save slot.
     * @param slot the slot whose area data to retrieve
     * @return the directory for the given save slot saved area
     */
    public static String getSaveSlotSlotAreaDir(int slot, String areaFilename) {
        return SaveData.getSaveSlotFolderDir(slot) + "/areas/" + areaFilename + ".wdr";
    }

    public static String getSaveSlotFolderDir(int slot) {
        return "data/saves/saveslot" + slot;
    }

    //Mutators
    public void updatePlayer(Player player) {
        this.player = player;
    }

    //Accessors
    public Player getPlayer() { return this.player; }
    public int getSaveSlot() { return this.saveSlot; }
}
