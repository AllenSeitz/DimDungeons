package com.catastrophe573.dimdungeons.capability;

public interface IPlayerDungeonData
{
    public void setLastOverworldPortalX(float x);
    public void setLastOverworldPortalY(float y);
    public void setLastOverworldPortalZ(float z);
    public void setLastOverworldPortalYaw(float yaw);
    
    public float getLastOverworldPortalX();
    public float getLastOverworldPortalY();
    public float getLastOverworldPortalZ();
    public float getLastOverworldPortalYaw();
}
