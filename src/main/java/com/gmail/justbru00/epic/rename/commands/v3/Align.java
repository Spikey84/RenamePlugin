package com.gmail.justbru00.epic.rename.commands.v3;

import java.util.ArrayList;
import java.util.List;

import com.gmail.justbru00.epic.rename.utils.v3.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.justbru00.epic.rename.enums.v3.EpicRenameCommands;
import com.gmail.justbru00.epic.rename.main.v3.Main;
import com.gmail.justbru00.epic.rename.utils.v3.align.FormatItem;
import com.gmail.justbru00.epic.rename.utils.v3.align.TableGenerator;
import com.gmail.justbru00.epic.rename.utils.v3.align.TableGenerator.Alignment;
import com.gmail.justbru00.epic.rename.utils.v3.align.TableGenerator.Receiver;

/**
 * 
 * @author Justin "JustBru00" Brubaker
 * This is licensed under the MPL Version 2.0. See license info in LICENSE.txt
 * Created for Issue #86
 *
 */
public class Align implements CommandExecutor {
	
	private static final int ALIGN_LEFT = 1;
	private static final int ALIGN_CENTER = 2;
	private static final int ALIGN_RIGHT = 3;
	int cooldownID = 0;
	
	public static void main(String[] args) {
		// Test the align stuff
		ArrayList<String> text = new ArrayList<String>();
		text.add("             Swords");
		text.add("");
		text.add("Click to see all custom");
		text.add("enchantments for swords!");
		for(String s : alignStrings(text, ALIGN_RIGHT)) {
			System.out.println(s);
		}
	}

