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

import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2ChestInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.scripts.ai.npc.AbstractNpcAI;
import l2r.gameserver.util.Util;

/**
 * Chest AI implementation.
 * @author Fulminus
 */
public class Chests extends AbstractNpcAI
{
	// NPCs
	// @formatter:off
	private static final int[] TREASURE_CHESTS =
	{
		18265, 18266, 18267, 18268, 18269, 18270, 18271, 18272, 18273, 18274,
		18275, 18276, 18277, 18278, 18279, 18280, 18281, 18282, 18283, 18284,
		18285, 18286, 18287, 18288, 18289, 18290, 18291, 18292, 18293, 18294,
		18295, 18296, 18297, 18298, 21671, 21694, 21717, 21740, 21763, 21786,
		21801, 21802, 21803, 21804, 21805, 21806, 21807, 21808, 21809, 21810,
		21811, 21812, 21813, 21814, 21815, 21816, 21817, 21818, 21819, 21820,
		21821, 21822
	};
	// @formatter:on
	private static final int SKILL_DELUXE_KEY = 2229;
	// Base chance for BOX to be opened
	private static final int BASE_CHANCE = 100;
	// Percent to decrease base chance when grade of DELUXE key not match
	private static final int LEVEL_DECREASE = 40;
	// Chance for a chest to actually be a BOX (as opposed to being a mimic).
	private static final int IS_BOX = 40;
	
	private Chests()
	{
		super(Chests.class.getSimpleName(), "ai/group_template");
		registerMobs(TREASURE_CHESTS, QuestEventType.ON_ATTACK, QuestEventType.ON_SKILL_SEE);
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isSummon)
	{
		if (npc instanceof L2ChestInstance)
		{
			// this behavior is only run when the target of skill is the passed npc (chest)
			// i.e. when the player is attempting to open the chest using a skill
			if (!Util.contains(targets, npc))
			{
				return super.onSkillSee(npc, caster, skill, targets, isSummon);
			}
			L2ChestInstance chest = ((L2ChestInstance) npc);
			
			// if this has already been interacted, no further ai decisions are needed
			// if it's the first interaction, check if this is a box or mimic
			if (!chest.isInteracted())
			{
				chest.setInteracted();
				if (getRandom(100) < IS_BOX)
				{
					// if it's a box, either it will be successfully opened by a proper key, or instantly disappear
					if (skill.getId() == SKILL_DELUXE_KEY)
					{
						// check the chance to open the box
						int keyLevelNeeded = chest.getLevel() / 10;
						keyLevelNeeded -= skill.getLevel();
						if (keyLevelNeeded < 0)
						{
							keyLevelNeeded *= -1;
						}
						int chance = BASE_CHANCE - (keyLevelNeeded * LEVEL_DECREASE);
						
						// success, pretend-death with rewards: chest.reduceCurrentHp(99999999, player)
						if (getRandom(100) < chance)
						{
							chest.setMustRewardExpSp(false);
							chest.setSpecialDrop();
							chest.reduceCurrentHp(99999999, caster, null);
							return null;
						}
					}
					// used a skill other than chest-key, or used a chest-key but failed to open: disappear with no rewards
					chest.deleteMe();
				}
				else
				{
					L2Character originalCaster = isSummon ? caster.getSummon() : caster;
					chest.setRunning();
					chest.addDamageHate(originalCaster, 0, 999);
					chest.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, originalCaster);
				}
			}
		}
		return super.onSkillSee(npc, caster, skill, targets, isSummon);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (npc instanceof L2ChestInstance)
		{
			L2ChestInstance chest = ((L2ChestInstance) npc);
			// if this was a mimic, set the target, start the skills and become agro
			if (!chest.isInteracted())
			{
				chest.setInteracted();
				if (getRandom(100) < IS_BOX)
				{
					chest.deleteMe();
				}
				else
				{
					// if this weren't a box, upon interaction start the mimic behaviors...
					// TODO: perhaps a self-buff (skill id 4245) with random chance goes here?
					L2Character originalAttacker = isSummon ? attacker.getSummon() : attacker;
					chest.setRunning();
					chest.addDamageHate(originalAttacker, 0, (damage * 100) / (chest.getLevel() + 7));
					chest.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, originalAttacker);
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	public static void main(String[] args)
	{
		new Chests();
	}
}
