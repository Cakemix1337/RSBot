package org.rsbot.service;

import org.rsbot.script.internal.wrappers.TileFlags;
import org.rsbot.script.methods.Web;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.CacheWriter;
import org.rsbot.util.GlobalConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

public class WebQueue {
	private static CacheWriter cacheWriter = null;
	private static final Logger log = Logger.getLogger("WebQueue");
	public static boolean weAreBuffering = false;
	public static boolean speedBuffer = false;
	public static int bufferingCount = 0;

	public static void Create() {
		if (cacheWriter == null) {
			cacheWriter = new CacheWriter(GlobalConfiguration.Paths.getWebCache());
		}
	}

	public static CacheWriter getCacheWriter() {
		return cacheWriter;
	}

	public static void Add(final HashMap<RSTile, TileFlags> theFlagsList) {
		Web.map.putAll(theFlagsList);
		new Thread() {
			public void run() {
				final int count = theFlagsList.size();
				try {
					String addedString = "";
					final HashMap<RSTile, TileFlags> theFlagsList2 = new HashMap<RSTile, TileFlags>();
					theFlagsList2.putAll(theFlagsList);
					final Map<RSTile, TileFlags> tl = Collections.unmodifiableMap(theFlagsList2);
					bufferingCount = bufferingCount + count;
					Iterator<Map.Entry<RSTile, TileFlags>> tileFlagsIterator = tl.entrySet().iterator();
					while (tileFlagsIterator.hasNext()) {
						TileFlags tileFlags = tileFlagsIterator.next().getValue();
						if (tileFlags != null) {
							addedString += tileFlags.toString() + "\n";
							bufferingCount--;
							try {
								weAreBuffering = true;
								if (!speedBuffer) {
									Thread.sleep(10);
								}
							} catch (InterruptedException ignored) {
							}
						}
					}
					if (bufferingCount < 0) {
						bufferingCount = 0;
					}
					cacheWriter.add(addedString);
					addedString = null;
					theFlagsList2.clear();
					try {
						Thread.sleep(500);//Prevent data loss.
					} catch (InterruptedException ignored) {
					}
					weAreBuffering = false;
				} catch (Exception e) {
					bufferingCount = count;
					if (bufferingCount < 0) {
						bufferingCount = 0;
					}
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static void Remove(final RSTile tile) {
		new Thread() {
			public void run() {
				cacheWriter.remove(tile.getX() + "," + tile.getY() + tile.getZ());
			}
		}.start();
	}

	public static void Remove(final String str) {
		new Thread() {
			public void run() {
				cacheWriter.remove(str);
			}
		}.start();
	}

	public static boolean IsRunning() {
		return cacheWriter.IsRunning();
	}

	public static void Destroy() {
		speedBuffer = true;
		cacheWriter.destroy();
	}
}
