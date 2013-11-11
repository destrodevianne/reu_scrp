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
package l2r.gameserver.scripts.handlers.admincommandhandlers;

import java.io.File;
import java.util.StringTokenizer;

import javax.script.ScriptException;

import l2r.Config;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.datatables.AdminTable;
import l2r.gameserver.datatables.BuyListData;
import l2r.gameserver.datatables.CrestTable;
import l2r.gameserver.datatables.DoorTable;
import l2r.gameserver.datatables.ItemTable;
import l2r.gameserver.datatables.MultiSell;
import l2r.gameserver.datatables.NpcTable;
import l2r.gameserver.datatables.SkillTable;
import l2r.gameserver.datatables.SpawnTable;
import l2r.gameserver.datatables.TeleportLocationTable;
import l2r.gameserver.handler.IAdminCommandHandler;
import l2r.gameserver.instancemanager.CursedWeaponsManager;
import l2r.gameserver.instancemanager.QuestManager;
import l2r.gameserver.instancemanager.WalkingManager;
import l2r.gameserver.instancemanager.ZoneManager;
import l2r.gameserver.model.L2Spawn;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.scripting.L2ScriptEngineManager;
import l2r.gameserver.util.Util;
import gr.reunion.configsEngine.CustomConfigController;

/**
 * @author Nos
 */
public class AdminReload implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_reload"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		if (actualCommand.equalsIgnoreCase("admin_reload"))
		{
			if (!st.hasMoreTokens())
			{
				AdminHtml.showAdminHtml(activeChar, "reload.htm");
				activeChar.sendMessage("Usage: //reload <config|access|npc [npc_id]|quest [quest_id|quest_name]|walker|htm[l] [file|directory]|multisell|buylist|teleport|skill|item|door|effect|handler>");
				return true;
			}
			
			final String type = st.nextToken();
			switch (type.toLowerCase())
			{
				case "config":
				{
					Config.load();
					CustomConfigController.getInstance().reloadCustomConfigs();
					AdminTable.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Configs.");
					break;
				}
				case "access":
				{
					AdminTable.getInstance().load();
					AdminTable.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Access.");
					break;
				}
				case "npc":
				{
					if (st.hasMoreElements())
					{
						Integer npcId = Integer.parseInt(st.nextToken());
						if (npcId != null)
						{
							NpcTable.getInstance().reloadNpc(npcId);
							for (L2Spawn spawn : SpawnTable.getInstance().getSpawns(npcId))
							{
								if (spawn != null)
								{
									spawn.respawnNpc(spawn.getLastSpawn());
								}
							}
							activeChar.sendMessage("NPC " + npcId + " have been reloaded");
						}
					}
					else
					{
						NpcTable.getInstance().reloadAllNpc();
						QuestManager.getInstance().reloadAllQuests();
						activeChar.sendMessage("All NPCs have been reloaded");
					}
					break;
				}
				case "quest":
				{
					if (st.hasMoreElements())
					{
						String value = st.nextToken();
						if (!Util.isDigit(value))
						{
							QuestManager.getInstance().reload(value);
							AdminTable.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Quest Name:" + value + ".");
						}
						else
						{
							final int questId = Integer.parseInt(value);
							QuestManager.getInstance().reload(questId);
							AdminTable.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Quest ID:" + questId + ".");
						}
					}
					else
					{
						QuestManager.getInstance().reloadAllQuests();
						activeChar.sendMessage("All Quests have been reloaded.");
						AdminTable.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Quests.");
					}
					break;
				}
				case "walker":
				{
					WalkingManager.getInstance().load();
					activeChar.sendMessage("All Walkers have been reloaded");
					AdminTable.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Walkers.");
					break;
				}
				case "htm":
				case "html":
				{
					if (st.hasMoreElements())
					{
						final String path = st.nextToken();
						final File file = new File(Config.DATAPACK_ROOT, "data/html/" + path);
						if (file.exists())
						{
							HtmCache.getInstance().reload(file);
							AdminTable.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Htm File:" + file.getName() + ".");
						}
						else
						{
							activeChar.sendMessage("File or Directory does not exist.");
						}
					}
					else
					{
						HtmCache.getInstance().reload();
						activeChar.sendMessage("Cache[HTML]: " + HtmCache.getInstance().getMemoryUsage() + " megabytes on " + HtmCache.getInstance().getLoadedFiles() + " files loaded");
						AdminTable.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Htms.");
					}
					break;
				}
				case "multisell":
				{
					MultiSell.getInstance().reload();
					AdminTable.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Multisells.");
					break;
				}
				case "buylist":
				{
					BuyListData.getInstance().load();
					AdminTable.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Buylists.");
					break;
				}
				case "teleport":
				{
					TeleportLocationTable.getInstance().reloadAll();
					AdminTable.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Teleports.");
					break;
				}
				case "skill":
				{
					SkillTable.getInstance().reload();
					AdminTable.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Skills.");
					break;
				}
				case "item":
				{
					ItemTable.getInstance().reload();
					AdminTable.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Items.");
					break;
				}
				case "door":
				{
					DoorTable.getInstance().load();
					AdminTable.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Doors.");
					break;
				}
				case "zone":
				{
					ZoneManager.getInstance().reload();
					AdminTable.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Zones.");
					break;
				}
				case "cw":
				{
					CursedWeaponsManager.getInstance().reload();
					AdminTable.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Cursed Weapons.");
					break;
				}
				case "crest":
				{
					CrestTable.getInstance().load();
					AdminTable.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Crests.");
					break;
				}
				case "effect":
				{
					final File file = new File(L2ScriptEngineManager.SCRIPT_FOLDER, "handlers/EffectMasterHandler.java");
					try
					{
						L2ScriptEngineManager.getInstance().executeScript(file);
						AdminTable.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Effects.");
					}
					catch (ScriptException e)
					{
						L2ScriptEngineManager.getInstance().reportScriptFileError(file, e);
						activeChar.sendMessage("There was an error while loading handlers.");
					}
					break;
				}
				case "handler":
				{
					final File file = new File(L2ScriptEngineManager.SCRIPT_FOLDER, "handlers/MasterHandler.java");
					try
					{
						L2ScriptEngineManager.getInstance().executeScript(file);
						AdminTable.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Handlers.");
					}
					catch (ScriptException e)
					{
						L2ScriptEngineManager.getInstance().reportScriptFileError(file, e);
						activeChar.sendMessage("There was an error while loading handlers.");
					}
					break;
				}
				default:
				{
					activeChar.sendMessage("Usage: //reload <config|access|npc [npc_id]|quest [quest_id|quest_name]|walker|htm[l] [file|directory]|multisell|buylist|teleport|skill|item|door|effect|handler>");
					return true;
				}
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
}
