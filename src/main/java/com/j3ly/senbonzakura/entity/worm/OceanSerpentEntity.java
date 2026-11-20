package com.j3ly.senbonzakura.entity.worm;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

/** Water-only copy of the original phantom-style serpent. */
public class OceanSerpentEntity extends WormEntity {
    public OceanSerpentEntity(EntityType<? extends WormEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public WormVariant variant() {
        return WormVariant.OCEAN;
    }

    public static boolean checkOceanSpawnRules(EntityType<OceanSerpentEntity> type,
                                               ServerLevelAccessor level,
                                               MobSpawnType spawnType,
                                               BlockPos pos,
                                               RandomSource random) {
        return level.getFluidState(pos).is(FluidTags.WATER)
                && Monster.checkAnyLightMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    @Override
    public void tick() {
        super.tick();
        // If it ever ends up on land, dive back under the surface.
        if (!this.isInWater()) {
            this.moveTargetPoint = new Vec3(this.getX(), this.level().getSeaLevel() - 6.0, this.getZ());
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.08, 0.0));
        }
    }
}
