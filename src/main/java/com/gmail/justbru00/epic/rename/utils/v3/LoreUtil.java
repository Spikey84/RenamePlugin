/**
 * @author Justin "JustBru00" Brubaker
 * 
 * This is licensed under the MPL Version 2.0. See license info in LICENSE.txt
 */
package com.gmail.justbru00.epic.rename.utils.v3;

import java.util.ArrayList;
import java.util.List;

import com.gmail.justbru00.epic.rename.spikeyutils.inventory.ConfirmInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.justbru00.epic.rename.enums.v3.EcoMessage;
import com.gmail.justbru00.epic.rename.enums.v3.EpicRenameCommands;
import com.gmail.justbru00.epic.rename.enums.v3.XpMessage;
import com.gmail.justbru00.epic.rename.main.v3.Main;

public class LoreUtil {

	@SuppressWarnings("deprecation")
	public static void setLoreLine(int lineNumber, Player player, String[] args) {
		Debug.send("LoreUtil#setLoreLine() start.");
		StringBuilder builder = new StringBuilder("");

		ItemStack inHand = RenameUtil.getInHand(player);

		// Check Material Permissions
		if (!MaterialPermManager.checkPerms(EpicRenameCommands.SETLORELINE, inHand, player)) {
			Messager.msgPlayer(Main.getMsgFromConfig("setloreline.no_permission_for_material"), player);
			return;
		}

		// Check Blacklist
		if (!Blacklists.checkTextBlacklist(args, player)) {
			Messager.msgPlayer(Main.getMsgFromConfig("setloreline.blacklisted_word_found"), player);
			return;
		}

		// Whoops forgot this in the release
		if (!Blacklists.checkMaterialBlacklist(RenameUtil.getInHand(player).getType(), player)) {
			Messager.msgPlayer(Main.getMsgFromConfig("setloreline.blacklisted_material_found"), player);
			return;
		}
		
		// Check Existing Name Blacklist #81
		if (!Blacklists.checkExistingName(player)) {
			Messager.msgPlayer(Main.getMsgFromConfig("setloreline.blacklisted_existing_name_found"), player);
			return;
		}
		
		// Check Existing Lore Blacklist #81
		if (!Blacklists.checkExistingLore(player)) {
			Messager.msgPlayer(Main.getMsgFromConfig("setloreline.blacklisted_existing_lore_found"), player);
			return;
		}

		// Check FormattingPerms
		if (!FormattingPermManager.checkPerms(EpicRenameCommands.SETLORELINE, args, player)) {
			// FormattingPermManager handles the message.
			return;
		}
		
		lineNumber = lineNumber - 1;

		for (int i = 1; i < args.length; i++) {
			builder.append(args[i] + " ");
		}

		String loreToBeSet = builder.toString().trim();
		Debug.send("Text to set is: " + loreToBeSet);
		
		// Issue #32
		if (!FormattingCodeCounter.checkMinColorCodes(player, loreToBeSet, EpicRenameCommands.SETLORELINE, true)) {
			FormattingCodeCounter.sendMinNotReachedMsg(player, EpicRenameCommands.SETLORELINE);
			return;
		}		
				
		if (!FormattingCodeCounter.checkMaxColorCodes(player, loreToBeSet, EpicRenameCommands.SETLORELINE, true)) {
			FormattingCodeCounter.sendMaxReachedMsg(player, EpicRenameCommands.SETLORELINE);
			return;
		}		
		// End Issue #32
		
		List<String> newLore = new ArrayList<String>();

		loreToBeSet = Messager.color(loreToBeSet);

		Debug.send("Colored args are: " + loreToBeSet);

		ItemMeta im = inHand.getItemMeta();

		if (im.hasLore()) {

			Debug.send("Item has lore");

			List<String> oldLore = im.getLore();

			try {

				oldLore.set(lineNumber, loreToBeSet); // ERROR WILL BE CAUSED IF BIGGER

				newLore = oldLore;
				Debug.send("Line number " + lineNumber + " fits in the current lore.");
			} catch (IndexOutOfBoundsException e) {
				Debug.send("Line number is bigger than current size.");

				// Debug
				if (Main.debug)
					for (String item : oldLore) {
						Debug.send("oldLore has: " + item);
					}

				for (int i = 0; i < oldLore.size(); i++) { // Fill new lore with old stuff
					newLore.add(oldLore.get(i));
				}

				// Debug
				if (Main.debug)
					for (String item : newLore) {
						Debug.send("newLore has: " + item);
					}

				for (int i = oldLore.size() - 1; i < lineNumber; i++) { // Expand new lore to proper size
					newLore.add("");
				}

				newLore.set(lineNumber, loreToBeSet);
			}

			im.setLore(newLore);
			inHand.setItemMeta(im);
			if (Main.USE_NEW_GET_HAND) {
				player.getInventory().setItemInMainHand(inHand);
			} else {
				player.setItemInHand(inHand);
			}
			Messager.msgPlayer(Main.getMsgFromConfig("setloreline.success"), player);

		} else { // Item has no lore
			Debug.send("Item has no lore D:");

			for (int i = 0; i <= lineNumber; i++) {
				newLore.add("");
			}

			Debug.send("New Lore size is: " + newLore.size());

			newLore.set(lineNumber, loreToBeSet);

			im.setLore(newLore);
			inHand.setItemMeta(im);
			if (Main.USE_NEW_GET_HAND) {
				player.getInventory().setItemInMainHand(inHand);
			} else {
				player.setItemInHand(inHand);
			}
			Messager.msgPlayer(Main.getMsgFromConfig("setloreline.success"), player);
		}

	}

