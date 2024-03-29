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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// thank you gigaherz for showing me an example of a Forge config
public class DungeonConfig
{
    public static final int DEFAULT_CONFIG_VERSION = 4;

    public static final int DEFAULT_BASIC_DUNGEON_SIZE = 25;
    public static final int DEFAULT_ADVANCED_DUNGEON_SIZE = 46;

    public static final int DEFAULT_NUMBER_OF_THEMES = 1;
    public static final int MAXIMUM_NUMBER_OF_THEMES = 99;
    public static final int DEFAULT_THEME_DUNGEON_SIZE = 14;
    public static final int DEFAULT_CHANCE_FOR_THEME_KEYS = 4;

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
    public static int configVersion = DEFAULT_CONFIG_VERSION;
    public static boolean globalBlockProtection = true;
    public static boolean hardcoreMode = false;
    public static boolean enableDebugCheats = false;
    public static int portalCooldownTicks = 80;
    public static int keyEnscriberDowngradeChanceFull = 100;
    public static int keyEnscriberDowngradeChanceUsed = 100;
    public static int keyEnscriberDowngradeChanceDamaged = 100;
    public static String logLevel = "error";
    public static String worldborderToRespect = "error";
    public static int numberOfThemes = 1;
    public static int chanceForThemeKeys = DEFAULT_CHANCE_FOR_THEME_KEYS;
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
    public static List<? extends List<String>> advancedLarge;

    public static List<? extends String> basicEnemySet1;
    public static List<? extends String> basicEnemySet2;
    public static List<? extends String> advancedEnemySet1;
    public static List<? extends String> advancedEnemySet2;
    public static double basicEnemyHealthScaling = 1.0f;
    public static double advancedEnemyHealthScaling = 2.0f;

    // theme options
    public static class ThemeStructure
    {
	public List<? extends List<String>> themeEntrances;
	public List<? extends List<String>> themeFourways;
	public List<? extends List<String>> themeThreeways;
	public List<? extends List<String>> themeHallways;
	public List<? extends List<String>> themeCorners;
	public List<? extends List<String>> themeEnds;
	public List<? extends String> themeEnemySet1;
	public List<? extends String> themeEnemySet2;
	public double themeEnemyHealthScaling;
	public int themeDungeonSize;
    }

    // actual theme options
    public static List<ThemeStructure> themeSettings;

    public static class ServerConfig
    {
	public final ConfigValue<Integer> configVersion;

	public final ForgeConfigSpec.BooleanValue globalBlockProtection;
	public final ForgeConfigSpec.BooleanValue hardcoreMode;
	public final ForgeConfigSpec.BooleanValue enableDebugCheats;
	public final ConfigValue<Integer> portalCooldownTicks;
	public final ConfigValue<Integer> keyEnscriberDowngradeChanceFull;
	public final ConfigValue<Integer> keyEnscriberDowngradeChanceUsed;
	public final ConfigValue<Integer> keyEnscriberDowngradeChanceDamaged;
	public final ConfigValue<String> logLevel;
	public final ConfigValue<String> worldborderToRespect;
	public final ConfigValue<Integer> numberOfThemes;
	public final ConfigValue<Integer> chanceForThemeKeys;

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

	    // by default nothing should be breakable. but gravestone/death chest-type mods need this special exception
	    List<String> hardcodedDefaultBreakingWhitelist = Lists.newArrayList();
	    hardcodedDefaultBreakingWhitelist.add("gravestone:gravestone");

	    // list of server options and comments
	    builder.comment("Options for general mod behavior.").push("general");

	    configVersion = builder.comment("You shouldn't manually change the version number.").translation("config.dimdungeons.configVersion").define("configVersion", DEFAULT_CONFIG_VERSION);

