package l2r.gameserver.scripts.ai.npc.BloodAltars;

import javolution.util.FastList;
import l2r.Config;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.util.Rnd;

public class OrenBloodAltar extends Quest
{
	private static final long delay = Config.CHANGE_STATUS * 60 * 1000;
	
	private final FastList<L2Npc> deadnpcs = new FastList<>();
	private final FastList<L2Npc> alivenpcs = new FastList<>();
	private final FastList<L2Npc> bosses = new FastList<>();
	
	protected boolean progress1 = false;
	protected boolean progress2 = false;
	
	private static final int[][] bossGroups =
	{
		{
			25767,
			80328,
			46792,
			-3189,
			36123
		},
		{
			25770,
			80520,
			47368,
			-3193,
			36736
		}
	};
	
	private static final int[][] BLOODALTARS_DEAD_NPC =
	{
		{
			4328,
			80328,
			47192,
			-3176,
			53375
		},
		{
			4328,
			80040,
			47176,
			-3176,
			16000
		},
		{
			4327,
			80184,
			47272,
			-3178,
			6000
		}
	};
	
	private static final int[][] BLOODALTARS_ALIVE_NPC =
	{
		{
			4325,
			80328,
			47192,
			-3176,
			53375
		},
		{
			4325,
			80040,
			47176,
			-3176,
			16000
		},
		{
			4324,
			80184,
			47272,
			-3178,
			6000
		}
	};
	
	public OrenBloodAltar(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		manageNpcs(true);
		
		addKillId(25767);
		addKillId(25770);
		
		ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
		{
			@Override
			public void run()
			{
				OrenBloodAltar.this.changestatus();
			}
		}, delay);
	}
	
	public static void main(String[] args)
	{
		new OrenBloodAltar(-1, OrenBloodAltar.class.getSimpleName(), "ai");
	}
	
	protected void manageNpcs(boolean spawnAlive)
	{
		if (spawnAlive)
		{
			for (int[] spawn : BLOODALTARS_ALIVE_NPC)
			{
				L2Npc npc = addSpawn(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4], false, 0L, false);
				if (npc != null)
				{
					alivenpcs.add(npc);
				}
			}
			
			if (!deadnpcs.isEmpty())
			{
				for (L2Npc npc : deadnpcs)
				{
					if (npc != null)
					{
						npc.deleteMe();
					}
				}
			}
			deadnpcs.clear();
		}
		else
		{
			for (int[] spawn : BLOODALTARS_DEAD_NPC)
			{
				L2Npc npc = addSpawn(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4], false, 0L, false);
				if (npc != null)
				{
					deadnpcs.add(npc);
				}
			}
			
			if (!alivenpcs.isEmpty())
			{
				for (L2Npc npc : alivenpcs)
				{
					if (npc != null)
					{
						npc.deleteMe();
					}
				}
			}
			alivenpcs.clear();
		}
	}
	
	protected void manageBosses(boolean spawn)
	{
		if (spawn)
		{
			for (int[] bossspawn : bossGroups)
			{
				L2Npc boss = addSpawn(bossspawn[0], bossspawn[1], bossspawn[2], bossspawn[3], bossspawn[4], false, 0L, false);
				if (boss != null)
				{
					bosses.add(boss);
				}
				
			}
			
		}
		else if (!bosses.isEmpty())
		{
			for (L2Npc boss : bosses)
			{
				if (boss != null)
				{
					boss.deleteMe();
				}
			}
		}
	}
	
	protected void changestatus()
	{
		ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
		{
			@Override
			public void run()
			{
				if (Rnd.chance(Config.CHANCE_SPAWN))
				{
					OrenBloodAltar.this.manageNpcs(false);
					OrenBloodAltar.this.manageBosses(true);
				}
				else
				{
					OrenBloodAltar.this.manageBosses(false);
					OrenBloodAltar.this.manageNpcs(true);
					ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
					{
						@Override
						public void run()
						{
							OrenBloodAltar.this.changestatus();
						}
					}, Config.RESPAWN_TIME * 60 * 1000);
				}
			}
		}, 10000L);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		int npcId = npc.getNpcId();
		
		if (npcId == 25767)
		{
			progress1 = true;
		}
		
		if (npcId == 25770)
		{
			progress2 = true;
		}
		
		if ((progress1) && (progress2))
		{
			ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
			{
				@Override
				public void run()
				{
					progress1 = false;
					progress2 = false;
					
					OrenBloodAltar.this.manageBosses(false);
					OrenBloodAltar.this.manageNpcs(true);
					ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
					{
						@Override
						public void run()
						{
							OrenBloodAltar.this.changestatus();
						}
					}, Config.RESPAWN_TIME * 60 * 1000);
				}
			}, 30000L);
		}
		
		return super.onKill(npc, player, isSummon);
	}
}