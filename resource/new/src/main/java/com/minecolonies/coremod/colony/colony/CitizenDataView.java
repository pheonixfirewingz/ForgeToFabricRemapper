package com.minecolonies.coremod.colony.colony;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * The CitizenDataView is the client-side representation of a CitizenData. Views contain the CitizenData's data that is relevant to a Client, in a more client-friendly form.
 * Mutable operations on a View result in a message to the server to perform the operation.
 */
public class CitizenDataView implements ICitizen
{
    private static final String TAG_HELD_ITEM_SLOT = "HeldItemSlot";

    /**
     * The resource location for the blocking overlay.
     */
    private static final Identifier BLOCKING_RESOURCE = new Identifier(Constants.MOD_ID, "textures/icons/blocking.png");

    /**
     * The resource location for the pending overlay.
     */
    private static final Identifier PENDING_RESOURCE = new Identifier(Constants.MOD_ID, "textures/icons/warning.png");

    /**
     * Attributes.
     */
    private final int     id;
    protected     int     entityId;
    protected     String  name;
    protected     boolean female;
    protected     boolean paused;
    protected     boolean isChild;

    private IJobView jobView;

    /**
     * colony id of the citizen.
     */
    protected int colonyId;

    /**
     * Placeholder skills.
     */
    private double health;
    private double maxHealth;
    private double saturation;

    /**
     * holds the current citizen happiness value
     */
    private double happiness;

    /**
     * The position of the guard.
     */
    private BlockPos position;

    /**
     * Job identifier.
     */
    private String job;

    /**
     * Working and home position.
     */
    @Nullable
    private BlockPos homeBuilding;
    @Nullable
    private BlockPos workBuilding;

    private InventoryCitizen inventory;

    /**
     * The citizen chat options on the server side.
     */
    private final Map<Text, IInteractionResponseHandler> citizenChatOptions = new LinkedHashMap<>();

    /**
     * List of primary interactions (sorted by priority).
     */
    private List<IInteractionResponseHandler> sortedInteractions;

    /**
     * The citizen skill handler on the client side.
     */
    private final CitizenSkillHandler citizenSkillHandler;

    /**
     * The citizen happiness handler.
     */
    private final CitizenHappinessHandler citizenHappinessHandler;

    /**
     * The citizens status icon
     */
    private VisibleCitizenStatus statusIcon;

    /**
     * Set View id.
     *
     * @param id the id to set.
     */
    protected CitizenDataView(final int id)
    {
        this.id = id;
        this.citizenSkillHandler = new CitizenSkillHandler();
        this.citizenHappinessHandler = new CitizenHappinessHandler();
    }

    @Override
    public int getId()
    {
        return id;
    }