	    globalBlockProtection = builder.comment("If set to FALSE the block protection on the dungeon dimension will be disabled, making the options in the next section useless.").translation("config.dimdungeons.globalBlockProtection")
		    .define("globalBlockProtection", true);
	    hardcoreMode = builder.comment("If set to TRUE then dungeon keys are consumed whenever a player enters a dungeon portal.").translation("config.dimdungeons.hardcoreMode").define("hardcoreMode", false);
	    enableDebugCheats = builder.comment("If set to TRUE some cheats are available.").translation("config.dimdungeons.enableDebugCheats").define("enableDebugCheats", false);
	    portalCooldownTicks = builder.comment("How many ticks the portal blocks cooldown for.").translation("config.dimdungeons.portalCooldownTicks").define("portalCooldownTicks", 80);
	    keyEnscriberDowngradeChanceFull = builder.comment("The odds of a Key Enscriber taking damage upon use, like an anvil, turning into a Used Key Enscriber. Range 0-100.").translation("config.dimdungeons.keyEnscriberDowngradeChanceFull")
		    .define("keyEnscriberDowngradeChanceFull", 100);
	    keyEnscriberDowngradeChanceUsed = builder.comment("The odds of a Used Key Enscriber taking damage upon use, like an anvil, turning into a Damaged Key Enscriber. Range 0-100.").translation("config.dimdungeons.keyEnscriberDowngradeChanceUsed")
		    .define("keyEnscriberDowngradeChanceUsed", 100);
	    keyEnscriberDowngradeChanceDamaged = builder.comment("The odds of a Damaged Key Enscriber being destroyed upon use, like a damaged anvil. Range 0-100.").translation("config.dimdungeons.keyEnscriberDowngradeChanceDamaged")
		    .define("keyEnscriberDowngradeChanceDamaged", 100);
	    logLevel = builder.comment("Can be used to limit log spam. Can be set to 'all', 'warn', or 'error'.").translation("config.dimdungeons.logLevel").define("logLevel", "error");
	    worldborderToRespect = builder.comment("Which dimension's worldborder to consider when activating keys. Using dimdungeons:dungeon_dimension may not work for everyone.").translation("config.dimdungeons.worldborderToRespect")
		    .define("worldborderToRespect", "minecraft:overworld");
	    numberOfThemes = builder.comment("The number of themes to expect in the common config.").translation("config.dimdungeons.numberOfThemes").define("numberOfThemes", DEFAULT_NUMBER_OF_THEMES);
	    chanceForThemeKeys = builder.comment("The chance for an enemy in a basic dungeon to be carrying a theme key.").translation("config.dimdungeons.chanceForThemeKeys").define("chanceForThemeKeys", DEFAULT_CHANCE_FOR_THEME_KEYS);
	    builder.pop();
	    builder.comment("Options for block behavior in the dungeon dimension.").push("blocks");
	    breakingWhitelist = builder.comment("List of blocks which any player should be allowed to break, defying the block protection. (For example, gravestones or death chests.) Default value is empty.")
		    .translation("config.dimdungeons.breakingWhitelist").defineList("breakingWhitelist", hardcodedDefaultBreakingWhitelist, o -> o instanceof String);
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

    public static List<? extends List<String>> defaultBasicEntrances()
    {
	List<String> temp = Lists.newArrayList();
	List<List<String>> tempBasicEntrances = Lists.newArrayList();
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

	return tempBasicEntrances;
    }

    public static List<? extends List<String>> defaultBasicFourways()
    {
	List<String> temp = Lists.newArrayList();
	List<List<String>> tempBasicFourways = Lists.newArrayList();
	temp.add("dimdungeons:fourway_1");
	temp.add("dimdungeons:fourway_2");
	temp.add("dimdungeons:fourway_3");
	tempBasicFourways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:fourway_4");
	temp.add("dimdungeons:fourway_5");
	temp.add("dimdungeons:fourway_6");
	tempBasicFourways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:combat_1");
	temp.add("dimdungeons:combat_2");
	temp.add("dimdungeons:combat_3");
	tempBasicFourways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:combat_4");
	temp.add("dimdungeons:combat_5");
	temp.add("dimdungeons:combat_6");
	tempBasicFourways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:disco_1");
	temp.add("dimdungeons:disco_2");
	temp.add("dimdungeons:disco_3");
	temp.add("dimdungeons:disco_4");
	tempBasicFourways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:redstrap_1");
	temp.add("dimdungeons:redore_1");
	temp.add("dimdungeons:redore_2");
	tempBasicFourways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:hiddenpath_1");
	temp.add("dimdungeons:hiddenpath_2");
	temp.add("dimdungeons:hiddenpath_3");
	tempBasicFourways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:chain_1");
	temp.add("dimdungeons:honeyfall_2");
	tempBasicFourways.add(Lists.newArrayList(temp));
	temp.clear();

	return tempBasicFourways;

    }

    public static List<? extends List<String>> defaultBasicThreeways()
    {
	List<String> temp = Lists.newArrayList();
	List<List<String>> tempBasicThreeways = Lists.newArrayList();
	temp.add("dimdungeons:threeway_1");
	temp.add("dimdungeons:threeway_2");
	tempBasicThreeways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:threeway_5");
	temp.add("dimdungeons:morethree_2");
	tempBasicThreeways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:morethree_3");
	temp.add("dimdungeons:morethree_4");
	tempBasicThreeways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:morethree_5");
	temp.add("dimdungeons:morethree_6");
	tempBasicThreeways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:tetris_1");
	temp.add("dimdungeons:tetris_2");
	temp.add("dimdungeons:tetris_3");
	tempBasicThreeways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:redstrap_4");
	temp.add("dimdungeons:chesttrap_3");
	tempBasicThreeways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:advice_2");
	temp.add("dimdungeons:advice_2");
	temp.add("dimdungeons:slotmachine_1");
	temp.add("dimdungeons:mazenotfound_2");
	tempBasicThreeways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:firepath_5");
	temp.add("dimdungeons:honeyfall_4");
	tempBasicThreeways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:bedroom_1");
	temp.add("dimdungeons:bedroom_2");
	tempBasicThreeways.add(Lists.newArrayList(temp));
	temp.clear();

	return tempBasicThreeways;
    }

