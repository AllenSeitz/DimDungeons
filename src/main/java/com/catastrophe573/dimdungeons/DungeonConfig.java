package com.catastrophe573.dimdungeons;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// this class is based on gigaherz' mod, ToolBelt. Thank you!
public class DungeonConfig
{
    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static
    {
	final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
	SERVER_SPEC = specPair.getRight();
	SERVER = specPair.getLeft();
    }

    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static
    {
	final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
	CLIENT_SPEC = specPair.getRight();
	CLIENT = specPair.getLeft();
    }

    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static
    {
	final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
	COMMON_SPEC = specPair.getRight();
	COMMON = specPair.getLeft();
    }

    // server options
    public static int configVersion = 1;
    public static boolean globalBlockProtection = true;
    public static boolean enableDebugCheats = false;
    public static Set<Block> blockBreakWhitelist = Sets.newHashSet();
    public static Set<Block> blockInteractBlacklist = Sets.newHashSet();

    // client options
    public static boolean showParticles = true;
    public static boolean playPortalSounds = true;

    // common options
    public static List<? extends List<String>> basicEntrances;
    public static List<? extends List<String>> basicFourways;
    public static List<? extends List<String>> basicThreeways;
    public static List<? extends List<String>> basicHallways;
    public static List<? extends List<String>> basicCorners;
    public static List<? extends List<String>> basicEnds;
    public static List<? extends List<String>> advancedEntrances;
    public static List<? extends List<String>> advancedFourways;
    public static List<? extends List<String>> advancedThreeways;
    public static List<? extends List<String>> advancedHallways;
    public static List<? extends List<String>> advancedCorners;
    public static List<? extends List<String>> advancedEnds;

    public static class ServerConfig
    {
	public final ConfigValue<Integer> configVersion;

	public final ForgeConfigSpec.BooleanValue globalBlockProtection;

	public final ForgeConfigSpec.ConfigValue<List<? extends String>> breakingWhitelist;
	public final ForgeConfigSpec.ConfigValue<List<? extends String>> interactionBlacklist;

