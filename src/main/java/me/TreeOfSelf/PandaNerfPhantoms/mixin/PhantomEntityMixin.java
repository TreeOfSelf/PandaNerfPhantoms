package me.TreeOfSelf.PandaNerfPhantoms.mixin;

import me.TreeOfSelf.PandaNerfPhantoms.PandaNerfPhantoms;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Phantom.class)
public abstract class PhantomEntityMixin {

	@Inject(method = "tick", at = @At("HEAD"))
	private void pandaNerfPhantoms$tick(CallbackInfo ci) {
		if (!PandaNerfPhantoms.CONFIG.burnPhantoms) {
			return;
		}
		Phantom phantom = (Phantom) (Object) this;
		if (phantom.hasCustomName()) {
			return;
		}
		LivingEntity target = phantom.getTarget();
		if (target instanceof Player player && player instanceof ServerPlayer serverPlayer) {
			ServerStatsCounter stats = serverPlayer.getStats();
			int rested = Mth.clamp(stats.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
			if (rested < PandaNerfPhantoms.CONFIG.getInsomniaThresholdTicks()) {
				if (phantom.getRandom().nextInt(10) == 0) {
					phantom.igniteForTicks(10);
				}
			}
		}
	}
}