    public static List<? extends List<String>> defaultBasicHallways()
    {
	List<String> temp = Lists.newArrayList();
	List<List<String>> tempBasicHallways = Lists.newArrayList();
	temp.add("dimdungeons:hallway_1");
	temp.add("dimdungeons:hallway_2");
	temp.add("dimdungeons:hallway_4");
	tempBasicHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:hallway_5");
	temp.add("dimdungeons:hallway_6");
	temp.add("dimdungeons:redsand_3");
	tempBasicHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:coalhall_1");
	temp.add("dimdungeons:coalhall_2");
	temp.add("dimdungeons:coalhall_3");
	tempBasicHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:juke_1");
	temp.add("dimdungeons:juke_2");
	temp.add("dimdungeons:juke_3");
	tempBasicHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:advice_3");
	tempBasicHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:library_2");
	temp.add("dimdungeons:chesttrap_1");
	tempBasicHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:moohall_1");
	temp.add("dimdungeons:moohall_2");
	tempBasicHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:redstrap_2");
	temp.add("dimdungeons:crushhall_1");
	temp.add("dimdungeons:mazenotfound_3");
	tempBasicHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:yinyang_1");
	temp.add("dimdungeons:yinyang_2");
	tempBasicHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:waterhall_1");
	temp.add("dimdungeons:firepath_4");
	tempBasicHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:tempt_1");
	temp.add("dimdungeons:tempt_2");
	temp.add("dimdungeons:tempt_3");
	temp.add("dimdungeons:tempt_4");
	tempBasicHallways.add(Lists.newArrayList(temp));
	temp.clear();

	return tempBasicHallways;
    }

    public static List<? extends List<String>> defaultBasicCorners()
    {
	List<String> temp = Lists.newArrayList();
	List<List<String>> tempBasicCorners = Lists.newArrayList();
	temp.add("dimdungeons:corner_1");
	temp.add("dimdungeons:corner_3");
	tempBasicCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:firepath_2");
	tempBasicCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:corner_4");
	tempBasicCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:corner_5");
	temp.add("dimdungeons:redsand_1");
	tempBasicCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:corner_6");
	temp.add("dimdungeons:corner_7");
	tempBasicCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:longcorner_1");
	temp.add("dimdungeons:longcorner_3");
	tempBasicCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:longcorner_4");
	temp.add("dimdungeons:longcorner_5");
	tempBasicCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:icetrap_1");
	tempBasicCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:redstrap_3");
	temp.add("dimdungeons:redstrap_3");
	temp.add("dimdungeons:mazenotfound_1");
	tempBasicCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:skullcorner_1");
	temp.add("dimdungeons:corner_8");
	tempBasicCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:easycorner_1");
	temp.add("dimdungeons:easycorner_2");
	tempBasicCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:easycorner_3");
	temp.add("dimdungeons:easycorner_4");
	tempBasicCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:easycorner_5");
	temp.add("dimdungeons:easycorner_6");
	tempBasicCorners.add(Lists.newArrayList(temp));
	temp.clear();

	return tempBasicCorners;
    }

    public static List<? extends List<String>> defaultBasicEnds()
    {
	List<String> temp = Lists.newArrayList();
	List<List<String>> tempBasicEnds = Lists.newArrayList();
	temp.add("dimdungeons:deadend_1");
	temp.add("dimdungeons:deadend_2");
	temp.add("dimdungeons:deadend_3");
	temp.add("dimdungeons:deadend_4");
	tempBasicEnds.add(Lists.newArrayList(temp));
	temp.clear();
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
	temp.add("dimdungeons:slotmachine_2");
	tempBasicEnds.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:magicpuzzle_1");
	temp.add("dimdungeons:magicpuzzle_2");
	tempBasicEnds.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:speedpuzzle_1");
	temp.add("dimdungeons:speedpuzzle_2");
	tempBasicEnds.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:restroom_1");
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
	temp.add("dimdungeons:crueltrap_1");
	temp.add("dimdungeons:crueltrap_2");
	temp.add("dimdungeons:crueltrap_3");
	tempBasicEnds.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:beacon_2");
	temp.add("dimdungeons:library_1");
	tempBasicEnds.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:freebie_1");
	temp.add("dimdungeons:freebie_2");
	temp.add("dimdungeons:freebie_3");
	temp.add("dimdungeons:chesttrap_2");
	tempBasicEnds.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:honeytrap_1");
	temp.add("dimdungeons:honeytrap_3");
	temp.add("dimdungeons:honeytrap_4");
	tempBasicEnds.add(Lists.newArrayList(temp));
	temp.clear();

	return tempBasicEnds;
    }

