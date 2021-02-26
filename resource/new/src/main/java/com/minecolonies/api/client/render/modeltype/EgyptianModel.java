package com.minecolonies.api.client.render.modeltype;


/**
 * Egyptian model.
 */
public class EgyptianModel<T extends AbstractEntityEgyptian> extends BipedModel<AbstractEntityEgyptian>
{
	/**
	 * Create a model of a specific size.
	 *
	 * @param size the size.
	 */
	public EgyptianModel(final float size)
	{
		super(size);
	}

	/**
	 * Create a model of the default size.
	 */
	public EgyptianModel()
	{
		this(1.0F);
	}
}
