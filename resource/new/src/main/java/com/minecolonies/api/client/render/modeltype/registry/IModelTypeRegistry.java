package com.minecolonies.api.client.render.modeltype.registry;


import java.util.Map;

public interface IModelTypeRegistry
{

	static IModelTypeRegistry getInstance()
	{
		return IMinecoloniesAPI.getInstance().getModelTypeRegistry();
	}

	IModelTypeRegistry register(IModelType type, CitizenModel<AbstractEntityCitizen> maleModel, CitizenModel<AbstractEntityCitizen> femaleModel);

	Map<IModelType, CitizenModel<AbstractEntityCitizen>> getMaleMap();

	Map<IModelType, CitizenModel<AbstractEntityCitizen>> getFemaleMap();
}
