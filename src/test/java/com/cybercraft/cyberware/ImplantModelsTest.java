package com.cybercraft.cyberware;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ImplantModelsTest {
  private static final int SWATCH_COUNT =
      ImplantModels.Material.values().length * ImplantModels.Tone.values().length;

  @Test
  void everyCyberwareHasAValidModel() {
    for (var implant : Cyberware.values()) {
      var model = ImplantModels.get(implant.id());
      assertNotNull(model, implant.id());
      assertFalse(model.groups().isEmpty(), implant.id());
      for (var group : model.groups()) {
        assertNotNull(group.part(), implant.id());
        assertFalse(group.boxes().isEmpty(), implant.id());
        for (var box : group.boxes()) {
          assertNotNull(box.material(), implant.id());
          assertTrue(box.x0() < box.x1(), implant.id());
          assertTrue(box.y0() < box.y1(), implant.id());
          assertTrue(box.z0() < box.z1(), implant.id());
          int swatch = ImplantModels.swatch(box.material(), ImplantModels.Tone.BASE);
          assertTrue(swatch >= 0 && swatch < SWATCH_COUNT, implant.id() + " swatch " + swatch);
        }
      }
    }
  }

  @Test
  void monowireHasAModel() {
    assertNotNull(ImplantModels.get("monowire"));
  }

  @Test
  void mirroredGroupsAlwaysTargetLeftLimbs() {
    for (var implant : Cyberware.values()) {
      var model = ImplantModels.get(implant.id());
      assertNotNull(model, implant.id());
      for (var group : model.groups())
        if (group.mirror()) assertTrue(ImplantModels.isLeft(group.part()), implant.id());
    }
  }

  @Test
  void paletteUvsStayInsideTheAtlas() {
    for (var material : ImplantModels.Material.values())
      for (var tone : ImplantModels.Tone.values()) {
        int swatch = ImplantModels.swatch(material, tone);
        assertTrue(swatch < SWATCH_COUNT);
        assertTrue(ImplantModels.u0(swatch) >= 0 && ImplantModels.u1(swatch) <= 16);
        assertTrue(ImplantModels.v0(swatch) >= 0 && ImplantModels.v1(swatch) <= 16);
        assertTrue(ImplantModels.u0(swatch) < ImplantModels.u1(swatch));
        assertTrue(ImplantModels.v0(swatch) < ImplantModels.v1(swatch));
      }
  }

  @Test
  void everyModelHasAtLeastOneRenderableGroup() {
    Set<String> ids = new HashSet<>();
    for (var implant : Cyberware.values()) {
      var model = ImplantModels.get(implant.id());
      int boxes = model.groups().stream().mapToInt(g -> g.boxes().size()).sum();
      assertTrue(boxes > 0, implant.id());
      ids.add(implant.id());
    }
    assertEquals(43, ids.size());
  }
}
