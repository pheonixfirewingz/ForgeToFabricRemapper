package com.minecraftabnormals.endergetic.common.items.blockitems;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.IWorldReader;

public class CorrockCrownBlockItem extends BlockItem {
	protected final Block wallBlock;

	public CorrockCrownBlockItem(Block floorBlock, Block wallBlockIn, Item.Properties p_i48462_3_) {
		super(floorBlock, p_i48462_3_);
		this.wallBlock = wallBlockIn;
	}

	@Nullable
	protected BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState iblockstate = this.wallBlock.getStateForPlacement(context);
		BlockState iblockstate1 = null;
		IWorldReader iworldreaderbase = context.getWorld();
		BlockPos blockpos = context.getPos();

		for (Direction enumfacing : context.getNearestLookingDirections()) {
			BlockState iblockstate2 = enumfacing == Direction.UP || enumfacing == Direction.DOWN ? this.getBlock().getStateForPlacement(context) : iblockstate;
			if (iblockstate2 != null && iblockstate2.isValidPosition(iworldreaderbase, blockpos)) {
				iblockstate1 = iblockstate2;
				break;
			}
		}

		return iblockstate1 != null && iworldreaderbase.placedBlockCollides(iblockstate1, blockpos, ISelectionContext.dummy()) ? iblockstate1 : null;
	}

	public void addToBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
		super.addToBlockToItemMap(blockToItemMap, itemIn);
		blockToItemMap.put(this.wallBlock, itemIn);
	}
}