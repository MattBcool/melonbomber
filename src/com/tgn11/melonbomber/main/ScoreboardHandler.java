package com.tgn11.melonbomber.main;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardHandler
{
	public HashMap<String, Scoreboard> playerScoreboards = new HashMap<String, Scoreboard>();
	public ScoreboardManager manager = null;
	
	private com.tgn11.melonbomber.main.Main plugin;
	
	public ScoreboardHandler(com.tgn11.melonbomber.main.Main p)
	{
		plugin = p;
		
		manager = Bukkit.getScoreboardManager();
	}
	
	public void createWaitingScoreboard()
	{
		
	}


	public void updateScoreboard(ScoreboardManager manager, Player p, int[] values)
	{
		Scoreboard board = manager.getNewScoreboard();

		String n = p.getName();

		Objective objective = board.registerNewObjective("melonbomber", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.GREEN + "MelonBomber");

		Score score = objective.getScore("Bombs: " + ChatColor.GREEN + values[0]);
		score.setScore(6);

		Score score2 = objective.getScore("Speed: " + ChatColor.GREEN + values[1]);
		score2.setScore(5);

		Score score3 = objective.getScore("Power: " + ChatColor.GREEN + values[2]);
		score3.setScore(4);

		String str1 = (values[3] == 1)? ChatColor.GREEN + "Activated"   : ChatColor.DARK_RED + "Disabled";
		Score score4 = objective.getScore("Piercing: " + str1);
		score4.setScore(2);

		String str2 = (values[4] == 1)? ChatColor.GREEN + "Activated"  : ChatColor.DARK_RED + "Disabled";
		Score score5 = objective.getScore("Line Bomb: " + str2);
		score5.setScore(1);

		String str3 = (values[5] == 1)? ChatColor.GREEN + "Activated" : ChatColor.DARK_RED + "Disabled";
		Score score6 = objective.getScore("Melon Kick: " + str3);
		score6.setScore(3);

		plugin.dataHandler.bombs.put(n, values[0]);
		plugin.dataHandler.speed.put(n, values[1]);
		plugin.dataHandler.power.put(n, values[2]);
		if(values[3] == 1)
		{
			plugin.dataHandler.hasPiercing.put(n, true);
		}
		else
		{
			plugin.dataHandler.hasPiercing.put(n, false);
		}

		if(values[4] == 1)
		{
			plugin.dataHandler.hasLineBomb.put(n, true);
		}
		else
		{
			plugin.dataHandler.hasLineBomb.put(n, false);
		}

		if(values[5] == 1)
		{
			plugin.dataHandler.hasMelonKick.put(n, true);
		}
		else
		{
			plugin.dataHandler.hasMelonKick.put(n, false);
		}

		p.setScoreboard(board);
		playerScoreboards.put(p.getName(), board);
	}
	

	public void updateScoreboardCheck(ScoreboardManager manager, Player p, int[] values)
	{
		String n = p.getName();
		if(!playerScoreboards.containsKey(n))
		{
			return;
		}
		Scoreboard board = playerScoreboards.get(n);

		Objective objective = board.getObjective("melonbomber");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.GREEN + "MelonBomber");

		int sci1 = 1;
		int sci2 = 1;
		int sci3 = 1;
		int sci4 = 0;
		int sci5 = 0;
		int sci6 = 0;

		try{
			if(values[0] == -1)
			{
				for(int i=0; i < 11; i++)
				{
					Score score = objective.getScore("Bombs: " + ChatColor.GREEN + i);
					if(score.getScore() != 0 && i != 0)
					{
						sci1 = i;
					}
				}
			}else{
				if(values[0] == 0)
				{
					sci1 = values[0];
				}else{
					boolean success = false;
					for(int i=0; i < 11; i++)
					{
						Score score = objective.getScore("Bombs: " + ChatColor.GREEN + i);
						if(score.getScore() != 0 && i != 0)
						{
							success = true;
							if(i + values[0] >= 11)
							{
								sci1 = 10;
							}else{
								sci1 = i + values[0];
							}
						}
					}
					if(!success)
					{
						sci1 += values[0];
					}
				}
			}

			if(values[1] == -1)
			{
				for(int i=0; i < 11; i++)
				{
					Score score = objective.getScore("Speed: " + ChatColor.GREEN + i);
					if(score.getScore() != 0 && i != 0)
					{
						sci2 = i;
					}
				}
			}else{
				if(values[1] == 0)
				{
					sci2 = values[1];
				}else{
					boolean success = false;
					for(int i=0; i < 11; i++)
					{
						Score score = objective.getScore("Speed: " + ChatColor.GREEN + i);
						if(score.getScore() != 0 && i != 0)
						{
							success = true;
							if(i + values[1] >= 11)
							{
								sci2 = 10;
							}else{
								sci2 = i + values[1];
							}
						}
					}
					
					if(!success)
					{
						sci2 += values[1];
					}
				}
			}

			if(values[2] == -1)
			{
				for(int i=0; i < 11; i++)
				{
					Score score = objective.getScore("Power: " + ChatColor.GREEN + i);
					if(score.getScore() != 0 && i != 0)
					{
						sci3 = i;
					}
				}
			}else{
				if(values[2] == 0)
				{
					sci3 = values[2];
				}else{
					boolean success = false;
					for(int i=0; i < 11; i++)
					{
						Score score = objective.getScore("Power: " + ChatColor.GREEN + i);
						if(score.getScore() != 0 && i != 0)
						{
							success = true;
							if(i + values[2] >= 11)
							{
								sci3 = 10;
							}else{
								sci3 = i + values[2];
							}
						}
					}
					if(!success)
					{
						sci3 += values[2];
					}
				}
			}

			if(values[3] == -1)
			{
				Score score = objective.getScore("Piercing: " + ChatColor.GREEN + "Activated");

				if(score.getScore() != 0)
				{
					sci4 = 1;
				}else{
					sci4 = 0;
				}
			}else{
				sci4 = values[3];
			}

			if(values[4] == -1)
			{
				Score score = objective.getScore("Line Bomb: " + ChatColor.GREEN + "Activated");

				if(score.getScore() != 0)
				{
					sci5 = 1;
				}else{
					sci5 = 0;
				}
			}else{
				sci5 = values[4];
			}

			if(values[5] == -1)
			{
				Score score = objective.getScore("Melon Kick: " + ChatColor.GREEN + "Activated");

				if(score.getScore() != 0)
				{
					sci6 = 1;
				}else{
					sci6 = 0;
				}
			}else{
				sci6 = values[5];
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.updateScoreboard(manager, p, new int[] {sci1, sci2, sci3, sci4, sci5, sci6});
	}
	
}
