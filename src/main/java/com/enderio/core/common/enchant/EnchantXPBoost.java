package com.enderio.core.common.enchant;

import javax.annotation.Nullable;

import com.enderio.core.EnderCore;
import com.enderio.core.api.common.enchant.IAdvancedEnchant;
import com.enderio.core.common.config.ConfigHandler;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EnchantXPBoost extends Enchantment implements IAdvancedEnchant {
  private static EnchantXPBoost INSTANCE;

  /**
   * Can be null prior to registration, or if disabled
   */
  @Nullable
  public static EnchantXPBoost instance() {
    return INSTANCE;
  }

  private EnchantXPBoost() {
    // The ResourceLocation is mostly irrelevant, it's just a key to retreive the enchantment with
    super(Rarity.UNCOMMON, EnumEnchantmentType.BREAKABLE,  new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
    setRegistryName("xpboost");
  }

  @Override
  public int getMaxEnchantability(int level) {
    return super.getMaxEnchantability(level) + 30;
  }

  @Override
  public int getMinEnchantability(int level) {
    return super.getMinEnchantability(level);
  }

  @Override
  public int getMaxLevel() {
    return 3;
  }

  @Override
  public boolean canApply(ItemStack stack) {
    return type.canEnchantItem(stack.getItem()) && !(stack.getItem() instanceof ItemArmor);
  }

  @Override
  public String getName() {
    return "enchantment.xpboost";
  }

  @Override
  public boolean isAllowedOnBooks() {
    return ConfigHandler.allowXPBoost;
  }

  @Override
  public String[] getTooltipDetails(ItemStack stack) {
    return new String[] { EnderCore.lang.localize("enchantment.xpboost.tooltip", false) };
  }

  public static void register() {
    if(ConfigHandler.allowXPBoost) {
      INSTANCE = new EnchantXPBoost();
      GameRegistry.register(INSTANCE);
      FMLInterModComms.sendMessage("EnderIO", "recipe:enchanter",
          "<enchantment name=\"" + EnderCore.DOMAIN + ":xpboost\" costPerLevel=\"4\">\n<itemStack oreDictionary=\"ingotGold\" number=\"16\"/>\n</enchantment>");      
    }
  }
}
