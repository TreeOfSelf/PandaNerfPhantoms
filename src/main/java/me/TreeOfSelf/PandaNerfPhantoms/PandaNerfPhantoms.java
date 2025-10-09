package me.TreeOfSelf.PandaNerfPhantoms;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PandaNerfPhantoms implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("panda-nerf-phantoms");
	public static Config CONFIG;

	@Override
	public void onInitialize() {
		CONFIG = Config.load();
		LOGGER.info("PandaNerfPhantoms Started");
	}
}