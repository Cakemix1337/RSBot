package org.rsbot.script.internal.wrappers;

import org.rsbot.script.wrappers.RSTile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TileFlags extends RSTile {
	public static interface Keys {
		static final int TILE_WATER = 1280;
		static final int WALL_NORTH_WEST = 1;
		static final int WALL_NORTH = 2;
		static final int WALL_NORTH_EAST = 4;
		static final int WALL_EAST = 8;
		static final int WALL_SOUTH_EAST = 10;
		static final int WALL_SOUTH = 20;
		static final int WALL_SOUTH_WEST = 40;
		static final int WALL_WEST = 80;
		static final int BLOCKED = 100;
	}

	public static interface Flags {
		public static final int WALL_NORTH_WEST = 0x1;
		public static final int WALL_NORTH = 0x2;
		public static final int WALL_NORTH_EAST = 0x4;
		public static final int WALL_EAST = 0x8;
		public static final int WALL_SOUTH_EAST = 0x10;
		public static final int WALL_SOUTH = 0x20;
		public static final int WALL_SOUTH_WEST = 0x40;
		public static final int WALL_WEST = 0x80;
		public static final int BLOCKED = 0x100;
		public static final int WATER = 0x1280100;
	}

	private List<Integer> keys = new ArrayList<Integer>();

	public TileFlags(RSTile tile) {
		super(tile.getX(), tile.getY(), tile.getZ());
	}

	public Integer[] getKeys() {
		return keys.toArray(new Integer[keys.size()]);
	}

	public TileFlags(RSTile tile, Integer[] keys) {
		super(tile.getX(), tile.getY(), tile.getZ());
		this.keys.addAll(Arrays.asList(keys));
	}

	public boolean isQuestionable() {
		return keys.size() > 0 && !keys.contains(Keys.TILE_WATER) && !keys.contains(Keys.BLOCKED);
	}

	public boolean isWalkable() {
		return keys.size() == 0;
	}

	public boolean isWater() {
		return keys.contains(Keys.TILE_WATER);
	}

	public void addKey(final int key) {
		keys.add(key);
	}

	public boolean containsKey(final int... keyz) {
		boolean check = false;
		for (int key : keyz) {
			check = check || keys.contains(key);
		}
		return check;
	}

	@Override
	public String toString() {
		String flags = "";
		Iterator<Integer> keysIterator = keys.listIterator();
		while (keysIterator.hasNext()) {
			int flag = keysIterator.next();
			flags += flag + "=";
		}
		return getX() + "," + getY() + "," + getZ() + "tile=data" + flags;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof TileFlags) {
			TileFlags tileFlags = (TileFlags) o;
			return flagsEqual(tileFlags, this);
		}
		return false;
	}

	private static final boolean flagsEqual(final TileFlags c, final TileFlags v) {
		if (c.keys.size() == v.keys.size()) {
			Iterator<Integer> keysIterator = c.keys.listIterator();
			while (keysIterator.hasNext()) {
				int flag = keysIterator.next();
				if (!v.keys.contains(flag)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}