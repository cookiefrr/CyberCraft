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

## Add models

Built-in 3D geometry lives in `ImplantModels`, keyed by the implant's item path. Each model is a list of groups; a group names a bone, a pivot, and one body part (`HEAD`, `BODY`, `RIGHT_ARM`, `LEFT_ARM`, `RIGHT_LEG`, `LEFT_LEG`), and lists cuboids in that part's local model pixels (1 px = 1/16 block). `mirror` groups receive a generated left-limb copy with `_r` renamed to `_l`. Cuboids must have increasing bounds; materials come from the shared `Material` enum and select a light, base, or dark swatch from `textures/item/cyberware_palette.png`. Worn geometry is drawn directly by `ImplantRenderer`, including pivots and bone poses.

Data generation writes only `<id>_3d.json` (the auto-fitted, uniformly scaled item model); no `_worn_*` files exist. Rotating or moving a group's pivot changes its animation behaviour without moving the rest pose. Registering or replacing a catalogue model requires running `./gradlew runData`.

Items that are not in the catalogue keep the flat `item/generated` model. Data packs that introduce a new `cybercraft` item can add a catalogue entry or supply their own resource-pack model through `model`; unknown model IDs fall back to the legacy anchor placement.

Add `book.cybercraft.desc.amethyst` to your resource pack for a concise description. Item components are retained through equipment transactions. Missing definitions or reduced position counts move affected stacks into recoverable slots when the equipment is reconciled; nothing is deleted.

## Add animations

`ImplantAnimations` maps an implant model path to an `AnimationSet(idle, active, Map<Trigger, Clip>)`. A `Clip` has an id, duration in milliseconds, loop mode (`ONCE` or `LOOP`), priority, and a list of `Track`s. A track targets one bone from `ImplantModels` and holds `Key`s of `(time, Pose, Ease)`. Poses carry translation in pixels, rotation in degrees, scale, and alpha; translations are divided by 16 when rendered. Tracks layer per bone by priority (`IDLE` 10 → `ACTIVE` 20 → `TRIGGER` 30), and trigger clips play once then release the bone back to the loop beneath.

Dispatch a one-shot with `ImplantTriggers.play(entity, Trigger.X)` from a server event; the payload carries the implant and clip id to tracking clients. Idle and active loops are derived client-side from synced cyberware and vanilla state instead of packets. Every track must name an existing bone, and clip ids must stay unique per set; the unit tests enforce both.

## Add behavior in Java

During mod setup, register an `ImplantBehavior` with `ImplantBehaviors.register(ResourceLocation, behavior)`. The interface provides `equipped`, `unequipped`, and server `tick` hooks. Use `CyberwareManager.hasInstalled(entity, definitionId)` or `CyberwareManager.get(entity).hasBehavior(behaviorId)` in normal NeoForge combat events for additional effects. Remove transient modifiers in `unequipped`; tick hooks run only while the entity is operational.

Built-in behavior IDs match implant IDs. Custom behavior IDs must be registered before server equipment validation. Data packs assign registered behaviors; they do not execute scripts.

## Mob eligibility

Extend `data/cybercraft/tags/entity_type/cyberware_users.json` with `replace: false` and eligible hostile humanoid entity types. Natural spawning, the server progression settings, compatible slots, tier limits, and the 60-point mob capacity still apply. Custom non-humanoid models require an add-on render layer.

Progression settings live in the world's `serverconfig/cybercraft-server.toml`. Equipment and cooldowns persist; chunk reloads do not reroll loadouts. No implant loot is added automatically.
