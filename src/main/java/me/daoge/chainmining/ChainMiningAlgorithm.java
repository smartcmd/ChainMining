package me.daoge.chainmining;

import org.allaymc.api.block.dto.Block;
import org.allaymc.api.block.type.BlockState;
import org.allaymc.api.block.type.BlockType;
import org.allaymc.api.world.Dimension;
import org.joml.Vector3i;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * BFS algorithm for finding connected blocks of the same type.
 *
 * @author daoge_cmd
 */
public final class ChainMiningAlgorithm {

    // Adjacent block offsets (6 faces: up, down, north, south, east, west)
    private static final int[][] OFFSETS = {
            {0, 1, 0},   // Up
            {0, -1, 0},  // Down
            {0, 0, -1},  // North
            {0, 0, 1},   // South
            {1, 0, 0},   // East
            {-1, 0, 0}   // West
    };

    private ChainMiningAlgorithm() {
        // Utility class, prevent instantiation
    }

    /**
     * Find all connected blocks of the same type using BFS algorithm.
     *
     * @param dimension  the dimension to search in
     * @param startBlock the starting block
     * @param maxBlocks  the maximum number of blocks to find
     * @return a set of positions of all connected blocks (including the start block)
     */
    public static Set<Vector3i> findChainBlocks(Dimension dimension, Block startBlock, int maxBlocks) {
        Set<Vector3i> visited = new HashSet<>();
        Queue<Vector3i> queue = new LinkedList<>();

        BlockType<?> targetType = startBlock.getBlockType();
        var pos = startBlock.getPosition();
        Vector3i startPos = new Vector3i(pos.x(), pos.y(), pos.z());

        queue.add(startPos);
        visited.add(startPos);

        while (!queue.isEmpty() && visited.size() < maxBlocks) {
            Vector3i current = queue.poll();

            // Check all 6 adjacent blocks
            for (int[] offset : OFFSETS) {
                int nx = current.x + offset[0];
                int ny = current.y + offset[1];
                int nz = current.z + offset[2];

                Vector3i neighbor = new Vector3i(nx, ny, nz);

                // Skip if already visited
                if (visited.contains(neighbor)) {
                    continue;
                }

                // Check if within world bounds
                if (!isValidPosition(dimension, ny)) {
                    continue;
                }

                // Get the block state at this position
                BlockState neighborState = dimension.getBlockState(nx, ny, nz);

                // Check if it's the same block type
                if (neighborState.getBlockType() == targetType) {
                    visited.add(neighbor);
                    queue.add(neighbor);

                    // Stop if we've reached the limit
                    if (visited.size() >= maxBlocks) {
                        break;
                    }
                }
            }
        }

        return visited;
    }

    /**
     * Check if a Y coordinate is within the valid range for the dimension.
     *
     * @param dimension the dimension
     * @param y         the Y coordinate
     * @return true if the position is valid
     */
    private static boolean isValidPosition(Dimension dimension, int y) {
        var dimInfo = dimension.getDimensionInfo();
        return y >= dimInfo.minHeight() && y < dimInfo.maxHeight();
    }
}
