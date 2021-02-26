package com.minecolonies.coremod.entity.mobs.pirates;


/**
 * Class for the Archer Pirate entity.
 */
public class EntityArcherPirate extends AbstractEntityPirate implements IArcherPirateEntity
{
    /**
     * Constructor of the entity.
     *
     * @param worldIn world to construct it in.
     * @param type    the entity type.
     */
    public EntityArcherPirate(final EntityType<? extends EntityArcherPirate> type, final World worldIn)
    {
        super(type, worldIn);
    }
}
