package com.tgn11.melonbomber.main;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.tgn11.melonbomber.data.MelonDataBuffer;

public class DataHandler
{

	public HashMap<Entity, Vector> melondata = new HashMap<Entity, Vector>();
	public HashMap<String, Boolean> justMeloned = new HashMap<String, Boolean>();
	public boolean isRestarting = false;
	public boolean matchInProgress = false;
	public ArrayList<Location> playerSpawns = new ArrayList<Location>();
	public HashMap<String, MelonDataBuffer> entityMelonData = new HashMap<String, MelonDataBuffer>();
	public HashMap<String, String> killer = new HashMap<String, String>();

	public HashMap<String, Integer> bombs = new HashMap<String, Integer>();
	public HashMap<String, Integer> speed = new HashMap<String, Integer>();
	public HashMap<String, Integer> power = new HashMap<String, Integer>();
	public HashMap<String, Boolean> hasPiercing = new HashMap<String, Boolean>();
	public HashMap<String, Boolean> hasLineBomb = new HashMap<String, Boolean>();
	public HashMap<String, Boolean> hasMelonKick = new HashMap<String, Boolean>();
	
	private Main plugin;
	
	public DataHandler(Main m)
	{
		plugin = m;
		addPlayerSpawns(plugin.WORLD_NAME);
	}

	private void addPlayerSpawns(String w)
	{
		playerSpawns.add(new Location(Bukkit.getWorld(w), 277, 53.5, 1313));
		playerSpawns.add(new Location(Bukkit.getWorld(w), 209, 53.5, 1313));
		playerSpawns.add(new Location(Bukkit.getWorld(w), 209, 53.5, 1381));
		playerSpawns.add(new Location(Bukkit.getWorld(w), 277, 53.5, 1381));
		playerSpawns.add(new Location(Bukkit.getWorld(w), 243, 53.5, 1347));
		playerSpawns.add(new Location(Bukkit.getWorld(w), 229, 53.5, 1335));
		playerSpawns.add(new Location(Bukkit.getWorld(w), 257, 53.5, 1335));
		playerSpawns.add(new Location(Bukkit.getWorld(w), 257, 53.5, 1361));
		playerSpawns.add(new Location(Bukkit.getWorld(w), 229, 53.5, 1361));
	}
}
