package agency.highlysuspect.oneoffs.bobbyallowlist;

import agency.highlysuspect.oneoffs.common.AutoloadProperties;
import agency.highlysuspect.oneoffs.common.BaseClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

import java.nio.file.Path;

public class BobbyAllowlist extends BaseClient {
	public BobbyAllowlist() {
		super("modfest-oneoffs-bobbyallowlist");
	}
	
	public static BobbyAllowlistConfig CONFIG = new BobbyAllowlistConfig();
	
	@Override
	public void onInitializeClient() {
		log.info("Hello from bobbyallowlist");
		
		Path configPath = FabricLoader.getInstance().getConfigDir().resolve("bobby-block-entity-allowlist.conf");
		AutoloadProperties<BobbyAllowlistConfig> autoload = new AutoloadProperties<>(
			log, configPath,
			BobbyAllowlistConfig::new,
			BobbyAllowlistConfig::toProperties,
			BobbyAllowlistConfig::fromProperties
		);
		
		//wait for CLIENT_STARTED to load config since it uses BlockEntityTypes
		ClientLifecycleEvents.CLIENT_STARTED.register(started -> {
			//initial load
			autoload.load(newState -> CONFIG = newState);
			
			//watch for changes
			autoload.watch(newState ->
				Minecraft.getInstance().execute(() -> CONFIG = newState));
		});
	}
}
