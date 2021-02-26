package com.minecolonies.coremod.items;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Class handling the Scepter for the Pharao.
 */
public class ItemFireArrow extends ArrowItem
{
    /**
     * Constructor method for the Chief Sword Item
     *
     * @param properties the properties.
     */
    public ItemFireArrow()
    {
        super(new MineColoniesItemSettings());
    }

    @NotNull
    @Override
    public PersistentProjectileEntity createArrow(@NotNull final World worldIn, @NotNull final ItemStack stack, final LivingEntity shooter)
    {
        //fixme: need entity code
        return null;
    }
}
