package com.minecolonies.coremod.util;

{

    private T value;

    public ModifyableLambdaWrapper(final T value)
    {
        this.value = value;
    }

    public T getValue()
    {
        return value;
    }

    public void setValue(final T value)
    {
        this.value = value;
    }
}
