package com.minecolonies.coremod.entity.mobs.pirates;


/**
 * Class for the Pirate entity.
 */
public class EntityPirate extends AbstractEntityPirate implements IMeleePirateEntity
{

    /**
     * Constructor of the entity.
     *
     * @param type    the entity type.
     * @param worldIn world to construct it in.
     */
    public EntityPirate(final EntityType<? extends EntityPirate> type, final World worldIn)
    {
        super(type, worldIn);
        this.moveController = new MovementHandler(this);
    }
}
