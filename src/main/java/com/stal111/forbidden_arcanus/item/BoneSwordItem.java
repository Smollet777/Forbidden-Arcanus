package com.stal111.forbidden_arcanus.item;

import com.stal111.forbidden_arcanus.util.ModUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BoneSwordItem extends SwordItem {

    public BoneSwordItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos blockpos = context.getPos();
        BlockPos blockpos1 = blockpos.offset(context.getFace());
        PlayerEntity playerEntity = context.getPlayer();
        if (ModUtils.applyBonemeal(context.getItem(), world, blockpos, context.getPlayer())) {
            if (!world.isRemote) {
                world.playEvent(2005, blockpos, 0);
                if (playerEntity != null) {
                    context.getItem().damageItem(1, playerEntity, (p_220041_1_) -> {
                        p_220041_1_.sendBreakAnimation(context.getHand());
                    });
                }
            }

            return ActionResultType.SUCCESS;
        } else {
            BlockState blockstate = world.getBlockState(blockpos);
            boolean flag = blockstate.func_224755_d(world, blockpos, context.getFace());
            if (flag && ModUtils.growSeagrass(context.getItem(), world, blockpos1, context.getFace())) {
                if (!world.isRemote) {
                    world.playEvent(2005, blockpos1, 0);
                    if (playerEntity != null) {
                        context.getItem().damageItem(1, playerEntity, (p_220041_1_) -> {
                            p_220041_1_.sendBreakAnimation(context.getHand());
                        });
                    }
                }

                return ActionResultType.SUCCESS;
            } else {
                return ActionResultType.PASS;
            }
        }
    }
}
