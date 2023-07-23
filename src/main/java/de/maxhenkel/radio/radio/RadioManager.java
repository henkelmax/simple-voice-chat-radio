package de.maxhenkel.radio.radio;

import com.mojang.authlib.GameProfile;
import de.maxhenkel.radio.Radio;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RadioManager {

    private final Map<UUID, RadioStream> radioStreams;

    public RadioManager() {
        radioStreams = new HashMap<>();
    }

    public void onLoadHead(SkullBlockEntity skullBlockEntity) {
        if (!(skullBlockEntity.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        @Nullable GameProfile ownerProfile = skullBlockEntity.getOwnerProfile();

        RadioData radioData = RadioData.fromGameProfile(ownerProfile);
        if (radioData != null) {
            // Set the UUID if none was present (block was just placed)
            radioData.updateProfile(ownerProfile);
            RadioStream radioStream = new RadioStream(radioData, serverLevel, skullBlockEntity.getBlockPos());
            Radio.LOGGER.debug("Loaded radio stream for '{}' ({})", radioData.getStationName(), radioData.getId());
            radioStream.init();
            RadioStream oldStream = radioStreams.put(radioData.getId(), radioStream);
            if (oldStream != null) {
                oldStream.close();
                Radio.LOGGER.warn("Replacing radio stream for '{}' ({})", radioData.getStationName(), radioData.getId());
            }
        }
    }

    public static boolean isValidRadioLocation(UUID id, BlockPos pos, ServerLevel level) {
        if (!level.isLoaded(pos)) {
            return false;
        }
        if (!(level.getBlockEntity(pos) instanceof SkullBlockEntity skullBlockEntity)) {
            return false;
        }
        @Nullable GameProfile ownerProfile = skullBlockEntity.getOwnerProfile();
        RadioData radioData = RadioData.fromGameProfile(ownerProfile);
        return radioData != null && radioData.getId().equals(id);
    }

    public void onRemoveHead(UUID id) {
        RadioStream radioStream = radioStreams.remove(id);
        if (radioStream != null) {
            radioStream.close();
            Radio.LOGGER.debug("Removed radio stream for '{}' ({})", radioStream.getRadioData().getStationName(), radioStream.getRadioData().getId());
        } else {
            Radio.LOGGER.debug("Removed radio stream {}", id);
        }
    }

    public void stopStream(UUID id) {
        RadioStream radioStream = radioStreams.get(id);
        if (radioStream != null) {
            radioStream.stop();
        }
    }

    public void updateHeadOnState(UUID id, boolean on) {
        RadioStream radioStream = radioStreams.get(id);
        if (radioStream == null) {
            return;
        }
        if (on) {
            radioStream.start();
        } else {
            radioStream.stop();
        }
    }

    public void onChunkUnload(ServerLevel serverLevel, LevelChunk levelChunk) {
        radioStreams.values().removeIf(radioStream -> {
            boolean remove = radioStream.getServerLevel().dimension().equals(serverLevel.dimension()) && isInChunk(radioStream.getPosition(), levelChunk.getPos());
            if (remove) {
                radioStream.close();
            }
            return remove;
        });
    }

    private static boolean isInChunk(BlockPos pos, ChunkPos chunkPos) {
        int chunkX = SectionPos.blockToSectionCoord(pos.getX());
        int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
        return chunkX == chunkPos.x && chunkZ == chunkPos.z;
    }

    public void clear() {
        radioStreams.values().forEach(RadioStream::close);
        radioStreams.clear();
    }

    private static RadioManager instance;

    public static RadioManager getInstance() {
        if (instance == null) {
            instance = new RadioManager();
        }
        return instance;
    }
}
