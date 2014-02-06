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
package l2r.gameserver.scripts.handlers.bypasshandlers;

import l2r.gameserver.handler.IBypassHandler;
import l2r.gameserver.instancemanager.TerritoryWarManager;
import l2r.gameserver.model.L2Clan;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2FortSiegeNpcInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.SystemMessage;

public class FortSiege implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"fort_register",
		"fort_unregister"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!(target instanceof L2FortSiegeNpcInstance))
		{
			return false;
		}
		
		if ((activeChar.getClanId() > 0) && activeChar.hasClanPrivilege(L2Clan.CP_CS_MANAGE_SIEGE))
		{
			if (command.toLowerCase().startsWith(COMMANDS[0])) // register
			{
				if ((System.currentTimeMillis() < TerritoryWarManager.getInstance().getTWStartTimeInMillis()) && TerritoryWarManager.getInstance().getIsRegistrationOver())
				{
					activeChar.sendPacket(SystemMessageId.NOT_SIEGE_REGISTRATION_TIME2);
					return false;
				}
				else if ((System.currentTimeMillis() > TerritoryWarManager.getInstance().getTWStartTimeInMillis()) && TerritoryWarManager.getInstance().isTWChannelOpen())
				{
					activeChar.sendPacket(SystemMessageId.NOT_SIEGE_REGISTRATION_TIME2);
					return false;
				}
				else if (((L2Npc) target).getFort().getSiege().registerAttacker(activeChar, false))
				{
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.REGISTERED_TO_S1_FORTRESS_BATTLE);
					sm.addString(((L2Npc) target).getFort().getName());
					activeChar.sendPacket(sm);
					((L2Npc) target).showChatWindow(activeChar, 7);
					return true;
				}
			}
			else if (command.toLowerCase().startsWith(COMMANDS[1])) // unregister
			{
				((L2Npc) target).getFort().getSiege().removeSiegeClan(activeChar.getClan());
				((L2Npc) target).showChatWindow(activeChar, 8);
				return true;
			}
			return false;
		}
		
		((L2Npc) target).showChatWindow(activeChar, 10);
		
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}