    @Override
    public int getEntityId()
    {
        return entityId;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean isFemale()
    {
        return female;
    }

    @Override
    public boolean isPaused()
    {
        return paused;
    }

    @Override
    public boolean isChild()
    {
        return isChild;
    }

    /**
     * DEPRECATED
     */
    @Override
    public void setPaused(final boolean p)
    {
        this.paused = p;
    }

    
    public String getJob()
    {
        return job;
    }

    
    @Nullable
    public BlockPos getHomeBuilding()
    {
        return homeBuilding;
    }

    
    @Nullable
    public BlockPos getWorkBuilding()
    {
        return workBuilding;
    }

    /**
     * DEPRECATED
     */
    
    public void setWorkBuilding(@Nullable final BlockPos bp)
    {
        this.workBuilding = bp;
    }

    
    public int getColonyId()
    {
        return colonyId;
    }

    
    public double getHappiness()
    {
        return happiness;
    }

    
    public double getSaturation()
    {
        return saturation;
    }

    
    public double getHealth()
    {
        return health;
    }

    
    public double getMaxHealth()
    {
        return maxHealth;
    }

    
    public BlockPos getPosition()
    {
        return position;
    }

    
    public void deserialize(@NotNull final PacketByteBuf buf)
    {
        name = buf.readString(32767);
        female = buf.readBoolean();
        entityId = buf.readInt();
        paused = buf.readBoolean();
        isChild = buf.readBoolean();

        homeBuilding = buf.readBoolean() ? buf.readBlockPos() : null;
        workBuilding = buf.readBoolean() ? buf.readBlockPos() : null;

        // Attributes
        health = buf.readFloat();
        maxHealth = buf.readFloat();

        saturation = buf.readDouble();
        happiness = buf.readDouble();

        citizenSkillHandler.read(buf.readCompoundTag());

        job = buf.readString(32767);

        colonyId = buf.readInt();

        final CompoundTag compound = buf.readCompoundTag();
        inventory = new InventoryCitizen(this.name, true);
        final ListTag ListTag = compound.getList("inventory", 10);
        this.inventory.read(ListTag);
        this.inventory.setHeldItem(Hand.MAIN_HAND, compound.getInt(TAG_HELD_ITEM_SLOT));
        this.inventory.setHeldItem(Hand.OFF_HAND, compound.getInt(TAG_OFFHAND_HELD_ITEM_SLOT));

        position = buf.readBlockPos();

        citizenChatOptions.clear();
        final int size = buf.readInt();
        for (int i = 0; i < size; i++)
        {
            final CompoundTag compoundNBT = buf.readCompoundTag();
            final ServerCitizenInteraction handler =
              (ServerCitizenInteraction) MinecoloniesAPIProxy.getInstance().getInteractionResponseHandlerDataManager().createFrom(this, compoundNBT);
            citizenChatOptions.put(handler.getInquiry(), handler);
        }

        sortedInteractions = citizenChatOptions.values().stream().sorted(Comparator.comparingInt(e -> e.getPriority().getPriority())).collect(Collectors.toList());

        citizenHappinessHandler.read(buf.readCompoundTag());

        int statusindex = buf.readInt();
        statusIcon = statusindex >= 0 ? VisibleCitizenStatus.getForId(statusindex) : null;

        if (buf.readBoolean())
        {
            final ColonyView colonyView = ColonyManager.getInstance().getColonyView(colonyId, Minecraft.getInstance().world.getDimensionKey());
            jobView = IJobDataManager.getInstance().createViewFrom(colonyView, this, buf);
        }
    }

    
    public IJobView getJobView()
    {
        return this.jobView;
    }

    @Override
    public InventoryCitizen getInventory()
    {
        return inventory;
    }

    
    public List<IInteractionResponseHandler> getOrderedInteractions()
    {
        return sortedInteractions;
    }

    
    @Nullable
    public IInteractionResponseHandler getSpecificInteraction(@NotNull final Text component)
    {
        return citizenChatOptions.getOrDefault(component, null);
    }

    
    public boolean hasBlockingInteractions()
    {
        if (sortedInteractions.isEmpty())
        {
            return false;
        }

        final IInteractionResponseHandler interaction = sortedInteractions.get(0);
        if (interaction != null)
        {
            return interaction.getPriority().getPriority() >= ChatPriority.IMPORTANT.getPriority();
        }

        return false;
    }

    
    public boolean hasPendingInteractions()
    {
        if (sortedInteractions.isEmpty())
        {
            return false;
        }

        final IInteractionResponseHandler interaction = sortedInteractions.get(0);
        if (interaction != null)
        {
            return interaction.isPrimary();
        }

        return false;
    }

    
    public ICitizenSkillHandler getCitizenSkillHandler()
    {
        return citizenSkillHandler;
    }

    
    public ICitizenHappinessHandler getHappinessHandler()
    {
        return citizenHappinessHandler;
    }

    
    public Identifier getInteractionIcon()
    {
        if (sortedInteractions == null || sortedInteractions.isEmpty())
        {
            return null;
        }

       Identifier icon = sortedInteractions.get(0).getInteractionIcon();
        if (icon == null)
        {
            if (hasBlockingInteractions())
            {
                icon = BLOCKING_RESOURCE;
            }
            else
            {
                icon = PENDING_RESOURCE;
            }
        }

        return icon;
    }

    
    public VisibleCitizenStatus getVisibleStatus()
    {
        return statusIcon;
    }
}
