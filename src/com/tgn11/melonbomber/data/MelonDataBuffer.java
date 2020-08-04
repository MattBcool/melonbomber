package com.tgn11.melonbomber.data;

import java.util.ArrayList;

import org.bukkit.entity.Entity;


public class MelonDataBuffer 
{
	public String owner;
	public ArrayList<MelonData> melons = new ArrayList<MelonData>();
	
	public MelonDataBuffer(int a, boolean b, String d, Entity e)
	{
		update(a,b,d,e);
	}
	
	public void update(int a, boolean b, String d, Entity e)
	{
		owner = d;
		MelonData md = new MelonData(this, e, a, b);
		melons.add(md);
	}
	
	public MelonData getContainsMelon(Entity e)
	{
		for(MelonData mel : melons)
		{
			if(mel != null)
			{
				if(mel.melon != null && (mel.melon == e || mel.melon.getUniqueId().toString().equals(e.getUniqueId().toString())))
				{
					return mel;
				}
			}
		}
		return null;
	}
	
	public Entity getMelon(Entity e)
	{
		for(MelonData mel : melons)
		{
			if(mel != null)
			{
				if(mel.melon == e)
				{
					return mel.melon;
				}
			}
		}
		return null;
	}
}
