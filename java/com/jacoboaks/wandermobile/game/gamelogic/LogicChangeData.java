package com.jacoboaks.wandermobile.game.gamelogic;

/**
 * Encapsulates data appropriate for a logic change.
 */
public class LogicChangeData {

    //Data
    private String logicTag;
    private boolean loadNewLogicData;
    private boolean saveOldLogicData;

    //Constructor
    public LogicChangeData(String logicTag, boolean loadNewLogicData, boolean saveOldLogicData) {
        this.logicTag = logicTag;
        this.loadNewLogicData = loadNewLogicData;
        this.saveOldLogicData = saveOldLogicData;
    }

    //Accessors
    public String getLogicTag() { return this.logicTag; }
    public boolean doesLoadNewLogicData() { return this.loadNewLogicData; }
    public boolean doesSaveOldLogicData() { return this.saveOldLogicData; }
}