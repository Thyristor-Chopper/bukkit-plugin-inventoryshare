package com.pb.ivshare;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryShare extends JavaPlugin implements Listener, CommandExecutor {
	Map<String, Inventory> mp;
	// String clickEventKey = "clickEvent";
	
	@Override 
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		this.getCommand("inv").setExecutor((CommandExecutor)this);
		mp = new HashMap<String, Inventory>();
		
		// https://www.spigotmc.org/threads/multiple-nms-versions.79618/
		// Matcher matcher = Pattern.compile("(\\(MC: )([\\d\\.]+)(\\))").matcher(Bukkit.getVersion());
		// if(matcher.find() && Double.parseDouble(matcher.group(2).substring(2)) < 21.5)
		// 	clickEventKey = "click_event";
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		String msg = event.getMessage();
		if(msg == null) return;
		Player player = event.getPlayer();
		if(player == null) return;
		if(msg.equals("[인벤토리]") || msg.equals("[인벤]")) {
			event.setCancelled(true);
			Inventory gui = Bukkit.createInventory(null, 45, player.getName() + "의 인벤토리");
			PlayerInventory inventory = player.getInventory();
			try {
				int i;
				gui.setItem(0, inventory.getHelmet());
				gui.setItem(2, inventory.getChestplate());
				gui.setItem(4, inventory.getLeggings());
				gui.setItem(6, inventory.getBoots());
				gui.setItem(8, inventory.getItemInOffHand());
				
				for(i=0; i<36; i++) 
					gui.setItem(9 + i, inventory.getItem(i));
				
				mp.remove(player.getUniqueId().toString());
			} catch(Exception e) {}
			
			mp.put(player.getUniqueId().toString(), gui);
			
			new BukkitRunnable() {
				@Override
				public void run() {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
						"tellraw @a " +
							"[{\"text\":\"  -  -  -  [" + player.getName() + "의 인벤토리]\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/inv " + player.getUniqueId().toString() + "\"},\"click_event\":{\"action\":\"run_command\",\"command\":\"/inv " + player.getUniqueId().toString() + "\"}}]");
				}
			}.runTask(this);
		} else if(msg.equals("[엔더상자]") || msg.equals("[엔상]")) {
			event.setCancelled(true);
			Inventory gui = Bukkit.createInventory(null, 27, player.getName() + "의 엔더 상자");
			Inventory inventory = player.getEnderChest();
			try {
				int i;
				for(i=0; i<27; i++) 
					gui.setItem(i, inventory.getItem(i));
				
				mp.remove("E_" + player.getUniqueId().toString());
			} catch(Exception e) {}
			
			mp.put("E_" + player.getUniqueId().toString(), gui);
			
			new BukkitRunnable() {
				@Override
				public void run() {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
						"tellraw @a " +
							"[{\"text\":\"  -  -  -  [" + player.getName() + "의 엔더 상자]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/inv E_" + player.getUniqueId().toString() + "\"},\"click_event\":{\"action\":\"run_command\",\"command\":\"/inv E_" + player.getUniqueId().toString() + "\"}}]");
				}
			}.runTask(this);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(mp.containsValue(event.getInventory())) {
			event.setCancelled(true);
			event.setResult(Event.Result.DENY);
			// event.getWhoClicked().closeInventory();
		}
	}
	
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		if(mp.containsValue(event.getInventory())) {
			event.setCancelled(true);
			event.setResult(Event.Result.DENY);
			// event.getWhoClicked().closeInventory();
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length != 1) return true;
		if(!(sender instanceof Player)) return true;
		Player player = (Player) sender;
		String uuid = args[0];
		if(mp.containsKey(uuid))
			player.openInventory(mp.get(uuid));
		return true;
	}
	
	@Override
	public void onDisable() {
	}
}
