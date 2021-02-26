package com.minecolonies.api.advancements.place_structure;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * Triggers whenever the build tool is used to position a new structure
 */
public class PlaceStructureTrigger extends AbstractCriterionTrigger<PlaceStructureListeners, PlaceStructureCriterionInstance>
{
    public PlaceStructureTrigger()
    {
        super(new ResourceLocation(Constants.MOD_ID, Constants.CRITERION_STRUCTURE_PLACED), PlaceStructureListeners::new);
    }

    /**
     * Triggers the listener checks if there are any listening in
     * @param player the player the check regards
     * @param structureName the structure id of what was just placed
     */
    public void trigger(final ServerPlayerEntity player, final StructureName structureName)
    {
        final PlaceStructureListeners listeners = this.getListeners(player.getAdvancements());
        if (listeners != null)
        {
            listeners.trigger(structureName);
        }
    }

    @NotNull
    @Override
    public PlaceStructureCriterionInstance deserialize(@NotNull final JsonObject jsonObject, @NotNull final ConditionArrayParser conditionArrayParser)
    {
        if (jsonObject.has("hut_name"))
        {
            final String hutName = JSONUtils.getString(jsonObject, "hut_name");
            return new PlaceStructureCriterionInstance(hutName);
        }
        else if (jsonObject.has("structure_name"))
        {
            final StructureName structureName = new StructureName(JSONUtils.getString(jsonObject, "structure_name"));
            return new PlaceStructureCriterionInstance(structureName);
        }
        return new PlaceStructureCriterionInstance();
    }
}