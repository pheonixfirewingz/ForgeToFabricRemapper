package com.minecolonies.coremod.client.render.mobs.egyptians;


/**
 * Renderer used for archer mummy.
 */
public class RendererArcherMummy extends AbstractRendererEgyptian<AbstractEntityEgyptian, ModelArcherMummy>
{
    /**
     * Texture of the entity.
     */
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecolonies:textures/entity/raiders/archer_mummy.png");

    /**
     * Constructor method for renderer
     *
     * @param renderManagerIn the renderManager
     */
    public RendererArcherMummy(final EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new ModelArcherMummy(), 0.5F);
    }

    @Override
    public ResourceLocation getEntityTexture(final AbstractEntityEgyptian entity)
    {
        return TEXTURE;
    }
}
