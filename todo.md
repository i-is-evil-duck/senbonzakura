# Senbonzakura — Chaotic Mode Bosses TODO

| Feature | Entity/System | HP | Hearts | Reskin Base | Summon Item | Drop Highlights | Status | Notes |
|---------|--------------|-----|--------|-------------|-------------|-----------------|--------|-------|
| Boss framework | `BossSummonItem` | — | — | — | — | — | ✅ Done | One-time-use summon items, dimension restricted |
| Boss bars | `ServerBossEvent` | — | — | — | — | — | ✅ Done | All tier 1 bosses |
| Anti-despawn | Boss entities | — | — | — | — | — | ✅ Done | Bosses never despawn naturally |
| Advancement tree | `chaotic_mode_*` | — | — | — | — | — | ✅ Done | Root + 4 tier-1 advancements |

## Tier 1 — Pre-Iron Worm (Overworld)

| Boss | HP | Hearts | Reskin Base | Summon Item | Key Drops | Status | Notes |
|------|-----|--------|-------------|-------------|-----------|--------|-------|
| King Slime | 44 | 22 | Slime | Royal Jelly | Slime Balls, Condensed Slime, Slime Ball Slingshot, Slime Shield | ✅ Done | Splits into slimes on death |
| Zombie General | 80 | 40 | Zombie | Putrid Bait | Rotten Flesh, Iron Ingots, Forgotten Insignia, Undead Sword, Tomb Chestplate | ✅ Done | Grey skin texture needed |
| Skeleton General | 80 | 40 | Skeleton | Calcium | Bones, Iron Ingots, Forgotten Insignia, Undead Bow, Tomb Chestplate | ✅ Done | |
| Queen Spider | 90 | 45 | Spider (2x) | Suspicious Bug | String, Spider Eyes, Queen Silk, Spider Necklace, Webber, Noxious Dagger | ✅ Done | 2x hitbox; visual scale pending |
| Iron Worm | 12×40 | 12×20 | — | Corroding Lure | Blocks of Iron, Meteor Iron Shard, Drill Shaped Teeth, Iron Scale, Iron Bone Fragment, Iron Mandible Blade, Iron Spine Spear | ⬜ Todo | Multi-segment worm; needs custom AI |

## Post-Ender Dragon / Wither

| Boss | HP | Hearts | Reskin Base | Summon Item | Key Drops | Status | Notes |
|------|-----|--------|-------------|-------------|-----------|--------|-------|
| Werewolf Tribe Chief | 400 | 200 | Wolf | ⬜ Todo | ⬜ Todo | ⬜ Todo | |
| Sky Serpent | 420 | 210 | Phantom | ⬜ Todo | ⬜ Todo | ⬜ Todo | High Y / sky dimension |
| Hell Serpent | 480 | 240 | — | ⬜ Todo | ⬜ Todo | ⬜ Todo | Nether-restricted |

## Post-Warden

| Boss | HP | Hearts | Reskin Base | Summon Item | Key Drops | Status | Notes |
|------|-----|--------|-------------|-------------|-----------|--------|-------|
| Frozen Serpent | 500 | 250 | — | ⬜ Todo | ⬜ Todo | ⬜ Todo | Cold biome / dimension restricted |
| Kraken Guardian | 520 | 260 | Elder Guardian | ⬜ Todo | ⬜ Todo | ⬜ Todo | Ocean/Water dimension |
| Wendigo Warlord | 600 | 300 | Enderman | ⬜ Todo | ⬜ Todo | ⬜ Todo | |

## Post-Wendigo Warlord

| Boss | HP | Hearts | Reskin Base | Summon Item | Key Drops | Status | Notes |
|------|-----|--------|-------------|-------------|-----------|--------|-------|
| Sinful Squire | 700 | 350 | — | ⬜ Todo | ⬜ Todo | ⬜ Todo | |
| Sinful Bishop | 700 | 350 | — | ⬜ Todo | ⬜ Todo | ⬜ Todo | |
| Sinister Knight | 800 | 400 | — | ⬜ Todo | ⬜ Todo | ⬜ Todo | |

## Post-Sinister Knight — Elemental Heroes

