package l2r.gameserver.scripts.quests;

import java.util.HashMap;
import java.util.Map;

import l2r.gameserver.datatables.SkillData;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.L2Summon;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.serverpackets.NpcSay;
import l2r.gameserver.util.Util;

public class Q00421_LittleWingAdventures extends Quest
{
	private static final String qn = "Q00421_LittleWingAdventures";
	
	private static final Map<Integer, Integer> killedTrees = new HashMap<>();
	
	private static final int CRONOS = 30610;
	private static final int MIMYU = 30747;
	private static final int FT_LEAF = 4325;
	
	public Q00421_LittleWingAdventures(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(CRONOS);
		addTalkId(CRONOS);
		addTalkId(MIMYU);
		
		for (int i : Util.getRange(27185, 27189))
		{
			addAttackId(i);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return getNoQuestMsg(player);
		}
		
		final L2Summon summon = player.getSummon();
		if (event.equalsIgnoreCase("30610-05.htm"))
		{
			if ((st.getQuestItemsCount(3500) + st.getQuestItemsCount(3501) + st.getQuestItemsCount(3502)) == 1)
			{
				if (st.hasQuestItems(3500))
				{
					L2ItemInstance item = player.getInventory().getItemByItemId(3500);
					if (item.getEnchantLevel() < 55)
					{
						htmltext = "30610-06.htm";
						st.exitQuest(true);
					}
					else
					{
						st.setState(State.STARTED);
						st.set("summonOid", "" + item.getObjectId());
						st.set("cond", "1");
						st.set("id", "1");
						st.playSound("ItemSound.quest_accept");
					}
				}
				else if (st.hasQuestItems(3501))
				{
					L2ItemInstance item = player.getInventory().getItemByItemId(3501);
					if (item.getEnchantLevel() < 55)
					{
						htmltext = "30610-06.htm";
						st.exitQuest(true);
					}
					else
					{
						st.setState(State.STARTED);
						st.set("summonOid", "" + item.getObjectId());
						st.set("cond", "1");
						st.set("id", "1");
						st.playSound("ItemSound.quest_accept");
					}
				}
				else if (st.hasQuestItems(3502))
				{
					L2ItemInstance item = player.getInventory().getItemByItemId(3502);
					if (item.getEnchantLevel() < 55)
					{
						htmltext = "30610-06.htm";
						st.exitQuest(true);
					}
					else
					{
						st.setState(State.STARTED);
						st.set("summonOid", "" + item.getObjectId());
						st.set("cond", "1");
						st.set("id", "1");
						st.playSound("ItemSound.quest_accept");
					}
				}
			}
			else
			{
				htmltext = "30610-06.htm";
				st.exitQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("30747-02.htm"))
		{
			if (summon != null)
			{
				if (summon.getControlObjectId() == st.getInt("summonOid"))
				{
					htmltext = "30747-04.htm";
				}
				else
				{
					htmltext = "30747-03.htm";
				}
			}
		}
		else if (event.equalsIgnoreCase("30747-05.htm"))
		{
			if (summon != null)
			{
				if (summon.getControlObjectId() == st.getInt("summonOid"))
				{
					htmltext = "30747-05.htm";
					st.giveItems(FT_LEAF, 4);
					st.set("cond", "2");
					st.set("id", "0");
					st.playSound("ItemSound.quest_middle");
				}
				else
				{
					htmltext = "30747-06.htm";
				}
			}
			else
			{
				htmltext = "30747-06.htm";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		QuestState st = player.getQuestState(qn);
		if (st == null)
		{
			st = newQuestState(player);
		}
		
		final int npcId = npc.getId();
		final L2Summon summon = player.getSummon();
		switch (st.getState())
		{
			case State.CREATED:
			{
				if (npcId == CRONOS)
				{
					if ((player.getLevel() < 45) && (st.hasQuestItems(3500) || st.hasQuestItems(3501) || st.hasQuestItems(3502)))
					{
						htmltext = "30610-01.htm";
						st.exitQuest(true);
					}
					else if ((player.getLevel() >= 45) && ((st.getQuestItemsCount(3500) + st.getQuestItemsCount(3501) + st.getQuestItemsCount(3502)) > 1))
					{
						htmltext = "30610-02.htm";
						st.exitQuest(true);
					}
					else if ((player.getLevel() >= 45) && ((st.getQuestItemsCount(3500) + st.getQuestItemsCount(3501) + st.getQuestItemsCount(3502)) == 1))
					{
						if (st.hasQuestItems(3500))
						{
							if (player.getInventory().getItemByItemId(3500).getEnchantLevel() < 55)
							{
								htmltext = "30610-03.htm";
							}
							else
							{
								htmltext = "30610-04.htm";
							}
						}
						else if (st.hasQuestItems(3501))
						{
							if (player.getInventory().getItemByItemId(3501).getEnchantLevel() < 55)
							{
								htmltext = "30610-03.htm";
							}
							else
							{
								htmltext = "30610-04.htm";
							}
						}
						else if (st.hasQuestItems(3502))
						{
							if (player.getInventory().getItemByItemId(3502).getEnchantLevel() < 55)
							{
								htmltext = "30610-03.htm";
							}
							else
							{
								htmltext = "30610-04.htm";
							}
						}
					}
				}
				break;
			}
			case State.STARTED:
			{
				switch (npcId)
				{
					case CRONOS:
					{
						htmltext = "30610-07.htm";
						break;
					}
					case MIMYU:
					{
						final int id = st.getInt("id");
						if (id == 1)
						{
							st.set("id", "2");
							htmltext = "30747-01.htm";
						}
						else if (id == 2)
						{
							if (summon != null)
							{
								if (summon.getControlObjectId() == st.getInt("summonOid"))
								{
									htmltext = "30747-04.htm";
								}
								else
								{
									htmltext = "30747-03.htm";
								}
							}
							else
							{
								htmltext = "30747-02.htm";
							}
						}
						else if (id == 0)
						{
							htmltext = "30747-07.htm";
						}
						else if ((id > 0) && (id < 15) && st.hasQuestItems(FT_LEAF))
						{
							htmltext = "30747-11.htm";
						}
						else if ((id == 15) && !st.hasQuestItems(FT_LEAF))
						{
							if (summon != null)
							{
								if (summon.getControlObjectId() == st.getInt("summonOid"))
								{
									st.set("id", "16");
									htmltext = "30747-13.htm";
								}
								else
								{
									htmltext = "30747-14.htm";
								}
							}
							else
							{
								htmltext = "30747-12.htm";
							}
						}
						else if (id == 16)
						{
							if (summon == null)
							{
								htmltext = "30747-15.htm";
							}
							else if ((st.getQuestItemsCount(3500) + st.getQuestItemsCount(3501) + st.getQuestItemsCount(3502)) == 1)
							{
								if (st.hasQuestItems(3500))
								{
									final L2ItemInstance item = player.getInventory().getItemByItemId(3500);
									if (item.getObjectId() == st.getInt("summonOid"))
									{
										st.takeItems(3500, 1);
										st.giveItems(4422, 1);
										htmltext = "30747-16.htm";
										st.exitQuest(true);
										st.playSound("ItemSound.quest_finish");
									}
									else
									{
										npc.setTarget(player);
										L2Skill skill = SkillData.getInstance().getInfo(4167, 1);
										if (skill != null)
										{
											skill.getEffects(npc, player);
										}
										htmltext = "30747-18.htm";
									}
								}
								else if (st.hasQuestItems(3501))
								{
									final L2ItemInstance item = player.getInventory().getItemByItemId(3501);
									if (item.getObjectId() == st.getInt("summonOid"))
									{
										st.takeItems(3501, 1);
										st.giveItems(4423, 1);
										htmltext = "30747-16.htm";
										st.exitQuest(true);
										st.playSound("ItemSound.quest_finish");
									}
									else
									{
										npc.setTarget(player);
										L2Skill skill = SkillData.getInstance().getInfo(4167, 1);
										if (skill != null)
										{
											skill.getEffects(npc, player);
										}
										htmltext = "30747-18.htm";
									}
								}
								else if (st.hasQuestItems(3502))
								{
									final L2ItemInstance item = player.getInventory().getItemByItemId(3502);
									if (item.getObjectId() == st.getInt("summonOid"))
									{
										st.takeItems(3502, 1);
										st.giveItems(4424, 1);
										htmltext = "30747-16.htm";
										st.exitQuest(true);
										st.playSound("ItemSound.quest_finish");
									}
									else
									{
										npc.setTarget(player);
										L2Skill skill = SkillData.getInstance().getInfo(4167, 1);
										if (skill != null)
										{
											skill.getEffects(npc, player);
										}
										htmltext = "30747-18.htm";
									}
								}
							}
							else if ((st.getQuestItemsCount(3500) + st.getQuestItemsCount(3501) + st.getQuestItemsCount(3502)) > 1)
							{
								htmltext = "30747-17.htm";
							}
						}
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, L2Skill skill)
	{
		final QuestState st = attacker.getQuestState(getName());
		if (st == null)
		{
			return null;
		}
		
		final int npcId = npc.getId();
		for (int id : killedTrees.keySet())
		{
			if ((id == attacker.getObjectId()) && (killedTrees.get(id) == npcId))
			{
				return null;
			}
		}
		
		if (isSummon && (st.getInt("id") < 16))
		{
			if ((st.getRandom(100) <= 2) && st.hasQuestItems(FT_LEAF))
			{
				st.takeItems(FT_LEAF, 1);
				st.playSound("ItemSound.quest_middle");
				npc.broadcastPacket(new NpcSay(npcId, 0, npcId, NpcStringId.GIVE_ME_A_FAIRY_LEAF));
				killedTrees.put(attacker.getObjectId(), npcId);
				if (st.getQuestItemsCount(FT_LEAF) == 0)
				{
					st.set("id", "15");
					st.set("cond", "3");
				}
			}
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q00421_LittleWingAdventures(421, qn, "");
	}
}