package com.catastrophe573.dimdungeons.structure;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.catastrophe573.dimdungeons.DungeonConfig;
import com.google.common.collect.Lists;

import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;

// this class is used by the DungeonChunkGenerator to design dungeons
public class DungeonBuilderLogic
{
    // an enumeration of the six room types, used internally for randomization and classification
    enum RoomType
    {
	ENTRANCE, END, CORNER, HALLWAY, THREEWAY, FOURWAY, NONE
    };

    // an enumeration of dungeon types
    enum DungeonType
    {
	BASIC, ADVANCED
    };

    // dungeons are a maximum of 8x8 chunks (always much smaller) where each chunk is a structure at a specific rotation
    public class DungeonRoom
    {
	public String structure;
	public Rotation rotation;
	public RoomType type;

	DungeonRoom()
	{
	    structure = "";
	    rotation = Rotation.NONE;
	    type = RoomType.NONE;
	}

	public boolean hasRoom()
	{
	    return type != RoomType.NONE;
	}

	public boolean hasDoorNorth()
	{
	    return type == RoomType.FOURWAY || (type == RoomType.ENTRANCE && rotation != Rotation.CLOCKWISE_180) || (type == RoomType.THREEWAY && rotation != Rotation.NONE) || (type == RoomType.CORNER && rotation == Rotation.NONE)
		    || (type == RoomType.CORNER && rotation == Rotation.COUNTERCLOCKWISE_90) || (type == RoomType.HALLWAY && rotation == Rotation.NONE) || (type == RoomType.HALLWAY && rotation == Rotation.CLOCKWISE_180)
		    || (type == RoomType.END && rotation == Rotation.CLOCKWISE_180);
	}

	public boolean hasDoorSouth()
	{
	    return type == RoomType.FOURWAY || (type == RoomType.ENTRANCE && rotation != Rotation.NONE) || (type == RoomType.THREEWAY && rotation != Rotation.CLOCKWISE_180) || (type == RoomType.CORNER && rotation == Rotation.CLOCKWISE_90)
		    || (type == RoomType.CORNER && rotation == Rotation.CLOCKWISE_180) || (type == RoomType.HALLWAY && rotation == Rotation.NONE) || (type == RoomType.HALLWAY && rotation == Rotation.CLOCKWISE_180)
		    || (type == RoomType.END && rotation == Rotation.NONE);
	}

	public boolean hasDoorWest()
	{
	    return type == RoomType.FOURWAY || (type == RoomType.ENTRANCE && rotation != Rotation.CLOCKWISE_90) || (type == RoomType.THREEWAY && rotation != Rotation.COUNTERCLOCKWISE_90)
		    || (type == RoomType.CORNER && rotation == Rotation.COUNTERCLOCKWISE_90) || (type == RoomType.CORNER && rotation == Rotation.CLOCKWISE_180) || (type == RoomType.HALLWAY && rotation == Rotation.CLOCKWISE_90)
		    || (type == RoomType.HALLWAY && rotation == Rotation.COUNTERCLOCKWISE_90) || (type == RoomType.END && rotation == Rotation.CLOCKWISE_90);
	}

	public boolean hasDoorEast()
	{
	    return type == RoomType.FOURWAY || (type == RoomType.ENTRANCE && rotation != Rotation.COUNTERCLOCKWISE_90) || (type == RoomType.THREEWAY && rotation != Rotation.CLOCKWISE_90) || (type == RoomType.CORNER && rotation == Rotation.NONE)
		    || (type == RoomType.CORNER && rotation == Rotation.CLOCKWISE_90) || (type == RoomType.HALLWAY && rotation == Rotation.CLOCKWISE_90) || (type == RoomType.HALLWAY && rotation == Rotation.COUNTERCLOCKWISE_90)
		    || (type == RoomType.END && rotation == Rotation.COUNTERCLOCKWISE_90);
	}
    };

    // this is the final constructed dungeon
    public DungeonRoom finalLayout[][] = new DungeonRoom[8][8];
    public int enemyVariation1 = 0;
    public int enemyVariation2 = 0;

