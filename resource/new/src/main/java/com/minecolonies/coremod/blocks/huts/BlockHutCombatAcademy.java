package com.minecolonies.coremod.blocks.huts;



import org.jetbrains.annotations.NotNull;

/**
 * Block of the combat academy camp.
 */
public class BlockHutCombatAcademy extends AbstractBlockHut<BlockHutCombatAcademy>
{
    @NotNull
    @Override
    public String getName()
    {
        return "blockhutcombatacademy";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.combatAcademy;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void checkResearch(final IColonyView colony)
    {
        checkResearch(colony, ResearchInitializer.COMBAT_ACADEMY_RESEARCH);
    }
}
