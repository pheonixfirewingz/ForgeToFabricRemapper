package com.minecraftabnormals.endergetic.core.registry;

import com.minecraftabnormals.abnormals_core.common.items.AbnormalsMusicDiscItem;
import com.minecraftabnormals.abnormals_core.common.items.AbnormalsSpawnEggItem;
import com.minecraftabnormals.abnormals_core.core.util.registry.ItemSubRegistryHelper;
import com.minecraftabnormals.endergetic.common.entities.bolloom.BalloonColor;
import com.minecraftabnormals.endergetic.common.items.*;
import com.minecraftabnormals.endergetic.core.EndergeticExpansion;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EndergeticExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EEItems {
	private static final ItemSubRegistryHelper HELPER = EndergeticExpansion.REGISTRY_HELPER.getItemSubHelper();

	public static final RegistryObject<Item> EUMUS_BRICK = HELPER.createItem("eumus_brick", () -> new Item(ItemSubRegistryHelper.createSimpleItemProperty(64, ItemGroup.MATERIALS)));

	public static final RegistryObject<Item> POISE_BOAT = HELPER.createBoatItem("poise", EEBlocks.POISE_PLANKS);
	public static final RegistryObject<Item> BOLLOOM_FRUIT = HELPER.createItem("bolloom_fruit", () -> new BolloomFruitItem(new Item.Properties().group(ItemGroup.FOOD).food(EEFoods.BOLLOOM_FRUIT)));
	public static final RegistryObject<Item> BOOFLO_HIDE = HELPER.createItem("booflo_hide", () -> new Item(ItemSubRegistryHelper.createSimpleItemProperty(64, ItemGroup.MATERIALS)));
	public static final RegistryObject<Item> BOOFLO_VEST = HELPER.createItem("booflo_vest", () -> new BoofloVestItem(new Item.Properties().group(ItemGroup.COMBAT)));
	public static final RegistryObject<Item> PUFFBUG_BOTTLE = HELPER.createItem("puffbug_bottle", () -> new PuffBugBottleItem(ItemSubRegistryHelper.createSimpleItemProperty(1, ItemGroup.MISC)));
	public static final RegistryObject<Item> MUSIC_DISC_KILOBYTE = HELPER.createItem("music_disc_kilobyte", () -> new AbnormalsMusicDiscItem(16, EESounds.KILOBYTE, new Item.Properties().maxStackSize(1).group(ItemGroup.MISC).rarity(Rarity.RARE)));

	public static final RegistryObject<Item> BOLLOOM_BALLOON = HELPER.createItem("bolloom_balloon", () -> new BolloomBalloonItem(ItemSubRegistryHelper.createSimpleItemProperty(16, ItemGroup.TOOLS), BalloonColor.DEFAULT));
	public static final RegistryObject<Item> BOLLOOM_BALLOON_RED = HELPER.createItem("red_bolloom_balloon", () -> new BolloomBalloonItem(ItemSubRegistryHelper.createSimpleItemProperty(16, ItemGroup.TOOLS), BalloonColor.RED));
	public static final RegistryObject<Item> BOLLOOM_BALLOON_ORANGE = HELPER.createItem("orange_bolloom_balloon", () -> new BolloomBalloonItem(ItemSubRegistryHelper.createSimpleItemProperty(16, ItemGroup.TOOLS), BalloonColor.ORANGE));
	public static final RegistryObject<Item> BOLLOOM_BALLOON_YELLOW = HELPER.createItem("yellow_bolloom_balloon", () -> new BolloomBalloonItem(ItemSubRegistryHelper.createSimpleItemProperty(16, ItemGroup.TOOLS), BalloonColor.YELLOW));
	public static final RegistryObject<Item> BOLLOOM_BALLOON_LIME = HELPER.createItem("lime_bolloom_balloon", () -> new BolloomBalloonItem(ItemSubRegistryHelper.createSimpleItemProperty(16, ItemGroup.TOOLS), BalloonColor.LIME));
	public static final RegistryObject<Item> BOLLOOM_BALLOON_GREEN = HELPER.createItem("green_bolloom_balloon", () -> new BolloomBalloonItem(ItemSubRegistryHelper.createSimpleItemProperty(16, ItemGroup.TOOLS), BalloonColor.GREEN));
	public static final RegistryObject<Item> BOLLOOM_BALLOON_LIGHT_BLUE = HELPER.createItem("light_blue_bolloom_balloon", () -> new BolloomBalloonItem(ItemSubRegistryHelper.createSimpleItemProperty(16, ItemGroup.TOOLS), BalloonColor.LIGHT_BLUE));
	public static final RegistryObject<Item> BOLLOOM_BALLOON_CYAN = HELPER.createItem("cyan_bolloom_balloon", () -> new BolloomBalloonItem(ItemSubRegistryHelper.createSimpleItemProperty(16, ItemGroup.TOOLS), BalloonColor.CYAN));
	public static final RegistryObject<Item> BOLLOOM_BALLOON_BLUE = HELPER.createItem("blue_bolloom_balloon", () -> new BolloomBalloonItem(ItemSubRegistryHelper.createSimpleItemProperty(16, ItemGroup.TOOLS), BalloonColor.BLUE));
	public static final RegistryObject<Item> BOLLOOM_BALLOON_PINK = HELPER.createItem("pink_bolloom_balloon", () -> new BolloomBalloonItem(ItemSubRegistryHelper.createSimpleItemProperty(16, ItemGroup.TOOLS), BalloonColor.PINK));
	public static final RegistryObject<Item> BOLLOOM_BALLOON_MAGENTA = HELPER.createItem("magenta_bolloom_balloon", () -> new BolloomBalloonItem(ItemSubRegistryHelper.createSimpleItemProperty(16, ItemGroup.TOOLS), BalloonColor.MAGENTA));
	public static final RegistryObject<Item> BOLLOOM_BALLOON_PURPLE = HELPER.createItem("purple_bolloom_balloon", () -> new BolloomBalloonItem(ItemSubRegistryHelper.createSimpleItemProperty(16, ItemGroup.TOOLS), BalloonColor.PURPLE));
	public static final RegistryObject<Item> BOLLOOM_BALLOON_BROWN = HELPER.createItem("brown_bolloom_balloon", () -> new BolloomBalloonItem(ItemSubRegistryHelper.createSimpleItemProperty(16, ItemGroup.TOOLS), BalloonColor.BROWN));
	public static final RegistryObject<Item> BOLLOOM_BALLOON_GRAY = HELPER.createItem("gray_bolloom_balloon", () -> new BolloomBalloonItem(ItemSubRegistryHelper.createSimpleItemProperty(16, ItemGroup.TOOLS), BalloonColor.GRAY));
	public static final RegistryObject<Item> BOLLOOM_BALLOON_LIGHT_GRAY = HELPER.createItem("light_gray_bolloom_balloon", () -> new BolloomBalloonItem(ItemSubRegistryHelper.createSimpleItemProperty(16, ItemGroup.TOOLS), BalloonColor.LIGHT_GRAY));
	public static final RegistryObject<Item> BOLLOOM_BALLOON_WHITE = HELPER.createItem("white_bolloom_balloon", () -> new BolloomBalloonItem(ItemSubRegistryHelper.createSimpleItemProperty(16, ItemGroup.TOOLS), BalloonColor.WHITE));
	public static final RegistryObject<Item> BOLLOOM_BALLOON_BLACK = HELPER.createItem("black_bolloom_balloon", () -> new BolloomBalloonItem(ItemSubRegistryHelper.createSimpleItemProperty(16, ItemGroup.TOOLS), BalloonColor.BLACK));

	public static final RegistryObject<AbnormalsSpawnEggItem> PUFF_BUG_SPAWN_EGG = HELPER.createSpawnEggItem("puff_bug", EEEntities.PUFF_BUG::get, 15660724, 16610303);
	public static final RegistryObject<AbnormalsSpawnEggItem> BOOFLO_SPAWN_EGG = HELPER.createSpawnEggItem("booflo", EEEntities.BOOFLO::get, 8143741, 16641190);
}