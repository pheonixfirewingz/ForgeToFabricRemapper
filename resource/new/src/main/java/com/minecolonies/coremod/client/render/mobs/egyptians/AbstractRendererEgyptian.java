package com.minecolonies.coremod.client.render.mobs.egyptians;


/**
 * Abstract for rendering Pirates.
 */
public abstract class AbstractRendererEgyptian<T extends AbstractEntityEgyptian, M extends BipedModel<T>> extends BipedRenderer<T, M>
{
    public AbstractRendererEgyptian(final EntityRendererManager renderManagerIn, final M modelBipedIn, final float shadowSize)
    {
        super(renderManagerIn, modelBipedIn, shadowSize);
        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5F), new BipedModel<>(1.0F)));
    }
}
