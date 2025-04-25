package agency.highlysuspect.oneoffs.hideservermessages;

import java.util.function.BiFunction;

import agency.highlysuspect.oneoffs.common.AutoloadProperties;
import agency.highlysuspect.oneoffs.common.BaseClient;
import agency.highlysuspect.oneoffs.hideservermessages.mixin.AccessorChatComponent;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;

public class HideServerMessages extends BaseClient {
	public HideServerMessages() {
		super("oneoffs-hideservermessages");
		INSTANCE = this;
	}

	public static HideServerMessages INSTANCE;
	public HideServerMessagesConfig config = new HideServerMessagesConfig();

	@Override
	public void onInitializeClient() {
		log.info("Hello from HideServerMessages");

		AutoloadProperties<HideServerMessagesConfig> autoload = new AutoloadProperties<>(
			log,
			configPath("hideservermessages.properties"),
			HideServerMessagesConfig::new,
			HideServerMessagesConfig::toProperties,
			HideServerMessagesConfig::fromProperties,
			HideServerMessagesConfig.comment()
		);

		autoload.load(newConfig -> config = newConfig);
		autoload.watch(newConfig ->
			Minecraft.getInstance().execute(() -> {
				config = newConfig;

				//call the refresh function (normally called when resizing window or something)
				ChatComponent chat = Minecraft.getInstance().gui.getChat();
				if(chat instanceof AccessorChatComponent acc) acc.hsm$refreshTrimmedMessages();
			}));

		//lol
		BiFunction<Boolean, CommandContext<FabricClientCommandSource>, Integer> set = (newOpt, cmd) -> {
			//send the command feedback first so it doesn't get hidden as well :)
			//n.b. this doesnt work lol
			cmd.getSource().sendFeedback(Component.literal("Server messages are now ")
				.append(Component.literal(newOpt ? "shown" : "hidden").withStyle(newOpt ? ChatFormatting.GREEN : ChatFormatting.RED)));

			config.show = newOpt;

			//call the refresh function (normally called when resizing window or something)
			ChatComponent chat = Minecraft.getInstance().gui.getChat();
			if(chat instanceof AccessorChatComponent acc) acc.hsm$refreshTrimmedMessages();

			autoload.saveLater(config);
			return 0;
		};
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, ctx) -> {
			dispatcher.register(ClientCommandManager.literal("hideservermessages")
				.executes(cmd -> set.apply(!config.show, cmd))
				.then(ClientCommandManager.literal("on").executes(cmd -> set.apply(true, cmd)))
				.then(ClientCommandManager.literal("off").executes(cmd -> set.apply(false, cmd))));
		});
	}
}
