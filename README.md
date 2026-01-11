# ChainMining

A chain mining plugin for [AllayMC](https://github.com/AllayMC/Allay) that automatically breaks adjacent blocks of the same type when mining.

## Features

- **Chain Mining**: When you break a block that is in the chainable blocks list, all adjacent blocks of the same type will also be broken automatically.
- **Sneak to Activate**: Chain mining only triggers when sneaking (configurable).
- **Chain Message**: Shows how many blocks were chain-mined (configurable).
- **Configurable Block List**: Customize which blocks can be chain-mined via configuration file or in-game GUI.
- **Max Chain Size Limit**: Set a maximum number of blocks to prevent lag and abuse.
- **In-Game GUI Configuration**: Use `/chainmining` command to configure the plugin with an intuitive form-based GUI.
- **Multi-Language Support**: Supports English (en_US) and Chinese (zh_CN).
- **Hot Reload**: Configuration can be reloaded without restarting the server.

## Installation

1. Download the latest release from the [Releases](https://github.com/smartcmd/ChainMining/releases) page.
2. Place the `.jar` file in your AllayMC server's `plugins` directory.
3. Start or restart the server.

## Commands

| Command | Alias | Description | Permission |
|---------|-------|-------------|------------|
| `/chainmining` | `/cm` | Opens the configuration GUI | `chainmining.command` |

## Configuration

The configuration file is located at `plugins/ChainMining/config.yml`.

```yaml
# List of block identifiers that can be chain-mined
# Use full identifiers like 'minecraft:oak_log'
chainable-blocks:
  - minecraft:oak_log
  - minecraft:birch_log
  - minecraft:spruce_log
  - minecraft:jungle_log
  - minecraft:acacia_log
  - minecraft:dark_oak_log
  - minecraft:mangrove_log
  - minecraft:cherry_log
  - minecraft:crimson_stem
  - minecraft:warped_stem
  - minecraft:coal_ore
  - minecraft:deepslate_coal_ore
  - minecraft:iron_ore
  - minecraft:deepslate_iron_ore
  - minecraft:gold_ore
  - minecraft:deepslate_gold_ore
  - minecraft:diamond_ore
  - minecraft:deepslate_diamond_ore
  - minecraft:emerald_ore
  - minecraft:deepslate_emerald_ore
  - minecraft:lapis_ore
  - minecraft:deepslate_lapis_ore
  - minecraft:redstone_ore
  - minecraft:deepslate_redstone_ore
  - minecraft:copper_ore
  - minecraft:deepslate_copper_ore
  - minecraft:nether_gold_ore
  - minecraft:nether_quartz_ore
  - minecraft:ancient_debris

# Maximum number of blocks to chain mine at once
# Higher values may cause lag, recommended: 64-128
max-chain-size: 64

# If true, chain mining only triggers when the player is sneaking
require-sneaking: true

# If true, show a message to the player when chain mining is triggered
show-chain-message: true
```

## In-Game Configuration

Use the `/chainmining` command to open the configuration GUI where you can:

- **View Chainable Blocks**: See all currently configured chainable blocks.
- **Add Block**: Add a new block to the chainable list by entering its identifier.
- **Remove Block**: Remove a block from the chainable list.
- **Set Max Chain Size**: Adjust the maximum number of blocks that can be chain-mined at once.
- **Toggle Settings**: Enable/disable sneak requirement and chain message display.

## How It Works

When a player breaks a block:
1. The plugin checks if the player is sneaking (if `require-sneaking` is enabled).
2. The plugin checks if the block type is in the chainable blocks list.
3. If yes, it uses a BFS (Breadth-First Search) algorithm to find all adjacent blocks of the same type.
4. Each found block is broken automatically, with proper item drops and experience.
5. The algorithm respects the max chain size limit to prevent lag.
6. A message is sent to the player showing how many blocks were mined (if `show-chain-message` is enabled).

## Building from Source

```bash
# Clone the repository
git clone https://github.com/smartcmd/ChainMining.git
cd ChainMining

# Build the plugin
./gradlew shadowJar

# The built jar will be in build/libs/
```

## Testing

```bash
# Run a test server with the plugin
./gradlew runServer
```

## Requirements

- Java 21 or higher
- AllayMC Server (API version 0.20.0+)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

- **daoge_cmd** - [GitHub](https://github.com/smartcmd)
