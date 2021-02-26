package com.minecolonies.api.entity.mobs.barbarians;

import net.minecraft.command.*;
import net.minecraft.entity.monster.*;
import net.minecraft.nbt.*;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface IBarbarianEntity extends IMob, ICommandSource, ICapabilitySerializable<CompoundNBT>
{
}
