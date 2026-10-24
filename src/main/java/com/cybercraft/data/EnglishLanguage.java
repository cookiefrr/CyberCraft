package com.cybercraft.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public final class EnglishLanguage extends LanguageProvider {
  public EnglishLanguage(PackOutput output) {
    super(output, "cybercraft", "en_us");
  }

  @Override
  protected void addTranslations() {
    add("cyberware.cybercraft.cyberdeck", "Cyberdeck");
    add("block.cybercraft.dark_metal_block", "Dark Metal Block");
    add("block.cybercraft.deepslate_lithium_ore", "Deepslate Lithium Ore");
    add("block.cybercraft.deepslate_neodymium_ore", "Deepslate Neodymium Ore");
    add("block.cybercraft.deepslate_titanium_ore", "Deepslate Titanium Ore");
    add("block.cybercraft.implant_forge", "Implant Forge");
    add("block.cybercraft.installation_table", "Installation Table");
    add("block.cybercraft.lithium_block", "Block of Lithium");
    add("block.cybercraft.lithium_ore", "Lithium Ore");
    add("block.cybercraft.neodymium_block", "Block of Neodymium");
    add("block.cybercraft.neodymium_ore", "Neodymium Ore");
    add("block.cybercraft.neon_block", "Neon Block");
    add("block.cybercraft.raw_lithium_block", "Block of Raw Lithium");
    add("block.cybercraft.raw_neodymium_block", "Block of Raw Neodymium");
    add("block.cybercraft.raw_titanium_block", "Block of Raw Titanium");
    add("block.cybercraft.titanium_block", "Block of Titanium");
    add("block.cybercraft.titanium_ore", "Titanium Ore");
    add(
        "book.cybercraft.desc.adrenaline_converter",
        "Below 30% health: Speed II, Strength II and Resistance I for 6 s. 60 s cooldown.");
    add("book.cybercraft.desc.axolotl", "+2 max health. Sandevistan lasts 2 seconds longer.");
    add("book.cybercraft.desc.ballistic_coprocessor", "+1.5 attack damage and +0.5 attack speed.");
    add("book.cybercraft.desc.behavioral_imprint", "Quickhacks cost 10% less RAM. +1 Luck.");
    add(
        "book.cybercraft.desc.berserk",
        "Activates Berserk: Strength II, Resistance II and Speed I. 30 tolerance.");
    add("book.cybercraft.desc.bio_gel", "Organic conductive gel for circulatory implants.");
    add(
        "book.cybercraft.desc.biomonitor",
        "Below 25% health: heals 3 hearts and grants Absorption II. 60 s cooldown.");
    add("book.cybercraft.desc.bionic_joints", "+3% movement speed.");
    add("book.cybercraft.desc.blood_pump", "+2 max health. Heals 0.5 hearts per kill.");
    add(
        "book.cybercraft.desc.chrome_compressor",
        "Increases cyberware capacity by 20, allowing more implants at once.");
    add(
        "book.cybercraft.desc.circuit_board",
        "Base component for every implant. Crafted from redstone and gold.");
    add(
        "book.cybercraft.desc.cox2_optimizer",
        "Short Circuit deals +25% damage. RAM regenerates 0.04/s faster.");
    add(
        "book.cybercraft.desc.cyber_legs",
        "Passive Jump Boost III. Drains 1 tolerance per second.");
    add(
        "book.cybercraft.desc.cyberdeck",
        "Install in the dedicated cyberdeck slot to enable quickhacks. Requires Kiroshi optics.");
    add(
        "book.cybercraft.desc.dark_metal_block",
        "Tough dark metal block used for industrial builds.");
    add(
        "book.cybercraft.desc.deepslate_lithium_ore",
        "Deepslate variant of lithium ore. Drops raw lithium.");
    add(
        "book.cybercraft.desc.deepslate_neodymium_ore",
        "Deepslate variant of neodymium ore. Drops raw neodymium.");
    add(
        "book.cybercraft.desc.deepslate_titanium_ore",
        "Deepslate variant of titanium ore. Drops raw titanium.");
    add("book.cybercraft.desc.dense_marrow", "+4 max health.");
    add(
        "book.cybercraft.desc.edgerunners_book",
        "Illustrated manual: description, icon and recipe for every CyberCraft item.");
    add(
        "book.cybercraft.desc.epimorphic_skeleton",
        "+6 max health. Regeneration while below half health.");
    add(
        "book.cybercraft.desc.ex_disk_ram_upgrade",
        "+0.10 RAM regeneration per second. Maximum RAM is 100.");
    add("book.cybercraft.desc.fireproof_grounding", "Permanent fire resistance.");
    add("book.cybercraft.desc.fortified_ankles", "-50% fall damage.");
    add(
        "book.cybercraft.desc.gorilla_arms",
        "+3 attack damage and +20% knockback resistance. Unarmed hits add another +3 damage for 2"
            + " tolerance.");
    add("book.cybercraft.desc.handle_wrap", "+0.5 attack speed.");
    add("book.cybercraft.desc.heal_on_kill", "Heals 2 hearts on every kill.");
    add(
        "book.cybercraft.desc.implant_forge",
        "Workbench for assembling cyberware. Uses the standard 3x3 crafting grid.");
    add(
        "book.cybercraft.desc.installation_table",
        "Right-click with an implant to install it, or empty-handed to open the cyberware screen.");
    add("book.cybercraft.desc.jenkins_tendons", "+5% movement speed.");
    add("book.cybercraft.desc.kerenzikov", "+2% movement speed.");
    add(
        "book.cybercraft.desc.kiroshi_optics",
        "Night vision and access to the scanner. Install a cyberdeck to hack targets.");
    add(
        "book.cybercraft.desc.lithium_block",
        "Decorative and compact storage block for lithium ingots.");
    add(
        "book.cybercraft.desc.lithium_ingot",
        "Reactive metal used in batteries, bio gel and power cells.");
    add(
        "book.cybercraft.desc.lithium_ore",
        "Generates between Y -32 and Y 64, 12 veins per chunk. Drops raw lithium.");
    add("book.cybercraft.desc.lynx_paws", "+30% sneaking speed.");
    add("book.cybercraft.desc.maneuvering_system", "+0.08 jump strength and -15% fall damage.");
    add(
        "book.cybercraft.desc.mantis_blades",
        "Toggle extending blades with the Mantis Blades key. Extended: unarmed strikes deal +6 damage for 3 tolerance per hit. Blades stay out until toggled off.");
    add(
        "book.cybercraft.desc.microgenerator",
        "+1 attack damage. Melee hits have a 15% chance to add a 2.5-damage electrical burst.");
    add("book.cybercraft.desc.monowire", "High damage, slow swings and 2 extra blocks of reach.");
    add(
        "book.cybercraft.desc.neodymium_block",
        "Decorative and compact storage block for neodymium ingots.");
    add(
        "book.cybercraft.desc.neodymium_ingot",
        "Powerful magnet metal used in advanced and military cyberware.");
    add(
        "book.cybercraft.desc.neodymium_ore",
        "Generates between Y -64 and Y 16, 7 veins per chunk. Drops raw neodymium.");
    add("book.cybercraft.desc.neon_block", "Decorative block emitting maximum light.");
    add(
        "book.cybercraft.desc.neural_link",
        "Open cyberware, install an implant from the other hand, or sneak-use to activate your"
            + " operating system.");
    add(
        "book.cybercraft.desc.newton_memory_boost",
        "RAM regenerates 0.08/s faster. Sandevistan cooldown reduced by 3 seconds.");
    add(
        "book.cybercraft.desc.optical_camo",
        "Grants invisibility while sneaking and standing still. +10% sneaking speed.");
    add(
        "book.cybercraft.desc.optics_lens",
        "Precision lens for ocular implants and targeting systems.");
    add("book.cybercraft.desc.pain_editor", "+2 armor and +2 armor toughness.");
    add(
        "book.cybercraft.desc.projectile_launch_system",
        "Unarmed hits trigger an explosion every 5 seconds. Costs 20 tolerance.");
    add("book.cybercraft.desc.quantum_tuner", "Reduces Sandevistan cooldown by 6 seconds.");
    add("book.cybercraft.desc.raw_lithium", "Light reactive ore. Smelt it into an ingot.");
    add("book.cybercraft.desc.raw_lithium_block", "Compact storage for raw lithium.");
    add("book.cybercraft.desc.raw_neodymium", "Magnetic rare earth ore. Smelt it into an ingot.");
    add("book.cybercraft.desc.raw_neodymium_block", "Compact storage for raw neodymium.");
    add(
        "book.cybercraft.desc.raw_titanium",
        "Rough titanium ore. Smelt or blast it into an ingot.");
    add("book.cybercraft.desc.raw_titanium_block", "Compact storage for raw titanium.");
    add(
        "book.cybercraft.desc.reflex_synaptic_accelerator",
        "+2% movement speed and +1 attack speed.");
    add("book.cybercraft.desc.reinforced_tendons", "Press jump again in midair to double jump.");
    add(
        "book.cybercraft.desc.sandevistan",
        "Activates Sandevistan: Speed III and Haste II for 10 s while mobs within 15 blocks are"
            + " slowed by 85%. 30 s cooldown, 35 tolerance.");
    add(
        "book.cybercraft.desc.second_heart",
        "Cancels one lethal hit every 60 s, healing to 30% health with regeneration and"
            + " absorption.");
    add("book.cybercraft.desc.self_ice", "Passive ICE protection: +2 armor.");
    add(
        "book.cybercraft.desc.servo_motor",
        "Precision actuator used by arms, legs and joint implants.");
    add(
        "book.cybercraft.desc.shock_n_awe",
        "Melee attackers within 4 blocks take 2.5 lightning damage.");
    add("book.cybercraft.desc.smart_link", "+2 attack damage.");
    add("book.cybercraft.desc.spring_joints", "+0.15 jump strength and -30% fall damage.");
    add("book.cybercraft.desc.subdermal_armor", "+4 armor.");
    add(
        "book.cybercraft.desc.syn_lungs_bioplastic",
        "Water breathing and +50% water movement efficiency.");
    add(
        "book.cybercraft.desc.titanium_block",
        "Decorative and compact storage block for titanium ingots.");
    add("book.cybercraft.desc.titanium_bones", "+4 armor toughness.");
    add(
        "book.cybercraft.desc.titanium_ingot",
        "Light and strong alloy used for skeletons, limbs and armor plating.");
    add(
        "book.cybercraft.desc.titanium_ore",
        "Generates between Y -64 and Y 32, 10 veins per chunk. Drops raw titanium.");
    add("category.cybercraft", "CyberCraft");
    add("container.cybercraft.implant_forge", "Implant Forge");
    add("cyberware.cybercraft.adrenaline_converter", "Adrenaline Converter");
    add("cyberware.cybercraft.axolotl", "Axolotl");
    add("cyberware.cybercraft.ballistic_coprocessor", "Ballistic Coprocessor");
    add("cyberware.cybercraft.behavioral_imprint", "Behavioral Imprint");
    add("cyberware.cybercraft.berserk", "Berserk");
    add("cyberware.cybercraft.biomonitor", "Biomonitor");
    add("cyberware.cybercraft.bionic_joints", "Bionic Joints");
    add("cyberware.cybercraft.blood_pump", "Blood Pump");
    add("cyberware.cybercraft.chrome_compressor", "Chrome Compressor");
    add("cyberware.cybercraft.cox2_optimizer", "COX-2 Optimizer");
    add("cyberware.cybercraft.cyber_legs", "Cybernetics Legs");
    add("cyberware.cybercraft.dense_marrow", "Dense Marrow");
    add("cyberware.cybercraft.epimorphic_skeleton", "Epimorphic Skeleton");
    add("cyberware.cybercraft.ex_disk_ram_upgrade", "Ex-Disk RAM Upgrade");
    add("cyberware.cybercraft.fireproof_grounding", "Fireproof Grounding");
    add("cyberware.cybercraft.fortified_ankles", "Fortified Ankles");
    add("cyberware.cybercraft.gorilla_arms", "Gorilla Arms");
    add("cyberware.cybercraft.handle_wrap", "Handle Wrap");
    add("cyberware.cybercraft.heal_on_kill", "Heal-On-Kill");
    add("cyberware.cybercraft.jenkins_tendons", "Jenkins' Tendons");
    add("cyberware.cybercraft.kerenzikov", "Kerenzikov");
    add("cyberware.cybercraft.kiroshi_optics", "Kiroshi Optics");
    add("cyberware.cybercraft.lynx_paws", "Lynx Paws");
    add("cyberware.cybercraft.maneuvering_system", "Maneuvering System");
    add("cyberware.cybercraft.mantis_blades", "Mantis Blades");
    add("cyberware.cybercraft.microgenerator", "Microgenerator");
    add("cyberware.cybercraft.newton_memory_boost", "Newton Memory Boost");
    add("cyberware.cybercraft.optical_camo", "Optical Camo");
    add("cyberware.cybercraft.pain_editor", "Pain Editor");
    add("cyberware.cybercraft.projectile_launch_system", "Projectile Launch System");
    add("cyberware.cybercraft.quantum_tuner", "Quantum Tuner");
    add("cyberware.cybercraft.reflex_synaptic_accelerator", "Reflex Synaptic Accelerator");
    add("cyberware.cybercraft.reinforced_tendons", "Reinforced Tendons");
    add("cyberware.cybercraft.sandevistan", "Sandevistan");
    add("cyberware.cybercraft.second_heart", "Second Heart");
    add("cyberware.cybercraft.self_ice", "Self-ICE");
    add("cyberware.cybercraft.shock_n_awe", "Shock 'n Awe");
    add("cyberware.cybercraft.smart_link", "Smart Link");
    add("cyberware.cybercraft.spring_joints", "Spring Joints");
    add("cyberware.cybercraft.subdermal_armor", "Subdermal Armor");
    add("cyberware.cybercraft.syn_lungs_bioplastic", "Syn-Lungs Bioplastic");
    add("cyberware.cybercraft.titanium_bones", "Titanium Bones");
    add("hud.cybercraft.berserk.active", "BERSERK %ss");
    add("hud.cybercraft.berserk.cooldown", "BERSERK %ss");
    add("hud.cybercraft.sandevistan.active", "SANDEVISTAN %ss");
    add("hud.cybercraft.sandevistan.cooldown", "SANDEVISTAN %ss");
    add("hud.cybercraft.scan.ability", "ABILITY");
    add("hud.cybercraft.scan.ability.none", "None");
    add("hud.cybercraft.scan.archetype", "ARCHETYPE");
    add("hud.cybercraft.scan.archetype.hostile", "HOSTILE");
    add("hud.cybercraft.scan.archetype.neutral", "NEUTRAL");
    add("hud.cybercraft.scan.blocked", "BLOCKED");
    add("hud.cybercraft.scan.buffer.offline", "BUFFER OFFLINE");
    add("hud.cybercraft.scan.buffer.online", "BUFFER STABLE");
    add("hud.cybercraft.scan.faction", "FACTION");
    add("hud.cybercraft.scan.faction.civilian", "CIVILIAN");
    add("hud.cybercraft.scan.faction.hostile", "HOSTILE");
    add("hud.cybercraft.scan.faction.neutral", "NEUTRAL");
    add("hud.cybercraft.scan.faction.passive", "PASSIVE");
    add("hud.cybercraft.scan.faction.player", "PLAYER");
    add("hud.cybercraft.scan.faction.tech", "TECH");
    add("hud.cybercraft.scan.hint.execute", "Execute Quickhack");
    add("hud.cybercraft.scan.hint.exit", "Exit Scan");
    add("hud.cybercraft.scan.hint.navigate", "Navigate List");
    add("hud.cybercraft.scan.idle", "SCANNER ONLINE");
    add("hud.cybercraft.scan.lore.hostile", "Hostile unit. No negotiation protocol.");
    add("hud.cybercraft.scan.lore.neutral", "Non-hostile subject. No bounty detected.");
    add("hud.cybercraft.scan.organic", "ORGANIC DETECTED");
    add("hud.cybercraft.scan.quickhacks", "AVAILABLE QUICKHACKS");
    add("hud.cybercraft.scan.searching", "NO SIGNATURE // SCANNING");
    add("hud.cybercraft.scan.status", "STATUS EFFECTS");
    add("hud.cybercraft.scan.status.none", "NONE");
    add("hud.cybercraft.scan.tech", "TECHNOLOGY DETECTED");
    add("hud.cybercraft.scan.threat", "THREAT");
    add("hud.cybercraft.scan.threat.high", "HIGH");
    add("hud.cybercraft.scan.threat.low", "LOW");
    add("hud.cybercraft.scan.threat.tech", "TECH");
    add("hud.cybercraft.scan.type", "TYPE");
    add("hud.cybercraft.scan.upload", "UPLOADING");
    add("hud.cybercraft.scan.upload.fail", "UPLOAD FAILED");
    add("hud.cybercraft.scan.upload.ok", "UPLOAD COMPLETE");
    add("hud.cybercraft.scan.vulnerabilities", "VULNERABILITIES");
    add("hud.cybercraft.status", "TOL %s %s/%s   RAM %s %s/%s   CAP %s/%s");
    add("item.cybercraft.adrenaline_converter", "Adrenaline Converter");
    add("item.cybercraft.axolotl", "Axolotl");
    add("item.cybercraft.ballistic_coprocessor", "Ballistic Coprocessor");
    add("item.cybercraft.behavioral_imprint", "Behavioral Imprint");
    add("item.cybercraft.berserk", "Berserk");
    add("item.cybercraft.bio_gel", "Bio Gel");
    add("item.cybercraft.biomonitor", "Biomonitor");
    add("item.cybercraft.bionic_joints", "Bionic Joints");
    add("item.cybercraft.blood_pump", "Blood Pump");
    add("item.cybercraft.chrome_compressor", "Chrome Compressor");
    add("item.cybercraft.circuit_board", "Circuit Board");
    add("item.cybercraft.cox2_optimizer", "COX-2 Optimizer");
    add("item.cybercraft.cyber_legs", "Cybernetics Legs");
    add("item.cybercraft.cyberdeck", "Cyberdeck");
    add("item.cybercraft.dense_marrow", "Dense Marrow");
    add("item.cybercraft.edgerunners_book", "Edgerunner's Book");
    add("item.cybercraft.epimorphic_skeleton", "Epimorphic Skeleton");
    add("item.cybercraft.ex_disk_ram_upgrade", "Ex-Disk RAM Upgrade");
    add("item.cybercraft.fireproof_grounding", "Fireproof Grounding");
    add("item.cybercraft.fortified_ankles", "Fortified Ankles");
    add("item.cybercraft.gorilla_arms", "Gorilla Arms");
    add("item.cybercraft.handle_wrap", "Handle Wrap");
    add("item.cybercraft.heal_on_kill", "Heal-On-Kill");
    add("item.cybercraft.jenkins_tendons", "Jenkins' Tendons");
    add("item.cybercraft.kerenzikov", "Kerenzikov");
    add("item.cybercraft.kiroshi_optics", "Kiroshi Optics");
    add("item.cybercraft.lithium_ingot", "Lithium Ingot");
    add("item.cybercraft.lynx_paws", "Lynx Paws");
    add("item.cybercraft.maneuvering_system", "Maneuvering System");
    add("item.cybercraft.mantis_blades", "Mantis Blades");
    add("item.cybercraft.microgenerator", "Microgenerator");
    add("item.cybercraft.monowire", "Monowire");
    add("item.cybercraft.neodymium_ingot", "Neodymium Ingot");
    add("item.cybercraft.neural_link", "Neural Link");
    add("item.cybercraft.newton_memory_boost", "Newton Memory Boost");
    add("item.cybercraft.optical_camo", "Optical Camo");
    add("item.cybercraft.optics_lens", "Optics Lens");
    add("item.cybercraft.pain_editor", "Pain Editor");
    add("item.cybercraft.projectile_launch_system", "Projectile Launch System");
    add("item.cybercraft.quantum_tuner", "Quantum Tuner");
    add("item.cybercraft.raw_lithium", "Raw Lithium");
    add("item.cybercraft.raw_neodymium", "Raw Neodymium");
    add("item.cybercraft.raw_titanium", "Raw Titanium");
    add("item.cybercraft.reflex_synaptic_accelerator", "Reflex Synaptic Accelerator");
    add("item.cybercraft.reinforced_tendons", "Reinforced Tendons");
    add("item.cybercraft.sandevistan", "Sandevistan");
    add("item.cybercraft.second_heart", "Second Heart");
    add("item.cybercraft.self_ice", "Self-ICE");
    add("item.cybercraft.servo_motor", "Servo Motor");
    add("item.cybercraft.shock_n_awe", "Shock 'n Awe");
    add("item.cybercraft.smart_link", "Smart Link");
    add("item.cybercraft.spring_joints", "Spring Joints");
    add("item.cybercraft.subdermal_armor", "Subdermal Armor");
    add("item.cybercraft.syn_lungs_bioplastic", "Syn-Lungs Bioplastic");
    add("item.cybercraft.titanium_bones", "Titanium Bones");
    add("item.cybercraft.titanium_ingot", "Titanium Ingot");
    add("itemGroup.cybercraft.main", "CyberCraft");
    add("key.cybercraft.cyberware", "Open Cyberware Screen");
    add("key.cybercraft.execute_hack", "Execute Quickhack");
    add("key.cybercraft.hack_next", "Next Quickhack");
    add("key.cybercraft.hack_prev", "Previous Quickhack");
    add("key.cybercraft.mantis_blades", "Toggle Mantis Blades");
    add("key.cybercraft.sandevistan", "Activate Sandevistan");
    add("key.cybercraft.scanner", "Toggle Scanner");
    add("msg.cybercraft.adrenaline", "ADRENALINE SURGE");
    add("msg.cybercraft.already_installed", "Implant already installed");
    add("msg.cybercraft.berserk_active", "BERSERK ENGAGED");
    add("msg.cybercraft.biomonitor", "BIOMONITOR: emergency heal");
    add("msg.cybercraft.cooldown", "Sandevistan cooling down (%ss)");
    add("msg.cybercraft.cooldown_short", "Quickhack recharging (%ss)");
    add("msg.cybercraft.hack_selected", "Quickhack: %s (%s RAM)");
    add("msg.cybercraft.install_failed", "No compatible slot or insufficient capacity");
    add("msg.cybercraft.installed", "Implant installed: %s");
    add("msg.cybercraft.no_cyberdeck", "Cyberdeck required");
    add("msg.cybercraft.no_ram", "Not enough RAM");
    add("msg.cybercraft.no_sandevistan", "Sandevistan not installed");
    add("msg.cybercraft.no_target", "No target in sight");
    add("msg.cybercraft.not_enough_capacity", "Not enough cyberware capacity: %s/%s (need %s)");
    add("msg.cybercraft.optics_required", "Install Kiroshi optics to enable the scanner");
    add("msg.cybercraft.overload_start", "CYBERWARE OVERLOAD!");
    add("msg.cybercraft.overloaded", "CYBERWARE OVERLOAD: implants offline");
    add("msg.cybercraft.recovered", "Systems stabilized");
    add("msg.cybercraft.removed", "Implant removed: %s");
    add("msg.cybercraft.sandevistan_active", "Sandevistan active");
    add("msg.cybercraft.second_heart", "SECOND HEART: lethal damage negated");
    add("msg.cybercraft.self_ice", "SELF-ICE: hostile effect blocked");
    add("msg.cybercraft.slot_empty", "No implant in %s");
    add("msg.cybercraft.slot_occupied", "Slot occupied: %s");
    add("quickhack.cybercraft.contagion", "Contagion");
    add("quickhack.cybercraft.disruption", "Disruption");
    add("quickhack.cybercraft.optic_reboot", "Optic Reboot");
    add("quickhack.cybercraft.remote_access", "Remote Access");
    add("quickhack.cybercraft.short_circuit", "Short Circuit");
    add("screen.cybercraft.book", "Edgerunner's Book");
    add("screen.cybercraft.book.hint", "Click an entry  |  Mouse wheel scrolls  |  ESC closes");
    add("screen.cybercraft.book.index", "INDEX");
    add(
        "screen.cybercraft.book.no_recipe",
        "No crafting recipe: found in the world or via another mod.");
    add("screen.cybercraft.book.recipe", "RECIPE");
    add("screen.cybercraft.book.subtitle", "Cyberware catalog, field recipes and notes");
    add("screen.cybercraft.buffer", "RAM BUFFER");
    add("screen.cybercraft.buffer.online", "STABLE");
    add("screen.cybercraft.buffer.overload", "OVERLOAD");
    add("screen.cybercraft.capacity", "CAPACITY");
    add("screen.cybercraft.close", "Close");
    add("screen.cybercraft.cyberware", "Cyberware");
    add(
        "screen.cybercraft.cyberware.subtitle",
        "Persistent implant slots synced with the neural link");
    add("screen.cybercraft.cyberware.title", "RIPPERDOC LINK // CYBERWARE");
    add("screen.cybercraft.detail", "DETAIL");
    add("screen.cybercraft.empty", "EMPTY");
    add("screen.cybercraft.footer", "Click a slot  |  REMOVE uninstalls  |  ESC closes");
    add("screen.cybercraft.overview", "OVERVIEW");
    add("screen.cybercraft.remove", "REMOVE");
    add("screen.cybercraft.remove.hint", "The implant returns to your inventory");
    add("screen.cybercraft.selected", "SELECTED QUICKHACK");
    add("screen.cybercraft.slot_empty", "NO IMPLANT");
    add(
        "screen.cybercraft.slot_empty.hint",
        "Move a compatible implant into this slot. Hold Shift over an item for details.");
    add("screen.cybercraft.slots", "SLOTS");
    add("screen.cybercraft.tolerance", "TOLERANCE");
    add("screen.cybercraft.used", "USED");
    add("slot.cybercraft.arms", "arms");
    add("slot.cybercraft.circulatory", "circulatory system");
    add("slot.cybercraft.cyberdeck", "Cyberdeck");
    add("slot.cybercraft.frontal_cortex", "frontal cortex");
    add("slot.cybercraft.hands", "hands");
    add("slot.cybercraft.integumentary", "integumentary system");
    add("slot.cybercraft.legs", "legs");
    add("slot.cybercraft.nervous", "nervous system");
    add("slot.cybercraft.ocular", "ocular");
    add("slot.cybercraft.operating_system", "operating system");
    add("slot.cybercraft.skeleton", "skeleton");
    add("tooltip.cybercraft.capacity", "Capacity: %s");
    add("tooltip.cybercraft.slot", "Slot: %s");
    add("tooltip.cybercraft.tier", "Tier: %s / 3");
  }
}
