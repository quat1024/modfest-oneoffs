package agency.highlysuspect.oneoffs.bobbyallowlist;

import agency.highlysuspect.oneoffs.common.AutoloadProperties;
import agency.highlysuspect.oneoffs.common.BaseClient;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BobbyAllowlist extends BaseClient {
	public BobbyAllowlist() {
		super("oneoffs-bobbyallowlist");
		INSTANCE = this;
	}
	
	public static BobbyAllowlist INSTANCE;
	public static BobbyAllowlistConfig CONFIG = new BobbyAllowlistConfig();
	
	@Override
	public void onInitializeClient() {
		log.info("Hello from bobbyallowlist");

		AutoloadProperties<BobbyAllowlistConfig> autoload = new AutoloadProperties<>(
			log,
			configPath("bobby-block-entity-allowlist.conf"),
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
		
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> {
			dispatcher.register(ClientCommandManager.literal("bobbyallowlist")
				.then(ClientCommandManager.literal("type")
					.executes(cmd -> {
						HitResult h = cmd.getSource().getClient().hitResult;
						if(!(h instanceof BlockHitResult bhr)) {
							cmd.getSource().sendFeedback(Component.literal("nothing under cursor"));
							return 0;
						}
						BlockEntity be = cmd.getSource().getWorld().getBlockEntity(bhr.getBlockPos());
						if(be == null) {
							cmd.getSource().sendFeedback(Component.literal("not a block entity"));
							return 0;
						}
						BlockEntityType<?> type = be.getType();
						ResourceLocation rl = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type);
						assert rl != null;
						cmd.getSource().sendFeedback(Component.literal("adding type " + rl + " to allowlist"));
						CONFIG.allowTypes.add(rl.toString());
						autoload.saveLater(CONFIG);
						return 1;
					})
					.then(ClientCommandManager.argument("type", StringArgumentType.greedyString())
						.executes(cmd -> {
							String type = StringArgumentType.getString(cmd, "type");
							cmd.getSource().sendFeedback(Component.literal("adding type " + type + " to allowlist"));
							CONFIG.allowTypes.add(type);
							autoload.saveLater(CONFIG);
							return 1;
						})
					)
				)
				.then(ClientCommandManager.literal("pos")
					.executes(cmd -> {
						HitResult h = cmd.getSource().getClient().hitResult;
						if(!(h instanceof BlockHitResult bhr)) {
							cmd.getSource().sendFeedback(Component.literal("nothing under cursor"));
							return 0;
						}
						BlockPos pos = bhr.getBlockPos();
						cmd.getSource().sendFeedback(Component.literal("adding pos " + pos.toShortString() + " to allowlist"));
						CONFIG.allowPos.add(pos);
						autoload.saveLater(CONFIG);
						return 1;
					})
					.then(ClientCommandManager.argument("pos", BlockPosArgument.blockPos())
						.executes(cmd -> {
							//BlockPosArgument.getBlockPos doesn't work on fabric client command source :(
							BlockPos pos = cmd.getArgument("pos", Coordinates.class).getBlockPos(new CommandSourceShim(cmd.getSource()));
							
							cmd.getSource().sendFeedback(Component.literal("adding pos " + pos.toShortString() + " to allowlist"));
							CONFIG.allowPos.add(pos);
							autoload.saveLater(CONFIG);
							return 1;
						})
					)
				)
			);
		});
	}
	
	//Just to pass to Coordinates.getBlockPos()
	private static class CommandSourceShim extends CommandSourceStack {
		public CommandSourceShim(FabricClientCommandSource src) {
			super(
				null, //CommandSource
				src.getPosition(), //position
				src.getRotation(), //rotation
				null, //server level
				4, //permission level
				src.getEntity().getScoreboardName(), //name
				src.getEntity().getDisplayName(), //display name
				null, //server
				src.getEntity(), //entity
				false, //silent flag
				null, //command result callback
				EntityAnchorArgument.Anchor.EYES, //anchor
				null, //signing context
				null //task chainer
			);
		}
	}
}
