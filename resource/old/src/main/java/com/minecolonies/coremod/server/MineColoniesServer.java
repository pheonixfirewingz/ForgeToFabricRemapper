package com.minecolonies.coremod.server;

import com.minecolonies.api.MinecoloniesAPIProxy;
import com.minecolonies.api.colony.*;
import com.minecolonies.apiimp.CommonMinecoloniesAPIImpl;
import com.minecolonies.coremod.Minecolonies;
import com.minecolonies.coremod.proxy.IProxy;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.recipe.book.*;
import net.minecraft.server.network.*;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.io.File;

public class MineColoniesServer implements DedicatedServerModInitializer , IProxy
{
	protected static CommonMinecoloniesAPIImpl apiImpl;

	@Override public void onInitializeServer()
	{
		apiImpl = new CommonMinecoloniesAPIImpl();
	}

	@Override public void setupApi()
	{
		MinecoloniesAPIProxy.getInstance().setApiInstance(apiImpl);
	}

	@Override
	public boolean isClient()
	{
		return false;
	}

	@Override
	public void showCitizenWindow(final ICitizenDataView citizen)
	{
		/*
		 * Intentionally left empty.
		 */
	}

	@Override
	public void openBuildToolWindow(final BlockPos pos)
	{
		/*
		 * Intentionally left empty.
		 */
	}

	@Override
	public void openSuggestionWindow(@NotNull BlockPos pos, @NotNull BlockState state, @NotNull final ItemStack stack)
	{
		/*
		 * Intentionally left empty.
		 */
	}

	@Override
	public void openBuildToolWindow(final BlockPos pos, final String structureName, final int rotation)
	{
		/*
		 * Intentionally left empty.
		 */
	}

	@Override
	public void openBannerRallyGuardsWindow(final ItemStack banner)
	{
		/*
		 * Intentionally left empty.
		 */
	}

	@Override
	public void openClipboardWindow(final IColonyView colonyView)
	{
		/*
		 * Intentionally left empty.
		 */
	}

	@Override
	public void openResourceScrollWindow(final int colonyId, final BlockPos pos)
	{
		/*
		 * Intentionally left empty.
		 */
	}

	@NotNull
	@Override
	public RecipeBook getRecipeBookFromPlayer(@NotNull final PlayerEntity player)
	{
		return ((ServerPlayerEntity) player).getRecipeBook();
	}

	@Override
	public void openDecorationControllerWindow(@NotNull final BlockPos pos)
	{
		/*
		 * Intentionally left empty.
		 */
	}

	@Override
	public File getSchematicsFolder()
	{
		return new File(ServerLifecycleHooks.getCurrentServer().getDataDirectory()
				+ "/" + Minecolonies.MOD_ID);
	}

	@Override
	public World getWorld(final RegistryKey<World> dimension)
	{
		return ServerLifecycleHooks.getCurrentServer().getWorld(dimension);
	}
}
