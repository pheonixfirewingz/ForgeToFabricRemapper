package com.minecraftabnormals.endergetic.common.blocks.poise.hive;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.*;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.minecraftabnormals.endergetic.common.entities.puffbug.PuffBugEntity;
import com.minecraftabnormals.endergetic.common.tileentities.PuffBugHiveTileEntity;
import com.minecraftabnormals.endergetic.core.registry.EEBlocks;

import java.util.List;

public class PuffBugHiveBlock extends Block {
	private static final VoxelShape HIVE_SHAPE = VoxelShapes.or(Block.makeCuboidShape(0.0D, 3.0D, 0.0D, 16.0D, 16.0D, 16.0D), Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 3.0D, 15.0D));

	public PuffBugHiveBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return HIVE_SHAPE;
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		alertPuffBugs(world, pos, player);
		super.onBlockHarvested(world, pos, state, player);
	}

	@Override
	public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion) {
		alertPuffBugs(world, pos, explosion.getExplosivePlacedBy());
		super.onExplosionDestroy(world, pos, explosion);
	}

	@Override
	public void onProjectileCollision(World world, BlockState state, BlockRayTraceResult hit, ProjectileEntity projectile) {
		alertPuffBugs(world, hit.getPos(), null);
	}

	@Override
	public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		alertPuffBugs(world, pos, player);
	}

	@Override
	@Nonnull
	public BlockState updatePostPlacement(@Nonnull BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		return !isValidPosition(state, world, currentPos) ? alertPuffBugs(world, currentPos, null) : state;
	}

	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockPos blockpos = context.getPos();
		World world = context.getWorld();
		for (Direction enumfacing : context.getNearestLookingDirections()) {
			if (enumfacing == Direction.UP) {
				BlockPos up = blockpos.up();
				if (world.isAirBlock(blockpos.down()) && world.getBlockState(up).isSolidSide(context.getWorld(), up, Direction.DOWN)) {
					AxisAlignedBB bb = new AxisAlignedBB(context.getPos().down());
					List<Entity> entities = context.getWorld().getEntitiesWithinAABB(Entity.class, bb);
					if (entities.size() > 0) {
						return null;
					}
					world.setBlockState(blockpos.down(), this.getDefaultState());
					return EEBlocks.HIVE_HANGER.get().getDefaultState();
				} else {
					return this.getDefaultState();
				}
			} else {
				return this.getDefaultState();
			}
		}
		return null;
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
		BlockState down = world.getBlockState(pos.down());
		return (world.getBlockState(pos.up()).getBlock() instanceof PuffbugHiveHangerBlock) || down.isSolid() || down.getBlock() instanceof PuffBugHiveBlock;
	}

	@Override
	public ToolType getHarvestTool(BlockState state) {
		return ToolType.PICKAXE;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new PuffBugHiveTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	public static BlockState alertPuffBugs(IWorld world, BlockPos pos, @Nullable LivingEntity breaker) {
		if (!world.isRemote()) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof PuffBugHiveTileEntity) {
				PuffBugHiveTileEntity hive = (PuffBugHiveTileEntity) tile;
				if (breaker == null) {
					hive.alertPuffBugs(null);
				} else {
					if (PuffBugEntity.CAN_ANGER.test(breaker)) {
						hive.alertPuffBugs(breaker);
					}
				}
			}
		}
		return Blocks.AIR.getDefaultState();
	}
}
