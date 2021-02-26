package com.minecolonies.api.advancements.create_build_request;

import com.google.gson.JsonObject;

/**
 * A Trigger for any building request that gets made
 */
public class CreateBuildRequestTrigger extends AbstractCriterionTrigger<CreateBuildRequestListeners, CreateBuildRequestCriterionInstance>
{
    public CreateBuildRequestTrigger()
    {
        super(new ResourceLocation(Constants.MOD_ID, Constants.CRITERION_CREATE_BUILD_REQUEST), CreateBuildRequestListeners::new);
    }

    /**
     * Triggers the listener checks if there are any listening in
     * @param player the player the check regards
     * @param structureName the structure that is to be created
     * @param level the level that the request will complete
     */
    public void trigger(final ServerPlayerEntity player, final StructureName structureName, final int level)
    {
        if (player != null)
        {
            final CreateBuildRequestListeners listeners = this.getListeners(player.getAdvancements());
            if (listeners != null)
            {
                listeners.trigger(structureName, level);
            }
        }
    }

    @Override
    public CreateBuildRequestCriterionInstance deserialize(final JsonObject jsonObject, final ConditionArrayParser conditionArrayParser)
    {
        if (jsonObject.has("hut_name"))
        {
            final String hutName = JSONUtils.getString(jsonObject, "hut_name");
            if (jsonObject.has("level"))
            {
                final int level = JSONUtils.getInt(jsonObject, "level");
                return new CreateBuildRequestCriterionInstance(hutName, level);
            }
            return new CreateBuildRequestCriterionInstance(hutName);
        }

        if (jsonObject.has("structure_name"))
        {
            final StructureName structureName = new StructureName(JSONUtils.getString(jsonObject, "structure_name"));
            if (jsonObject.has("structure_name"))
            {
                final int level = JSONUtils.getInt(jsonObject, "level");
                return new CreateBuildRequestCriterionInstance(structureName, level);
            }
            return new CreateBuildRequestCriterionInstance(structureName);
        }

        return new CreateBuildRequestCriterionInstance();
    }
}