package com.minecolonies.coremod.tileentities;

import org.jetbrains.annotations.NotNull;

import java.util.Random;


/**
 * The composted dirty tileEntity to grow all kinds of flowers.
 */
public class TileEntityCompostedDirt extends TileEntity implements ITickableTileEntity
{
    /**
     * If currently composted.
     */
    private boolean composted = false;

    /**
     * The current tick timer.
     */
    private int ticker = 0;

    /**
     * Chance to grow something (per second).
     */
    private double percentage = 1.0D;

    /**
     * Max tick limit.
     */
    private final static int TICKER_LIMIT = 300;

    /**
     * Random tick.
     */
    private final Random random = new Random();

    /**
     * The flower to grow.
     */
    private ItemStack flower;

    /**
     * Constructor to create an instance of this tileEntity.
     */
    public TileEntityCompostedDirt()
    {
        super(MinecoloniesTileEntities.COMPOSTED_DIRT);
    }

    @Override
    public void tick()
    {
        final World world = this.getWorld();
        if (!world.isRemote && this.composted && ticker % TICKS_SECOND == 0)
        {
            this.updateTick(world);
        }
        ticker++;
    }

    /**
     * Update tick running on the server world.
     *
     * @param worldIn the server world.
     */
    private void updateTick(@NotNull final World worldIn)
    {
        if (flower == null || flower.isEmpty())
        {
            this.composted = false;
            return;
        }

        if (this.composted)
        {
            ((ServerWorld) worldIn).spawnParticle(
              ParticleTypes.HAPPY_VILLAGER, this.getPos().getX() + 0.5,
              this.getPos().getY() + 1, this.getPos().getZ() + 0.5,
              1, 0.2, 0, 0.2, 0);
        }

        if (random.nextDouble() * 100 <= this.percentage)
        {
            final BlockPos position = pos.up();
            if (worldIn.getBlockState(position).getBlock() instanceof AirBlock)
            {
                if (flower.getItem() instanceof BlockItem)
                {
                    if (((BlockItem) flower.getItem()).getBlock() instanceof DoublePlantBlock)
                    {
                        ((DoublePlantBlock) ((BlockItem) flower.getItem()).getBlock()).placeAt(worldIn, position, UPDATE_FLAG);
                    }
                    else
                    {
                        worldIn.setBlockState(position, ((BlockItem) flower.getItem()).getBlock().getDefaultState());
                    }
                }
                else
                {
                    worldIn.setBlockState(position, BlockUtils.getBlockStateFromStack(flower));
                }
            }
        }

        if (this.ticker >= TICKER_LIMIT * TICKS_SECOND)
        {
            this.ticker = 0;
            this.composted = false;
        }
    }

    @Override
    public void markDirty()
    {
        WorldUtil.markChunkDirty(world, pos);
    }

    /**
     * Method for the composter to call to start producing flowers.
     *
     * @param percentage the chance for this block to appear per second.
     * @param flower     the flower to grow.
     */
    public void compost(final double percentage, @NotNull final ItemStack flower)
    {
        if (percentage >= 0 && percentage <= 100)
        {
            this.percentage = percentage;
            try
            {
                this.flower = flower;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        this.composted = true;
    }

    /**
     * Check if the current compost tile entity is running.
     *
     * @return true if so.
     */
    public boolean isComposted()
    {
        return this.composted;
    }
}
