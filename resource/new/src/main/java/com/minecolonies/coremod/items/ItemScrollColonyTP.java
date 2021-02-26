package com.minecolonies.coremod.items;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Teleport scroll to teleport you back to the set colony. Requires colony permissions
 */
public class ItemScrollColonyTP extends AbstractItemScroll
{
    /**
     * Sets the name, creative tab, and registers the item.
     *
     * @param properties the properties.
     */
    public ItemScrollColonyTP(final Properties properties)
    {
        super("scroll_tp", properties);
    }

    @Override
    protected ItemStack onItemUseSuccess(final ItemStack itemStack, final World world, final ServerPlayerEntity player)
    {
        if (world.rand.nextInt(10) == 0)
        {
            // Fail
            player.sendStatusMessage(new TranslationTextComponent("minecolonies.scroll.failed" + (world.rand.nextInt(FAIL_RESPONSES_TOTAL) + 1)).setStyle(Style.EMPTY.setFormatting(
              TextFormatting.GOLD)), true);

            BlockPos pos = null;
            for (final Direction dir : Direction.Plane.HORIZONTAL)
            {
                pos = BlockPosUtil.findAround(world, player.getPosition().offset(dir, 10), 5, 5, state -> state.getBlock() instanceof AirBlock);
                if (pos != null)
                {
                    break;
                }
            }

            if (pos != null)
            {
                player.addPotionEffect(new EffectInstance(Effects.NAUSEA, TICKS_SECOND * 7));
                player.teleport((ServerWorld) world, pos.getX(), pos.getY(), pos.getZ(), player.rotationYaw, player.rotationPitch);
            }

            SoundUtils.playSoundForPlayer(player, SoundEvents.ENTITY_BAT_TAKEOFF, 0.4f, 1.0f);
        }
        else
        {
            // Success
            doTeleport(player, getColony(itemStack), itemStack);
            SoundUtils.playSoundForPlayer(player, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 0.6f, 1.0f);
        }

        itemStack.shrink(1);
        return itemStack;
    }

    @Override
    protected boolean needsColony()
    {
        return true;
    }

    /**
     * Does the teleport action
     *
     * @param player user of the item
     * @param colony colony to teleport to
     */
    protected void doTeleport(final ServerPlayerEntity player, final IColony colony, final ItemStack stack)
    {
        TeleportHelper.colonyTeleport(player, colony);
    }

    @Override
    public void onUse(World worldIn, LivingEntity entity, ItemStack stack, int count)
    {
        if (!worldIn.isRemote && worldIn.getGameTime() % 5 == 0)
        {
            Network.getNetwork()
              .sendToTrackingEntity(new VanillaParticleMessage(entity.getPosX(), entity.getPosY(), entity.getPosZ(), ParticleTypes.INSTANT_EFFECT),
                entity);
            Network.getNetwork()
              .sendToPlayer(new VanillaParticleMessage(entity.getPosX(), entity.getPosY(), entity.getPosZ(), ParticleTypes.INSTANT_EFFECT),
                (ServerPlayerEntity) entity);
        }
    }

    @Override
    public void addInformation(
      @NotNull final ItemStack stack, @Nullable final World worldIn, @NotNull final List<ITextComponent> tooltip, @NotNull final ITooltipFlag flagIn)
    {
        final IFormattableTextComponent guiHint = LanguageHandler.buildChatComponent("item.minecolonies.scroll_tp.tip");
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
