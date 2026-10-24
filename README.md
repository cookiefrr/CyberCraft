# CyberCraft — Fabric

Cyberpunk-inspired cyberware and netrunning for **Minecraft 1.21.1 / Fabric**.

**Version:** 0.2.2 · **Stable:** `fabric/1.21.1` · **Authors:** Ian and Xmilon

## Installation

Use Java 21, Fabric Loader, and Fabric API for Minecraft 1.21.1.
Put the matching CyberCraft and Fabric API jars in your instance's `mods` folder.
This baseline does not require GeckoLib. Updated development builds have their own dependency instructions.

## Features

- 42 implants across ten anatomical categories, with XP-based capacity and tolerance.
- Scanner, three living-target quickhacks, Sandevistan, Berserk, and double jump.
- Monowire, three generated metals, crafting stations, and an in-game manual.

This branch retains the original 0.2.2 gameplay. The inventory menu, extra slot system, block hacks, progression, and fixes are being brought to Fabric on `fabric/1.21.1-indev`; they are not present in this stable baseline.

## Controls

C opens cyberware; R toggles the scanner; G casts a hack; X/Z select hacks; V activates the OS.

## Build

```sh
./gradlew build
```

Windows users can use `gradlew.bat`. Output is under `build/libs`.

## Future integrations

Fluids will use Fabric API. Team Reborn Energy 4.1.x will be bundled when powered features arrive; no separate energy API download or full technology mod will be required. This version adds no powered machinery or fluid consumption.

[Integration plan](docs/integrations.md) · [Contributors](CONTRIBUTORS.md)

NeoForge branches: `neoforge/1.21.1` and `neoforge/1.21.1-indev`.
The original `main` and `indev` branches remain unchanged.

License: **All Rights Reserved**.
