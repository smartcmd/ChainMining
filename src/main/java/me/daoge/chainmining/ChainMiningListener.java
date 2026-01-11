package me.daoge.chainmining;

import org.allaymc.api.block.dto.Block;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.eventbus.EventHandler;
import org.allaymc.api.eventbus.event.block.BlockBreakEvent;
import org.allaymc.api.item.ItemStack;
import org.allaymc.api.message.I18n;
import org.allaymc.api.message.LangCode;
import org.allaymc.api.utils.TextFormat;
import org.allaymc.api.world.Dimension;
import org.joml.Vector3i;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Event listener for chain mining functionality.
 *
 * @author daoge_cmd
 */
public class ChainMiningListener {

    /**
     * Tracks blocks that are currently being chain-mined to prevent recursion.
     * When breakBlock() is called for chained blocks, it triggers another BlockBreakEvent.
     * This set prevents those events from triggering more chain mining.
     * Uses ConcurrentHashMap.newKeySet() for thread safety.
     */
    private final Set<Vector3i> processingBlocks = ConcurrentHashMap.newKeySet();

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        // Check if event is cancelled
        if (event.isCancelled()) {
            return;
        }

        // Check if the breaker is a player
        if (!(event.getEntity() instanceof EntityPlayer player)) {
            return;
        }

        Block block = event.getBlock();
        var blockPos = block.getPosition();
        Vector3i currentPos = new Vector3i(blockPos.x(), blockPos.y(), blockPos.z());

        // Prevent recursion: skip if this block is being chain-mined
        if (processingBlocks.contains(currentPos)) {
            return;
        }

        ChainMiningConfig config = ChainMining.getInstance().getConfig();

        // Check if sneaking is required and player is not sneaking
        if (config.isRequireSneaking() && !player.isSneaking()) {
            return;
        }

        String blockId = block.getBlockType().getIdentifier().toString();

        // Check if this block type is chainable
        if (!config.isChainable(blockId)) {
            return;
        }

        // Get the item used to break the block
        ItemStack usedItem = event.getUsedItem();
        Dimension dimension = block.getDimension();

        // Find all connected blocks of the same type using BFS
        Set<Vector3i> chainedBlocks = ChainMiningAlgorithm.findChainBlocks(
                dimension,
                block,
                config.getMaxChainSize()
        );

        // Remove the original block from the set (it will be broken normally by the event)
        chainedBlocks.remove(currentPos);

        if (chainedBlocks.isEmpty()) {
            return;
        }

        // Mark all blocks as being processed to prevent recursion
        processingBlocks.addAll(chainedBlocks);

        try {
            // Break all chained blocks
            for (Vector3i pos : chainedBlocks) {
                // breakBlock handles drops and XP automatically
                dimension.breakBlock(pos.x, pos.y, pos.z, usedItem, player, true);
            }

            // Send chain mining message to player
            if (config.isShowChainMessage()) {
                int chainedCount = chainedBlocks.size();
                LangCode langCode = getPlayerLangCode(player);
                String message = TextFormat.GREEN + I18n.get().tr(
                        langCode,
                        "chainmining:message.chain_success",
                        chainedCount + 1  // +1 to include the original block
                );
                player.sendMessage(message);
            }
        } finally {
            // Clean up: remove processed blocks from the set
            processingBlocks.removeAll(chainedBlocks);
        }
    }

    private LangCode getPlayerLangCode(EntityPlayer player) {
        var controller = player.getController();
        if (controller != null) {
            return controller.getLoginData().getLangCode();
        }
        return LangCode.en_US;
    }
}