    public static List<? extends List<String>> defaultAdvancedEntrances()
    {
	List<String> temp = Lists.newArrayList();
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

	return tempAdvancedEntrances;
    }

    public static List<? extends List<String>> defaultAdvancedFourways()
    {
	List<String> temp = Lists.newArrayList();
	List<List<String>> tempAdvancedFourways = Lists.newArrayList();
	temp.add("dimdungeons:fourway_4");
	temp.add("dimdungeons:fourway_5");
	temp.add("dimdungeons:fourway_6");
	tempAdvancedFourways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:fourway_7");
	temp.add("dimdungeons:fourway_8");
	temp.add("dimdungeons:fourway_9");
	tempAdvancedFourways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:combat_1");
	temp.add("dimdungeons:combat_2");
	temp.add("dimdungeons:combat_3");
	tempAdvancedFourways.add(Lists.newArrayList(temp));
	temp.add("dimdungeons:combat_4");
	temp.add("dimdungeons:combat_5");
	temp.add("dimdungeons:combat_6");
	tempAdvancedFourways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:disco_1");
	temp.add("dimdungeons:disco_2");
	temp.add("dimdungeons:disco_3");
	temp.add("dimdungeons:disco_4");
	tempAdvancedFourways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:redstrap_1");
	temp.add("dimdungeons:redore_1");
	temp.add("dimdungeons:redore_3");
	tempAdvancedFourways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:hiddenpath_1");
	temp.add("dimdungeons:hiddenpath_2");
	temp.add("dimdungeons:hiddenpath_3");
	temp.add("dimdungeons:swimmaze_1");
	tempAdvancedFourways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:chain_2");
	temp.add("dimdungeons:honeyfall_1");
	tempAdvancedFourways.add(Lists.newArrayList(temp));
	temp.clear();

	return tempAdvancedFourways;
    }

    public static List<? extends List<String>> defaultAdvancedThreeways()
    {
	List<String> temp = Lists.newArrayList();
	List<List<String>> tempAdvancedThreeways = Lists.newArrayList();
	temp.add("dimdungeons:threeway_3");
	temp.add("dimdungeons:threeway_4");
	tempAdvancedThreeways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:threeway_5");
	temp.add("dimdungeons:morethree_2");
	tempAdvancedThreeways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:morethree_3");
	temp.add("dimdungeons:morethree_4");
	tempAdvancedThreeways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:morethree_5");
	temp.add("dimdungeons:morethree_6");
	tempAdvancedThreeways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:tetris_1");
	temp.add("dimdungeons:tetris_2");
	temp.add("dimdungeons:tetris_3");
	tempAdvancedThreeways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:redstrap_4");
	temp.add("dimdungeons:chesttrap_4");
	tempAdvancedThreeways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:advice_2");
	temp.add("dimdungeons:advice_2");
	temp.add("dimdungeons:advice_5");
	tempAdvancedThreeways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:slotmachine_1");
	tempAdvancedThreeways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:firepath_6");
	temp.add("dimdungeons:honeyfall_3");
	tempAdvancedThreeways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:bedroom_2");
	temp.add("dimdungeons:bedroom_3");
	tempAdvancedThreeways.add(Lists.newArrayList(temp));
	temp.clear();

	return tempAdvancedThreeways;
    }

