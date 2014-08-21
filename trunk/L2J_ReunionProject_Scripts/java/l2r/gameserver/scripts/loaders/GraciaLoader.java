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
package l2r.gameserver.scripts.loaders;

import l2r.gameserver.scripts.gracia.AI.EnergySeeds;
import l2r.gameserver.scripts.gracia.AI.Lindvior;
import l2r.gameserver.scripts.gracia.AI.Maguen;
import l2r.gameserver.scripts.gracia.AI.StarStones;
import l2r.gameserver.scripts.gracia.AI.NPC.FortuneTelling;
import l2r.gameserver.scripts.gracia.AI.NPC.GeneralDilios;
import l2r.gameserver.scripts.gracia.AI.NPC.Lekon;
import l2r.gameserver.scripts.gracia.AI.NPC.Nemo;
import l2r.gameserver.scripts.gracia.AI.NPC.Nottingale;
import l2r.gameserver.scripts.gracia.AI.NPC.Seyo;
import l2r.gameserver.scripts.gracia.AI.NPC.ZealotOfShilen;
import l2r.gameserver.scripts.gracia.AI.SeedOfAnnihilation.SeedOfAnnihilation;
import l2r.gameserver.scripts.gracia.instances.HallOfErosionAttack;
import l2r.gameserver.scripts.gracia.instances.HallOfErosionDefence;
import l2r.gameserver.scripts.gracia.instances.HallOfSufferingAttack;
import l2r.gameserver.scripts.gracia.instances.HallOfSufferingDefence;
import l2r.gameserver.scripts.gracia.instances.HeartInfinityAttack;
import l2r.gameserver.scripts.gracia.instances.HeartInfinityDefence;
import l2r.gameserver.scripts.gracia.instances.SecretArea;
import l2r.gameserver.scripts.gracia.instances.Stage1;
import l2r.gameserver.scripts.gracia.vehicles.AirShipGludioGracia;
import l2r.gameserver.scripts.gracia.vehicles.KeucereusNorthController;
import l2r.gameserver.scripts.gracia.vehicles.KeucereusSouthController;
import l2r.gameserver.scripts.gracia.vehicles.SoDController;
import l2r.gameserver.scripts.gracia.vehicles.SoIController;

import org.slf4j.LoggerFactory;

/**
 * Gracia class-loader.
 * @author Pandragon
 */
public final class GraciaLoader
{
	private static final org.slf4j.Logger _log = LoggerFactory.getLogger(GraciaLoader.class.getName());
	
	public GraciaLoader()
	{
		_log.info(GraciaLoader.class.getSimpleName() + ": Loading Gracia related scripts.");
		for (Class<?> script : SCRIPTS)
		{
			try
			{
				script.newInstance();
			}
			catch (Exception e)
			{
				_log.error(GraciaLoader.class.getSimpleName() + ": Failed loading " + script.getSimpleName() + ":", e);
			}
		}
	}
	
	private static final Class<?>[] SCRIPTS =
	{
		// AIs
		EnergySeeds.class,
		Lindvior.class,
		Maguen.class,
		StarStones.class,
		// NPCs
		FortuneTelling.class,
		GeneralDilios.class,
		Lekon.class,
		Nemo.class,
		Nottingale.class,
		Seyo.class,
		ZealotOfShilen.class,
		// Seed of Annihilation
		SeedOfAnnihilation.class,
		// Instances
		HallOfErosionAttack.class,
		HallOfErosionDefence.class,
		HallOfSufferingAttack.class,
		HallOfSufferingDefence.class,
		HeartInfinityAttack.class,
		HeartInfinityDefence.class,
		SecretArea.class,
		Stage1.class, // Seed of Destruction
		// Vehicles
		AirShipGludioGracia.class,
		KeucereusNorthController.class,
		KeucereusSouthController.class,
		SoIController.class,
		SoDController.class,
	};
	
	public static GraciaLoader getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final GraciaLoader INSTANCE = new GraciaLoader();
	}
}
