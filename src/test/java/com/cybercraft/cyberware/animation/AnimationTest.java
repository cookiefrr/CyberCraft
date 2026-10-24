package com.cybercraft.cyberware.animation;

import static org.junit.jupiter.api.Assertions.*;

import com.cybercraft.cyberware.Cyberware;
import com.cybercraft.cyberware.ImplantModels;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AnimationTest {
  private static Track track(Key... keys) {
    return new Track("bone", List.of(keys));
  }

  @Test
  void trackClampsOutsideItsKeyRange() {
    var track =
        track(Key.at(0, Pose.REST), Key.at(100, Pose.rotation(0, 0, 45)));
    assertEquals(Pose.REST, track.sample(-20));
    assertEquals(0f, track.sample(0).rz());
    assertEquals(45f, track.sample(100).rz());
    assertEquals(45f, track.sample(500).rz());
  }

  @Test
  void trackInterpolatesWithTheFromKeyEase() {
    var linear =
        track(Key.at(0, Pose.REST), Key.at(100, Pose.rotation(0, 0, 90)));
    assertEquals(45f, linear.sample(50).rz(), .001f);
    var stepped =
        track(Key.at(0, Pose.REST, Ease.STEP), Key.at(100, Pose.rotation(0, 0, 90)));
    assertEquals(0f, stepped.sample(99).rz());
    assertEquals(90f, stepped.sample(100).rz());
  }

  @Test
  void easingCurvesHitTheirEndpoints() {
    for (var ease : Ease.values()) {
      assertEquals(0f, ease.apply(0), .001f, ease.name());
      assertEquals(1f, ease.apply(1), .001f, ease.name());
    }
    assertEquals(0f, Ease.STEP.apply(.999f), .001f);
    assertEquals(.5f, Ease.SMOOTH.apply(.5f), .001f);
    assertEquals(.25f, Ease.EASE_IN.apply(.5f), .001f);
    assertEquals(.75f, Ease.EASE_OUT.apply(.5f), .001f);
  }

  @Test
  void loopingClipsWrapAndOneShotsFinish() {
    var looping =
        Clip.idle(
            "idle",
            100,
            List.of(track(Key.at(0, Pose.REST), Key.at(100, Pose.rotation(0, 0, 10)))));
    assertEquals(50, looping.timeAt(150));
    assertEquals(0, looping.timeAt(100));
    assertFalse(looping.finished(5_000));
    var once =
        Clip.of(
            "hit",
            100,
            Loop.ONCE,
            List.of(track(Key.at(0, Pose.REST), Key.at(100, Pose.rotation(0, 0, 10)))));
    assertEquals(100, once.timeAt(250));
    assertTrue(once.finished(100));
    assertFalse(once.finished(99));
  }

  @Test
  void clipPrioritiesKeepIdleUnderActiveUnderTrigger() {
    assertTrue(Clip.PRIORITY_IDLE < Clip.PRIORITY_ACTIVE);
    assertTrue(Clip.PRIORITY_ACTIVE < Clip.PRIORITY_TRIGGER);
    assertEquals(
        Clip.PRIORITY_TRIGGER,
        Clip.of("x", 10, Loop.ONCE, List.of(track(Key.at(0, Pose.REST)))).priority());
    assertEquals(
        Clip.PRIORITY_ACTIVE,
        Clip.active("x", 10, List.of(track(Key.at(0, Pose.REST)))).priority());
    assertEquals(
        Clip.PRIORITY_IDLE,
        Clip.idle("x", 10, List.of(track(Key.at(0, Pose.REST)))).priority());
  }

  @Test
  void sampleAllReturnsOnlyTheClipBones() {
    var clip =
        Clip.of(
            "hit",
            100,
            Loop.ONCE,
            List.of(
                new Track("a", List.of(Key.at(0, Pose.rotation(0, 0, 5)))),
                new Track("b", List.of(Key.at(0, Pose.translation(1, 0, 0))))));
    var poses = clip.sampleAll(0);
    assertEquals(Set.of("a", "b"), poses.keySet());
    assertEquals(5f, poses.get("a").rz());
    assertEquals(1f, poses.get("b").tx());
    assertNull(clip.sample("c", 0));
  }

  @Test
  void everyImplantHasAtLeastOneClip() {
    Set<String> expected = new HashSet<>();
    for (var implant : Cyberware.values()) expected.add(implant.id());
    expected.add("monowire");
    for (String id : expected) {
      var set = ImplantAnimations.get(id);
      assertNotNull(set, id);
      assertTrue(
          set.idle() != null || set.active() != null || !set.triggers().isEmpty(),
          id + " has no clips");
      assertTrue(
          allClips(set).stream().anyMatch(clip -> !clip.tracks().isEmpty()),
          id + " has no animation tracks");
    }
    assertEquals(expected, Set.copyOf(ImplantAnimations.ids()));
  }

  @Test
  void clipsAreWellFormedAndTriggersMatchTheirIds() {
    for (String id : ImplantAnimations.ids()) {
      var set = ImplantAnimations.get(id);
      Set<String> clipIds = new HashSet<>();
      for (var clip : allClips(set)) {
        assertTrue(clip.duration() > 0, id + "/" + clip.id());
        assertTrue(clipIds.add(clip.id()), id + " duplicate clip " + clip.id());
        Set<String> bones = new HashSet<>();
        for (var track : clip.tracks()) {
          assertTrue(bones.add(track.bone()), id + "/" + clip.id() + " duplicate " + track.bone());
          assertFalse(track.keys().isEmpty(), id + "/" + clip.id());
          for (var key : track.keys()) {
            assertTrue(key.time() >= 0, id + "/" + clip.id());
            assertTrue(key.time() <= clip.duration(), id + "/" + clip.id() + " key past duration");
          }
        }
      }
      set.triggers()
          .forEach(
              (trigger, clip) -> {
                assertNotNull(
                    ImplantAnimations.find(set, clip.id()),
                    id + "/" + trigger + " clip is not discoverable by id");
                assertFalse(clip.id().isBlank(), id + "/" + trigger + " has a blank clip id");
              });
    }
  }

  @Test
  void everyTrackTargetsABoneThatExistsInTheModel() {
    for (String id : ImplantAnimations.ids()) {
      var model = ImplantModels.get(id);
      assertNotNull(model, id);
      Set<String> bones = new HashSet<>();
      for (var group : model.groups()) bones.add(group.bone());
      for (var clip : allClips(ImplantAnimations.get(id)))
        for (var track : clip.tracks())
          assertTrue(
              bones.contains(track.bone()),
              id + "/" + clip.id() + " targets missing bone " + track.bone());
    }
  }

  private static List<Clip> allClips(AnimationSet set) {
    var clips = new java.util.ArrayList<Clip>();
    if (set.idle() != null) clips.add(set.idle());
    if (set.active() != null) clips.add(set.active());
    clips.addAll(set.triggers().values());
    return clips;
  }
}
