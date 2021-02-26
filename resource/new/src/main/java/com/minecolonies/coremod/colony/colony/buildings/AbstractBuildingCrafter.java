package com.minecolonies.coremod.colony.colony.buildings;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Class of the crafter building.
 */
public abstract class AbstractBuildingCrafter extends AbstractBuildingWorker implements IBuildingPublicCrafter
{


    /**
     * Instantiates a new crafter building.
     *
     * @param c the colony.
     * @param l the location
     */
    public AbstractBuildingCrafter(final IColony c, final BlockPos l)
    {
        super(c, l);
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return CONST_DEFAULT_MAX_BUILDING_LEVEL;
    }

    @Override
    public boolean canBeGathered()
    {
        return super.canBeGathered() &&
                 this.getAssignedCitizen().stream()
                   .map(c -> c.getJob(AbstractJobCrafter.class))
                   .filter(Objects::nonNull)
                   .allMatch(AbstractJobCrafter::hasTask);
    }

    @Override
    public ImmutableCollection<IRequestResolver<?>> createResolvers()
    {
        final Collection<IRequestResolver<?>> supers =
          super.createResolvers().stream()
            .filter(r -> !(r instanceof PrivateWorkerCraftingProductionResolver || r instanceof PrivateWorkerCraftingRequestResolver)).collect(
            Collectors.toList());
        final ImmutableList.Builder<IRequestResolver<?>> builder = ImmutableList.builder();

        builder.addAll(supers);
        builder.add(new PublicWorkerCraftingRequestResolver(getRequester().getLocation(),
          getColony().getRequestManager().getFactoryController().getNewInstance(TypeConstants.ITOKEN)));
        builder.add(new PublicWorkerCraftingProductionResolver(getRequester().getLocation(),
          getColony().getRequestManager().getFactoryController().getNewInstance(TypeConstants.ITOKEN)));

        return builder.build();
    }

    @Override
    public Map<Predicate<ItemStack>, Tuple<Integer, Boolean>> getRequiredItemsAndAmount()
    {
        final Map<ItemStorage, Tuple<Integer, Boolean>> requiredItems = new HashMap<>();
        for (final Tuple<IRecipeStorage, Integer> recipeStorage : getPendingRequestQueue())
        {
            for (final ItemStorage itemStorage : recipeStorage.getA().getCleanedInput())
            {
                int amount = itemStorage.getAmount() * recipeStorage.getB();
                if (requiredItems.containsKey(itemStorage))
                {
                    amount += requiredItems.get(itemStorage).getA();
                }
                requiredItems.put(itemStorage, new Tuple<>(amount, false));
            }

            final ItemStorage output = new ItemStorage(recipeStorage.getA().getPrimaryOutput());
            int amount = output.getAmount() * recipeStorage.getB();
            if (requiredItems.containsKey(output))
            {
                amount += requiredItems.get(output).getA();
            }
            requiredItems.put(output, new Tuple<>(amount, false));
        }

        final Map<Predicate<ItemStack>, Tuple<Integer, Boolean>> toKeep = super.getRequiredItemsAndAmount();
        toKeep.putAll(requiredItems.entrySet().stream().collect(Collectors.toMap(key -> (stack -> stack.isItemEqual(key.getKey().getItemStack())), Map.Entry::getValue)));
        return toKeep;
    }

    @Override
    public Map<ItemStorage, Integer> reservedStacks()
    {
        final Map<ItemStorage, Integer> recipeOutputs = new HashMap<>();
        for (final Tuple<IRecipeStorage, Integer> recipeStorage : getPendingRequestQueue())
        {
            for (final ItemStorage itemStorage : recipeStorage.getA().getCleanedInput())
            {
                int amount = itemStorage.getAmount() * recipeStorage.getB();
                if (recipeOutputs.containsKey(itemStorage))
                {
                    amount += recipeOutputs.get(itemStorage);
                }
                recipeOutputs.put(itemStorage, amount);
            }
        }
        return recipeOutputs;
    }

    /**
     * Get a list of all recipeStorages of the pending requests in the crafters queues.
     * @return the list.
     */
    private List<Tuple<IRecipeStorage, Integer>> getPendingRequestQueue()
    {
        final List<Tuple<IRecipeStorage, Integer>> recipes = new ArrayList<>();
        for (final ICitizenData citizen : getAssignedCitizen())
        {
            if (citizen.getJob() instanceof AbstractJobCrafter)
            {
                final List<IToken<?>> assignedTasks = new ArrayList<>(citizen.getJob(AbstractJobCrafter.class).getAssignedTasks());
                assignedTasks.addAll(citizen.getJob(AbstractJobCrafter.class).getTaskQueue());

                for (final IToken<?> taskToken : assignedTasks)
                {
                    final IRequest<? extends PublicCrafting> request = (IRequest<? extends PublicCrafting>) colony.getRequestManager().getRequestForToken(taskToken);
                    final IRecipeStorage recipeStorage = getFirstRecipe(request.getRequest().getStack());
                    if (recipeStorage != null)
                    {
                        recipes.add(new Tuple<>(recipeStorage, request.getRequest().getCount()));
                    }
                }
            }
        }
        return recipes;
    }

    @Override
    public IRecipeStorage getFirstFullFillableRecipe(final Predicate<ItemStack> stackPredicate, final int count, final boolean considerReservation)
    {
        for (final IToken<?> token : recipes)
        {
            final IRecipeStorage storage = IColonyManager.getInstance().getRecipeManager().getRecipes().get(token);
            if (storage != null && (stackPredicate.test(storage.getPrimaryOutput()) || storage.getAlternateOutputs().stream().anyMatch(i -> stackPredicate.test(i))))
            {
                final List<IItemHandler> handlers = getHandlers();
                IRecipeStorage toTest = storage.getRecipeType() instanceof MultiOutputRecipe ? storage.getClassicForMultiOutput(stackPredicate) : storage;
                if (toTest.canFullFillRecipe(count, considerReservation ? reservedStacks() : Collections.emptyMap(), handlers.toArray(new IItemHandler[0])))
                {
                    return toTest;
                }
            }
        }
        return null;
    }

    @Override
    public boolean canCraftComplexRecipes()
    {
        return true;
    }

    /**
     * Crafter building View.
     */
    public static class View extends AbstractBuildingWorker.View
    {
        /**
         * Instantiate the crafter view.
         *
         * @param c the colonyview to put it in
         * @param l the positon
         */
        public View(final IColonyView c, final BlockPos l)
        {
            super(c, l);
        }

    }

    @Override
    protected Optional<Boolean> canRecipeBeAddedBasedOnTags(final IToken token)
    {
        return super.canRecipeBeAddedBasedOnTags(token);
    }

    @Override
    public Skill getCraftSpeedSkill()
    {
        return getSecondarySkill();
    }

    @Override
    public Skill getRecipeImprovementSkill()
    {
        return getPrimarySkill();
    }
}
