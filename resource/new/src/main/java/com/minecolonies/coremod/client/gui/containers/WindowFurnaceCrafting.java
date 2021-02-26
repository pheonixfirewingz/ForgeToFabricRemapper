package com.minecolonies.coremod.client.gui.containers;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Furnace crafting gui.
 */
public class WindowFurnaceCrafting extends ContainerScreen<ContainerCraftingFurnace>
{
    private static final ResourceLocation CRAFTING_FURNACE = new ResourceLocation(Constants.MOD_ID, "textures/gui/furnace.png");

    /**
     * X offset of the button.
     */
    private static final int BUTTON_X_OFFSET = 1;

    /**
     * Y offset of the button.
     */
    private static final int BUTTON_Y_POS = 170;

    /**
     * Button width.
     */
    private static final int BUTTON_WIDTH = 150;

    /**
     * Button height.
     */
    private static final int BUTTON_HEIGHT = 20;

    /**
     * The building the window belongs to.
     */
    private final ContainerCraftingFurnace container;

    /**
     * The building assigned to this.
     */
    private final AbstractBuildingSmelterCrafter.View building;

    /**
     * Create a crafting gui window.
     *
     * @param container       the container.
     * @param playerInventory the player inv.
     * @param iTextComponent  the display text component.
     */
    public WindowFurnaceCrafting(final ContainerCraftingFurnace container, final PlayerInventory playerInventory, final ITextComponent iTextComponent)
    {
        super(container, playerInventory, iTextComponent);
        this.container = container;
        this.building = (AbstractBuildingSmelterCrafter.View) IColonyManager.getInstance().getBuildingView(playerInventory.player.world.getDimensionKey(), container.getPos());
    }

    @Override
    protected void init()
    {
        super.init();
        final String buttonDisplay = building.canRecipeBeAdded() ? I18n.format("gui.done") : LanguageHandler.format("com.minecolonies.coremod.gui.recipe.full");
        /*
         * The button to click done after finishing the recipe.
         */
        final Button doneButton = new Button(guiLeft + BUTTON_X_OFFSET, guiTop + BUTTON_Y_POS, BUTTON_WIDTH, BUTTON_HEIGHT, new StringTextComponent(buttonDisplay), new OnButtonPress());
        this.addButton(doneButton);
        if (!building.canRecipeBeAdded())
        {
            doneButton.active = false;
        }
    }

    public class OnButtonPress implements Button.IPressable
    {
        @Override
        public void onPress(@NotNull final Button button)
        {
            if (building.canRecipeBeAdded())
            {
                final List<ItemStack> input = new ArrayList<>();
                input.add(container.inventorySlots.get(0).getStack());
                final ItemStack primaryOutput = container.inventorySlots.get(1).getStack().copy();

                if (!ItemStackUtils.isEmpty(primaryOutput))
                {
                    Network.getNetwork().sendToServer(new AddRemoveRecipeMessage(building, input, 1, primaryOutput, ImmutableList.of(), false));
                }
            }
        }
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(@NotNull final MatrixStack stack, final float partialTicks, final int mouseX, final int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(CRAFTING_FURNACE);
        this.blit(stack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void render(@NotNull final MatrixStack stack, int x, int y, float z)
    {
        this.renderBackground(stack);
        super.render(stack, x, y, z);
        this.renderHoveredTooltip(stack, x, y);
    }
}
