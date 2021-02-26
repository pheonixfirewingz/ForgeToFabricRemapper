package com.minecolonies.api.advancements.deep_mine;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * A Trigger that is triggered when the miner reaches a certain depth
 */
public class DeepMineTrigger extends AbstractCriterionTrigger<CriterionListeners<DeepMineCriterionInstance>, DeepMineCriterionInstance>
{
    private final static ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, Constants.CRITERION_DEEP_MINE);

    public DeepMineTrigger()
    {
        super(ID, CriterionListeners::new);
    }

    /**
     * Triggers the listener checks if there are any listening in
     * @param player the player the check regards
     */
    public void trigger(final ServerPlayerEntity player)
    {
        final CriterionListeners<DeepMineCriterionInstance> listeners = this.getListeners(player.getAdvancements());
        if (listeners != null)
        {
            listeners.trigger();
        }
    }

    @NotNull
    @Override
    public DeepMineCriterionInstance deserialize(@NotNull final JsonObject object, @NotNull final ConditionArrayParser conditions)
    {
        return new DeepMineCriterionInstance();
    }
}