	@SuppressWarnings("deprecation")
	/**
	 * Handles the lore command.
	 * 
	 * @param args
	 * @param player
	 */
	public static void loreHandle(String[] args, Player player) {
		if (Blacklists.checkTextBlacklist(args, player)) {
			Debug.send("[LoreUtil] Passed Text Blacklist");
			if (Blacklists.checkMaterialBlacklist(RenameUtil.getInHand(player).getType(), player)) {
				Debug.send("[LoreUtil] Passed Material Blacklist");
				if (Blacklists.checkExistingName(player)) {
					Debug.send("[LoreUtil] Passed Existing Name Blacklist");
					if (Blacklists.checkExistingLore(player)) {
						Debug.send("[LoreUtil] Passed Existing Lore Blacklist");

						if (FormattingPermManager.checkPerms(EpicRenameCommands.LORE, args, player)) {
							Debug.send("[LoreUtil] Passed FormattingPermManager#checkPerms()");

							boolean firstLine = true;
							
							// Issue #32
							for (String line : LoreUtil.buildLoreFromArgs(args, false)) {
							
								if (!FormattingCodeCounter.checkMinColorCodes(player, line, EpicRenameCommands.LORE, firstLine)) {
									FormattingCodeCounter.sendMinNotReachedMsg(player, EpicRenameCommands.LORE);
									return;
								}		
										
								if (!FormattingCodeCounter.checkMaxColorCodes(player, line, EpicRenameCommands.LORE, firstLine)) {
									FormattingCodeCounter.sendMaxReachedMsg(player, EpicRenameCommands.LORE);
									return;
								}		
								firstLine = false;
							}
							Debug.send("[LoreUtil] Passed FormattingCodeCounter min and max");
							// End Issue #32
							
							ItemStack inHand = RenameUtil.getInHand(player);

							if (inHand.getType() != Material.AIR) {
								Debug.send("[LoreUtil] Passed Air check");

								if (MaterialPermManager.checkPerms(EpicRenameCommands.LORE, inHand, player)) {

									EcoMessage ecoStatus = EconomyManager.takeMoney(player, EpicRenameCommands.LORE);

									if (ecoStatus == EcoMessage.TRANSACTION_ERROR) {
										return;
									}
									
									// Add experience cost option #121
									XpMessage xpStatus = XpCostManager.takeXp(player, EpicRenameCommands.LORE);
									
									if (xpStatus == XpMessage.TRANSACTION_ERROR) {
										return;
									}

									ItemStack toLore = inHand.clone();
									ItemMeta toLoreMeta = toLore.getItemMeta();
									toLoreMeta.setLore(LoreUtil.buildLoreFromArgs(args, true));
									toLore.setItemMeta(toLoreMeta);

									if (Main.USE_NEW_GET_HAND) { // Use 1.9+ method
										new ConfirmInventory(Main.plugin, "Set Lore", player, (b) -> {
											if (b) {
												inHand.setAmount(0);
												player.getInventory().setItemInMainHand(toLore);
												Main.cooldownAPI.updateCooldown(player, 6);
												Messager.msgPlayer(Main.getMsgFromConfig("lore.success"), player);
											} else {
												player.sendMessage("Lore not applied.");
											}
										}).open(player);
										return;
									} else { // Use older method.
										new ConfirmInventory(Main.plugin, "Set Lore", player, (b) -> {
											if (b) {
												inHand.setAmount(0);
												player.setItemInHand(toLore);
												Main.cooldownAPI.updateCooldown(player, 6);
												Messager.msgPlayer(Main.getMsgFromConfig("lore.success"), player);
											} else {
												player.sendMessage("Lore not applied.");
											}
										}).open(player);
										return;
									}

								} else {
									Messager.msgPlayer(Main.getMsgFromConfig("lore.no_permission_for_material"),
											player);
									return;
								}
							} else {
								Messager.msgPlayer(Main.getMsgFromConfig("lore.cannot_lore_air"), player);
								return;
							}
						} else {
							// FormattingPermManager handles the message.
							return;
						}
					} else {
						// Existing lore
						Messager.msgPlayer(Main.getMsgFromConfig("lore.blacklisted_existing_lore_found"), player);
						return;
					}
				} else {
					// Existing name
					Messager.msgPlayer(Main.getMsgFromConfig("lore.blacklisted_existing_name_found"), player);
					return;
				}
			} else {
				Messager.msgPlayer(Main.getMsgFromConfig("lore.blacklisted_material_found"), player);
				return;
			}
		} else {
			Messager.msgPlayer(Main.getMsgFromConfig("lore.blacklisted_word_found"), player);
			return;
		}
	}

