package agency.highlysuspect.oneoffs.bobbyallowlist;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.*;
import java.util.stream.Collectors;

public class BobbyAllowlistConfig {
	public Set<BlockEntityType<?>> allowTypes = new HashSet<>();
	public Set<BlockPos> allowPos = new TreeSet<>(BlockPos::compareTo);
	
	public void toProperties(Properties props) {
		props.setProperty("allowPos", writeBlockPosCollection(allowPos));
		props.setProperty("allowTypes", writeBlockEntityTypeCollection(allowTypes));
	}
	
	public static BobbyAllowlistConfig fromProperties(Properties props) {
		BobbyAllowlistConfig cfg = new BobbyAllowlistConfig();
		cfg.allowPos.addAll(parseBlockPosList(props.getProperty("allowPos", "")));
		cfg.allowTypes.addAll(parseBlockEntityTypeList(props.getProperty("allowTypes", "")));
		
		System.out.println(cfg.allowPos);
		return cfg;
	}
	
	private static Optional<BlockPos> parseBlockPos(String s) {
		if(s == null || s.isEmpty()) return Optional.empty();
		String[] split = s.split(",");
		try {
			return Optional.of(new BlockPos(
				Integer.parseInt(split[0].trim()),
				Integer.parseInt(split[1].trim()),
				Integer.parseInt(split[2].trim())
			));
		} catch (Throwable e) {
			return Optional.empty();
		}
	}
	
	private static String writeBlockPos(BlockPos p) {
		return p.getX() + "," + p.getY() + "," + p.getZ();
	}
	
	private static List<BlockPos> parseBlockPosList(String s) {
		return Arrays.stream(s.split(";")).flatMap(a -> parseBlockPos(a).stream()).toList();
	}
	
	private static String writeBlockPosCollection(Collection<BlockPos> c) {
		return c.stream().map(BobbyAllowlistConfig::writeBlockPos).collect(Collectors.joining(";"));
	}
	
	private static Optional<BlockEntityType<?>> parseBlockEntityType(String s) {
		try {
			ResourceLocation rl = ResourceLocation.tryParse(s);
			if(rl == null) return Optional.empty();
			else return BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional(rl);
		} catch (Throwable e) {
			return Optional.empty();
		}
	}
	
	private static String writeBlockEntityType(BlockEntityType<?> type) {
		return Objects.requireNonNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type)).toString();
	}
	
	private static List<BlockEntityType<?>> parseBlockEntityTypeList(String s) {
		return Arrays.stream(s.split(";")).flatMap(a -> parseBlockEntityType(a).stream()).toList();
	}
	
	private static String writeBlockEntityTypeCollection(Collection<BlockEntityType<?>> types) {
		return types.stream().map(BobbyAllowlistConfig::writeBlockEntityType).sorted().collect(Collectors.joining(";"));
	}
}
