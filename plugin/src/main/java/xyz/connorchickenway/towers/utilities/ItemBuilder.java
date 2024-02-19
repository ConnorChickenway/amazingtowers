package xyz.connorchickenway.towers.utilities;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.connorchickenway.towers.nms.NMSVersion;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private final ItemStack itemStack;

    private ItemBuilder(ItemBuilder itemBuilder) {
        this.itemStack = itemBuilder.itemStack.clone();
    }

    private ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
    }

    public ItemBuilder setDisplayName(String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(StringUtils.color(name));
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemBuilder setData(byte data) {
        if (NMSVersion.isNewerVersion) return this;
        if (itemStack.getType().getMaxDurability() != 0) return this;
        itemStack.setDurability(data);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        List<String> loreList = new ArrayList<>();
        for (String l : lore)
            loreList.add(StringUtils.color(l));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(loreList);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        ItemMeta meta = itemStack.getItemMeta();
        List<String> loreMeta = meta.getLore();
        if (loreMeta != null) {
            List<String> list = new ArrayList<>(loreMeta);
            for (String l : lore)
                list.add(StringUtils.color(l));
            meta.setLore(list);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder addLore(int index, String lore) {
        final String line = StringUtils.color(lore);
        ItemMeta meta = itemStack.getItemMeta();
        List<String> loreMeta = meta.getLore();
        if (loreMeta != null) {
            List<String> list = new ArrayList<>(loreMeta);
            list.set(index, line);
            meta.setLore(list);
        } else {
            List<String> list = new ArrayList<>();
            list.add(line);
            meta.setLore(list);
        }
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setGlow() {
        itemStack.addUnsafeEnchantment(Enchantment.LUCK, 1);
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemStack toItemStack() {
        return this.itemStack;
    }

    public ItemBuilder clone() {
        return new ItemBuilder(this);
    }

    public static ItemBuilder of(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    public static ItemBuilder of(Material material) {
        return of(material, 1);
    }

}
