/**
 * @author Justin "JustBru00" Brubaker
 * 
 * This is licensed under the MPL Version 2.0. See license info in LICENSE.txt
 */
package com.gmail.justbru00.epic.rename.commands.v3;

import com.gmail.justbru00.epic.rename.utils.v3.CF;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.justbru00.epic.rename.main.v3.Main;
import com.gmail.justbru00.epic.rename.utils.v3.LoreUtil;
import com.gmail.justbru00.epic.rename.utils.v3.Messager;
import com.gmail.justbru00.epic.rename.utils.v3.WorldChecker;

public class Lore implements CommandExecutor {

	int cooldownID = 6;

	public Lore() {
		Main.cooldownAPI.registerCooldown(cooldownID, "lore");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (command.getName().equalsIgnoreCase("lore")) { // Start /lore

			if (sender instanceof Player) {

				Player player = (Player) sender;

				if (WorldChecker.checkWorld(player)) {

					if (player.hasPermission("epicrename.lore")) {

						if (Main.cooldownAPI.isOnCooldown(player.getUniqueId(), cooldownID) && !player.hasPermission("epicrename.bypasscooldown")) {
							Messager.msgSenderWithConfigMsg("lore.cooldown", sender, CF.getCoolDownTimeInDays(player.getUniqueId(), cooldownID));
							return true;
						}

						if (args.length >= 1) {

							LoreUtil.loreHandle(args, player);

							return true;
						} else {
							Messager.msgPlayer(Main.getMsgFromConfig("lore.no_args"), player);
							return true;
						}

					} else {
						Messager.msgPlayer(Main.getMsgFromConfig("lore.no_permission"), player);
						return true;
					}

				} else {
					Messager.msgSender(Main.getMsgFromConfig("lore.disabled_world"), sender);
					return true;
				}
			} else {
				Messager.msgSender(Main.getMsgFromConfig("lore.wrong_sender"), sender);
				return true;
			}
		} // End /lore

		return false;
	}
}
