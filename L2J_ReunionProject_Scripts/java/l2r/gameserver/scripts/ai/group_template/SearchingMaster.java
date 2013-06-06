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
package l2r.gameserver.scripts.ai.group_template;

import l2r.gameserver.ai.CtrlIntention;
import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.scripts.ai.npc.AbstractNpcAI;

/**
 * Searching Master AI.
 * @author Charus
 */
public class SearchingMaster extends AbstractNpcAI
{
	private SearchingMaster()
	{
		super(SearchingMaster.class.getSimpleName(), "ai/group_template");
		addAttackId(20965, 20966, 20967, 20968, 20969, 20970, 20971, 20972, 20973);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isSummon)
	{
		if (player != null)
		{
			npc.setIsRunning(true);
			((L2Attackable) npc).addDamageHate(player, 0, 999);
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
		}
		return super.onAttack(npc, player, damage, isSummon);
	}
	
	public static void main(String[] args)
	{
		new SearchingMaster();
	}
}