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

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;

/**
 * Budenka AI.
 */
public final class Budenka extends Quest
{
	private static final int BUDENKA = 32294;
	private static final int STANDART_CERT = 9851;
	private static final int PREMIUM_CERT = 9852;
	
	public Budenka()
	{
		super(-1, Budenka.class.getSimpleName(), "hellbound/AI/NPC");
		addFirstTalkId(BUDENKA);
	}
	
	@Override
	public final String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (player.getInventory().getInventoryItemCount(PREMIUM_CERT, -1, false) > 0)
		{
			return "32294-premium.htm";
		}
		
		if (player.getInventory().getInventoryItemCount(STANDART_CERT, -1, false) > 0)
		{
			return "32294-standart.htm";
		}
		
		npc.showChatWindow(player);
		return null;
	}
}