package com.minecolonies.coremod.permissions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class handles all permission checks on events and cancels them if needed.
 */
{
    /**
     * The colony involved in this permission-check event
     */
    private final Colony colony;

    /**
     * The last time the player was notified about not having permission.
     */
    private final Map<UUID, Long> lastPlayerNotificationTick = new HashMap<>();

    /**
     * Create this EventHandler.
     *
     * @param colony the colony to check on.
     */
    public ColonyPermissionEventHandler(final Colony colony)
    {
        this.colony = colony;
    }

    /**
     * BlockEvent.PlaceEvent handler.
     *
     * @param event BlockEvent.PlaceEvent
     */
    @SubscribeEvent
    public void on(final BlockEvent.EntityPlaceEvent event)
    {
        final Action action = event.getPlacedBlock().getBlock() instanceof AbstractBlockHut ? Action.PLACE_HUTS : Action.PLACE_BLOCKS;
        if (MineColonies.getConfig().getServer().enableColonyProtection.get() && checkBlockEventDenied(event.getWorld(),
          event.getPos(),
          event.getEntity(),
          event.getPlacedBlock(),
          action))
        {
            cancelEvent(event, event.getEntity(), colony, action, event.getPos());
        }
    }

    /**
     * This method returns TRUE if this event should be denied.
     *
     * @param worldIn    the world to check in
     * @param posIn      the block to check
     * @param entity     the player who tries
     * @param blockState the state that block is in
     * @param action     the action that was performed on the position
     * @return true if canceled
     */
    private boolean checkBlockEventDenied(
      final IWorld worldIn, final BlockPos posIn, final Entity entity, final BlockState blockState,
      final Action action)
    {
        if (entity instanceof PlayerEntity)
        {
            @NotNull final PlayerEntity player = EntityUtils.getPlayerOfFakePlayer((PlayerEntity) entity, entity.world);
            if (colony.isCoordInColony(entity.world, posIn))
            {
                if (blockState.getBlock() instanceof AbstractBlockHut
                      && colony.getPermissions().hasPermission(player, action))
                {
                    return false;
                }

                return !colony.getPermissions().hasPermission(player, action);
            }
        }
        /*
         * - We are not inside the colony
         * - We are in but not denied
         * - The placer is not a player.
         */
        return false;
    }

    /**
     * Cancel an event and record the denial details in the colony's town hall.
     *
     * @param event  the event to cancel
     * @param entity the player whose action was denied
     * @param colony the colony where the event took place
     * @param action the action which was denied
     * @param pos    the location of the action which was denied
     */
    private void cancelEvent(final Event event, @Nullable final Entity entity, final Colony colony, final Action action, final BlockPos pos)
    {
        event.setResult(Event.Result.DENY);
        if (event.isCancelable())
        {
            event.setCanceled(true);
            if (entity == null)
            {
                if (colony.hasTownHall())
                {
                    colony.getBuildingManager().getTownHall().addPermissionEvent(new PermissionEvent(null, "-", action, pos));
                }
                return;
            }
            if (colony.hasTownHall())
            {
                colony.getBuildingManager().getTownHall().addPermissionEvent(new PermissionEvent(entity.getUniqueID(), entity.getName().getString(), action, pos));
            }


            if (entity instanceof FakePlayer)
            {
                return;
            }

            final long worldTime = entity.world.getGameTime();
            if (!lastPlayerNotificationTick.containsKey(entity.getUniqueID())
                  || lastPlayerNotificationTick.get(entity.getUniqueID()) + (Constants.TICKS_SECOND * MineColonies.getConfig().getServer().secondsBetweenPermissionMessages.get())
                       < worldTime)
            {
                LanguageHandler.sendPlayerMessage((PlayerEntity) entity, "com.minecolonies.coremod.permission.no");
                lastPlayerNotificationTick.put(entity.getUniqueID(), worldTime);
            }
        }
    }

    /**
     * BlockEvent.BreakEvent handler.
     *
     * @param event BlockEvent.BreakEvent
     */
    @SubscribeEvent
    public void on(final BlockEvent.BreakEvent event)
    {
        final IWorld world = event.getWorld();
        if (!MineColonies.getConfig().getServer().enableColonyProtection.get() || world.isRemote())
        {
            return;
        }

        if (event.getState().getBlock() instanceof AbstractBlockHut)
        {
            @Nullable final IBuilding building = IColonyManager.getInstance().getBuilding(event.getPlayer().world, event.getPos());
            if (building == null)
            {
                return;
            }

            if (event.getState().getBlock() == ModBlocks.blockHutTownHall && !((BlockHutTownHall)event.getState().getBlock()).getValidBreak() && !event.getPlayer().isCreative())
            {
                cancelEvent(event, event.getPlayer(), colony, Action.BREAK_HUTS, event.getPos());
                return;
            }

            if (!building.getColony().getPermissions().hasPermission(event.getPlayer(), Action.BREAK_HUTS))
            {
                if (checkEventCancelation(Action.BREAK_HUTS, event.getPlayer(), event.getPlayer().getEntityWorld(), event, event.getPos()))
                {
                    return;
                }
            }

            building.destroy();

            if (MineColonies.getConfig().getServer().pvp_mode.get() && event.getState().getBlock() == ModBlocks.blockHutTownHall)
            {
                IColonyManager.getInstance().deleteColonyByWorld(building.getColony().getID(), false, event.getPlayer().world);
            }
        }
        else
        {
            checkEventCancelation(Action.BREAK_BLOCKS, event.getPlayer(), event.getPlayer().getEntityWorld(), event, event.getPos());
        }
    }

    /**
     * ExplosionEvent.Detonate handler.
     *
     * @param event ExplosionEvent.Detonate
     */
    @SubscribeEvent
    public void on(final ExplosionEvent.Detonate event)
    {
        if (!MineColonies.getConfig().getServer().turnOffExplosionsInColonies.get())
        {
            return;
        }

        final World eventWorld = event.getWorld();
        final Predicate<BlockPos> getBlocksInColony = pos -> colony.isCoordInColony(eventWorld, pos);
        final Predicate<Entity> getEntitiesInColony = entity -> colony.isCoordInColony(entity.getEntityWorld(), new BlockPos(entity.getPositionVec()));
        // if block is in colony -> remove from list
        final List<BlockPos> blocksToRemove = event.getAffectedBlocks().stream()
                                                .filter(getBlocksInColony)
                                                .collect(Collectors.toList());

        // if entity is in colony -> remove from list
        final List<Entity> entitiesToRemove = event.getAffectedEntities().stream()
                                                .filter(getEntitiesInColony)
                                                .collect(Collectors.toList());
        event.getAffectedBlocks().removeAll(blocksToRemove);
        event.getAffectedEntities().removeAll(entitiesToRemove);
    }

    /**
     * ExplosionEvent.Start handler.
     *
     * @param event ExplosionEvent.Detonate
     */
    @SubscribeEvent
    public void on(final ExplosionEvent.Start event)
    {
        if (MineColonies.getConfig().getServer().enableColonyProtection.get()
              && MineColonies.getConfig().getServer().turnOffExplosionsInColonies.get()
              && colony.isCoordInColony(event.getWorld(), new BlockPos(event.getExplosion().getPosition())))
        {
            cancelEvent(event, null, colony, Action.EXPLODE, new BlockPos(event.getExplosion().getPosition()));
        }
    }

    /**
     * PlayerInteractEvent handler.
     * <p>
     * Check, if a player right clicked a block. Deny if: - If the block is in colony - block is AbstractBlockHut - player has not permission
     *
     * @param event PlayerInteractEvent
     */
    @SubscribeEvent
    public void on(final PlayerInteractEvent event)
    {
        if (colony.isCoordInColony(event.getWorld(), event.getPos())
              && !(event instanceof PlayerInteractEvent.EntityInteract || event instanceof PlayerInteractEvent.EntityInteractSpecific))
        {
            final Block block = event.getWorld().getBlockState(event.getPos()).getBlock();

            // Huts
            if (event instanceof PlayerInteractEvent.RightClickBlock && block instanceof AbstractBlockHut
                  && !colony.getPermissions().hasPermission(event.getPlayer(), Action.ACCESS_HUTS))
            {
                cancelEvent(event, event.getPlayer(), colony, Action.ACCESS_HUTS, event.getPos());
                return;
            }

            final Permissions perms = colony.getPermissions();

            if (isFreeToInteractWith(block, event.getPos())
                  && perms.hasPermission(event.getPlayer(), Action.ACCESS_FREE_BLOCKS))
            {
                return;
            }

            if (MineColonies.getConfig().getServer().enableColonyProtection.get())
            {
                if (!perms.hasPermission(event.getPlayer(), Action.RIGHTCLICK_BLOCK) && !(block instanceof AirBlock))
                {
                    checkEventCancelation(Action.RIGHTCLICK_BLOCK, event.getPlayer(), event.getWorld(), event, event.getPos());
                    return;
                }

                if (block instanceof ContainerBlock && !perms.hasPermission(event.getPlayer(),
                  Action.OPEN_CONTAINER))
                {
                    cancelEvent(event, event.getPlayer(), colony, Action.OPEN_CONTAINER, event.getPos());
                    return;
                }

                if (event.getWorld().getTileEntity(event.getPos()) != null && !perms.hasPermission(event.getPlayer(), Action.RIGHTCLICK_ENTITY))
                {
                    checkEventCancelation(Action.RIGHTCLICK_ENTITY, event.getPlayer(), event.getWorld(), event, event.getPos());
                    return;
                }

                final ItemStack stack = event.getItemStack();
                if (ItemStackUtils.isEmpty(stack) || stack.getItem().isFood())
                {
                    return;
                }


                if (stack.getItem() instanceof PotionItem)
                {
                    checkEventCancelation(Action.THROW_POTION, event.getPlayer(), event.getWorld(), event, event.getPos());
                    return;
                }

                if (stack.getItem() instanceof ItemScanTool
                      && !perms.hasPermission(event.getPlayer(), Action.USE_SCAN_TOOL))
                {
                    cancelEvent(event, event.getPlayer(), colony, Action.USE_SCAN_TOOL, event.getPos());
                }
            }
        }
    }

    /**
     * Check in the config if that block can be interacted with freely.
     *
     * @param block the block to check.
     * @param pos   the position of the interaction
     * @return true if so.
     */
    private boolean isFreeToInteractWith(@Nullable final Block block, final BlockPos pos)
    {
        return (block != null && (IColonyManager.getInstance().getCompatibilityManager().isFreeBlock(block) || colony.getFreeBlocks().contains(block) || ModTags.colonyProtectionException.contains(block))) || colony.getFreePositions().contains(pos) || IColonyManager.getInstance().getCompatibilityManager().isFreePos(pos);
    }

    /**
     * PlayerInteractEvent.EntityInteract handler.
     * <p>
     * Check, if a player right clicked an entity. Deny if: - If the entity is in colony - player has not permission
     *
     * @param event PlayerInteractEvent
     */
    @SubscribeEvent
    public void on(final PlayerInteractEvent.EntityInteract event)
    {
        if (isFreeToInteractWith(null, event.getPos())
              && colony.getPermissions().hasPermission(event.getPlayer(), Action.ACCESS_FREE_BLOCKS))
        {
            return;
        }

        checkEventCancelation(Action.RIGHTCLICK_ENTITY, event.getPlayer(), event.getWorld(), event, event.getPos());
    }

    /**
     * Check if the event should be canceled for a given player and minimum rank.
     *
     * @param action   the action that was performed on the position
     * @param playerIn the player.
     * @param world    the world.
     * @param event    the event.
     * @param pos      the position.  Can be null if no target was provided to the event.
     * @return true if canceled.
     */
    private boolean checkEventCancelation(
      final Action action, @NotNull final PlayerEntity playerIn, @NotNull final World world, @NotNull final Event event,
      @Nullable final BlockPos pos)
    {
        @NotNull final PlayerEntity player = EntityUtils.getPlayerOfFakePlayer(playerIn, world);

        BlockPos positionToCheck = pos;
        if (null == positionToCheck)
        {
            positionToCheck = new BlockPos(player.getPositionVec());
        }
        if (MineColonies.getConfig().getServer().enableColonyProtection.get()
              && colony.isCoordInColony(player.getEntityWorld(), positionToCheck)
              && !colony.getPermissions().hasPermission(player, action))
        {
            if (MineColonies.getConfig().getServer().pvp_mode.get())
            {
                if (!world.isRemote && colony.isValidAttackingPlayer(playerIn))
                {
                    return false;
                }
            }
            else
            {
                cancelEvent(event, player, colony, action, positionToCheck);
                return true;
            }
        }
        return false;
    }

    /**
     * PlayerInteractEvent.EntityInteractSpecific handler.
     * <p>
     * Check, if a player right clicked a entity. Deny if: - If the entity is in colony - player has not permission
     *
     * @param event PlayerInteractEvent
     */
    @SubscribeEvent
    public void on(final PlayerInteractEvent.EntityInteractSpecific event)
    {
        if (isFreeToInteractWith(null, event.getPos())
              && colony.getPermissions().hasPermission(event.getPlayer(), Action.ACCESS_FREE_BLOCKS))
        {
            return;
        }
        checkEventCancelation(Action.RIGHTCLICK_ENTITY, event.getPlayer(), event.getWorld(), event, event.getPos());
    }

    /**
     * ItemTossEvent handler.
     * <p>
     * Check, if a player tossed a block. Deny if: - If the tossing happens in the colony - player is hostile to colony
     *
     * @param event ItemTossEvent
     */
    @SubscribeEvent
    public void on(final ItemTossEvent event)
    {
        if (checkEventCancelation(Action.TOSS_ITEM, event.getPlayer(), event.getPlayer().getEntityWorld(), event, new BlockPos(event.getPlayer().getPositionVec())))
        {
            event.getPlayer().inventory.addItemStackToInventory(event.getEntityItem().getItem());
        }
    }

    /**
     * ItemEntityPickupEvent handler.
     * <p>
     * Check, if a player tries to pickup a block. Deny if: - If the pickUp happens in the colony - player is neutral or hostile to colony
     *
     * @param event ItemEntityPickupEvent
     */
    @SubscribeEvent
    public void on(final EntityItemPickupEvent event)
    {
        checkEventCancelation(Action.PICKUP_ITEM, event.getPlayer(), event.getPlayer().getEntityWorld(), event, new BlockPos(event.getPlayer().getPositionVec()));
    }

    /**
     * FillBucketEvent handler.
     * <p>
     * Check, if a player tries to fill a bucket. Deny if: - If the fill happens in the colony - player is neutral or hostile to colony
     *
     * @param event ItemEntityPickupEvent
     */
    @SubscribeEvent
    public void on(final FillBucketEvent event)
    {
        @Nullable BlockPos targetBlockPos = null;
        if (event.getTarget() instanceof BlockRayTraceResult)
        {
            targetBlockPos = ((BlockRayTraceResult) event.getTarget()).getPos();
        }
        else if (event.getTarget() instanceof EntityRayTraceResult)
        {
            targetBlockPos = new BlockPos(((EntityRayTraceResult) event.getTarget()).getEntity().getPositionVec());
        }
        checkEventCancelation(Action.FILL_BUCKET, event.getPlayer(), event.getPlayer().getEntityWorld(), event, targetBlockPos);
    }

    /**
     * ArrowLooseEvent handler.
     * <p>
     * Check if a player tries to shoot an arrow. Deny if: - If the shooting happens in the colony - player is neutral or hostile to colony
     *
     * @param event ItemEntityPickupEvent
     */
    @SubscribeEvent
    public void on(final ArrowLooseEvent event)
    {
        checkEventCancelation(Action.SHOOT_ARROW, event.getPlayer(), event.getPlayer().getEntityWorld(), event, new BlockPos(event.getPlayer().getPositionVec()));
    }

    /**
     * LivingHurtEvent handler.
     * <p>
     * Check if the entity that is getting hurt is a player,
     * players that get hurt by other players are handled elsewhere,
     * this here is handling players getting hurt by citizens.
     * @param event
     */
    @SubscribeEvent
    public void on(final LivingHurtEvent event)
    {
        if (event.getEntity() instanceof ServerPlayerEntity
              && event.getSource() instanceof EntityDamageSource
              && event.getSource().getTrueSource() instanceof EntityCitizen
              && ((EntityCitizen) event.getSource().getTrueSource()).getCitizenColonyHandler().getColonyId() == colony.getID()
              && colony.getRaiderManager().isRaided()
              && !colony.getPermissions().hasPermission((PlayerEntity) event.getEntity(), Action.GUARDS_ATTACK))
        {
            event.setCanceled(true);
        }
    }

    /**
     * AttackEntityEvent handler.
     * <p>
     * Check, if a player tries to attack an entity.. Deny if: - If the attacking happens in the colony - Player is less than officer to the colony.
     *
     * @param event ItemEntityPickupEvent
     */
    @SubscribeEvent
    public void on(final AttackEntityEvent event)
    {
        if (event.getTarget() instanceof MobEntity)
        {
            return;
        }

        @NotNull final PlayerEntity player = EntityUtils.getPlayerOfFakePlayer(event.getPlayer(), event.getPlayer().getEntityWorld());

        if (MineColonies.getConfig().getServer().enableColonyProtection.get()
              && colony.isCoordInColony(player.getEntityWorld(), new BlockPos(player.getPositionVec())))
        {
            final Permissions perms = colony.getPermissions();
            if (event.getTarget() instanceof EntityCitizen)
            {
                final AbstractEntityCitizen citizen = (AbstractEntityCitizen) event.getTarget();
                if (citizen.getCitizenJobHandler().getColonyJob() instanceof AbstractJobGuard && perms.hasPermission(event.getPlayer(), Action.GUARDS_ATTACK))
                {
                    return;
                }

                if (perms.hasPermission(event.getPlayer(), Action.ATTACK_CITIZEN))
                {
                    return;
                }

                cancelEvent(event, event.getPlayer(), colony, Action.ATTACK_CITIZEN, new BlockPos(event.getTarget().getPositionVec()));
                return;
            }

            if (!(event.getTarget() instanceof MobEntity) && !perms.hasPermission(event.getPlayer(), Action.ATTACK_ENTITY))
            {
                cancelEvent(event, event.getPlayer(), colony, Action.ATTACK_ENTITY, new BlockPos(event.getTarget().getPositionVec()));
            }
        }
    }
}