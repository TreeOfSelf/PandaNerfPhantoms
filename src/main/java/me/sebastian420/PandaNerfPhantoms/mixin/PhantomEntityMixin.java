package me.sebastian420.PandaNerfPhantoms.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PhantomEntity.class)
public abstract class PhantomEntityMixin extends LivingEntity {

    protected PhantomEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "mobTick", at = @At(value = "HEAD"))
    protected void mobTick(CallbackInfo ci) {
        PhantomEntity phantomEntity = (PhantomEntity) (Object) this;
        LivingEntity target = phantomEntity.getTarget();
        if (target instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) target;
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) playerEntity;
            ServerStatHandler serverStatHandler = serverPlayerEntity.getStatHandler();
            int j = MathHelper.clamp(serverStatHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
            if (j < 168000) {
                if (!this.hasCustomName()) {
                    if (this.random.nextInt(10) == 0) {
                        this.setOnFireFor(10);
                    }
                }
            }
        }

    }
}
