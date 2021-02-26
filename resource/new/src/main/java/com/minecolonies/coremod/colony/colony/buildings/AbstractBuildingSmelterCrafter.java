package com.minecolonies.coremod.colony.colony.buildings;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Class of the crafter building.
 */
public abstract class AbstractBuildingSmelterCrafter extends AbstractBuildingFurnaceUser implements IBuildingPublicCrafter
{

    /**
     * Instantiates a new crafter building.
     *
     * @param c the colony.
     * @param l the location
     */
    public AbstractBuildingSmelterCrafter(final IColony c, final BlockPos l)
    {
        super(c, l);
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return CONST_DEFAULT_MAX_BUILDING_LEVEL;
    }

    @Override
    public ImmutableCollection<IRequestResolver<?>> createResolvers()
    {
        final ImmutableCollection<IRequestResolver<?>> supers = super.createResolvers();
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
        for (final ICitizenData citizen : getAssignedCitizen())
        {
            if (citizen.getJob() instanceof AbstractJobCrafter)
            {
                final List<IToken<?>> assignedTaskIds = ((AbstractJobCrafter<?, ?>) citizen.getJob()).getAssignedTasksFromDataStore();
                for (final IToken<?> taskToken : assignedTaskIds)
                {
                    final IRequest<? extends PublicCrafting> request = (IRequest<? extends PublicCrafting>) colony.getRequestManager().getRequestForToken(taskToken);
                    if (request != null)
                    {
                        final IRecipeStorage recipeStorage = getFirstRecipe(request.getRequest().getStack());
                        if (recipeStorage != null)
                        {
                            for (final ItemStorage itemStorage : recipeStorage.getCleanedInput())
                            {
                                int amount = itemStorage.getAmount() * request.getRequest().getCount();
                                if (requiredItems.containsKey(itemStorage))
                                {
                                    amount += requiredItems.get(itemStorage).getA();
                                }
                                requiredItems.put(itemStorage, new Tuple<>(amount, false));
                            }
                            final ItemStorage output = new ItemStorage(recipeStorage.getPrimaryOutput());
                            if (requiredItems.containsKey(output))
                            {
                                output.setAmount(requiredItems.get(output).getA() + output.getAmount());
                            }
                            requiredItems.put(output, new Tuple<>(output.getAmount(), false));
                        }
                    }
                }
            }
        }

        final Map<Predicate<ItemStack>, Tuple<Integer, Boolean>> toKeep = new HashMap<>(super.getRequiredItemsAndAmount());
        toKeep.putAll(requiredItems.entrySet().stream().collect(Collectors.toMap(key -> (stack -> stack.isItemEqual(key.getKey().getItemStack())), Map.Entry::getValue)));
        return toKeep;
    }

    @Override
    public boolean canCraftComplexRecipes()
    {
        return true;
    }

    @Override
    public Skill getCraftSpeedSkill()
    {
        return getSecondarySkill();
    }

    @Override
    public void openCraftingContainer(final ServerPlayerEntity player)
    {
        NetworkHooks.openGui(player, new INamedContainerProvider()
        {
            @Override
            public ITextComponent getDisplayName()
            {
                return new StringTextComponent("Furnace Crafting GUI");
            }

            @NotNull
            @Override
            public Container createMenu(final int id, @NotNull final PlayerInventory inv, @NotNull final PlayerEntity player)
            {
                final PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
                buffer.writeBlockPos(getID());
                return new ContainerCraftingFurnace(id, inv, buffer);
            }
        }, getID());
    }


    /**
     * Crafter building View.
     */
    public static class View extends AbstractFilterableListsView
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


}
