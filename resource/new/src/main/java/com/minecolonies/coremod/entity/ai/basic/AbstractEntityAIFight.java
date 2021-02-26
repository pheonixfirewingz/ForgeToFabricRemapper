package com.minecolonies.coremod.entity.ai.basic;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Class taking of the abstract guard methods for both archer and knights.
 *
 * @param <J> the generic job.
 */
public abstract class AbstractEntityAIFight<J extends AbstractJobGuard<J>, B extends AbstractBuildingGuards> extends AbstractEntityAIInteract<J, B>
{

    /**
     * Tools and Items needed by the worker.
     */
    public final List<ToolType> toolsNeeded = new ArrayList<>();

    /**
     * List of items that are required by the guard based on building level and guard level.  This array holds a pointer to the building level and then pointer to GuardGear
     */
    public final List<List<GuardGear>> itemsNeeded = new ArrayList<>();

    /**
     * The current target for our guard.
     */
    protected LivingEntity target = null;

    /**
     * The value of the speed which the guard will move.
     */
    private static final double COMBAT_SPEED = 1.0;

    /**
     * The bonus speed per worker level.
     */
    public static final double SPEED_LEVEL_BONUS = 0.01;

    /**
     * Creates the abstract part of the AI. Always use this constructor!
     *
     * @param job the job to fulfill
     */
    public AbstractEntityAIFight(@NotNull final J job)
    {
        super(job);
        super.registerTargets(
          new AITarget(IDLE, START_WORKING, 1),
          new AITarget(START_WORKING, this::startWorkingAtOwnBuilding, 100),
          new AITarget(PREPARING, this::prepare, TICKS_SECOND)
        );
        worker.setCanPickUpLoot(true);

        itemsNeeded.add(GuardGearBuilder.buildGearForLevel(ARMOR_LEVEL_IRON, ARMOR_LEVEL_MAX, LEATHER_LEVEL_RANGE, DIA_BUILDING_LEVEL_RANGE));
        itemsNeeded.add(GuardGearBuilder.buildGearForLevel(ARMOR_LEVEL_CHAIN, ARMOR_LEVEL_DIAMOND, LEATHER_LEVEL_RANGE, DIA_BUILDING_LEVEL_RANGE));
        itemsNeeded.add(GuardGearBuilder.buildGearForLevel(ARMOR_LEVEL_LEATHER, ARMOR_LEVEL_IRON, LEATHER_LEVEL_RANGE, IRON_BUILDING_LEVEL_RANGE));
        itemsNeeded.add(GuardGearBuilder.buildGearForLevel(ARMOR_LEVEL_LEATHER, ARMOR_LEVEL_CHAIN, LEATHER_LEVEL_RANGE, CHAIN_BUILDING_LEVEL_RANGE));
        itemsNeeded.add(GuardGearBuilder.buildGearForLevel(ARMOR_LEVEL_LEATHER, ARMOR_LEVEL_GOLD, LEATHER_LEVEL_RANGE, GOLD_BUILDING_LEVEL_RANGE));
    }

    protected abstract int getAttackRange();

    /**
     * Redirects the guard to their building.
     *
     * @return The next {@link IAIState}.
     */
    protected IAIState startWorkingAtOwnBuilding()
    {
        worker.getCitizenStatusHandler().setLatestStatus(new TranslationTextComponent(TranslationConstants.COM_MINECOLONIES_COREMOD_STATUS_WORKER_GOINGTOHUT));
        if (walkToBuilding())
        {
            return getState();
        }
        return PREPARING;
    }

    /**
     * Prepares the guard. Fills his required armor and tool lists and transfer from building chest if required.
     *
     * @return The next {@link IAIState}.
     */
    private IAIState prepare()
    {
        setDelay(TICKS_SECOND * PREPARE_DELAY_SECONDS);

        for (final ToolType tool : toolsNeeded)
        {
            if (checkForToolOrWeapon(tool))
            {
                return getState();
            }
            else if (getOwnBuilding() != null)
            {
                InventoryFunctions.matchFirstInProviderWithSimpleAction(worker,
                  stack -> !ItemStackUtils.isEmpty(stack)
                             && ItemStackUtils.doesItemServeAsWeapon(stack)
                             && ItemStackUtils.hasToolLevel(stack, tool, 0, getOwnBuilding().getMaxToolLevel()),
                  itemStack -> worker.getCitizenItemHandler().setMainHeldItem(itemStack));
            }
        }

        equipInventoryArmor();

        // Can only "see" the inventory and check for items if at the building
        if (worker.getPosition().distanceSq(getOwnBuilding().getID()) > 50)
        {
            return DECIDE;
        }

        atBuildingActions();
        return DECIDE;
    }