	ServerConfig(ForgeConfigSpec.Builder builder)
	{
	    // these are the blocks that the vanilla design does not want the player to 'open', right click, or use
	    List<String> hardcodedDefaultInteractionBlacklist = Lists.newArrayList();

	    // most of these are crafting stations I don't want the player to use inside the dungeon
	    // dispensers, hoppers, droppers, and redstone are the most urgently blacklisted blocks, since they define puzzles
	    hardcodedDefaultInteractionBlacklist.add("minecraft:dispenser");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:dropper");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:hopper");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:anvil");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:chipped_anvil");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:damaged_anvil");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:furnace");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:note_block");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:repeater");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:comparator");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:redstone_wire");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:beacon");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:crafting_table");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:enchanting_table");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:loom");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:smoker");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:blast_furnace");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:cartography_table");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:fletching_table");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:smithing_table");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:grindstone");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:stonecutter");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:respawn_anchor");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:lodestone");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:beehive");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:bee_nest");

	    // or I could add support for mixing tags and block ids in the blacklist
	    hardcodedDefaultInteractionBlacklist.add("minecraft:black_bed");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:blue_bed");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:brown_bed");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:cyan_bed");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:gray_bed");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:green_bed");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:light_blue_bed");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:light_gray_bed");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:lime_bed");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:magenta_bed");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:orange_bed");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:pink_bed");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:purple_bed");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:red_bed");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:white_bed");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:yellow_bed");

	    // also a good argument for tags
	    hardcodedDefaultInteractionBlacklist.add("minecraft:flower_pot");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_dandelion");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_poppy");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_blue_orchid");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_allium");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_azure_bluet");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_red_tulip");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_orange_tulip");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_white_tulip");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_pink_tulip");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_oxeye_daisy");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_cornflower");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_lily_of_the_valley");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_wither_rose");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_oak_sapling");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_spruce_sapling");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_birch_sapling");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_jungle_sapling");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_acacia_sapling");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_dark_oak_sapling");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_red_mushroom");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_brown_mushroom");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_fern");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_dead_bush");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_cactus");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_bamboo");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_crimson_fungus");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_warped_fungus");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_crimson_roots");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:potted_warped_roots");

	    // list of server options and comments
	    builder.comment("Options for general mod behavior.").push("general");

	    configVersion = builder.comment("You shouldn't manually change the version number.").translation("config.dimdungeons.configVersion").define("configVersion", 1);

	    globalBlockProtection = builder.comment("If set to FALSE the block protection on the dungeon dimension will be disabled, making the options in the next section useless.").translation("config.dimdungeons.globalBlockProtection")
		    .define("globalBlockProtection", true);
	    builder.pop();
	    builder.comment("Options for block behavior in the dungeon dimension.").push("blocks");
	    breakingWhitelist = builder.comment("List of blocks which any player should be allowed to break, defying the block protection. (For example, gravestones or death chests.) Default value is empty.")
		    .translation("config.dimdungeons.breakingWhitelist").defineList("breakingWhitelist", Lists.newArrayList(), o -> o instanceof String);
	    interactionBlacklist = builder.comment("List of blocks that players will be unable to interact with. It is strongly recommended to preserve the defaults.").translation("config.dimdungeons.interactionBlacklist")
		    .defineList("interactionBlacklist", hardcodedDefaultInteractionBlacklist, o -> o instanceof String);
	    builder.pop();
	}
    }

    public static class ClientConfig
    {
	public final ForgeConfigSpec.BooleanValue showParticles;
	public final ForgeConfigSpec.BooleanValue playPortalSounds;

	ClientConfig(ForgeConfigSpec.Builder builder)
	{
	    builder.comment("Options for client-side rendering.").push("render");
	    showParticles = builder.comment("If set to FALSE, the portal keyhole block will not emit particles.").translation("config.dimdungeons.showParticles").define("showParticles", true);
	    builder.pop();
	    builder.comment("Options for client-side sounds.").push("audio");
	    playPortalSounds = builder.comment("If set to FALSE, the portal keyhole block will not make ambient noises.").translation("config.dimdungeons.playPortalSounds").define("playPortalSounds", true);
	    builder.pop();
	}
    }

    // any config that has to deal with datapacks
    public static class CommonConfig
    {
	// tier 1
	public final ForgeConfigSpec.ConfigValue<List<? extends List<String>>> basicEntrances;
	public final ForgeConfigSpec.ConfigValue<List<? extends List<String>>> basicFourways;
	public final ForgeConfigSpec.ConfigValue<List<? extends List<String>>> basicThreeways;
	public final ForgeConfigSpec.ConfigValue<List<? extends List<String>>> basicHallways;
	public final ForgeConfigSpec.ConfigValue<List<? extends List<String>>> basicCorners;
	public final ForgeConfigSpec.ConfigValue<List<? extends List<String>>> basicEnds;

	// tier 2
	public final ForgeConfigSpec.ConfigValue<List<? extends List<String>>> advancedEntrances;
	public final ForgeConfigSpec.ConfigValue<List<? extends List<String>>> advancedFourways;
	public final ForgeConfigSpec.ConfigValue<List<? extends List<String>>> advancedThreeways;
	public final ForgeConfigSpec.ConfigValue<List<? extends List<String>>> advancedHallways;
	public final ForgeConfigSpec.ConfigValue<List<? extends List<String>>> advancedCorners;
	public final ForgeConfigSpec.ConfigValue<List<? extends List<String>>> advancedEnds;	
	
	CommonConfig(ForgeConfigSpec.Builder builder)
	{
	    //
	    // basic entrances
	    //
	    List<List<String>> tempBasicEntrances = Lists.newArrayList();
	    List<String> temp = Lists.newArrayList();
	    temp.add("dimdungeons:entrance_1");
	    temp.add("dimdungeons:entrance_2");
	    temp.add("dimdungeons:entrance_3");
	    temp.add("dimdungeons:entrance_4");
	    temp.add("dimdungeons:entrance_5");
	    temp.add("dimdungeons:entrance_6");
	    temp.add("dimdungeons:entrance_7");
	    temp.add("dimdungeons:entrance_8");
	    temp.add("dimdungeons:entrance_9");
	    tempBasicEntrances.add(Lists.newArrayList(temp));
	    temp.clear();

	    //
	    // basic fourways
	    //
	    List<List<String>> tempBasicFourways = Lists.newArrayList();
	    // add all fourways as separate pools
	    for (int i = 1; i <= 9; i++)
	    {
		temp.add("dimdungeons:fourway_" + i);
		tempBasicFourways.add(Lists.newArrayList(temp));
		temp.clear();
	    }
	    // add all combats as separate pools - TWICE
	    for (int i = 1; i <= 6; i++)
	    {
		temp.add("dimdungeons:combat_" + i);
		tempBasicFourways.add(Lists.newArrayList(temp));
		tempBasicFourways.add(Lists.newArrayList(temp));
		temp.clear();
	    }
	    // individual room pools
	    temp.add("dimdungeons:disco_1");
	    temp.add("dimdungeons:disco_2");
	    temp.add("dimdungeons:disco_3");
	    temp.add("dimdungeons:disco_4");
	    tempBasicFourways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:redstrap_1");
	    tempBasicFourways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:hiddenpath_1");
	    tempBasicFourways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:hiddenpath_2");
	    temp.add("dimdungeons:hiddenpath_3");
	    tempBasicFourways.add(Lists.newArrayList(temp));
	    temp.clear();

	    //
	    // basic threeways
	    //
	    List<List<String>> tempBasicThreeways = Lists.newArrayList();
	    // add all threeways as separate pools
	    for (int i = 1; i <= 5; i++)
	    {
		temp.add("dimdungeons:threeway_" + i);
		tempBasicThreeways.add(Lists.newArrayList(temp));
		temp.clear();
	    }
	    // add all morethrees as separate pools
	    for (int i = 1; i <= 6; i++)
	    {
		temp.add("dimdungeons:morethree_" + i);
		tempBasicThreeways.add(Lists.newArrayList(temp));
		temp.clear();
	    }
	    // individual room pools
	    temp.add("dimdungeons:tetris_1");
	    temp.add("dimdungeons:tetris_2");
	    temp.add("dimdungeons:tetris_3");
	    tempBasicThreeways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:redstrap_4");
	    tempBasicThreeways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:advice_2");
	    tempBasicThreeways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:mazenotfound_2");
	    tempBasicThreeways.add(Lists.newArrayList(temp));
	    temp.clear();

	    //
	    // basic hallways
	    //
	    List<List<String>> tempBasicHallways = Lists.newArrayList();
	    // add all hallways as separate pools
	    for (int i = 1; i <= 6; i++)
	    {
		temp.add("dimdungeons:hallway_" + i);
		tempBasicHallways.add(Lists.newArrayList(temp));
		temp.clear();
	    }
	    // add all extrahall as separate pools
	    for (int i = 1; i <= 5; i++)
	    {
		temp.add("dimdungeons:extrahall_" + i);
		tempBasicHallways.add(Lists.newArrayList(temp));
		temp.clear();
	    }
	    // individual room pools
	    temp.add("dimdungeons:coalhall_1");
	    temp.add("dimdungeons:coalhall_2");
	    temp.add("dimdungeons:coalhall_3");
	    tempBasicHallways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:juke_1");
	    tempBasicHallways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:advice_3");
	    tempBasicHallways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:library_2");
	    tempBasicHallways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:moohall_1");
	    tempBasicHallways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:redstrap_2");
	    tempBasicHallways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:mazenotfound_3");
	    tempBasicHallways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:yinyang_1");
	    temp.add("dimdungeons:yinyang_2");
	    tempBasicHallways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:waterhall_1");
	    tempBasicHallways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:tempt_1");
	    temp.add("dimdungeons:tempt_2");
	    temp.add("dimdungeons:tempt_3");
	    temp.add("dimdungeons:tempt_4");
	    tempBasicHallways.add(Lists.newArrayList(temp));
	    temp.clear();

	    //
	    // basic corners
	    //
	    List<List<String>> tempBasicCorners = Lists.newArrayList();
	    // add all corners as separate pools
	    for (int i = 1; i <= 8; i++)
	    {
		temp.add("dimdungeons:corner_" + i);
		tempBasicCorners.add(Lists.newArrayList(temp));
		temp.clear();
	    }
	    // add all longcorner as separate pools
	    for (int i = 1; i <= 5; i++)
	    {
		temp.add("dimdungeons:longcorner_" + i);
		tempBasicCorners.add(Lists.newArrayList(temp));
		temp.clear();
	    }
	    // individual room pools
	    temp.add("dimdungeons:redstrap_3");
	    tempBasicCorners.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:skullcorner_1");
	    tempBasicCorners.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:mazenotfound_1");
	    tempBasicCorners.add(Lists.newArrayList(temp));
	    temp.clear();

	    //
	    // basic ends
	    //
	    List<List<String>> tempBasicEnds = Lists.newArrayList();
	    // add all deadends as separate pools
	    for (int i = 1; i <= 8; i++)
	    {
		temp.add("dimdungeons:deadend_" + i);
		tempBasicEnds.add(Lists.newArrayList(temp));
		temp.clear();
	    }
	    // individual room pools
	    temp.add("dimdungeons:coffin_1");
	    temp.add("dimdungeons:coffin_2");
	    temp.add("dimdungeons:coffin_3");
	    temp.add("dimdungeons:coffin_4");
	    temp.add("dimdungeons:coffin_5");
	    tempBasicEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:advice_1");
	    tempBasicEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:restroom_1");
	    tempBasicEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:restroom_2");
	    temp.add("dimdungeons:restroom_3");
	    temp.add("dimdungeons:restroom_4");
	    temp.add("dimdungeons:restroom_5");
	    tempBasicEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:shoutout_1");
	    temp.add("dimdungeons:shoutout_2");
	    temp.add("dimdungeons:shoutout_3");
	    tempBasicEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:spawner_1");
	    temp.add("dimdungeons:spawner_2");
	    temp.add("dimdungeons:spawner_3");
	    temp.add("dimdungeons:spawner_4");
	    temp.add("dimdungeons:spawner_5");
	    temp.add("dimdungeons:spawner_6");
	    temp.add("dimdungeons:spawner_6");
	    temp.add("dimdungeons:spawner_6");
	    tempBasicEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:redspuzzle_1");
	    temp.add("dimdungeons:redspuzzle_2");
	    temp.add("dimdungeons:redspuzzle_3");
	    temp.add("dimdungeons:redspuzzle_4");
	    tempBasicEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:deathtrap_1");
	    temp.add("dimdungeons:deathtrap_2");
	    temp.add("dimdungeons:deathtrap_3");
	    temp.add("dimdungeons:deathtrap_4");
	    tempBasicEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:keyroom_1");
	    temp.add("dimdungeons:keyroom_2");
	    tempBasicEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:keyroom_3");
	    temp.add("dimdungeons:keyroom_4");
	    tempBasicEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:library_1");
	    tempBasicEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:crueltrap_1");
	    temp.add("dimdungeons:crueltrap_2");
	    temp.add("dimdungeons:crueltrap_3");
	    tempBasicEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:beacon_2");
	    tempBasicEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:freebie_1");
	    temp.add("dimdungeons:freebie_2");
	    temp.add("dimdungeons:freebie_3");
	    tempBasicEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    
	    //
	    // advanced entrances
	    //
	    List<List<String>> tempAdvancedEntrances = Lists.newArrayList();
	    temp.add("dimdungeons:entrance_1");
	    temp.add("dimdungeons:entrance_2");
	    temp.add("dimdungeons:entrance_3");
	    temp.add("dimdungeons:entrance_4");
	    temp.add("dimdungeons:entrance_5");
	    temp.add("dimdungeons:entrance_6");
	    temp.add("dimdungeons:entrance_7");
	    temp.add("dimdungeons:entrance_8");
	    temp.add("dimdungeons:entrance_9");
	    tempAdvancedEntrances.add(Lists.newArrayList(temp));
	    temp.clear();    

	    //
	    // advanced fourways
	    //
	    List<List<String>> tempAdvancedFourways = Lists.newArrayList();
	    // add all fourways as separate pools
	    for (int i = 1; i <= 9; i++)
	    {
		temp.add("dimdungeons:fourway_" + i);
		tempAdvancedFourways.add(Lists.newArrayList(temp));
		temp.clear();
	    }
	    // add all combats as separate pools - TWICE
	    for (int i = 1; i <= 6; i++)
	    {
		temp.add("dimdungeons:combat_" + i);
		tempAdvancedFourways.add(Lists.newArrayList(temp));
		tempAdvancedFourways.add(Lists.newArrayList(temp));
		temp.clear();
	    }
	    // individual room pools
	    temp.add("dimdungeons:disco_1");
	    temp.add("dimdungeons:disco_2");
	    temp.add("dimdungeons:disco_3");
	    temp.add("dimdungeons:disco_4");
	    tempAdvancedFourways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:redstrap_1");
	    tempAdvancedFourways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:hiddenpath_1");
	    temp.add("dimdungeons:swimmaze_1");
	    tempAdvancedFourways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:hiddenpath_2");
	    temp.add("dimdungeons:hiddenpath_3");
	    tempAdvancedFourways.add(Lists.newArrayList(temp));
	    temp.clear();

	    //
	    // advanced threeways
	    //
	    List<List<String>> tempAdvancedThreeways = Lists.newArrayList();
	    // add all threeways as separate pools
	    for (int i = 1; i <= 5; i++)
	    {
		temp.add("dimdungeons:threeway_" + i);
		tempAdvancedThreeways.add(Lists.newArrayList(temp));
		temp.clear();
	    }
	    // add all morethrees as separate pools
	    for (int i = 1; i <= 6; i++)
	    {
		temp.add("dimdungeons:morethree_" + i);
		tempAdvancedThreeways.add(Lists.newArrayList(temp));
		temp.clear();
	    }
	    // individual room pools
	    temp.add("dimdungeons:tetris_1");
	    temp.add("dimdungeons:tetris_2");
	    temp.add("dimdungeons:tetris_3");
	    tempAdvancedThreeways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:redstrap_4");
	    tempAdvancedThreeways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:advice_2");
	    tempAdvancedThreeways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:mazenotfound_2");
	    tempAdvancedThreeways.add(Lists.newArrayList(temp));
	    temp.clear();

	    //
	    // advanced hallways
	    //
	    List<List<String>> tempAdvancedHallways = Lists.newArrayList();
	    // add all hallways as separate pools
	    for (int i = 1; i <= 6; i++)
	    {
		temp.add("dimdungeons:hallway_" + i);
		tempAdvancedHallways.add(Lists.newArrayList(temp));
		temp.clear();
	    }
	    // add all extrahall as separate pools
	    for (int i = 1; i <= 5; i++)
	    {
		temp.add("dimdungeons:extrahall_" + i);
		tempAdvancedHallways.add(Lists.newArrayList(temp));
		temp.clear();
	    }
	    // individual room pools
	    temp.add("dimdungeons:coalhall_1");
	    temp.add("dimdungeons:coalhall_2");
	    temp.add("dimdungeons:coalhall_3");
	    tempAdvancedHallways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:juke_1");
	    tempAdvancedHallways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:advice_3");
	    tempAdvancedHallways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:library_2");
	    tempAdvancedHallways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:moohall_1");
	    tempAdvancedHallways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:redstrap_2");
	    tempAdvancedHallways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:mazenotfound_3");
	    tempAdvancedHallways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:yinyang_1");
	    temp.add("dimdungeons:yinyang_2");
	    tempAdvancedHallways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:waterhall_1");
	    tempAdvancedHallways.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:tempt_1");
	    temp.add("dimdungeons:tempt_2");
	    temp.add("dimdungeons:tempt_3");
	    temp.add("dimdungeons:tempt_4");
	    tempAdvancedHallways.add(Lists.newArrayList(temp));
	    temp.clear();

	    //
	    // advanced corners
	    //
	    List<List<String>> tempAdvancedCorners = Lists.newArrayList();
	    // add all corners as separate pools
	    for (int i = 1; i <= 8; i++)
	    {
		temp.add("dimdungeons:corner_" + i);
		tempAdvancedCorners.add(Lists.newArrayList(temp));
		temp.clear();
	    }
	    // add all longcorner as separate pools
	    for (int i = 1; i <= 5; i++)
	    {
		temp.add("dimdungeons:longcorner_" + i);
		tempAdvancedCorners.add(Lists.newArrayList(temp));
		temp.clear();
	    }
	    // individual room pools
	    temp.add("dimdungeons:redstrap_3");
	    tempAdvancedCorners.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:skullcorner_1");
	    tempAdvancedCorners.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:mazenotfound_1");
	    tempAdvancedCorners.add(Lists.newArrayList(temp));
	    temp.clear();

	    //
	    // advanced ends
	    //
	    List<List<String>> tempAdvancedEnds = Lists.newArrayList();
	    // add all deadends as separate pools
	    for (int i = 1; i <= 8; i++)
	    {
		temp.add("dimdungeons:deadend_" + i);
		tempAdvancedEnds.add(Lists.newArrayList(temp));
		temp.clear();
	    }
	    // individual room pools
	    temp.add("dimdungeons:coffin_1");
	    temp.add("dimdungeons:coffin_2");
	    temp.add("dimdungeons:coffin_4");
	    tempAdvancedEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:advice_1");
	    tempAdvancedEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:restroom_1");
	    tempAdvancedEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:shoutout_1");
	    temp.add("dimdungeons:shoutout_2");
	    temp.add("dimdungeons:shoutout_3");
	    tempAdvancedEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:spawner_1");
	    temp.add("dimdungeons:spawner_2");
	    temp.add("dimdungeons:spawner_3");
	    temp.add("dimdungeons:spawner_4");
	    temp.add("dimdungeons:spawner_5");
	    temp.add("dimdungeons:spawner_6");
	    tempAdvancedEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:redspuzzle_1");
	    temp.add("dimdungeons:redspuzzle_2");
	    temp.add("dimdungeons:redspuzzle_3");
	    temp.add("dimdungeons:redspuzzle_4");
	    tempAdvancedEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:deathtrap_1");
	    temp.add("dimdungeons:deathtrap_2");
	    temp.add("dimdungeons:deathtrap_3");
	    temp.add("dimdungeons:deathtrap_4");
	    tempAdvancedEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:keytrap_1");
	    temp.add("dimdungeons:keytrap_2");
	    temp.add("dimdungeons:keytrap_3");
	    temp.add("dimdungeons:keytrap_4");
	    temp.add("dimdungeons:keytrap_5");
	    tempAdvancedEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:library_1");
	    tempAdvancedEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:crueltrap_1");
	    temp.add("dimdungeons:crueltrap_2");
	    temp.add("dimdungeons:crueltrap_3");
	    tempAdvancedEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    temp.add("dimdungeons:beacon_1");
	    tempAdvancedEnds.add(Lists.newArrayList(temp));
	    temp.clear();
	    
	    // room generation for tier 1 dungeons
	    builder.comment("Room Selections for Basic Dungeons").push("roomsTier1");
	    basicEntrances = builder.translation("config.dimdungeons.basicEntrances").define("basicEntrances", tempBasicEntrances);
	    basicFourways = builder.translation("config.dimdungeons.basicFourways").define("basicFourways", tempBasicFourways);
	    basicThreeways = builder.translation("config.dimdungeons.basicThreeways").define("basicThreeways", tempBasicThreeways);
	    basicHallways = builder.translation("config.dimdungeons.basicHallways").define("basicHallways", tempBasicHallways);
	    basicCorners = builder.translation("config.dimdungeons.basicCorners").define("basicCorners", tempBasicCorners);
	    basicEnds = builder.translation("config.dimdungeons.basicEnds").define("basicEnds", tempBasicEnds);
	    builder.pop();
	    
	    // room generation for tier 2 dungeons
	    builder.comment("Room Selections for Advanced Dungeons").push("roomsTier2");
	    advancedEntrances = builder.translation("config.dimdungeons.advancedEntrances").define("advancedEntrances", tempAdvancedEntrances);
	    advancedFourways = builder.translation("config.dimdungeons.advancedFourways").define("advancedFourways", tempAdvancedFourways);
	    advancedThreeways = builder.translation("config.dimdungeons.advancedThreeways").define("advancedThreeways", tempAdvancedThreeways);
	    advancedHallways = builder.translation("config.dimdungeons.advancedHallways").define("advancedHallways", tempAdvancedHallways);
	    advancedCorners = builder.translation("config.dimdungeons.advancedCorners").define("advancedCorners", tempAdvancedCorners);
	    advancedEnds = builder.translation("config.dimdungeons.advancedEnds").define("advancedEnds", tempAdvancedEnds);
	    builder.pop();	    
	}
    }

    public static void refreshClient()
    {
	showParticles = CLIENT.showParticles.get();
	playPortalSounds = CLIENT.playPortalSounds.get();
    }

    public static void refreshServer()
    {
	// this is also where COMMON config is refreshed
	basicEntrances = COMMON.basicEntrances.get();
	basicFourways = COMMON.basicFourways.get();
	basicThreeways = COMMON.basicThreeways.get();
	basicHallways = COMMON.basicHallways.get();
	basicCorners = COMMON.basicCorners.get();
	basicEnds = COMMON.basicEnds.get();

	advancedEntrances = COMMON.advancedEntrances.get();
	advancedFourways = COMMON.advancedFourways.get();
	advancedThreeways = COMMON.advancedThreeways.get();
	advancedHallways = COMMON.advancedHallways.get();
	advancedCorners = COMMON.advancedCorners.get();
	advancedEnds = COMMON.advancedEnds.get();
	
	// refresh SERVER
	configVersion = SERVER.configVersion.get();
	globalBlockProtection = SERVER.globalBlockProtection.get();
	blockBreakWhitelist = SERVER.breakingWhitelist.get().stream().map(DungeonConfig::parseBlock).collect(Collectors.toSet());
	blockInteractBlacklist = SERVER.interactionBlacklist.get().stream().map(DungeonConfig::parseBlock).collect(Collectors.toSet());
    }

    // a helper function for translating ResourceLOcation strings (such as minecraft:chest) into blocks
    private static Block parseBlock(String location)
    {
	Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(location));
	if (block == null)
	{
	    DimDungeons.LOGGER.warn("dimdungeons: blacklist/whitelist could not find block " + location);
	    return Blocks.VOID_AIR; // a block that will do nothing in either the whitelist or blacklist
	}

	return block;
    }

    public static boolean patchouliInstalled()
    {
	return ModList.get().isLoaded("patchouli");
    }
}