	public Align() {
		Main.cooldownAPI.registerCooldown(cooldownID, "align");
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (command.getName().equalsIgnoreCase("align")) {
			if (sender.hasPermission("epicrename.align")) {



				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (Main.cooldownAPI.isOnCooldown(player.getUniqueId(), cooldownID) && !player.hasPermission("epicrename.bypasscooldown")) {
						Messager.msgSenderWithConfigMsg("align.cooldown", sender, CF.getCoolDownTimeInDays(player.getUniqueId(), cooldownID));
						return true;
					}
					
					if (WorldChecker.checkWorld(player)) {
						ItemStack inHand = RenameUtil.getInHand(player);
						Material m = inHand.getType();
						
						// Check Material Permissions
						if (!MaterialPermManager.checkPerms(EpicRenameCommands.ALIGN, inHand, player)) {
							Messager.msgSenderWithConfigMsg("align.no_permission_for_material", sender);
							return true;
						}
						
						// Check Material Blacklist | #76
						if (!Blacklists.checkMaterialBlacklist(m, player)) {
							Messager.msgSenderWithConfigMsg("align.blacklisted_material_found", sender);
							return true;
						}
						
						// Check Existing Name Blacklist | #81
						if (!Blacklists.checkExistingName(player)) {
							Messager.msgSenderWithConfigMsg("align.blacklisted_existing_name_found", player);
							return true;
						}
						
						// Check Existing Lore Blacklist | #81
						if (!Blacklists.checkExistingLore(player)) {
							Messager.msgSenderWithConfigMsg("align.blacklisted_existing_lore_found", player);
							return true;
						}
						
						// Make sure item is not AIR or null
						if (m == Material.AIR || m == null) {
							Messager.msgSenderWithConfigMsg("align.cannot_edit_air", sender);
							return true;
						}
						
						// Example Command Usage: /align name <left || center || right>
						// /align lore <left || center || right> <1,2,3,5>
						// /align lore <left || center || right> <all>
						// /align both <left || center || right> 						
						if (args.length == 0) {
							Messager.msgSenderWithConfigMsg("align.not_enough_args", sender);
							return true;
						}						
						
						if (args.length >= 2) {				
							ArrayList<String> textToAlign = new ArrayList<String>();
							
							if (inHand.getItemMeta().hasDisplayName()) {
								textToAlign.add((inHand.getItemMeta().getDisplayName()).trim().replace("៲", ""));
							} else {								
								textToAlign.add(Messager.color("&r" + new FormatItem(inHand).getName().trim()).replace("៲", ""));								
							}
								
							if (inHand.getItemMeta().hasLore()) {
								for (String s : inHand.getItemMeta().getLore()) {
									textToAlign.add(s.trim().replace("៲", ""));
								}
							} else {
								// Well nothing /shrug
							}
							
							
							
							if (args[0].equalsIgnoreCase("name")) {
								if (args[1].equalsIgnoreCase("left")) {
									Main.cooldownAPI.updateCooldown(player, cooldownID);
									ArrayList<String> aligned = alignStringsClient(textToAlign, ALIGN_LEFT);
									
									ItemMeta meta = inHand.getItemMeta();
									meta.setDisplayName(aligned.get(0));
									
									// Only Name
									/*if (aligned.size() > 1) {
										// Lore in this thing
										List<String> lore = new ArrayList<String>();
										for (int i = 1; i < aligned.size(); i++) {
											lore.add(aligned.get(i));
										}
										meta.setLore(lore);
									} */
									
									inHand.setItemMeta(meta);
									if (Main.USE_NEW_GET_HAND) { // Use 1.9+ method
										player.getInventory().setItemInMainHand(inHand);
									} else { // Use older method.
										player.setItemInHand(inHand);
									}
									Messager.msgSenderWithConfigMsg("align.name_aligned_left_success", sender);
									return true;
								} else if (args[1].equalsIgnoreCase("center")) {
									Main.cooldownAPI.updateCooldown(player, cooldownID);
									ArrayList<String> aligned = alignStringsClient(textToAlign, ALIGN_CENTER);
									
									ItemMeta meta = inHand.getItemMeta();
									meta.setDisplayName(aligned.get(0));
									
									// Only Name
									/*if (aligned.size() > 1) {
										// Lore in this thing
										List<String> lore = new ArrayList<String>();
										for (int i = 1; i < aligned.size(); i++) {
											lore.add(aligned.get(i));
										}
										meta.setLore(lore);
									}*/
									
									inHand.setItemMeta(meta);
									if (Main.USE_NEW_GET_HAND) { // Use 1.9+ method
										player.getInventory().setItemInMainHand(inHand);
									} else { // Use older method.
										player.setItemInHand(inHand);
									}
									Messager.msgSenderWithConfigMsg("align.name_aligned_center_success", sender);
									return true;
								} else if (args[1].equalsIgnoreCase("right")) {
									Main.cooldownAPI.updateCooldown(player, cooldownID);
									ArrayList<String> aligned = alignStringsClient(textToAlign, ALIGN_RIGHT);
									
									ItemMeta meta = inHand.getItemMeta();
									meta.setDisplayName(aligned.get(0));
									
									// Only Name
									/*if (aligned.size() > 1) {
										// Lore in this thing
										List<String> lore = new ArrayList<String>();
										for (int i = 1; i < aligned.size(); i++) {
											lore.add(aligned.get(i));
										}
										meta.setLore(lore);
									}*/
									
									inHand.setItemMeta(meta);
									if (Main.USE_NEW_GET_HAND) { // Use 1.9+ method
										player.getInventory().setItemInMainHand(inHand);
									} else { // Use older method.
										player.setItemInHand(inHand);
									}
									Messager.msgSenderWithConfigMsg("align.name_aligned_right_success", sender);
									return true;									
								} else {
									Messager.msgSenderWithConfigMsg("align.incorrect_name_args", sender);
									return true;
								}
							} else if (args[0].equalsIgnoreCase("lore")) {
								Main.cooldownAPI.updateCooldown(player, cooldownID);
								if (args[1].equalsIgnoreCase("left")) {
									ArrayList<String> aligned = alignStringsClient(textToAlign, ALIGN_LEFT);
									
									ItemMeta meta = inHand.getItemMeta();
									/*meta.setDisplayName(aligned.get(0));*/								
									
									if (aligned.size() > 1) {
										// Lore in this thing
										List<String> lore = new ArrayList<String>();
										for (int i = 1; i < aligned.size(); i++) {
											lore.add(aligned.get(i));
										}
										meta.setLore(lore);
									}
									
									inHand.setItemMeta(meta);
									if (Main.USE_NEW_GET_HAND) { // Use 1.9+ method
										player.getInventory().setItemInMainHand(inHand);
									} else { // Use older method.
										player.setItemInHand(inHand);
									}
									Messager.msgSenderWithConfigMsg("align.lore_aligned_left_success", sender);
									return true;
								} else if (args[1].equalsIgnoreCase("center")) {
									Main.cooldownAPI.updateCooldown(player, cooldownID);
									ArrayList<String> aligned = alignStringsClient(textToAlign, ALIGN_CENTER);
									
									ItemMeta meta = inHand.getItemMeta();
									/*meta.setDisplayName(aligned.get(0));*/								
									
									if (aligned.size() > 1) {
										// Lore in this thing
										List<String> lore = new ArrayList<String>();
										for (int i = 1; i < aligned.size(); i++) {
											lore.add(aligned.get(i));
										}
										meta.setLore(lore);
									}
									
									inHand.setItemMeta(meta);
									if (Main.USE_NEW_GET_HAND) { // Use 1.9+ method
										player.getInventory().setItemInMainHand(inHand);
									} else { // Use older method.
										player.setItemInHand(inHand);
									}
									Messager.msgSenderWithConfigMsg("align.lore_aligned_center_success", sender);
									return true;
								} else if (args[1].equalsIgnoreCase("right")) {
									Main.cooldownAPI.updateCooldown(player, cooldownID);
									ArrayList<String> aligned = alignStringsClient(textToAlign, ALIGN_RIGHT);
									
									ItemMeta meta = inHand.getItemMeta();
									/*meta.setDisplayName(aligned.get(0));*/								
									
									if (aligned.size() > 1) {
										// Lore in this thing
										List<String> lore = new ArrayList<String>();
										for (int i = 1; i < aligned.size(); i++) {
											lore.add(aligned.get(i));
										}
										meta.setLore(lore);
									}
									
									inHand.setItemMeta(meta);
									if (Main.USE_NEW_GET_HAND) { // Use 1.9+ method
										player.getInventory().setItemInMainHand(inHand);
									} else { // Use older method.
										player.setItemInHand(inHand);
									}
									Messager.msgSenderWithConfigMsg("align.lore_aligned_right_success", sender);
									return true;
								} else {
									Messager.msgSenderWithConfigMsg("align.incorrect_lore_args", sender);
									return true;
								}
							} else if (args[0].equalsIgnoreCase("both")) {
								Main.cooldownAPI.updateCooldown(player, cooldownID);
								if (args[1].equalsIgnoreCase("left")) {
									Main.cooldownAPI.updateCooldown(player, cooldownID);
									ArrayList<String> aligned = alignStringsClient(textToAlign, ALIGN_LEFT);
									
									ItemMeta meta = inHand.getItemMeta();
									meta.setDisplayName(aligned.get(0));							
									
									if (aligned.size() > 1) {
										// Lore in this thing
										List<String> lore = new ArrayList<String>();
										for (int i = 1; i < aligned.size(); i++) {
											lore.add(aligned.get(i));
										}
										meta.setLore(lore);
									}
									
									inHand.setItemMeta(meta);
									if (Main.USE_NEW_GET_HAND) { // Use 1.9+ method
										player.getInventory().setItemInMainHand(inHand);
									} else { // Use older method.
										player.setItemInHand(inHand);
									}
									Messager.msgSenderWithConfigMsg("align.both_aligned_left_success", sender);
									return true;
								} else if (args[1].equalsIgnoreCase("center")) {
									Main.cooldownAPI.updateCooldown(player, cooldownID);
									ArrayList<String> aligned = alignStringsClient(textToAlign, ALIGN_CENTER);
									
									ItemMeta meta = inHand.getItemMeta();
									meta.setDisplayName(aligned.get(0));							
									
									if (aligned.size() > 1) {
										// Lore in this thing
										List<String> lore = new ArrayList<String>();
										for (int i = 1; i < aligned.size(); i++) {
											lore.add(aligned.get(i));
										}
										meta.setLore(lore);
									}
									
									inHand.setItemMeta(meta);
									if (Main.USE_NEW_GET_HAND) { // Use 1.9+ method
										player.getInventory().setItemInMainHand(inHand);
									} else { // Use older method.
										player.setItemInHand(inHand);
									}
									Messager.msgSenderWithConfigMsg("align.both_aligned_center_success", sender);
									return true;
								} else if (args[1].equalsIgnoreCase("right")) {
									Main.cooldownAPI.updateCooldown(player, cooldownID);
									ArrayList<String> aligned = alignStringsClient(textToAlign, ALIGN_RIGHT);
									
									ItemMeta meta = inHand.getItemMeta();
									meta.setDisplayName(aligned.get(0));							
									
									if (aligned.size() > 1) {
										// Lore in this thing
										List<String> lore = new ArrayList<String>();
										for (int i = 1; i < aligned.size(); i++) {
											lore.add(aligned.get(i));
										}
										meta.setLore(lore);
									}
									
									inHand.setItemMeta(meta);
									if (Main.USE_NEW_GET_HAND) { // Use 1.9+ method
										player.getInventory().setItemInMainHand(inHand);
									} else { // Use older method.
										player.setItemInHand(inHand);
									}
									Messager.msgSenderWithConfigMsg("align.both_aligned_right_success", sender);
									return true;
								} else {
									Messager.msgSenderWithConfigMsg("align.incorrect_both_args", sender);
									return true;
								}
							} else {
								Messager.msgSenderWithConfigMsg("align.incorrect_args", sender);
								return true;
							}
						} else {
							Messager.msgSenderWithConfigMsg("align.not_enough_args", sender);
							return true;
						}
					} else {
						Messager.msgSenderWithConfigMsg("align.disabled_world", sender);
						return true;
					}
				} else {
					Messager.msgSenderWithConfigMsg("align.wrong_sender", sender);
					return true;
				}
			} else {
				Messager.msgSenderWithConfigMsg("align.no_permission", sender);
				return true;
			}
		}
		
