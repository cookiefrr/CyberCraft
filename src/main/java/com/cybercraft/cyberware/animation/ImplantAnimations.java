package com.cybercraft.cyberware.animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ImplantAnimations {
  private static final Map<String, AnimationSet> SETS = build();

  private ImplantAnimations() {}

  public static AnimationSet get(String implant) {
    return SETS.get(implant);
  }

  public static Set<String> ids() {
    return SETS.keySet();
  }

  public static Clip find(AnimationSet set, String clipId) {
    if (set == null || clipId == null) return null;
    if (set.idle() != null && set.idle().id().equals(clipId)) return set.idle();
    if (set.active() != null && set.active().id().equals(clipId)) return set.active();
    for (Clip clip : set.triggers().values()) {
      if (clip.id().equals(clipId)) return clip;
    }
    return null;
  }

  private static Map<String, AnimationSet> build() {
    Map<String, AnimationSet> map = new LinkedHashMap<>();

    map.put(
        "cyberdeck",
        new B()
            .idle(
                2400,
                ts(
                    t("led_a", k(0, on()), k(600, hide())),
                    t("led_b", k(0, hide()), k(1200, on()), k(1800, hide())),
                    t("circuit_a", k(0, on()), k(1200, hide())),
                    t("circuit_b", k(0, hide()), k(1200, on()))))
            .build());

    map.put(
        "sandevistan",
        new B()
            .idle(
                3000,
                ts(
                    pulse("module_1", 3000, 1.02f),
                    pulse("module_2", 3000, 1.02f),
                    pulse("module_3", 3000, 1.02f),
                    pulse("module_4", 3000, 1.02f),
                    pulse("module_5", 3000, 1.02f)))
            .active(
                900,
                ts(
                    lift("module_5", 0),
                    lift("module_4", 120),
                    lift("module_3", 240),
                    lift("module_2", 360),
                    lift("module_1", 480)))
            .build());

    map.put(
        "berserk",
        new B()
            .idle(3000, ts(pulse("core", 3000, 1.06f)))
            .active(
                1200,
                ts(
                    t(
                        "core",
                        k(0, scl(1.15f, 1.15f, 1.15f), Ease.SMOOTH),
                        k(600, scl(1.25f, 1.25f, 1.25f), Ease.SMOOTH),
                        k(1200, scl(1.15f, 1.15f, 1.15f), Ease.SMOOTH)),
                    t("vent_l", k(0, rot(0, 0, 90))),
                    t("vent_r", k(0, rot(0, 0, -90))),
                    t("vent_flap_l", k(0, on())),
                    t("vent_flap_r", k(0, on()))))
            .on(
                Trigger.ACTIVATE,
                250,
                ts(
                    t("vent_l", k(0, on(), Ease.EASE_OUT), k(200, rot(0, 0, 90), Ease.EASE_OUT)),
                    t("vent_r", k(0, on(), Ease.EASE_OUT), k(200, rot(0, 0, -90), Ease.EASE_OUT)),
                    t(
                        "core",
                        k(0, on(), Ease.EASE_OUT),
                        k(150, scl(1.15f, 1.15f, 1.15f), Ease.EASE_OUT)),
                    t("vent_flap_l", k(0, hide()), k(60, on())),
                    t("vent_flap_r", k(0, hide()), k(60, on()))))
            .on(
                Trigger.DEACTIVATE,
                200,
                ts(
                    t("vent_l", k(0, rot(0, 0, 90), Ease.EASE_IN), k(200, on())),
                    t("vent_r", k(0, rot(0, 0, -90), Ease.EASE_IN), k(200, on())),
                    t(
                        "core",
                        k(0, scl(1.15f, 1.15f, 1.15f), Ease.EASE_IN),
                        k(200, on())),
                    t("vent_flap_l", k(0, on()), k(200, hide())),
                    t("vent_flap_r", k(0, on()), k(200, hide()))))
            .build());

    map.put(
        "chrome_compressor",
        new B()
            .idle(
                1000,
                ts(
                    fan("fan_1"),
                    fan("fan_2"),
                    t("status", k(0, on()), k(500, hide()))))
            .build());

    map.put(
        "axolotl",
        new B()
            .idle(2000, ts(pulse("insert", 2000, 1.15f)))
            .on(
                Trigger.KILL,
                150,
                ts(
                    t(
                        "glow",
                        k(0, full(0, 0, 0, 0, 0, 0, 1.1f, 1.1f, 1.1f, 1)),
                        k(150, hide())),
                    t("chip", k(0, scl(1.06f, 1.06f, 1.06f), Ease.EASE_OUT), k(150, on()))))
            .build());

    map.put(
        "quantum_tuner",
        new B()
            .idle(2000, ts(pulse("core", 2000, 1.08f)))
            .on(
                Trigger.COOLDOWN,
                200,
                ts(
                    t(
                        "chip",
                        k(0, on(), Ease.EASE_OUT),
                        k(100, tr(0, 0, -0.5f), Ease.EASE_OUT),
                        k(200, on())),
                    t(
                        "ring",
                        k(0, on(), Ease.EASE_OUT),
                        k(100, scl(1.15f, 1.15f, 1.15f), Ease.EASE_OUT),
                        k(200, on()))))
            .build());

    map.put(
        "cox2_optimizer",
        new B()
            .idle(3000, ts(pulse("chip", 3000, 1.02f)))
            .on(Trigger.HACK, 1200, ts(grid("grid_1", 1), grid("grid_2", 51), grid("grid_3", 101),
                grid("grid_4", 151), grid("grid_5", 201), grid("grid_6", 251)))
            .build());

    map.put(
        "self_ice",
        new B()
            .idle(3000, ts(pulse("node", 3000, 1.1f)))
            .on(
                Trigger.BLOCK,
                500,
                ts(
                    t(
                        "barrier",
                        k(0, full(0, 0, 0, 0, 0, 0, 0.4f, 0.4f, 0.4f, 0.45f), Ease.EASE_OUT),
                        k(500, full(0, 0, 0, 0, 0, 0, 1.8f, 1.8f, 1.8f, 0), Ease.EASE_OUT)),
                    t(
                        "node",
                        k(0, scl(1.2f, 1.2f, 1.2f), Ease.EASE_OUT),
                        k(300, on()))))
            .build());

    map.put(
        "newton_memory_boost",
        new B()
            .idle(3000, ts(pulse("chip", 3000, 1.03f)))
            .on(
                Trigger.RAM,
                200,
                ts(
                    t("flash_blue", k(0, on()), k(200, hide())),
                    t("chip", k(0, scl(1.04f, 1.04f, 1.04f)), k(200, on()))))
            .on(Trigger.COOLDOWN, "cooldown_flash", 200, ts(t("flash_green", k(0, on()), k(200, hide()))))
            .build());

    map.put(
        "ex_disk_ram_upgrade",
        new B()
            .idle(
                1600,
                ts(
                    t("data_1", k(0, on()), k(400, hide())),
                    t("data_2", k(0, hide()), k(400, on()), k(800, hide())),
                    t("data_3", k(0, hide()), k(800, on()), k(1200, hide())),
                    t("data_4", k(0, hide()), k(1200, on()), k(1600, hide())),
                    t("card", k(0, on(), Ease.SMOOTH), k(800, scl(1.01f, 1.01f, 1), Ease.SMOOTH), k(1600, on()))))
            .build());

    map.put(
        "kiroshi_optics",
        new B()
            .idle(
                2400,
                ts(
                    t(
                        "iris_l",
                        k(0, on()),
                        k(600, scl(0.75f, 0.75f, 1)),
                        k(1200, on()),
                        k(1800, scl(0.75f, 0.75f, 1))),
                    t(
                        "iris_r",
                        k(0, on()),
                        k(600, scl(0.75f, 0.75f, 1)),
                        k(1200, on()),
                        k(1800, scl(0.75f, 0.75f, 1)))))
            .on(
                Trigger.SCAN,
                600,
                ts(
                    t(
                        "iris_l",
                        k(0, on(), Ease.EASE_OUT),
                        k(220, scl(0.75f, 0.75f, 1), Ease.EASE_OUT),
                        k(440, scl(0.75f, 0.75f, 1), Ease.EASE_OUT),
                        k(600, on())),
                    t(
                        "iris_r",
                        k(0, on(), Ease.EASE_OUT),
                        k(220, scl(0.75f, 0.75f, 1), Ease.EASE_OUT),
                        k(440, scl(0.75f, 0.75f, 1), Ease.EASE_OUT),
                        k(600, on())),
                    t(
                        "cone",
                        k(
                            0,
                            full(0, 0, 0, 0, 0, 0, 0.2f, 0.2f, 0.1f, 0),
                            Ease.EASE_OUT),
                        k(
                            220,
                            full(0, 0, 0, 0, 0, 0, 0.9f, 0.9f, 2.6f, 0.22f),
                            Ease.EASE_OUT),
                        k(
                            440,
                            full(0, 0, 0, 0, 0, 0, 0.9f, 0.9f, 2.6f, 0.22f),
                            Ease.EASE_OUT),
                        k(600, full(0, 0, 0, 0, 0, 0, 0.2f, 0.2f, 0.1f, 0)))))
            .build());

    map.put(
        "behavioral_imprint",
        new B()
            .idle(
                3000,
                ts(
                    t(
                        "holo",
                        k(0, alp(0.5f), Ease.SMOOTH),
                        k(1500, alp(0.2f), Ease.SMOOTH),
                        k(3000, alp(0.5f), Ease.SMOOTH))))
            .on(
                Trigger.IMPRINT,
                2000,
                ts(
                    t("seg_1", k(0, on()), k(60, tr(0.5f, 0, 0)), k(1800, tr(0.5f, 0, 0)), k(1900, on())),
                    t("seg_2", k(0, on()), k(120, tr(0.5f, 0, 0)), k(1800, tr(0.5f, 0, 0)), k(1900, on())),
                    t("seg_3", k(0, on()), k(180, tr(0.5f, 0, 0)), k(1800, tr(0.5f, 0, 0)), k(1900, on())),
                    t("holo", k(0, alp(0)), k(60, alp(0.9f)), k(1900, alp(0.9f)), k(2000, alp(0)))))
            .on(Trigger.SCAN, 2000, ts(t("holo", k(0, alp(0)), k(120, alp(0.6f)), k(600, alp(0)))))
            .build());

    map.put(
        "smart_link",
        new B()
            .idle(
                2000,
                ts(
                    t("reticle_r", k(0, on()), k(500, rot(0, 0, 90)), k(1000, rot(0, 0, 180)), k(1500, rot(0, 0, 270))),
                    t("reticle_l", k(0, on()), k(500, rot(0, 0, 90)), k(1000, rot(0, 0, 180)), k(1500, rot(0, 0, 270)))))
            .active(
                600,
                ts(
                    t(
                        "reticle_r",
                        k(0, on(), Ease.SMOOTH),
                        k(300, scl(1.15f, 1.15f, 1), Ease.SMOOTH),
                        k(600, on(), Ease.SMOOTH)),
                    t(
                        "reticle_l",
                        k(0, on(), Ease.SMOOTH),
                        k(300, scl(1.15f, 1.15f, 1), Ease.SMOOTH),
                        k(600, on(), Ease.SMOOTH)),
                    pulse("port_r", 600, 1.12f),
                    pulse("port_l", 600, 1.12f)))
            .on(
                Trigger.ATTACK,
                200,
                ts(
                    t("reticle_r", k(0, scl(1.2f, 1.2f, 1), Ease.EASE_OUT), k(200, on())),
                    t("reticle_l", k(0, scl(1.2f, 1.2f, 1), Ease.EASE_OUT), k(200, on()))))
            .build());

    map.put(
        "ballistic_coprocessor",
        new B()
            .idle(3000, ts())
            .on(
                Trigger.IMPACT,
                360,
                ts(
                    t("plate_r", k(0, on()), k(60, tr(0, 0, 0.15f)), k(240, on())),
                    t("plate_l", k(0, on()), k(60, tr(0, 0, 0.15f)), k(240, on())),
                    blink("line_1_r"),
                    blink("line_1_l"),
                    blink("line_2_r"),
                    blink("line_2_l"),
                    blink("line_3_r"),
                    blink("line_3_l")))
            .build());

    map.put(
        "microgenerator",
        new B()
            .idle(1600, ts(pulse("coil_r", 1600, 1.06f), pulse("coil_l", 1600, 1.06f)))
            .on(
                Trigger.HIT,
                400,
                ts(
                    t(
                        "ring_r",
                        k(0, full(0, 0, 0, 0, 0, 0, 0.5f, 0.5f, 0.5f, 0.55f), Ease.EASE_OUT),
                        k(400, full(0, 0, 0, 0, 0, 0, 1.6f, 1.6f, 1.6f, 0), Ease.EASE_OUT)),
                    t(
                        "ring_l",
                        k(0, full(0, 0, 0, 0, 0, 0, 0.5f, 0.5f, 0.5f, 0.55f), Ease.EASE_OUT),
                        k(400, full(0, 0, 0, 0, 0, 0, 1.6f, 1.6f, 1.6f, 0), Ease.EASE_OUT)),
                    t("charge_1_r", k(0, on()), k(400, hide())),
                    t("charge_1_l", k(0, on()), k(400, hide())),
                    t("charge_2_r", k(0, on()), k(400, hide())),
                    t("charge_2_l", k(0, on()), k(400, hide())),
                    t("charge_3_r", k(0, on()), k(400, hide())),
                    t("charge_3_l", k(0, on()), k(400, hide())),
                    t("charge_4_r", k(0, on()), k(400, hide())),
                    t("charge_4_l", k(0, on()), k(400, hide()))))
            .build());

    map.put(
        "handle_wrap",
        new B()
            .idle(3000, ts())
            .on(
                Trigger.GRIP,
                120,
                ts(
                    t("grip_r", k(0, scl(1.03f, 1.03f, 1.03f), Ease.EASE_OUT), k(120, on())),
                    t("grip_l", k(0, scl(1.03f, 1.03f, 1.03f), Ease.EASE_OUT), k(120, on()))))
            .build());

    map.put(
        "mantis_blades",
        new B()
            .active(
                1000,
                ts(
                    t("plate", k(0, rot(0, 0, -45))),
                    t("blade", k(0, scl(1, 1.05f, 1))),
                    t("edge", k(0, scl(1.04f, 1.04f, 1.04f)))))
            .on(
                Trigger.DEPLOY,
                300,
                ts(
                    t("plate", k(0, on(), Ease.SMOOTH), k(180, rot(0, 0, -45), Ease.SMOOTH), k(300, rot(0, 0, -45))),
                    t("blade", k(0, on(), Ease.SMOOTH), k(180, scl(1, 1.05f, 1), Ease.SMOOTH), k(300, scl(1, 1.05f, 1))),
                    t("edge", k(0, on()), k(300, scl(1.04f, 1.04f, 1.04f)))))
            .on(
                Trigger.RETRACT,
                220,
                ts(
                    t("plate", k(0, rot(0, 0, -45), Ease.EASE_IN), k(220, on())),
                    t("blade", k(0, scl(1, 1.05f, 1), Ease.EASE_IN), k(220, on())),
                    t("edge", k(0, scl(1.04f, 1.04f, 1.04f), Ease.EASE_IN), k(220, on()))))
            .on(Trigger.SLASH_R, 250, slash(1))
            .on(Trigger.SLASH_L, 250, slash(-1))
            .build());

    map.put(
        "gorilla_arms",
        new B()
            .idle(
                2000,
                ts(
                    pulse("fist_r", 2000, 1.015f),
                    pulse("fist_l", 2000, 1.015f)))
            .on(
                Trigger.PUNCH,
                300,
                ts(
                    t("fist_r", k(0, on(), Ease.EASE_OUT), k(180, tr(0, 0, -1.2f), Ease.EASE_OUT), k(300, on())),
                    t("fist_l", k(0, on(), Ease.EASE_OUT), k(180, tr(0, 0, -1.2f), Ease.EASE_OUT), k(300, on())),
                    t("piston_a_r", k(0, on()), k(180, scl(1, 1, 1.08f)), k(300, on())),
                    t("piston_a_l", k(0, on()), k(180, scl(1, 1, 1.08f)), k(300, on())),
                    t("piston_b_r", k(0, on()), k(180, scl(1, 1, 1.08f)), k(300, on())),
                    t("piston_b_l", k(0, on()), k(180, scl(1, 1, 1.08f)), k(300, on())),
                    t("knuckle_a_r", k(0, on()), k(180, scl(1.2f, 1.2f, 1.2f)), k(300, on())),
                    t("knuckle_a_l", k(0, on()), k(180, scl(1.2f, 1.2f, 1.2f)), k(300, on())),
                    t("knuckle_b_r", k(0, on()), k(180, scl(1.2f, 1.2f, 1.2f)), k(300, on())),
                    t("knuckle_b_l", k(0, on()), k(180, scl(1.2f, 1.2f, 1.2f)), k(300, on()))))
            .on(
                Trigger.HIT,
                100,
                ts(
                    t("fist_r", k(0, tr(0, 0, 0.3f), Ease.EASE_OUT), k(100, on())),
                    t("fist_l", k(0, tr(0, 0, 0.3f), Ease.EASE_OUT), k(100, on()))))
            .build());

    map.put(
        "projectile_launch_system",
        new B()
            .idle(2000, ts(pulse("muzzle", 2000, 1.2f)))
            .on(
                Trigger.LAUNCH,
                300,
                ts(
                    t("hatch", k(0, on()), k(100, tr(0, 0, 3)), k(200, tr(0, 0, 3)), k(300, on())),
                    t("rocket", k(0, hide()), k(100, on()), k(200, tr(0, 0, 2)), k(300, hide())),
                    t("flame_1", k(0, hide()), k(100, on()), k(150, hide())),
                    t("flame_2", k(0, hide()), k(150, on()), k(200, hide())),
                    t("flame_3", k(0, hide()), k(200, on()), k(250, hide()))))
            .build());

    map.put(
        "kerenzikov",
        new B()
            .idle(2000, ts(pulse("node_l", 2000, 1.08f), pulse("node_r", 2000, 1.08f)))
            .active(
                500,
                ts(
                    t("node_l", k(0, tr(0.2f, 0, 0)), k(125, tr(-0.2f, 0, 0)), k(250, tr(0.2f, 0, 0)), k(375, tr(-0.2f, 0, 0)), k(500, tr(0.2f, 0, 0))),
                    t("node_r", k(0, tr(-0.2f, 0, 0)), k(125, tr(0.2f, 0, 0)), k(250, tr(-0.2f, 0, 0)), k(375, tr(0.2f, 0, 0)), k(500, tr(-0.2f, 0, 0))),
                    t("ring_l", k(0, full(0, 0, 0, 0, 0, 0, 0.5f, 0.5f, 0.5f, 0.4f), Ease.EASE_OUT), k(500, full(0, 0, 0, 0, 0, 0, 2.2f, 2.2f, 2.2f, 0), Ease.EASE_OUT)),
                    t("ring_r", k(0, full(0, 0, 0, 0, 0, 0, 0.5f, 0.5f, 0.5f, 0.4f), Ease.EASE_OUT), k(500, full(0, 0, 0, 0, 0, 0, 2.2f, 2.2f, 2.2f, 0), Ease.EASE_OUT))))
            .build());

    map.put(
        "reflex_synaptic_accelerator",
        new B()
            .idle(
                2400,
                ts(
                    t("filament_1", k(0, on()), k(800, scl(0.7f, 0.7f, 0.7f))),
                    t("filament_2", k(0, hide()), k(800, on()), k(1600, scl(0.7f, 0.7f, 0.7f))),
                    t("filament_3", k(0, hide()), k(1600, on()))))
            .on(
                Trigger.SURGE,
                450,
                ts(
                    t("filament_1", k(0, on()), k(150, hide()), k(300, hide())),
                    t("filament_2", k(0, hide()), k(150, on()), k(300, hide())),
                    t("filament_3", k(0, hide()), k(300, on()), k(450, hide())),
                    t("ring", k(0, full(0, 0, 0, 0, 0, 0, 0.5f, 0.5f, 0.5f, 0.5f), Ease.EASE_OUT), k(450, full(0, 0, 0, 0, 0, 0, 1.8f, 1.8f, 1.8f, 0), Ease.EASE_OUT))))
            .build());

    map.put(
        "maneuvering_system",
        new B()
            .idle(3000, ts())
            .on(
                Trigger.BOOST,
                300,
                ts(
                    t("thruster_l", k(0, on(), Ease.EASE_OUT), k(60, rot(30, 0, 0), Ease.EASE_OUT), k(150, rot(30, 0, 0), Ease.EASE_OUT), k(300, on())),
                    t("thruster_r", k(0, on(), Ease.EASE_OUT), k(60, rot(30, 0, 0), Ease.EASE_OUT), k(150, rot(30, 0, 0), Ease.EASE_OUT), k(300, on())),
                    t("flame_l", k(0, hide()), k(50, on()), k(100, hide()), k(150, on()), k(200, hide()), k(250, on()), k(300, hide())),
                    t("flame_r", k(0, hide()), k(50, on()), k(100, hide()), k(150, on()), k(200, hide()), k(250, on()), k(300, hide()))))
            .build());

    map.put(
        "adrenaline_converter",
        new B()
            .idle(
                3000,
                ts(
                    t("liquid_1", k(0, on(), Ease.SMOOTH), k(750, scl(1.05f, 1.05f, 1.05f), Ease.SMOOTH), k(1500, on(), Ease.SMOOTH), k(3000, on(), Ease.SMOOTH)),
                    t("liquid_4", k(0, on(), Ease.SMOOTH), k(2250, scl(1.05f, 1.05f, 1.05f), Ease.SMOOTH), k(3000, on(), Ease.SMOOTH))))
            .on(
                Trigger.INJECT,
                400,
                ts(
                    t("liquid_4", k(0, on()), k(80, hide())),
                    t("liquid_3", k(0, on()), k(160, hide())),
                    t("liquid_2", k(0, on()), k(240, hide())),
                    t("liquid_1", k(0, on()), k(320, hide())),
                    t("cap", k(0, on(), Ease.EASE_OUT), k(80, scl(1.1f, 1.1f, 1.1f), Ease.EASE_OUT), k(200, on()))))
            .on(
                Trigger.REFILL,
                60000,
                ts(
                    t("liquid_1", k(0, hide(), Ease.LINEAR), k(15000, on(), Ease.LINEAR)),
                    t("liquid_2", k(0, hide(), Ease.LINEAR), k(15000, hide(), Ease.LINEAR), k(30000, on(), Ease.LINEAR)),
                    t("liquid_3", k(0, hide(), Ease.LINEAR), k(30000, hide(), Ease.LINEAR), k(45000, on(), Ease.LINEAR)),
                    t("liquid_4", k(0, hide(), Ease.LINEAR), k(45000, hide(), Ease.LINEAR), k(60000, on(), Ease.LINEAR))))
            .build());

    map.put(
        "second_heart",
        new B()
            .idle(
                800,
                ts(
                    t("heart", k(0, on(), Ease.EASE_OUT), k(200, scl(1.04f, 1.04f, 1.04f), Ease.EASE_OUT), k(400, on())),
                    t("core", k(0, on(), Ease.EASE_OUT), k(200, scl(1.05f, 1.05f, 1.05f), Ease.EASE_OUT), k(400, on()))))
            .on(
                Trigger.REVIVE,
                600,
                ts(
                    t("core", k(0, on(), Ease.EASE_OUT), k(600, scl(2.6f, 2.6f, 2.6f), Ease.EASE_OUT)),
                    t("flash", k(0, alp(0.9f), Ease.EASE_OUT), k(600, alp(0), Ease.EASE_OUT)),
                    t("heart", k(0, on(), Ease.EASE_OUT), k(100, scl(1.15f, 1.15f, 1.15f), Ease.EASE_OUT), k(400, on()))))
            .build());

    map.put(
        "biomonitor",
        new B()
            .idle(
                2000,
                ts(
                    t("bar_1", k(0, on()), k(500, hide())),
                    t("bar_2", k(0, hide()), k(500, on()), k(1000, hide())),
                    t("bar_3", k(0, hide()), k(1000, on()), k(1500, hide())),
                    t("bar_4", k(0, hide()), k(1500, on()), k(2000, hide()))))
            .active(
                500,
                ts(
                    t("cross", k(0, on()), k(250, hide()), k(500, on())),
                    t("bar_1", k(0, scl(1, 0.6f, 1))),
                    t("bar_2", k(0, scl(1, 0.6f, 1))),
                    t("bar_3", k(0, scl(1, 0.6f, 1))),
                    t("bar_4", k(0, scl(1, 0.6f, 1)))))
            .on(
                Trigger.INJECT,
                600,
                ts(
                    t("heal", k(0, on()), k(400, hide()), k(600, hide())),
                    t("bar_1", k(0, on()), k(600, on())),
                    t("bar_2", k(0, on()), k(600, on())),
                    t("bar_3", k(0, on()), k(600, on())),
                    t("bar_4", k(0, on()), k(600, on()))))
            .build());

    map.put(
        "blood_pump",
        new B()
            .idle(
                1600,
                ts(
                    t("flow_1", k(0, on()), k(400, hide())),
                    t("flow_2", k(0, hide()), k(400, on()), k(800, hide())),
                    t("flow_3", k(0, hide()), k(800, on()), k(1200, hide())),
                    t("flow_4", k(0, hide()), k(1200, on()), k(1600, hide()))))
            .on(
                Trigger.KILL,
                150,
                ts(
                    t("pump", k(0, on()), k(50, scl(1.15f, 1.15f, 1.15f)), k(100, on())),
                    t("pulse", k(0, on()), k(50, scl(1.2f, 1.2f, 1.2f)), k(150, on()))))
            .on(Trigger.DAMAGE, "surge", 150, ts(t("pump", k(0, on()), k(50, scl(1.15f, 1.15f, 1.15f)), k(100, on()))))
            .build());

    map.put(
        "heal_on_kill",
        new B()
            .idle(2400, ts(pulse("canister", 2400, 1.05f)))
            .on(
                Trigger.INJECT,
                300,
                ts(
                    t("needle", k(0, on(), Ease.EASE_OUT), k(80, tr(0, 0, -1), Ease.EASE_OUT), k(180, on())),
                    t("glow", k(0, hide()), k(80, on()), k(200, hide())),
                    t("injector", k(0, on()), k(80, scl(1.06f, 1.06f, 1.06f)), k(300, on()))))
            .build());

    map.put(
        "syn_lungs_bioplastic",
        new B()
            .idle(
                4000,
                ts(
                    t("lung_l", k(0, on(), Ease.SMOOTH), k(2000, scl(1.06f, 1.02f, 1.04f), Ease.SMOOTH), k(4000, on(), Ease.SMOOTH)),
                    t("lung_r", k(0, on(), Ease.SMOOTH), k(2000, scl(1.06f, 1.02f, 1.04f), Ease.SMOOTH), k(4000, on(), Ease.SMOOTH))))
            .on(
                Trigger.KILL,
                300,
                ts(
                    t("vein_l", k(0, on()), k(150, scl(1.1f, 1.1f, 1.1f)), k(300, on())),
                    t("vein_r", k(0, on()), k(150, scl(1.1f, 1.1f, 1.1f)), k(300, on())),
                    t("lung_l", k(0, on()), k(100, scl(1.06f, 1.06f, 1.06f)), k(300, on())),
                    t("lung_r", k(0, on()), k(100, scl(1.06f, 1.06f, 1.06f)), k(300, on()))))
            .build());

    map.put(
        "subdermal_armor",
        new B()
            .idle(3000, ts())
            .on(
                Trigger.IMPACT,
                180,
                ts(
                    t("plate_chest", k(0, tr(0, 0, 0.18f), Ease.EASE_OUT), k(180, on())),
                    t("plate_back", k(0, tr(0, 0, -0.18f), Ease.EASE_OUT), k(180, on())),
                    platePair("plate_shoulder"),
                    platePair("plate_arm"),
                    platePair("plate_leg")))
            .build());

    map.put(
        "optical_camo",
        new B()
            .idle(3000, ts(pulse("emitter", 3000, 1.15f)))
            .active(
                1000,
                ts(
                    camoShell("shell_head", 0.32f),
                    camoShell("shell_body", 0.32f),
                    camoShell("shell_arm_r", 0.32f),
                    camoShell("shell_arm_l", 0.32f),
                    camoShell("shell_leg_r", 0.32f),
                    camoShell("shell_leg_l", 0.32f)))
            .on(
                Trigger.FADE_IN,
                500,
                ts(
                    camoFade("shell_head", 120),
                    camoFade("shell_body", 200),
                    camoFade("shell_arm_r", 280),
                    camoFade("shell_arm_l", 280),
                    camoFade("shell_leg_r", 280),
                    camoFade("shell_leg_l", 280)))
            .on(
                Trigger.FADE_OUT,
                350,
                ts(
                    camoOut("shell_leg_r", 120),
                    camoOut("shell_leg_l", 120),
                    camoOut("shell_arm_r", 120),
                    camoOut("shell_arm_l", 120),
                    camoOut("shell_body", 250),
                    camoOut("shell_head", 350)))
            .build());

    map.put(
        "pain_editor",
        new B()
            .idle(3000, ts(pulse("glow", 3000, 1.06f)))
            .on(
                Trigger.SUPPRESS,
                250,
                ts(
                    t("clamp_1", k(0, tr(0.1f, 0, 0), Ease.EASE_OUT), k(250, on())),
                    t("clamp_2", k(0, tr(0.1f, 0, 0), Ease.EASE_OUT), k(250, on())),
                    t("clamp_3", k(0, tr(0.1f, 0, 0), Ease.EASE_OUT), k(250, on())),
                    t("glow", k(0, scl(1.15f, 1.15f, 1.15f), Ease.EASE_OUT), k(250, on()))))
            .build());

    map.put(
        "shock_n_awe",
        new B()
            .idle(
                2000,
                ts(
                    t("coil_l", k(0, on(), Ease.SMOOTH), k(1000, scl(1.08f, 1.08f, 1.08f), Ease.SMOOTH), k(2000, on(), Ease.SMOOTH)),
                    t("coil_r", k(0, on(), Ease.SMOOTH), k(1000, scl(1.08f, 1.08f, 1.08f), Ease.SMOOTH), k(2000, on(), Ease.SMOOTH))))
            .on(
                Trigger.DISCHARGE,
                400,
                ts(
                    arc("arc_1", 0),
                    arc("arc_2", 120),
                    arc("arc_3", 240),
                    arc("arc_4", 0),
                    t("plate_l", k(0, tr(-0.15f, 0, 0), Ease.EASE_OUT), k(300, on())),
                    t("plate_r", k(0, tr(0.15f, 0, 0), Ease.EASE_OUT), k(300, on())),
                    t("coil_l", k(0, scl(1.2f, 1.2f, 1.2f)), k(200, on())),
                    t("coil_r", k(0, scl(1.2f, 1.2f, 1.2f)), k(200, on()))))
            .build());

    map.put(
        "fireproof_grounding",
        new B()
            .idle(3000, ts())
            .active(
                2000,
                ts(
                    heat("heat_1", 0),
                    heat("heat_2", 130),
                    heat("heat_3", 260),
                    t("coat", k(0, on(), Ease.SMOOTH), k(1000, scl(1.01f, 1.01f, 1.01f), Ease.SMOOTH), k(2000, on(), Ease.SMOOTH))))
            .on(
                Trigger.GROUND,
                400,
                ts(
                    t("ground_l", k(0, scl(1.15f, 1.15f, 1.15f), Ease.EASE_OUT), k(400, on())),
                    t("ground_r", k(0, scl(1.15f, 1.15f, 1.15f), Ease.EASE_OUT), k(400, on())),
                    t("coat", k(0, scl(1.05f, 1.05f, 1.05f), Ease.EASE_OUT), k(400, on())),
                    t("heat_1", k(0, hide())),
                    t("heat_2", k(0, hide())),
                    t("heat_3", k(0, hide()))))
            .build());

    map.put(
        "titanium_bones",
        new B()
            .idle(
                3000,
                ts(
                    glint("glint_arm_r"),
                    glint("glint_arm_l"),
                    glint("glint_leg_r"),
                    glint("glint_leg_l")))
            .on(
                Trigger.BRACE,
                200,
                ts(
                    t("strip_arm_r", k(0, scl(1.02f, 1.02f, 1.02f), Ease.EASE_OUT), k(200, on())),
                    t("strip_arm_l", k(0, scl(1.02f, 1.02f, 1.02f), Ease.EASE_OUT), k(200, on())),
                    t("strip_leg_r", k(0, scl(1.02f, 1.02f, 1.02f), Ease.EASE_OUT), k(200, on())),
                    t("strip_leg_l", k(0, scl(1.02f, 1.02f, 1.02f), Ease.EASE_OUT), k(200, on()))))
            .on(Trigger.LAND, "land_brace", 200, ts(t("strip_arm_r", k(0, scl(1.02f, 1.02f, 1.02f)), k(200, on())), t("strip_leg_r", k(0, scl(1.02f, 1.02f, 1.02f)), k(200, on()))))
            .build());

    map.put(
        "dense_marrow",
        new B()
            .idle(
                2000,
                ts(
                    pulse("fluid_arm_r", 2000, 1.05f),
                    pulse("fluid_arm_l", 2000, 1.05f),
                    pulse("fluid_leg_r", 2000, 1.05f),
                    pulse("fluid_leg_l", 2000, 1.05f)))
            .on(
                Trigger.IMPACT,
                250,
                ts(
                    compress("cyl_arm_r"),
                    compress("cyl_arm_l"),
                    compress("cyl_leg_r"),
                    compress("cyl_leg_l"),
                    t("fluid_arm_r", k(0, scl(1.1f, 1.1f, 1.1f)), k(120, on())),
                    t("fluid_arm_l", k(0, scl(1.1f, 1.1f, 1.1f)), k(120, on())),
                    t("fluid_leg_r", k(0, scl(1.1f, 1.1f, 1.1f)), k(120, on())),
                    t("fluid_leg_l", k(0, scl(1.1f, 1.1f, 1.1f)), k(120, on()))))
            .on(Trigger.LAND, "land_impact", 250, ts(compress("cyl_arm_r"), compress("cyl_arm_l"), compress("cyl_leg_r"), compress("cyl_leg_l")))
            .build());

    map.put(
        "epimorphic_skeleton",
        new B()
            .idle(
                4000,
                ts(
                    rib("rib_r_1", 0.8f),
                    rib("rib_r_2", 0.8f),
                    rib("rib_r_3", 0.8f),
                    rib("rib_l_1", -0.8f),
                    rib("rib_l_2", -0.8f),
                    rib("rib_l_3", -0.8f),
                    t("sternum", k(0, on(), Ease.SMOOTH), k(2000, scl(1.01f, 1.01f, 1.01f), Ease.SMOOTH), k(4000, on(), Ease.SMOOTH))))
            .on(
                Trigger.BRACE,
                300,
                ts(
                    t("rib_r_1", k(0, rot(0, 0, -1.2f), Ease.EASE_OUT), k(150, rot(0, 0, 1.5f), Ease.EASE_OUT), k(300, on())),
                    t("rib_r_2", k(0, rot(0, 0, -1.2f), Ease.EASE_OUT), k(150, rot(0, 0, 1.5f), Ease.EASE_OUT), k(300, on())),
                    t("rib_r_3", k(0, rot(0, 0, -1.2f), Ease.EASE_OUT), k(150, rot(0, 0, 1.5f), Ease.EASE_OUT), k(300, on())),
                    t("rib_l_1", k(0, rot(0, 0, 1.2f), Ease.EASE_OUT), k(150, rot(0, 0, -1.5f), Ease.EASE_OUT), k(300, on())),
                    t("rib_l_2", k(0, rot(0, 0, 1.2f), Ease.EASE_OUT), k(150, rot(0, 0, -1.5f), Ease.EASE_OUT), k(300, on())),
                    t("rib_l_3", k(0, rot(0, 0, 1.2f), Ease.EASE_OUT), k(150, rot(0, 0, -1.5f), Ease.EASE_OUT), k(300, on())),
                    t("glow", k(0, on()), k(200, hide()))))
            .build());

    map.put(
        "bionic_joints",
        new B()
            .idle(3000, ts())
            .on(
                Trigger.LAND,
                150,
                ts(
                    t("joint_arm_r", k(0, on()), k(150, rot(0, 0, 90))),
                    t("joint_arm_l", k(0, on()), k(150, rot(0, 0, 90))),
                    t("joint_leg_r", k(0, on()), k(150, rot(0, 0, 90))),
                    t("joint_leg_l", k(0, on()), k(150, rot(0, 0, 90)))))
            .build());

    map.put(
        "spring_joints",
        new B()
            .idle(2000, ts(pulse("coil_r", 2000, 1.03f), pulse("coil_l", 2000, 1.03f)))
            .on(
                Trigger.JUMP,
                220,
                ts(
                    compress("spring_r", 0.7f, 150),
                    compress("spring_l", 0.7f, 150),
                    t("accent_r", k(0, scl(1.2f, 1.2f, 1.2f)), k(150, on())),
                    t("accent_l", k(0, scl(1.2f, 1.2f, 1.2f)), k(150, on()))))
            .on(
                Trigger.LAND,
                250,
                ts(
                    t("spring_r", k(0, scl(1, 0.72f, 1), Ease.EASE_OUT), k(150, scl(1, 1.08f, 1), Ease.EASE_OUT), k(250, on())),
                    t("spring_l", k(0, scl(1, 0.72f, 1), Ease.EASE_OUT), k(150, scl(1, 1.08f, 1), Ease.EASE_OUT), k(250, on()))))
            .build());

    map.put(
        "cyber_legs",
        new B()
            .idle(2000, ts(pulse("piston_r", 2000, 1.02f), pulse("piston_l", 2000, 1.02f)))
            .on(
                Trigger.JUMP,
                200,
                ts(
                    t("piston_r", k(0, on(), Ease.EASE_OUT), k(120, scl(1, 1.3f, 1), Ease.EASE_OUT), k(200, on())),
                    t("piston_l", k(0, on(), Ease.EASE_OUT), k(120, scl(1, 1.3f, 1), Ease.EASE_OUT), k(200, on())),
                    t("knee_r", k(0, scl(1.1f, 1.1f, 1.1f)), k(120, on())),
                    t("knee_l", k(0, scl(1.1f, 1.1f, 1.1f)), k(120, on()))))
            .on(
                Trigger.LAND,
                220,
                ts(
                    t("piston_r", k(0, scl(1, 0.8f, 1), Ease.EASE_OUT), k(220, on())),
                    t("piston_l", k(0, scl(1, 0.8f, 1), Ease.EASE_OUT), k(220, on())),
                    t("shin_r", k(0, rot(3, 0, 0), Ease.EASE_OUT), k(220, on())),
                    t("shin_l", k(0, rot(3, 0, 0), Ease.EASE_OUT), k(220, on()))))
            .build());

    map.put(
        "reinforced_tendons",
        new B()
            .idle(2000, ts(pulse("tendon_r", 2000, 1.03f), pulse("tendon_l", 2000, 1.03f)))
            .on(
                Trigger.BOOST,
                300,
                ts(
                    t("tendon_r", k(0, on(), Ease.EASE_OUT), k(120, scl(1, 1.4f, 1), Ease.EASE_OUT), k(300, on())),
                    t("tendon_l", k(0, on(), Ease.EASE_OUT), k(120, scl(1, 1.4f, 1), Ease.EASE_OUT), k(300, on())),
                    t("string_r", k(0, scl(1.2f, 1.2f, 1.2f)), k(150, on())),
                    t("string_l", k(0, scl(1.2f, 1.2f, 1.2f)), k(150, on())),
                    t("glow_r", k(0, on()), k(150, hide())),
                    t("glow_l", k(0, on()), k(150, hide()))))
            .build());

    map.put(
        "fortified_ankles",
        new B()
            .idle(3000, ts())
            .on(
                Trigger.LAND,
                260,
                ts(
                    t("piston_r", k(0, scl(1, 0.8f, 1), Ease.EASE_OUT), k(150, scl(1, 1.04f, 1), Ease.EASE_OUT), k(260, on())),
                    t("piston_l", k(0, scl(1, 0.8f, 1), Ease.EASE_OUT), k(150, scl(1, 1.04f, 1), Ease.EASE_OUT), k(260, on())),
                    t("sole_r", k(0, tr(0.2f, 0, 0), Ease.EASE_OUT), k(260, on())),
                    t("sole_l", k(0, tr(-0.2f, 0, 0), Ease.EASE_OUT), k(260, on())),
                    t("ankle_r", k(0, rot(-4, 0, 0), Ease.EASE_OUT), k(260, on())),
                    t("ankle_l", k(0, rot(-4, 0, 0), Ease.EASE_OUT), k(260, on()))))
            .build());

    map.put(
        "lynx_paws",
        new B()
            .idle(
                800,
                ts(
                    toe("toe_1_r", 0),
                    toe("toe_1_l", 0),
                    toe("toe_2_r", 200),
                    toe("toe_2_l", 200),
                    toe("toe_3_r", 400),
                    toe("toe_3_l", 400)))
            .active(1000, ts(t("pad_r", k(0, scl(1, 0.9f, 1))), t("pad_l", k(0, scl(1, 0.9f, 1)))))
            .build());

    map.put(
        "jenkins_tendons",
        new B()
            .idle(3000, ts())
            .active(
                600,
                ts(
                    pulse("pulse_1_r", 600, 1.4f),
                    pulse("pulse_1_l", 600, 1.4f),
                    t("pulse_2_r", k(0, hide()), k(150, on())),
                    t("pulse_2_l", k(0, hide()), k(150, on())),
                    t("pulse_3_r", k(0, hide()), k(300, on())),
                    t("pulse_3_l", k(0, hide()), k(300, on())),
                    t("pulse_4_r", k(0, hide()), k(450, on())),
                    t("pulse_4_l", k(0, hide()), k(450, on())),
                    t("anchor_r", k(0, scl(1.1f, 1.1f, 1.1f)), k(300, on())),
                    t("anchor_l", k(0, scl(1.1f, 1.1f, 1.1f)), k(300, on()))))
            .build());

    map.put(
        "monowire",
        new B()
            .idle(1000, ts(pulse("housing", 1000, 1.06f)))
            .on(
                Trigger.WHIP,
                500,
                ts(
                    wire("wire_1", 1),
                    wire("wire_2", 2),
                    wire("wire_3", 3),
                    wire("wire_4", 4),
                    wire("wire_5", 5),
                    wire("wire_6", 6),
                    wire("wire_7", 7),
                    wire("wire_8", 8)))
            .build());

    return Map.copyOf(map);
  }

  private static Track lift(String bone, int offset) {
    return t(
        bone,
        k(offset, on()),
        k(offset + 60, full(0, -1, 1, 8, 0, 0, 1, 1, 1, 1), Ease.EASE_OUT),
        k(720, full(0, -1, 1, 8, 0, 0, 1, 1, 1, 1)),
        k(840, on()));
  }

  private static Track fan(String bone) {
    return t(
        bone,
        k(0, on()),
        k(250, rot(0, 0, 90)),
        k(500, rot(0, 0, 180)),
        k(750, rot(0, 0, 270)),
        k(1000, rot(0, 0, 360)));
  }

  private static Track grid(String bone, int offset) {
    return t(
        bone,
        k(0, hide()),
        k(offset, on()),
        k(550, hide()),
        k(600 + offset, on()),
        k(1200, hide()));
  }

  private static Track blink(String bone) {
    return t(bone, k(0, on()), k(120, hide()), k(240, on()), k(360, hide()));
  }

  private static List<Track> slash(int direction) {
    return ts(
        t(
            "blade",
            k(0, on(), Ease.EASE_OUT),
            k(120, full(0, 0, -1.5f, 0, 20 * direction, 35 * direction, 1, 1.04f, 1, 1), Ease.EASE_OUT),
            k(250, on())),
        t("edge", k(0, on()), k(120, scl(1.15f, 1.15f, 1.15f)), k(250, on())),
        t("plate", k(0, rot(0, 0, -45), Ease.EASE_OUT), k(120, rot(0, 0, -45 + 8 * direction), Ease.EASE_OUT), k(250, rot(0, 0, -45))));
  }

  private static Track platePair(String base) {
    return t(base + "_r", k(0, scl(0.97f, 0.97f, 0.97f), Ease.EASE_OUT), k(180, on()));
  }

  private static Track camoShell(String bone, float alpha) {
    return t(bone, k(0, alp(alpha)));
  }

  private static Track camoFade(String bone, int arrival) {
    return t(bone, k(0, alp(0), Ease.EASE_OUT), k(arrival, alp(0.32f), Ease.EASE_OUT), k(500, alp(0.32f)));
  }

  private static Track camoOut(String bone, int arrival) {
    return t(bone, k(0, alp(0.32f), Ease.EASE_OUT), k(arrival, alp(0)));
  }

  private static Track arc(String bone, int start) {
    return t(bone, k(0, hide()), k(start + 1, on()), k(start + 61, hide()));
  }

  private static Track heat(String bone, int offset) {
    return t(
        bone,
        k(0, hide()),
        k(offset + 1, on()),
        k(offset + 667, tr(0, -1, 0)),
        k(offset + 1333, tr(0, -2, 0)),
        k(2000, hide()));
  }

  private static Track glint(String bone) {
    return t(
        bone,
        k(0, alp(0)),
        k(1000, alp(0.8f)),
        k(2000, alp(0)),
        k(3000, alp(0)));
  }

  private static Track compress(String bone) {
    return t(
        bone,
        k(0, scl(1, 0.85f, 1), Ease.EASE_OUT),
        k(150, scl(1, 1.06f, 1), Ease.EASE_OUT),
        k(250, on()));
  }

  private static Track compress(String bone, float factor, int settle) {
    return t(
        bone,
        k(0, scl(1, factor, 1), Ease.EASE_OUT),
        k(settle, scl(1, 1.08f, 1), Ease.EASE_OUT),
        k(settle + 70, on()));
  }

  private static Track toe(String bone, int offset) {
    return t(
        bone,
        k(0, hide()),
        k(offset + 1, on()),
        k(offset + 200, scl(0.9f, 1, 1)),
        k(800, hide()));
  }

  private static Track wire(String bone, int index) {
    int peak = 40 * index;
    return t(
        bone,
        k(0, on(), Ease.EASE_OUT),
        k(peak, rot(0, 22.5f * index, 0), Ease.EASE_OUT),
        k(320, rot(0, 22.5f * index, 0), Ease.EASE_OUT),
        k(500, on()));
  }

  private static Track rib(String bone, float angle) {
    return t(
        bone,
        k(0, on(), Ease.SMOOTH),
        k(2000, rot(0, 0, angle), Ease.SMOOTH),
        k(4000, on(), Ease.SMOOTH));
  }

  private static Track pulse(String bone, int duration, float scale) {
    int mid = duration / 2;
    return t(
        bone,
        k(0, on(), Ease.SMOOTH),
        k(mid, scl(scale, scale, scale), Ease.SMOOTH),
        k(duration, on(), Ease.SMOOTH));
  }

  private static Track t(String bone, Key... keys) {
    return new Track(bone, List.of(keys));
  }

  private static List<Track> ts(Object... entries) {
    List<Track> out = new ArrayList<>();
    for (Object entry : entries) {
      if (entry instanceof Track track) out.add(track);
      else if (entry instanceof Track[] group) out.addAll(Arrays.asList(group));
      else throw new IllegalArgumentException("not a track: " + entry);
    }
    return List.copyOf(out);
  }

  private static Key k(int time, Pose pose) {
    return new Key(time, pose, Ease.STEP);
  }

  private static Key k(int time, Pose pose, Ease ease) {
    return new Key(time, pose, ease);
  }

  private static Pose on() {
    return Pose.REST;
  }

  private static Pose hide() {
    return new Pose(0, 0, 0, 0, 0, 0, 0.01f, 0.01f, 0.01f, 1);
  }

  private static Pose tr(float x, float y, float z) {
    return Pose.translation(x, y, z);
  }

  private static Pose rot(float x, float y, float z) {
    return Pose.rotation(x, y, z);
  }

  private static Pose scl(float x, float y, float z) {
    return Pose.scale(x, y, z);
  }

  private static Pose alp(float alpha) {
    return Pose.alpha(alpha);
  }

  private static Pose full(
      float tx,
      float ty,
      float tz,
      float rx,
      float ry,
      float rz,
      float sx,
      float sy,
      float sz,
      float alpha) {
    return new Pose(tx, ty, tz, rx, ry, rz, sx, sy, sz, alpha);
  }

  private static final class B {
    private Clip idle;
    private Clip active;
    private final Map<Trigger, Clip> triggers = new EnumMap<>(Trigger.class);

    B idle(int duration, List<Track> tracks) {
      idle = Clip.idle("idle", duration, tracks);
      return this;
    }

    B active(int duration, List<Track> tracks) {
      active = Clip.active("active", duration, tracks);
      return this;
    }

    B on(Trigger trigger, int duration, List<Track> tracks) {
      return on(trigger, trigger.name().toLowerCase(Locale.ROOT), duration, tracks);
    }

    B on(Trigger trigger, String id, int duration, List<Track> tracks) {
      triggers.put(trigger, Clip.of(id, duration, Loop.ONCE, tracks));
      return this;
    }

    AnimationSet build() {
      return new AnimationSet(idle, active, triggers);
    }
  }
}
