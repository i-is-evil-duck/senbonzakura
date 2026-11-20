# Senbonzakura — Chaotic Mode Bosses TODO

| Feature | Entity/System | HP | Hearts | Reskin Base | Summon Item | Drop Highlights | Status | Notes |
|---------|--------------|-----|--------|-------------|-------------|-----------------|--------|-------|
| Boss framework | `BossSummonItem` + `ChaoticBossEntity` | — | — | Iron Golem | — | — | ✅ Done | Base classes + boss bars + anti-despawn + AI tick hook |
| Boss bars | `ServerBossEvent` | — | — | — | — | — | ✅ Done | All bosses |
| Anti-despawn | Boss entities | — | — | — | — | — | ✅ Done | `checkDespawn()` override |
| Advancement tree | `chaotic_mode_*` | — | — | — | — | — | ✅ Done | Root + 22 tier advancements |

## Tier 1 — Pre-Iron Worm (Overworld)

| Boss | HP | Hearts | Reskin Base | Summon Item | Key Drops | Status | Notes |
|------|-----|--------|-------------|-------------|-----------|--------|-------|
| King Slime | 44 | 22 | Slime | Royal Jelly | Slime Balls, Condensed Slime, Slime Ball Slingshot, Slime Shield | ✅ Done | Splits into slimes on death; jump slam AI |
| Zombie General | 80 | 40 | Zombie | Putrid Bait | Rotten Flesh, Iron Ingots, Forgotten Insignia (50%), Undead Sword (25%), Tomb Chestplate (25%) | ✅ Done | Grey skin texture **DONE**; sweeping cleave AI |
| Skeleton General | 80 | 40 | Skeleton | Calcium | Bones, Iron Ingots, Forgotten Insignia (50%), Undead Bow (25%), Tomb Chestplate (25%) | ✅ Done | Arrow barrage AI |
| Queen Spider | 90 | 45 | Spider (2x) | Suspicious Bug | String, Spider Eyes, Queen Silk (50%), Spider Necklace (25%), Webber (25%), Noxious Dagger (10%) | ✅ Done | 2x hitbox; texture **DONE**; web trap AI |
| Iron Worm | 480 | 240 | Segmented worm | Corroding Lure | Iron Blocks, Meteor Iron Shard, Drill Shaped Teeth, Iron Scale, Iron Bone Fragment, Iron Mandible Blade (1%), Iron Spine Spear (1%) | ✅ Done | Real multi-part worm; rear-up strike; boss bar |

## Post-Ender Dragon / Wither

| Boss | HP | Hearts | Reskin Base | Summon Item | Key Drops | Status | Notes |
|------|-----|--------|-------------|-------------|-----------|--------|-------|
| Werewolf Tribe Chief | 400 | 200 | Iron Golem | Lunar Howl Totem | Rotten Flesh, Werewolf Fang, Moon Claw (25%), Lunar Pelt (25%), Gold Ingots | ✅ Done | Wolf summon/howl AI |
| Sky Serpent | 420 | 210 | Segmented worm | Storm Caller Charm | Phantom Membrane, Serpent Fang, Sky Scale (25%), Cloud Essence (25%), Elytra (5%) | ✅ Done | Flying segmented worm; phantom circle/swoop AI |
| Hell Serpent | 480 | 240 | Segmented worm | Infernal Totem | Magma Cream, Hell Scale (25%), Infernal Core (16%), Netherite Scrap (5%) | ✅ Done | Segmented worm; rear-up strike; Nether biome spawn |

## Regular Worms (non-boss)

| Mob | HP | Spawn | Spawn Egg | Key Drops | Status | Notes |
|-----|-----|-------|-----------|-----------|--------|-------|
| Earthworm | 14 | Spawn egg only | Earthworm Spawn Egg | String, Dirt, Ancient Amber (2%) | ✅ Done | 4-segment ground worm |
| Ocean Serpent | 30 | All ocean biomes | Ocean Serpent Spawn Egg | Prismarine Shard, Ink Sac, Nautilus Shell (10%) | ✅ Done | 7-segment water worm; phantom-style circling |

## Post-Warden

| Boss | HP | Hearts | Reskin Base | Summon Item | Key Drops | Status | Notes |
|------|-----|--------|-------------|-------------|-----------|--------|-------|
| Frozen Serpent | 500 | 250 | Iron Golem | Frost Totem | Packed Ice, Frost Fang (25%), Everfrost Shard (16%), Diamond (5%) | ✅ Done | Arrow shoot AI |
| Kraken Guardian | 520 | 260 | Iron Golem | Deep Totem | Prismarine Shard, Kraken Tentacle (25%), Deep Prism (16%), Nautilus Shell (5%) | ✅ Done | Web trap AI |
| Wendigo Warlord | 600 | 300 | Iron Golem | End Totem | Bone, Wendigo Claw (25%), Shadow Heart (16%), Totem of Undying (5%) | ✅ Done | Arrow shoot AI |

## Post-Wendigo Warlord

| Boss | HP | Hearts | Reskin Base | Summon Item | Key Drops | Status | Notes |
|------|-----|--------|-------------|-------------|-----------|--------|-------|
| Sinful Squire | 700 | 350 | Iron Golem | Sinful Totem | Iron Sword, Sinful Blade (25%), Penitent Shield (16%) | ✅ Done | Area damage AI |
| Sinful Bishop | 700 | 350 | Iron Golem | Bishop Totem | Book, Bishop Staff (25%), Holy Seal (16%) | ✅ Done | Arrow shoot AI |
| Sinister Knight | 800 | 400 | Iron Golem | Knight Totem | Iron Ingot, Knight Lance (25%), Dark Plating (16%) | ✅ Done | Leap slam AI |