    // these contain the candidates from pool selection, from the config file
    ArrayList<String> entrance = Lists.newArrayList();
    ArrayList<String> fourway = Lists.newArrayList();
    ArrayList<String> threeway = Lists.newArrayList();
    ArrayList<String> hallway = Lists.newArrayList();
    ArrayList<String> corner = Lists.newArrayList();
    ArrayList<String> end = Lists.newArrayList();

    // after shuffling the list of structures these are used to ensure no duplicates
    protected int entranceIndex = 0;
    protected int endIndex = 0;
    protected int cornerIndex = 0;
    protected int hallwayIndex = 0;
    protected int threewayIndex = 0;
    protected int fourwayIndex = 0;

    // this is initialized during the constructor with values from the ChunkGenerator, to ensure the dungeons use the world seed
    // or at least that was the intent in 1.14 anyways, but as of 1.16 dungeons are no longer built insid
    protected Random rand;

    public DungeonBuilderLogic(Random randIn, long chunkX, long chunkZ, DungeonType type)
    {
	rand = randIn;

	if (type == DungeonType.BASIC)
	{
	    // pick one candidate from each pool
	    for (int i = 0; i < DungeonConfig.basicEntrances.size(); i++)
	    {
		int poolSize = DungeonConfig.basicEntrances.get(i).size();
		int index = rand.nextInt(poolSize);
		entrance.add(DungeonConfig.basicEntrances.get(i).get(index));
	    }
	    for (int i = 0; i < DungeonConfig.basicFourways.size(); i++)
	    {
		int poolSize = DungeonConfig.basicFourways.get(i).size();
		int index = rand.nextInt(poolSize);
		fourway.add(DungeonConfig.basicFourways.get(i).get(index));
	    }
	    for (int i = 0; i < DungeonConfig.basicThreeways.size(); i++)
	    {
		int poolSize = DungeonConfig.basicThreeways.get(i).size();
		int index = rand.nextInt(poolSize);
		threeway.add(DungeonConfig.basicThreeways.get(i).get(index));
	    }
	    for (int i = 0; i < DungeonConfig.basicHallways.size(); i++)
	    {
		int poolSize = DungeonConfig.basicHallways.get(i).size();
		int index = rand.nextInt(poolSize);
		hallway.add(DungeonConfig.basicHallways.get(i).get(index));
	    }
	    for (int i = 0; i < DungeonConfig.basicCorners.size(); i++)
	    {
		int poolSize = DungeonConfig.basicCorners.get(i).size();
		int index = rand.nextInt(poolSize);
		corner.add(DungeonConfig.basicCorners.get(i).get(index));
	    }
	    for (int i = 0; i < DungeonConfig.basicEnds.size(); i++)
	    {
		int poolSize = DungeonConfig.basicEnds.get(i).size();
		int index = rand.nextInt(poolSize);
		end.add(DungeonConfig.basicEnds.get(i).get(index));
	    }
	}
	else if (type == DungeonType.ADVANCED)
	{
	    // pick one candidate from each pool
	    for (int i = 0; i < DungeonConfig.advancedEntrances.size(); i++)
	    {
		int poolSize = DungeonConfig.advancedEntrances.get(i).size();
		int index = rand.nextInt(poolSize);
		entrance.add(DungeonConfig.advancedEntrances.get(i).get(index));
	    }
	    for (int i = 0; i < DungeonConfig.advancedFourways.size(); i++)
	    {
		int poolSize = DungeonConfig.advancedFourways.get(i).size();
		int index = rand.nextInt(poolSize);
		fourway.add(DungeonConfig.advancedFourways.get(i).get(index));
	    }
	    for (int i = 0; i < DungeonConfig.advancedThreeways.size(); i++)
	    {
		int poolSize = DungeonConfig.advancedThreeways.get(i).size();
		int index = rand.nextInt(poolSize);
		threeway.add(DungeonConfig.advancedThreeways.get(i).get(index));
	    }
	    for (int i = 0; i < DungeonConfig.advancedHallways.size(); i++)
	    {
		int poolSize = DungeonConfig.advancedHallways.get(i).size();
		int index = rand.nextInt(poolSize);
		hallway.add(DungeonConfig.advancedHallways.get(i).get(index));
	    }
	    for (int i = 0; i < DungeonConfig.advancedCorners.size(); i++)
	    {
		int poolSize = DungeonConfig.advancedCorners.get(i).size();
		int index = rand.nextInt(poolSize);
		corner.add(DungeonConfig.advancedCorners.get(i).get(index));
	    }
	    for (int i = 0; i < DungeonConfig.advancedEnds.size(); i++)
	    {
		int poolSize = DungeonConfig.advancedEnds.get(i).size();
		int index = rand.nextInt(poolSize);
		end.add(DungeonConfig.advancedEnds.get(i).get(index));
	    }
	}

	shuffleStringArray(entrance);
	shuffleStringArray(fourway);
	shuffleStringArray(threeway);
	shuffleStringArray(hallway);
	shuffleStringArray(corner);
	shuffleStringArray(end);

	for (int i = 0; i < 8; i++)
	{
	    for (int j = 0; j < 8; j++)
	    {
		finalLayout[i][j] = new DungeonRoom();
	    }
	}

	enemyVariation1 = rand.nextInt(3);
	enemyVariation2 = rand.nextInt(3);
    }

