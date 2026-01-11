package me.daoge.chainmining;

import org.allaymc.api.block.type.BlockType;
import org.allaymc.api.command.Command;
import org.allaymc.api.command.SenderType;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.form.Forms;
import org.allaymc.api.message.I18n;
import org.allaymc.api.message.LangCode;
import org.allaymc.api.permission.OpPermissionCalculator;
import org.allaymc.api.player.Player;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.utils.TextFormat;
import org.allaymc.api.utils.identifier.Identifier;

import java.util.List;

/**
 * Command for configuring chain mining via Form GUI.
 *
 * @author daoge_cmd
 */
public class ChainMiningCommand extends Command {

    public ChainMiningCommand() {
        super("chainmining", "chainmining:command.description", "chainmining.command");
        aliases.add("cm");
    }

    @Override
    public void prepareCommandTree(CommandTree tree) {
        tree.getRoot()
                .exec((context, player) -> {
                    showMainMenu(player);
                    return context.success();
                }, SenderType.PLAYER);
    }

    private Player getPlayer(EntityPlayer entityPlayer) {
        return entityPlayer.getController();
    }

    private LangCode getPlayerLangCode(EntityPlayer entityPlayer) {
        var controller = entityPlayer.getController();
        if (controller != null) {
            return controller.getLoginData().getLangCode();
        }
        return LangCode.en_US;
    }

    private String tr(EntityPlayer player, String key, Object... args) {
        return I18n.get().tr(getPlayerLangCode(player), key, args);
    }

    private void showMainMenu(EntityPlayer entityPlayer) {
        Player player = getPlayer(entityPlayer);
        if (player == null) return;

        Forms.simple()
                .title(tr(entityPlayer, "chainmining:gui.title"))
                .content(tr(entityPlayer, "chainmining:gui.main_content"))
                .button(tr(entityPlayer, "chainmining:gui.view_blocks")).onClick(btn -> showBlockList(entityPlayer))
                .button(tr(entityPlayer, "chainmining:gui.add_block")).onClick(btn -> showAddBlockForm(entityPlayer))
                .button(tr(entityPlayer, "chainmining:gui.remove_block")).onClick(btn -> showRemoveBlockForm(entityPlayer))
                .button(tr(entityPlayer, "chainmining:gui.set_max_chain")).onClick(btn -> showMaxChainForm(entityPlayer))
                .button(tr(entityPlayer, "chainmining:gui.toggle_settings")).onClick(btn -> showToggleSettingsForm(entityPlayer))
                .sendTo(player);
    }

    private void showBlockList(EntityPlayer entityPlayer) {
        Player player = getPlayer(entityPlayer);
        if (player == null) return;

        ChainMiningConfig config = ChainMining.getInstance().getConfig();
        List<String> blocks = config.getChainableBlocks();

        StringBuilder content = new StringBuilder();
        content.append(tr(entityPlayer, "chainmining:gui.block_list_header")).append("\n\n");

        if (blocks.isEmpty()) {
            content.append(tr(entityPlayer, "chainmining:gui.no_blocks"));
        } else {
            for (int i = 0; i < blocks.size(); i++) {
                content.append(TextFormat.YELLOW).append((i + 1)).append(". ")
                        .append(TextFormat.WHITE).append(blocks.get(i)).append("\n");
            }
        }

        content.append("\n").append(tr(entityPlayer, "chainmining:gui.max_chain_info", config.getMaxChainSize()));

        Forms.simple()
                .title(tr(entityPlayer, "chainmining:gui.view_blocks"))
                .content(content.toString())
                .button(tr(entityPlayer, "chainmining:gui.back")).onClick(btn -> showMainMenu(entityPlayer))
                .sendTo(player);
    }

    private void showAddBlockForm(EntityPlayer entityPlayer) {
        Player player = getPlayer(entityPlayer);
        if (player == null) return;

        Forms.custom()
                .title(tr(entityPlayer, "chainmining:gui.add_block"))
                .input(
                        tr(entityPlayer, "chainmining:gui.block_id_label"),
                        "minecraft:block_name",
                        ""
                )
                .onResponse(responses -> {
                    String blockId = responses.get(0).trim();
                    if (blockId.isEmpty()) {
                        entityPlayer.sendMessage(TextFormat.RED + tr(entityPlayer, "chainmining:message.empty_input"));
                        return;
                    }

                    // Validate block identifier
                    if (!isValidBlockId(blockId)) {
                        entityPlayer.sendMessage(TextFormat.RED + tr(entityPlayer, "chainmining:message.invalid_block", blockId));
                        return;
                    }

                    ChainMiningConfig config = ChainMining.getInstance().getConfig();
                    if (config.addChainableBlock(blockId)) {
                        ChainMining.getInstance().saveConfiguration();
                        entityPlayer.sendMessage(TextFormat.GREEN + tr(entityPlayer, "chainmining:message.block_added", blockId));
                    } else {
                        entityPlayer.sendMessage(TextFormat.YELLOW + tr(entityPlayer, "chainmining:message.block_exists", blockId));
                    }
                })
                .onClose(() -> {
                    // User closed the form without submitting
                })
                .sendTo(player);
    }

