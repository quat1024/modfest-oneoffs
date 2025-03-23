package agency.highlysuspect.oneoffs.spectatorheads;

import agency.highlysuspect.oneoffs.common.BaseClient;
import net.fabricmc.loader.api.FabricLoader;

public class SpectatorHeads extends BaseClient {
	public SpectatorHeads() {
		super("modfest-oneoffs-spectatorheads");
	}
	
	@Override
	public void onInitializeClient() {
		log.info("hello from spectatorheads");
		
	}
}
