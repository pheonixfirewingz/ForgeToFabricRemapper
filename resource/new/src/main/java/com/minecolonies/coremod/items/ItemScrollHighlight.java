package com.minecolonies.coremod.items;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;


/**
 * Magic scroll which highlights and speedbuffs workers of the building it is applied to
 */
public class ItemScrollHighlight extends AbstractItemScroll
{
    /**
     * Sets the name, creative tab, and registers the item.
     *
     * @param properties the properties.
     */
    public ItemScrollHighlight(final Properties properties)
    {
        super("scroll_highlight", properties);
    }

    @Override
    @NotNull
    public ActionResultType onItemUse(ItemUseContext ctx)
    {
        // Right click on block
        if (ctx.getWorld().isRemote || ctx.getPlayer() == null || !ctx.getPlayer().isSneaking())
        {
            return ActionResultType.PASS;
        }

        final TileEntity te = ctx.getWorld().getTileEntity(ctx.getPos());
        if (te instanceof TileEntityColonyBuilding)
        {
            ctx.getItem().shrink(1);

            if (ctx.getWorld().rand.nextInt(10) == 0)
            {
                ctx.getPlayer()
                  .sendStatusMessage(new TranslationTextComponent(
                    "minecolonies.scroll.failed" + (ctx.getWorld().rand.nextInt(FAIL_RESPONSES_TOTAL) + 1)).setStyle(Style.EMPTY.setFormatting(
                    TextFormatting.GOLD)), true);
                ctx.getPlayer().addPotionEffect(new EffectInstance(Effects.GLOWING, TICKS_SECOND * 300));
                SoundUtils.playSoundForPlayer((ServerPlayerEntity) ctx.getPlayer(), SoundEvents.BLOCK_ENDER_CHEST_OPEN, 0.3f, 1.0f);
                return ActionResultType.SUCCESS;
            }

            final TileEntityColonyBuilding building = (TileEntityColonyBuilding) te;
            final List<ICitizenData> citizens = building.getColony().getBuildingManager().getBuilding(ctx.getPos()).getAssignedCitizen();

            for (final ICitizenData citizenData : citizens)
            {
                if (citizenData.getEntity().isPresent())
                {
                    citizenData.getEntity().get().addPotionEffect(new EffectInstance(Effects.GLOWING, TICKS_SECOND * 120));
                    citizenData.getEntity().get().addPotionEffect(new EffectInstance(Effects.SPEED, TICKS_SECOND * 120));
                }
            }

            SoundUtils.playSoundForPlayer((ServerPlayerEntity) ctx.getPlayer(), SoundEvents.ENTITY_PLAYER_LEVELUP, 0.3f, 1.0f);
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    protected boolean needsColony()
    {
        return false;
    }

    @Override
    protected ItemStack onItemUseSuccess(final ItemStack itemStack, final World world, final ServerPlayerEntity player)
    {
        return itemStack;
    }

    @Override
    public void addInformation(
      @NotNull final ItemStack stack, @Nullable final World worldIn, @NotNull final List<ITextComponent> tooltip, @NotNull final ITooltipFlag flagIn)
    {
        final IFormattableTextComponent guiHint = LanguageHandler.buildChatComponent("item.minecolonies.scroll_highlight.tip");
        guiHint.setStyle(Style.EMPTY.setFormatting(TextFormatting.DARK_GREEN));
        tooltip.add(guiHint);
    }
}