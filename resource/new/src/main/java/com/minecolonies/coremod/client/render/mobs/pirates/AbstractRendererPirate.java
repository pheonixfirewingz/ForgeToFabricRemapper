package com.minecolonies.coremod.client.render.mobs.pirates;


/**
 * Abstract for rendering Pirates.
 */
public abstract class AbstractRendererPirate<T extends AbstractEntityPirate, M extends BipedModel<T>> extends BipedRenderer<T, M>
{
    public AbstractRendererPirate(final EntityRendererManager renderManagerIn, final M modelBipedIn, final float shadowSize)
    {
        super(renderManagerIn, modelBipedIn, shadowSize);
        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5F), new BipedModel<>(1.0F)));
    }
}
