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

import l2r.gameserver.scripts.instances.BloodShedParty;
import l2r.gameserver.scripts.instances.Bloodshed;
import l2r.gameserver.scripts.instances.ChamberOfDelusionEast;
import l2r.gameserver.scripts.instances.ChamberOfDelusionNorth;
import l2r.gameserver.scripts.instances.ChamberOfDelusionSouth;
import l2r.gameserver.scripts.instances.ChamberOfDelusionSquare;
import l2r.gameserver.scripts.instances.ChamberOfDelusionTower;
import l2r.gameserver.scripts.instances.ChamberOfDelusionWest;
import l2r.gameserver.scripts.instances.CrystalCaverns;
import l2r.gameserver.scripts.instances.DarkCloudMansion;
import l2r.gameserver.scripts.instances.DisciplesNecropolisPast;
import l2r.gameserver.scripts.instances.ElcadiaTent;
import l2r.gameserver.scripts.instances.FinalEmperialTomb;
import l2r.gameserver.scripts.instances.HideoutOfTheDawn;
import l2r.gameserver.scripts.instances.IceQueenCastleExtreme;
import l2r.gameserver.scripts.instances.IceQueenCastleNormal;
import l2r.gameserver.scripts.instances.IceQueensCastle;
import l2r.gameserver.scripts.instances.JiniaGuildHideout1;
import l2r.gameserver.scripts.instances.JiniaGuildHideout2;
import l2r.gameserver.scripts.instances.JiniaGuildHideout3;
import l2r.gameserver.scripts.instances.JiniaGuildHideout4;
import l2r.gameserver.scripts.instances.Kamaloka;
import l2r.gameserver.scripts.instances.LibraryOfSages;
import l2r.gameserver.scripts.instances.MithrilMine;
import l2r.gameserver.scripts.instances.NornilsGarden;
import l2r.gameserver.scripts.instances.PailakaDevilsLegacy;
import l2r.gameserver.scripts.instances.PailakaInjuredDragon;
import l2r.gameserver.scripts.instances.PailakaSongOfIceAndFire;
import l2r.gameserver.scripts.instances.RimKamaloka;
import l2r.gameserver.scripts.instances.SanctumOftheLordsOfDawn;
import l2r.gameserver.scripts.instances.SecretAreaKeucereus;
import l2r.gameserver.scripts.instances.ToTheMonastery;
import l2r.gameserver.scripts.instances.Zaken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nos
 */
public class InstancesLoader
{
	private static final Logger _log = LoggerFactory.getLogger(InstancesLoader.class);
	
	public InstancesLoader()
	{
		_log.info(getClass().getSimpleName() + ": Loading Instances related scripts");
		
		for (Class<?> instances : INSTANCES)
		{
			try
			{
				instances.newInstance();
			}
			catch (Exception e)
			{
				_log.error(InstancesLoader.class.getSimpleName() + ": Failed loading " + instances.getSimpleName() + ":", e);
			}
		}
	}
	
	private static final Class<?>[] INSTANCES =
	{
		Bloodshed.class,
		BloodShedParty.class,
		ChamberOfDelusionEast.class,
		ChamberOfDelusionNorth.class,
		ChamberOfDelusionSouth.class,
		ChamberOfDelusionSquare.class,
		ChamberOfDelusionTower.class,
		ChamberOfDelusionWest.class,
		CrystalCaverns.class,
		DarkCloudMansion.class,
		DisciplesNecropolisPast.class,
		ElcadiaTent.class,
		FinalEmperialTomb.class,
		HideoutOfTheDawn.class,
		IceQueenCastleExtreme.class,
		IceQueenCastleNormal.class,
		IceQueensCastle.class,
		JiniaGuildHideout1.class,
		JiniaGuildHideout2.class,
		JiniaGuildHideout3.class,
		JiniaGuildHideout4.class,
		Kamaloka.class,
		LibraryOfSages.class,
		MithrilMine.class,
		NornilsGarden.class,
		PailakaDevilsLegacy.class,
		PailakaInjuredDragon.class,
		PailakaSongOfIceAndFire.class,
		RimKamaloka.class,
		SanctumOftheLordsOfDawn.class,
		SecretAreaKeucereus.class,
		ToTheMonastery.class,
		Zaken.class,
	};
	
	public static InstancesLoader getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final InstancesLoader INSTANCE = new InstancesLoader();
	}
}
