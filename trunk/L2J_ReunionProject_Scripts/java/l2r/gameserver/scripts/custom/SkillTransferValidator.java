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
package l2r.gameserver.scripts.custom;

import l2r.Config;
import l2r.gameserver.datatables.ClassListData;
import l2r.gameserver.datatables.SkillTreesData;
import l2r.gameserver.enums.IllegalActionPunishmentType;
import l2r.gameserver.enums.PcCondOverride;
import l2r.gameserver.model.L2SkillLearn;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.holders.ItemHolder;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.scripting.scriptengine.events.ProfessionChangeEvent;
import l2r.gameserver.scripting.scriptengine.impl.L2Script;
import l2r.gameserver.util.Util;

/**
 * Skill Transfer validator.
 * @author Zoey76
 */
public final class SkillTransferValidator extends L2Script
{
	private static final ItemHolder[] PORMANDERS =
	{
		// Cardinal (97)
		new ItemHolder(15307, 1),
		// Eva's Saint (105)
		new ItemHolder(15308, 1),
		// Shillen Saint (112)
		new ItemHolder(15309, 4)
	};
	
	public SkillTransferValidator()
	{
		super(-1, "SkillTransfer", "custom");
		setOnEnterWorld(true);
	}
	
	@Override
	public String onEnterWorld(L2PcInstance player)
	{
		addProfessionChangeNotify(player);
		if (getTransferClassIndex(player) >= 0)
		{
			startQuestTimer("givePormanders", 2000, null, player);
		}
		return null;
	}
	
	@Override
	public void onProfessionChange(ProfessionChangeEvent event)
	{
		startQuestTimer("givePormanders", 2000, null, event.getPlayer());
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("givePormanders"))
		{
			final int index = getTransferClassIndex(player);
			if (index >= 0)
			{
				QuestState st = player.getQuestState(getName());
				if (st == null)
				{
					st = newQuestState(player);
				}
				
				final String name = getName() + String.valueOf(player.getClassId().getId());
				if (st.getInt(name) == 0)
				{
					st.setInternal(name, "1");
					if (st.getGlobalQuestVar(name).isEmpty())
					{
						st.saveGlobalQuestVar(name, "1");
						player.addItem(getName(), PORMANDERS[index].getId(), PORMANDERS[index].getCount(), null, true);
					}
				}
				
				if (Config.SKILL_CHECK_ENABLE && (!player.canOverrideCond(PcCondOverride.SKILL_CONDITIONS) || Config.SKILL_CHECK_GM))
				{
					long count = PORMANDERS[index].getCount() - player.getInventory().getInventoryItemCount(PORMANDERS[index].getId(), -1, false);
					for (L2Skill sk : player.getAllSkills())
					{
						for (L2SkillLearn s : SkillTreesData.getInstance().getTransferSkillTree(player.getClassId()).values())
						{
							if (s.getSkillId() == sk.getId())
							{
								// Holy Weapon allowed for Shilien Saint/Inquisitor stance
								if ((sk.getId() == 1043) && (index == 2) && player.isInStance())
								{
									continue;
								}
								
								count--;
								if (count < 0)
								{
									final String className = ClassListData.getInstance().getClass(player.getClassId()).getClassName();
									Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " has too many transfered skills or items, skill:" + s.getName() + " (" + sk.getId() + "/" + sk.getLevel() + "), class:" + className, IllegalActionPunishmentType.BROADCAST);
									if (Config.SKILL_CHECK_REMOVE)
									{
										player.removeSkill(sk);
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	private int getTransferClassIndex(L2PcInstance player)
	{
		switch (player.getClassId().getId())
		{
			case 97: // Cardinal
				return 0;
			case 105: // Eva's Saint
				return 1;
			case 112: // Shillien Saint
				return 2;
			default:
				return -1;
		}
	}
	
	public static void main(String[] args)
	{
		new SkillTransferValidator();
	}
}
