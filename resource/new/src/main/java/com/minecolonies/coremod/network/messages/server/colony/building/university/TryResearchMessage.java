package com.minecolonies.coremod.network.messages.server.colony.building.university;

import org.jetbrains.annotations.NotNull;


/**
 * Message for the research execution.
 */
public class TryResearchMessage extends AbstractBuildingServerMessage<BuildingUniversity>
{
    /**
     * Id of research to try research.
     */
    private String researchId;

    /**
     * Id of research to try research.
     */
    private String branch;

    /**
     * Default constructor for forge
     */
    public TryResearchMessage() {super();}

    /**
     * Construct a message to attempt to research.
     *
     * @param researchId the research id.
     * @param branch     the research branch.
     * @param building   the building we're executing on.
     */
    public TryResearchMessage(final IBuildingView building, @NotNull final String researchId, final String branch)
    {
        super(building);
        this.researchId = researchId;
        this.branch = branch;
    }

    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {
        researchId = buf.readString(32767);
        branch = buf.readString(32767);
    }

    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {
        buf.writeString(researchId);
        buf.writeString(branch);
    }

    @Override
    protected void onExecute(
      final NetworkEvent.Context ctxIn, final boolean isLogicalServer, final IColony colony, final BuildingUniversity building)
    {
        final PlayerEntity player = ctxIn.getSender();
        if (player == null)
        {
            return;
        }

        if (colony.getResearchManager().getResearchTree().getResearch(branch, researchId) == null)
        {
            final IGlobalResearch research = IGlobalResearchTree.getInstance().getResearch(branch, researchId);
            if (research.canResearch(building.getBuildingLevel() == building.getMaxBuildingLevel() ? Integer.MAX_VALUE : building.getBuildingLevel(),
              colony.getResearchManager().getResearchTree()) && research.hasEnoughResources(new InvWrapper(player.inventory)) || player.isCreative())
            {
                if (player.isCreative())
                {
                    research.startResearch(player, colony.getResearchManager().getResearchTree());
                    colony.getResearchManager()
                      .getResearchTree()
                      .getResearch(branch, research.getId())
                      .setProgress((int) (BASE_RESEARCH_TIME * Math.pow(2, research.getDepth() - 1)));
                }
                else if (research.getResearchRequirement() != null && !research.getResearchRequirement().isFulfilled(colony))
                {
                    player.sendMessage(new TranslationTextComponent("com.minecolonies.coremod.research.requirementnotmet"), player.getUniqueID());
                    return;
                }
                else
                {
                    for (final ItemStorage cost : research.getCostList())
                    {
                        InventoryUtils.removeStackFromItemHandler(new InvWrapper(player.inventory), cost.getItemStack(), cost.getAmount());
                    }

                    player.sendMessage(new TranslationTextComponent("com.minecolonies.coremod.research.started", research.getDesc()), player.getUniqueID());
                    research.startResearch(player, colony.getResearchManager().getResearchTree());
                }
                colony.markDirty();
                // Remove items from player
            }
        }
        else
        {
            if (player.isCreative() && colony.getResearchManager().getResearchTree().getResearch(branch, researchId).getState() == ResearchState.IN_PROGRESS)
            {
                final IGlobalResearch research = IGlobalResearchTree.getInstance().getResearch(branch, researchId);
                colony.getResearchManager().getResearchTree().getResearch(branch, research.getId()).setProgress((int) (BASE_RESEARCH_TIME * Math.pow(2, research.getDepth() - 1)));
            }
            else
            {
                player.sendMessage(new TranslationTextComponent("com.minecolonies.coremod.research.alreadystarted"), player.getUniqueID());
            }
        }
    }
}
