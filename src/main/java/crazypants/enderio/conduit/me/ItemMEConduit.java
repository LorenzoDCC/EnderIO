package crazypants.enderio.conduit.me;

import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractItemConduit;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;

public class ItemMEConduit extends AbstractItemConduit {

  private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
      new ItemConduitSubtype(ModObject.itemMEConduit.name(), "enderio:itemMeConduit"),
      new ItemConduitSubtype(ModObject.itemMEConduit.name() + "Dense", "enderio:itemMeConduitDense")
  };

  public static ItemMEConduit create() {
    ItemMEConduit result = new ItemMEConduit();
    if (MEUtil.isMEEnabled()) {
      result.init(subtypes);
    }
    return result;
  }

  protected ItemMEConduit() {
    super(ModObject.itemMEConduit);
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IMEConduit.class;
  }

  @Override
  public IConduit createConduit(ItemStack item) {
    return new MEConduit(item.getItemDamage());
  }
}