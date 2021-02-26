package com.minecolonies.coremod.colony.colony.buildings.workerbuildings;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Class of the dyer building.
 */
public class BuildingDyer extends AbstractBuildingSmelterCrafter
{
    /**
     * Description string of the building.
     */
    private static final String DYER = "dyer";

    /**
     * Instantiates a new dyer building.
     *
     * @param c the colony.
     * @param l the location
     */
    public BuildingDyer(final IColony c, final BlockPos l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return DYER;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return CONST_DEFAULT_MAX_BUILDING_LEVEL;
    }

    @NotNull
    @Override
    public IJob<?> createJob(final ICitizenData citizen)
    {
        return new JobDyer(citizen);
    }

    @NotNull
    @Override
    public String getJobName()
    {
        return DYER;
    }

    @NotNull
    @Override
    public Skill getPrimarySkill()
    {
        return Skill.Creativity;
    }

    @NotNull
    @Override
    public Skill getSecondarySkill()
    {
        return Skill.Dexterity;
    }

    @Override
    public boolean canRecipeBeAdded(final IToken<?> token)
    {

        Optional<Boolean> isRecipeAllowed;

        if (!super.canRecipeBeAdded(token))
        {
            return false;
        }

        isRecipeAllowed = super.canRecipeBeAddedBasedOnTags(token);
        return isRecipeAllowed.orElse(false);
    }

    @Override
    public boolean canCraftComplexRecipes()
    {
        return true;
    }

    @Override
    public void openCraftingContainer(final ServerPlayerEntity player)
    {
        NetworkHooks.openGui(player, new INamedContainerProvider()
        {
            @Override
            public ITextComponent getDisplayName()
            {
                return new StringTextComponent("Crafting GUI");
            }

            @NotNull
            @Override
            public Container createMenu(final int id, @NotNull final PlayerInventory inv, @NotNull final PlayerEntity player)
            {
                final PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
                buffer.writeBoolean(canCraftComplexRecipes());
                buffer.writeBlockPos(getID());
                return new ContainerCrafting(id, inv, buffer);
            }
        }, buffer -> new PacketBuffer(buffer.writeBoolean(canCraftComplexRecipes())).writeBlockPos(getID()));
    }

    @Override
    public BuildingEntry getBuildingRegistryEntry()
    {
        return ModBuildings.dyer;
    }

    @Override
    public void requestUpgrade(final PlayerEntity player, final BlockPos builder)
    {
        final UnlockBuildingResearchEffect effect = colony.getResearchManager().getResearchEffects().getEffect(ResearchInitializer.DYER_RESEARCH, UnlockBuildingResearchEffect.class);
        if (effect == null)
        {
            player.sendMessage(new TranslationTextComponent("com.minecolonies.coremod.research.havetounlock"), player.getUniqueID());
            return;
        }
        super.requestUpgrade(player, builder);
    }

    @Override
    public IRecipeStorage getFirstRecipe(Predicate<ItemStack> stackPredicate)
    {
        IRecipeStorage recipe = super.getFirstRecipe(stackPredicate);

        if(recipe == null && stackPredicate.test(new ItemStack(Items.WHITE_WOOL)))
        {
            final HashMap<ItemStorage, Integer> inventoryCounts = new HashMap<>();

            if (!colony.getBuildingManager().hasWarehouse())
            {
                return null;
            }
            
            final List<ItemStorage> woolItems = ItemTags.WOOL.getAllElements().stream()
                                                        .filter(item -> !item.equals(Items.WHITE_WOOL))
                                                        .map(i -> new ItemStorage(new ItemStack(i))).collect(Collectors.toList());

            for(ItemStorage color : woolItems)
            {
                for(IBuilding wareHouse: colony.getBuildingManager().getWareHouses())
                {
                    final int colorCount = InventoryUtils.getCountFromBuilding(wareHouse, color);
                    inventoryCounts.put(color, inventoryCounts.getOrDefault(color, 0) + colorCount);
                }
            }

            ItemStorage woolToUse = inventoryCounts.entrySet().stream().min(Map.Entry.comparingByValue(Comparator.reverseOrder())).get().getKey();

            recipe = StandardFactoryController.getInstance().getNewInstance(
                TypeConstants.RECIPE,
                StandardFactoryController.getInstance().getNewInstance(TypeConstants.ITOKEN),
                ImmutableList.of(woolToUse.getItemStack(), new ItemStack(Items.WHITE_DYE, 1)),
                1,
                new ItemStack(Items.WHITE_WOOL, 1),
                Blocks.AIR);
        }
        return recipe;
    }

    @Override
    public IRecipeStorage getFirstFullFillableRecipe(Predicate<ItemStack> stackPredicate, int count, final boolean considerReservation)
    {
        IRecipeStorage recipe =  super.getFirstFullFillableRecipe(stackPredicate, count, considerReservation);

        if(recipe == null)
        {
            final IRecipeStorage storage = getFirstRecipe(stackPredicate);
            if (storage != null && stackPredicate.test(storage.getPrimaryOutput()))
            {
                final List<IItemHandler> handlers = getHandlers();
                if (storage.canFullFillRecipe(count, Collections.emptyMap(), handlers.toArray(new IItemHandler[0])))
                {
                    return storage;
                }
            }
        }
        return recipe;        
    }

    /**
     * Dyer View.
     */
    public static class View extends AbstractBuildingSmelterCrafter.View
    {

        /**
         * Instantiate the dyer view.
         *
         * @param c the colonyview to put it in
         * @param l the positon
         */
        public View(final IColonyView c, final BlockPos l)
        {
            super(c, l);
        }

        @NotNull
        @Override
        public Window getWindow()
        {
            return new WindowHutDyer(this);
        }
    }
}
