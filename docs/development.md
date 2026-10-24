# Development and testing

The project uses Java 21, Fabric Loader 0.20.1, Fabric API 0.116.17+1.21.1, Loom 1.17.21, Gradle 9.5.0, and GeckoLib 4.9.3. Versions are pinned in the build and properties files. The local GeckoLib jar in `src/mods` takes precedence over the official Maven dependency; exactly one copy is used. CI verifies the Maven fallback. GeckoLib remains a separate player installation dependency.

## Reproducible resources

`./gradlew runData` runs the recipe, gameplay, asset, and English language providers. Their output is tracked in `src/generated/resources`; edit providers instead of generated JSON. Run generation again and inspect the diff before committing. Textures remain authored resources under `src/main/resources`.

A GUI-context rendering hook uses GeckoLib integration to draw the original flat icon. Held/dropped and equipped views use the native 3D geometry. GeckoLib does not drive combat or body animations.

## Automated checks

```sh
./gradlew runData
./gradlew test
./gradlew runGameTestServer
./gradlew clean build
```

- `src/test`: progression boundaries and resource completeness checks.
- `src/gametest`: independent test mod, fixtures, synthetic players, inventories, mobs, and circuitry.
- `run-gametest`: disposable server world and logs; never uses a player save.
- `build/reports/tests/test`: unit-test report.

The test mod is loaded only by the GameTest and client smoke runs and is excluded from the published jar. Tests cover installed access requirements, inventory/full-inventory transactions, named components, death copying, saved cooldowns, damage bonuses, retaliation, effect ownership, added/missing definitions, protected targets, block access, redstone output/propagation/expiry, disk persistence, and unloaded-chunk expiry.

GameTests run on a real Fabric server with GeckoLib installed. GeckoLib development refmap warnings do not indicate a mod failure.

## Automated client loading

`./gradlew runClientSmoke` uses `run-client-test`, loads the real client, checks all 43 native models, flat icons and GUI renderer entries, renders every inventory icon, and exits automatically. It does not inspect visual placement.

## Manual client review

`./gradlew runClient` starts the development client. Use a fresh world. Verify the CW tab in survival and creative, Back navigation and selected creative category, GUI scales, Shift tooltips, ordinary clicks/dragging, scanner targets, and first-/third-person implant placement. Check armor and both player arm widths. The user performs visual acceptance; automated checks do not establish visual quality.

`./gradlew runServer` starts a normal dedicated server. Keep world saves, server configuration, logs, third-party mods, IDE metadata, and local tools out of commits.

## Branches

Publish NeoForge development commits to `neoforge/1.21.1-indev` and Fabric development commits to `fabric/1.21.1-indev`. Their corresponding stable branches are `neoforge/1.21.1` and `fabric/1.21.1`. Promote gameplay changes only after the user's visual acceptance. The original `main` and `indev` branches remain historical baselines. Do not force-push or publish unverified changes.
