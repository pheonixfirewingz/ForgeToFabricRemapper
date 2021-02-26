package com.minecraftabnormals.endergetic.common.entities.bolloom;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * The boat has orders in which its balloons are positioned.
 * This is an enum used to represent the values of the order positions for Boats.
 *
 * @author - SmellyModder (Luke Tonon)
 */
public enum BalloonOrder {
	FIRST(0.9F, 1.6F, -0.5F),
	SECOND(-0.9F, -1.6F, 0.5F),
	THIRD(-0.9F, -1.6F, -0.5F),
	LAST(0.9F, 1.6F, 0.5F);

	public final float normalX, largeX, normalZ;

	BalloonOrder(float normalX, float largeX, float normalZ) {
		this.normalX = normalX;
		this.largeX = largeX;
		this.normalZ = normalZ;
	}

	/**
	 * Converts a {@link Set} of {@link BalloonOrder}s to a {@link Set} of integers representing the orders' ordinal values.
	 *
	 * @param orders - The {@link Set} of {@link BalloonOrder}s.
	 * @return - A {@link Set} of integers representing the orders' ordinal values
	 */
	public static Set<Integer> toOrdinalSet(Set<BalloonOrder> orders) {
		Set<Integer> ordinals = Sets.newHashSet();
		for (BalloonOrder order : orders) {
			ordinals.add(order.ordinal());
		}
		return ordinals;
	}

	/**
	 * Gets a {@link BalloonOrder} by its ordinal value.
	 *
	 * @param ordinal - The ordinal.
	 * @return a {@link BalloonOrder} with its ordinal matching the supplied ordinal.
	 */
	public static BalloonOrder byOrdinal(int ordinal) {
		for (BalloonOrder order : values()) {
			if (order.ordinal() == ordinal) {
				return order;
			}
		}
		return BalloonOrder.FIRST;
	}
}