		return false;
	
	}
	
	/**
	 * Attempts to align text for the minecraft client.
	 * @param itemTextList
	 * @param alignmentScheme
	 * @return
	 */
	public static ArrayList<String> alignStringsClient(ArrayList<String> itemTextList, int alignmentScheme) {
		ArrayList<String> toReturn = new ArrayList<String>();
		
		TableGenerator tg = null;
		
		if (alignmentScheme == ALIGN_LEFT) {
			tg = new TableGenerator(Alignment.LEFT);
		} else if (alignmentScheme == ALIGN_CENTER) {
			tg = new TableGenerator(Alignment.CENTER);
		} else if (alignmentScheme == ALIGN_RIGHT) {
			tg = new TableGenerator(Alignment.RIGHT);
		}
		
		for (String s : itemTextList) {
			tg.addRow(s);
		}
		
		toReturn = (ArrayList<String>) tg.generate(Receiver.CLIENT, true, true);
		
		return toReturn;
	}
	
	/**
	 * Attempts to align text for the minecraft client.
	 * @param itemTextList
	 * @param alignmentScheme
	 * @return
	 */
	public static ArrayList<String> alignStringsClientNew(ArrayList<String> itemTextList, int alignmentScheme) {
		ArrayList<String> toReturn = new ArrayList<String>();
		
		// TODO Align is being very easy to make
		
		if (alignmentScheme == ALIGN_LEFT) {
			
		} else if (alignmentScheme == ALIGN_CENTER) {
			
		} else if (alignmentScheme == ALIGN_RIGHT) {
			
		}
		
		
		
		
		
		return toReturn;
	}
	
	/**
	 * @deprecated
	 * Aligns text to the LEFT, CENTER, or RIGHT
	 * Ignores color codes.
	 * @param itemTextList
	 * @param alignmentScheme
	 * @return
	 */
	public static ArrayList<String> alignStrings(ArrayList<String> itemTextList, int alignmentScheme) {
		
		ArrayList<String> toReturn = new ArrayList<String>();
		
		if (alignmentScheme == ALIGN_LEFT) {
			for (String s : itemTextList) {
				toReturn.add(s.trim());
			}
		} else if (alignmentScheme == ALIGN_CENTER) {
			int placeholder = 0;
			for (String s : itemTextList) {
				itemTextList.set(placeholder, s.trim());
				placeholder++;
			}
			
			toReturn = itemTextList;
			ArrayList<String> colorRemovedStrings = new ArrayList<String>();
			for (String s : itemTextList) {
				colorRemovedStrings.add(ChatColor.stripColor(Messager.color(s)));
			}
			
			int longestText = 0;
			
			// Find longest string
			for (String s : colorRemovedStrings) {
				if (s.length() > longestText) {
					longestText = s.length();
				}
			}
			
			int current = 0;
			
			for (String s : colorRemovedStrings) {				
				if (s.length() != longestText) {
					int totalToAdd = longestText - s.length();				
						
					String coloredValue = itemTextList.get(current);
						
					String addLeft = "";
					String addRight = "";
						
					for (int i = 0; i < totalToAdd / 2; i++) {
						addLeft = addLeft + " ";
					}
						
					for (int i = 0; i < totalToAdd / 2; i++) {
						addRight = addRight + " ";
					}				
						
					coloredValue = addLeft + coloredValue + addRight;
					toReturn.set(current, coloredValue);
					
				} else {
					// Doesn't need space padding
					toReturn.set(current, itemTextList.get(current));
				}
				current++;
			}			
		} else if (alignmentScheme == ALIGN_RIGHT) {
			int placeholder = 0;
			for (String s : itemTextList) {
				itemTextList.set(placeholder, s.trim());
				placeholder++;
			}
			
			toReturn = itemTextList;
			ArrayList<String> colorRemovedStrings = new ArrayList<String>();
			for (String s : itemTextList) {
				colorRemovedStrings.add(ChatColor.stripColor(Messager.color(s)));
			}
			
			int longestText = 0;
			
			// Find longest string
			for (String s : colorRemovedStrings) {
				if (s.length() > longestText) {
					longestText = s.length();
				}
			}
			
			int current = 0;
			
			for (String s : colorRemovedStrings) {				
				if (s.length() != longestText) {
					int totalToAdd = longestText - s.length();				
						
					String coloredValue = itemTextList.get(current);
						
					String addLeft = "";
					
						
					for (int i = 0; i < totalToAdd; i++) {
						addLeft = addLeft + " ";
					}						
						
					coloredValue = addLeft + coloredValue;
					toReturn.set(current, coloredValue);					
				} else {
					// Doesn't need space padding
					toReturn.set(current, itemTextList.get(current));
				}
				current++;
			}
		}
		
		for (String value : toReturn) {
			Debug.send(value);
		}
		
		return toReturn;
	}


}