    // when this function is done you may read the dungeon layout from the public variable finalLayout
    public void calculateDungeonShape(int maxNumRooms)
    {
	//System.out.println("START CALC DUNGEON SHAPE");

	// step 1: place a constant entrance at the center of the bottom row
	placeRoomShape(4, 7, entrance.get(entranceIndex), RoomType.ENTRANCE, Rotation.NONE);
	entranceIndex++;
	int numRoomsPlaced = 1;

	// step 2: create three openings off of the entrance room
	ArrayList<ImmutablePair<Integer, Integer>> openings = new ArrayList<ImmutablePair<Integer, Integer>>();
	openings.add(new ImmutablePair<Integer, Integer>(3, 7));
	openings.add(new ImmutablePair<Integer, Integer>(5, 7));
	openings.add(new ImmutablePair<Integer, Integer>(4, 6));
	shuffleArray(openings);

	// remaining rooms: for each opening, place a room that fits, and update openings, until no openings are left
	while (openings.size() > 0)
	{
	    ImmutablePair<Integer, Integer> roomPos = openings.remove(0);
	    RoomType nextType = RoomType.END;
	    Rotation nextRot = Rotation.NONE;

	    boolean mustPickEndings = false;
	    boolean noEndingsYet = false;
	    //DimDungeons.LOGGER.info("Processing opening " + roomPos.left + ", " + roomPos.right);

	    // it can happen that an "opening" is in the list twice due to loops, in which case skip this loop
	    if (finalLayout[roomPos.left][roomPos.right].hasRoom())
	    {
		continue;
	    }

	    // if this room has neighbors already then it must connect to them
	    boolean mustConnectNorth = hasOpenDoor(roomPos.left, roomPos.right, Direction.NORTH);
	    boolean mustConnectSouth = hasOpenDoor(roomPos.left, roomPos.right, Direction.SOUTH);
	    boolean mustConnectWest = hasOpenDoor(roomPos.left, roomPos.right, Direction.WEST);
	    boolean mustConnectEast = hasOpenDoor(roomPos.left, roomPos.right, Direction.EAST);

	    // likewise some walls must be acknowledged as mutual too
	    boolean cantConnectNorth = hasSolidWall(roomPos.left, roomPos.right, Direction.NORTH);
	    boolean cantConnectSouth = hasSolidWall(roomPos.left, roomPos.right, Direction.SOUTH);
	    boolean cantConnectWest = hasSolidWall(roomPos.left, roomPos.right, Direction.WEST);
	    boolean cantConnectEast = hasSolidWall(roomPos.left, roomPos.right, Direction.EAST);

	    // this puts the dungeon generation into "end it now" mode, where doors will no longer create more doors
	    if (numRoomsPlaced + openings.size() >= maxNumRooms)
	    {
		mustPickEndings = true;
	    }
	    // this keeps the randomizer from putting dead ends too early in a dungeon path
	    else if (numRoomsPlaced + openings.size() < maxNumRooms / 2)
	    {
		noEndingsYet = true;
	    }

	    // when mustPickEndings is false this is used to hold the list of valid possibilities
	    ArrayList<ImmutablePair<RoomType, Rotation>> roomPossibilities = new ArrayList<ImmutablePair<RoomType, Rotation>>(0);

	    // look at the neighboring doors and pick the room and rotation that connects them without making new openings
	    // TODO: eliminate the redundant half of this IF statement by using mustPickEndings to abort the roomPossibilities[] check at the end
	    if (mustPickEndings)
	    {
		// this case should be impossible
		if (mustConnectNorth && mustConnectSouth && mustConnectWest && mustConnectEast)
		{
		    nextType = RoomType.FOURWAY;
		}

		// forced threeway
		if (!mustConnectNorth && mustConnectSouth && mustConnectWest && mustConnectEast)
		{
		    nextType = RoomType.THREEWAY;
		}
		if (mustConnectNorth && !mustConnectSouth && mustConnectWest && mustConnectEast)
		{
		    nextType = RoomType.THREEWAY;
		    nextRot = Rotation.CLOCKWISE_180;
		}
		if (mustConnectNorth && mustConnectSouth && !mustConnectWest && mustConnectEast)
		{
		    nextType = RoomType.THREEWAY;
		    nextRot = Rotation.COUNTERCLOCKWISE_90;
		}
		if (mustConnectNorth && mustConnectSouth && mustConnectWest && !mustConnectEast)
		{
		    nextType = RoomType.THREEWAY;
		    nextRot = Rotation.CLOCKWISE_90;
		}

		// forced corner
		if (mustConnectNorth && !mustConnectSouth && !mustConnectWest && mustConnectEast)
		{
		    nextType = RoomType.CORNER;
		}
		if (!mustConnectNorth && mustConnectSouth && !mustConnectWest && mustConnectEast)
		{
		    nextType = RoomType.CORNER;
		    nextRot = Rotation.CLOCKWISE_90;
		}
		if (!mustConnectNorth && mustConnectSouth && mustConnectWest && !mustConnectEast)
		{
		    nextType = RoomType.CORNER;
		    nextRot = Rotation.CLOCKWISE_180;
		}
		if (mustConnectNorth && !mustConnectSouth && mustConnectWest && !mustConnectEast)
		{
		    nextType = RoomType.CORNER;
		    nextRot = Rotation.COUNTERCLOCKWISE_90;
		}

		// forced hallway is really rare but whatever lets code it for consistency
		if (mustConnectNorth && mustConnectSouth && !mustConnectWest && !mustConnectEast)
		{
		    nextType = RoomType.HALLWAY;
		}
		if (!mustConnectNorth && !mustConnectSouth && mustConnectWest && mustConnectEast)
		{
		    nextType = RoomType.HALLWAY;
		    nextRot = Rotation.CLOCKWISE_90;
		}

		// forced dead end is most common
		if (mustConnectNorth && !mustConnectSouth && !mustConnectWest && !mustConnectEast)
		{
		    nextType = RoomType.END;
		    nextRot = Rotation.CLOCKWISE_180;
		}
		if (!mustConnectNorth && mustConnectSouth && !mustConnectWest && !mustConnectEast)
		{
		    nextType = RoomType.END;
		}
		if (!mustConnectNorth && !mustConnectSouth && mustConnectWest && !mustConnectEast)
		{
		    nextType = RoomType.END;
		    nextRot = Rotation.CLOCKWISE_90;
		}
		if (!mustConnectNorth && !mustConnectSouth && !mustConnectWest && mustConnectEast)
		{
		    nextType = RoomType.END;
		    nextRot = Rotation.COUNTERCLOCKWISE_90;
		}
	    }
	    else
	    {
		if (mustConnectNorth && mustConnectSouth && mustConnectWest && mustConnectEast)
		{
		    nextType = RoomType.FOURWAY;
		}

		// three doors are required, so place either a threeway or a fourway
		if (!mustConnectNorth && mustConnectSouth && mustConnectWest && mustConnectEast)
		{
		    nextType = RoomType.THREEWAY;
		    if (noEndingsYet && !cantConnectNorth)
		    {
			nextType = RoomType.FOURWAY;
		    }
		}
		if (mustConnectNorth && !mustConnectSouth && mustConnectWest && mustConnectEast)
		{
		    nextType = RoomType.THREEWAY;
		    nextRot = Rotation.CLOCKWISE_180;
		    if (noEndingsYet && !cantConnectSouth)
		    {
			nextType = RoomType.FOURWAY;
		    }
		}
		if (mustConnectNorth && mustConnectSouth && !mustConnectWest && mustConnectEast)
		{
		    nextType = RoomType.THREEWAY;
		    nextRot = Rotation.COUNTERCLOCKWISE_90;
		    if (noEndingsYet && !cantConnectWest)
		    {
			nextType = RoomType.FOURWAY;
		    }
		}
		if (mustConnectNorth && mustConnectSouth && mustConnectWest && !mustConnectEast)
		{
		    nextType = RoomType.THREEWAY;
		    nextRot = Rotation.CLOCKWISE_90;
		    if (noEndingsYet && !cantConnectEast)
		    {
			nextType = RoomType.FOURWAY;
		    }
		}

		// forced corner, other two walls may or may not be open
		if (mustConnectNorth && !mustConnectSouth && !mustConnectWest && mustConnectEast)
		{
		    nextType = RoomType.CORNER;

		    // can we make this a 3way or 4way?
		    if (!cantConnectSouth && !cantConnectWest)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.FOURWAY, Rotation.NONE));
		    }
		    if (!cantConnectSouth)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.COUNTERCLOCKWISE_90));
		    }
		    if (!cantConnectWest)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.CLOCKWISE_180));
		    }
		}
		if (!mustConnectNorth && mustConnectSouth && !mustConnectWest && mustConnectEast)
		{
		    nextType = RoomType.CORNER;
		    nextRot = Rotation.CLOCKWISE_90;

		    // can we make this a 3way or 4way?
		    if (!cantConnectNorth && !cantConnectWest)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.FOURWAY, Rotation.NONE));
		    }
		    if (!cantConnectNorth)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.COUNTERCLOCKWISE_90));
		    }
		    if (!cantConnectWest)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.NONE));
		    }
		}
		if (!mustConnectNorth && mustConnectSouth && mustConnectWest && !mustConnectEast)
		{
		    nextType = RoomType.CORNER;
		    nextRot = Rotation.CLOCKWISE_180;

		    // can we make this a 3way or 4way?
		    if (!cantConnectNorth && !cantConnectEast)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.FOURWAY, Rotation.NONE));
		    }
		    if (!cantConnectNorth)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.CLOCKWISE_90));
		    }
		    if (!cantConnectEast)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.NONE));
		    }
		}
		if (mustConnectNorth && !mustConnectSouth && mustConnectWest && !mustConnectEast)
		{
		    nextType = RoomType.CORNER;
		    nextRot = Rotation.COUNTERCLOCKWISE_90;

		    // can we make this a 3way or 4way?
		    if (!cantConnectSouth && !cantConnectEast)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.FOURWAY, Rotation.NONE));
		    }
		    if (!cantConnectSouth)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.CLOCKWISE_90));
		    }
		    if (!cantConnectEast)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.CLOCKWISE_180));
		    }
		}

		// forced hallway, but can we make it a 3way or 4way?
		if (mustConnectNorth && mustConnectSouth && !mustConnectWest && !mustConnectEast)
		{
		    nextType = RoomType.HALLWAY;

		    if (!cantConnectWest && !cantConnectEast)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.FOURWAY, Rotation.NONE));
		    }
		    if (!cantConnectWest)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.CLOCKWISE_90));
		    }
		    if (!cantConnectEast)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.COUNTERCLOCKWISE_90));
		    }
		}
		if (!mustConnectNorth && !mustConnectSouth && mustConnectWest && mustConnectEast)
		{
		    nextType = RoomType.HALLWAY;
		    nextRot = Rotation.CLOCKWISE_90;

		    if (!cantConnectNorth && !cantConnectSouth)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.FOURWAY, Rotation.NONE));
		    }
		    if (!cantConnectNorth)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.CLOCKWISE_180));
		    }
		    if (!cantConnectSouth)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.NONE));
		    }
		}

		// one doorway is required, but can we add 2-4 more doorways here?
		if (mustConnectNorth && !mustConnectSouth && !mustConnectWest && !mustConnectEast)
		{
		    nextType = RoomType.END;
		    nextRot = Rotation.CLOCKWISE_180;

		    // try for 4ways, then 3ways
		    if (!cantConnectSouth && !cantConnectWest && !cantConnectEast)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.FOURWAY, Rotation.NONE));
		    }
		    if (!cantConnectSouth && !cantConnectEast)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.COUNTERCLOCKWISE_90));
		    }
		    if (!cantConnectSouth && !cantConnectWest)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.CLOCKWISE_90));
		    }
		    if (!cantConnectWest && !cantConnectEast)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.CLOCKWISE_180));
		    }

		    // try for 2ways too
		    if (!cantConnectEast)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.CORNER, Rotation.NONE));
		    }
		    if (!cantConnectSouth)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.HALLWAY, Rotation.NONE));
		    }
		    if (!cantConnectWest)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.CORNER, Rotation.COUNTERCLOCKWISE_90));
		    }
		}
		if (!mustConnectNorth && mustConnectSouth && !mustConnectWest && !mustConnectEast)
		{
		    nextType = RoomType.END;

		    // try for 4ways, then 3ways
		    if (!cantConnectNorth && !cantConnectWest && !cantConnectEast)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.FOURWAY, Rotation.NONE));
		    }
		    if (!cantConnectNorth && !cantConnectEast)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.COUNTERCLOCKWISE_90));
		    }
		    if (!cantConnectNorth && !cantConnectWest)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.CLOCKWISE_90));
		    }
		    if (!cantConnectWest && !cantConnectEast)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.NONE));
		    }

		    // try for 2ways too
		    if (!cantConnectEast)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.CORNER, Rotation.CLOCKWISE_90));
		    }
		    if (!cantConnectNorth)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.HALLWAY, Rotation.NONE));
		    }
		    if (!cantConnectWest)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.CORNER, Rotation.CLOCKWISE_180));
		    }
		}
		if (!mustConnectNorth && !mustConnectSouth && mustConnectWest && !mustConnectEast)
		{
		    nextType = RoomType.END;
		    nextRot = Rotation.CLOCKWISE_90;

		    // try for 4ways, then 3ways
		    if (!cantConnectNorth && !cantConnectSouth && !cantConnectEast)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.FOURWAY, Rotation.NONE));
		    }
		    if (!cantConnectNorth && !cantConnectEast)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.CLOCKWISE_180));
		    }
		    if (!cantConnectNorth && !cantConnectSouth)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.CLOCKWISE_90));
		    }
		    if (!cantConnectSouth && !cantConnectEast)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.NONE));
		    }

		    // try for 2ways too
		    if (!cantConnectEast)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.HALLWAY, Rotation.CLOCKWISE_90));
		    }
		    if (!cantConnectNorth)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.CORNER, Rotation.COUNTERCLOCKWISE_90));
		    }
		    if (!cantConnectSouth)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.CORNER, Rotation.CLOCKWISE_180));
		    }
		}
		if (!mustConnectNorth && !mustConnectSouth && !mustConnectWest && mustConnectEast)
		{
		    nextType = RoomType.END;
		    nextRot = Rotation.COUNTERCLOCKWISE_90;

		    // try for 4ways, then 3ways
		    if (!cantConnectNorth && !cantConnectSouth && !cantConnectWest)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.FOURWAY, Rotation.NONE));
		    }
		    if (!cantConnectNorth && !cantConnectWest)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.CLOCKWISE_180));
		    }
		    if (!cantConnectNorth && !cantConnectSouth)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.COUNTERCLOCKWISE_90));
		    }
		    if (!cantConnectSouth && !cantConnectWest)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.THREEWAY, Rotation.NONE));
		    }

		    // try for 2ways too
		    if (!cantConnectWest)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.HALLWAY, Rotation.CLOCKWISE_90));
		    }
		    if (!cantConnectNorth)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.CORNER, Rotation.NONE));
		    }
		    if (!cantConnectSouth)
		    {
			roomPossibilities.add(new ImmutablePair<RoomType, Rotation>(RoomType.CORNER, Rotation.CLOCKWISE_90));
		    }
		}
	    }

	    // if the previous block of code populated the roomPossibilities array then make a selection from it and use it instead
	    // this code path represents more doorways being added and branching paths
	    if (roomPossibilities.size() > 0 && !mustPickEndings)
	    {
		shuffleRoomPossibilities(roomPossibilities);
		nextType = roomPossibilities.get(0).left;
		nextRot = roomPossibilities.get(0).right;
	    }

	    // get the next random structure of this type and put it in there
	    String nextRoom = "";
	    if (nextType == RoomType.FOURWAY)
	    {
		nextRoom = fourway.get(fourwayIndex);
		fourwayIndex = fourwayIndex == fourway.size() - 1 ? 0 : fourwayIndex + 1;
	    }
	    if (nextType == RoomType.THREEWAY)
	    {
		nextRoom = threeway.get(threewayIndex);
		threewayIndex = threewayIndex == threeway.size() - 1 ? 0 : threewayIndex + 1;
	    }
	    if (nextType == RoomType.HALLWAY)
	    {
		nextRoom = hallway.get(hallwayIndex);
		hallwayIndex = hallwayIndex == hallway.size() - 1 ? 0 : hallwayIndex + 1;
	    }
	    if (nextType == RoomType.CORNER)
	    {
		nextRoom = corner.get(cornerIndex);
		cornerIndex = cornerIndex == corner.size() - 1 ? 0 : cornerIndex + 1;
	    }
	    if (nextType == RoomType.END)
	    {
		nextRoom = end.get(endIndex);
		endIndex = endIndex == end.size() - 1 ? 0 : endIndex + 1;
	    }

	    // commit the room to the blueprint and open any potential new doors for the next loop to work with
	    int roomX = roomPos.left;
	    int roomZ = roomPos.right;
	    placeRoomShape(roomX, roomZ, nextRoom, nextType, nextRot);
	    numRoomsPlaced++;
	    if (hasOpenDoor(roomX - 1, roomZ, Direction.EAST) && !finalLayout[roomX - 1][roomZ].hasRoom())
	    {
		openings.add(new ImmutablePair<Integer, Integer>(roomX - 1, roomZ));
		//DimDungeons.LOGGER.info("Adding opening " + (roomX-1) + ", " + roomZ);
	    }
	    if (hasOpenDoor(roomX + 1, roomZ, Direction.WEST) && !finalLayout[roomX + 1][roomZ].hasRoom())
	    {
		openings.add(new ImmutablePair<Integer, Integer>(roomX + 1, roomZ));
		//DimDungeons.LOGGER.info("Adding opening " + (roomX+1) + ", " + roomZ);
	    }
	    if (hasOpenDoor(roomX, roomZ - 1, Direction.SOUTH) && !finalLayout[roomX][roomZ - 1].hasRoom())
	    {
		openings.add(new ImmutablePair<Integer, Integer>(roomX, roomZ - 1));
		//DimDungeons.LOGGER.info("Adding opening " + roomX + ", " + (roomZ-1));
	    }
	    if (hasOpenDoor(roomX, roomZ + 1, Direction.NORTH) && !finalLayout[roomX][roomZ + 1].hasRoom())
	    {
		openings.add(new ImmutablePair<Integer, Integer>(roomX, roomZ + 1));
		//DimDungeons.LOGGER.info("Adding opening " + roomX + ", " + (roomZ+1));
	    }

	    // next loop - reshuffle the openings because we may have added some
	    shuffleArray(openings);
	    //DimDungeons.LOGGER.info("Num openings: " + openings.size());
	}

	//System.out.println("END CALC DUNGEON SHAPE");
    }

    // returns true if another chunk has a door leading into this chunk from the specified direction
    // safe to call with x or y that are out of bounds
    private boolean hasOpenDoor(int x, int z, Direction direction)
    {
	if (x < 0 || z < 0 || x > 7 || z > 7)
	{
	    return false;
	}
	if (direction == Direction.NORTH && z > 0)
	{
	    return finalLayout[x][z - 1].hasDoorSouth();
	}
	if (direction == Direction.SOUTH && z < 7)
	{
	    return finalLayout[x][z + 1].hasDoorNorth();
	}
	if (direction == Direction.WEST && x > 0)
	{
	    return finalLayout[x - 1][z].hasDoorEast();
	}
	if (direction == Direction.EAST && x < 7)
	{
	    return finalLayout[x + 1][z].hasDoorWest();
	}
	return false;
    }

    // returns true if the neighbor in this direction has placed a solid wall, and false if there is a door or no neighbor yet at all
    // safe to call with x or y that are out of bounds
    private boolean hasSolidWall(int x, int z, Direction direction)
    {
	if (x < 0 || z < 0 || x > 7 || z > 7)
	{
	    return true;
	}
	if (direction == Direction.NORTH && z > 0)
	{
	    return !finalLayout[x][z - 1].hasDoorSouth() && finalLayout[x][z - 1].hasRoom();
	}
	if (direction == Direction.SOUTH && z < 7)
	{
	    return !finalLayout[x][z + 1].hasDoorNorth() && finalLayout[x][z + 1].hasRoom();
	}
	if (direction == Direction.WEST && x > 0)
	{
	    return !finalLayout[x - 1][z].hasDoorEast() && finalLayout[x - 1][z].hasRoom();
	}
	if (direction == Direction.EAST && x < 7)
	{
	    return !finalLayout[x + 1][z].hasDoorWest() && finalLayout[x + 1][z].hasRoom();
	}
	return true;
    }

    public void placeRoomShape(int x, int z, String room, RoomType type, Rotation rot)
    {
	finalLayout[x][z].structure = room;
	finalLayout[x][z].type = type;
	finalLayout[x][z].rotation = rot;
	//System.out.println("Put a " + type.toString() + " at (" + x + ", " + z + ") with rotation " + rot.toString() + ".");
	//DimDungeons.LOGGER.info("Put a " + room + " at (" + x + ", " + z + ") with rotation " + rot.toString() + ".");
    }

    private void shuffleStringArray(ArrayList<String> array)
    {
	for (int i = array.size() - 1; i > 0; i--)
	{
	    int index = rand.nextInt(i + 1);

	    String temp = array.get(index);
	    array.set(index, array.get(i));
	    array.set(i, temp);
	}
    }

    private void shuffleArray(ArrayList<ImmutablePair<Integer, Integer>> array)
    {
	for (int i = array.size() - 1; i > 0; i--)
	{
	    int index = rand.nextInt(i + 1);

	    ImmutablePair<Integer, Integer> temp = array.get(index);
	    array.set(index, array.get(i));
	    array.set(i, temp);
	}
    }

    private void shuffleRoomPossibilities(ArrayList<ImmutablePair<RoomType, Rotation>> array)
    {
	for (int i = array.size() - 1; i > 0; i--)
	{
	    int index = rand.nextInt(i + 1);

	    // Simple swap
	    ImmutablePair<RoomType, Rotation> temp = array.get(index);
	    array.set(index, array.get(i));
	    array.set(i, temp);
	}
    }
}
