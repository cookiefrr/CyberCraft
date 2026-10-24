# CyberCraft

Cyberpunk-inspired cyberware and netrunning for **Minecraft 1.21.1 / Fabric**.

**Version:** 0.3.1 Â· **Development:** `fabric/1.21.1-indev` Â· **Authors:** Ian and Xmilon

## Requirements

- Java 21
- Fabric Loader 0.20.1 and Fabric API 0.116.17+1.21.1
- [GeckoLib 4.9.3](https://modrinth.com/mod/geckolib) for Fabric 1.21.1, on client and server
- A fresh world; upgrading old Fabric saves or converting NeoForge worlds is unsupported

Put CyberCraft, Fabric API and GeckoLib in your instance's `mods` folder. Remove older CyberCraft jars.

## Features

- 43 installable implants across 11 extensible slot types; equipment survives death.
- Cyberware tabs beside survival and creative inventories, with real slots and Back navigation.
- Dark/cyan interfaces, Shift-expanded descriptions, flat inventory icons, and 3D equipped models.
- Kiroshi optics unlock scanning; an installed cyberdeck additionally unlocks hacking.
- Three living-target hacks, Remote Access, and five-second redstone Disruption.
- Sandevistan, Berserk, double jump, Monowire, toggleable animated Mantis Blades, crafting stations, and three world-generated metals.
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

Output: `build/libs/cybercraft-fabric-1.21.1-0.3.1.jar`. Windows users can use `gradlew.bat`.
Gradle uses the provided GeckoLib jar in `src/mods` when present, otherwise the official Maven dependency. Local jars stay out of Git.

[Gameplay](docs/gameplay.md) Â· [Extend slots and implants](docs/extending.md) Â· [Development and tests](docs/development.md) Â· [Changes and fixes](docs/changelog.md) Â· [Future energy and fluids](docs/integrations.md) Â· [Contributors](CONTRIBUTORS.md)

Stable: `fabric/1.21.1`. NeoForge: `neoforge/1.21.1` and `neoforge/1.21.1-indev`.
Energy/fluid machinery is planned. Fluids will use Fabric API; Team Reborn Energy will be bundled when powered features arrive. No separate energy API download is needed.

License: **All Rights Reserved**.
