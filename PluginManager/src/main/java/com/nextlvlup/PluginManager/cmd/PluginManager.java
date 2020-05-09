package com.nextlvlup.PluginManager.cmd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.json.JSONArray;

import com.nextlvlup.PluginManager.Main;
import com.nextlvlup.PluginManager.utils.HashUtils;
import com.nextlvlup.PluginManager.utils.PluginUtils;

public class PluginManager {
	
	public static Plugin[] list() {
		return Bukkit.getPluginManager().getPlugins();
	}
	
	public static boolean reload(String name) {
		name = name.replaceAll("[^\\w]", "").toLowerCase();
		Plugin plugin = PluginUtils.getPluginByName(name);
		if(plugin != null) {
			PluginUtils.reload(plugin);
			return true;
		}
		return false;
	}
	
	public static int disable(String name) {
		name = name.replaceAll("[^\\w]", "").toLowerCase();
		Plugin plugin = PluginUtils.getPluginByName(name);
		if(plugin != null) {
			if(plugin.isEnabled()) {
				PluginUtils.disable(plugin);
				return 200;
			}
			return 409;
		}
		return 500;
	}
	
	public static int enable(String name) {
		name = name.replaceAll("[^\\w]", "").toLowerCase();
		Plugin plugin = PluginUtils.getPluginByName(name);
		if(plugin != null) {
			if(!plugin.isEnabled()) {
				PluginUtils.enable(plugin);
				return 200;
			}
			return 409;
		}
		return 500;
	}
	
	@SuppressWarnings("unchecked")
	public static int install(String name, boolean force) {
		name = name.replaceAll("[^\\w]", "").toLowerCase();
		try {
			List<String> uninstalledPlugins = (List<String>) Main.getInstance().getConfig().getList("uninstalled");
			if(uninstalledPlugins == null) uninstalledPlugins = new ArrayList<String>();
			
			File file = new File("plugins" + File.separator + name + ".jar");
			if(!file.exists() || uninstalledPlugins.contains(name) || force) {
				file.createNewFile();
				
				CloseableHttpClient client = HttpClientBuilder.create().build();
				HttpGet request = new HttpGet("http://localhost:41/load/?plugin=" + name + "&version=" + Bukkit.getServer().getBukkitVersion());
				HttpResponse response = client.execute(request);
				if(response.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					InputStream inStream = entity.getContent();
					FileOutputStream outFile = new FileOutputStream(file);
					int index;
					while((index = inStream.read()) != -1) {
						outFile.write(index);
					}
					outFile.close();
					inStream.close();
				}
				
				if(file.length() > 0) {
					if(uninstalledPlugins.contains(name.toLowerCase())) {
						uninstalledPlugins.remove(name.toLowerCase());
						Main.getInstance().getConfig().set("uninstalled", uninstalledPlugins);
						try {
							Main.getInstance().getConfig().save(new File("plugins/PluginManager/config.yml"));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					PluginUtils.load(name);
					if(PluginUtils.getPluginByName(name).isEnabled()) return 200;
					else return 403;
				}else {
					file.delete();
				}
				return 404;
			}
			return 409;
		}catch(Exception e) {
			e.printStackTrace();
			return 500;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static boolean uninstall(String name) {
		name = name.replaceAll("[^\\w]", "").toLowerCase();
		File file = new File("plugins" + File.separator + name + ".jar");
		if(file.exists()) {
			try {
				Plugin plugin = PluginUtils.getPluginByName(name);
				
				PluginUtils.disable(plugin);
				PluginUtils.unload(plugin);
			}catch(Exception e) { e.printStackTrace(); }
			
			if(file.delete()) return true;
			
			List<String> uninstalledPlugins = (List<String>) Main.getInstance().getConfig().getList("uninstalled");
			if(uninstalledPlugins == null) uninstalledPlugins = new ArrayList<String>();
			if(uninstalledPlugins.contains(name.toLowerCase())) return false;
			uninstalledPlugins.add(name.toLowerCase());
			Main.getInstance().getConfig().set("uninstalled", uninstalledPlugins);
			
			try {
				Main.getInstance().getConfig().save(new File("plugins/PluginManager/config.yml"));
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	public static int update(String name) {
		name = name.replaceAll("[^\\w]", "").toLowerCase();
		File file = new File("plugins" + File.separator + name + ".jar");
		if(file.exists()) {
			try {
				HttpClient client = HttpClientBuilder.create().build();
				HttpGet request = new HttpGet("http://localhost:41/version/?plugin=" + name + "&hash=" + HashUtils.getFileChecksum(file) + "&version=" + Bukkit.getServer().getBukkitVersion());
				HttpResponse response = client.execute(request);
				return response.getStatusLine().getStatusCode();
			}catch(Exception e) {
				e.printStackTrace();
				return 500;
			}
		}
		return 404;
	}
	
	public static List<String> browse() {
		List<String> list = new ArrayList<String>();
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet("http://localhost:41/browse/?version=" + Bukkit.getServer().getBukkitVersion());
			HttpResponse response = client.execute(request);
			if(response.getStatusLine().getStatusCode() == 200) {
				String json = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
				JSONArray array = new JSONArray(json);
				for(Object obj : array.toList()) {
					list.add(obj.toString());
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			return list;
		}
		return list;
	}

}
