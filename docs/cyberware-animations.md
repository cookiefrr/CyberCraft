# Cyberware models and low-poly animations — 0.4.0

Status: **proposed** (awaiting approval before implementation).
Target: Minecraft 1.21.1, NeoForge 21.1.264, Java 21.
Scope: revision of the 43 implant models (plus Monowire), a client-side animation runtime, and the gameplay triggers that play the animations.

## 1. Goals

- Every implant has a cuboid-only model that reads as its Cyberpunk 2077 reference at Minecraft scale.
- Every implant has at least one animation: an ambient idle, an active loop, a one-shot trigger, or a combination.
- Animations are **low-poly and pixel-art**: rigid cuboids, 90° angles, stepped keyframes, movement quantised to the 1/16-block grid where possible.
- Models stay rigidly attached to the vanilla `Head`, `Body`, `RightArm`, `LeftArm`, `RightLeg`, `LeftLeg` parts. No free-floating parts, no clipping through the skin beyond the intended 1 px protrusion.
- Visual-only. Except for two small, disclosed additions (Self-ICE effect block and the existing Optical Camo sneak state), gameplay numbers, recipes and slots do not change.

## 2. Non-goals

- No guns, ammunition, magazines, reload or ricochet systems. Smart Link, Ballistic Coprocessor, Microgenerator and Projectile Launch System reuse existing melee/projectile/scan events.
- No enemy netrunners. Self-ICE triggers on a hostile mob effect instead of an enemy hack.
- No separate Kerenzikov slow-motion. Kerenzikov and Reflex Tuner animate while Sandevistan slows time and during low health.
- No GeckoLib migration. The existing dependency stays available, but the animation format is data-driven Java compatible with the current renderer.
- Item/GUI models remain static rest-pose renders; only worn (third-person and first-person) models animate.

## 3. References

The brief listed only the domains `fandom.com` and `fextralife.com`. URLs below were returned by web search; Fandom blocks automated page fetching (HTTP 403), so visual calls marked **(?)** are inferred from inventory-icon filenames, item descriptions and in-game knowledge and must be checked against the linked page image during modelling.

Reference research: all ten categories complete. Fandom blocks page fetching, so icon presence was verified through its MediaWiki API and the visual calls marked **(?)** are inferred from icon filenames, item descriptions and Fextralife thumbnails. Where the brief deliberately departs from canon (external spine Sandevistan, visible forehead chips, back-mounted Berserk and Chrome Compressor), the brief wins and the deviation is noted in the implant entry.

Colour references use the existing 39-material palette atlas (`gen-cyberware-palette.js`), not new textures. The reference's job is shape, proportion and placement; the palette's job is consistency across 44 items.

## 4. Technical constraints (inherited and extended)

- One unit = 1/16 block. Entity model space is y-down.
- `Head` pivot at y −8..0; `Body` 0..12; arm pivot x ±5, y 2; leg pivot x ±1.9, y 12.
- Every box is axis-aligned in rest pose. Animation rotates whole bones; individual boxes never deform.
- Palettes: 16×16 swatch cells in a 256×256 atlas, UV `[col+.04,row+.04,col+.96,row+.96]`; up = LIGHT tone, down = DARK, sides = BASE.
- Only `GLASS` renders translucent. New translucent effects (camo shell, Self-ICE barrier, Kiroshi cone, revive flash) add two swatches handled by the translucent pass: `HOLO` (cyan) and `FLASH` (white). Both are added to the 39-material palette.
- Emissive is **shader-only**: no `RenderType.eyes`, no fullbright. Neon swatches stay saturated in the base palette; a companion emissive atlas is generated for shader packs. Without shaders the neon is bright but not glowing. This is the agreed trade-off.

## 5. Model schema v2 (bones)

The current schema has one group per body part and no pivots. 0.4.0 keeps every box and every 0.3.3 coordinate, and adds a bone layer:

```java
record Group(Part part, boolean mirror, String bone, Vec3 pivot, List<Box> boxes)
record Model(List<Group> groups)
```

- `bone` is unique inside one implant (`root`, `iris_l`, `vent_r`, `module_1`, ...).
- `pivot` is part-local, in the same space as the boxes, and is the point the bone rotates/scales around.
- `mirror` keeps the existing meaning: the group is drawn on the right side and mirrored on the left. A mirrored bone expands to `<bone>_r` and `<bone>_l`; an animation track targeting `<bone>` applies symmetrically to both, a track targeting `<bone>_r` or `<bone>_l` applies to one side.
- `root` is the default bone with pivot `(0,0,0)`; static boxes stay in `root`.
- Boxes never move between parts. The anchor part remains the single source of placement.

Rest pose = 0.3.3 geometry. The item `_3d` models are generated from the rest pose, so the inventory look does not change.

## 6. Rendering

### 6.1 Worn renderer

Replace the JSON `_worn_*` models with a direct hierarchical box renderer:

1. `CyberwareLayer` resolves the installed implant and its `Model`.
2. For each group: apply `part.translateAndRotate(pose)`, then the bone transform around the pivot, then draw the group's boxes with palette UVs.
3. Solid boxes go to `RenderType.entityCutoutNoCull(cyberware_palette)`; `GLASS`/`HOLO` boxes go to `RenderType.entityTranslucent(cyberware_palette)` with per-vertex alpha.
4. First-person uses the same renderer through the existing `ClientEquipment` arm hook, so both views share one code path and one animation clock.

Bone transform order: translate to pivot, rotate Y then X then Z (degrees), scale, translate back, then apply the bone's animated translation offset.

The Mantis Blades keep their dedicated `MantisBladeRenderer` (32×32 shaped texture, custom blade silhouette). It gains the forearm plate lift and the alternating X-slash pose, but does not move to the palette atlas.

### 6.2 Item models

Unchanged: `_3d` auto-fit first group, flat icon, `icons/<id>`, `separate_transforms`. The `_worn_*` JSON generation and standalone model registration are removed; `ResourceTest` is updated accordingly.

### 6.3 Emissive

Generate `assets/cybercraft/textures/item/cyberware_palette_e.png` with the same 256×256 layout: black everywhere except the neon/emissive swatches (`CYAN`, `NEON_ORANGE`, `NEON_RED`, `NEON_YELLOW`, `ELECTRIC_BLUE`, `TOXIC`, `ACID`, `VIOLET`, `PURPLE`, `AMBER`, `CIRCUIT`, `MEDICAL`, `EMERALD`) at full brightness. Shader packs that support emissive textures can use it for item models; whether it applies to the custom worn render type depends on the pack and is documented as shader-dependent. No vanilla fullbright fallback.

### 6.4 Texture-level effects without per-implant textures

The shared palette rules out `.mcmeta` animations. Pixel effects are built from geometry and swatch swaps:

