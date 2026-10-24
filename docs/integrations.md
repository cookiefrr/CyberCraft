# Future energy and fluid integration

CyberCraft 0.3.1 does not yet add powered machinery, chargeable implants, fluid tanks, or resource consumption. RAM and tolerance keep their existing rules.

| Platform | Energy | Fluids |
|---|---|---|
| NeoForge 1.21.1 | Native Forge Energy capability | Native fluid capability |
| Fabric 1.21.1 | Team Reborn Energy 4.1.x | Fabric Transfer API, included in Fabric API |

When powered features are added, the Fabric energy API will be resolved by Gradle and bundled inside CyberCraft with its license. Players will not need the full Tech Reborn mod or a separate energy API download. There is no unused energy dependency in this release.

Expose native block and item storage interfaces so compatible external cables, chargers, tanks, and pipes can connect. Keep processing rules, recipes, saved-data versions, and balance consistent across loaders; handle API-specific units and transaction rules at their boundaries. Test partial transfers, rollback, full destinations, capacity limits, and save/reload before shipping machinery.

Provide a basic power source and charging station with the first powered gameplay, so CyberCraft remains usable without a large technology mod. Compatibility with individual third-party mods and their energy balance must be tested explicitly.

No Connector, Forgified Fabric API, or NeoForge-on-Fabric bridge is required.

References: [Team Reborn Energy](https://github.com/TechReborn/Energy), [Fabric fluid API](https://maven.fabricmc.net/docs/fabric-api-0.115.6%2B1.21.1/net/fabricmc/fabric/api/transfer/v1/fluid/FluidStorage.html).
