package com.minecolonies.coremod.entity.mobs.norsemen;


/**
 * Class for the Archer norsemen entity.
 */
public class EntityNorsemenArcher extends AbstractEntityNorsemen implements IArcherNorsemenEntity
{

    /**
     * Constructor of the entity.
     *
     * @param worldIn world to construct it in.
     * @param type    the entity type.
     */
    public EntityNorsemenArcher(final EntityType<? extends EntityNorsemenArcher> type, final World worldIn)
    {
        super(type, worldIn);
    }
}