    public static List<? extends List<String>> defaultAdvancedHallways()
    {
	List<String> temp = Lists.newArrayList();
	List<List<String>> tempAdvancedHallways = Lists.newArrayList();
	temp.add("dimdungeons:redsand_4");
	temp.add("dimdungeons:extrahall_1");
	temp.add("dimdungeons:extrahall_2");
	tempAdvancedHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:extrahall_3");
	temp.add("dimdungeons:extrahall_4");
	temp.add("dimdungeons:extrahall_5");
	tempAdvancedHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:coalhall_1");
	temp.add("dimdungeons:coalhall_2");
	temp.add("dimdungeons:coalhall_3");
	tempAdvancedHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:juke_1");
	temp.add("dimdungeons:juke_2");
	temp.add("dimdungeons:juke_3");
	tempAdvancedHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:advice_3");
	temp.add("dimdungeons:advice_3");
	temp.add("dimdungeons:advice_6");
	tempAdvancedHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:library_2");
	temp.add("dimdungeons:chesttrap_1");
	tempAdvancedHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:moohall_1");
	temp.add("dimdungeons:moohall_2");
	tempAdvancedHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:redstrap_2");
	temp.add("dimdungeons:crushhall_2");
	tempAdvancedHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:yinyang_1");
	temp.add("dimdungeons:yinyang_2");
	tempAdvancedHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:waterhall_1");
	temp.add("dimdungeons:firepath_1");
	tempAdvancedHallways.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:tempt_1");
	temp.add("dimdungeons:tempt_2");
	temp.add("dimdungeons:tempt_3");
	temp.add("dimdungeons:tempt_4");
	tempAdvancedHallways.add(Lists.newArrayList(temp));
	temp.clear();

	return tempAdvancedHallways;
    }

    public static List<? extends List<String>> defaultAdvancedCorners()
    {
	List<String> temp = Lists.newArrayList();
	List<List<String>> tempAdvancedCorners = Lists.newArrayList();
	temp.add("dimdungeons:corner_1");
	tempAdvancedCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:corner_4");
	tempAdvancedCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:firepath_3");
	tempAdvancedCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:corner_5");
	tempAdvancedCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:redsand_2");
	tempAdvancedCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:corner_6");
	tempAdvancedCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:corner_7");
	tempAdvancedCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:longcorner_1");
	tempAdvancedCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:longcorner_3");
	tempAdvancedCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:longcorner_4");
	temp.add("dimdungeons:longcorner_5");
	tempAdvancedCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:icetrap_2");
	temp.add("dimdungeons:redstrap_3");
	tempAdvancedCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:skullcorner_2");
	temp.add("dimdungeons:corner_8");
	tempAdvancedCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:hardcorner_1");
	temp.add("dimdungeons:hardcorner_2");
	tempAdvancedCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:hardcorner_3");
	temp.add("dimdungeons:hardcorner_4");
	tempAdvancedCorners.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:hardcorner_5");
	temp.add("dimdungeons:hardcorner_6");
	tempAdvancedCorners.add(Lists.newArrayList(temp));
	temp.clear();

	return tempAdvancedCorners;
    }

    public static List<? extends List<String>> defaultAdvancedEnds()
    {
	List<String> temp = Lists.newArrayList();
	List<List<String>> tempAdvancedEnds = Lists.newArrayList();
	temp.add("dimdungeons:deadend_5");
	temp.add("dimdungeons:deadend_6");
	temp.add("dimdungeons:deadend_7");
	temp.add("dimdungeons:deadend_8");
	tempAdvancedEnds.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:coffin_1");
	temp.add("dimdungeons:coffin_2");
	temp.add("dimdungeons:coffin_4");
	tempAdvancedEnds.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:advice_1");
	temp.add("dimdungeons:advice_1");
	temp.add("dimdungeons:advice_4");
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
	temp.add("dimdungeons:magicpuzzle_1");
	temp.add("dimdungeons:magicpuzzle_2");
	tempAdvancedEnds.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:speedpuzzle_3");
	temp.add("dimdungeons:speedpuzzle_4");
	tempAdvancedEnds.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:honeytrap_2");
	temp.add("dimdungeons:honeytrap_3");
	temp.add("dimdungeons:honeytrap_4");
	tempAdvancedEnds.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:beacon_1");
	tempAdvancedEnds.add(Lists.newArrayList(temp));
	temp.clear();
	temp.add("dimdungeons:slotmachine_2");
	tempAdvancedEnds.add(Lists.newArrayList(temp));
	temp.clear();

	return tempAdvancedEnds;
    }

    public static List<? extends List<String>> defaultAdvancedLarge()
    {
	List<String> temp = Lists.newArrayList();
	List<List<String>> tempAdvancedLarge = Lists.newArrayList();
	temp.add("dimdungeons:large_maze");
	temp.add("dimdungeons:large_bricks");
	temp.add("dimdungeons:large_ballroom");
	temp.add("dimdungeons:large_garage");
	temp.add("dimdungeons:large_jetcoaster");
	temp.add("dimdungeons:large_slime");
	temp.add("dimdungeons:large_archery");
	temp.add("dimdungeons:large_hallways");
	tempAdvancedLarge.add(Lists.newArrayList(temp));
	temp.clear();

	return tempAdvancedLarge;
    }

    public static List<? extends String> defaultBasicEnemySet1()
    {
	List<String> temp = Lists.newArrayList();
	temp.add("minecraft:zombie");
	temp.add("minecraft:husk");
	temp.add("minecraft:drowned");
	temp.add("minecraft:spider");

	return temp;
    }

