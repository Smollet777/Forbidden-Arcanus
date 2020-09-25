package com.stal111.forbidden_arcanus.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class QuantumCatcherItem extends Item {

    public QuantumCatcherItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        ItemStack stack = player.getHeldItem(context.getHand());
        World world = context.getWorld();

        if (getEntity(stack, world) != null) {
            Entity entity = getEntity(stack, world);

            if (!world.isRemote()) {
                entity.setPosition(context.getPos().getX(), context.getPos().getY() + 1, context.getPos().getZ());

                world.addEntity(entity);
            }

            clearEntity(stack);

            return ActionResultType.func_233537_a_(world.isRemote());
        }

        return super.onItemUse(context);
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
        World world = player.world;
        ItemStack stack1 = player.getHeldItem(hand);

        if (getEntity(stack1, world) == null && target.isAlive()) {
            setEntity(target, stack1);
            if (!world.isRemote()) {
                target.remove();
            }

            return ActionResultType.func_233537_a_(player.world.isRemote());
        }

        return super.itemInteractionForEntity(stack1, player, target, hand);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (world != null && getEntity(stack, world) != null)  {
            Entity entity = getEntity(stack, world);

            if (entity != null) {
                IFormattableTextComponent textComponent = new TranslationTextComponent("tooltip.forbidden_arcanus.entity").appendString(": ").append(new StringTextComponent(Objects.requireNonNull(entity.getType().getRegistryName()).toString()));

                if (entity.hasCustomName()) {
                    textComponent.appendString(" (").append(Objects.requireNonNull(entity.getCustomName())).appendString(")");
                }

                textComponent.mergeStyle(TextFormatting.GRAY);

                tooltip.add(textComponent);
            }
        }
    }

    private void setEntity(Entity entity, ItemStack stack) {
        entity.stopRiding();
        entity.removePassengers();

        CompoundNBT entityNBT = new CompoundNBT();
        entity.writeUnlessPassenger(entityNBT);

        CompoundNBT itemNBT = stack.getOrCreateTag();
        itemNBT.put("entity", entityNBT);
    }

    private Entity getEntity(ItemStack stack, World world) {
        CompoundNBT itemNBT = stack.getTag();

        if (itemNBT == null) {
            return null;
        }

        CompoundNBT entityNBT = itemNBT.getCompound("entity");

        Optional<Entity> entity = EntityType.loadEntityUnchecked(entityNBT, world);
        return entity.orElse(null);
    }

    private void clearEntity(ItemStack stack) {
        CompoundNBT compoundNBT = stack.getTag();

        if (compoundNBT != null) {
            compoundNBT.remove("entity");
        }
    }
}