package com.cybercraft.menu;

import com.cybercraft.cyberware.*;
import com.cybercraft.equipment.*;
import com.cybercraft.registry.ModMenus;
import java.util.*;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class CyberwareMenu extends AbstractContainerMenu {
  public static final int PAGE_SIZE = 12;
  public final Player player;
  public final List<EquipmentRegistry.Position> positions;
  public final int equipmentSlots;
  public int page;

  public CyberwareMenu(int id, Inventory inventory) {
    this(id, inventory, CyberwareManager.get(inventory.player).recovery().size());
  }

  public CyberwareMenu(int id, Inventory inventory, Integer recoveryCount) {
    super(ModMenus.CYBERWARE, id);
    player = inventory.player;
    positions = EquipmentRegistry.positions(player.level());
    equipmentSlots = positions.size() + recoveryCount;
    Container equipment = new EquipmentContainer(recoveryCount);
    for (int i = 0; i < equipmentSlots; i++) {
      final int n = i;
      int visible = i % PAGE_SIZE;
      addSlot(
          new Slot(equipment, i, visible < 6 ? 12 : 290, 28 + (visible % 6) * 18) {
            @Override
            public boolean mayPlace(ItemStack stack) {
              return n < positions.size()
                  && CyberwareManager.canEquip(
                      player, positions.get(n).type(), positions.get(n).index(), stack);
            }

            @Override
            public int getMaxStackSize() {
              return 1;
            }

            @Override
            public boolean isActive() {
              return n / PAGE_SIZE == page;
            }
          });
    }
    for (int row = 0; row < 3; row++)
      for (int col = 0; col < 9; col++)
        addSlot(new Slot(inventory, col + row * 9 + 9, 79 + col * 18, 152 + row * 18));
    for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 79 + col * 18, 210));
  }

  public int pages() {
    return Math.max(1, (equipmentSlots + PAGE_SIZE - 1) / PAGE_SIZE);
  }

  @Override
  public boolean stillValid(Player player) {
    return player == this.player && player.isAlive();
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
    Slot slot = slots.get(index);
    if (!slot.hasItem()) return ItemStack.EMPTY;
    ItemStack source = slot.getItem();
    ItemStack original = source.copy();
    if (index < equipmentSlots) {
      if (!moveItemStackTo(source, equipmentSlots, slots.size(), true)) return ItemStack.EMPTY;
    } else {
      boolean moved = false;
      for (int i = 0; i < positions.size(); i++)
        if (!slots.get(i).hasItem() && slots.get(i).mayPlace(source)) {
          ItemStack installed = source.copyWithCount(1);
          slots.get(i).setByPlayer(installed);
          source.shrink(1);
          moved = true;
          break;
        }
      if (!moved) {
        int hotbar = equipmentSlots + 27;
        if (index < hotbar) {
          if (!moveItemStackTo(source, hotbar, slots.size(), false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(source, equipmentSlots, hotbar, false)) return ItemStack.EMPTY;
      }
    }
    if (source.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
    else slot.setChanged();
    slot.onTake(player, source);
    return original;
  }

  private final class EquipmentContainer implements Container {
    private final int recoveryCount;

    EquipmentContainer(int recoveryCount) {
      this.recoveryCount = recoveryCount;
    }

    public int getContainerSize() {
      return positions.size() + recoveryCount;
    }

    public boolean isEmpty() {
      for (int i = 0; i < getContainerSize(); i++) if (!getItem(i).isEmpty()) return false;
      return true;
    }

    public ItemStack getItem(int i) {
      var data = CyberwareManager.get(player);
      if (i < positions.size()) {
        var pos = positions.get(i);
        return CyberwareManager.at(data, pos.type(), pos.index())
            .map(InstalledImplant::stack)
            .orElse(ItemStack.EMPTY);
      }
      int r = i - positions.size();
      return r < data.recovery().size() ? data.recovery().get(r) : ItemStack.EMPTY;
    }

    public ItemStack removeItem(int i, int count) {
      ItemStack stack = getItem(i).copy();
      ItemStack taken = stack.split(count);
      setItem(i, stack);
      return taken;
    }

    public ItemStack removeItemNoUpdate(int i) {
      ItemStack item = getItem(i).copy();
      setItem(i, ItemStack.EMPTY);
      return item;
    }

    public void setItem(int i, ItemStack stack) {
      if (i < positions.size()) {
        var pos = positions.get(i);
        if (player.level().isClientSide) {
          var data = CyberwareManager.get(player);
          var items = new ArrayList<>(data.equipment());
          items.removeIf(e -> e.slot().equals(pos.type()) && e.index() == pos.index());
          var def = EquipmentRegistry.find(player.level(), stack);
          def.ifPresent(
              d ->
                  items.add(
                      new InstalledImplant(
                          pos.type(),
                          pos.index(),
                          d.getKey().location(),
                          d.getValue().behavior(),
                          stack)));
          CyberwareManager.set(player, data.withEquipment(items, data.recovery()));
        } else CyberwareManager.equip(player, pos.type(), pos.index(), stack);
      } else {
        var data = CyberwareManager.get(player);
        var items = new ArrayList<>(data.recovery());
        int r = i - positions.size();
        while (items.size() <= r) items.add(ItemStack.EMPTY);
        items.set(r, stack.copy());
        CyberwareManager.set(player, data.withEquipment(data.equipment(), items));
        if (!player.level().isClientSide) CyberwareManager.sendSync(player);
      }
    }

    public void setChanged() {}

    public boolean stillValid(Player p) {
      return CyberwareMenu.this.stillValid(p);
    }

    public void clearContent() {
      for (int i = 0; i < getContainerSize(); i++) setItem(i, ItemStack.EMPTY);
    }
  }
}
