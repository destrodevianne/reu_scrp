package l2r.gameserver.scripts.ai.modifier;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.scripts.ai.npc.AbstractNpcAI;

public class NoTalkingNpcs extends AbstractNpcAI
{
	// @formatter:off
	private final static int[] NO_TALKING_LIST =
	{
		18684, 18685, 18686, 18687, 18688, 18689, 18690, 19691, 18692, 31557, 31606,
		31671, 31672, 31673, 31674, 32026, 32030, 32031, 32032, 32619, 32620, 32621
	};
	// @formatter:on
	
	public NoTalkingNpcs(String name, String descr)
	{
		super(name, descr);
		for (int _npcIds : NO_TALKING_LIST)
		{
			addStartNpc(_npcIds);
			addFirstTalkId(_npcIds);
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (contains(NO_TALKING_LIST, npc.getId()))
		{
			return "skipNextAction";
		}
		
		npc.showChatWindow(player);
		return null;
	}
	
	public static void main(String[] args)
	{
		new NoTalkingNpcs("NoTalkingNpcs", "ai");
	}
}
