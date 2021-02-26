package com.minecolonies.coremod.entity.mobs.amazons;


/**
 * Class for the Archer amazon entity.
 */
public class EntityArcherAmazon extends AbstractEntityAmazon implements IArcherAmazon
{
    /**
     * Constructor of the entity.
     *
     * @param worldIn world to construct it in.
     * @param type    the entity type.
     */
    public EntityArcherAmazon(final EntityType<? extends EntityArcherAmazon> type, final World worldIn)
    {
        super(type, worldIn);
    }
}
