package agency.highlysuspect.oneoffs.bobbyallowlist;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;

public class BobbyAllowlistConfig {
	public Set<String> allowTypes = new HashSet<>();
	public Set<BlockPos> allowPos = new TreeSet<>(BlockPos::compareTo);
	private boolean debug = false;
	
	public boolean canBypassNoBlockEntities(CompoundTag tag) {
		if(debug)
			return canBypass_Debug(tag);
		
		return allowTypes.contains(tag.getString("id")) || allowPos.contains(BlockEntity.getPosFromTag(tag));
	}
	
	private boolean canBypass_Debug(CompoundTag tag) {
		Logger log = BobbyAllowlist.INSTANCE.log;
		
		String type = tag.getString("id");
		BlockPos pos = BlockEntity.getPosFromTag(tag);
		String msg = type + " (" + pos.toShortString() + ")";
		if(allowTypes.contains(type)) {
			log.info("Match TYPE: {}", msg);
			return true;
		} else {
			log.warn("Fail TYPE: {}", msg);
		}
		
		if(allowPos.contains(pos)) {
			log.info("Match POS: {}", msg);
			return true;
		} else {
			log.warn("Fail POS: {}", msg);
		}
		
		return false;
	}
	
	public void toProperties(Properties props) {
		props.setProperty("allowPos", writeBlockPosCollection(allowPos));
		props.setProperty("allowTypes", writeBlockEntityTypeCollection(allowTypes));
		props.setProperty("debug", Boolean.toString(debug));
	}
	
	public static BobbyAllowlistConfig fromProperties(Properties props) {
		BobbyAllowlistConfig cfg = new BobbyAllowlistConfig();
		cfg.allowPos.addAll(parseBlockPosList(props.getProperty("allowPos", "")));
		cfg.allowTypes.addAll(parseBlockEntityTypeList(props.getProperty("allowTypes", "")));
		cfg.debug = Boolean.parseBoolean(props.getProperty("debug", "false"));
		return cfg;
	}

	public static String[] comment() {
		return new String[] {
			"oneoffs-bobbyallowlist config file",
			"",
			"Note that this mod only does anything if `no-block-entities` is `true` in Bobby's config.",
			"",
			"allowTypes: Semicolon-separated list of block entity types to allow in Bobby fake chunks.",
			"  (You can add block entities to this list by looking at them and running '/bobbyallowlist type'.)",
			"",
			"allowPos: Semicolon-separated list of x,y,z positions. Any block entities in these positions",
			"  are allowed in Bobby fake chunks. (You can add positions to this list by looking at them and",
			"  running '/bobbyallowlist pos'.)",
			"",
			"debug: Spam a lot of debug information when a Bobby fake chunk is loaded or unloaded.",
			"",
		};
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
	
	private static Optional<String> parseBlockEntityType(String s) {
		s = s.trim();
		if(s.isEmpty()) return Optional.empty();
		
		//just double-check that it's well-formed
		else if(ResourceLocation.tryParse(s) == null) return Optional.empty();
		
		else return Optional.of(s);
	}
	
	//todo this made more sense when i actually tried to use BlockEntityType
	private static String writeBlockEntityType(String type) {
		return type.trim();
	}
	
	private static List<String> parseBlockEntityTypeList(String s) {
		return Arrays.stream(s.split(";")).flatMap(a -> parseBlockEntityType(a).stream()).toList();
	}
	
	private static String writeBlockEntityTypeCollection(Collection<String> types) {
		return types.stream().map(BobbyAllowlistConfig::writeBlockEntityType).sorted().collect(Collectors.joining(";"));
	}
}
