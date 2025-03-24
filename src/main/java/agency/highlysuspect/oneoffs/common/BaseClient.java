package agency.highlysuspect.oneoffs.common;

import java.nio.file.Path;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseClient implements ClientModInitializer {
	public BaseClient(String modid) {
		this.log = LoggerFactory.getLogger(modid);
	}
	
	public final Logger log;

	protected Path configPath(String filename) {
		return FabricLoader.getInstance().getConfigDir().resolve(filename);
	}
}
