package com.minecolonies.coremod.colony.colony;

import org.jetbrains.annotations.NotNull;

/**
 * View data for visitors
 */
public class VisitorDataView extends CitizenDataView
{
    /**
     * The related colony view
     */
    private final ColonyView colony;

    /**
     * The recruitment costs
     */
    private       ItemStack                         recruitmentCosts;

    /**
     * Create a CitizenData given an ID. Used as a super-constructor or during loading.
     *
     * @param id     ID of the Citizen.
     * @param colony Colony the Citizen belongs to.
     */
    public VisitorDataView(final int id, final ColonyView colony)
    {
        super(id);
        this.colony = colony;
    }


    public void deserialize(@NotNull final PacketByteBuf buf)
    {
        super.deserialize(buf);
        recruitmentCosts = buf.readItemStack();
    }

    public ColonyView getColonyView()
    {
        return colony;
    }

    public ItemStack getRecruitCost()
    {
        return recruitmentCosts;
    }
}
