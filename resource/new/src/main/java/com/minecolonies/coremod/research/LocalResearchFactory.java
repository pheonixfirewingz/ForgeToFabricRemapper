package com.minecolonies.coremod.research;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;


/**
 * Factory implementation taking care of creating new instances, serializing and deserializing LocalResearch.
 */
public class LocalResearchFactory implements ILocalResearchFactory
{
    @NotNull
    @Override
    public TypeToken<LocalResearch> getFactoryOutputType()
    {
        return TypeToken.of(LocalResearch.class);
    }

    @NotNull
    @Override
    public TypeToken<FactoryVoidInput> getFactoryInputType()
    {
        return TypeConstants.FACTORYVOIDINPUT;
    }

    @NotNull
    @Override
    public ILocalResearch getNewInstance(final String id, final String branch, final int depth)
    {
        return new LocalResearch(id, branch, depth);
    }

    @NotNull
    @Override
    public CompoundNBT serialize(@NotNull final IFactoryController controller, @NotNull final ILocalResearch research)
    {
        final CompoundNBT compound = new CompoundNBT();
        compound.putInt(TAG_STATE, research.getState().ordinal());
        compound.putString(TAG_ID, research.getId());
        compound.putString(TAG_BRANCH, research.getBranch());
        compound.putInt(TAG_PROGRESS, research.getProgress());
        compound.putInt(TAG_DEPTH, research.getDepth());

        return compound;
    }

    @NotNull
    @Override
    public ILocalResearch deserialize(@NotNull final IFactoryController controller, @NotNull final CompoundNBT nbt)
    {
        final int state = nbt.getInt(TAG_STATE);
        final String id = nbt.getString(TAG_ID);
        final String branch = nbt.getString(TAG_BRANCH);
        final int depth = nbt.getInt(TAG_DEPTH);
        final int progress = nbt.getInt(TAG_PROGRESS);

        final ILocalResearch research = getNewInstance(id, branch, depth);
        research.setState(ResearchState.values()[state]);
        research.setProgress(progress);
        return research;
    }

    @Override
    public void serialize(IFactoryController controller, ILocalResearch input, PacketBuffer packetBuffer)
    {
        packetBuffer.writeInt(input.getState().ordinal());
        packetBuffer.writeString(input.getId());
        packetBuffer.writeString(input.getBranch());
        packetBuffer.writeInt(input.getProgress());
        packetBuffer.writeInt(input.getDepth());
    }

    @Override
    public ILocalResearch deserialize(IFactoryController controller, PacketBuffer buffer) throws Throwable
    {
        final int state = buffer.readInt();
        final String id = buffer.readString(32767);
        final String branch = buffer.readString(32767);
        final int progress = buffer.readInt();
        final int depth = buffer.readInt();

        final ILocalResearch research = getNewInstance(id, branch, depth);
        research.setState(ResearchState.values()[state]);
        research.setProgress(progress);
        return research;
    }

    @Override
    public short getSerializationId()
    {
        return 29;
    }
}
