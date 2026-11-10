# Senbonzakura

A **Minecraft NeoForge 1.21.1** mod adding legendary weapons, crafting materials, and coins with custom abilities and progression.

**Author**: j3ly  
**Version**: 1.3.0 (Minecraft 1.21.1, NeoForge 21.1.x, Java 21)

---

## Building

```bash
./gradlew build
```
Output jar: `build/libs/senbonzakura-<version>.jar`

---

## Admin Configuration (Ability XP Cost)

Weapon ability activations can optionally cost XP (admin-configurable via game rules):

```mcfunction
/gamerule senbonzakuraAbilityXpCostEnabled true|false   # default: false
/gamerule senbonzakuraAbilityXpCostLevels <levels>       # default: 1
```

- Applies on **fully charged ability activation** (before cooldown/ability fires).
- If enabled and the player lacks enough levels, activation is blocked with an actionbar message.
- **Creative** players bypass the XP cost.

---

## Obtaining Items (Summary)

- **Weapons**: obtained by **crafting only** (direct weapon drops from mobs are **disabled**).
- **Materials**: obtained by **drops** and/or **crafting** (see tables below).
- **Coins**: obtained by **crafting** (shapeless, any order).
- All crafting recipes listed are **Crafting Table** recipes with **1 output** unless noted. Recipes are shapeless (ingredients can be placed in any order) unless your pack/client shows otherwise.

### Material Drops (item drop rates shown are 3× rarer than previous values)

| Item | How to obtain (drops) | How to obtain (crafting) |
|------|------------------------|--------------------------|
| **Exalted Blossom** | 1/7500 from Cherry Blossom Leaves | — |
| **Ancient Amber** | 1/900 when breaking/stripping a log with an axe | — |
| **Deepwoken Relic** | 1/6000 from Zombies, Skeletons, Spiders, Creepers, Endermen | — |
| **Sinners Ash** | 1/30 from Ender Dragon, Wither, Warden | 3 Wither Skeleton Skulls + 1 Netherite Ingot + 2 Darkened Rubble |
| **Skystone** | 1/300 from Phantoms, Breeze | — |
| **Cindering Core** | 1/300 from Ghast, Blaze | — |
| **Inky Pearl** | 1/90 from Elder Guardian; 1/900 from Guardian or Drowned | 1 Eye of Ender + 6 Netherite Ingots + 2 Ink Sacs |
| **Darkened Rubble** | 1/450 from Zombified Piglin (Nether) | 4 Netherite Ingots + 4 Gilded Blackstone + 1 Nether Star |

> *Notes:* "Netherite Bars" in recipes are **Netherite Ingots**. The in-game item `Crude Jewel` uses item ID `senbonzakura:crude_jewel_steel`.

---

## Crafting Materials

| Item | Crafting recipe | Used to craft |
|------|------------------|---------------|
| **Crude Jewel** (`crude_jewel_steel`) | 8 Deepslate Diamond Ore + 1 Diamond Block | Excellent Princess Cut Diamond |
| **Tamahagane** | 3 Iron Blocks + 6 Coal Blocks | Excellent Princess Cut Diamond, Senbonzakura |
| **Excellent Princess Cut Diamond** | 3 Diamond Blocks + 1 Crude Jewel + 2 Tamahagane + 3 Stone Bricks | Senbonzakura, Yuki no Ame |
| **Sacred Threads** | 1 String + 8 Gold Blocks | Senbonzakura, Rite of Commandments |
| **Ultra Conductive Material** | 4 Copper Blocks + 2 Gold Blocks + 2 Skystone + 1 Ancient Amber | Rite of Commandments |
| **Supreme Alloy** | 1 Netherite Block + 2 Diamond Blocks + 3 Gold Blocks + 3 Darkened Rubble | Crescent Blade of Winds, Crypt Blade, Rite of Commandments, Pyrerizer |
| **Frozen Core** | 1 Cindering Core + 1 Skystone | Yuki no Ame |

---

## Weapons & God Rock

> Hold right-click to charge and release to activate abilities (unless noted). **No weapons drop directly from mobs** — craft them.

| Item | Crafting recipe | Notes |
|------|------------------|-------|
| **Senbonzakura** | 1 Netherite Sword + 1 Exalted Blossom + 2 Sacred Threads + 1 Ancient Amber + 2 Excellent Princess Cut Diamond + 2 Tamahagane | Netherite sword; charge-dash + sweep ability (5s CD), cherry blossom particles |
| **Crescent Blade of Winds** | 2 Diamond Swords + 1 Deepwoken Relic + 2 Supreme Alloy + 4 Skystone | Diamond sword; 360° slash (7s CD), wind particles; dual-wield doubles damage |
| **Crypt Blade** | 1 Netherite Axe + 2 Deepwoken Relic + 1 Inky Pearl + 2 Sinners Ash + 3 Supreme Alloy | Netherite axe; charged explosion ability (15s CD), dark soul particles |
| **Rite of Commandments** | 1 Netherite Sword + 2 Ultra Conductive Material + 1 Deepwoken Relic + 1 Supreme Alloy + 2 Skystone + 2 Sacred Threads | Netherite sword; lightning ability (cooldown per implementation), white particles |
| **Pyrerizer** | 1 Netherite Sword + 3 Sinners Ash + 3 Cindering Core + 1 Deepwoken Relic + 1 Supreme Alloy | Netherite axe-weapon; fire leap ability (7s CD), flame/smoke particles |
| **Yuki no Ame** | 1 Senbonzakura + 4 Frozen Core + 4 Excellent Princess Cut Diamond | Upgraded Senbonzakura; dash/sweep with slow/weakness effects (5s CD), cherry/snowy particles |
| **God Rock** | 1 Yuki no Ame + 2 Crescent Blade of Winds + 1 Crypt Blade + 1 Rite of Commandments + 1 Pyrerizer | 19.5 ATK, unbreakable; grants Resistance II while held; yellow sparkles |
| **Saw Blade Axe** | 3 Iron Ingots + 2 Sticks (shaped) | Mines wood very fast (no special charged ability) |

*Ability/passive details and exact timings follow the current in-mod implementation; see in-game/tooltips and item behavior for specifics.*

---

## Coins (Crafting Table, shapeless / any order)

Vanilla materials only (no vanilla silver; **Iron Ingot** is used as “silver”):

| Coin | Recipe |
|------|--------|
| **Gold Coin** | 2 Gold Ingots + 1 Iron Ingot |
| **Shiny Coin** | 1 Gold Ingot + 1 Iron Ingot + 2 Copper Ingots |
| **Copper Coin** | 4 Copper Ingots **or** 3 Copper Ingots + 1 Iron Ingot |

---

## License

Copyright (c) CCC Contributors. All rights reserved. No part of this software or its source code may be reproduced, distributed, or transmitted in any form or by any means without the prior written permission of the author.