    public static List<? extends String> defaultBasicEnemySet2()
    {
	List<String> temp = Lists.newArrayList();
	temp.add("minecraft:wither_skeleton");
	temp.add("minecraft:stray");
	temp.add("minecraft:skeleton");
	temp.add("minecraft:pillager");

	return temp;
    }

    public static List<? extends String> defaultAdvancedEnemySet1()
    {
	List<String> temp = Lists.newArrayList();
	temp.add("minecraft:pillager");
	temp.add("minecraft:skeleton");
	temp.add("minecraft:stray");
	temp.add("minecraft:blaze");

	return temp;
    }

    public static List<? extends String> defaultAdvancedEnemySet2()
    {
	List<String> temp = Lists.newArrayList();
	temp.add("minecraft:wither_skeleton");
	temp.add("minecraft:hoglin");
	temp.add("minecraft:vindicator");
	temp.add("minecraft:witch");

	return temp;
    }

    // this takes themeNum as a parameter to make the loop where it is used more readable
    // I'm trying to hardcode initial values for lists of lists of strings in the most concise and readable way possible
    public static List<? extends String> defaultThemeEnemySet1(int themeNum)
    {
	if (themeNum == 1)
	{
	    return Lists.newArrayList("minecraft:skeleton", "minecraft:piglin", "minecraft:blaze");
	}

	return defaultAdvancedEnemySet1();
    }

    // this takes themeNum as a parameter to make the loop where it is used more readable
    // I'm trying to hardcode initial values for lists of lists of strings in the most concise and readable way possible
    public static List<? extends String> defaultThemeEnemySet2(int themeNum)
    {
	if (themeNum == 1)
	{
	    return Lists.newArrayList("minecraft:wither_skeleton", "minecraft:blaze", "minecraft:wither_skeleton", "minecraft:blaze", "minecraft:hoglin", "minecraft:piglin_brute");
	}

	return defaultAdvancedEnemySet2();
    }

