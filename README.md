# Senbonzakura

A **Minecraft NeoForge 1.21.1** mod adding a collection of legendary weapons with unique abilities, particle effects, crafting progression, and coins.

**Author**: j3ly

---

## Requirements / Building

- Minecraft **1.21.1**
- **NeoForge** (21.1.x)
- Java **21**

```bash
./gradlew build
```

---

## Admin Configuration (Ability XP Cost)

Ability activation XP cost is configurable via **game rules** (admin/operator):

```mcfunction
/gamerule senbonzakuraAbilityXpCostEnabled true|false   # default: false
/gamerule senbonzakuraAbilityXpCostLevels <levels>       # default: 1
```

When enabled, fully charged ability activations consume XP levels (Creative players bypass the cost).

---

## Weapons

> Hold right-click to charge and release to activate abilities (unless noted).

### Senbonzakura
- **Type**: Netherite Sword (8 ATK, 2031 durability)
- **Ability**: Hold right-click to charge, release to dash 12 blocks forward, pushing enemies back (50% dmg) with a 180° sweep (100% dmg) — 5s cooldown
- **Passive**: every 4 hits, gain Swiftness II for 5 seconds
- **Particles**: Cherry blossom petals
- **Crafting** (crafting table, 1 output): 1 Netherite Sword, 1 Exalted Blossom, 2 Sacred Threads, 1 Ancient Amber, 2 Excellent Princess Cut Diamond, 2 Tamahagane

### Crescent Blade of Winds
- **Type**: Diamond Sword (7 ATK, 1561 durability)
- **Ability**: Hold right-click for a 360° slash with knockback — 7s cooldown
- **Passive**: every 4 hits, instantly recharge ability; dual-wielding doubles damage
- **Particles**: Green wind swirls
- **Crafting**: 2 Diamond Swords, 1 Deepwoken Relic, 2 Supreme Alloy, 4 Skystone

### Crypt Blade
- **Type**: Netherite Axe (10 ATK, 2031 durability)
- **Ability**: Hold right-click 5s to charge a devastating explosion (6-block radius, no block damage). Immobile with Resistance II during charge — 15s cooldown
- **Passive**: every 8 hits, apply Withering and Darkness to opponent for 3 seconds
- **Particles**: Dark soul particles
- **Crafting**: 1 Netherite Axe, 2 Deepwoken Relic, 1 Inky Pearl, 2 Sinners Ash, 3 Supreme Alloy

### Rite of Commandments
- **Type**: Netherite Sword (8 ATK, 2031 durability)
- **Ability**: Hold right-click for 2 seconds to summon a ring of lightning bolts around you and gain Absorption for 5 seconds — 8s cooldown
- **Passive**: every 5 hits, summon a lightning bolt on your target
- **Particles**: White divine particles
- **Crafting**: 1 Netherite Sword, 2 Ultra Conductive Material, 1 Deepwoken Relic, 1 Supreme Alloy, 2 Skystone, 2 Sacred Threads

### Pyrerizer
- **Type**: Netherite Sword (8 ATK, 2031 durability)
- **Ability**: Hold right-click to charge, release to shoot a volley of 4 fire projectiles dealing 6 dmg each with fire effect — 7s cooldown
- **Passive**: every hit, set your opponent on fire and heal yourself by 0.5 hearts
- **Particles**: Grey smoke and fire particles
- **Crafting**: 1 Netherite Sword, 3 Sinners Ash, 3 Cindering Core, 1 Deepwoken Relic, 1 Supreme Alloy

### Yuki no Ame
- **Type**: Netherite Sword (8 ATK, 2031 durability) — upgraded Senbonzakura
- **Ability**: Hold right-click to charge, release to dash 12 blocks forward, pushing enemies back (50% dmg) with a 180° sweep (100% dmg) — 5s cooldown
- **Passive**: every 4 hits, gain Swiftness II and Regeneration for 5 seconds and apply Slowness II and Weakness II to your opponent for 3 seconds
- **Particles**: Snowflakes
- **Crafting**: 1 Senbonzakura, 4 Frozen Core, 4 Excellent Princess Cut Diamond

### God Rock (NEW)
- **Type**: Cobblestone-style item (19.5 ATK, infinite durability)
- **Passive**: While holding, gain Resistance II
- **Particles**: Yellow sparkles
- **Crafting**: 1 Yuki no Ame, 2 Crescent Blade of Winds, 1 Crypt Blade, 1 Rite of Commandments, 1 Pyrerizer

### Saw Blade Axe
- **Type**: Axe
- **Passive**: Mines wood blocks very fast
- **Crafting**: (existing mod recipe)

---

## Crafting Items & Recipes (Crafting Table, 1 output)

| Item | Recipe / Source |
|------|------------------|
| **Supreme Alloy** | 1 Netherite Block, 2 Diamond Blocks, 3 Gold Blocks, 3 Darkened Rubble |
| **Darkened Rubble** | 4 Netherite Ingots, 4 Gilded Blackstone, 1 Nether Star (also drops from Zombified Piglin 1/150) |
| **Inky Pearl** | 1 Eye of Ender, 6 Netherite Ingots, 2 Ink Sacs |
| **Tamahagane** | 3 Iron Blocks, 6 Coal Blocks |
| **Excellent Princess Cut Diamond** | 3 Diamond Blocks, 1 Crude Jewel, 2 Tamahagane, 3 Stone Bricks |
| **Crude Jewel** (`crude_jewel_steel`) | 8 Deepslate Diamond Ore, 1 Diamond Block |
| **Sacred Threads** | 1 String, 8 Gold Blocks |
| **Ultra Conductive Material** | 4 Copper Blocks, 2 Gold Blocks, 2 Skystone, 1 Ancient Amber |
| **Frozen Core** | 1 Cindering Core, 1 Skystone |

## Drops

| Item | Drop |
|------|------|
| **Exalted Blossom** | 1/2500 from Cherry Blossom Leaves |
| **Ancient Amber** | 1/300 when breaking/stripping a log with an axe |
| **Deepwoken Relic** | 1/2000 from Zombies, Skeletons, Spiders, Creepers, Endermen |
| **Sinners Ash** | 1/10 from Ender Dragon, Wither, Warden |
| **Skystone** | 1/100 from Phantoms, Breeze |
| **Cindering Core** | 1/100 from Ghast, Blaze |
| **Archaic Crown** | (existing drop) |

---

## Coins (Crafting Table, shapeless / any order)

Ingredients use vanilla materials (vanilla has no silver; **iron ingot** is used as “silver”):

- **Gold Coin**: 2 Gold Ingots + 1 Iron Ingot
- **Shiny Coin**: 1 Gold Ingot + 1 Iron Ingot + 2 Copper Ingots
- **Copper Coin**: 4 Copper Ingots **or** 3 Copper Ingots + 1 Iron Ingot

---

## License

Copyright (c) CCC Contributors. All rights reserved. No part of this software or its source code may be reproduced, distributed, or transmitted in any form or by any means without the prior written permission of the author.
