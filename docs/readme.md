# Senbonzakura Mod — Technical Reference

## Package Structure

```
com.j3ly.senbonzakura
├── SenbonzakuraMod.java         # Main mod class, registrations, loot, recipes
├── item/
│   ├── SenbonzakuraItem.java    # Dash + sweep ability
│   ├── CrescentBladeItem.java   # 360° slash, dual-wield passive
│   ├── CryptBladeItem.java      # Charge explosion, immobile + resistant
│   ├── SawBladeAxeItem.java     # Fast wood mining passive
│   ├── RiteOfCommandmentsItem.java  # Absorption + Regeneration support
│   ├── PyrerizerItem.java       # Fire projectile ability
│   ├── YukiNoAmeItem.java       # Snowstorm slow aura ability
│   └── TheFinaleItem.java       # Massive projectile + explosion ability
├── network/
│   └── DashPacket.java          # Network channel for dash sync
└── client/
    └── ClientEventHandler.java  # Client-side particle effects
```

## Crafting Items (Items.ItemRegistration)

| Registry Name | Item |
|---|---|
| ARCHAIC_CROWN | Archaic Crown |
| INKY_PEARL | Inky Pearl |
| EXALTED_BLOSSOM | Exalted Blossom |
| ANCIENT_AMBER | Ancient Amber |
| DEEPWOKEN_RELIC | Deepwoken Relic |
| EXCELLENT_PRINCESS_DIAMOND | Excellent Princess Diamond |
| SACRED_THREADS | Sacred Threads |
| CRUDE_JEWEL_STEEL | Crude Jewel Steel |
| TAMAHAGANE | Tamahagane |
| SUPREME_ALLOY | Supreme Alloy |
| CINDERING_CORE | Cindering Core |
| ULTRA_CONDUCTIVE_MATERIAL | Ultra Conductive Material |
| SKYSTONE | Skystone |
| DARKENED_RUBBLE | Darkened Rubble |
| SINNERS_ASH | Sinners Ash |
| FROZEN_CORE | Frozen Core |

## Weapons — Ability Details

### Senbonzakura
- `CHARGE_TIME`: 10 ticks (0.5s)
- `COOLDOWN_TIME`: 100 ticks (5s)
- Teleport 12 blocks in look direction, no collision checks
- Damage: 50% push damage + 100% sweep damage on enemies within 3 blocks
- Passive: 4 consecutive hits trigger bonus damage

### Crescent Blade of Winds
- `CHARGE_TIME`: 10 ticks (0.5s)
- `COOLDOWN_TIME`: 140 ticks (7s)
- 360-degree slash — damages all enemies within 4-block radius
- Dual-wield: when offhand also has this item, deals 2x damage
- Passive: 4 consecutive hits trigger bonus damage

### Crypt Blade
- `CHARGE_TIME`: 300 ticks (15s)
- `COOLDOWN_TIME`: 300 ticks (15s)
- Immobile (Slowness 255) + Resistance II during charge
- Explosion: 6-block radius, no block damage, targets mobs only
- Passive: 5 consecutive hits trigger bonus damage

### Rite of Commandments
- `CHARGE_TIME`: 10 ticks (0.5s)
- `COOLDOWN_TIME`: 240 ticks (12s)
- Grants Absorption IV (8s) + Regeneration II (8s)
- Passive: 4 consecutive hits trigger bonus damage

### Pyrerizer
- `CHARGE_TIME`: 10 ticks (0.5s)
- `COOLDOWN_TIME`: 140 ticks (7s)
- Fires projectile that deals 6 damage + 3s fire
- Projectile uses SmallFireball entity
- Passive: 4 consecutive hits trigger bonus damage

### Yuki no Ame
- `CHARGE_TIME`: 10 ticks (0.5s)
- `COOLDOWN_TIME`: 100 ticks (5s)
- Snowstorm: applies Slowness III to all enemies within 8 blocks for 5s
- Passive: 4 consecutive hits trigger bonus damage

### The Finale
- `CHARGE_TIME`: 60 ticks (3s)
- `COOLDOWN_TIME`: 600 ticks (30s)
- Fires large projectile — 10 damage + explosion on impact
- Passive: 5 consecutive hits trigger bonus damage

### Saw Blade Axe
- No charge ability
- Extends AxeItem — mines wood at Diamond Axe speed

## Mob Drops

See [LOOT_TABLES.md](./LOOT_TABLES.md) (if exists) or `src/main/java/com/j3ly/senbonzakura/SenbonzakuraMod.java` loot table modifications.

## Particle Effects (ClientEventHandler)

Particles spawn around the player while holding each weapon:

| Weapon | Particle |
|--------|----------|
| Senbonzakura | Sakura (cherry blossom) |
| Crescent Blade of Winds | End rod (green) |
| Crypt Blade | Smoke + large smoke (black) |
| Rite of Commandments | End rod (white) |
| Pyrerizer | Flame + smoke (grey) |
| Yuki no Ame | Snowflake |
| The Finale | Flame (red/lava) |

## Crafting Recipes

Recipes are in `src/main/resources/data/senbonzakura/recipes/`.

### Intermediates
- **Excellent Princess Diamond**: Inky Pearl + Diamond (shapeless)
- **Sacred Threads**: Exalted Blossom + String (shapeless)
- **Crude Jewel Steel**: Ancient Amber + Iron Ingot (shapeless)
- **Tamahagane**: Crude Jewel Steel + Deepwoken Relic (shapeless)
- **Supreme Alloy**: Excellent Princess Diamond + Tamahagane (shapeless)
- **Darkened Rubble**: Skystone + Cobblestone (shapeless)
- **Sinners Ash**: Darkened Rubble + Blaze Powder (shapeless)
- **Frozen Core**: Ice + Ultra Conductive Material (shapeless)
- **Ultra Conductive Material**: Copper Ingot + Redstone (shapeless)

### Weapons (Smithing Table)
All weapons use the Smithing Table recipe format (template + base + addition):
- **Senbonzakura**: netherite_sword + cherry_leaves
- **Crescent Blade**: diamond_sword + wind_charge
- **Crypt Blade**: netherite_axe + sculk_catalyst
- **Saw Blade Axe**: Crafting Table — 2 iron ingots, 2 sticks (axe pattern)
- **Rite of Commandments**: netherite_sword + sacred_threads
- **Pyrerizer**: netherite_sword + cindering_core
- **Yuki no Ame**: diamond_sword + frozen_core
- **The Finale**: netherite_sword + supreme_alloy

## Required Textures

All textures are currently missing (placeholder pink/black squares). To add them, place 16×16 or 32×32 PNG files in `src/main/resources/assets/senbonzakura/textures/item/`:

- `senbonzakura.png`
- `crescent_blade.png`
- `crypt_blade.png`
- `saw_blade_axe.png`
- `rite_of_commandments.png`
- `pyrerizer.png`
- `yuki_no_ame.png`
- `the_finale.png`
- `archaic_crown.png`
- `inky_pearl.png`
- `exalted_blossom.png`
- `ancient_amber.png`
- `deepwoken_relic.png`
- `excellent_princess_diamond.png`
- `sacred_threads.png`
- `crude_jewel_steel.png`
- `tamahagane.png`
- `supreme_alloy.png`
- `cindering_core.png`
- `ultra_conductive_material.png`
- `skystone.png`
- `darkened_rubble.png`
- `sinners_ash.png`
- `frozen_core.png`
