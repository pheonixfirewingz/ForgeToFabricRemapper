package com.minecolonies.coremod.colony.colony.requestsystem.resolvers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;


/**
 * A crafting resolver which takes care of 2x2 crafts which are crafted by the requesting worker.
 */
public class PrivateWorkerCraftingRequestResolver extends AbstractCraftingRequestResolver
{
    public PrivateWorkerCraftingRequestResolver(@NotNull final ILocation location, @NotNull final IToken<?> token)
    {
        super(location, token, false);
    }

    @Nullable
    @Override
    public List<IRequest<?>> getFollowupRequestForCompletion(@NotNull final IRequestManager manager, @NotNull final IRequest<? extends IDeliverable> completedRequest)
    {
        //No followup needed, crafting already completed at the requesting building / worker.
        return null;
    }

    @Override
    public void onAssignedRequestBeingCancelled(@NotNull final IRequestManager manager, @NotNull final IRequest<? extends IDeliverable> request)
    {
        return;
    }

    @Override
    public void onAssignedRequestCancelled(@NotNull final IRequestManager manager, @NotNull final IRequest<? extends IDeliverable> request)
    {

    }

    @Override
    public void onRequestedRequestCancelled(@NotNull final IRequestManager manager, @NotNull final IRequest<?> request)
    {

    }

    @NotNull
    @Override
    public IFormattableTextComponent getRequesterDisplayName(@NotNull final IRequestManager manager, @NotNull IRequest<?> request)
    {
        if (request.hasParent())
        {
            request = manager.getRequestForToken(request.getParent());
        }
        else
        {
            return new TranslationTextComponent(TranslationConstants.COM_MINECOLONIES_PRIVATE_CRAFTING_RESOLVER_NAME);
        }

        if (request == null)
        {
            return new TranslationTextComponent(TranslationConstants.COM_MINECOLONIES_PRIVATE_CRAFTING_RESOLVER_NAME);
        }

        return request.getRequester().getRequesterDisplayName(manager, request)
                 .append(new StringTextComponent(" ("))
                 .append(new TranslationTextComponent(TranslationConstants.COM_MINECOLONIES_PRIVATE_CRAFTING_RESOLVER_NAME))
                 .append(new StringTextComponent(")"));
    }

    @Override
    public int getPriority()
    {
        return CONST_CRAFTING_RESOLVER_PRIORITY;
    }

    @Override
    public boolean canBuildingCraftStack(@NotNull final AbstractBuildingWorker building, final Predicate<ItemStack> stackPredicate)
    {
        final IRecipeStorage recipe = building.getFirstRecipe(stackPredicate);
        return recipe != null && (recipe.getIntermediate() == null || recipe.getIntermediate() == Blocks.AIR);
    }

    @Override
    protected IRequestable createNewRequestableForStack(final ItemStack stack, final int count, final int minCount)
    {
        return new PrivateCrafting(stack, count, minCount);
    }
}
