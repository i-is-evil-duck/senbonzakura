# Senbonzakura Mod

A Minecraft Forge 1.20.1 weapon mod featuring four legendary blades.

---

## ⚔️ Weapons

### Senbonzakura
- **Base Stats**: Netherite Sword (8 ATK, 2031 durability)
- **Ability**: Hold right-click 3s → Dash 12 blocks forward → Push enemies back (50% dmg) → 180° sweep (100% dmg)
- **Particles**: Cherry blossom petals + cloud trail + sweep effects
- **Crafting**: Netherite Sword + Cherry Leaves at Smithing Table

### Crescent Blade of Winds
- **Base Stats**: Diamond Sword (7 ATK, 1561 durability)
- **Passive**: **Dual-wield** (both hands) → **Damage doubles** on all attacks
- **Ability**: Hold right-click 2s → **360° slash** with strong knockback
- **Dual-wield Bonus**: Perform **TWO** consecutive slashes
- **Particles**: Wind swirls + sweep ring + explosion burst
- **Crafting**: Diamond Sword + Wind Charge at Smithing Table

### Crypt Blade
- **Base Stats**: Netherite Axe (10 ATK, 2031 durability)
- **Ability**: Hold right-click **5 seconds** → **Explosion** (no block damage)
- **During Charge**:
  - **Cannot move** or be knocked back (frozen in place)
  - **50% damage resistance** (Resistance II)
  - Dark soul particles swirling around you
- **Explosion**: Damages all enemies in 6-block radius, strong knockback, doesn't hurt you or break blocks
- **Particles**: Soul fire + smoke + ash + explosion
- **Crafting**: Netherite Axe + Sculk Catalyst at Smithing Table

### Saw Blade Axe
- **Base Stats**: Iron Sword (6 ATK, 250 durability)
- **Passive**: Mines **wood** (logs, planks) as fast as a **Diamond Axe** (speed 8.0)
- **Can harvest** wood blocks like an axe
- **No special charge ability** - straightforward utility weapon
- **Crafting**: Crafting Table — 2 Iron Ingots + 2 Sticks in axe pattern

---

## 🎮 Controls

| Weapon | Action |
|--------|--------|
| Senbonzakura | Right-click hold 3s → Release to dash |
| Crescent Blade | Right-click hold 2s → Release for 360° slash |
| Crescent Blade | Equip in **both hands** for double damage + double slash |
| Crypt Blade | Right-click hold **5s** → Release for explosion |
| Crypt Blade | You are **immobile** and **resistant** during charge |
| Saw Blade Axe | Use like normal sword/axe — fast wood mining passive |

---

## 🛠️ VS Code Setup

### Prerequisites
- Java 17 JDK
- VS Code with Java Extension Pack
- `gradle.bat` in your PATH

### Quick Start
1. **Open** the project folder in VS Code
2. **Install** recommended extensions when prompted
3. Wait for Gradle sync (check bottom status bar)
4. **Build**: `Ctrl+Shift+P` → `Tasks: Run Task` → `gradle: build`
5. **Run**: `Ctrl+Shift+P` → `Tasks: Run Task` → `gradle: runClient`

Or use Terminal:
```bash
gradle.bat build
gradle.bat runClient
```

### VS Code Tasks
| Task | Command |
|------|---------|
| Build | `gradle.bat build` |
| Run Client | `gradle.bat runClient` |
| Run Server | `gradle.bat runServer` |
| Clean | `gradle.bat clean` |

### Debug
Press `F5` → select **"Minecraft Client"** to launch with debugger.

---

## 📦 Project Structure

```
senbonzakura/
├── .vscode/                          # VS Code config
│   ├── settings.json
│   ├── launch.json
│   ├── tasks.json
│   └── extensions.json
├── build.gradle
├── settings.gradle
├── gradle.properties
├── gradlew / gradlew.bat
├── README.md
└── src/main/
    ├── java/com/senbonzakura/
    │   ├── SenbonzakuraMod.java
    │   ├── item/
    │   │   ├── SenbonzakuraItem.java    # Dash + sweep
    │   │   ├── CrescentBladeItem.java   # Dual-wield + 360° slash
    │   │   ├── CryptBladeItem.java      # Immobile charge + explosion
    │   │   └── SawBladeAxeItem.java     # Fast wood mining passive
    │   ├── network/
    │   │   └── DashPacket.java
    │   └── client/
    │       └── ClientEventHandler.java
    └── resources/
        ├── META-INF/mods.toml
        ├── pack.mcmeta
        └── assets/senbonzakura/
            ├── models/item/
            │   ├── senbonzakura.json
            │   ├── crescent_blade.json
            │   ├── crypt_blade.json
            │   └── saw_blade_axe.json
            ├── textures/item/
            │   ├── senbonzakura.png       # Cherry blossom katana
            │   ├── crescent_blade.png     # Wind crescent blade
            │   ├── crypt_blade.png        # Dark corrupted great axe
            │   └── saw_blade_axe.png      # Iron sword with saw teeth
            ├── lang/en_us.json
            └── data/senbonzakura/recipes/
                ├── senbonzakura.json
                ├── crescent_blade.json
                ├── crypt_blade.json
                └── saw_blade_axe.json
```

---

## 🔧 Troubleshooting

### "Could not find net.minecraftforge:forge:1.20.1-47.2.0"
Ensure internet connection for first build. Forge downloads from Maven.

### "JAVA_HOME not set"
Set `JAVA_HOME` to your Java 17 JDK path.

### Gradle sync fails
1. `Ctrl+Shift+P` → `Java: Clean Workspace`
2. Restart VS Code
3. Wait for sync

---

## 📄 License

MIT License
