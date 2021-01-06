package com.catastrophe573.dimdungeons;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
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
    public static boolean globalBlockProtection = true;
    public static Set<Block> blockBreakWhitelist = Sets.newHashSet();
    public static Set<Block> blockInteractBlacklist = Sets.newHashSet();

    // client options
    public static boolean showParticles = true;
    public static boolean playPortalSounds = true;

    // common options
    // (none)

    public static class ServerConfig
    {
	public final ForgeConfigSpec.BooleanValue globalBlockProtection;

	public final ForgeConfigSpec.ConfigValue<List<? extends String>> breakingWhitelist;
	public final ForgeConfigSpec.ConfigValue<List<? extends String>> interactionBlacklist;

	ServerConfig(ForgeConfigSpec.Builder builder)
	{
	    // these are the blocks that the vanilla design does not want the player to 'open', right click, or use
	    List<String> hardcodedDefaultInteractionBlacklist = Lists.newArrayList();
	    hardcodedDefaultInteractionBlacklist.add("minecraft:dispenser");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:dropper");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:hopper");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:flower_pot");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:anvil");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:chipped_anvil");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:damaged_anvil");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:furnace");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:note_block");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:repeater");
	    hardcodedDefaultInteractionBlacklist.add("minecraft:comparator");
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

	    // list of server options and comments
	    builder.comment("Options for general mod behavior.").push("general");
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
	CommonConfig(ForgeConfigSpec.Builder builder)
	{
	}
    }

    public static void refreshClient()
    {
	showParticles = CLIENT.showParticles.get();
	playPortalSounds = CLIENT.playPortalSounds.get();
    }

    public static void refreshServer()
    {
	// this is also where COMMON config would be refreshed

	// refresh SERVER
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