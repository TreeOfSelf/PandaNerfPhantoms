package me.TreeOfSelf.PandaNerfPhantoms.mixin;

import me.TreeOfSelf.PandaNerfPhantoms.PandaNerfPhantoms;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.spawner.PhantomSpawner;
import net.minecraft.world.spawner.SpecialSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@Mixin(PhantomSpawner.class)
public abstract class PhantomSpawnerMixin implements SpecialSpawner {
	@Shadow private int cooldown;

	@Inject(method = "spawn", at = @At(value = "HEAD"), cancellable = true)
	public void spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals, CallbackInfo ci) {
		if (!spawnMonsters) {
			ci.cancel();
			return;
		}

		if (!world.getGameRules().getBoolean(GameRules.DO_INSOMNIA)) {
			ci.cancel();
			return;
		}

		Random random = world.random;
		--this.cooldown;

		if (this.cooldown > 0) {
			ci.cancel();
			return;
		}

		this.cooldown += (120 + random.nextInt(600)) * 20;

		if (world.getAmbientDarkness() < 5 && world.getDimension().hasSkyLight()) {
			ci.cancel();
			return;
		}

        for (ServerPlayerEntity serverPlayerEntity : world.getPlayers()) {
            if (!serverPlayerEntity.isSpectator()) {
                BlockPos blockPos = serverPlayerEntity.getBlockPos();

                if (!world.getDimension().hasSkyLight() || (blockPos.getY() >= world.getSeaLevel() && world.isSkyVisible(blockPos))) {
                    LocalDifficulty localDifficulty = world.getLocalDifficulty(blockPos);

                    if (localDifficulty.isHarderThan(random.nextFloat() * 3.0F)) {
                        ServerStatHandler serverStatHandler = serverPlayerEntity.getStatHandler();
                        int j = MathHelper.clamp(serverStatHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);

                        if (random.nextInt(j) >= PandaNerfPhantoms.CONFIG.getInsomniaThresholdTicks()) {
                            BlockPos spawnPos = blockPos.up(20 + random.nextInt(15)).east(-10 + random.nextInt(21)).south(-10 + random.nextInt(21));
                            BlockState blockState = world.getBlockState(spawnPos);
                            FluidState fluidState = world.getFluidState(spawnPos);

                            if (SpawnHelper.isClearForSpawn(world, spawnPos, blockState, fluidState, EntityType.PHANTOM)) {
                                EntityData entityData = null;
                                int l = 1 + random.nextInt(localDifficulty.getGlobalDifficulty().getId() + 1);

                                for (int m = 0; m < l; ++m) {
                                    PhantomEntity phantomEntity = EntityType.PHANTOM.create(world,SpawnReason.EVENT);

                                    if (phantomEntity != null) {
                                        phantomEntity.refreshPositionAndAngles(spawnPos, 0.0F, 0.0F);
                                        entityData = phantomEntity.initialize(world, localDifficulty, SpawnReason.NATURAL, entityData);
                                        world.spawnEntityAndPassengers(phantomEntity);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

		ci.cancel();
	}
}
