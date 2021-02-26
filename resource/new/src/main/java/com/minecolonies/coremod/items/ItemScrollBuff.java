package com.minecolonies.coremod.items;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Magic scroll which applies a regeneration buff to the user and all citizens around
 */
public class ItemScrollBuff extends AbstractItemScroll
{
    /**
     * Sets the name, creative tab, and registers the item.
     *
     * @param properties the properties.
     */
    public ItemScrollBuff(final Properties properties)
    {
        super("scroll_buff", properties);
    }

    @Override
    protected ItemStack onItemUseSuccess(final ItemStack itemStack, final World world, final ServerPlayerEntity player)
    {
        if (world.rand.nextInt(8) > 0)
        {
            for (final LivingEntity entity : world.getLoadedEntitiesWithinAABB(EntityCitizen.class, player.getBoundingBox().grow(15, 2, 15)))
            {
                addRegenerationWithParticles(entity);
            }

            addRegenerationWithParticles(player);
            // Send to player additionally, as players do not track themselves
            Network.getNetwork()
              .sendToPlayer(new VanillaParticleMessage(player.getPosX(), player.getPosY(), player.getPosZ(), ParticleTypes.HEART), player);
            SoundUtils.playSoundForPlayer(player, SoundEvents.ENTITY_PLAYER_LEVELUP, 0.2f, 1.0f);
        }
        else
        {
            player.sendStatusMessage(new TranslationTextComponent("minecolonies.scroll.failed" + (world.rand.nextInt(FAIL_RESPONSES_TOTAL) + 1)).setStyle(Style.EMPTY.setFormatting(
              TextFormatting.GOLD)), true);
            player.addPotionEffect(new EffectInstance(Effects.BLINDNESS, TICKS_SECOND * 10));
            SoundUtils.playSoundForPlayer(player, SoundEvents.ITEM_TOTEM_USE, 0.04f, 1.0f);
        }

        itemStack.shrink(1);
        return itemStack;
    }

    /**
     * Adds a regeneration potion instance and displays particles
     *
     * @param entity entity to apply to
     */
    private void addRegenerationWithParticles(final LivingEntity entity)
    {
        entity.addPotionEffect(new EffectInstance(Effects.REGENERATION, TICKS_SECOND * 60));
        Network.getNetwork()
          .sendToTrackingEntity(new VanillaParticleMessage(entity.getPosX(), entity.getPosY(), entity.getPosZ(), ParticleTypes.HEART),
            entity);
    }

    @Override
    public void addInformation(
      @NotNull final ItemStack stack, @Nullable final World worldIn, @NotNull final List<ITextComponent> tooltip, @NotNull final ITooltipFlag flagIn)
    {
        final IFormattableTextComponent guiHint = LanguageHandler.buildChatComponent("item.minecolonies.scroll_buff.tip");
        guiHint.setStyle(Style.EMPTY.setFormatting(TextFormatting.DARK_GREEN));
        tooltip.add(guiHint);
    }

    @Override
    protected boolean needsColony()
    {
        return false;
    }
}
