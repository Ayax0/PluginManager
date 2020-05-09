package com.nextlvlup.PluginManager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.nextlvlup.PluginManager.cmd.PluginCommand;
import com.nextlvlup.PluginManager.cmd.PluginManager;
import com.nextlvlup.PluginManager.utils.PluginUtils;

import lombok.Getter;

public class Main extends JavaPlugin {
	
	@Getter private static JavaPlugin instance;
	@Getter private static List<String> BLACKLIST;
	@Getter private static List<String> BLACKLIST_PREMIUM;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		instance = this;
		
		System.out.println(Bukkit.getServer().getBukkitVersion());
		
		getCommand("plugin").setExecutor(new PluginCommand());
		
		saveDefaultConfig();
		
		BLACKLIST = (List<String>) getConfig().getList("blacklist");
		BLACKLIST_PREMIUM = (List<String>) getConfig().getList("blacklist_premium");
		
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				for(String plugin : (List<String>)getConfig().getList("uninstalled")) {
					try {
						PluginUtils.unload(PluginUtils.getPluginByName(plugin));
					}catch(Exception e) {
						System.out.println("failed to unload " + plugin);
					}
				}
				
				for(Plugin plugin : PluginManager.list()) {
					int code = PluginManager.update(plugin.getName());
					if(code == 409) {
						PluginUtils.disable(plugin);
						PluginUtils.unload(plugin);
						Bukkit.broadcastMessage(ChatColor.GRAY + "downloading updates for " + plugin.getName());
						int update = PluginManager.install(plugin.getName(), true);
						if(update == 200) Bukkit.broadcastMessage(ChatColor.GREEN + plugin.getName() + " update successfull");
						else Bukkit.broadcastMessage(ChatColor.RED + plugin.getName() + " update failed (" + update + ")");
					}
				}
			}
		}, 2000);
	}
}
