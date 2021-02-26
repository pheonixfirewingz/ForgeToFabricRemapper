package com.minecolonies.api.colony.requestsystem.data;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.token.IToken;

import java.util.*;

public interface IRequestSystemBuildingDataStore extends IDataStore
{
    Map<TypeToken<?>, Collection<IToken<?>>> getOpenRequestsByRequestableType();

    Map<Integer, Collection<IToken<?>>> getOpenRequestsByCitizen();

    Map<Integer, Collection<IToken<?>>> getCompletedRequestsByCitizen();

    Map<IToken<?>, Integer> getCitizensByRequest();
}
