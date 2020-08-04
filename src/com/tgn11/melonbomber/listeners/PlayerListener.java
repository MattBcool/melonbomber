package com.tgn11.melonbomber.listeners;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener
{
	com.tgn11.melonbomber.main.Main plugin;
	HashMap<String, Integer> antiJump = new HashMap<String, Integer>();
	Random rand = new Random();

	public PlayerListener(com.tgn11.melonbomber.main.Main plugin)
	{
		super();
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		Player p = e.getPlayer();
		if(p.getGameMode() == GameMode.SURVIVAL)
		{
			final String n = p.getName();
			
			int speed = 0;
			int bombs = 0;
			
			if(plugin.dataHandler.speed.containsKey(n))
			{
				speed = plugin.dataHandler.speed.get(n)-1;
			}
			
			if(plugin.dataHandler.bombs.containsKey(n))
			{
				bombs = plugin.dataHandler.bombs.get(n);
			}
			
			if(speed < 0)
			{
				speed = 0;
			}
			
			p.setWalkSpeed(0.2f+(float)((float)(speed)/80f));

			int amountAtTime = bombs; //this amount - 1;

			Location loc = p.getLocation();
			int y = (int)p.getLocation().getY();
			
			if(loc.getBlock().getType() == Material.WOOD || loc.getBlock().getType() == Material.STONE)
			{
				e.setCancelled(true);
			}
			
			if(!antiJump.containsKey(n))
			{
				antiJump.put(n, y);
			}
			else
			{
				if(y != antiJump.get(n) || y != 53)
				{
					loc.setY(loc.getY()-.5);
					p.teleport(loc);


					if(plugin.dataHandler.justMeloned.get(n) == false)
					{
						plugin.launchMelon(p,3);
						doJustMelonedTimer(amountAtTime,n);
					}
				}
				antiJump.remove(n);
				antiJump.put(n, y);
			}

			if(plugin.dataHandler.hasMelonKick.get(n))
			{
				if(plugin.dataHandler.justMeloned.get(n) == false)
				{
					if(p.isSprinting())
					{
						Location bloc = behindPlayer(p);
						if(bloc.getBlock().getType() == Material.MELON_BLOCK)
						{
							bloc.getBlock().setType(Material.AIR);
							plugin.launchMelon(p,1);
							doJustMelonedTimer(amountAtTime,n);
						}
					}
				}
			}
		}
	}

	private void doJustMelonedTimer(int a, final String n)
	{
		plugin.dataHandler.justMeloned.put(n, true);
		long timer = (20L*6)/a;
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				plugin.dataHandler.justMeloned.put(n, false);
			}
		}, timer);
	}

	public Location behindPlayer(Player player)
	{
		double rotation = (player.getLocation().getYaw() + 180) % 360;
		Block b = player.getLocation().getBlock();
		if (rotation < 0) {
			rotation += 360.0;
		}
		if (0 <= rotation && rotation < 22.5) {
			return b.getRelative(BlockFace.NORTH).getLocation();
		} else if (22.5 <= rotation && rotation < 67.5) {
			return b.getRelative(BlockFace.NORTH_EAST).getLocation();
		} else if (67.5 <= rotation && rotation < 112.5) {
			return b.getRelative(BlockFace.EAST).getLocation();
		} else if (112.5 <= rotation && rotation < 157.5) {
			return b.getRelative(BlockFace.SOUTH_EAST).getLocation();
		} else if (157.5 <= rotation && rotation < 202.5) {
			return b.getRelative(BlockFace.SOUTH).getLocation();
		} else if (202.5 <= rotation && rotation < 247.5) {
			return b.getRelative(BlockFace.SOUTH_WEST).getLocation();
		} else if (247.5 <= rotation && rotation < 292.5) {
			return b.getRelative(BlockFace.WEST).getLocation();
		} else if (292.5 <= rotation && rotation < 337.5) {
			return b.getRelative(BlockFace.NORTH_WEST).getLocation() ;
		} else if (337.5 <= rotation && rotation < 360.0) {
			return b.getRelative(BlockFace.NORTH).getLocation();
		} else {
			return null;
		}
	}

	@EventHandler
	public void onPlayerPickup(PlayerPickupItemEvent e)
	{
		if(e.getItem().getItemStack() != null)
		{
			final Player p = e.getPlayer();
			String n = p.getName();
			ItemStack stack = e.getItem().getItemStack();

			if(stack.getType() == Material.MELON_BLOCK)
			{
				plugin.scoreboardHandler.updateScoreboardCheck(plugin.scoreboardHandler.manager, p, new int[] {Math.min(10,stack.getAmount()),-1,-1,-1,-1,-1});
				e.getItem().remove();
				e.setCancelled(true);
				p.sendMessage(ChatColor.AQUA + "You picked up an Upgrade!");
			}
			if(stack.getType() == Material.DIAMOND_BOOTS)
			{
				plugin.scoreboardHandler.updateScoreboardCheck(plugin.scoreboardHandler.manager, p, new int[] {-1,Math.min(10,stack.getAmount()),-1,-1,-1,-1});
				e.getItem().remove();
				e.setCancelled(true);
				p.sendMessage(ChatColor.AQUA + "You picked up an Upgrade!");
			}
			if(stack.getType() == Material.TNT)
			{
				plugin.scoreboardHandler.updateScoreboardCheck(plugin.scoreboardHandler.manager, p, new int[] {-1,-1,Math.min(10,stack.getAmount()),-1,-1,-1});
				e.getItem().remove();
				e.setCancelled(true);
				p.sendMessage(ChatColor.AQUA + "You picked up an Upgrade!");
			}
			if(stack.getType() == Material.DIAMOND_SWORD)
			{
				plugin.dataHandler.hasPiercing.put(n, true);
				plugin.scoreboardHandler.updateScoreboardCheck(plugin.scoreboardHandler.manager, p, new int[] {-1,-1,-1,1,-1,-1});
				e.getItem().remove();
				e.setCancelled(true);
				p.sendMessage(ChatColor.AQUA + "You picked up an Upgrade!");
			}
			if(stack.getType() == Material.LEATHER_BOOTS)
			{
				plugin.dataHandler.hasLineBomb.put(n, true);
				plugin.scoreboardHandler.updateScoreboardCheck(plugin.scoreboardHandler.manager, p, new int[] {-1,-1,-1,-1,-1,1});
				e.getItem().remove();
				e.setCancelled(true);
				p.sendMessage(ChatColor.AQUA + "You picked up an Upgrade!");
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		Player p = e.getEntity();
		String n = p.getName();
		String k = plugin.dataHandler.killer.get(n);
		String msg = ChatColor.RED + n + ChatColor.DARK_RED + " was bombed by " + ChatColor.RED + k + "!";
		p.getWorld().playSound(p.getLocation(), Sound.IRONGOLEM_DEATH, 1f, 0.1f);
		
		if(n == k)
		{
			msg = ChatColor.RED + n + ChatColor.DARK_RED + " bombed themselves!";
		}
		
		if(!msg.contains("was bombed by null!"))
		{
			e.setDeathMessage(msg);
			boolean playerOnline = false;
			for(Player pk : Bukkit.getOnlinePlayers())
			{
				if(pk.getName().equals(k))
				{
					playerOnline = true;
				}
			}
			if(playerOnline)
			{
				Player ply = Bukkit.getPlayer(k);
				if(ply != null || !plugin.dataHandler.killer.containsKey(n))
				{
					/**
					String uuid = ply.getUniqueId().toString();
					me.nick.sqlcurrency.Util.Utils.addCurrency(uuid, 1); RE-ENABLE THIS LATER! @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
					*/
				}
			}
		}
		else
		{
			e.setDeathMessage(ChatColor.RED + n + ChatColor.DARK_RED + " was bombed!");
		}

		int hgm = 0;
		for(Player ply : Bukkit.getOnlinePlayers())
		{
			if(ply.getGameMode() == GameMode.SURVIVAL)
			{
				hgm=hgm + 1;
			}
		}

		if(hgm == 1)
		{
			for(Player ply : Bukkit.getOnlinePlayers())
			{
				if(ply.getGameMode() == GameMode.SURVIVAL)
				{
					plugin.prepareRestart(ply.getName(), 30);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		String n = p.getName();
		plugin.scoreboardHandler.updateScoreboard(plugin.scoreboardHandler.manager, p, new int[] {1,1,1,0,0,0});
		plugin.doEffectsAroundPlayers(p);
		plugin.dataHandler.justMeloned.put(n, false);
		p.setDisplayName(ChatColor.GREEN + n + ChatColor.WHITE);
		if(Bukkit.getOnlinePlayers().length > 4)
		{
			if(!plugin.isRestarting)
			{
				plugin.prepareRestart("Nobody", 30);
			}
		}
		else
		{
			Bukkit.broadcastMessage(ChatColor.GOLD + "Waiting for more players...");
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		int hgm = 0;
		for(Player ply : Bukkit.getOnlinePlayers())
		{
			if(ply.getGameMode() == GameMode.SURVIVAL)
			{
				hgm=hgm + 1;
			}
		}

		if(hgm == 1)
		{
			for(Player ply : Bukkit.getOnlinePlayers())
			{
				if(ply.getGameMode() == GameMode.SURVIVAL)
				{
					Bukkit.broadcastMessage(ChatColor.GOLD + "Waiting for more players...");
				}
			}
		}
	}

	@EventHandler
	public void regenHealth(EntityRegainHealthEvent event) 
	{
		event.setCancelled(true);
	}

	@EventHandler
	public void onDamageTake(EntityDamageEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e)
	{
		if(plugin.matchInProgress || plugin.isRestarting)
		{
			Player p = e.getPlayer();
			p.setGameMode(GameMode.SPECTATOR);
		}
	}
}