## Post-Sinister Knight — Elemental Heroes

| Boss | HP | Hearts | Reskin Base | Summon Item | Key Drops | Status | Notes |
|------|-----|--------|-------------|-------------|-----------|--------|-------|
| Felio, of Flame | 1000 | 500 | Iron Golem | Flame Totem | Cursed Flames, Sinner's Ash (10%), Scorched Earth (7.5%), Insignia of Fire (7.5%), Sentence (5%), Pyrerizer (1%) | ✅ Done | Area damage AI |
| Ieirith, of Ice | 1000 | 500 | Iron Golem | Ice Totem | Everfrosted Glass, Sinner's Ash (10%), Gran Ieirith (7.5%), Insignia of Ice (7.5%), Sentence (5%), Frozone (1%) | ✅ Done | Arrow shoot AI |
| Therim, of Thunder | 1000 | 500 | Iron Golem | Thunder Totem | Roaring Spark, Sinner's Ash (10%), Thunderbird (7.5%), Insignia of Thunder (7.5%), Sentence (5%), Rite of Commandments (1%) | ✅ Done | Double arrow AI |
| Wahiko, of Wind | 1000 | 500 | Iron Golem | Wind Totem | Feather, Wind Essence (25%), Cherry Petal (16%) | ✅ Done | Leap slam AI |
| Sakurai Wahiko, Senbonzakura | 2500 | 1250 | Iron Golem | Cherry Totem | Sakurai Blade, Blossom Core (25%) | ✅ Done | Cherry Grove variant; leap slam AI |
| Serrik, of Shadow | 1000 | 500 | Iron Golem | Shadow Totem | Umbral Spores, Sinner's Ash (10%), Depth Ascender (7.5%), Insignia of Shadow (7.5%), Sentence (5%), Crypt Blade (1%) | ✅ Done | Web trap AI |

## Final Tier — Chaos

| Boss | HP | Hearts | Reskin Base | Summon Item | Key Drops | Status | Notes |
|------|-----|--------|-------------|-------------|-----------|--------|-------|
| Lucius, Retainer of Ruin | 2000 | 1000 | Iron Golem | Ruin Totem | Ruin Shard, Void Essence (16%) | ✅ Done | Area damage AI |
| Drallius, Visions of Doom | 5000 | 2500 | Iron Golem | Visions Totem | Visions of Chaos, Sinner's Ash (10%) | ✅ Done | Minion spawn + area damage |
| Drallius, Chaos: UNLEASHED | 5000 | 2500 | Iron Golem | Chaos Totem | The Credits, Visions of Chaos, Finality, Chaotic Circlet/Mantle/Plating/Kicks, Music Disc (Universe) | ✅ Done | Minion spawn + area damage |

## Custom AI (Complete)

| Ability | Boss | Status |
|---------|------|--------|
| Boss bar + health tracking | All bosses | ✅ Done |
| Anti-despawn | All bosses | ✅ Done |
| Custom AI tick framework | All bosses | ✅ Done |
| Jump slam | King Slime, Iron Worm, Sinister Knight, Wahiko, Sakurai Wahiko | ✅ Done |
| Sweeping cleave | Zombie General | ✅ Done |
| Arrow barrage | Skeleton General, Frozen Serpent, Wendigo Warlord, Ieirith, Therim | ✅ Done |
| Web trap | Queen Spider, Kraken Guardian, Serrik | ✅ Done |
| Howl + summon minions | Werewolf Tribe Chief | ✅ Done |
| Dive bomb | Sky Serpent | ✅ Done |
| Area damage | Iron Worm, Hell Serpent, Frozen Serpent, Sinful Squire, Felio, Lucius, Drallius x2 | ✅ Done |
| Minion spawn | Drallius Visions of Doom, Drallius Chaos Unleashed | ✅ Done |

## Systems Complete

| System | Status | Notes |
|--------|--------|-------|
| All 23 boss entities | ✅ Done | Iron Golem placeholders except the 3 segmented worm bosses |
| All 23 summon items | ✅ Done | One-time use, dimension restricted |
| All boss loot tables | ✅ Done | With specified drop rates |
| All summon recipes | ✅ Done | Crafting shapeless |
| All advancements | ✅ Done | 22-tier progression tree |
| Music disc items | ✅ Done | 2 discs registered |
| Textures: Zombie General | ✅ Done | Grey skin |
| Textures: Queen Spider | ✅ Done | 2x scale |
| Textures: All other bosses | ⬜ Todo | See assets.md |
| Textures: All items | ⬜ Todo | See assets.md |
| Music disc OGG files | ⬜ Todo | Need audio assets |
| Item models | ✅ Done | 88 model JSONs created |
| Spawn eggs for all bosses | ✅ Done | 25 spawn eggs registered (incl. Earthworm + Ocean Serpent) |
| Chaos Mode creative tab | ✅ Done | New tab with all boss spawn eggs |
| Release as v2.0.6 | ✅ Done | https://github.com/i-is-evil-duck/senbonzakura/releases/tag/v2.0.6 |
| Release as v2.1.0 | ⬜ Todo | Segmented worms integration |