    private void showRemoveBlockForm(EntityPlayer entityPlayer) {
        Player player = getPlayer(entityPlayer);
        if (player == null) return;

        ChainMiningConfig config = ChainMining.getInstance().getConfig();
        List<String> blocks = config.getChainableBlocks();

        if (blocks.isEmpty()) {
            Forms.simple()
                    .title(tr(entityPlayer, "chainmining:gui.remove_block"))
                    .content(tr(entityPlayer, "chainmining:gui.no_blocks"))
                    .button(tr(entityPlayer, "chainmining:gui.back")).onClick(btn -> showMainMenu(entityPlayer))
                    .sendTo(player);
            return;
        }

        var form = Forms.simple()
                .title(tr(entityPlayer, "chainmining:gui.remove_block"))
                .content(tr(entityPlayer, "chainmining:gui.select_block_to_remove"));

        for (String blockId : blocks) {
            form.button(blockId).onClick(btn -> {
                if (config.removeChainableBlock(blockId)) {
                    ChainMining.getInstance().saveConfiguration();
                    entityPlayer.sendMessage(TextFormat.GREEN + tr(entityPlayer, "chainmining:message.block_removed", blockId));
                }
            });
        }

        form.button(tr(entityPlayer, "chainmining:gui.back")).onClick(btn -> showMainMenu(entityPlayer))
                .sendTo(player);
    }

    private void showMaxChainForm(EntityPlayer entityPlayer) {
        Player player = getPlayer(entityPlayer);
        if (player == null) return;

        ChainMiningConfig config = ChainMining.getInstance().getConfig();

        Forms.custom()
                .title(tr(entityPlayer, "chainmining:gui.set_max_chain"))
                .label(tr(entityPlayer, "chainmining:gui.max_chain_description"))
                .slider(
                        tr(entityPlayer, "chainmining:gui.max_chain_label"),
                        1, 256, 1, config.getMaxChainSize()
                )
                .onResponse(responses -> {
                    // responses.get(0) is empty for label, responses.get(1) is slider value
                    int maxChain = (int) Float.parseFloat(responses.get(1));
                    config.setMaxChainSize(maxChain);
                    ChainMining.getInstance().saveConfiguration();
                    entityPlayer.sendMessage(TextFormat.GREEN + tr(entityPlayer, "chainmining:message.max_chain_set", maxChain));
                })
                .onClose(() -> {
                    // User closed the form without submitting
                })
                .sendTo(player);
    }

    private void showToggleSettingsForm(EntityPlayer entityPlayer) {
        Player player = getPlayer(entityPlayer);
        if (player == null) return;

        ChainMiningConfig config = ChainMining.getInstance().getConfig();

        Forms.custom()
                .title(tr(entityPlayer, "chainmining:gui.toggle_settings"))
                .label(tr(entityPlayer, "chainmining:gui.toggle_description"))
                .toggle(tr(entityPlayer, "chainmining:gui.require_sneaking"), config.isRequireSneaking())
                .toggle(tr(entityPlayer, "chainmining:gui.show_chain_message"), config.isShowChainMessage())
                .onResponse(responses -> {
                    // responses: [0] = label (empty), [1] = require sneaking, [2] = show message
                    boolean requireSneaking = Boolean.parseBoolean(responses.get(1));
                    boolean showChainMessage = Boolean.parseBoolean(responses.get(2));

                    config.setRequireSneaking(requireSneaking);
                    config.setShowChainMessage(showChainMessage);
                    ChainMining.getInstance().saveConfiguration();

                    entityPlayer.sendMessage(TextFormat.GREEN + tr(entityPlayer, "chainmining:message.settings_saved"));
                })
                .onClose(() -> {
                    // User closed the form without submitting
                })
                .sendTo(player);
    }

    /**
     * Validate if a block identifier exists in the registry.
     *
     * @param blockId the block identifier to validate
     * @return true if the block exists
     */
    private boolean isValidBlockId(String blockId) {
        try {
            Identifier identifier = new Identifier(blockId);
            BlockType<?> blockType = Registries.BLOCKS.get(identifier);
            return blockType != null;
        } catch (Exception e) {
            return false;
        }
    }
}