	/**
	 * Takes the command args and changes them to a ArrayList with multiple lines
	 * and color
	 * 
	 * @param args
	 *            The args you want to change.
	 * @return An ArrayList with line breaks at every '|'
	 */
	public static List<String> buildLoreFromArgs(String[] args, boolean colorOutput) {
		List<String> toBeLore = new ArrayList<String>();

		StringBuilder builder = new StringBuilder("");
		String completeArgs = "";

		for (String item : args) { // Closes #68
			if (Main.getInstance().getConfig().getBoolean("replace_underscores")) {
				item = item.replace("_", " ");
				Debug.send("Replaced the underscores.");
			}
			builder.append(item + " ");
		} // End closes #68

		// Add .trim() to fix ISSUE #135
		completeArgs = builder.toString().trim();

		int lastBreak = 0;

		for (int i = 0; i < completeArgs.length(); i++) {

			char testing = completeArgs.charAt(i);

			if (testing == '|') {
				toBeLore.add(completeArgs.substring(lastBreak, i));
				lastBreak = i;
			}
		}

		toBeLore.add(completeArgs.substring(lastBreak, completeArgs.length()));

		List<String> loreToReturn = new ArrayList<String>();

		for (String item : toBeLore) {
			if (colorOutput) {
				loreToReturn.add(Messager.color(item.replace("|", "")));
			} else {
				loreToReturn.add(item.replace("|", ""));
			}
		}

		return loreToReturn;
	}

}
