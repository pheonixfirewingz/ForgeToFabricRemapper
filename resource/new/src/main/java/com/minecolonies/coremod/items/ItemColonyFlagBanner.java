package com.minecolonies.coremod.items;


import javax.annotation.Nullable;
import java.util.List;


/**
 * This item represents the colony flag banner, both wall and floor blocks.
 * Allows duplication of other banner pattern lists to its own default
 */
public class ItemColonyFlagBanner extends BannerItem
{
    public ItemColonyFlagBanner(String name, ItemGroup tab, Properties properties)
    {
        this(ModBlocks.blockColonyBanner, ModBlocks.blockColonyWallBanner, properties.maxStackSize(16).group(tab));
        setRegistryName(Constants.MOD_ID, name);
    }

    public ItemColonyFlagBanner(Block standingBanner, Block wallBanner, Properties builder)
    {
        super(standingBanner, wallBanner, builder);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        // Duplicate the patterns of the banner that was clicked on
        TileEntity te = context.getWorld().getTileEntity(context.getPos());
        BlockState state = context.getWorld().getBlockState(context.getPos());
        ItemStack stack = context.getPlayer().getHeldItemMainhand();

        if (te instanceof BannerTileEntity || te instanceof TileEntityColonyFlag) {
            CompoundNBT source =
                (te instanceof BannerTileEntity
                    ? ((BannerTileEntity) te).getItem(state)
                    : ((TileEntityColonyFlag) te).getItem())
                .getTag()
                .getCompound("BlockEntityTag");
            ListNBT patternList = source.getList(TAG_BANNER_PATTERNS, 10);

            // Set the base pattern, if there wasn't one set.
            // This saves us attempting to alter the item itself to change the base color.
            if (!patternList.getCompound(0).getString(TAG_SINGLE_PATTERN).equals(BannerPattern.BASE.getHashname()))
            {
                CompoundNBT nbt = new CompoundNBT();
                nbt.putString(TAG_SINGLE_PATTERN, BannerPattern.BASE.getHashname());
                nbt.putInt(TAG_PATTERN_COLOR, ((AbstractBannerBlock) state.getBlock()).getColor().getId());
                patternList.add(0, nbt);
            }

            CompoundNBT tag = stack.getOrCreateChildTag("BlockEntityTag");
            tag.put(TAG_BANNER_PATTERNS, patternList);

            return ActionResultType.SUCCESS;
        }
        return super.onItemUse(context);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        // Remove the base, as they have no translations (Mojang were lazy. Or maybe saving space?)
        if (tooltip.size() > 1) tooltip.remove(1);
    }
}
