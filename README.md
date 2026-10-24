# CyberCraft

Cyberpunk-inspired cyberware and netrunning for **Minecraft 1.21.1 / NeoForge**.

**Version:** 0.3.0 · **Development:** `indev` · **Author:** Ian

## Requirements

- Java 21
- NeoForge 21.1.264 or newer within 21.1.x
- [GeckoLib 4.9.3](https://modrinth.com/mod/geckolib) for NeoForge 1.21.1, on client and server
- A fresh NeoForge world; Fabric save migration is unsupported

Put CyberCraft and GeckoLib in your instance's `mods` folder. Remove older CyberCraft jars.

## Features

- 43 installable implants across 11 extensible slot types; equipment survives death.
- Cyberware tabs beside survival and creative inventories, with real slots and Back navigation.
- Dark/cyan interfaces, Shift-expanded descriptions, flat inventory icons, and 3D equipped models.
- Kiroshi optics unlock scanning; an installed cyberdeck additionally unlocks hacking.
- Three living-target hacks, Remote Access, and five-second redstone Disruption.
- Sandevistan, Berserk, double jump, Monowire, toggleable Mantis Blades with animated 3D extensions, crafting stations, and three world-generated metals.
- Hostile mobs gradually gain cyberware from day 5, capped at 35% spawn chance and three implants.
- English UI and an illustrated in-game manual.

## Controls

| Action | Default |
|---|---|
| Cyberware / inventory tab | C / CW button |
| Scanner | R |
| Execute hack | G |
| Previous / next hack | X / Z |
| Sandevistan / Berserk | V |
| Toggle Mantis Blades | M |
| Detailed implant tooltip | Hold Shift |

Neural Link opens cyberware, installs from the other hand, or activates the OS when sneak-used.

## Build and test

```sh
./gradlew runData
./gradlew build runGameTestServer
```

Output: `build/libs/cybercraft-0.3.0.jar`. Windows users can use `gradlew.bat`.
Gradle resolves GeckoLib automatically; third-party jars do not belong in this repository.

[Gameplay](docs/gameplay.md) · [Extend slots and implants](docs/extending.md) · [Development and tests](docs/development.md) · [Changes and fixes](docs/changelog.md)

License: **All Rights Reserved**.
