package com.minecolonies.coremod.items;



/**
 * Class handling the Scimitar item.
 */
public class ItemIronScimitar extends SwordItem
{
    /**
     * Constructor method for the Scimitar Item
     *
     * @param properties the properties.
     */
    public ItemIronScimitar(final Item.Properties properties)
    {
        super(ItemTier.IRON, 3, -2.4f, properties.group(ModCreativeTabs.MINECOLONIES));
        setRegistryName(Constants.MOD_ID.toLowerCase() + ":" + SCIMITAR_NAME);
    }
}
