package agency.highlysuspect.oneoffs.bobbyallowlist.mixin;

import agency.highlysuspect.oneoffs.bobbyallowlist.BobbyAllowlist;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.johni0702.minecraft.bobby.ChunkSerializer;
import de.johni0702.minecraft.bobby.FakeChunk;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkSerializer.class)
public class MixinBobbyChunkSerializer {
	@ModifyExpressionValue(
		method = "deserialize",
		at = @At(value = "INVOKE", target = "Lde/johni0702/minecraft/bobby/BobbyConfig;isNoBlockEntities()Z"),
		remap = false
	)
	private static boolean bobbyallowlist$patchDeserialize(
		boolean original,
		@SuppressWarnings("unused") ChunkPos _pos,
		CompoundTag level,
		@SuppressWarnings("unused") Level _world,
		@Local FakeChunk chunk
	) {
		//noBlockEntities is false -> block entities is true -> user wants to see all block entities anyway
		if(!original) return false;
		
		//copypasta of original logic
		ListTag blockEntitiesTag = level.getList("block_entities", 10);
		for(int i = 0; i < blockEntitiesTag.size(); i++) {
			CompoundTag cmp = blockEntitiesTag.getCompound(i);
			if(BobbyAllowlist.CONFIG.canBypassNoBlockEntities(cmp))
				chunk.setBlockEntityNbt(cmp);
		}
		
		return true; //skip the original logic
	}
	
	@ModifyExpressionValue(
		method = "shallowCopy",
		at = @At(value = "INVOKE", target = "Lde/johni0702/minecraft/bobby/BobbyConfig;isNoBlockEntities()Z"),
		remap = false
	)
	private static boolean bobbyallowlist$patchShallowCopy(boolean original, @Local CompoundTag blockEntityTag) {
		return original && !BobbyAllowlist.CONFIG.canBypassNoBlockEntities(blockEntityTag);
	}
}