    // this function is silly in purpose, but it is similar to the other hardcoded functions for the tiered dungeons
    // I just named all the default theme rooms consistently so that this works
    public static List<? extends List<String>> makeDefaultThemeRoomSet(int themeNum, String roomPart, int numRoomsInSet)
    {
	List<List<String>> returnList = Lists.newArrayList();

	// make the room sets have just one room each
	for (int i = 0; i < numRoomsInSet; i++)
	{
	    List<String> temp = Lists.newArrayList();
	    temp.add("dimdungeons:theme" + themeNum + "_" + roomPart + (i + 1));
	    returnList.add(temp);
	}

	return returnList;
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
	public final ForgeConfigSpec.ConfigValue<List<? extends List<String>>> advancedLarge;

	// enemy sets
	public final ForgeConfigSpec.ConfigValue<List<? extends String>> basicEnemySet1;
	public final ForgeConfigSpec.ConfigValue<List<? extends String>> basicEnemySet2;
	public final ForgeConfigSpec.ConfigValue<List<? extends String>> advancedEnemySet1;
	public final ForgeConfigSpec.ConfigValue<List<? extends String>> advancedEnemySet2;
	public final ConfigValue<Double> basicEnemyHealthScaling;
	public final ConfigValue<Double> advancedEnemyHealthScaling;

	public static class ThemeConfigStructure
	{
	    public ForgeConfigSpec.ConfigValue<List<? extends List<String>>> themeEntrances;
	    public ForgeConfigSpec.ConfigValue<List<? extends List<String>>> themeFourways;
	    public ForgeConfigSpec.ConfigValue<List<? extends List<String>>> themeThreeways;
	    public ForgeConfigSpec.ConfigValue<List<? extends List<String>>> themeHallways;
	    public ForgeConfigSpec.ConfigValue<List<? extends List<String>>> themeCorners;
	    public ForgeConfigSpec.ConfigValue<List<? extends List<String>>> themeEnds;
	    public ForgeConfigSpec.ConfigValue<List<? extends String>> themeEnemySet1;
	    public ForgeConfigSpec.ConfigValue<List<? extends String>> themeEnemySet2;
	    public ConfigValue<Double> themeEnemyHealthScaling;
	    public ConfigValue<Integer> themeDungeonSize;
	}

	// themes
	List<ThemeConfigStructure> allThemeConfigs;

	CommonConfig(ForgeConfigSpec.Builder builder)
	{
	    // room generation for tier 1 dungeons
	    builder.comment("Room Selections for Basic Dungeons").push("roomsTier1");
	    basicEntrances = builder.translation("config.dimdungeons.basicEntrances").define("basicEntrances", defaultBasicEntrances());
	    basicFourways = builder.translation("config.dimdungeons.basicFourways").define("basicFourways", defaultBasicFourways());
	    basicThreeways = builder.translation("config.dimdungeons.basicThreeways").define("basicThreeways", defaultBasicThreeways());
	    basicHallways = builder.translation("config.dimdungeons.basicHallways").define("basicHallways", defaultBasicHallways());
	    basicCorners = builder.translation("config.dimdungeons.basicCorners").define("basicCorners", defaultBasicCorners());
	    basicEnds = builder.translation("config.dimdungeons.basicEnds").define("basicEnds", defaultBasicEnds());
	    builder.pop();

	    // room generation for tier 2 dungeons
	    builder.comment("Room Selections for Advanced Dungeons").push("roomsTier2");
	    advancedEntrances = builder.translation("config.dimdungeons.advancedEntrances").define("advancedEntrances", defaultAdvancedEntrances());
	    advancedFourways = builder.translation("config.dimdungeons.advancedFourways").define("advancedFourways", defaultAdvancedFourways());
	    advancedThreeways = builder.translation("config.dimdungeons.advancedThreeways").define("advancedThreeways", defaultAdvancedThreeways());
	    advancedHallways = builder.translation("config.dimdungeons.advancedHallways").define("advancedHallways", defaultAdvancedHallways());
	    advancedCorners = builder.translation("config.dimdungeons.advancedCorners").define("advancedCorners", defaultAdvancedCorners());
	    advancedEnds = builder.translation("config.dimdungeons.advancedEnds").define("advancedEnds", defaultAdvancedEnds());
	    advancedLarge = builder.translation("config.dimdungeons.advancedLarge").define("advancedLarge", defaultAdvancedLarge());
	    builder.pop();

	    // enemy sets
	    builder.comment("Enemy Sets for Dungeons").push("enemySets");
	    basicEnemySet1 = builder.translation("config.dimdungeons.basicEnemySet1").define("basicEnemySet1", defaultBasicEnemySet1());
	    basicEnemySet2 = builder.translation("config.dimdungeons.basicEnemySet2").define("basicEnemySet2", defaultBasicEnemySet2());
	    advancedEnemySet1 = builder.translation("config.dimdungeons.advancedEnemySet1").define("advancedEnemySet1", defaultAdvancedEnemySet1());
	    advancedEnemySet2 = builder.translation("config.dimdungeons.advancedEnemySet2").define("advancedEnemySet2", defaultAdvancedEnemySet2());
	    basicEnemyHealthScaling = builder.translation("config.dimdungeons.basicEnemyHealthScaling").define("basicEnemyHealthScaling", 1.0);
	    advancedEnemyHealthScaling = builder.translation("config.dimdungeons.advancedEnemyHealthScaling").define("advancedEnemyHealthScaling", 2.0);
	    builder.pop();

	    // handle each theme with a separate section in a loop
	    allThemeConfigs = new ArrayList<ThemeConfigStructure>();
	    for (int i = 1; i <= MAXIMUM_NUMBER_OF_THEMES; i++)
	    {
		int numEntrances = 3;
		int numOtherRooms = 6;
		if (i > DEFAULT_NUMBER_OF_THEMES)
		{
		    numEntrances = 0;
		    numOtherRooms = 0;
		}

		ThemeConfigStructure temp = new ThemeConfigStructure();
		builder.comment("Settings for Theme " + i).push("dungeonTheme" + i);
		temp.themeEntrances = builder.translation("config.dimdungeons.themeEntrances" + i).define("themeEntrances" + i, makeDefaultThemeRoomSet(i, "entrance", numEntrances));
		temp.themeFourways = builder.translation("config.dimdungeons.themeFourways" + i).define("themeFourways" + i, makeDefaultThemeRoomSet(i, "fourway", numOtherRooms));
		temp.themeThreeways = builder.translation("config.dimdungeons.basicThreeways" + i).define("themeThreeways" + i, makeDefaultThemeRoomSet(i, "threeway", numOtherRooms));
		temp.themeHallways = builder.translation("config.dimdungeons.basicHallways" + i).define("themeHallways" + i, makeDefaultThemeRoomSet(i, "hallway", numOtherRooms));
		temp.themeCorners = builder.translation("config.dimdungeons.basicCorners" + i).define("themeCorners" + i, makeDefaultThemeRoomSet(i, "corner", numOtherRooms));
		temp.themeEnds = builder.translation("config.dimdungeons.basicEnds" + i).define("themeEnds" + i, makeDefaultThemeRoomSet(i, "end", numOtherRooms));
		temp.themeEnemySet1 = builder.translation("config.dimdungeons.basicEnemySet1_" + i).define("themeEnemySet1_" + i, defaultThemeEnemySet1(i));
		temp.themeEnemySet2 = builder.translation("config.dimdungeons.basicEnemySet2_" + i).define("themeEnemySet2_" + i, defaultThemeEnemySet2(i));
		temp.themeEnemyHealthScaling = builder.translation("config.dimdungeons.themeEnemyHealthScaling" + i).define("themeEnemyHealthScaling" + i, 1.0);
		temp.themeDungeonSize = builder.translation("config.dimdungeons.themeDungeonSize" + i).define("themeDungeonSize" + i, DEFAULT_THEME_DUNGEON_SIZE);
		builder.pop();

		allThemeConfigs.add(i - 1, temp);
	    }
	}
    }

