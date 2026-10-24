# Extending cyberware

Slots and implant metadata are synchronized NeoForge data-pack registries. Their IDs are namespaced resource locations, not Java enum ordinals. Restart the world/server after changing registry definitions; clients receive the registries when they connect.

## Add a slot

Create `data/example/cybercraft/slot_type/auxiliary.json`:

```json
{
  "name": "slot.example.auxiliary",
  "icon": "minecraft:amethyst_shard",
  "order": 100,
  "count": 2,
  "anchor": "body"
}
```

`count` accepts 1–32 positions. Built-in render anchors are `head`, `arms`, `legs`, and `body`. Other anchor names use the body placement until an add-on supplies a renderer. Add a resource-pack translation for the name. Extra positions automatically appear on additional equipment pages.

## Assign an item

Create `data/example/cybercraft/implant/amethyst.json`:

```json
{
  "item": "minecraft:amethyst_shard",
  "slots": ["example:auxiliary"],
  "tier": 1,
  "capacity": 4,
  "behavior": "cybercraft:smart_link",
  "model": "minecraft:amethyst_shard"
}
```

One definition may accept several slot types. Each item must have one implant definition. Tier is 1–3; capacity is a finite non-negative number. An individual implant definition cannot be installed twice. `model` identifies a registered item's baked inventory model; resource packs can replace its geometry. Built-in implants select flat icons for GUI rendering and native 3D geometry for other contexts.

Add `book.cybercraft.desc.amethyst` to your resource pack for a concise description. Item components are retained through equipment transactions. Missing definitions or reduced position counts move affected stacks into recoverable slots when the equipment is reconciled; nothing is deleted.

## Add behavior in Java

During mod setup, register an `ImplantBehavior` with `ImplantBehaviors.register(ResourceLocation, behavior)`. The interface provides `equipped`, `unequipped`, and server `tick` hooks. Use `CyberwareManager.hasInstalled(entity, definitionId)` or `CyberwareManager.get(entity).hasBehavior(behaviorId)` in normal NeoForge combat events for additional effects. Remove transient modifiers in `unequipped`; tick hooks run only while the entity is operational.

Built-in behavior IDs match implant IDs. Custom behavior IDs must be registered before server equipment validation. Data packs assign registered behaviors; they do not execute scripts.

## Mob eligibility

Extend `data/cybercraft/tags/entity_type/cyberware_users.json` with `replace: false` and eligible hostile humanoid entity types. Natural spawning, the server progression settings, compatible slots, tier limits, and the 60-point mob capacity still apply. Custom non-humanoid models require an add-on render layer.

Progression settings live in the world's `serverconfig/cybercraft-server.toml`. Equipment and cooldowns persist; chunk reloads do not reroll loadouts. No implant loot is added automatically.
