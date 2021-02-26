package com.minecolonies.coremod.entity.mobs.norsemen;


/**
 * Class for the Norsemen Shieldmaiden entity.
 */
public class EntityShieldmaiden extends AbstractEntityNorsemen implements IMeleeNorsemenEntity
{

    /**
     * Constructor of the entity.
     *
     * @param worldIn world to construct it in.
     * @param type    the entity type.
     */
    public EntityShieldmaiden(final EntityType<? extends EntityShieldmaiden> type, final World worldIn)
    {
        super(type, worldIn);
    }
}
