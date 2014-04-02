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
package l2r.gameserver.scripts.hellbound.AI.NPC;

import l2r.Config;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.enums.PcCondOverride;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.zone.L2ZoneType;
import l2r.gameserver.scripts.ai.npc.AbstractNpcAI;
import l2r.gameserver.scripts.hellbound.HellboundEngine;
import l2r.gameserver.scripts.quests.Q00130_PathToHellbound;
import l2r.gameserver.scripts.quests.Q00133_ThatsBloodyHot;

/**
 * Warpgate teleport AI.
 * @author _DS_
 */
public final class Warpgate extends AbstractNpcAI
{
	// Misc
	private static final int MAP = 9994;
	private static final int ZONE = 40101;
	// Teleports
	private static final int[] WARPGATES =
	{
		32314,
		32315,
		32316,
		32317,
		32318,
		32319
	};
	// Locations
	private static final Location HELLBOUND = new Location(-11272, 236464, -3248);
	protected static final Location REMOVE_LOC = new Location(-16555, 209375, -3670);
	
	public Warpgate()
	{
		super(Warpgate.class.getSimpleName(), "hellbound/AI/NPC");
		addStartNpc(WARPGATES);
		addFirstTalkId(WARPGATES);
		addTalkId(WARPGATES);
		addEnterZoneId(ZONE);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (!canEnter(player))
		{
			if (HellboundEngine.getInstance().isLocked())
			{
				return "warpgate-locked.htm";
			}
		}
		return npc.getId() + ".htm";
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		if (!canEnter(player))
		{
			return "warpgate-no.htm";
		}
		
		player.teleToLocation(HELLBOUND, true);
		HellboundEngine.getInstance().setLevel(1);
		return null;
	}
	
	@Override
	public final String onEnterZone(L2Character character, L2ZoneType zone)
	{
		if (character.isPlayer())
		{
			if (!canEnter(character.getActingPlayer()) && !character.canOverrideCond(PcCondOverride.ZONE_CONDITIONS))
			{
				ThreadPoolManager.getInstance().scheduleGeneral(new Teleport(character), 1000);
			}
			else if (!character.getActingPlayer().isMinimapAllowed())
			{
				if (character.getInventory().getItemByItemId(MAP) != null)
				{
					character.getActingPlayer().setMinimapAllowed(true);
				}
			}
		}
		return null;
	}
	
	private static final class Teleport implements Runnable
	{
		private final L2Character _char;
		
		public Teleport(L2Character c)
		{
			_char = c;
		}
		
		@Override
		public void run()
		{
			try
			{
				_char.teleToLocation(REMOVE_LOC, true);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private static boolean canEnter(L2PcInstance player)
	{
		if (player.isFlying())
		{
			return false;
		}
		
		if (Config.HELLBOUND_WITHOUT_QUEST)
		{
			return true;
		}
		
		QuestState st;
		if (!HellboundEngine.getInstance().isLocked())
		{
			st = player.getQuestState(Q00130_PathToHellbound.class.getSimpleName());
			if ((st != null) && st.isCompleted())
			{
				return true;
			}
		}
		st = player.getQuestState(Q00133_ThatsBloodyHot.class.getSimpleName());
		return ((st != null) && st.isCompleted());
	}
}
