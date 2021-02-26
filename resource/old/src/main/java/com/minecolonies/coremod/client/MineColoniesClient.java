package com.minecolonies.coremod.client;

import com.minecolonies.api.MinecoloniesAPIProxy;
import com.minecolonies.api.colony.*;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.apiimp.*;
import com.minecolonies.coremod.client.gui.*;
import com.minecolonies.coremod.colony.colony.*;
import com.minecolonies.coremod.proxy.IProxy;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.block.*;
import net.minecraft.client.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.io.File;

public class MineColoniesClient implements ClientModInitializer, IProxy
{
	protected static CommonMinecoloniesAPIImpl apiImpl;


	@Override public void onInitializeClient()
	{
		apiImpl = new ClientMinecoloniesAPIImpl();
	}

	@Override public void setupApi()
	{
		MinecoloniesAPIProxy.getInstance().setApiInstance(apiImpl);
	}

	@Override
	public boolean isClient()
	{
		return true;
	}

	@Override
	public void showCitizenWindow(final CitizenDataView citizen)
	{
		@NotNull final WindowInteraction window = new WindowInteraction(citizen);
		window.open();
	}

	@Override
	public void openBuildToolWindow(@Nullable final BlockPos pos)
	{
		if (pos == null && Settings.instance.getActiveStructure() == null)
		{
			return;
		}

		@Nullable final WindowMinecoloniesBuildTool window = new WindowMinecoloniesBuildTool(pos);
		window.open();
	}

	@Override
	public void openDecorationControllerWindow(@Nullable final BlockPos pos)
	{
		if (pos == null)
		{
			return;
		}

		@Nullable final WindowDecorationController window = new WindowDecorationController(pos);
		window.open();
	}

	@Override public World getWorld(RegistryKey<World> dimension)
	{
		return  Minecraft.getInstance().world;
	}

	@Override
	public World getWorld(final RegistryKey<World> dimension)
	{
		return Minecraft.getInstance().world;
	}

	@Override
	public void openSuggestionWindow(@NotNull final BlockPos pos, @NotNull final BlockState state, @NotNull final ItemStack stack)
	{
		new WindowSuggestBuildTool(pos, state, stack).open();
	}

	@Override
	public void openBuildToolWindow(final BlockPos pos, final String structureName, final int rotation)
	{
		if (pos == null && Settings.instance.getActiveStructure() == null)
		{
			return;
		}

		@Nullable final WindowMinecoloniesBuildTool window = new WindowMinecoloniesBuildTool(pos, structureName, rotation);
		window.open();
	}

	@Override
	public void openBannerRallyGuardsWindow(final ItemStack banner)
	{
		@Nullable final WindowBannerRallyGuards window = new WindowBannerRallyGuards(banner);
		window.open();
	}

	@Override
	public void openClipboardWindow(final IColonyView colonyView)
	{
		@Nullable final WindowClipBoard window = new WindowClipBoard(colonyView);
		window.open();
	}

	@Override
	public void openResourceScrollWindow(final int colonyId, final BlockPos buildingPos)
	{
		@Nullable final WindowResourceList window = new WindowResourceList(colonyId, buildingPos);
		window.open();
	}

	@Override
	public File getSchematicsFolder()
	{
		if (ServerLifecycleHooks.getCurrentServer() == null)
		{
			if (ColonyManager.getInstance().getServerUUID() != null)
			{
				return new File(MinecraftClient.getInstance().gameDir, Constants.MOD_ID + "/" + IColonyManager.getInstance().getServerUUID());
			}
			else
			{
				Log.getLogger().error("ColonyManager.getServerUUID() => null this should not happen", new Exception());
				return null;
			}
		}

		// if the world schematics folder exists we use it
		// otherwise we use the minecraft folder  /minecolonies/schematics if on the physical client on the logical server
		final File worldSchematicFolder = new File(ServerLifecycleHooks.getCurrentServer().getDataDirectory()
				+ "/" + Constants.MOD_ID + '/' + Structures.SCHEMATICS_PREFIX);

		if (!worldSchematicFolder.exists())
		{
			return new File(Minecraft.getInstance().gameDir, Constants.MOD_ID);
		}

		return worldSchematicFolder.getParentFile();
	}

	@NotNull
	@Override
	public RecipeBook getRecipeBookFromPlayer(@NotNull final PlayerEntity player)
	{
		if (player instanceof ClientPlayerEntity)
		{
			return ((ClientPlayerEntity) player).getRecipeBook();
		}

		return super.getRecipeBookFromPlayer(player);
	}
}
