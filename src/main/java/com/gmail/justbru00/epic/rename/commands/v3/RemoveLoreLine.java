/**
 * @author Justin "JustBru00" Brubaker
 * 
 * This is licensed under the MPL Version 2.0. See license info in LICENSE.txt
 */
package com.gmail.justbru00.epic.rename.commands.v3;

import java.util.List;

import com.gmail.justbru00.epic.rename.utils.v3.*;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.justbru00.epic.rename.enums.v3.EpicRenameCommands;
import com.gmail.justbru00.epic.rename.main.v3.Main;

public class RemoveLoreLine implements CommandExecutor {

	int cooldownID = 8;

	public RemoveLoreLine() {
		Main.cooldownAPI.registerCooldown(cooldownID, "removeloreline");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (command.getName().equalsIgnoreCase("removeloreline")) {
			if (sender.hasPermission("epicrename.removeloreline")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;

					if (Main.cooldownAPI.isOnCooldown(player.getUniqueId(), cooldownID) && !player.hasPermission("epicrename.bypasscooldown")) {
						Messager.msgSenderWithConfigMsg("removeloreline.cooldown", sender, CF.getCoolDownTimeInDays(player.getUniqueId(), cooldownID));
						return true;
					}
					if (WorldChecker.checkWorld(player)) {
						if (args.length == 1) {
							
							// Check Material Permissions
							if (!MaterialPermManager.checkPerms(EpicRenameCommands.REMOVELORELINE, RenameUtil.getInHand(player), player)) {
								Messager.msgPlayer(Main.getMsgFromConfig("removeloreline.no_permission_for_material"), player);
								return true;
							}
							
							// Issue #76 | Check Blacklist							
							if (!Blacklists.checkMaterialBlacklist(RenameUtil.getInHand(player).getType(), player)) {
								Messager.msgPlayer(Main.getMsgFromConfig("removeloreline.blacklisted_material_found"), player);
								return true;
							}
							// End Issue #76
							
							// Check Existing Name Blacklist #81
							if (!Blacklists.checkExistingName(player)) {
								Messager.msgPlayer(Main.getMsgFromConfig("removeloreline.blacklisted_existing_name_found"), player);
								return true;
							}
							
							// Check Existing Lore Blacklist #81
							if (!Blacklists.checkExistingLore(player)) {
								Messager.msgPlayer(Main.getMsgFromConfig("removeloreline.blacklisted_existing_lore_found"), player);
								return true;
							}
							
							int lineNumber = -1;

							try {
								lineNumber = Integer.parseInt(args[0]);
							} catch (Exception e) {
								Messager.msgPlayer(Main.getMsgFromConfig("removeloreline.not_an_int"), player);
								return true;
							}
							
							// Issue #80
							if (lineNumber <= 0) {
								Debug.send("[RemoveLoreLine] The number " + lineNumber + " is below or equal to 0.");
								Messager.msgPlayer(Main.getMsgFromConfig("removeloreline.invalid_number"), player);
								return true;
							}
							// End Issue #80

							if (RenameUtil.getInHand(player).hasItemMeta()) {
								if (RenameUtil.getInHand(player).getType() == Material.AIR) {
									Messager.msgPlayer(Main.getMsgFromConfig("removeloreline.cannot_edit_air"), player);
									return true;
								}

								if (RenameUtil.getInHand(player).getItemMeta().hasLore()) {

									List<String> itemLore = RenameUtil.getInHand(player).getItemMeta().getLore();

									if (lineNumber < (itemLore.size() + 1)) { // Line
																				// number
																				// exists
										itemLore.remove((lineNumber - 1));
										ItemMeta im = RenameUtil.getInHand(player).getItemMeta();
										im.setLore(itemLore);
										RenameUtil.getInHand(player).setItemMeta(im);
										Main.cooldownAPI.updateCooldown(player, cooldownID);
										Messager.msgPlayer(Main.getMsgFromConfig("removeloreline.success"), player);
										return true;
									} else {
										Messager.msgPlayer(Main.getMsgFromConfig("removeloreline.out_of_bounds"),
												player);
										return true;
									}

								} else {
									Messager.msgPlayer(Main.getMsgFromConfig("removeloreline.has_no_lore"), player);
									return true;
								}
							}

						} else {
							Messager.msgPlayer(Main.getMsgFromConfig("removeloreline.wrong_args"), player);
							return true;
						}
					} else {
						Messager.msgSender(Main.getMsgFromConfig("removeloreline.disabled_world"), sender);
						return true;
					}
				} else {
					Messager.msgSender(Main.getMsgFromConfig("removeloreline.wrong_sender"), sender);
					return true;
				}
			} else {
				Messager.msgSender(Main.getMsgFromConfig("removeloreline.no_permission"), sender);
				return true;
			}
		}

		return false;
	}

}
