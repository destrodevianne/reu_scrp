package l2r.gameserver.scripts.ai.npc.DragonVortex;

import javolution.util.FastList;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;

public class DragonVortex extends Quest
{
	private static final int VORTEX_1 = 32871;
	private static final int VORTEX_2 = 32892;
	private static final int VORTEX_3 = 32893;
	private static final int VORTEX_4 = 32894;
	
	protected final FastList<L2Npc> bosses1 = new FastList<>();
	protected final FastList<L2Npc> bosses2 = new FastList<>();
	protected final FastList<L2Npc> bosses3 = new FastList<>();
	protected final FastList<L2Npc> bosses4 = new FastList<>();
	
	protected boolean progress1 = false;
	protected boolean progress2 = false;
	protected boolean progress3 = false;
	protected boolean progress4 = false;
	
	private static final int LARGE_DRAGON_BONE = 17248;
	
	private static final int[] RAIDS =
	{
		25724, // Muscle Bomber
		25723, // Spike Slasher
		25722, // Shadow Summoner
		25721, // Blackdagger Wing
		25720, // Bleeding Fly
		25719, // Dust Rider
		25718, // Emerald Horn
	};
	
	private L2Npc boss1;
	private L2Npc boss2;
	private L2Npc boss3;
	private L2Npc boss4;
	
	private static final int DESPAWN_DELAY = 1800000;
	
	public DragonVortex(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addFirstTalkId(VORTEX_1, VORTEX_2, VORTEX_3, VORTEX_4);
		addStartNpc(VORTEX_1, VORTEX_2, VORTEX_3, VORTEX_4);
		addTalkId(VORTEX_1, VORTEX_2, VORTEX_3, VORTEX_4);
		
		for (int i : RAIDS)
		{
			addKillId(i);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("Spawn"))
		{
			if (npc.getNpcId() == VORTEX_1)
			{
				if (progress1)
				{
					return "32871-03.htm";
				}
				
				if (hasQuestItems(player, LARGE_DRAGON_BONE))
				{
					takeItems(player, LARGE_DRAGON_BONE, 1);
					boss1 = addSpawn(RAIDS[getRandom(RAIDS.length)], new Location(player.getX() - 300, player.getY() - 100, player.getZ() - 2, player.getHeading()), false, 0);
					progress1 = true;
					if (boss1 != null)
					{
						bosses1.add(boss1);
						ThreadPoolManager.getInstance().scheduleGeneral(new SpawnVortexBoss(bosses1, 1), DESPAWN_DELAY);
					}
					return "32871-01.htm";
				}
				return "32871-02.htm";
			}
			
			if (npc.getNpcId() == VORTEX_2)
			{
				if (progress2)
				{
					return "32871-03.htm";
				}
				
				if (hasQuestItems(player, LARGE_DRAGON_BONE))
				{
					takeItems(player, LARGE_DRAGON_BONE, 1);
					boss2 = addSpawn(RAIDS[getRandom(RAIDS.length)], new Location(player.getX() - 300, player.getY() - 100, player.getZ() - 2, player.getHeading()), false, 0);
					progress2 = true;
					if (boss2 != null)
					{
						bosses2.add(boss2);
						ThreadPoolManager.getInstance().scheduleGeneral(new SpawnVortexBoss(bosses2, 2), DESPAWN_DELAY);
					}
					return "32871-01.htm";
				}
				return "32871-02.htm";
			}
			
			if (npc.getNpcId() == VORTEX_3)
			{
				if (progress3)
				{
					return "32871-03.htm";
				}
				
				if (hasQuestItems(player, LARGE_DRAGON_BONE))
				{
					takeItems(player, LARGE_DRAGON_BONE, 1);
					boss3 = addSpawn(RAIDS[getRandom(RAIDS.length)], new Location(player.getX() - 300, player.getY() - 100, player.getZ() - 2, player.getHeading()), false, 0);
					progress3 = true;
					if (boss3 != null)
					{
						bosses3.add(boss3);
						ThreadPoolManager.getInstance().scheduleGeneral(new SpawnVortexBoss(bosses3, 3), DESPAWN_DELAY);
					}
					return "32871-01.htm";
				}
				return "32871-02.htm";
			}
			
			if (npc.getNpcId() == VORTEX_4)
			{
				if (progress4)
				{
					return "32871-03.htm";
				}
				
				if (hasQuestItems(player, LARGE_DRAGON_BONE))
				{
					takeItems(player, LARGE_DRAGON_BONE, 1);
					boss4 = addSpawn(RAIDS[getRandom(RAIDS.length)], new Location(player.getX() - 300, player.getY() - 100, player.getZ() - 2, player.getHeading()), false, 0);
					progress4 = true;
					if (boss4 != null)
					{
						bosses4.add(boss4);
						ThreadPoolManager.getInstance().scheduleGeneral(new SpawnVortexBoss(bosses4, 4), DESPAWN_DELAY);
					}
					return "32871-01.htm";
				}
				return "32871-02.htm";
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		
		if (st == null)
		{
			st = newQuestState(player);
		}
		
		return "32871.htm";
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		if (progress1)
		{
			progress1 = false;
		}
		
		if (progress2)
		{
			progress2 = false;
		}
		
		if (progress3)
		{
			progress3 = false;
		}
		
		if (progress4)
		{
			progress4 = false;
		}
		return super.onKill(npc, player, isSummon);
	}
	
	private class SpawnVortexBoss implements Runnable
	{
		private final FastList<L2Npc> _bosses;
		private final int _vortex;
		
		public SpawnVortexBoss(FastList<L2Npc> bosses, int vortex)
		{
			_bosses = bosses;
			_vortex = vortex;
		}
		
		@Override
		public void run()
		{
			if (!_bosses.isEmpty())
			{
				for (L2Npc boss : _bosses)
				{
					if (boss != null)
					{
						boss.deleteMe();
						switch (_vortex)
						{
							case 1:
								progress1 = false;
								break;
							case 2:
								progress2 = false;
								break;
							case 3:
								progress3 = false;
								break;
							case 4:
								progress4 = false;
								break;
						}
					}
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		new DragonVortex(-1, "DragonVortex", "ai/npc");
	}
}