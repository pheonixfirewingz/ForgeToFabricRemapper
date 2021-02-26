package com.minecolonies.coremod.client.render;


/**
 * Empty renderer for entities which should not actually be rendered.
 *
 * @param <T> the entity that shall sit.
 */
public class RenderSitting<T extends Entity> extends EntityRenderer<T>
{
    public RenderSitting(final EntityRendererManager p_i46179_1_)
    {
        super(p_i46179_1_);
    }

    @Override
    public ResourceLocation getEntityTexture(final T t)
    {
        return null;
    }

    @Override
    public boolean shouldRender(T entity, ClippingHelper clippingHelper, double x, double y, double z)
    {
        return false;
    }
}
