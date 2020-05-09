package com.nextlvlup.PluginManager.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nextlvlup.PluginManager.Main;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.storage.objects.Group;

public class PluginCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.isOp()) {
			sender.sendMessage(" ");
			if(args.length == 0) {
				help(sender);
			}else {
				//List
				if(args[0].equalsIgnoreCase("list")) {
					sender.sendMessage(ChatColor.GOLD + "Plugins:");
					for(Plugin plugin : PluginManager.list()) {
						if(checkPermission(sender, plugin.getName()))
							sender.sendMessage((plugin.isEnabled() ? ChatColor.GRAY : ChatColor.RED) + "- " + plugin.getName());
					}
				}
				//Reload
				else if(args[0].equalsIgnoreCase("reload")) {
					if(args.length > 1) {
						if(checkPermission(sender, args[1])) {
							if(PluginManager.reload(args[1])) sender.sendMessage(ChatColor.GREEN + "Plugin " + args[1] + " successfully reloaded");
							else sender.sendMessage(ChatColor.RED + "Plugin " + args[1] + " failed to reload");
						}else {
							sender.sendMessage(ChatColor.RED + "an error has occurred");
						}
					}else {
						sender.sendMessage(ChatColor.RED + "/plugin reload [Plugin]");
					}
				}
				//Disable
				else if(args[0].equalsIgnoreCase("disable")) {
					if(args.length > 1) {
						if(checkPermission(sender, args[1])) {
							int code = PluginManager.disable(args[1]);
							switch(code) {
							case 200:
								sender.sendMessage(ChatColor.GOLD + "Plugin " + args[1] + " successfully disabled");
								break;
							case 409:
								sender.sendMessage(ChatColor.RED + "Plugin is already disabled");
								break;
							case 500:
								sender.sendMessage(ChatColor.RED + "an error has occurred");
								break;
							}
						}else {
							sender.sendMessage(ChatColor.RED + "an error has occurred");
						}
					}else {
						sender.sendMessage(ChatColor.RED + "/plugin disable [Plugin]");
					}
				}
				//Enable
				else if(args[0].equalsIgnoreCase("enable")) {
					if(args.length > 1) {
						if(checkPermission(sender, args[1])) {
							int code = PluginManager.enable(args[1]);
							switch(code) {
							case 200:
								sender.sendMessage(ChatColor.GREEN + "Plugin " + args[1] + " successfully enabled");
								break;
							case 409:
								sender.sendMessage(ChatColor.RED + "Plugin is already enabled");
								break;
							case 500:
								sender.sendMessage(ChatColor.RED + "an error has occurred");
								break;
							}
						}else {
							sender.sendMessage(ChatColor.RED + "an error has occurred");
						}
					}else {
						sender.sendMessage(ChatColor.RED + "/plugin enable [Plugin]");
					}
				}
				//Install
				else if(args[0].equalsIgnoreCase("install")) {
					if(args.length > 1) {
						if(checkPermission(sender, args[1])) {
							int code = PluginManager.install(args[1], false);
							
							switch(code) {
							case 200:
								sender.sendMessage(ChatColor.GREEN + "instalation complete");
								sender.sendMessage(ChatColor.GREEN + args[1] + " enabled");
								break;
							case 403:
								sender.sendMessage(ChatColor.RED + "failed to enable " + args[1]);
								break;
							case 404:
								sender.sendMessage(ChatColor.RED + "Plugin '" + args[1] + "' not found");
								break;
							case 409:
								sender.sendMessage(ChatColor.RED + "Plugin '" + args[1] + "' already installed");
								sender.sendMessage(ChatColor.GRAY + "user /plugin update [Plugin] to update");
								break;
							case 500:
								sender.sendMessage(ChatColor.RED + "an error has occurred");
								break;
							}
						}else {
							sender.sendMessage(ChatColor.RED + "an error has occurred");
						}
					}else {
						sender.sendMessage(ChatColor.RED + "/plugin install [Plugin]");
					}
				}
				//Uninstall
				else if(args[0].equalsIgnoreCase("uninstall")) {
					if(args.length > 1) {
						if(checkPermission(sender, args[1])) {
							if(PluginManager.uninstall(args[1])) sender.sendMessage(ChatColor.GREEN + "Plugin successfully uninstalled");
							else sender.sendMessage(ChatColor.RED + "Plugin could not be uninstalled");
						}else {
							sender.sendMessage(ChatColor.RED + "an error has occurred");
						}
					}else {
						sender.sendMessage(ChatColor.RED + "/plugin uninstall [Plugin]");
					}
				}
				//Update
				else if(args[0].equalsIgnoreCase("update")) {
					if(args.length > 1) {
						if(checkPermission(sender, args[1])) {
							int code = PluginManager.update(args[1]);
							
							switch(code) {
							case 200:
								sender.sendMessage(ChatColor.GOLD + "there is no update available for " + args[1]);
								break;
							case 409:
								sender.sendMessage(ChatColor.GREEN + "there is an update available for " + args[1]);
								sender.sendMessage(ChatColor.GRAY + "downloading new version...");
								
								PluginManager.uninstall(args[1]);
								int code2 = PluginManager.install(args[1], true);
								
								switch(code2) {
								case 200:
									sender.sendMessage(ChatColor.GREEN + "update complete");
									sender.sendMessage(ChatColor.GREEN + args[1] + " enabled");
									break;
								case 403:
									sender.sendMessage(ChatColor.RED + "failed to enable " + args[1]);
									break;
								case 404:
									sender.sendMessage(ChatColor.RED + "Plugin '" + args[1] + "' not found");
									break;
								case 500:
									sender.sendMessage(ChatColor.RED + "an error has occurred");
									break;
								}
								break;
							case 404:
								sender.sendMessage(ChatColor.RED + "Plugin '" + args[1] + "' not found");
								break;
							case 500:
								sender.sendMessage(ChatColor.RED + "an error has occurred");
								break;
							}
						}else {
							sender.sendMessage(ChatColor.RED + "an error has occurred");
						}
					}else {
						sender.sendMessage(ChatColor.RED + "/plugin update [Plugin]");
					}
				}
				//Browse
				else if(args[0].equalsIgnoreCase("browse")) {
					sender.sendMessage(ChatColor.GOLD + "Available Plugins:");
					for(String plugin : PluginManager.browse()) {
						sender.sendMessage(ChatColor.GRAY + "- " + plugin);
					}
				}else {
					help(sender);
				}
			}
		}
		return false;
	}
	
	private boolean checkPermission(CommandSender sender, String plugin) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(isPremium(p)) {
				for(String pl : Main.getBLACKLIST_PREMIUM()) {
					if(pl.equalsIgnoreCase(plugin)) return false;
				}
			}else {
				for(String pl : Main.getBLACKLIST()) {
					if(pl.equalsIgnoreCase(plugin)) return false;
				}
			}
		}
		return true;
	}
	
	private boolean isPremium(Player p) {
		for(Group group : UltraPermissions.getAPI().getUsers().uuid(p.getUniqueId()).getGroups().get()) {
			if(group.getName().equalsIgnoreCase("User")) return false;
		}
		return true;
	}
	
	private void help(CommandSender sender) {
		sender.sendMessage(ChatColor.GOLD + "Plugin Manager:");
		
		sender.sendMessage(ChatColor.AQUA + "/plugin list");
		sender.sendMessage(ChatColor.GRAY + "List all installed Plugins");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.AQUA + "/plugin reload [Plugin]");
		sender.sendMessage(ChatColor.GRAY + "Reload selected Plugin");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.AQUA + "/plugin disable [Plugin]");
		sender.sendMessage(ChatColor.GRAY + "Disable selected Plugin");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.AQUA + "/plugin enable [Plugin]");
		sender.sendMessage(ChatColor.GRAY + "Enable selected Plugin");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.AQUA + "/plugin install [Plugin]");
		sender.sendMessage(ChatColor.GRAY + "Install Plugin from Database");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.AQUA + "/plugin uninstall [Plugin]");
		sender.sendMessage(ChatColor.GRAY + "Uninstall selected Plugin");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.AQUA + "/plugin update [Plugin]");
		sender.sendMessage(ChatColor.GRAY + "Download newest Version from Database");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.AQUA + "/plugin browse");
		sender.sendMessage(ChatColor.GRAY + "Show all Installable Plugins from Database");
	}

}
