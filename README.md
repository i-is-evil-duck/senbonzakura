# Senbonzakura

A **Minecraft NeoForge 1.21.1** mod adding legendary weapons, crafting materials, coins, and custom enchantments.

**Author**: j3ly  
**Version**: 2.0.6 (Minecraft 1.21.1, NeoForge 21.1.x, Java 21)

## Recap of recent changes

**2.0.6** — Comprehensive Boss Fixes
- Fixed root cause of repeating attribute crashes: all bosses now inherit complete vanilla attribute sets (IronGolem, Mob, Zombie, Skeleton, Spider parents)
- Fixed King Slime spawn NPE (setSize needs ATTACK_DAMAGE) and boss health override order
- Fixed entity renderer registration bus (MOD bus instead of GAME bus)
- Fixed King Slime boss bar not clearing on death
- Fixed summon items ignoring dimension restrictions
- Fixed invalid `minecraft:prismarine_block` in deep_totem recipe → `minecraft:prismarine`
- Fixed null entity type in CorrodingLureItem

**2.0.5** — Attribute Fixes (gravity)
- Fixed `generic.gravity` attribute crash: added GRAVITY to all boss attribute suppliers
- Bosses no longer crash on entity tick (aiStep → travel → getGravity)

**2.0.4** — Renderer + Attribute Fixes
- Fixed `generic.scale` attribute crash: added SCALE to all boss attribute suppliers
- Fixed `generic.follow_range` crash: added FOLLOW_RANGE to all tier 1 bosses
- All bosses now have proper attribute registration for rendering

**2.0.3** — Entity Renderer Fix
- Fixed crash on boss summon/render: registered entity renderers for all 23 bosses
- Tier 1 bosses use vanilla renderers (SlimeRenderer, ZombieRenderer, SkeletonRenderer, SpiderRenderer)
- Iron Golem-based bosses use IronGolemRenderer as placeholder

**2.0.2** — Spawn Eggs & Creative Tab
- Added new "Chaotic Mode" creative tab with spawn eggs for all 23 bosses
- Fixed FOLLOW_RANGE attribute on King Slime, Zombie General, Skeleton General, Queen Spider (crash on summon)
- Fixed deep_totem recipe format (id instead of item)
- Fixed duplicate creative tab entry crash

**2.0.1** — Chaotic Mode Bosses: Fixes
- Fixed duplicate creative tab entry crash (werewolf_fang)
- Added missing item models for all 88 new items
- Fixed skeleton_general loot table (minecraft:bones → minecraft:bone)

**2.0.0** — Chaotic Mode Bosses: Full Release
- All 23 Chaotic Mode bosses implemented with Iron Golem placeholder reskins
- Tier 1 (Pre-Iron Worm): King Slime, Zombie General, Skeleton General, Queen Spider, Iron Worm
- Post-Ender Dragon/Wither: Werewolf Tribe Chief, Sky Serpent, Hell Serpent
- Post-Warden: Frozen Serpent, Kraken Guardian, Wendigo Warlord
- Post-Wendigo Warlord: Sinful Squire, Sinful Bishop, Sinister Knight
- Post-Sinister Knight: Felio of Flame, Ieirith of Ice, Therim of Thunder, Wahiko of Wind, Sakurai Wahiko (Cherry Grove), Serrik of Shadow
- Final: Lucius Retainer of Ruin, Drallius Visions of Doom, Drallius Chaos UNLEASHED
- All bosses have custom AI abilities, boss bars, anti-despawn, and loot tables
- 23 one-time-use summon items with crafting recipes
- 22-tier advancement progression tree
- 2 new music discs: "Servants of the Scourge" and "Scourge of the Universe & Universal Collapse"
- Textures: Zombie General grey skin, Queen Spider 2x scale — DONE
- See assets.md for all remaining textures and audio assets needed

**1.9.8** — Chaotic Mode Bosses: Custom AI
- **King Slime** — jump slam every 60 ticks: leaps at the player, area damage on landing (6 HP).
- **Zombie General** — undead cleave every 120 ticks: sweeping arc attack in front (4 HP).
- **Skeleton General** — arrow barrage every 80 ticks: fires 3-5 arrows at the target.
- **Queen Spider** — web trap every 100 ticks: places 6 cobwebs around the target.
- All bosses share a custom AI tick framework (`onCustomAiTick()` hook), boss bars, and anti-despawn.