- blinking LEDs / circuit alternation: two overlay boxes toggled by a stepped track;
- scrolling grids / data transfer: a column of small emissive cubes shown bottom-to-top in sequence;
- liquid drain: liquid sub-boxes hidden from the top down;
- fan rotation: four stepped rotations of a cross-shaped box cluster;
- iris shrink, heart beat, breathing, piston compression: bone scale;
- camo fade: per-bone alpha on `HOLO`.

This keeps one texture, one material set and full control on the client.

## 7. Animation runtime

### 7.1 Format

```java
record Pose(float tx, float ty, float tz,
            float rx, float ry, float rz,
            float sx, float sy, float sz)
record Key(float time, Pose pose, Ease ease)
record Track(String bone, List<Key> keys)
record Clip(String id, float durationMs, Loop loop, int priority, List<Track> tracks)
enum Ease { STEP, LINEAR, SMOOTH, EASE_OUT, EASE_IN }
```

- Time in milliseconds; evaluation uses game time plus partial tick.
- `STEP` is the default for mechanical motion and the pixel-art feel; `SMOOTH`/`EASE_OUT` for organic motion (breathing, heart, camo fade).
- Key values are deltas from rest pose. Rotations in degrees, translations in model units (1/16 block), scales as multipliers.
- Clip priority: `TRIGGER (30) > ACTIVE (20) > IDLE (10)`. Resolution is per bone: the highest-priority clip owning a bone wins; other bones keep animating from lower clips. This lets a revive flash play over a heartbeat.

### 7.2 Catalogue

`ImplantAnimations.java` holds, per implant:

- `idle`: looping ambient clip (may be empty);
- `active`: looping clip while a state is active (Sandevistan, Berserk, camo, Mantis deployed);
- `triggers`: map of trigger id → one-shot clip (`kill`, `damage`, `hack`, `cooldown`, `jump`, `land`, `attack`, `projectile`, `revive`, `block`, ...).

A `Trigger` enum centralises ids. A test asserts every implant has at least one clip and every track targets an existing bone.

### 7.3 Client runtime

`ImplantAnimationClient` keeps a map `entityId → implant → active clips`. It:

- plays a one-shot clip when `ImplantAnimationPayload` arrives;
- derives active loops from synced cyberware (`CyberwareSyncPayload`) and existing state (`MantisBladeClientState`, Sandevistan/Berserk client state);
- derives ambient drivers from vanilla entity state: walk phase, attack swing, sneaking, sprinting, on-ground, airborne ticks, health ratio, hurt time, scan state;
- advances clips per frame with partial tick, removes finished one-shots, clears on logout/dimension change/entity removal;
- caps distance at 64 blocks and updates idle clips at 10 Hz (stepped look, low CPU).

No per-tick packets. Discrete events only.

### 7.4 Server triggers

`ImplantTriggers` centralises event → clip dispatch and sends `ImplantAnimationPayload(entityId, implant, clip)` to players tracking the entity and to the entity itself. Existing sites are reused:

| Event | Existing hook | Implants |
|---|---|---|
| kill | `CyberCraft.death` → `CyberwareManager.onKill` | Axolotl, Heal-on-Kill, Syn-Lungs, Blood Pump |
| damage taken | `CyberCraft.damage` → `CyberwareManager.onDamaged` | Subdermal Armor, Shock-n-Awe, Pain Editor, Biomonitor, Adrenaline Converter, Second Heart (revive) |
| melee hit | `ImplantEffects.onMeleeHit` | Gorilla Arms, Microgenerator, Projectile Launcher, Mantis Blades |
| quickhack cast | `NetrunnerManager.cast` | COX-2, Behavioral Imprint, Smart Link |
| RAM/cooldown tick | `NetrunnerManager` tick | Newton Module, Ex-Disk, Quantum Tuner |
| double jump | `DoubleJumpHandler` | Maneuvering System, Reinforced Tendons, Spring Joints |
| landing | player tick on ground transition | Fortified Ankles, Spring Joints, Dense Marrow |
| projectile impact | `ProjectileImpactEvent` | Ballistic Coprocessor |
| harmful effect applied | new Self-ICE check | Self-ICE |
| scan / low health / sneak-still / sprint | client-side only | Kiroshi, Smart Link, Biomonitor, Blood Pump, Optical Camo, Jenkins Tendons |

## 8. Minimal gameplay additions

1. **Optical Camo** keeps its current trigger: sneaking while almost still grants invisibility. The animation fades the shell top-down on activation and reverses when it ends. No new keybind.
2. **Self-ICE** gains one disclosed behaviour: when a hostile mob effect would be applied and the implant is operational, cancel it and start a 60 s cooldown, then play the barrier clip. This replaces the impossible "blocks an enemy hack" with the closest existing threat (mob effects). Armor bonus is unchanged.
3. All other requested triggers are mapped to existing events (see the per-implant table). Where the game has no equivalent, the animation is visual-only and the limitation is listed in section 12.

## 9. Performance and compatibility

- Animation state is client-only and O(installed implants) per rendered entity.
- Boxes are submitted directly; no per-frame model JSON lookups.
- One atlas, two render types, no new texture per implant.
- Multiplayer parity: other players' implants animate from synced cyberware plus trigger packets.
- Armor: worn models render after the armor layer as today. Camo uses translucent alpha and does not hide armor.
- First person: only arm/hand implants are visible; head/body/leg animations remain third-person. Documented, not a bug.

## 10. Testing

- Unit: clip evaluation endpoints, easing, looping, priority resolution per bone, mirror expansion, trigger catalogue completeness, bone/track consistency, palette emissive atlas dimensions.
- Data: 44 `_3d` item models, 44 icons, no `_worn_*` models, palette texture + emissive companion, 43 definitions + Monowire, recipes unchanged.
- Gametest: payload encode/decode, trigger dispatch without crash, Self-ICE effect cancellation and cooldown, install/uninstall leaves no animation state.
- Build: `gradlew build --offline` and `runGameTestServer`.
- Visual: in-game check of every animated implant in third and first person, without shaders and with a shader pack if available.

## 11. Phasing

1. Schema v2 + direct worn renderer at rest pose; parity with 0.3.3; tests updated.
2. Idle/ambient clips for all implants (texture-like loops included).
3. Active loops and one-shot clips + payload + client runtime.
4. Gameplay triggers and the two minimal additions (camo state animation, Self-ICE block).
5. Docs, version 0.4.0, build, gametest, commit/push `indev`.

## 12. Limitations to accept

- Emissive depends on the shader pack; without shaders neon is bright but not glowing.
- No enemy hacking: Self-ICE uses hostile mob effects.
- No ranged weapon system: Smart Link reticles appear during scan; Ballistic Coprocessor uses projectile impacts; Microgenerator uses a 3-hit melee charge; Projectile Launcher reuses its existing melee-triggered explosion and adds rocket visuals only.
- No Kerenzikov time-slow of its own: it animates during Sandevistan and low health.
- Monowire is still a weapon item, not an installable implant: its whip is animated in hand on attack; the worn model is not used.
- Item/GUI models do not animate.
- First-person view shows arm/hand animations only.

