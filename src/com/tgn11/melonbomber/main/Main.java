package com.tgn11.melonbomber.main;

import java.util.ArrayList;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.util.Vector;

import com.tgn11.melonbomber.data.MelonDataBuffer;
import com.tgn11.melonbomber.extra.ParticleEffect;
import com.tgn11.melonbomber.listeners.BlockListener;
import com.tgn11.melonbomber.listeners.PlayerListener;

public class Main extends JavaPlugin
{
	public static Main plugin;
	
	private Random rand = new Random();
	public boolean isRestarting = false;
	public boolean matchInProgress = false;
	private boolean worldEffectsEnabled;
	public ScoreboardHandler scoreboardHandler;
	public DataHandler dataHandler;
	
	public String WORLD_NAME = "world";

	public void onEnable()
	{
		plugin = this;
		dataHandler = new DataHandler(this);
		scoreboardHandler = new ScoreboardHandler(this);
		
		getServer().getLogger().info("MelonBomber (Gamemode) - Running!");
		getServer().getPluginManager().registerEvents(new BlockListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

		doPlayerSetup();
	}

	@SuppressWarnings("deprecation")
	private void doPlayerSetup()
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			String n = p.getName();
			dataHandler.justMeloned.put(n, false);
			p.setDisplayName(ChatColor.GREEN + n + ChatColor.WHITE);
			scoreboardHandler.updateScoreboard(scoreboardHandler.manager, p, new int[] {1,1,1,0,0,0});
			doEffectsAroundPlayers(p);
		}
	}

	public void doEffectsAroundPlayers(final Player p)
	{
		if(!worldEffectsEnabled)
		{
			worldEffectsEnabled = true;
			if(p.isOnline())
			{
				for(Block b : blocksFromTwoPoints(new Location(p.getWorld(), 279, 62, 1383), new Location(p.getWorld(), 207, 55, 1311)))
				{
					if(rand.nextInt(100) > 98)
					{
						Location loc = b.getLocation();
						ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, .2F, 1, loc);
					}
				}
				
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
				{
					public void run()
					{
						doEffectsAroundPlayers(p);
					}
				}, 10L*3);
			}
		}
	}

	public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2)
	{
		List<Block> blocks = new ArrayList<Block>();

		int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
		int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());

		int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
		int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());

		int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
		int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());

		for(int x = bottomBlockX; x <= topBlockX; x++)
		{
			for(int z = bottomBlockZ; z <= topBlockZ; z++)
			{
				for(int y = bottomBlockY; y <= topBlockY; y++)
				{
					Block block = loc1.getWorld().getBlockAt(x, y, z);

					blocks.add(block);
				}
			}
		}

		return blocks;
	}


	public void onDisable()
	{
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	{
		if(sender instanceof Player)
		{
			Player p = (Player)sender;
			if(p.isOp())
			{
				if(label.equalsIgnoreCase("sm"))
				{
					launchMelon(p,3);
				}
				if(label.equalsIgnoreCase("mbr"))
				{
					prepareRestart("Nobody", 30);
				}
				if(label.equalsIgnoreCase("imbr"))
				{
					prepareRestart("Nobody", 1);
				}
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public void launchMelon(Player p, int timer)
	{
		String n = p.getName();
		Location tloc = p.getLocation();

		Location loc = tloc.getBlock().getLocation();
		loc.setY(loc.getY()+1);
		loc.setYaw(0);
		Vector dir = tloc.getDirection();
		Location loc2 = loc.clone();
		loc2.add(0, 0.5f, 0);

		Entity e = p.getWorld().spawnFallingBlock(tloc, Material.MELON_BLOCK, (byte)0);
		Vector v = new Vector(dir.getX(), 0.11f,dir.getZ());
		e.setCustomName(""+timer);
		dataHandler.melondata.put(e, v);
		boolean hp = false;
		int po = 1;

		if(dataHandler.hasPiercing.containsKey(n))
		{
			hp = dataHandler.hasPiercing.get(n);
		}

		if(dataHandler.power.containsKey(n))
		{
			po = dataHandler.power.get(n);
		}

		if(dataHandler.entityMelonData.containsKey(n))
		{
			MelonDataBuffer md = dataHandler.entityMelonData.get(n);
			md.update(po+1,hp,n,e);
		}
		else
		{
			dataHandler.entityMelonData.put(n, new MelonDataBuffer(po+1,hp,n,e));
		}

		e.setVelocity(v);
	}

	public static ItemStack createCustomItemStack(Material mat, ChatColor color, String disname, String lore)
	{
		ItemStack item = new ItemStack(mat);
		ItemMeta item_meta = item.getItemMeta();
		item_meta.setDisplayName(color + disname);
		ArrayList<String> item_meta_lore = new ArrayList<String>();
		item_meta_lore.add(ChatColor.AQUA + lore);
		item_meta.setLore(item_meta_lore);
		item.setItemMeta(item_meta);

		return item;
	}


	@SuppressWarnings("deprecation")
	public void initMB()
	{
		matchInProgress = true;
		isRestarting = false;

		resetMap(Bukkit.getWorld(plugin.WORLD_NAME));
		for(Player p : Bukkit.getOnlinePlayers())
		{
			respawnPlayer(p);
		}
	}

	public void respawnPlayer(Player p)
	{	
		int ri = rand.nextInt(8);

		p.setGameMode(GameMode.SURVIVAL);
		final String n = p.getName();
		dataHandler.justMeloned.put(n, true);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			public void run()
			{
				plugin.dataHandler.justMeloned.put(n, false);
			}
		}, 20*2);
		
		if(ri <= dataHandler.playerSpawns.size())
		{
			p.teleport(dataHandler.playerSpawns.get(ri));
		}

		Location loc = p.getLocation();
		ArrayList<Location> psl = new ArrayList<Location>();
		psl.add(loc);
		psl.add(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()+1));
		psl.add(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()-1));
		psl.add(new Location(loc.getWorld(), loc.getX()+1, loc.getY(), loc.getZ()));
		psl.add(new Location(loc.getWorld(), loc.getX()-1, loc.getY(), loc.getZ()));

		for(Location locs : psl)
		{
			Block b = locs.getBlock();
			if(b.getType() == Material.WOOD)
			{
				b.setType(Material.AIR);
			}
		}
	}

	public void prepareRestart(String winner, int timer)
	{	
		isRestarting = true;
		matchInProgress = false;

		Bukkit.broadcastMessage(ChatColor.GOLD + winner + " Won The Game!!");
		playTimer(timer);

		for (Entity e : Bukkit.getWorld(plugin.WORLD_NAME).getEntities())
		{
			if(e instanceof Player)
			{
				Player p = (Player)e;
				p.setGameMode(GameMode.SPECTATOR);
				plugin.scoreboardHandler.updateScoreboard(plugin.scoreboardHandler.manager, p, new int[] {1,1,1,0,0,0});
			}
			if(e instanceof Item)
			{
				e.remove();
			}
		}
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				initMB();
			}
		}, 20L*timer);
	}

	@SuppressWarnings("deprecation")
	public void resetMap(World w)
	{
		for(Block b : blocksFromTwoPoints(new Location(w, 207, 53, 1383), new Location(w, 279, 53, 1311)))
		{
			if(b.getType() == Material.AIR || b.getType() == Material.MELON_BLOCK || b.getType() == Material.WOOD)
			{
				b.setType(Material.WOOD);
				b.setData((byte)0);
			}
		}
	}

	public void playTimer(final int i)
	{
		int i1 = 0;
		if(i > 1)
		{
			i1 = i-1;
		}
		final int i2 = i1;
		if(i1 != 0)
		{
			boolean isDivisibleBy10 = i % 10 == 0;
			if(isDivisibleBy10 || i < 10)
			{
				Bukkit.broadcastMessage(ChatColor.GREEN + "Match starting in "+i+"s!");
			}
			this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

				public void run() {
					playTimer(i2);
				}
			}, 20L);
		}
		else
		{
			Bukkit.broadcastMessage(ChatColor.GOLD + "New Match Starting Now!");
		}
	}
}