| Boss | HP | Hearts | Reskin Base | Summon Item | Key Drops | Status | Notes |
|------|-----|--------|-------------|-------------|-----------|--------|-------|
| Felio, of Flame | 1000 | 500 | — | ⬜ Todo | Cursed Flames, Sinner's Ash, Scorched Earth, Insignia of Fire, Sentence, Pyrerizer | ⬜ Todo | |
| Ieirith, of Ice | 1000 | 500 | — | ⬜ Todo | Everfrosted Glass, Sinner's Ash, Gran Ieirith, Insignia of Ice, Sentence, Frozone | ⬜ Todo | |
| Therim, of Thunder | 1000 | 500 | — | ⬜ Todo | Roaring Spark, Sinner's Ash, Thunderbird, Insignia of Thunder, Sentence, Rite of Commandments | ⬜ Todo | |
| Wahiko, of Wind | 1000 | 500 | — | ⬜ Todo | ⬜ Todo | ⬜ Todo | Cherry Grove phase-2 variant |
| Sakurai Wahiko, Senbonzakura | 2500 | 1250 | Wahiko variant | Cherry Grove summon | ⬜ Todo | ⬜ Todo | Separate summon in Cherry Grove |
| Serrik, of Shadow | 1000 | 500 | — | ⬜ Todo | Umbral Spores, Sinner's Ash, Depth Ascender, Insignia of Shadow, Sentence, Crypt Blade | ⬜ Todo | |

## Final Tier — Chaos

| Boss | HP | Hearts | Reskin Base | Summon Item | Key Drops | Status | Notes |
|------|-----|--------|-------------|-------------|-----------|--------|-------|
| Lucius, Retainer of Ruin | 2000 | 1000 | — | ⬜ Todo | ⬜ Todo | ⬜ Todo | Post-Elemental Heroes |
| Drallius, Visions of Doom | 5000 | 2500 | — | ⬜ Todo | ⬜ Todo | ⬜ Todo | Post-Lucius |
| Drallius, Chaos: UNLEASHED | 5000 | 2500 | — | Auto-spawn after Drallius defeat | The Credits, Visions of Chaos, Finality, Chaotic Circlet/Mantle/Plating/Kicks, Music Disc | ⬜ Todo | Immediate spawn after Drallius defeat |

## Custom AI (In Progress)

| Ability | Boss | Status | Notes |
|---------|------|--------|-------|
| Boss bar + health tracking | All bosses | ✅ Done | `ServerBossEvent` integration |
| Anti-despawn | All bosses | ✅ Done | `checkDespawn()` override |
| Custom AI tick framework | All bosses | ✅ Done | `onCustomAiTick()` hook on every boss |
| Jump-slam attack | King Slime | ✅ Done | Leaps at player every 60 ticks, area slam damage |
| Undead cleave | Zombie General | ✅ Done | Sweeping arc attack every 120 ticks |
| Arrow barrage | Skeleton General | ✅ Done | 3-5 arrows at target every 80 ticks |
| Web trap | Queen Spider | ✅ Done | Places 6 cobwebs around target every 100 ticks |

## Systems Still Needed

| System | Status | Notes |
|--------|--------|-------|
| Iron Worm multi-segment entity | ⬜ Todo | 12 segments, 40HP each |
| Custom AI base class (`BossEntity`) | ✅ Done | Base class with boss bar, anti-despawn, AI tick hook |
| Boss-specific abilities | ⬜ Todo | Per-boss custom mechanics |
| Cherry Grove detection for Wahiko | ⬜ Todo | Biome check for Sakurai Wahiko phase-2 |
| Auto-spawn Drallius Chaos Unleashed | ⬜ Todo | Triggered on Drallius defeat |
| Music disc items | ⬜ Todo | "Servants of the Scourge", "Scourge of the Universe & Universal Collapse" |
| Texture: Zombie General grey skin | ⬜ Todo | Vanilla zombie reskin texture |
| Texture: Queen Spider 2x visual | ⬜ Todo | Renderer scale or custom model |
| Iron Worm summon: Corroding Lure | ⬜ Todo | 2 condensed slime, 4 forgotten insignia, 2 queen silk, 1 iron block |