**1.9.7** — Chaotic Mode Bosses (Tier 1, expanded)
- **Zombie General** added — 80 HP (40 hearts), vanilla zombie reskin (grey skin texture), drops Rotten Flesh, Iron Ingots, Forgotten Insignia (50%), Undead Sword (25%), Tomb Chestplate (25%).
- **Skeleton General** added — 80 HP (40 hearts), vanilla skeleton reskin, drops Bones, Iron Ingots, Forgotten Insignia (50%), Undead Bow (25%), Tomb Chestplate (25%).
- **Queen Spider** added — 90 HP (45 hearts), vanilla spider reskin at 2x size, drops String, Spider Eyes, Queen Silk (50%), Spider Necklace (25%), Webber (25%), Noxious Dagger (10%).
- **Summon items:** Putrid Bait (gold block + 4 rotten flesh + 2 poisonous potato), Calcium (4 bone blocks + 4 milk buckets), Suspicious Bug (4 wool + 4 fermented spider eyes + 1 potion).
- **New drops:** Forgotten Insignia, Undead Sword, Undead Bow, Tomb Chestplate, Queen Silk, Spider Necklace, Webber, Noxious Dagger.
- Advancement chain: *Slime Sovereign* → *General of the Undead* → *General of the Bones* → *Queen of the Web*.

**1.9.6** — Chaotic Mode Bosses (Tier 1)
- **King Slime** added — 44 HP (22 hearts), vanilla slime reskin with boss bar, splits into smaller slimes on defeat.
- **Royal Jelly** summon item added — crafted with 1 honey comb + 8 slime blocks. One-time use, Overworld-only.
- King Slime drops: Slime Balls (24–64), Condensed Slime (5–24), Slime Ball Slingshot (25%), Slime Shield (25%).
- Advancement tree started: *Chaotic Mode* (root) → *Slime Sovereign* (kill King Slime).

