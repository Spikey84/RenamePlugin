package com.gmail.justbru00.epic.rename.utils.v3;

import org.bukkit.entity.Player;

import com.gmail.justbru00.epic.rename.enums.v3.EpicRenameCommands;
import com.gmail.justbru00.epic.rename.main.v3.Main;

public class MinMaxColorCodes {
	
	/**
	 * Checks if the given string has too many formatting codes.
	 * @return True if the max. is not reached. False if the max. has been reached.
	 */
	public static boolean checkMaxColorCodes(Player p, String valueToCheck, EpicRenameCommands cmd) {
		if (p.hasPermission("epicrename.bypass.formattingcodemax")) {
			Messager.msgPlayer(Main.getMsgFromConfig("format_code_limit.bypass_max"), p);
			return true;
		}
		
		int numOfCodes = getAmountOfColorCodes(valueToCheck, '&');
		
		if (numOfCodes > Main.getInstance().getConfig().getInt("formatting_code_limit." + EpicRenameCommands.getStringName(cmd) + ".max")) {
			return false;
		}
		
		return true;
	}
	/**
	 * Checks if the given string has too few formatting codes.
	 * @return True if the min. is reached. False if the min. has not been reached.
	 */
	public static boolean checkMinColorCodes(Player p, String valueToCheck, EpicRenameCommands cmd) {
		if (p.hasPermission("epicrename.bypass.formattingcodemin")) {
			Messager.msgPlayer(Main.getMsgFromConfig("format_code_limit.bypass_min"), p);
			return true;
		}
		
		int numOfCodes = getAmountOfColorCodes(valueToCheck, '&');
		
		if (numOfCodes < Main.getInstance().getConfig().getInt("formatting_code_limit." + EpicRenameCommands.getStringName(cmd) + ".min")) {
			return false;
		}
		return true;
	}
	
	/**
	 * Counts how many formatting codes are in the given String.
	 * @return The amount of formatting codes in the string.
	 */
	public static int getAmountOfColorCodes(String valueToCountCodesIn, char colorCodeChar) {
		int colorCodes = 0;
		char[] array = valueToCountCodesIn.toCharArray();
		
		for (int i = 0; i < array.length; i++) {
			
			if (array[i] == colorCodeChar) {
				// Might be a color code
				if (array.length != i + 1) { // Prevent error with color code character at end of string
					for(String s : FormattingPermManager.FORMAT_CODES) {
						if (String.valueOf(array[i+1]).equalsIgnoreCase(s)) {
							colorCodes++;
						}
					}
				}
			}			
		}
		
		return colorCodes;
	}
	
}
