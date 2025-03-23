package agency.highlysuspect.oneoffs.common;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseClient implements ClientModInitializer {
	public BaseClient(String modid) {
		this.log = LoggerFactory.getLogger(modid);
	}
	
	public final Logger log;
}
