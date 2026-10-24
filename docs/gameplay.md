# Gameplay

Install implants through the CW inventory tab, by using the implant, or through the Installation Table. Neural Link installs a compatible item held in the other hand. Standard clicks and shift-click move stacks between equipment and inventory. Equipment survives death; XP-derived capacity can still decrease after death. When equipment exceeds capacity, its effects suspend until the loadout fits again.

Capacity is `min(60, 20 + 2 × XP level)`, plus 20 with Chrome Compressor. Mobs are capped at 60. RAM and tolerance each cap at 100. RAM regenerates at 0.34/second with implant bonuses. Tolerance regenerates at 3/second; overload disables implants, pauses passive drain, and recovers at half speed until 25 tolerance.

Kiroshi optics unlock the R scanner. A separately installed cyberdeck enables G quickhacks. X/Z cycle compatible hacks. Range follows render distance up to 64 blocks; walls and server protections apply. Sandevistan/Berserk use V, last ten seconds by default, and cool down for thirty seconds after the effect ends. Removing an OS ends its effect without resetting the cooldown. M toggles Mantis Blades: the blades slide out over 300 ms and stay extended until M is pressed again, then retract over 300 ms. The toggle requires operational Mantis Blades, is server-authoritative, and resets when the implant is removed, the player dies, or the player logs out.

| Hack | Base RAM | Effect |
|---|---:|---|
| Short Circuit | 4 | Hacker-attributed electrical damage and fire |
| Optic Reboot | 3 | Blindness and immediate loss of the mob's target |
| Contagion | 5 | Poison and weakness |
| Remote Access | 3 | Toggle doors, trapdoors, fence gates, or levers |
| Disruption | 5 | Silence a vanilla redstone source for five seconds |

Mob enhancement starts on day 5 at a 5% chance. Chance rises linearly to 35% on day 50 and then stops. Maximum loadout/tier is one on day 5, two on day 15, and three on day 30. Age uses elapsed game ticks, not the sun clock. Only eligible naturally spawned hostile humanoids are rolled, once; no automatic implant drops or mob block hacks are added.

Implant item icons remain flat in inventory/hotbar views. In the world and on the player, all 43 implants and the Monowire use dedicated cuboid 3D models: OS and torso implants sit on the back or chest, frontal-cortex and ocular implants on the head, hand/arm implants on both arms or the right arm, and leg/skeleton implants on the matching limbs. Models follow player animation in third person and the rendered arm in first person. Hold Shift over an item for technical details. The Edgerunner's Book reads crafting recipes from the active recipe manager.

## Implant catalog

