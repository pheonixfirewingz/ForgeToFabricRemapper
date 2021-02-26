package com.minecolonies.coremod.blocks.decorative;

import com.minecolonies.api.blocks.decorative.AbstractBlockMinecoloniesConstructionTape;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.fluid.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.Direction.Axis;
import static net.minecraft.util.Direction.Plane;

/**
 * This block is used as a border to show the size of the building. It also shows that the building is in the progress of being built.
 */
public class BlockConstructionTape extends FallingBlock implements WaterLoggable
{
    public static final BooleanProperty NORTH = SixWayBlock.NORTH;
    public static final BooleanProperty EAST = SixWayBlock.EAST;
    public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
    public static final BooleanProperty WEST = SixWayBlock.WEST;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected VoxelShape[] shapes = new VoxelShape[]{};

    private final Object2IntMap<BlockState> stateShapeMap = new Object2IntOpenHashMap<>();

    /**
     * The default face for when there are no connections.
     */
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    /**
     * Implies that the tape should revert to a corner if there are no connections. Must be set explicitly. For use by the builder handler.
     */
    public static final BooleanProperty CORNER = BooleanProperty.create("corner");

    /**
     * Constructor for the Construction Tape decoration.
     */
    public BlockConstructionTape()
    {
        super(Properties.create(Material.TALL_PLANTS).hardnessAndResistance(0.0f).doesNotBlockMovement().noDrops());
        this.shapes = makeShapes(2, 2, 16, 0, 16);

        this.setDefaultState(this.getDefaultState()
                               .with(NORTH, false)
                               .with(EAST, false)
                               .with(SOUTH, false)
                               .with(WEST, false)
                               .with(FACING, Direction.NORTH)
                               .with(WATERLOGGED, false)
                               .with(CORNER, false)
        );
    }

