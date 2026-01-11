package me.daoge.chainmining;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class for ChainMining plugin using okaeri-config.
 *
 * @author daoge_cmd
 */
@Getter
@Setter
public class ChainMiningConfig extends OkaeriConfig {

    @Comment("List of block identifiers that can be chain-mined")
    @Comment("Use full identifiers like 'minecraft:oak_log'")
    @CustomKey("chainable-blocks")
    private List<String> chainableBlocks = new ArrayList<>(List.of(
            // Logs
            "minecraft:oak_log",
            "minecraft:birch_log",
            "minecraft:spruce_log",
            "minecraft:jungle_log",
            "minecraft:acacia_log",
            "minecraft:dark_oak_log",
            "minecraft:mangrove_log",
            "minecraft:cherry_log",
            "minecraft:crimson_stem",
            "minecraft:warped_stem",
            // Ores
            "minecraft:coal_ore",
            "minecraft:deepslate_coal_ore",
            "minecraft:iron_ore",
            "minecraft:deepslate_iron_ore",
            "minecraft:gold_ore",
            "minecraft:deepslate_gold_ore",
            "minecraft:diamond_ore",
            "minecraft:deepslate_diamond_ore",
            "minecraft:emerald_ore",
            "minecraft:deepslate_emerald_ore",
            "minecraft:lapis_ore",
            "minecraft:deepslate_lapis_ore",
            "minecraft:redstone_ore",
            "minecraft:deepslate_redstone_ore",
            "minecraft:copper_ore",
            "minecraft:deepslate_copper_ore",
            "minecraft:nether_gold_ore",
            "minecraft:nether_quartz_ore",
            "minecraft:ancient_debris"
    ));

    @Comment("Maximum number of blocks to chain mine at once")
    @Comment("Higher values may cause lag, recommended: 64-128")
    @CustomKey("max-chain-size")
    private int maxChainSize = 64;

    @Comment("If true, chain mining only triggers when the player is sneaking")
    @CustomKey("require-sneaking")
    private boolean requireSneaking = true;

    @Comment("If true, show a message to the player when chain mining is triggered")
    @CustomKey("show-chain-message")
    private boolean showChainMessage = true;

    /**
     * Check if a block identifier is in the chainable blocks list.
     *
     * @param blockId the block identifier to check
     * @return true if the block can be chain-mined
     */
    public boolean isChainable(String blockId) {
        return chainableBlocks.contains(blockId);
    }

    /**
     * Add a block to the chainable blocks list.
     *
     * @param blockId the block identifier to add
     * @return true if the block was added, false if it already exists
     */
    public boolean addChainableBlock(String blockId) {
        if (chainableBlocks.contains(blockId)) {
            return false;
        }
        chainableBlocks.add(blockId);
        return true;
    }

    /**
     * Remove a block from the chainable blocks list.
     *
     * @param blockId the block identifier to remove
     * @return true if the block was removed, false if it didn't exist
     */
    public boolean removeChainableBlock(String blockId) {
        return chainableBlocks.remove(blockId);
    }
}
