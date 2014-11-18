/*
 * Copyright (C) 2004-2014 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.gameserver.scripts.quests;

import javolution.text.TextBuilder;
import javolution.util.FastMap;
import l2r.gameserver.datatables.xml.SkillData;
import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.L2Clan;
import l2r.gameserver.model.L2ClanMember;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.network.serverpackets.MagicSkillLaunched;
import l2r.gameserver.network.serverpackets.MagicSkillUse;
import l2r.gameserver.network.serverpackets.NpcSay;
import l2r.gameserver.network.serverpackets.SocialAction;
import l2r.util.Rnd;

public final class Q00501_ProofOfClanAlliance extends Quest
{
	private static final boolean DEBUG = false;
	// a.k.a. Pledge of Blood
	private static final int NEEDED_MEMBERS = 3;
	private static final int SKILL_POISON = 4082;
	private static final int SKILL_DEATH = 4083;
	private static final int LOYALTY_TIMER = 4 * 1000;
	private static final int CHEST_TIMER = 300 * 1000;
	
	// Quest NPCs
	private static final int SIR_KRISTOF_RODEMAI = 30756;
	private static final int STATUE_OF_OFFERING = 30757;
	private static final int ATHREA = 30758;
	private static final int KALIS = 30759;
	
	// Quest items
	private static final int HERB_OF_HARIT = 3832;
	private static final int HERB_OF_VANOR = 3833;
	private static final int HERB_OF_OEL_MAHUM = 3834;
	private static final int BLOOD_OF_EVA = 3835;
	private static final int SYMBOL_OF_LOYALTY = 3837;
	private static final int ANTIDOTE_RECIPE_LIST = 3872;
	private static final int VOUCHER_OF_FAITH = 3873;
	private static final int ALLIANCE_MANIFESTO = 3874;
	private static final int POTION_OF_RECOVERY = 3889;
	
	// Quest monsters
	private static final int OEL_MAHUM_WITCH_DOCTOR = 20576;
	private static final int HARIT_LIZARDMAN_SHAMAN = 20644;
	private static final int VANOR_SILENOS_SHAMAN = 20685;
	
	private static final int[] CHEST =
	{
		27173,
		27174,
		27175,
		27176,
		27177
	};
	
	private static final String CHEST_KILLED = "##########Bingo!##########";
	
	private static final Location[] CHEST_POS =
	{
		new Location(102273, 103433, -3512),
		new Location(102190, 103379, -3524),
		new Location(102107, 103325, -3533),
		new Location(102024, 103271, -3500),
		new Location(102327, 103350, -3511),
		new Location(102244, 103296, -3518),
		new Location(102161, 103242, -3529),
		new Location(102078, 103188, -3500),
		new Location(102381, 103267, -3538),
		new Location(102298, 103213, -3532),
		new Location(102215, 103159, -3520),
		new Location(102132, 103105, -3513),
		new Location(102435, 103184, -3515),
		new Location(102352, 103130, -3522),
		new Location(102269, 103076, -3533),
		new Location(102186, 103022, -3541),
	};
	
	protected static final int NEEDED_WINS = Math.min(4, CHEST_POS.length);
	
	private final FastMap<Integer, QuestClan> _questers;
	private volatile QuestClan _minigame;
	
	public Q00501_ProofOfClanAlliance()
	{
		super(501, Q00501_ProofOfClanAlliance.class.getSimpleName(), "Proof of Clan Alliance");
		
		registerQuestItems(BLOOD_OF_EVA, SYMBOL_OF_LOYALTY, ANTIDOTE_RECIPE_LIST, VOUCHER_OF_FAITH, POTION_OF_RECOVERY);
		
		_questers = new FastMap<Integer, QuestClan>().shared();
		_minigame = null;
		addStartNpc(SIR_KRISTOF_RODEMAI);
		addStartNpc(STATUE_OF_OFFERING);
		addTalkId(SIR_KRISTOF_RODEMAI);
		addTalkId(STATUE_OF_OFFERING);
		addTalkId(ATHREA);
		addTalkId(KALIS);
		addKillId(OEL_MAHUM_WITCH_DOCTOR);
		addKillId(HARIT_LIZARDMAN_SHAMAN);
		addKillId(VANOR_SILENOS_SHAMAN);
		for (int id : CHEST)
		{
			addKillId(id);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		QuestClan qc = _questers.get(player.getClanId());
		if (event.startsWith("loyalty"))
		{
			if ((qc == null) || !qc.checkLeader())
			{
				return null;
			}
			if (qc.isLoyal(player))
			{
				player.addItem(getName(), SYMBOL_OF_LOYALTY, 1, npc, true);
			}
			else if (DEBUG)
			{
				_log.warn("501_PoCA: loyalty timer is up for non-loyal player? " + qc);
			}
			return null;
		}
		else if (event.startsWith("chest"))
		{
			if ((qc == null) || !qc.checkLeader())
			{
				return null;
			}
			qc.getChests().timeout();
			_minigame = null;
			return null;
		}
		else
		{
			if (player.isClanLeader())
			{
				QuestState qs = player.getQuestState(getName());
				if ("30756-07.htm".equals(event))
				{
					qc = new QuestClan(player.getClan());
					_questers.put(player.getClanId(), qc);
					qs.set("cond", "1");
					qc.setPart(SIR_KRISTOF_RODEMAI);
					qs.setState(State.STARTED);
					qs.playSound(QuestSound.ITEMSOUND_QUEST_ACCEPT);
				}
				else if ("30759-03.htm".equals(event))
				{
					qs.set("cond", "2");
					qc.setPart(STATUE_OF_OFFERING);
				}
				else if ("30759-07.htm".equals(event))
				{
					for (int i = 0; i < NEEDED_MEMBERS; i++)
					{
						qs.takeItems(SYMBOL_OF_LOYALTY, 1);
					}
					qs.giveItems(ANTIDOTE_RECIPE_LIST, 1);
					qs.set("cond", "3");
					qc.setPart(KALIS);
					qs.addNotifyOfDeath(player);
					L2Skill skill = SkillData.getInstance().getInfo(SKILL_POISON, 1);
					if (skill == null)
					{
						_log.warn("501_PoCA: Missing skill " + SKILL_POISON + ", terminating quest!");
						qs.exitQuest(true);
					}
					else
					{
						skill.getEffects(player, player);
					}
				}
			}
			else
			{
				if ((qc == null) || !qc.checkLeader())
				{
					return null;
				}
				else if ("30757-05.htm".equals(event))
				{
					if (!qc.addLoyalMember(player))
					{
						return "30757-07.htm";
					}
					QuestState qs = player.getQuestState(getName());
					qs.takeItems(SYMBOL_OF_LOYALTY, -1);
					qs.takeItems(BLOOD_OF_EVA, -1);
					if (Rnd.get(10) > 5)
					{
						player.addItem(getName(), SYMBOL_OF_LOYALTY, 1, npc, true);
						return "30757-06.htm";
					}
					
					L2Skill skill = SkillData.getInstance().getInfo(SKILL_DEATH, 1);
					if (skill == null)
					{
						// player.doDie(npc);
						_log.warn("501_PoCA: Missing skill " + SKILL_DEATH);
					}
					else
					{
						// npc.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST,
						// new SkillUsageRequest(skill));
						npc.broadcastPacket(new MagicSkillUse(npc, player, skill.getId(), skill.getLevel(), skill.getHitTime(), skill.getReuseDelay()));
						npc.broadcastPacket(new MagicSkillLaunched(npc, skill.getId(), skill.getLevel()));
					}
					player.doDie(npc);
					startQuestTimer("loyalty_" + player.getObjectId(), LOYALTY_TIMER, npc, player);
					return "30757-05.htm";
				}
				else if ("30758-03.htm".equals(event))
				{
					if (_minigame == null)
					{
						_minigame = qc;
						qc.setPart(ATHREA);
						for (Location loc : CHEST_POS)
						{
							addSpawn(CHEST[Rnd.get(CHEST.length)], loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), false, CHEST_TIMER);
						}
						startQuestTimer("chest_" + qc.getClan().getId(), CHEST_TIMER, npc, player);
					}
					else
					{
						return "30758-04.htm";
					}
				}
				else if ("30758-07.htm".equals(event))
				{
					if (_minigame == null)
					{
						if (player.reduceAdena(getName(), 10000, npc, true))
						{
							qc.getChests().reset();
							return "30758-08.htm";
						}
					}
					else
					{
						return "30758-04.htm";
					}
				}
			}
		}
		return event;
	}
	
	@Override
	public String onDeath(L2Character killer, L2Character victim, QuestState qs)
	{
		if (victim instanceof L2PcInstance)
		{
			L2PcInstance leader = victim.getActingPlayer();
			if (leader.getEffectList().isAffectedBySkill(SKILL_POISON))
			{
				leader.getEffectList().stopSkillEffects(SKILL_POISON);
			}
			QuestClan qc = _questers.remove(leader.getClanId());
			qs.exitQuest(true);
			for (L2ClanMember cm : qc._loyal)
			{
				if (cm == null)
				{
					break;
				}
				L2PcInstance member = cm.getPlayerInstance();
				if (member != null)
				{
					QuestState st = member.getQuestState(getName());
					if (st == null)
					{
						st = newQuestState(member);
					}
					st.exitQuest(true);
				}
			}
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (_minigame != null)
		{
			ChestInfo ci = _minigame.getChests();
			synchronized (ci)
			{
				if (ci.getWins() < NEEDED_WINS)
				{
					if (((ci.getKills() >= (CHEST_POS.length - NEEDED_WINS)) && ((ci.getKills() - ci.getWins()) == 12)) || (Rnd.get(NEEDED_WINS) == 0))
					{
						ci.incWins();
						npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getId(), CHEST_KILLED));
					}
				}
				ci.incKills();
			}
		}
		QuestClan qc = _questers.get(killer.getClanId());
		if ((qc == null) || !qc.checkLeader() || (qc.getPart() != KALIS) || !qc.isLoyal(killer))
		{
			return null;
		}
		switch (npc.getId())
		{
			case OEL_MAHUM_WITCH_DOCTOR:
				npc.dropItem(killer, HERB_OF_OEL_MAHUM, 1);
				break;
			case HARIT_LIZARDMAN_SHAMAN:
				npc.dropItem(killer, HERB_OF_HARIT, 1);
				break;
			case VANOR_SILENOS_SHAMAN:
				npc.dropItem(killer, HERB_OF_VANOR, 1);
				break;
		}
		return null;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		QuestClan qc = _questers.get(talker.getClanId());
		QuestState qs = talker.getQuestState(getName());
		int state = qs.getState();
		
		if (state == State.COMPLETED)
		{
			return "<html><body>This quest has already been completed.</body></html>";
		}
		
		if (qc == null)
		{
			if (state == State.CREATED)
			{
				switch (npc.getId())
				{
				// anyone may speak
					case SIR_KRISTOF_RODEMAI:
					case STATUE_OF_OFFERING:
						break;
					default:
						if (!talker.isClanLeader())
						{
							return null;
						}
				}
			}
			else
			{
				if (!talker.isClanLeader())
				{
					qs.exitQuest(true);
					return null;
				}
				qc = new QuestClan(talker.getClan());
				qc.setPart(SIR_KRISTOF_RODEMAI);
				_questers.put(talker.getClanId(), qc);
				qs.set("cond", "1");
				return onTalk(npc, talker);
			}
		}
		else if (!talker.isClanLeader() && !qc.checkLeader())
		{
			return null;
		}
		switch (npc.getId())
		{
			case SIR_KRISTOF_RODEMAI:
				if (state == State.CREATED)
				{
					if (talker.isClanLeader())
					{
						switch (talker.getClan().getLevel())
						{
							case 0:
							case 1:
							case 2:
								return buildReply(npc, 1);
							case 3:
								if (qs.getQuestItemsCount(ALLIANCE_MANIFESTO) > 0)
								{
									return buildReply(npc, 3);
								}
								return buildReply(npc, 4);
							default:
								return buildReply(npc, 2);
						}
					}
					return buildReply(npc, 5);
				}
				else if (state == State.STARTED)
				{
					if (qs.getQuestItemsCount(VOUCHER_OF_FAITH) > 0)
					{
						qs.takeItems(VOUCHER_OF_FAITH, -1);
						qs.exitQuest(false);
						qs.giveItems(ALLIANCE_MANIFESTO, 1);
						qs.addExpAndSp(0, 120000);
						talker.broadcastPacket(new SocialAction(talker.getObjectId(), 3));
						qs.playSound(QuestSound.ITEMSOUND_QUEST_FINISH);
						return buildReply(npc, 9);
					}
					return buildReply(npc, 10);
				}
				break;
			case KALIS:
				if (qc == null)
				{
					return null;
				}
				else if (state == State.CREATED)
				{
					QuestState l = qc.getClan().getLeader().getPlayerInstance().getQuestState(getName());
					if (l.getState() == State.STARTED)
					{
						return buildReply(npc, 12);
					}
				}
				else if (state == State.STARTED)
				{
					int part = qc.getPart();
					long symbols = qs.getQuestItemsCount(SYMBOL_OF_LOYALTY);
					boolean poisoned = talker.getEffectList().isAffectedBySkill(SKILL_POISON);
					if (part == SIR_KRISTOF_RODEMAI)
					{
						return buildReply(npc, 1);
					}
					else if ((part == STATUE_OF_OFFERING) && (symbols < NEEDED_MEMBERS))
					{
						return buildReply(npc, 5);
					}
					else if ((symbols >= NEEDED_MEMBERS) && !poisoned)
					{
						return buildReply(npc, 6);
					}
					else if ((part == (KALIS * 2)) && (qs.getQuestItemsCount(HERB_OF_HARIT) > 0) && (qs.getQuestItemsCount(HERB_OF_OEL_MAHUM) > 0) && (qs.getQuestItemsCount(HERB_OF_VANOR) > 0) && poisoned)
					{
						qs.takeItems(ANTIDOTE_RECIPE_LIST, -1);
						qs.takeItems(BLOOD_OF_EVA, -1);
						qs.takeItems(HERB_OF_HARIT, -1);
						qs.takeItems(HERB_OF_OEL_MAHUM, -1);
						qs.takeItems(HERB_OF_VANOR, -1);
						qs.giveItems(VOUCHER_OF_FAITH, 1);
						qs.giveItems(POTION_OF_RECOVERY, 1);
						qs.set("cond", "4");
						qc.setPart(SIR_KRISTOF_RODEMAI * 2);
						return buildReply(npc, 8);
					}
					else if (((part % KALIS) == 0) || (part == ATHREA))
					{
						if (!poisoned)
						{
							qc.setPart(SIR_KRISTOF_RODEMAI);
							return buildReply(npc, 9);
						}
						return buildReply(npc, 10);
					}
					else if (part == (SIR_KRISTOF_RODEMAI * 2))
					{
						return buildReply(npc, 11);
					}
				}
				break;
			case STATUE_OF_OFFERING:
				if (qc == null)
				{
					return null;
				}
				else if (qc.getPart() != STATUE_OF_OFFERING)
				{
					return buildReply(npc, 8);
				}
				else if (!talker.isClanLeader())
				{
					if (talker.getLevel() > 39)
					{
						if (qc.isLoyal(talker))
						{
							return buildReply(npc, 3);
						}
						return buildReply(npc, 1);
					}
					return buildReply(npc, 4);
				}
				else
				{
					return buildReply(npc, 2);
				}
			case ATHREA:
				if (qc == null)
				{
					return null;
				}
				int part = qc.getPart();
				QuestState l = qc.getClan().getLeader().getPlayerInstance().getQuestState(getName());
				if ((part == KALIS) && (l.getQuestItemsCount(ANTIDOTE_RECIPE_LIST) > 0) && (l.getQuestItemsCount(BLOOD_OF_EVA) == 0))
				{
					return buildReply(npc, 1);
				}
				else if (part == (KALIS * 2))
				{
					return buildReply(npc, 10);
				}
				else if (part == ATHREA)
				{
					switch (qc.getChests().getState())
					{
						case ChestInfo.WON:
							qs.giveItems(BLOOD_OF_EVA, 1);
							qc.setPart(KALIS * 2);
							return buildReply(npc, 9);
						case ChestInfo.FAILED:
							return buildReply(npc, 6);
						default:
							return buildReply(npc, 10);
					}
				}
				break;
		}
		return null;
	}
	
	private static final String buildReply(L2Npc npc, int answer)
	{
		TextBuilder tb = TextBuilder.newInstance();
		tb.append(npc.getId());
		tb.append('-');
		if (answer < 10)
		{
			tb.append('0');
		}
		tb.append(answer);
		tb.append(".htm");
		String rep = tb.toString();
		TextBuilder.recycle(tb);
		return rep;
	}
	
	private static class QuestClan
	{
		private final L2Clan _clan;
		protected final L2ClanMember[] _loyal;
		private final ChestInfo _chests;
		private int _part;
		
		public QuestClan(L2Clan clan)
		{
			_clan = clan;
			_loyal = new L2ClanMember[NEEDED_MEMBERS];
			_chests = new ChestInfo();
		}
		
		public synchronized boolean addLoyalMember(L2PcInstance player)
		{
			L2ClanMember cm = getClan().getClanMember(player.getObjectId());
			if (cm == null)
			{
				return false;
			}
			for (int i = 0; i < NEEDED_MEMBERS; i++)
			{
				if (_loyal[i] == null)
				{
					_loyal[i] = cm;
					return true;
				}
			}
			return false;
		}
		
		public boolean checkLeader()
		{
			L2PcInstance leader = getClan().getLeader().getPlayerInstance();
			if (leader == null)
			{
				return false;
			}
			QuestState qs = leader.getQuestState(Q00501_ProofOfClanAlliance.class.getSimpleName());
			if ((qs == null) || !qs.isStarted())
			{
				return false;
			}
			return true;
		}
		
		public boolean isLoyal(L2PcInstance player)
		{
			for (L2ClanMember cm : _loyal)
			{
				if ((cm != null) && (cm.getPlayerInstance() == player))
				{
					return true;
				}
			}
			return false;
		}
		
		public L2Clan getClan()
		{
			return _clan;
		}
		
		public ChestInfo getChests()
		{
			return _chests;
		}
		
		public int getPart()
		{
			return _part;
		}
		
		public void setPart(int part)
		{
			_part = part;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof QuestClan)
			{
				return hashCode() == obj.hashCode();
			}
			return false;
		}
		
		@Override
		public int hashCode()
		{
			return getClan().getId();
		}
		
		@Override
		public String toString()
		{
			TextBuilder tb = TextBuilder.newInstance();
			tb.append("Questing clan ");
			tb.append(getClan().getName());
			tb.append(", leader ");
			tb.append(getClan().getLeaderName());
			tb.append(", tasked members:");
			for (L2ClanMember cm : _loyal)
			{
				if (cm == null)
				{
					break;
				}
				tb.append(' ');
				tb.append(cm.getName());
				tb.append('(');
				if (cm.isOnline())
				{
					tb.append("ON");
				}
				else
				{
					tb.append("OFF");
				}
				tb.append(')');
			}
			String qc = tb.toString();
			TextBuilder.recycle(tb);
			return qc;
		}
	}
	
	private static class ChestInfo
	{
		private static final int NONE = 0;
		private static final int WON = 1;
		private static final int FAILED = 2;
		private int _kills;
		private int _wins;
		private int _state;
		
		public ChestInfo()
		{
			reset();
		}
		
		public int getKills()
		{
			return _kills;
		}
		
		public void incKills()
		{
			_kills++;
		}
		
		public int getWins()
		{
			return _wins;
		}
		
		public void incWins()
		{
			_wins++;
			if (getWins() == NEEDED_WINS)
			{
				_state = WON;
			}
		}
		
		public void timeout()
		{
			if (getState() == NONE)
			{
				_state = FAILED;
			}
		}
		
		public int getState()
		{
			return _state;
		}
		
		public void reset()
		{
			_kills = 0;
			_wins = 0;
			_state = NONE;
		}
	}
}