**1.9.5**
- **New Ducky's Wand recipe:** shaped vertically — Gold Duck Egg on top, Breeze Rod in the middle, Shiny Coin on the bottom.
- **Gold Duck Eggs** are laid by Gold Ducks. Gold Ducks cannot spawn naturally: feed any normal duck a Shiny Coin for a **1/15 chance** to turn it into one. (Requires Ducky's Duck Mod 1.1.0+.)
- **Wand ducks:** left-click shots now spawn a random duck at the impact point which immediately dies and drops nothing. If Ducky's Duck Mod is not installed, the original duck-sprite projectile effect is used. The duck mod is declared as an optional dependency.
- Added `/droprate` entries for the new gold duck transformation.

**1.9.4**
- Ducky Domain is now a sealed white room with an unbreakable outer barrier; its entry/exit chat notifications were removed.
- Cindering Core and Cindering particle textures were swapped.

---

## Building

The project uses [Stonecutter](https://stonecutter.kikugie.dev/) + Stonecraft so
new Minecraft versions and mod loaders can be added incrementally from one source
tree.

```bash
./gradlew build       # active target: 1.21.1-neoforge
```

Output jar: `versions/1.21.1-neoforge/build/libs/senbonzakura-<version>.jar`

**Port status:** NeoForge 1.21.1 is the only built/verified target. The full
version × loader matrix is declared in `settings.gradle.kts` and is gated behind
`-PstonecutterAllTargets=true`; the other targets are not ported yet (several
files use NeoForge-only APIs). Platform-specific entry points are marked with
Stonecutter `//? if neoforge {` directives. The matrix lives in
`settings.gradle.kts` and per-version dependency pins in `versions/dependencies/`.

---

## All Items

| Item | Type | How to obtain | Short description |
|------|------|---------------|-------------------|
| **Senbonzakura** | Weapon (Sword) | Craft: 1 Netherite Sword + 1 Exalted Blossom + 2 Sacred Threads + 1 Ancient Amber + 2 Excellent Princess Cut Diamond + 2 Tamahagane | 11 ATK. Charge-dash and sweep ability (5s CD) with cherry blossom particles. |
| **Crescent Blade of Winds** | Weapon (Sword) | Craft: 2 Diamond Swords + 1 Sigil of Doom + 2 Supreme Alloy + 4 Skystone | 10 ATK. 360° slash ability (7s CD; 3.5s when dual-wielding). While dual-wielding two Crescent Blades, the main blade deals double damage and recharges faster. Wind particles. |
| **Crypt Blade** | Weapon (Axe) | Craft: 1 Netherite Axe + 2 Sigil of Doom + 1 Inky Pearl + 2 Sinners Ash + 3 Supreme Alloy | 16 ATK. Charged explosion ability (15s CD) with dark soul particles. Held with both hands in first person (support hand appears when the offhand is empty). |
| **Saw Blade Axe** | Weapon (Axe) | Craft (shaped): 3 Iron Ingots + 2 Sticks | Utility axe that mines wood very fast; no special charged ability. |
| **Rite of Commandments** | Weapon (Sword) | Craft: 1 Netherite Sword + 2 Ultra Conductive Material + 1 Sigil of Doom + 1 Supreme Alloy + 2 Skystone + 2 Sacred Threads | 14 ATK. Lightning-themed ability with white particles. |
| **Pyrerizer** | Weapon (Axe) | Craft: 1 Netherite Sword + 3 Sinner's Ash + 3 Cindering Core + 1 Sigil of Doom + 1 Supreme Alloy | 14 ATK. Fire ability (7s CD) with flame/smoke particles. |
| **Yuki no Ame** | Weapon (Sword) | Craft: 1 Senbonzakura + 4 Frozen Core + 4 Excellent Princess Cut Diamond | 12 ATK. Upgraded Senbonzakura dash/sweep (5s CD) with snowflake particles. Fully charged hits have a 25% chance to apply Slowness II + Weakness for 5s. Right-click grants Swiftness II for 7s. |
| **Razor Shark** | Weapon (Axe) | Drop: Elder Guardian 1/35, Guardian 1/350, Drowned 1/1250 | 13 ATK. Hold right-click 1.5s to aim a bubble stream, then fire a thick bubble laser (35 blocks, knockback, grey smoke flecks) — 9s CD; grants Night Vision & Water Breathing in inventory. |
| **Sentence** | Weapon (Axe) | Drop: Warden/Wither/Ender Dragon 1/44 | 13 ATK. Hold right-click 1.5s for a 180° swing (6-block range) — 9s CD; applies Darkness & Wither (5s) to targets. |
| **Wendigo** | Weapon (Sword) | Drop: Skeletons/Stray/Wither Skeleton 1/1250, Silverfish 1/1500 | 15 ATK. Right-click fires 64 arrows (10s CD); hits apply Slowness (2s). Supports bow enchants and Increased Arrows. |
| **Empire Staff** | Weapon (Staff) | Craft: 1 Perfect Radiant Cut Diamond + 1 Blaze Rod + 1 Breeze Rod | 12 ATK, 1.6 swing speed. Hold right-click 2s to drop 6 random splash potions (good/bad, 7s duration) around you and nearby enemies — 15s CD. White divine particles. |
| **God Rock** | Weapon/Special | Craft: 1 Yuki no Ame + 2 Crescent Blade of Winds + 1 Crypt Blade + 1 Rite of Commandments + 1 Pyrerizer | 19.5 ATK, unbreakable; grants Resistance II while held with yellow sparkles. |
| **Matthias' Curseblade** | Weapon (Sword) | Craft (shapeless): 1 Netherite Sword + 1 Sinner's Ash + 1 Tamahagane | 9 ATK, 1.6 swing speed. Passive: each defeated mob bursts into purple particles and takes a mob-only explosion — players are never hurt and blocks are never broken. |
| **Ducky's Wand** | Weapon (Wand) | Craft (shaped, vertical): Gold Duck Egg (top) + Breeze Rod (middle) + Shiny Coin (bottom) | 19 ATK, 1.6 swing speed. Left-click fires ducks dealing 3 hearts of magic damage (ignores armor); with Ducky's Duck Mod installed, a random duck spawns on impact and dies without dropping anything, otherwise the duck-sprite projectile effect is used. Right-click opens Ducky Domain: all players and mobs within 15 blocks are pulled into a sealed white room in the same world for 30s, then returned — 120s CD. |
| **Inky Pearl** | Material | Drop: Elder Guardian 1/90, Guardian/Drowned 1/900. Craft: 1 Eye of Ender + 6 Netherite Ingots + 2 Ink Sacs | Dark aquatic pearl used in weapon crafting. |
| **Exalted Blossom** | Material | Drop: Cherry Blossom Leaves 1/7500 | Rare cherry blossom essence used to craft Senbonzakura. |
| **Ancient Amber** | Material | Drop: Breaking/stripping logs with an axe 1/900 | Ancient resin used in advanced crafting. |
| **Sigil of Doom** (`deepwoken_relic`) | Material | Drop: All hostile mobs 1/6000, Silverfish 1/1000 | Ominous relic used in multiple high-tier weapon recipes. |
| **Excellent Princess Cut Diamond** | Material | Craft: 3 Diamond Blocks + 1 Crude Jewel + 2 Tamahagane + 3 Stone Bricks | Refined diamond component for elite weapons. |
| **Perfect Radiant Cut Diamond** | Material | Craft (shapeless): 9 Excellent Princess Cut Diamond | Pinnacle diamond component used to craft the Empire Staff. |
| **Sacred Threads** | Material | Craft: 1 String + 8 Gold Blocks | Blessed threads used in weapon crafting. |
| **Crude Jewel** (`crude_jewel_steel`) | Material | Craft: 8 Deepslate Diamond Ore + 1 Diamond Block | Rough jewel refined into Excellent Princess Cut Diamond. |
| **Tamahagane** | Material | Craft: 3 Iron Blocks + 6 Coal Blocks | High-quality steel used in weapon forging. |
| **Supreme Alloy** | Material | Craft: 1 Netherite Block + 2 Diamond Blocks + 3 Gold Blocks + 3 Darkened Rubble | Powerful alloy for late-game weapons. |
| **Cindering Core** | Material | Drop: Ghast/Blaze 1/300 | Fiery core used in Pyrerizer and Frozen Core. |
| **Ultra Conductive Material** | Material | Craft: 4 Copper Blocks + 2 Gold Blocks + 2 Skystone + 1 Ancient Amber | Conductive component used in Rite of Commandments. |
| **Skystone** | Material | Drop: Phantoms/Breeze/Stray 1/300 | Sky-forged stone used in several recipes. |
| **Darkened Rubble** | Material | Drop: Wither Skeleton/Magma Cube/Blaze/Piglin/Zombified Piglin/Ghast (Nether) 1/1500. Craft: 4 Netherite Ingots + 4 Gilded Blackstone + 1 Nether Star | Nether debris material used in powerful alloys and ash. |
| **Sinner's Ash** | Material | Drop: Ender Dragon/Wither/Warden 1/30. Craft: 3 Wither Skeleton Skulls + 1 Netherite Ingot + 2 Darkened Rubble | Remains of mighty foes used in dark weapon crafting. |
| **Frozen Core** | Material | Craft: 1 Cindering Core + 1 Skystone | Chilled core used to craft Yuki no Ame. |
| **Duck Egg** | Material | Drop: Chicken drowned 1/100 | Rare egg. |
| **Gold Duck Egg** | Material | Laid by Gold Ducks | Gold Ducks only appear when a duck is fed a Shiny Coin (1/15 chance); used with a Breeze Rod and Shiny Coin to craft Ducky's Wand. |
| **Copper Coin** | Coin | Craft (shapeless): 4 Copper Ingots **or** 3 Copper Ingots + 1 Iron Ingot | Basic currency/collectible coin. |
| **Gold Coin** | Coin | Craft (shapeless): 2 Gold Ingots + 1 Iron Ingot | Valuable currency/collectible coin. |
| **Shiny Coin** | Coin | Craft (shapeless): 1 Gold Ingot + 1 Iron Ingot + 2 Copper Ingots | Premium currency/collectible coin. |
| **Enchanted Book (Cindering I–III)** | Enchanted Book | Craft (shapeless): Book + 1/2/3 Cindering Core + 1/2/3 Lapis Lazuli; also from enchanting tables | Stores Cindering I–III for applying to weapons in an anvil. |

> *Notes:* Direct weapon/material drop rates above are base rates; effective chance is scaled by `/droprate` (global) and per-player `/drop`/`/nodrop`. "Iron Ingot" is used as silver in coin recipes.

**Combat visuals**

- **Fix:** the third-person arm-pose mixin no longer shadows an inherited field, which previously aborted resource reload and caused startup/particle crashes.
- **Third-person tweak:** the left arm keeps its grip pose while the right arm angles a little further toward the centre. The Sentence's held-item scale has been reduced so it no longer renders oversized.

- **Two-handed holds:** the Crypt Blade, Senbonzakura, and Yuki no Ame are gripped with both hands in first person and third person (F5 / other players). In third person both arms rotate inward so the hands meet on the grip slightly right of centre. Holding an item in the offhand returns to a one-handed hold.
- **Visible sweep animations:** the Crypt Blade and Crescent Blade's 360° slashes and the Sentence's 180° swing spin the player model (and held weapon) so nearby players can see the attack.
- **Matthias' Curseblade passive:** a defeated mob triggers a purple burst that damages other mobs only. The blast never hurts players and never destroys blocks.

**Custom ability sounds**

Every weapon now has its own sound event and a short placeholder tone in `assets/senbonzakura/sounds/`. To replace one, drop an `.ogg` at `assets/senbonzakura/sounds/<name>.ogg` with the same name as the entry in `sounds.json` — no code changes needed. Names: `senbonzakura_swing`, `crescent_blade_slash`, `crypt_blade_blast`, `saw_blade_axe_spin`, `rite_of_commandments_thunder`, `pyrerizer_flame`, `yuki_no_ame_swing`, `razor_shark_laser`, `sentence_swing`, `wendigo_arrows`, `empire_staff_cast`, `god_rock_pulse`, `matthias_curseblade_burst`, `duckys_wand_duck`, `duckys_wand_domain`.

**Textures**

- Refreshed item textures. The Ancient Amber icon is temporarily absent; it will be restored in a future update.

---

## Potion Effects

| Effect | Description | How to obtain |
|--------|-------------|---------------|
| **Cindering** (`senbonzakura:cindering`) | Fire burns out **2x faster**, but fire damage taken is **3x** higher. Shows custom ember particles. | Random Empire Staff splash potions, or `/effect give <targets> senbonzakura:cindering <seconds>` |

---

## Admin Commands & Configuration

### Global drop rate multiplier (operator level 2+)

Sets a global multiplier for this mod’s custom item drop rates and displays the effective rates for all drops:

```mcfunction
/droprate                   # Show current multiplier and all drop rates
/droprate <multiplier>      # Set multiplier (e.g. 0.3, 1, 2, 5) and show all drop rates
/drop rate [multiplier]     # Alias
```

- Persists per world. Effective chance = `multiplier / base`.

### Per-player mod drop control (operator level 2+)

Controls this mod’s custom item drops for a specific player (e.g. when they are the killer/breaker):

```mcfunction
/nodrop <player>            # Disable Senbonzakura drops for that player (0%)
/drop <percent> <player>    # Set drop chance to 0-100% for that player (100 = normal)
```

- `percent` is applied on top of base rolls and global multiplier. Settings persist per world.

### Wendigo arrow damage multiplier (operator level 2+)

Scales the damage of the arrows fired by Wendigo's right-click ability (melee damage is unaffected):

```mcfunction
/wendigodamage              # Show current multiplier
/wendigodamage <multiplier> # Set multiplier (e.g. 0.5, 1, 2) — default 1
```

- Applies on top of bow enchantments such as Power. Persists per world.

### Ducky Domain control (operator level 2+)

```mcfunction
/duckydomain          # Show whether a domain is active and how long remains
/duckydomain end      # End the active domain immediately and return everyone
```

### Ability XP Cost (game rules)

Weapon ability activations can optionally cost XP (admin-configurable via game rules):

```mcfunction
/gamerule senbonzakuraAbilityXpCostEnabled true|false   # default: false
/gamerule senbonzakuraAbilityXpCostLevels <levels>       # default: 1
```

- Applies on **fully charged ability activation** (before cooldown/ability fires).
- If enabled and the player lacks enough levels, activation is blocked with an actionbar message.
- **Creative** players bypass the XP cost.

---

## Enchantments

| Enchantment | Max level | Effect | Applies to |
|-------------|-----------|--------|------------|
| **Increased Arrows** (`senbonzakura:increased_arrows`) | V | +32 arrows per level to Wendigo’s burst (base 64) | Wendigo (enchanting table / anvil / enchanted books) |
| **Cindering** (`senbonzakura:cindering`) | III | On a direct hit, ignites the target and applies the Cindering effect for 4s at I, +2s per level. While Cindering is active, fire burns out 2x faster but fire damage taken is 3x higher. | All Senbonzakura weapons (enchanting table / anvil / enchanted books) |

**Cindering books:** Enchanted Books with Cindering I, II, or III can be found in enchanting tables (surround with bookshelves for higher levels) or crafted shapelessly from a Book, 1/2/3 Cindering Cores and 1/2/3 Lapis Lazuli. Combine books in an anvil to reach Cindering III.

> **Ability cooldowns** for weapons are shown on the item durability bar (fills as the ability recharges).

---

## License

Copyright (c) CCC Contributors. All rights reserved. No part of this software or its source code may be reproduced, distributed, or transmitted in any form or by any means without the prior written permission of the author.
