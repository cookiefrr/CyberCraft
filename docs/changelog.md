# Changes

## 0.4.0 — Animated cyberware

Every implant now has a bone layer with authored pivots, and all 43 implants play idle, active, and trigger animations driven by gameplay. The catalogue covers idle loops, state loops (Sandevistan, Berserk, Optical Camo, Mantis Blades), and one-shot clips for kills, damage, hacks, cooldowns, RAM changes, jumps, landings, attacks, impacts, revives, injections, refills, suppression, discharges, and launches. Clips are evaluated per bone with idle → active → trigger priority, easing, and looping, and are replicated through discrete trigger payloads.

Bones, boxes, and tracks are authored in humanoid model pixels and rendered directly from the palette atlas; left limbs are generated as mirrored groups. The Mantis Blades renderer consumes its plate, blade, and edge bones for lift, deploy, alternating slashes, and edge pulse; the Monowire gains a first-person whip driven by swing progress; a mixin damps the swing arc while Handle Wrap is installed.

Self-ICE gains one disclosed behaviour: a hostile mob effect that would be applied while the implant is operational is cancelled with a 60-second cooldown, a barrier clip, particles, and a message. Landing events feed the LAND trigger. Emissive shading remains shader-only.

## 0.3.3 — Cuboid cyberware models

Every implant now has a dedicated palette-based 3D item model and hand-authored worn geometry made only of cuboids; the Monowire also gains a 3D item model. Worn models are authored in humanoid model pixels and attached to the head, torso, arms, or legs, with generated mirrored variants for left limbs. They align with the player model in third person and with the rendered arm in first person. A 256×256 material atlas provides dark metal, chrome, titanium, ceramics, circuits, and neon accents with blocky light/base/dark shading. Mantis Blades keep their animated renderer and 300 ms toggle, recolored to white ceramic, silver, and neon orange. No gameplay or balance changes.

## 0.3.1 — Animated Mantis Blades

Mantis Blades now use a dedicated 3D forearm housing and a long straight, serrated blade with a silver cutting edge and red cabling, rendered in first and third person. M (remappable) toggles the blades with a 300 ms slide-out or retract animation; they remain extended until pressed again. The toggle is server-authoritative and transient: it resets on implant removal, death, logout, or server stop, and is re-sent to players who start tracking the wearer. The +6 unarmed bonus and 3-tolerance cost apply only while the blades are extended and the implant is operational. Retracted blades keep the housing visible. Non-player wearers render the blades permanently extended, preserving their mob loadout behavior. Visual acceptance is performed by the user.

## 0.3.0 — NeoForge rewrite

Native NeoForge 1.21.1 registration, events, attachments, networking, biome modifiers, and data generation replace the Fabric implementation. Existing content IDs and recipes remain. This version requires fresh worlds.

Equipment now stores real component-bearing stacks in extensible data-pack slot types. The inventory-adjacent Cyberware tab includes normal inventory/hotbar controls, paging, detail display, and Back navigation. All maintained content is English. GeckoLib 4.9.3 supplies inventory icon rendering; native geometry remains in held, dropped, and equipped views.

Kiroshi optics and an installed cyberdeck gate scanning/hacking. Remote Access toggles controls, including iron doors. Disruption suppresses redstone output for five seconds without restoring stale block states. Natural hostile humanoids gain capped cyberware loadouts as elapsed world age increases.

### Ten flaws addressed

| Existing flaw | Correction and regression coverage |
|---|---|
| Free scanner and carried-deck access | Installed optics/deck checks, overload checks, access tests |
| Standalone UI and fixed body slots | Registered inventory container, named slots, add-on fixture and transfer tests |
| Italian strings and inaccurate English descriptions | English providers, corrected descriptions, resource completeness checks |
| No data generation | Deterministic providers, duplicate-resource and asset validation |
| Bonus melee damage issued as a separate hit | Incoming-damage adjustment, exact damage regression test |
| Retaliation could retrigger retaliation | Thorns-origin guard, opposing-implant regression test |
| Reinstall/death could clear ability cooldowns | Persistent independent cooldowns, reinstall and death-copy tests |
| Effects remained after removal/overload | Saved effect ownership, overload/removal and unrelated-potion tests |
| Scanner altered teams/glow and retained client state | Rendering-only outlines and disconnect reset; no scoreboard writes |
| Hacks lacked consistent attribution/protection checks | Hacker-attributed damage, shared aim validation, team/creative/protection tests |

Visual acceptance is performed by the user. Dedicated-server and data tests do not prove model placement or UI appearance.

## Previous releases

0.2.2 was the Fabric 1.21.1 release with scanner targeting, outlines, and the original standalone cyberware screen. The native rewrite is developed on `indev`; stable `main` is unchanged.