    @NotNull
    @Override
    public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext context)
    {
        return super.getShape(state, worldIn, pos, context);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockItemUseContext context)
    {
        // Get the closest horizontal axis for the default orientation
        Direction[] faces = context.getNearestLookingDirections();

        return BlockConstructionTape.getPlacementState(
          super.getStateForPlacement(context),
          context.getWorld(),
          context.getPos(),
          faces[0].getHorizontalIndex() >= 0 ? faces[0] : faces[1]
        );
    }

    @NotNull
    @Override
    public BlockState updatePostPlacement(
      @NotNull final BlockState stateIn,
      final Direction dir,
      final BlockState state,
      final IWorld worldIn,
      @NotNull final BlockPos currentPos,
      final BlockPos pos)
    {
        if (stateIn.get(WATERLOGGED))
        {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }

        return BlockConstructionTape.getPlacementState(
          super.updatePostPlacement(stateIn, dir, state, worldIn, currentPos, pos), worldIn, currentPos, stateIn.get(FACING)
        );
    }

    /**
     * A static version of getStateForPlacement to allow helpers to interact with states
     *
     * @param state the block state to configure
     * @param world the world
     * @param pos   the position of the new state
     * @param face  the default direction of the tape when there are no connections
     * @return the configured state
     */
    public static BlockState getPlacementState(@Nullable BlockState state, IBlockReader world, BlockPos pos, Direction face)
    {
        Fluid fluid = world.getFluidState(pos).getFluid();
        List<Direction> connections = getConnections(world, pos, face, state.get(CORNER));

        return state
                 .with(NORTH, connections.contains(Direction.NORTH))
                 .with(EAST, connections.contains(Direction.EAST))
                 .with(SOUTH, connections.contains(Direction.SOUTH))
                 .with(WEST, connections.contains(Direction.WEST))
                 .with(FACING, face)
                 .with(WATERLOGGED, fluid == Fluids.WATER);
    }

    public static List<Direction> getConnections(IBlockReader world, BlockPos pos, Direction face, boolean corner)
    {
        List<Direction> connections = new ArrayList<>();

        for (Direction direction : Plane.HORIZONTAL)
        {
            if (canConnect(world, pos, direction))
            {
                connections.add(direction);
            }
        }

        // When the tape is isolated, set it to its default orientation
        // considering whether it is a corner
        if (connections.size() == 0 || (connections.size() == 1 && corner))
        {
            if (corner)
            {
                connections.clear();
                connections.add(face);
                connections.add(face.rotateY());
            }
            else
            {
                connections.add(face.getAxis() == Axis.X ? Direction.SOUTH : Direction.EAST);
                connections.add(face.getAxis() == Axis.X ? Direction.NORTH : Direction.WEST);
            }
        }
        else if (connections.size() == 1)
        {
            connections.add(connections.get(0).getOpposite());
        }
        else if (connections.size() == 3)
        {
            Direction stem = Direction.NORTH;

            for (Direction direction : Plane.HORIZONTAL)
            {
                if (!connections.contains(direction))
                {
                    stem = direction.getOpposite();
                }
            }

            // If the block in the direction of the stem also has three connections
            // with it's stem facing this block, remove this block's stem
            if (canRemoveTStem(world, pos, stem))
            {
                connections.remove(stem);
            }
        }

        return connections;
    }

    protected static boolean canConnect(IBlockReader world, BlockPos pos, Direction face)
    {
        BlockPos adjacent;
        switch (face)
        {
            default:
            case NORTH:
                adjacent = pos.north();
                break;
            case EAST:
                adjacent = pos.east();
                break;
            case SOUTH:
                adjacent = pos.south();
                break;
            case WEST:
                adjacent = pos.west();
                break;
        }
        return world.getBlockState(adjacent).getBlock() instanceof BlockConstructionTape;
    }

    protected static boolean canRemoveTStem(IBlockReader world, BlockPos pos, Direction face)
    {
        BlockState neighbor = world.getBlockState(pos.offset(face));
        switch (face)
        {
            case NORTH:
                return !neighbor.get(NORTH) && neighbor.get(EAST) && neighbor.get(WEST);
            case EAST:
                return !neighbor.get(EAST) && neighbor.get(NORTH) && neighbor.get(SOUTH);
            case SOUTH:
                return !neighbor.get(SOUTH) && neighbor.get(EAST) && neighbor.get(WEST);
            case WEST:
                return !neighbor.get(WEST) && neighbor.get(NORTH) && neighbor.get(SOUTH);
        }
        return false;
    }

    @Override
    public boolean propagatesSkylightDown(final BlockState state, @NotNull final IBlockReader reader, @NotNull final BlockPos pos)
    {
        return true;
    }

    @Override
    public void onEndFalling(final World worldIn, final BlockPos pos, final BlockState fallingState, final BlockState hitState, final FallingBlockEntity blockEntity)
    {
        worldIn.setBlockState(pos, BlockConstructionTape.getPlacementState(
          fallingState, worldIn, pos, fallingState.get(FACING)
        ));
    }

    @Override
    protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(NORTH, EAST, SOUTH, WEST, FACING, CORNER, WATERLOGGED);
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return this.shapes[this.getIndex(state)];
    }

    private static int getMask(Direction facing)
    {
        return 1 << facing.getHorizontalIndex();
    }

    protected int getIndex(BlockState state)
    {
        return this.stateShapeMap.computeIntIfAbsent(state, (computeState) ->
        {
            int i = 0;
            if(computeState.get(NORTH))
            {
                i |= getMask(Direction.NORTH);
            }

            if(computeState.get(EAST))
            {
                i |= getMask(Direction.EAST);
            }

            if(computeState.get(SOUTH))
            {
                i |= getMask(Direction.SOUTH);
            }

            if(computeState.get(WEST))
            {
                i |= getMask(Direction.WEST);
            }

            return i;
        });
    }

    @Override
    public FluidState getFluidState(final BlockState state)
    {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    protected VoxelShape[] makeShapes(float nodeWidth, float limbWidth, float nodeHeight, float limbBase, float limbTop)
    {
        float nodeStart = 8.0F - nodeWidth;
        float nodeEnd = 8.0F + nodeWidth;
        float limbStart = 8.0F - limbWidth;
        float limbEnd = 8.0F + limbWidth;

        VoxelShape node = Block.makeCuboidShape(nodeStart, 0.0F, nodeStart, nodeEnd, nodeHeight, nodeEnd);
        VoxelShape north = Block.makeCuboidShape(limbStart, limbBase, 0.0F, limbEnd, limbTop, limbEnd);
        VoxelShape south = Block.makeCuboidShape(limbStart, limbBase, limbStart, limbEnd, limbTop, 16.0D);
        VoxelShape west = Block.makeCuboidShape(0.0F, limbBase, limbStart, limbEnd, limbTop, limbEnd);
        VoxelShape east = Block.makeCuboidShape(limbStart, limbBase, limbStart, 16.0D, limbTop, limbEnd);
        VoxelShape cornernw = VoxelShapes.or(north, east);
        VoxelShape cornerse = VoxelShapes.or(south, west);

        // All 16 possible block combinations, in a specific index to be retrieved by getIndex
        VoxelShape[] avoxelshape = new VoxelShape[]
                {
                        VoxelShapes.empty(), south, west, cornerse, north,
                        VoxelShapes.or(south, north),
                        VoxelShapes.or(west, north),
                        VoxelShapes.or(cornerse, north), east,
                        VoxelShapes.or(south, east),
                        VoxelShapes.or(west, east),
                        VoxelShapes.or(cornerse, east), cornernw,
                        VoxelShapes.or(south, cornernw),
                        VoxelShapes.or(west, cornernw),
                        VoxelShapes.or(cornerse, cornernw)
                };

        // Combine the arm voxel shapes with the main node for all combinations
        for(int i = 0; i < 16; ++i)
        {
            avoxelshape[i] = VoxelShapes.or(node, avoxelshape[i]);
        }

        return avoxelshape;
    }
}