    public static void refreshClient()
    {
	showParticles = CLIENT.showParticles.get();
	playPortalSounds = CLIENT.playPortalSounds.get();
    }

    public static void refreshServer()
    {
	// refresh the server config
	configVersion = SERVER.configVersion.get();
	globalBlockProtection = SERVER.globalBlockProtection.get();
	hardcoreMode = SERVER.hardcoreMode.get();
	enableDebugCheats = SERVER.enableDebugCheats.get();
	portalCooldownTicks = SERVER.portalCooldownTicks.get();
	keyEnscriberDowngradeChanceFull = SERVER.keyEnscriberDowngradeChanceFull.get();
	keyEnscriberDowngradeChanceUsed = SERVER.keyEnscriberDowngradeChanceUsed.get();
	keyEnscriberDowngradeChanceDamaged = SERVER.keyEnscriberDowngradeChanceDamaged.get();
	logLevel = SERVER.logLevel.get();
	worldborderToRespect = SERVER.worldborderToRespect.get();
	chanceForThemeKeys = SERVER.chanceForThemeKeys.get();
	numberOfThemes = SERVER.numberOfThemes.get();
	blockBreakWhitelist = SERVER.breakingWhitelist.get().stream().map(DungeonConfig::parseBlock).collect(Collectors.toSet());
	blockInteractBlacklist = SERVER.interactionBlacklist.get().stream().map(DungeonConfig::parseBlock).collect(Collectors.toSet());

	// this is also where the common config is refreshed
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
	advancedLarge = COMMON.advancedLarge.get();

	basicEnemySet1 = COMMON.basicEnemySet1.get();
	basicEnemySet2 = COMMON.basicEnemySet2.get();
	advancedEnemySet1 = COMMON.advancedEnemySet1.get();
	advancedEnemySet2 = COMMON.advancedEnemySet2.get();
	basicEnemyHealthScaling = COMMON.basicEnemyHealthScaling.get();
	advancedEnemyHealthScaling = COMMON.advancedEnemyHealthScaling.get();

	// refresh all theme configs
	themeSettings = new ArrayList<ThemeStructure>();
	for (int i = 0; i < DungeonConfig.numberOfThemes; i++)
	{
	    ThemeStructure tempStructure = new ThemeStructure();
	    tempStructure.themeEntrances = COMMON.allThemeConfigs.get(i).themeEntrances.get();
	    tempStructure.themeFourways = COMMON.allThemeConfigs.get(i).themeFourways.get();
	    tempStructure.themeThreeways = COMMON.allThemeConfigs.get(i).themeThreeways.get();
	    tempStructure.themeHallways = COMMON.allThemeConfigs.get(i).themeHallways.get();
	    tempStructure.themeCorners = COMMON.allThemeConfigs.get(i).themeCorners.get();
	    tempStructure.themeEnds = COMMON.allThemeConfigs.get(i).themeEnds.get();
	    tempStructure.themeEnemySet1 = COMMON.allThemeConfigs.get(i).themeEnemySet1.get();
	    tempStructure.themeEnemySet2 = COMMON.allThemeConfigs.get(i).themeEnemySet2.get();
	    tempStructure.themeEnemyHealthScaling = COMMON.allThemeConfigs.get(i).themeEnemyHealthScaling.get();
	    tempStructure.themeDungeonSize = COMMON.allThemeConfigs.get(i).themeDungeonSize.get();
	    themeSettings.add(i, tempStructure);
	}
    }

    // a helper function for translating ResourceLocation strings (such as minecraft:chest) into blocks
    private static Block parseBlock(String location)
    {
	Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(location));
	if (block == null)
	{
	    DimDungeons.logMessageWarn("dimdungeons: blacklist/whitelist could not find block " + location);
	    return Blocks.VOID_AIR; // a block that will do nothing in either the whitelist or blacklist
	}

	return block;
    }

    public static boolean isModInstalled(String namespace)
    {
	return ModList.get().isLoaded(namespace);
    }
}