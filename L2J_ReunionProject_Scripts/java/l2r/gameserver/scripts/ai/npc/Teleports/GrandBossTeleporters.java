/*
 * Copyright (C) 2004-2013 L2J DataPack
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
package l2r.gameserver.scripts.ai.npc.Teleports;

import l2r.Config;
import l2r.gameserver.datatables.DoorTable;
import l2r.gameserver.instancemanager.GrandBossManager;
import l2r.gameserver.instancemanager.QuestManager;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2GrandBossInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.zone.type.L2BossZone;
import l2r.gameserver.scripts.ai.grandboss.Antharas;
import l2r.gameserver.scripts.ai.grandboss.Valakas;
import l2r.gameserver.scripts.ai.npc.AbstractNpcAI;

/**
 * Grand Bosses teleport AI.<br>
 * Original python script by Emperorc.
 * @author Plim
 */
public class GrandBossTeleporters extends AbstractNpcAI
{
	// NPCs
	private static final int[] NPCs =
	{
		13001, // Heart of Warding : Teleport into Lair of Antharas
		31859, // Teleportation Cubic : Teleport out of Lair of Antharas
		31384, // Gatekeeper of Fire Dragon : Opening some doors
		31385, // Heart of Volcano : Teleport into Lair of Valakas
		31540, // Watcher of Valakas Klein : Teleport into Hall of Flames
		31686, // Gatekeeper of Fire Dragon : Opens doors to Heart of Volcano
		31687, // Gatekeeper of Fire Dragon : Opens doors to Heart of Volcano
		31759, // Teleportation Cubic : Teleport out of Lair of Valakas
	};
	// Items
	private static final int PORTAL_STONE = 3865;
	private static final int VACUALITE_FLOATING_STONE = 7267;
	
	private Quest valakasAI()
	{
		return QuestManager.getInstance().getQuest(Valakas.class.getSimpleName());
	}
	
	private Quest antharasAI()
	{
		return QuestManager.getInstance().getQuest(Antharas.class.getSimpleName());
	}
	
	private static int playerCount = 0;
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		QuestState st = player.getQuestState(getName());
		
		if (st == null)
		{
			st = newQuestState(player);
		}
		
		if (st.hasQuestItems(VACUALITE_FLOATING_STONE))
		{
			player.teleToLocation(183813, -115157, -3303);
			st.set("allowEnter", "1");
		}
		else
		{
			htmltext = "31540-06.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		QuestState st = player.getQuestState(getName());
		
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case 13001:
			{
				if (antharasAI() != null)
				{
					int status = GrandBossManager.getInstance().getBossStatus(29019);
					int statusW = GrandBossManager.getInstance().getBossStatus(29066);
					int statusN = GrandBossManager.getInstance().getBossStatus(29067);
					int statusS = GrandBossManager.getInstance().getBossStatus(29068);
					
					if ((status == 2) || (statusW == 2) || (statusN == 2) || (statusS == 2))
					{
						htmltext = "13001-02.htm";
					}
					else if ((status == 3) || (statusW == 3) || (statusN == 3) || (statusS == 3))
					{
						htmltext = "13001-01.htm";
					}
					else if ((status == 0) || (status == 1)) // If entrance to see Antharas is unlocked (he is Dormant or Waiting)
					{
						if (st.hasQuestItems(PORTAL_STONE))
						{
							L2BossZone zone = GrandBossManager.getInstance().getZone(179700, 113800, -7709);
							
							if (zone != null)
							{
								zone.allowPlayerEntry(player, 30);
							}
							
							player.teleToLocation(179700 + getRandom(700), 113800 + getRandom(2100), -7709);
							
							if (status == 0)
							{
								L2GrandBossInstance antharas = GrandBossManager.getInstance().getBoss(29019);
								try
								{
									antharasAI().notifyEvent("waiting", antharas, player);
								}
								catch (Exception e)
								{
									_log.warn("Logger: notifyEvent failed (GrandBossTeleporter) Report this to team. ");
								}
							}
						}
						else
						{
							htmltext = "13001-03.htm";
						}
					}
				}
				break;
			}
			case 31859:
			{
				player.teleToLocation(79800 + getRandom(600), 151200 + getRandom(1100), -3534);
				break;
			}
			case 31385:
			{
				if (valakasAI() != null)
				{
					int status = GrandBossManager.getInstance().getBossStatus(29028);
					
					if ((status == 0) || (status == 1))
					{
						if (playerCount >= 200)
						{
							htmltext = "31385-03.htm";
						}
						else if (st.getInt("allowEnter") == 1)
						{
							st.unset("allowEnter");
							L2BossZone zone = GrandBossManager.getInstance().getZone(212852, -114842, -1632);
							
							if (zone != null)
							{
								zone.allowPlayerEntry(player, 30);
							}
							
							player.teleToLocation(204328 + getRandom(600), -111874 + getRandom(600), 70);
							
							playerCount++;
							
							if (status == 0)
							{
								L2GrandBossInstance valakas = GrandBossManager.getInstance().getBoss(29028);
								valakasAI().startQuestTimer("beginning", Config.VALAKAS_WAIT_TIME * 60000, valakas, null);
								GrandBossManager.getInstance().setBossStatus(29028, 1);
							}
						}
						else
						{
							htmltext = "31385-04.htm";
						}
					}
					else if (status == 2)
					{
						htmltext = "31385-02.htm";
					}
					else
					{
						htmltext = "31385-01.htm";
					}
				}
				else
				{
					htmltext = "31385-01.htm";
				}
				break;
			}
			case 31384:
			{
				DoorTable.getInstance().getDoor(24210004).openMe();
				break;
			}
			case 31686:
			{
				DoorTable.getInstance().getDoor(24210006).openMe();
				break;
			}
			case 31687:
			{
				DoorTable.getInstance().getDoor(24210005).openMe();
				break;
			}
			case 31540:
			{
				if (playerCount < 50)
				{
					htmltext = "31540-01.htm";
				}
				else if (playerCount < 100)
				{
					htmltext = "31540-02.htm";
				}
				else if (playerCount < 150)
				{
					htmltext = "31540-03.htm";
				}
				else if (playerCount < 200)
				{
					htmltext = "31540-04.htm";
				}
				else
				{
					htmltext = "31540-05.htm";
				}
				break;
			}
			case 31759:
			{
				player.teleToLocation(150037 + getRandom(500), -57720 + getRandom(500), -2976);
				break;
			}
		}
		return htmltext;
	}
	
	private GrandBossTeleporters(String name, String descr)
	{
		super(name, descr);
		addStartNpc(NPCs);
		addTalkId(NPCs);
	}
	
	public static void main(String[] args)
	{
		new GrandBossTeleporters(GrandBossTeleporters.class.getSimpleName(), "ai/npc/Teleports");
	}
}
