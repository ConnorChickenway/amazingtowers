package xyz.connorchickenway.towers.game.kit;

import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import xyz.connorchickenway.towers.utilities.ItemUtils;

import java.util.HashMap;
import java.util.Map;

public class Kit {

    protected Map<Integer, ItemStack> contents;
    protected ItemStack[] armor;

    public Kit() {
        this.contents = new HashMap<>();
        this.armor = new ItemStack[4];
    }

    public void sendKit(Player player, Color color) {
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        contents.entrySet().forEach(e -> inventory.setItem(e.getKey(), e.getValue()));
        for (int i = 0; i < armor.length; i++) {
            if (armor[i] == null) continue;
            ItemStack itemStack = armor[i].clone();
            if (ItemUtils.isArmorLeather(itemStack.getType())) {
                LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
                meta.setColor(color);
                itemStack.setItemMeta(meta);
            }
            ItemUtils.setArmor(inventory, itemStack);
        }
    }

    public void addItem(int index, ItemStack itemStack) {
        contents.put(index, itemStack);
    }

    public void addArmor(ItemStack[] armor) {
        this.armor = armor;
    }

}
