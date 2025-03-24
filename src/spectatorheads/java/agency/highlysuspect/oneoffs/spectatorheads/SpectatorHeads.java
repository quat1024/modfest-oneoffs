package agency.highlysuspect.oneoffs.spectatorheads;

import java.util.function.BiFunction;

import agency.highlysuspect.oneoffs.common.AutoloadProperties;
import agency.highlysuspect.oneoffs.common.BaseClient;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class SpectatorHeads extends BaseClient {
	public SpectatorHeads() {
		super("oneoffs-spectatorheads");
	}
	
	public static SpectatorHeadsConfig CONFIG = new SpectatorHeadsConfig();
	
	@Override
	public void onInitializeClient() {
		log.info("hello from spectatorheads");

		AutoloadProperties<SpectatorHeadsConfig> autoload = new AutoloadProperties<>(
			log,
			configPath("spectatorheads.properties"),
			SpectatorHeadsConfig::new,
			SpectatorHeadsConfig::toProperties,
			SpectatorHeadsConfig::fromProperties
		);
		
		//initial load
		autoload.load(newState -> CONFIG = newState);
		
		//watch for changes
		autoload.watch(newState ->
			Minecraft.getInstance().execute(() -> CONFIG = newState));
		
		//lol
		BiFunction<Boolean, CommandContext<FabricClientCommandSource>, Integer> set = (newOpt, cmd) -> {
			CONFIG.showSpectatorHeads = newOpt;
			cmd.getSource().sendFeedback(Component.literal("Spectator heads are now ")
				.append(Component.literal(newOpt ? "shown" : "hidden").withStyle(newOpt ? ChatFormatting.GREEN : ChatFormatting.RED)));
			autoload.saveLater(CONFIG);
			return 0;
		};
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, ctx) -> {
			dispatcher.register(ClientCommandManager.literal("spectatorheads")
				.executes(cmd -> set.apply(!CONFIG.showSpectatorHeads, cmd))
				.then(ClientCommandManager.literal("on").executes(cmd -> set.apply(true, cmd)))
				.then(ClientCommandManager.literal("off").executes(cmd -> set.apply(false, cmd))));
		});
	}
}