    /**
     * Task to do when at the own building, as guards only go there on requests and on dump
     */
    protected void atBuildingActions()
    {
        final IGuardBuilding building = getOwnBuilding();
        for (final List<GuardGear> itemList : itemsNeeded)
        {
            for (final GuardGear item : itemList)
            {
                if (!(building.getBuildingLevel() >= item.getMinBuildingLevelRequired() && building.getBuildingLevel() <= item.getMaxBuildingLevelRequired()))
                {
                    continue;
                }

                int bestSlot = -1;
                int bestLevel = -1;
                IItemHandler bestHandler = null;

                if (!ItemStackUtils.isEmpty(worker.getItemStackFromSlot(item.getType())))
                {
                    bestLevel = ItemStackUtils.getMiningLevel(worker.getItemStackFromSlot(item.getType()), item.getItemNeeded());
                }

                final Map<IItemHandler, List<Integer>> items = InventoryUtils.findAllSlotsInProviderWith(building, item::test);
                if (items.isEmpty())
                {
                    // None found, check for equipped
                    if (ItemStackUtils.isEmpty(worker.getItemStackFromSlot(item.getType())))
                    {
                        // create request
                        checkForToolorWeaponASync(item.getItemNeeded(), item.getMinArmorLevel(), item.getMaxArmorLevel());
                    }
                }
                else
                {
                    // Compare levels
                    for (Map.Entry<IItemHandler, List<Integer>> entry : items.entrySet())
                    {
                        for (final Integer slot : entry.getValue())
                        {
                            final ItemStack stack = entry.getKey().getStackInSlot(slot);
                            if (ItemStackUtils.isEmpty(stack))
                            {
                                continue;
                            }

                            int currentLevel = ItemStackUtils.getMiningLevel(stack, item.getItemNeeded());

                            if (currentLevel > bestLevel)
                            {
                                bestLevel = currentLevel;
                                bestSlot = slot;
                                bestHandler = entry.getKey();
                            }
                        }
                    }
                }

                // Transfer if needed
                if (bestHandler != null)
                {
                    if (!ItemStackUtils.isEmpty(worker.getItemStackFromSlot(item.getType())))
                    {
                        final int slot =
                          InventoryUtils.findFirstSlotInItemHandlerNotEmptyWith(worker.getInventoryCitizen(), stack -> stack == worker.getItemStackFromSlot(item.getType()));
                        if (slot > -1)
                        {
                            InventoryUtils.transferItemStackIntoNextFreeSlotInProvider(worker.getInventoryCitizen(), slot, building);
                        }
                    }

                    // Used for further comparisons, set to the right inventory slot afterwards
                    worker.setItemStackToSlot(item.getType(), bestHandler.getStackInSlot(bestSlot));
                    InventoryUtils.transferItemStackIntoNextFreeSlotInItemHandler(bestHandler, bestSlot, worker.getInventoryCitizen());
                }
            }
        }

        equipInventoryArmor();
    }

    /**
     * This gets the attack speed for the guard with adjustment for guards level. Capped at 2
     *
     * @return movement speed for guard
     */
    public double getCombatMovementSpeed()
    {
        if (worker.getCitizenData() == null)
        {
            return COMBAT_SPEED;
        }
        double levelAdjustment = getCombatSpeedBonus();

        if (getOwnBuilding() != null)
        {
            levelAdjustment += (getOwnBuilding().getBuildingLevel() * 2 - 1) * SPEED_LEVEL_BONUS;
        }

        levelAdjustment = levelAdjustment > 0.3 ? 0.3 : levelAdjustment;
        return COMBAT_SPEED + levelAdjustment;
    }

    /**
     * Override to set a combat speed bonus
     *
     * @return bonus movement speed
     */
    protected double getCombatSpeedBonus()
    {
        return 0;
    }

    /**
     * Gets the base reload time for an attack.
     *
     * @return the reload time
     */
    protected int getAttackDelay()
    {
        if (worker.getCitizenData() != null)
        {
            final int delay = PHYSICAL_ATTACK_DELAY_BASE - (worker.getCitizenData().getCitizenSkillHandler().getLevel(Skill.Adaptability) / 5);
            return delay > PHYSICAL_ATTACK_DELAY_MIN ? PHYSICAL_ATTACK_DELAY_MIN : delay;
        }
        return PHYSICAL_ATTACK_DELAY_BASE;
    }

    @Override
    public IAIState afterDump()
    {
        return PREPARING;
    }

    /**
     * Equips armor existing in inventory
     */
    public void equipInventoryArmor()
    {
        cleanArmor();
        final IGuardBuilding building = getOwnBuilding();

        for (final List<GuardGear> itemList : itemsNeeded)
        {
            for (final GuardGear item : itemList)
            {
                if (building.getBuildingLevel() >= item.getMinBuildingLevelRequired() && building.getBuildingLevel() <= item.getMaxBuildingLevelRequired())
                {
                    int slot = InventoryUtils.findFirstSlotInItemHandlerNotEmptyWith(worker.getInventoryCitizen(), item::test);

                    if (slot > -1)
                    {
                        worker.setItemStackToSlot(item.getType(), worker.getInventoryCitizen().getStackInSlot(slot));
                    }
                }
            }
        }
    }

    /**
     * Removes currently equipped armor and shields
     */
    public void cleanArmor()
    {
        worker.setItemStackToSlot(EquipmentSlotType.CHEST, ItemStackUtils.EMPTY);
        worker.setItemStackToSlot(EquipmentSlotType.FEET, ItemStackUtils.EMPTY);
        worker.setItemStackToSlot(EquipmentSlotType.HEAD, ItemStackUtils.EMPTY);
        worker.setItemStackToSlot(EquipmentSlotType.LEGS, ItemStackUtils.EMPTY);
        worker.setItemStackToSlot(EquipmentSlotType.OFFHAND, ItemStackUtils.EMPTY);
    }
}
