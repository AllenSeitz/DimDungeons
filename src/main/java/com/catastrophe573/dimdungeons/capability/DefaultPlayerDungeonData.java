package com.catastrophe573.dimdungeons.capability;

// this class stores data to the ServerPlayerEntity that I can't modify otherwise
public class DefaultPlayerDungeonData implements IPlayerDungeonData
{
    private float lastOWX = 0;
    private float lastOWY = 0;
    private float lastOWZ = 0;
    private float lastOWYaw = 0;    
    
    public void setLastOverworldPortalX(float x)
    {
	lastOWX = x;
    }
    
    public void setLastOverworldPortalY(float y)
    {
	lastOWY = y;
    }
    
    public void setLastOverworldPortalZ(float z)
    {
	lastOWZ = z;
    }
    
    public void setLastOverworldPortalYaw(float yaw)
    {
	lastOWYaw = yaw;
    }
    
    public float getLastOverworldPortalX()
    {
	return lastOWX;
    }
    
    public float getLastOverworldPortalY()
    {
	return lastOWY;
    }
    
    public float getLastOverworldPortalZ()
    {
	return lastOWZ;
    }
    
    public float getLastOverworldPortalYaw()
    {
	return lastOWYaw;
    }
}
