package com.minecolonies.coremod.items;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Magic scroll which summons guards to the users aid, with a limited duration. Only works within the same world as the colony.
 */
public class ItemScrollGuardHelp extends AbstractItemScroll
{
    /**
     * Sets the name, creative tab, and registers the item.
     *
     * @param properties the properties.
     */
    public ItemScrollGuardHelp(final Properties properties)
    {
        super("scroll_guard_help", properties);
    }

    @Override
    protected ItemStack onItemUseSuccess(
      final ItemStack itemStack, final World world, final ServerPlayerEntity player)
    {
        final IColony colony = getColony(itemStack);
        final BlockPos buildingPos = BlockPosUtil.read(itemStack.getTag(), TAG_BUILDING_POS);
        final IBuilding building = colony.getBuildingManager().getBuilding(buildingPos);
        if (!(building instanceof AbstractBuildingGuards))
        {
            LanguageHandler.sendPlayerMessage(player, "minecolonies.scroll.noguardbuilding");
            return itemStack;
        }

        itemStack.shrink(1);
        final List<ICitizenData> guards = building.getAssignedCitizen();

        if (world.rand.nextInt(10) == 0 || colony.getWorld() != world)
        {
            // Fail
            final LlamaEntity entity = EntityType.LLAMA.create(world);
            entity.setPosition(player.getPosX(), player.getPosY(), player.getPosZ());
            world.addEntity(entity);

            player.sendStatusMessage(new TranslationTextComponent("minecolonies.scroll.failed" + (world.rand.nextInt(FAIL_RESPONSES_TOTAL) + 1)).setStyle(Style.EMPTY.setFormatting(
              TextFormatting.GOLD)), true);

            SoundUtils.playSoundForPlayer(player, SoundEvents.ENTITY_EVOKER_CAST_SPELL, 0.5f, 1.0f);
            return itemStack;
        }
        else
        {
            for (final ICitizenData citizenData : guards)
            {
                final AbstractJobGuard job = citizenData.getJob(AbstractJobGuard.class);
                if (job != null && job.getWorkerAI() != null && !((AbstractEntityAIGuard) job.getWorkerAI()).hasTool())
                {
                    continue;
                }

                if (citizenData.getEntity().isPresent())
                {
                    if (citizenData.getEntity().get().getCitizenDiseaseHandler().isSick())
                    {
                        continue;
                    }

                    citizenData.getEntity().get().remove();
                }

                colony.getCitizenManager().spawnOrCreateCivilian(citizenData, world, player.getPosition(), true);
                citizenData.setNextRespawnPosition(buildingPos);

                ((AbstractBuildingGuards) building).setTask(GuardTask.FOLLOW);
                ((AbstractBuildingGuards) building).setPlayerToFollow(player);
                ((AbstractBuildingGuards) building).setTightGrouping(true);

                citizenData.setSaturation(100);

                colony.getPackageManager().addCloseSubscriber(player);

                if (job != null && job.getWorkerAI() != null)
                {
                    final long spawnTime = world.getGameTime() + TICKS_SECOND * 900;

                    // Timed despawn
                    job.getWorkerAI().registerTarget(new AIOneTimeEventTarget(() ->
                    {
                        if (world.getGameTime() - spawnTime > 0)
                        {
                            ((AbstractBuildingGuards) building).setTask(GuardTask.PATROL);
                            citizenData.getEntity().ifPresent(Entity::remove);
                            colony.getPackageManager().removeCloseSubscriber(player);
                            return true;
                        }
                        return false;
                    }
                      , AIWorkerState.DECIDE));
                }
            }

            SoundUtils.playSoundForPlayer(player, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.3f, 1.0f);
        }


        return itemStack;
    }

    @Override
    protected boolean needsColony()
    {
        return true;
    }

    @Override
    @NotNull
    public ActionResultType onItemUse(ItemUseContext ctx)
    {
        final ActionResultType result = super.onItemUse(ctx);

        if (ctx.getWorld().isRemote)
        {
            return result;
        }

        final TileEntity te = ctx.getWorld().getTileEntity(ctx.getPos());
        if (te instanceof TileEntityColonyBuilding && ctx.getPlayer() != null)
        {
            final IBuilding building = ((TileEntityColonyBuilding) te).getColony().getBuildingManager().getBuilding(ctx.getPos());
            if (!(building instanceof AbstractBuildingGuards))
            {
                LanguageHandler.sendPlayerMessage(ctx.getPlayer(), "minecolonies.scroll.noguardbuilding");
            }
        }

        return result;
    }

    @Override
    public void onUse(World worldIn, LivingEntity entity, ItemStack stack, int count)
    {
        if (!worldIn.isRemote && worldIn.getGameTime() % 5 == 0)
        {
            Network.getNetwork()
              .sendToTrackingEntity(new VanillaParticleMessage(entity.getPosX(), entity.getPosY(), entity.getPosZ(), ParticleTypes.ENCHANT),
                entity);
            Network.getNetwork()
              .sendToPlayer(new VanillaParticleMessage(entity.getPosX(), entity.getPosY(), entity.getPosZ(), ParticleTypes.ENCHANT),
                (ServerPlayerEntity) entity);
        }
    }

    @Override
    public void addInformation(
      @NotNull final ItemStack stack, @Nullable final World worldIn, @NotNull final List<ITextComponent> tooltip, @NotNull final ITooltipFlag flagIn)
    {
        final IFormattableTextComponent guiHint = LanguageHandler.buildChatComponent("item.minecolonies.scroll_guard_help.tip");
        guiHint.setStyle(Style.EMPTY.setFormatting(TextFormatting.DARK_GREEN));
        tooltip.add(guiHint);

        String colonyDesc = new TranslationTextComponent("item.minecolonies.scroll.colony.none").getString();

        final IColony colony = getColonyView(stack);
        if (colony != null)
        {
            colonyDesc = colony.getName();
        }

        final IFormattableTextComponent guiHint2 = new TranslationTextComponent("item.minecolonies.scroll.colony.tip", colonyDesc);
        guiHint2.setStyle(Style.EMPTY.setFormatting(TextFormatting.GOLD));
        tooltip.add(guiHint2);
    }
}