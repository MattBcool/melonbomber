package com.tgn11.melonbomber.listeners;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingSand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.tgn11.melonbomber.data.MelonData;
import com.tgn11.melonbomber.data.MelonDataBuffer;
import com.tgn11.melonbomber.main.Main;

@SuppressWarnings("deprecation")
public class BlockListener implements Listener
{
	com.tgn11.melonbomber.main.Main plugin;

	public BlockListener(com.tgn11.melonbomber.main.Main plugin)
	{
		super();
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockChange(EntityChangeBlockEvent e)
	{
		Entity en = e.getEntity();

		if(!(en instanceof FallingSand))
		{
			return;
		}

		MelonData md = null;

		for (Map.Entry<String, MelonDataBuffer> entry : plugin.dataHandler.entityMelonData.entrySet())
		{
			//String key = entry.getKey();
			MelonDataBuffer value = entry.getValue();

			if(value != null)
			{
				md = value.getContainsMelon(en);
				if(md != null)
				{
					break;
				}
			}
		}

		if(plugin.dataHandler.melondata.containsKey(en))
		{
			final Location loc = en.getLocation();
			loc.setY(loc.getY()+.99);
			Entity nen = en.getWorld().spawnFallingBlock(loc, Material.MELON_BLOCK, (byte)0);
			Vector v = plugin.dataHandler.melondata.get(en);
			plugin.dataHandler.melondata.put(nen, v);
			v.multiply(0.7);
			nen.setVelocity(v);

			if(md == null)
			{
				return;
			}else{
				md.melon = nen;
			}

			for(Player ply : Bukkit.getOnlinePlayers())
			{
				if(ply.getLocation().distance(nen.getLocation()) < 1)
				{
					doExplodeThing(nen,loc,md);
				}
			}

			String name = en.getCustomName();

			if(name == "3")
			{
				nen.setCustomName("2");
			}
			if(name == "2")
			{
				nen.setCustomName("1");
			}

			if(name == "1")
			{
				doExplodeThing(nen,loc,md);
			}

			if(name == null)
			{
				nen.setCustomName("3");
			}
			nen.setCustomNameVisible(true);

			en.remove();
			plugin.dataHandler.melondata.remove(en);
			e.setCancelled(true);
		}
	}

	public void doExplodeThing(final Entity nen, final Location loc, final MelonData md)
	{
		nen.remove();
		final Block b = loc.getBlock();

		if(b.getType() == Material.AIR)
		{
			b.setType(Material.MELON_BLOCK);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
			{
				public void run()
				{

					if(b.getType() != Material.MELON_BLOCK)
					{
						return;
					}

					if(md == null)
					{
						Bukkit.broadcastMessage("[FATAL ERROR] Melon Data is null.");
						return;
					}

					Random rand = new Random();
					loc.getWorld().playSound(loc, Sound.EXPLODE, 1f, 0.9f+rand.nextFloat()/3);
					b.setType(Material.AIR);
					ArrayList<Location> locsZP = new ArrayList<Location>();
					ArrayList<Location> locsZN = new ArrayList<Location>();
					ArrayList<Location> locsXP = new ArrayList<Location>();
					ArrayList<Location> locsXN = new ArrayList<Location>();

					locsZP.add(loc);
					locsZN.add(loc);
					locsXP.add(loc);
					locsXN.add(loc);

					int cpower = md.cpower;

					if(cpower <= 0)
					{
						cpower = 1;
					}

					for(int ci = 0; ci < cpower; ci++)
					{
						locsZP.add(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()+ci));
						locsZN.add(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()-ci));
						locsXP.add(new Location(loc.getWorld(), loc.getX()+ci, loc.getY(), loc.getZ()));
						locsXN.add(new Location(loc.getWorld(), loc.getX()-ci, loc.getY(), loc.getZ()));
					}
					boolean pierceThrough = md.chasPiercing;//md3.chasPiercing; //true = does 8 block thing
					doLocationLoop(locsZP, pierceThrough, md, 0);
					doLocationLoop(locsZN, pierceThrough, md, 1);
					doLocationLoop(locsXP, pierceThrough, md, 2);
					doLocationLoop(locsXN, pierceThrough, md, 3);
				}

				private void doLocationLoop(ArrayList<Location> locs, boolean dr, MelonData md, int inte)
				{
					int i = 0;
					for(Location l : locs)
					{
						Location p = null;
						if((i-1) >= 0 && locs.get(i-1) != null)
						{
							p = locs.get(i-1);
						}

						boolean bb = doBlockExplosion(l,p,dr, md, inte);
						if(bb)
						{
							return;
						}
						i++;
					}
				}

				private boolean doBlockExplosion(Location l, Location p, boolean pt, MelonData md, int inte)
				{
					boolean marked = false;
					if(p != null)
					{
						if(p.getBlock().getType() == Material.STONE)
						{
							return true;
						}
					}

					if(!pt)
					{
						if(l.getBlock().getType() == Material.WOOD)
						{
							marked = true;
						}
					}


					if(l.getBlock().getType() == Material.WOOD)
					{
						Random random = new Random();
						boolean doSpruce = (random.nextInt(100) > 75);
						boolean doItems = false;

						if(!doSpruce || l.getBlock().getData() == 5 || pt)
						{
							l.getBlock().setType(Material.FIRE);
							doItems = true;
						}else{
							boolean doDark = (random.nextInt(100) > 85);
							l.getBlock().setType(Material.WOOD);

							if(doDark || l.getBlock().getData() == 1)
							{
								l.getBlock().setData((byte)5);
							}else{
								l.getBlock().setData((byte)1);
							}
						}

						if((random.nextInt(100) > 100-10) && doItems)
						{
							//set to 3 if not working
							int pickupAmount = 5;
							int pickup = random.nextInt(pickupAmount);
							ItemStack stack = null;

							if(pickup == 0)
							{
								stack = Main.createCustomItemStack(Material.MELON_BLOCK, ChatColor.DARK_GREEN, "Bombs Upgrade", "An Upgrade for MelonBomber");
							}
							if(pickup == 1)
							{
								stack = Main.createCustomItemStack(Material.DIAMOND_BOOTS, ChatColor.DARK_GREEN, "Speed Upgrade", "An Upgrade for MelonBomber");
							}
							if(pickup == 2)
							{
								stack = Main.createCustomItemStack(Material.TNT, ChatColor.DARK_GREEN, "Power Upgrade", "An Upgrade for MelonBomber");
							}
							if(pickup == 3)
							{
								stack = Main.createCustomItemStack(Material.DIAMOND_SWORD, ChatColor.DARK_GREEN, "Piercing Upgrade", "An Upgrade for MelonBomber");
							}
							if(pickup == 4)
							{
								stack = Main.createCustomItemStack(Material.LEATHER_BOOTS, ChatColor.DARK_GREEN, "Melon Kick Upgrade", "An Upgrade for MelonBomber");
							}
							if(pickup < pickupAmount)
							{
								if(stack != null)
								{
									loc.getWorld().dropItemNaturally(l, stack);
								}
							}
						}
					}

					if(l.getBlock().getType() == Material.AIR)
					{
						l.getBlock().setType(Material.FIRE);
					}

					if(l.getBlock().getType() == Material.FIRE)
					{
						for(Player ply : Bukkit.getOnlinePlayers())
						{
							String n = ply.getName();
							Location blockc = null;
							blockc = ply.getLocation();
							if(blockc.getBlock().getType() == Material.FIRE)
							{
								String k = md.parentBuffer.owner;
								//Bukkit.broadcastMessage("K VALUE: "+k);
								//Bukkit.broadcastMessage("N VALUE: "+n);
								plugin.dataHandler.killer.put(n, k);
								Player killer = Bukkit.getPlayer(k);
								killer.getWorld().playSound(killer.getLocation(), Sound.LEVEL_UP, 1f, 20f);
								
								ply.damage(20D);
							}
						}

						l.getBlock().setType(Material.AIR);
					}	

					l.getWorld().playEffect(l, Effect.MOBSPAWNER_FLAMES, 1);
					return marked;
				}
			}, 20L*5);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		Player p = e.getPlayer();
		if(p.getGameMode() != GameMode.CREATIVE)
		{
			p.sendMessage(ChatColor.RED + "Block-Breaking Is Not Allowed!");
			e.setCancelled(true);
		}
	}
}
