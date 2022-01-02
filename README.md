# RenamePlugin
A plugin that does different item modifications with easy to use commands.
Lots more detail at https://www.spigotmc.org/resources/epicrename.4341/       
Made by: Justin Brubaker

This fork allows most commands to have cooldowns on them. This relys on another plugin of mine called cooldownapi. CooldownAPI must be enabled on the server for cooldowns to work as intended. Below is a guide to controlling cooldown lengths and bypassing if needed.

Permissions:
epicrename.bypasscooldown - Bypasses all cooldowns (ops bypass by default)

cooldownapi.epicrename.rename.(number of minutes) - causes the player with this permission to wait the number of minutes provided until running the command.
cooldownapi.epicrename.align.(number of minutes)
cooldownapi.epicrename.glow.(number of minutes)
cooldownapi.epicrename.import.(number of minutes)
cooldownapi.epicrename.insertloreline.(number of minutes)
cooldownapi.epicrename.lore.(number of minutes)
cooldownapi.epicrename.removeglow.(number of minutes)
cooldownapi.epicrename.removeloreline.(number of minutes)
cooldownapi.epicrename.setloreline.(number of minutes)

All cooldownapi permissions default to the lowest number players have access to. For example if i had a 1 minute cool down for rename and a 10 minute it would only make u wait 1 minute.

Please report all issues to `Spikey Noob#8464` on discord
