package com.gmail.justbru00.epic.rename.spikeyutils.inventory;

import com.gmail.justbru00.epic.rename.spikeyutils.I;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class ConfirmInventory extends BaseInventory {
    public ConfirmInventory(Plugin plugin, String title, Player player, Consumer<Boolean> consumer) {
        super(1, plugin, title, player);

        fillInventory(I.getFiller());

        addItem(2, I.getDeny(), (clickType) -> {
            player.closeInventory();
            consumer.accept(false);


        });

        addItem(6, I.getConfirm(), (clickType) -> {
            player.closeInventory();
            consumer.accept(true);

        });
    }
}