---

## 13. Per-implant specifications

Legend: **Rest** = 0.3.3 coordinates (entity space, right side for mirrored arm/leg groups); **Bones** = new pivots; **Clips** = idle / active / trigger with duration and motion; **Trigger** = event source. All clips are in milliseconds; `STEP` unless stated.

### 13.1 Operating system

Canon note: in CP2077 the OS is a shard seated in a neuroport. The brief instead specifies external hardware (nape deck, spine Sandevistan, back modules); the brief is followed and this is the main deliberate deviation.

#### Cyberdeck (`cyberdeck`)
- **Ref**: [Cyberdeck](https://cyberpunk.fandom.com/wiki/Cyberdeck) (no canonical art; 2020 decks are flat cartridges with edge contacts).
- **Rest**: Body, upper back, x −4.3..4.3, y 5.0..10.8, z 1.9..3.4.
- **0.4.0 move**: to the nape on `Head`: x −3.2..3.2, y −6.8..−2.2, z 4.1..5.1, protruding 1 px past the hair layer. Flat cartridge silhouette with edge contacts, industrial grey shell, green circuit face, four cyan LEDs.
- **Bones**: `housing`, `led_a` (two LEDs), `led_b` (two LEDs), `circuit_a`, `circuit_b`, `slot`.
- **Clips**: *idle* 2.4 s loop — `led_a` visible 0–600 ms, `led_b` 1200–1800 ms; circuit overlays alternate every 1.2 s. All `STEP`, reading as texture animation.
- **Trigger**: while installed (ambient).
- **Emissive**: cyan LEDs, green circuit swatch.

#### Sandevistan (`sandevistan`)
- **Ref**: [Sandevistan](https://cyberpunk.fandom.com/wiki/Sandevistan) (canonical art is a neuroport socket; the external spine follows the brief).
- **Rest**: Body, spine, x −3.0..3.0, y 1.0..12.0, z 1.9..3.5.
- **0.4.0 addition**: a `neck` connector group on `Head` (x −1.4..1.4, y −2.4..−0.6, z 3.9..4.7) linking the deck area to the spine; five spine modules keep their 0.3.3 boxes.
- **Bones**: `neck`, `spine_1..spine_5` (pivot at each module's upper edge), `module_1..module_5`.
- **Clips**: *idle* — modules seated, bronze glint every 3 s. *active* 900 ms loop — each module lifts 1 px and tilts 8° outwards in sequence from top to bottom, 120 ms apart, then reseats; orange emissive strip brightens while lifted. *trail* — client spawns `DustParticleOptions` orange pixels behind the player at 10 Hz while active, with occasional `END_ROD` for the strobe.
- **Trigger**: `SandevistanManager` active state (existing payload).
- **Emissive**: neon orange modules, gold edges.

#### Berserk (`berserk`)
- **Ref**: [Berserk](https://cyberpunk.fandom.com/wiki/Berserk) (variant icons only; back-module shape follows the brief).
- **Rest**: Body, upper back, x −4.5..4.5, y 3.8..12.4, z 2.0..4.6.
- **Bones**: `core`, `housing`, `vent_l`, `vent_r` (pivot at the inner edge), `vent_flap_l/r`.
- **Clips**: *idle* — dark iron, red core pulse 3 s. *activate* 250 ms — vents rotate 90° outwards, core scale 1.15, yellow/orange warning overlays on. *active* 1.2 s loop — core pulse, vents stay open, red smoke particles (`DUST` red + `LARGE_SMOKE`) every 200 ms. *deactivate* 200 ms — vents close.
- **Trigger**: `Berserk` active state.
- **Emissive**: red core, yellow warning strips.

#### Chrome Compressor (`chrome_compressor`)
- **Ref**: [Chrome Compressor](https://cyberpunk.fandom.com/wiki/Chrome_Compressor) (icon `CyberwareCapacityBooster.png`; Fextralife thumbnail reads as a dark cylinder with blue/white glow).
- **Rest**: Body, centre back, x −3.1..3.1, y 4.0..9.2, z 2.0..3.5.
- **Bones**: `housing`, `fan_1`, `fan_2` (cross-shaped blade clusters, pivot at fan centre, axis Z), `exhaust_l`, `exhaust_r`, `status`.
- **Clips**: *idle* 1 s loop — each fan rotates 90° per 250 ms step (four steps); status light blinks 1 Hz; chrome exhausts stay static.
- **Trigger**: while installed (ambient).
- **Emissive**: yellow construction accents, blue status light.

### 13.2 Frontal cortex

Shared mount: six 1.4 × 1.4 px slots in a forehead band, y −7.4..−6.0, exposed face z −4.85 (≈0.85 px in front of the head face at z −4.0), body recessed to z −3.9. Slot centres at x −3.75, −2.25, −0.75, 0.75, 2.25, 3.75. This matches the brief's 1 px cybernetic slit and lets every chip be installed simultaneously without overlap. The 0.3.3 side/rear head placements are replaced.

#### Axolotl (`axolotl`)
- **Ref**: [Axolotl](https://cyberpunk.fandom.com/wiki/Axolotl) (shares `SubdermalCoProcessor.png`; canonical look undocumented).
- **Rest → 0.4.0**: from left-side head strip (x −4.9..−3.4, y −6.0..−1.4) to forehead slot 1 (x −4.45..−3.05).
- **Bones**: `chip`, `glow` (emissive overlay), `insert`.
- **Clips**: *idle* 2 s — white insert pulse. *kill* 150 ms — `glow` turns acid green at full alpha, then clears.
- **Trigger**: kill (`CyberwareManager.onKill`).
- **Emissive**: acid green overlay.

#### Quantum Tuner (`quantum_tuner`)
- **Ref**: [Quantum Tuner](https://cyberpunk.fandom.com/wiki/Quantum_Tuner) (icon `TimeBank.png`).
- **Rest → 0.4.0**: from rear head (z 2.8..4.5) to forehead slot 2 (x −2.95..−1.55).
- **Bones**: `chip`, `core` (cyan cube), `ring`.
- **Clips**: *idle* 2 s `SMOOTH` — core scale 1.0 → 1.08 → 1.0 (pulsing opacity surrogate). *cooldown* 200 ms `STEP` — chip translates z −0.5 px then returns.
- **Trigger**: cooldown reset (new `Trigger.COOLDOWN`, throttled to 1/s).
- **Emissive**: cyan core.

#### COX-2 Cybersomatic Optimizer (`cox2_optimizer`)
- **Ref**: [COX-2](https://cyberpunk.fandom.com/wiki/COX-2_Cybersomatic_Optimizer) (reuses `BioConductors.png`; glowing conductive traces).
- **Rest → 0.4.0**: from right-side head (x 3.4..4.7) to forehead slot 3 (x −1.45..−0.05).
- **Bones**: `chip`, `grid_1..grid_6` (orange emissive pixels), `copper_frame`.
- **Clips**: *idle* — copper frame. *hack* 300 ms loop — grid pixels appear bottom to top, one per 50 ms, hold 100 ms, clear; repeats while a hack is being cast (max 1.2 s).
- **Trigger**: quickhack cast (`NetrunnerManager.cast`).
- **Emissive**: neon orange grid.

#### Self-ICE (`self_ice`)
- **Ref**: [Self-ICE](https://cyberpunk.fandom.com/wiki/Self-ICE) (dedicated `SelfIce.png`; blue/white geometric shield with glowing nodes).
- **Rest → 0.4.0**: from brow plate (x −2.4..2.4, z −4.9..−4.1) to forehead slot 4 (x 0.05..1.45).
- **Bones**: `plate`, `node`, `barrier` (six `HOLO` cube faces around the head, hidden at rest).
- **Clips**: *idle* 3 s — silver glint, node pulse. *block* 500 ms `EASE_OUT` — barrier scale 0.4 → 1.8, alpha 0.45 → 0; node flashes electric blue.
- **Trigger**: new Self-ICE effect block (section 8).
- **Emissive**: electric blue node, barrier edges.

#### Newton Module / Memory Boost (`newton_memory_boost`)
- **Ref**: [Newton Module](https://cyberpunk.fandom.com/wiki/Newton_Module), [Memory Boost](https://cyberpunk.fandom.com/wiki/Memory_Boost) (Fextralife thumbnails: white/blue cube).
- **Rest → 0.4.0**: from left-rear head (x −3.6..−0.4, z 2.6..4.4) to forehead slot 5 (x 1.55..2.95).
- **Bones**: `chip`, `flash_blue`, `flash_green`.
- **Clips**: *idle* — grey/blue housing. *ram* 200 ms — blue flash overlay; *cooldown* 200 ms — green flash overlay.
- **Trigger**: RAM regeneration (`Trigger.RAM`, throttled) and cooldown reset.
- **Emissive**: blue/green overlays.

#### Ex-Disk / RAM Upgrade (`ex_disk_ram_upgrade`)
- **Ref**: [Ex-Disk](https://cyberpunk.fandom.com/wiki/Ex-Disk), [RAM Upgrade](https://cyberpunk.fandom.com/wiki/RAM_Upgrade) (Fextralife: grey/blue module with glowing edges).
- **Rest → 0.4.0**: from right-rear head (x 0.8..3.8, z 2.8..4.7) to forehead slot 6 (x 3.05..4.45).
- **Bones**: `card`, `edge_1..edge_4`, `data_1..data_4` (violet emissive pixels).
- **Clips**: *idle* 1.6 s loop — four data pixels travel along the card edges, one edge each 400 ms; dark violet card body.
- **Trigger**: while installed (ambient).
- **Emissive**: violet data pixels.

### 13.3 Ocular system

#### Kiroshi Optics (`kiroshi_optics`)
- **Ref**: [Basic Kiroshi Optics](https://cyberpunk.fandom.com/wiki/Basic_Kiroshi_Optics), [Cockatrice](https://cyberpunk.fandom.com/wiki/Kiroshi_Cockatrice_Optics), [Sentry](https://cyberpunk.fandom.com/wiki/Kiroshi_Sentry_Optics), [Clairvoyant](https://cyberpunk.fandom.com/wiki/Kiroshi_Clairvoyant_Optics).
- **Rest**: Head, face layer, x −4.0..4.0, y −6.0..−2.9, z −4.8..−3.9. Keep chrome rim and black lens; the square iris is already present.
- **Bones**: `lens_l`, `lens_r` (pivot at each lens centre, z −4.65); `iris_l`, `iris_r` (pivot at iris centre); `cone` (pivot between the eyes, z −4.8, translucent `HOLO`).
- **Clips**: *idle* 2.4 s — iris brightness simulated by a 1 px emissive overlay alternating every 600 ms (`STEP`). *scan* 220 ms `EASE_OUT` — iris scale 1.0 → 0.75, cone scale (0.2, 0.2, 0.1) → (0.9, 0.9, 2.6) and alpha 0 → 0.22; holds while scanning; reverses in 160 ms.
- **Trigger**: client `ScannerState`, plus a 180 ms version while the player is using a spyglass/bow (aim surrogate).
- **Emissive**: iris swatch (`CYAN`, `NEON_RED` or `GOLD` variant) and cone edge.

#### Behavioral Imprint Faceplate (`behavioral_imprint`)
- **Ref**: [Behavioral Imprint-synced Faceplate](https://cyberpunk.fandom.com/wiki/Behavioral_Imprint-synced_Faceplate).
- **Rest**: Head, left cheek and nose, x −4.4..0.8, y −5.1..−1.8, z −4.9..−2.0. Keep the segmented chrome/smoke plate.
- **Bones**: `seg_1`, `seg_2`, `seg_3` (each pivot at its inner edge), `holo` (overlay plate with a second material).
- **Clips**: *idle* 3 s — very slow holo shimmer between SMOKE and CHROME overlay boxes (`STEP`, 1.5 s each). *imprint* 2 s `STEP` — segments slide 0.5 px sideways in sequence (60 ms apart), holo overlay turns cyan for 2 s, then everything returns.
- **Trigger**: quickhack cast (`NetrunnerManager.cast`), and scan start as a secondary identity check.
- **Emissive**: cyan holo overlay only.

### 13.4 Hands

#### Smart Link (`smart_link`)
- **Ref**: [Smart Link](https://cyberpunk.fandom.com/wiki/Smart_Link).
- **Rest**: Arms, palm/forearm, x −2.6..0.6, y 7.9..11.2, z −2.4..−1.9 (mirrored to both hands).
- **Bones**: `palm` (pivot at wrist), `reticle` (small emissive cross on the palm), `port`.
- **Clips**: *idle* 2 s — reticle rotates 90° in four steps. *lock* 600 ms loop while a scan target exists — reticle scale 1 → 1.15 → 1, port pulses; client spawns 4 green pixel particles per second travelling from the hand to the target (`END_ROD`, no network).
- **Trigger**: scan target (`ScannerState`), plus melee attack for 200 ms.
- **Emissive**: orange/gold reticle, green lock pixels.

#### Ballistic Coprocessor (`ballistic_coprocessor`)
- **Ref**: [Ballistic Coprocessor](https://cyberpunk.fandom.com/wiki/Ballistic_Coprocessor).
- **Rest**: Arms, back of the hand, x −2.8..0.8, y 7.8..12.1, z 1.8..2.4.
- **Bones**: `plate`, `line_1`, `line_2`, `line_3` (emissive red strips).
- **Clips**: *idle* — none. *impact* 360 ms `STEP` — lines blink 3 × 120 ms and the plate recoils 0.15 px on z.
- **Trigger**: `ProjectileImpactEvent` when the projectile owner or the hit entity is the player.
- **Emissive**: red lines.

#### Microgenerator (`microgenerator`)
- **Ref**: [Microgenerator](https://cyberpunk.fandom.com/wiki/Microgenerator).
- **Rest**: Arms, wrist, x −3.0..0.9, y 7.0..9.3, z −2.0..2.2.
- **Bones**: `coil`, `charge_1..4` (white/blue pixels), `ring` (4 translucent blue cubes around the wrist).
- **Clips**: *idle* 1.6 s — coil glow pulse. *charge* — one charge pixel lights per melee hit (max 3, 5 s timeout). *burst* 400 ms `EASE_OUT` — ring scale 0.5 → 1.6, alpha 0.55 → 0; charge pixels clear. Existing 15% electric proc plays a 200 ms mini-burst.
- **Trigger**: melee hit (`ImplantEffects.onMeleeHit`), plus the existing proc.
- **Emissive**: charge pixels, ring.

#### Handle Wrap / Immovable Force (`handle_wrap`)
- **Ref**: [Handle Wrap](https://cyberpunk.fandom.com/wiki/Handle_Wrap), [Immovable Force](https://cyberpunk.fandom.com/wiki/Immovable_Force).
- **Rest**: Arms, hand and wrist, x −3.0..1.0, y 7.8..10.7, z −2.5..2.5.
- **Bones**: `wrap`, `grip`, `plate` (Immovable Force brace).
- **Clips**: *idle* — none. *grip* 120 ms — grip scale 1.03 on attack. Passive: when the implant is installed, the client dampens the vanilla arm swing amplitude to 0.7 while attacking (mixin on `HumanoidModel` setup, gated on synced cyberware).
- **Trigger**: attack (client-side), passive while installed.
- **Emissive**: none.

### 13.5 Arms

#### Mantis Blades (`mantis_blades`)
- **Ref**: [Mantis Blades](https://cyberpunk.fandom.com/wiki/Mantis_Blades).
- **Rest**: Right arm, forearm to blade, x −3.0..1.1, y 2.4..15.6, z −2.5..2.2. Keeps the dedicated 32×32 ceramic/steel/orange texture and `MantisBladeRenderer`.
- **Bones**: renderer-local `plate_l`, `plate_r` (forearm plates, pivot at the elbow side), `blade` (existing extension pivot), `edge` (emissive strip).
- **Clips**: *deploy* 300 ms `SMOOTH` — existing blade extension plus plates lift 45° over the first 180 ms. *retract* 220 ms. *slash_r* 250 ms `EASE_OUT` — yaw +20°, roll +35°, translation z −1.5 px; *slash_l* mirrored (roll −35°). Attacks alternate right/left, producing the X pattern; the blade also scales y 0.92 → 1.04 at impact.
- **Trigger**: existing `MantisBladeStatePayload` toggle for deploy/retract; vanilla attack swing for slashes (client).
- **Emissive**: blade edge strip.

#### Gorilla Arms (`gorilla_arms`)
- **Ref**: [Gorilla Arms](https://cyberpunk.fandom.com/wiki/Gorilla_Arms).
- **Rest**: Arms, oversized forearm and fist, x −3.8..1.2, y −2.0..10.8, z −2.3..2.3. Gold fingers, black plating, blue/red pistons already present.
- **Bones**: `forearm`, `fist`, `piston_l`, `piston_r` (blue/red), `knuckle_l`, `knuckle_r` (emissive overlays).
- **Clips**: *idle* — fists breathe with walk phase (procedural, ±0.3°). *charge* — driven by vanilla attack cooldown: piston z-scale 1 → 0.85, knuckle glow overlay alpha 0 → 1. *punch* 180 ms `EASE_OUT` — fist translation z −1.2 px, pistons rebound to 1.08, knuckles flash; then 120 ms return. *hit* 100 ms — recoil z +0.3 px.
- **Trigger**: attack cooldown for charge (client), melee hit for punch (`ImplantEffects.onMeleeHit`), existing knockback unchanged.
- **Emissive**: knuckles, piston fluid.

#### Monowire (`monowire`)
- **Ref**: [Monowire](https://cyberpunk.fandom.com/wiki/Monowire).
- **Rest**: Right wrist housing, x −2.8..0.8, y 8.0..15.6, z −2.3..2.2. Not installable: the item stays a weapon (`ModItems.MONOWIRE`).
- **Renderer**: new `MonowireRenderer` draws the housing and 8 segmented wire cubes in hand; the worn model is unused until Monowire becomes an implant.
- **Clips**: *idle held* — wire coiled, 1 px pulse. *whip* 320 ms `EASE_OUT` — segments unfurl along a 180° horizontal arc in front of the player, yaw 0 → 180°, tip lags 40 ms per segment; emissive orange; retract 180 ms. *slash* uses the vanilla attack swing as the driver.
- **Trigger**: attack with the Monowire item (client); no gameplay change.
- **Emissive**: entire wire.

#### Projectile Launch System (`projectile_launch_system`)
- **Ref**: [Projectile Launch System](https://cyberpunk.fandom.com/wiki/Projectile_Launch_System).
- **Rest**: Right forearm, top-mounted cannon, x −3.2..0.8, y 2.8..9.8, z −2.3..4.0. Olive/gunmetal, red/yellow muzzle.
- **Bones**: `hatch` (slides back 3 px), `muzzle`, `rocket` (hidden at rest), `flame_1..3` (emissive cubes, hidden).
- **Clips**: *idle* 2 s — warning light blinks. *launch* 300 ms `STEP` — hatch opens in 100 ms, rocket appears and translates z +2 px, three flame cubes flash for 3 frames, hatch closes in 100 ms. The existing explosion at the target is unchanged; the rocket is visual only.
- **Trigger**: existing melee-triggered explosion (`ImplantEffects.onMeleeHit`, empty hand, 20 tolerance).
- **Emissive**: muzzle, flame, rocket tip.

### 13.6 Nervous system

#### Kerenzikov (`kerenzikov`)
- **Ref**: [Kerenzikov](https://cyberpunk.fandom.com/wiki/Kerenzikov).
- **Rest**: Body, shoulder blades, x −4.6..4.6, y 7.0..11.6, z 2.0..3.2.
- **Bones**: `node_l`, `node_r` (pivot at each scapula), `ring_l`, `ring_r` (translucent cyan cubes), `spine`.
- **Clips**: *idle* 2 s — slow cyan pulse. *active* 500 ms loop — nodes vibrate ±0.2 px at 20 ms steps while Sandevistan is active; rings scale 0.5 → 2.2, alpha 0.4 → 0. *low_health* — same rings at 1.5 Hz when health < 35%.
- **Trigger**: `Sandevistan` active state and client health.
- **Emissive**: cyan nodes, rings.

#### Reflex Tuner / Synaptic Accelerator (`reflex_synaptic_accelerator`)
- **Ref**: [Reflex Tuner](https://cyberpunk.fandom.com/wiki/Reflex_Tuner), [Synaptic Accelerator](https://cyberpunk.fandom.com/wiki/Synaptic_Accelerator).
- **Rest**: Body, nape/upper spine, x −2.2..2.2, y 9.6..13.0, z −0.4..2.1.
- **Bones**: `filament_1..3`, `node` (violet/cyan), `ring`.
- **Clips**: *idle* 2.4 s — filaments brighten bottom-to-top in three steps. *surge* 450 ms — rapid upward flash ×3 with 150 ms steps, violet ring expands once.
- **Trigger**: damage taken and low health.
- **Emissive**: yellow/violet filaments.

#### Maneuvering System (`maneuvering_system`)
- **Ref**: [Maneuvering System](https://cyberpunk2077.wiki.fextralife.com/Maneuvering_System) (Militech; angular grey casing, amber accents).
- **Rest**: Body, hips, x −5.3..5.3, y 2.0..6.5, z −1.2..2.0.
- **Bones**: `thruster_l`, `thruster_r` (pivot at hip), `flame_l`, `flame_r` (orange emissive cubes, hidden).
- **Clips**: *idle* — slight sway with walk (procedural). *burst* 300 ms — thrusters tilt 30° down, flames appear for 3 × 50 ms steps, then thrusters return over 150 ms.
- **Trigger**: double jump (`DoubleJumpHandler`).
- **Emissive**: orange flames, amber accents.

#### Adrenaline Converter / Adreno-Trigger (`adrenaline_converter`)
- **Ref**: [Adrenaline Converter](https://cyberpunk.fandom.com/wiki/Adrenaline_Converter), [Adreno-trigger](https://cyberpunk.fandom.com/wiki/Adreno-trigger).
- **Rest**: Body, base of neck, x −2.5..2.5, y 9.4..12.7, z −2.9..−1.3.
- **Bones**: `vial`, `cap`, `liquid_1..liquid_4` (green cubes stacked inside).
- **Clips**: *idle* 3 s — liquid full, one bubble pixel rises every 1.5 s. *inject* 400 ms `STEP` — liquid hides top-down one cube per 80 ms; cap flashes. *refill* 60 s `LINEAR` — liquid cubes return bottom-up, driven by the existing 1200-tick cooldown.
- **Trigger**: existing low-health proc (`ImplantEffects.onDamaged`, ≤30%, 60 s cooldown).
- **Emissive**: chemical green liquid.

### 13.7 Circulatory system

Chest zoning (0.4.0): Second Heart centre-left lower, Blood Pump right lower, Heal-on-Kill right upper flank, Syn-Lungs upper chest, Biomonitor left wrist. This removes the 0.3.3 overlap between Blood Pump and Heal-on-Kill.

#### Second Heart (`second_heart`)
- **Ref**: [Second Heart](https://cyberpunk.fandom.com/wiki/Second_Heart).
- **Rest**: Body, left chest, x −3.2..0.3, y 3.4..8.9, z −3.8..−1.9.
- **Bones**: `heart` (pivot at centre), `core` (gold), `tube_l`, `tube_r`, `flash` (white `HOLO` overlay, hidden).
- **Clips**: *idle* 800 ms loop — heart scale 1.0 → 1.04 → 1.0 in two `STEP` frames; core glows. *revive* 600 ms `EASE_OUT` — core scale 1 → 2.6, white flash overlay alpha 0.9 → 0, heart scale 1.15 then settle; existing totem particles play.
- **Trigger**: revive (`ImplantEffects.allowDeath`).
- **Emissive**: gold core, red accents.

#### Biomonitor (`biomonitor`)
- **Ref**: [Biomonitor](https://cyberpunk.fandom.com/wiki/Biomonitor).
- **Rest → 0.4.0**: moves from the right wrist to the left wrist (`LeftArm`, x mirrored), display facing forward.
- **Bones**: `screen`, `bar_1..4`, `cross` (red overlay), `heal` (green overlay).
- **Clips**: *idle* 2 s loop — green bars scroll. *alert* 500 ms loop at health < 35% — red cross blinks, bars shrink to the current health ratio. *heal* 600 ms — green cross and bars fill once when the existing proc fires.
- **Trigger**: client health ratio; proc packet from `ImplantEffects.onDamaged`.
- **Emissive**: green bars, red cross.

#### Blood Pump (`blood_pump`)
- **Ref**: [Blood Pump](https://cyberpunk.fandom.com/wiki/Blood_Pump).
- **Rest**: Body, right lower chest, x 1.0..4.2, y 3.4..8.9, z −3.5..−1.9.
- **0.4.0 move**: shifted down/right to x 0.8..3.8, y 3.4..7.4 so Heal-on-Kill fits above.
- **Bones**: `pump`, `tube_1`, `tube_2`, `flow_1..flow_4` (red pixels), `pulse`.
- **Clips**: *idle* 1.6 s — flow pixels travel along the tubes. *surge* 150 ms `STEP` — pump scale 1 → 1.15 → 1 in 3 frames (50 ms each); flow speed ×3 for 600 ms.
- **Trigger**: kill and low-health damage (existing heal paths).
- **Emissive**: red/orange liquid.

#### Heal-On-Kill (`heal_on_kill`)
- **Ref**: [Heal-On-Kill](https://cyberpunk.fandom.com/wiki/Heal-On-Kill).
- **Rest**: Body, right chest, x 2.2..3.6, y 5.4..10.0, z −3.5..−2.1.
- **0.4.0 move**: right upper flank, x 2.6..4.2, y 8.2..10.6, above the Blood Pump.
- **Bones**: `injector`, `needle`, `glow` (green overlay), `canister`.
- **Clips**: *idle* 2.4 s — canister pulse. *inject* 300 ms — needle extends 1 px in 80 ms, green flash overlay 120 ms, retract 100 ms.
- **Trigger**: kill (`CyberwareManager.onKill` heal).
- **Emissive**: green canister, cyan accent.

#### Syn-Lungs / Bioplastic Blood Vessels (`syn_lungs_bioplastic`)
- **Ref**: [Syn-Lungs](https://cyberpunk.fandom.com/wiki/Syn-Lungs), [Bioplastic Blood Vessels](https://cyberpunk2077.wiki.fextralife.com/Bioplastic_Blood_Vessels).
- **Rest**: Body, upper chest both sides, x −3.8..3.8, y 5.4..11.6, z −3.2..−1.9.
- **Bones**: `lung_l`, `lung_r` (pivot at sternum), `vein_l`, `vein_r`, `glow`.
- **Clips**: *idle* 4 s `SMOOTH` — lungs scale (1.0, 1.0, 1.0) → (1.06, 1.02, 1.04) and back, following a breathing rhythm. *kill* 300 ms — vein overlays flash cyan, lungs expand once.
- **Trigger**: ambient breathing; kill event.
- **Emissive**: cyan veins.

### 13.8 Integumentary system

#### Subdermal Armor / Chitin / Carapace (`subdermal_armor`)
- **Ref**: [Subdermal Armor](https://cyberpunk.fandom.com/wiki/Subdermal_Armor), [Chitin](https://cyberpunk.fandom.com/wiki/Chitin), [Carapace](https://cyberpunk.fandom.com/wiki/Carapace).
- **Rest**: Body, torso plates, x −5.0..5.0, y 5.0..11.2, z −2.7..2.6.
- **0.4.0 extension**: add mirrored shoulder plates on both arms and thigh plates on both legs. New groups `plate_chest`, `plate_back`, `plate_arm` (mirror), `plate_leg` (mirror), `plate_shoulder` (mirror).
- **Bones**: `plate_chest` (pivot centre), `plate_back`, `plate_arm_l/r`, `plate_leg_l/r`, `plate_shoulder_l/r`.
- **Clips**: *idle* — none. *impact* 180 ms `STEP` — hit plate translates 0.18 px inward on z and 0.1 px along the hit axis, then returns; grey `CRIT` sparks spawn at the plate. The hit plate is chosen from the damage source direction (front/back/left/right).
- **Trigger**: damage taken (`CyberCraft.damage`).
- **Emissive**: none (carbon black / insect green / concrete grey).

#### Optical Camo (`optical_camo`)
- **Ref**: [Optical Camo](https://cyberpunk.fandom.com/wiki/Optical_Camo).
- **Rest**: Body shell, x −5.2..5.2, y 5.0..11.9, z −2.9..2.8.
- **0.4.0 extension**: full-body `HOLO` shell groups for head, body, both arms and both legs. Alpha 0 at rest; 0.32 while camo is active.
- **Bones**: `shell_head`, `shell_body`, `shell_arm_l/r`, `shell_leg_l/r`.
- **Clips**: *fade_in* 500 ms top-down — head alpha 0 → 0.32 in 120 ms, body 80 ms later, arms/legs 160 ms later. *fade_out* 350 ms bottom-up. A faint cyan edge cube remains visible on the silhouette.
- **Trigger**: current Optical Camo state (sneaking while almost still grants invisibility). `isInvisible` and the implant are checked client-side; no new keybind.
- **Emissive**: cyan edge only.

#### Pain Editor / Painducer (`pain_editor`)
- **Ref**: [Pain Editor](https://cyberpunk.fandom.com/wiki/Pain_Editor), [Painducer](https://cyberpunk.fandom.com/wiki/Painducer).
- **Rest**: Body, spine chip, x −2.6..2.6, y 3.0..10.4, z 1.9..3.2.
- **Bones**: `chip_1..3`, `glow`, `clamp_1..3`.
- **Clips**: *idle* 3 s — mustard glow pulse. *suppress* 250 ms — clamps tighten 0.1 px, glow flashes rust→mustard, then release. No player flinch change is claimed; it is a visual response.
- **Trigger**: damage taken.
- **Emissive**: mustard glow.

#### Shock-n-Awe (`shock_n_awe`)
- **Ref**: [Shock-n-Awe](https://cyberpunk.fandom.com/wiki/Shock-n-Awe).
- **Rest**: Body, pectoral plates, x −3.8..3.8, y 5.4..11.5, z −3.1..−2.0.
- **Bones**: `plate_l`, `plate_r`, `coil_l`, `coil_r`, `arc_1..arc_4` (emissive cubes bridging the plates).
- **Clips**: *idle* 2 s — coils pulse yellow. *discharge* 400 ms `STEP` — arcs appear for 3 × 60 ms frames, plates kick apart 0.15 px, blue-white flash; existing thorns damage and spark burst unchanged.
- **Trigger**: Shock-n-Awe proc in `ImplantEffects.onDamaged`.
- **Emissive**: electric blue arcs, yellow coils.

#### Fireproof Coating / Grounding Plating (`fireproof_grounding`)
- **Ref**: [Fireproof Coating](https://cyberpunk2077.wiki.fextralife.com/Fireproof_Coating), [Grounding Plating](https://cyberpunk2077.wiki.fextralife.com/Grounding_Plating) (no infobox image).
- **Rest**: Body, chest coating, x −4.2..4.2, y 4.2..10.3, z −2.9..−2.0.
- **Bones**: `coat`, `ground_l`, `ground_r`, `heat_1..heat_3` (orange pixels).
- **Clips**: *idle* — matte dark red coat; grounding plates static. *heat* 2 s loop while on fire — orange pixels rise 1 px and fade in three steps; coat swatch flashes. *ground* 400 ms when fire damage is taken — grounding plates flash chrome and the heat pixels extinguish.
- **Trigger**: `entity.isOnFire()` (client ambient) and fire damage.
- **Emissive**: orange heat pixels.

### 13.9 Skeleton

#### Titanium Bones (`titanium_bones`)
- **Ref**: [Titanium Bones](https://cyberpunk.fandom.com/wiki/Titanium_Bones).
- **Rest**: Arms and legs, bone strips, y 0.4..10.6.
- **Bones**: `strip_arm_l/r`, `strip_leg_l/r`, `glint`.
- **Clips**: *idle* 3 s — a metallic glint overlay travels one step along each strip every 3 s. *brace* 200 ms — strips scale 1.02 on landing or taking damage.
- **Trigger**: ambient; land/damage.
- **Emissive**: none (titanium white/silver).

#### Dense Marrow (`dense_marrow`)
- **Ref**: [Dense Marrow](https://cyberpunk.fandom.com/wiki/Dense_Marrow).
- **Rest**: Arms and legs, joint cylinders, y 0.0..5.2.
- **Bones**: `cyl_arm_l/r`, `cyl_leg_l/r` (pivot at each joint), `fluid`.
- **Clips**: *walk* — cylinders compress 0.04 per step (procedural). *impact* 250 ms — compress to 0.85, rebound to 1.06, settle; black fluid pixels flash.
- **Trigger**: walk (procedural), land/melee.
- **Emissive**: none.

#### Epimorphic Skeleton / Rara Avis (`epimorphic_skeleton`)
- **Ref**: [Epimorphic Skeleton](https://cyberpunk.fandom.com/wiki/Epimorphic_Skeleton), [Rara Avis](https://cyberpunk.fandom.com/wiki/Rara_Avis).
- **Rest**: Body, external rib cage, x −4.2..4.2, y 3.5..11.2, z −3.2..2.7.
- **Bones**: `rib_l_1..3`, `rib_r_1..3` (pivot at the spine), `sternum`, `glow`.
- **Clips**: *idle* 4 s `SMOOTH` — ribs rotate 0.8° outwards and back with breathing. *brace* 300 ms — ribs clamp 1.2° inward, then flare 1.5° on release; bronze glow while the low-health regeneration is active.
- **Trigger**: ambient breathing; damage; regeneration state.
- **Emissive**: bronze/gold accents.

#### Bionic Joints / Para Bellum (`bionic_joints`)
- **Ref**: [Bionic Joints](https://cyberpunk.fandom.com/wiki/Bionic_Joints), [Para Bellum](https://cyberpunk.fandom.com/wiki/Para_Bellum).
- **Rest**: Arms and legs, elbow/knee rings, y −0.2..4.5.
- **Bones**: `joint_arm_l/r`, `joint_leg_l/r` (pivot at each joint).
- **Clips**: *swing* — joint rings rotate with the vanilla arm swing and walk cycle (procedural, ±6°). *lock* 150 ms — ring rotates 90° `STEP` on landing.
- **Trigger**: ambient movement; land.
- **Emissive**: none (gunmetal/chrome).

#### Spring Joints (`spring_joints`)
- **Ref**: [Spring Joints](https://cyberpunk.fandom.com/wiki/Spring_Joints).
- **Rest**: Legs, rear calves, x −1.8..1.8, y 4.6..11.9, z 1.5..2.9.
- **Bones**: `spring_l`, `spring_r` (pivot at top), `coil_l/r`, `accent`.
- **Clips**: *walk* — springs compress 0.03 per step. *jump* 220 ms — compress 0.7, yellow accent flash. *land* 250 ms — compress 0.72, rebound to 1.08, settle.
- **Trigger**: jump/land.
- **Emissive**: yellow accent pixels.

### 13.10 Legs

#### Reinforced Tendons (`reinforced_tendons`)
- **Ref**: [Reinforced Tendons](https://cyberpunk.fandom.com/wiki/Reinforced_Tendons).
- **Rest**: Legs, rear ankle/calf, x −1.9..1.9, y 6.0..11.8, z 1.7..3.0.
- **Bones**: `tendon_l`, `tendon_r`, `string_l/r`, `glow`.
- **Clips**: *idle* — tendons follow walk tension. *boost* 300 ms — tendons extend 0.4 px, red strings flash, on double jump.
- **Trigger**: double jump.
- **Emissive**: red string pixels.

#### Fortified Ankles (`fortified_ankles`)
- **Ref**: [Fortified Ankles](https://cyberpunk.fandom.com/wiki/Fortified_Ankles).
- **Rest**: Legs, ankles/feet, x −3.3..3.3, y 9.4..12.3, z −4.7..2.8.
- **Bones**: `ankle_l`, `ankle_r` (pivot at the shin), `piston_l/r`, `sole_l/r`.
- **Clips**: *impact* 260 ms — ankle pistons compress 0.8, soles flare 0.2 px outwards, dust puffs at the feet, then settle.
- **Trigger**: landing (fall distance > 0.5).
- **Emissive**: gold piston accents.

#### Lynx Paws (`lynx_paws`)
- **Ref**: [Lynx Paws](https://cyberpunk.fandom.com/wiki/Lynx_Paws).
- **Rest**: Legs, feet, x −2.7..2.7, y 9.8..12.6, z −4.9..2.6.
- **Bones**: `paw_l`, `paw_r`, `toe_1..3` (per foot), `pad`.
- **Clips**: *walk* 4-phase `STEP` — toes flex in sequence with the walk cycle. *sneak* — pads flatten 0.1 px and a 1 px purple pixel fades at each step. No footstep sound change.
- **Trigger**: ambient walk; sneaking.
- **Emissive**: dark purple inserts.

#### Jenkins' Tendons / Leeroy Ligament System (`jenkins_tendons`)
- **Ref**: [Jenkins' Tendons](https://cyberpunk.fandom.com/wiki/Jenkins%27_Tendons), [Leeroy Ligament System](https://cyberpunk.fandom.com/wiki/Leeroy_Ligament_System).
- **Rest**: Legs, outer sides, x −3.6..−2.2, y 1.6..10.4 (mirrored).
- **Bones**: `tendon_l`, `tendon_r`, `pulse_1..4`, `anchor`.
- **Clips**: *sprint* 600 ms loop — a cyan/red pulse travels bottom-to-top in four steps while sprinting; anchors glint. *idle* — static.
- **Trigger**: sprinting (client ambient).
- **Emissive**: cyan/red pulse.

#### Cyber Legs (`cyber_legs`)
- **Ref**: No canonical page; category reference [Cyberpunk 2077 Cyberware — Legs](https://cyberpunk.fandom.com/wiki/Category:Cyberpunk_2077_Cyberware_-_Legs).
- **Rest**: Legs, full replacement, x −2.7..2.7, y 0.8..12.2, z −2.6..2.6.
- **Bones**: `thigh_l/r`, `knee_l/r`, `shin_l/r`, `foot_l/r`, `piston_l/r`.
- **Clips**: *walk* — knee pistons extend 0.15 px and shin plates rotate ±3° with the walk cycle (procedural). *jump* 200 ms — pistons extend 0.3 px, cyan joints flash. *land* 220 ms — pistons compress 0.2 px and settle.
- **Trigger**: ambient walk; jump/land.
- **Emissive**: cyan joint pixels.

---

## 14. Implementation status (0.4.0)

Delivered:

- Schema v2 (`Group(bone, pivot, ...)`, mirrored left groups) in `ImplantModels`; boxes and pivots unchanged from the 0.3.3 rest pose.
- `ImplantAnimations` with 43 implant sets plus Monowire: idle and active loops, trigger clips, bone tracks, easing, and per-bone priority resolution.
- Direct worn renderer over the palette atlas with per-face light/base/dark swatches; no per-implant worn models or textures.
- `ImplantAnimationPayload` and `ImplantAnimationClient` with 64-block culling, 100 ms idle sampling, one-shot playback, and swing-driven clips.
- Server triggers: kill, damage, fire, landing, melee hit, quickhack cast, RAM/cooldown, double jump, projectile impact, harmful effect (Self-ICE); client ambient triggers: scan, low health, sneak-still, sprinting.
- Gameplay additions: Self-ICE blocks hostile mob effects once per 60 s; Optical Camo keeps its existing state and fades its shell; Handle Wrap damps the vanilla swing arc through a client mixin.
- Mantis Blades bone integration and first-person Monowire whip.
- Tests: unit tests for clip evaluation, priority, catalogue completeness, trigger/ID consistency, and bone existence; gametests for payload round-trip, trigger dispatch, and Self-ICE behaviour.

Deviations and limits to keep in mind:

- The client receives discrete trigger packets only; state loops are derived independently from synced cyberware and vanilla state, so animation phase is not shared between players.
- Idle sampling is capped at 100 ms per entity; trigger clips can override idle bones exactly.
- First person shows arm/hand animations only; the Monowire whip is hand-space, not a worn model.
- `HandleWrapSwingMixin` targets the erased `setupAnim` descriptor and depends on `EntityModel.attackTime` remaining public.
- A visual pass in-game remains the acceptance step; automated tests do not prove model placement or animation appearance.