| Implant | Slot | Tier | Capacity | Effect |
|---|---|---:|---:|---|
| Adrenaline Converter | nervous | 2 | 5 | Below 30% health: Speed II, Strength II and Resistance I for 6 s. 60 s cooldown. |
| Axolotl | frontal cortex | 2 | 5 | +2 max health. Sandevistan lasts 2 seconds longer. |
| Ballistic Coprocessor | hands | 2 | 5 | +1.5 attack damage and +0.5 attack speed. |
| Behavioral Imprint | ocular | 2 | 4 | Quickhacks cost 10% less RAM. +1 Luck. |
| Berserk | operating system | 3 | 14 | Activates Berserk: Strength II, Resistance II and Speed I. 30 tolerance. |
| Biomonitor | circulatory | 2 | 6 | Below 25% health: heals 3 hearts and grants Absorption II. 60 s cooldown. |
| Bionic Joints | skeleton | 2 | 6 | +3% movement speed. |
| Blood Pump | circulatory | 2 | 5 | +2 max health. Heals 0.5 hearts per kill. |
| Chrome Compressor | operating system | 3 | 10 | Increases cyberware capacity by 20, allowing more implants at once. |
| COX-2 Optimizer | frontal cortex | 2 | 5 | Short Circuit deals +25% damage. RAM regenerates 0.04/s faster. |
| Cybernetics Legs | legs | 1 | 6 | Passive Jump Boost III. Drains 1 tolerance per second. |
| Cyberdeck | cyberdeck | 2 | 6 | Install in the dedicated cyberdeck slot to enable quickhacks. Requires Kiroshi optics. |
| Dense Marrow | skeleton | 2 | 6 | +4 max health. |
| Epimorphic Skeleton | skeleton | 3 | 8 | +6 max health. Regeneration while below half health. |
| Ex-Disk RAM Upgrade | frontal cortex | 3 | 6 | +0.10 RAM regeneration per second. Maximum RAM is 100. |
| Fireproof Grounding | integumentary | 2 | 5 | Permanent fire resistance. |
| Fortified Ankles | legs | 2 | 6 | -50% fall damage. |
| Gorilla Arms | arms | 2 | 8 | +3 attack damage and +20% knockback resistance. Unarmed hits add another +3 damage for 2 tolerance. |
| Handle Wrap | hands | 1 | 3 | +0.5 attack speed. |
| Heal-On-Kill | circulatory | 2 | 5 | Heals 2 hearts on every kill. |
| Jenkins' Tendons | legs | 2 | 5 | +5% movement speed. |
| Kerenzikov | nervous | 2 | 6 | +2% movement speed. |
| Kiroshi Optics | ocular | 2 | 5 | Night vision and access to the scanner. Install a cyberdeck to hack targets. |
| Lynx Paws | legs | 1 | 4 | +30% sneaking speed. |
| Maneuvering System | nervous | 2 | 5 | +0.08 jump strength and -15% fall damage. |
| Mantis Blades | arms | 2 | 8 | M extends or retracts the blades; they stay out until toggled off. While extended, unarmed strikes deal +6 damage and cost 3 tolerance per hit. |
| Microgenerator | hands | 2 | 6 | +1 attack damage. Melee hits have a 15% chance to add a 2.5-damage electrical burst. |
| Newton Memory Boost | frontal cortex | 2 | 4 | RAM regenerates 0.08/s faster. Sandevistan cooldown reduced by 3 seconds. |
| Optical Camo | integumentary | 3 | 7 | Grants invisibility while sneaking and standing still. +10% sneaking speed. |
| Pain Editor | integumentary | 2 | 6 | +2 armor and +2 armor toughness. |
| Projectile Launch System | arms | 3 | 10 | Unarmed hits trigger an explosion every 5 seconds. Costs 20 tolerance. |
| Quantum Tuner | frontal cortex | 3 | 6 | Reduces Sandevistan cooldown by 6 seconds. |
| Reflex Synaptic Accelerator | nervous | 3 | 7 | +2% movement speed and +1 attack speed. |
| Reinforced Tendons | legs | 2 | 6 | Press jump again in midair to double jump. |
| Sandevistan | operating system | 3 | 12 | Activates Sandevistan: Speed III and Haste II for 10 s while mobs within 15 blocks are slowed by 85%. 30 s cooldown, 35 tolerance. |
| Second Heart | circulatory | 3 | 10 | Cancels one lethal hit every 60 s, healing to 30% health with regeneration and absorption. |
| Self-ICE | frontal cortex | 2 | 4 | Passive ICE protection: +2 armor. Blocks one hostile mob effect every 60 s. |
| Shock 'n Awe | integumentary | 2 | 6 | Melee attackers within 4 blocks take 2.5 lightning damage. |
| Smart Link | hands | 2 | 5 | +2 attack damage. |
| Spring Joints | skeleton | 1 | 5 | +0.15 jump strength and -30% fall damage. |
| Subdermal Armor | integumentary | 2 | 7 | +4 armor. |
| Syn-Lungs Bioplastic | circulatory | 2 | 4 | Water breathing and +50% water movement efficiency. |
| Titanium Bones | skeleton | 2 | 6 | +4 armor toughness. |
