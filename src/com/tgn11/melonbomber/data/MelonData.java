package com.tgn11.melonbomber.data;

import org.bukkit.entity.Entity;

public class MelonData
{
	public Entity melon;
	public int cpower;
	public boolean chasPiercing;
	public MelonDataBuffer parentBuffer;
	
	public MelonData(MelonDataBuffer a, Entity e, int power, boolean pierce)
	{
		parentBuffer = a;
		melon = e;
		cpower = power;
		chasPiercing = pierce;
	}

}
