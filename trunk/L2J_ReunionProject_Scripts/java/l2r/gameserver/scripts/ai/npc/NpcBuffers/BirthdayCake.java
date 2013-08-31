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
package l2r.gameserver.scripts.ai.npc.NpcBuffers;

import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.holders.SkillHolder;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.scripts.ai.npc.AbstractNpcAI;

/**
 * @author UnAfraid
 */
public class BirthdayCake extends AbstractNpcAI
{
	private static final int BIRTHDAY_CAKE_24 = 106;
	private static final int BIRTHDAY_CAKE = 139;
	
	protected BirthdayCake(String name, String descr)
	{
		super(name, descr);
		addFirstTalkId(BIRTHDAY_CAKE, BIRTHDAY_CAKE_24);
		addSpawnId(BIRTHDAY_CAKE, BIRTHDAY_CAKE_24);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		addTask(npc);
		return super.onSpawn(npc);
	}
	
	private void addTask(L2Npc npc)
	{
		final SkillHolder holder;
		switch (npc.getNpcId())
		{
			case BIRTHDAY_CAKE:
			{
				holder = new SkillHolder(22035, 1);
				break;
			}
			case BIRTHDAY_CAKE_24:
			{
				holder = new SkillHolder(22250, 1);
				break;
			}
			default:
			{
				return;
			}
		}
		
		ThreadPoolManager.getInstance().scheduleGeneral(new BirthdayCakeAI(npc, holder), 1000);
	}
	
	protected class BirthdayCakeAI implements Runnable
	{
		private final L2Npc _npc;
		private final SkillHolder _holder;
		
		protected BirthdayCakeAI(L2Npc npc, SkillHolder holder)
		{
			_npc = npc;
			_holder = holder;
		}
		
		@Override
		public void run()
		{
			if ((_npc == null) || !_npc.isVisible() || (_holder == null) || (_holder.getSkill() == null))
			{
				return;
			}
			
			if (!_npc.isInsideZone(ZoneIdType.PEACE))
			{
				L2Skill skill = _holder.getSkill();
				switch (_npc.getNpcId())
				{
					case BIRTHDAY_CAKE:
					{
						for (L2PcInstance player : _npc.getKnownList().getKnownPlayersInRadius(skill.getAffectRange()))
						{
							skill.getEffects(_npc, player);
						}
						break;
					}
					case BIRTHDAY_CAKE_24:
					{
						final L2PcInstance player = _npc.getSummoner().getActingPlayer();
						if (player == null)
						{
							ThreadPoolManager.getInstance().scheduleGeneral(this, 1000);
							return;
						}
						
						if (!player.isInParty())
						{
							if (player.isInsideRadius(_npc, skill.getAffectRange(), true, true))
							{
								skill.getEffects(_npc, player);
							}
						}
						else
						{
							for (L2PcInstance member : player.getParty().getMembers())
							{
								if ((member != null) && member.isInsideRadius(_npc, skill.getAffectRange(), true, true))
								{
									skill.getEffects(_npc, member);
								}
							}
						}
						break;
					}
				}
			}
			ThreadPoolManager.getInstance().scheduleGeneral(this, 1000);
		}
	}
	
	public static void main(String[] args)
	{
		new BirthdayCake(BirthdayCake.class.getSimpleName(), "ai/npc");
	}
}