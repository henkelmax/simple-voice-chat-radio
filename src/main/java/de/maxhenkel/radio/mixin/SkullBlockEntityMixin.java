package de.maxhenkel.radio.mixin;

import com.mojang.authlib.GameProfile;
import de.maxhenkel.radio.radio.RadioManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkullBlockEntity.class)
public class SkullBlockEntityMixin extends BlockEntity {

    public SkullBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "setOwner", at = @At("RETURN"))
    public void setOwner(GameProfile gameProfile, CallbackInfo ci) {
        if (level != null && !level.isClientSide) {
            RadioManager.getInstance().onLoadHead((SkullBlockEntity) (Object) this);
        }
    }

    @Inject(method = "load", at = @At("RETURN"))
    public void load(CompoundTag compoundTag, CallbackInfo ci) {
        if (level != null && !level.isClientSide) {
            RadioManager.getInstance().onLoadHead((SkullBlockEntity) (Object) this);
        }
    }

    @Override
    public void setLevel(Level newLevel) {
        Level oldLevel = level;
        super.setLevel(newLevel);
        if (oldLevel == null && newLevel != null && !newLevel.isClientSide) {
            RadioManager.getInstance().onLoadHead((SkullBlockEntity) (Object) this);
        }
    }
}
