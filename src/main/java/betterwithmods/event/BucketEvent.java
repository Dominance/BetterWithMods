package betterwithmods.event;

import betterwithmods.config.BWConfig;
import betterwithmods.util.DispenserBehaviorFiniteWater;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.RegistryDefaulted;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BucketEvent {

    public static void editModdedFluidDispenseBehavior() {
        if (!BWConfig.hardcoreFluidContainer)
            return;
        RegistryDefaulted<Item, IBehaviorDispenseItem> reg = BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY;
        for (Item item : Item.REGISTRY) {
            if (isFluidContainer(new ItemStack(item))) {
                if (reg.getObject(item) instanceof DispenseFluidContainer)
                    BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item, new DispenserBehaviorFiniteWater());
            }
        }
    }

    private static boolean isFluidContainer(ItemStack stack) {
        return stack != null && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
    }

    private static boolean isNonVanillaBucket(ItemStack stack) {
        return stack.getItem() != Items.BUCKET && stack.getItem() != Items.WATER_BUCKET && stack.getItem() != Items.LAVA_BUCKET && stack.getItem() != Items.MILK_BUCKET && isFluidContainer(stack);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void fluidContainerUse(FillBucketEvent evt) {
        if (!BWConfig.hardcoreBuckets && !BWConfig.hardcoreFluidContainer)
            return;

        if (evt.getEmptyBucket() == null) {
            evt.setResult(Event.Result.DENY);
            return;
        }

        FluidStack fluid = FluidUtil.getFluidContained(evt.getEmptyBucket());

        RayTraceResult target = evt.getTarget();
        EntityPlayer player = evt.getEntityPlayer();
        boolean dimValid = evt.getWorld().provider.getDimensionType() == DimensionType.OVERWORLD;

        if (dimValid) {
            if (target != null && target.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos pos = target.getBlockPos().offset(target.sideHit);
                IBlockState state = evt.getWorld().getBlockState(pos);

                /*if (fluid == null || fluid.getFluid() == null) {
                    TODO: Find a way to fill a bucket from flowing water
                    if (isWater(state) && state.getBlock().getMetaFromState(state) > 0) {
                        if (!player.capabilities.isCreativeMode) {
                            if (evt.getEmptyBucket().getItem() == Items.BUCKET && BWConfig.hardcoreBuckets) {
                                evt.setFilledBucket(new ItemStack(Items.WATER_BUCKET));
                            }
                            else if (isFluidContainer(evt.getEmptyBucket()) && BWConfig.hardcoreFluidContainer) {
                                ItemStack fill = evt.getEmptyBucket().copy();
                                fill.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).fill(new FluidStack(FluidRegistry.WATER, 1000), true);
                                evt.setFilledBucket(fill);
                            }
                            evt.setResult(Event.Result.ALLOW);
                        }
                    }
                }
                else */if (fluid != null && fluid.getFluid() != null && fluid.getFluid() == FluidRegistry.WATER) {
                    if (state.getBlock().isAir(state, evt.getWorld(), pos) || state.getBlock().isReplaceable(evt.getWorld(), pos)) {
                        if (!player.capabilities.isCreativeMode) {
                            if ((evt.getEmptyBucket().getItem() == Items.WATER_BUCKET && BWConfig.hardcoreBuckets) || (isNonVanillaBucket(evt.getEmptyBucket()) && BWConfig.hardcoreFluidContainer))
                                bucketEmpty(evt, pos, evt.getEmptyBucket());
                        }
                    }
                }
            }
        }
    }

    private void bucketEmpty(FillBucketEvent evt, BlockPos pos, ItemStack equip) {
        if (isWater(evt.getWorld().getBlockState(pos))) {
            IBlockState state = evt.getWorld().getBlockState(pos);
            if (state.getBlock().getMetaFromState(state) > 0) {
                emptyBucket(evt, pos, equip);
            }
        }
        else {
            emptyBucket(evt, pos, equip);
        }
    }

    private void emptyBucket(FillBucketEvent evt, BlockPos pos, ItemStack equip) {
        Item item = equip.getItem();
        evt.getWorld().setBlockState(pos, Blocks.FLOWING_WATER.getStateFromMeta(2));
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos p2 = pos.offset(facing);
            if (!isWater(evt.getWorld().getBlockState(p2)) && (evt.getWorld().getBlockState(p2).getBlock().isAir(evt.getWorld().getBlockState(p2), evt.getWorld(), p2) || evt.getWorld().getBlockState(p2).getBlock().isReplaceable(evt.getWorld(), p2)))
                evt.getWorld().setBlockState(p2, Blocks.FLOWING_WATER.getStateFromMeta(5));
        }
        evt.getWorld().playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        evt.setFilledBucket(item.getContainerItem(equip).copy());
        evt.setResult(Event.Result.ALLOW);
    }

    private boolean isWater (IBlockState state) {
        return state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.FLOWING_WATER;
    }

    @SubscribeEvent
    public void checkPlayerInventory(TickEvent.PlayerTickEvent e) {
        World world = e.player.getEntityWorld();
        if (BWConfig.hardcoreLavaBuckets) {
            if (world.getTotalWorldTime() % 10 == 0) {
                if (!e.player.isPotionActive(MobEffects.FIRE_RESISTANCE)) {
                    IItemHandler inv = e.player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                    BlockPos pos = e.player.getPosition();
                    for (int i = 0; i < inv.getSlots(); i++) {
                        ItemStack stack = inv.getStackInSlot(i);
                        if (world.rand.nextInt(50) == 0) {
                            if (stack != null && stack.isItemEqual(new ItemStack(Items.LAVA_BUCKET))) {
                                IFluidHandler bucket = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                                bucket.drain(1000, true);
                                world.playSound(e.player, pos, SoundEvents.ITEM_BUCKET_EMPTY_LAVA, SoundCategory.PLAYERS, 1, 1);
                                placeLavaBucket(world, pos.offset(e.player.getHorizontalFacing()), 0);
                            }
                        }
                    }
                }
            }
        }
    }

    public void placeLavaBucket(World world, BlockPos pos, int depth) {
        if (depth >= 5)
            return;
        if (world.isAirBlock(pos)) {
            world.setBlockState(pos, Blocks.FLOWING_LAVA.getDefaultState());
        } else {
            placeLavaBucket(world, pos.offset(EnumFacing.VALUES[world.rand.nextInt(6)]), depth++);
        }
    }

    @SubscribeEvent
    public void onFillBucket(FillBucketEvent e) {
        if (BWConfig.hardcoreLavaBuckets) {
            if (e.getEntityPlayer().isPotionActive(MobEffects.FIRE_RESISTANCE))
                return;
            if (e.getTarget() != null && e.getTarget().getBlockPos() != null) {
                Block block = e.getWorld().getBlockState(e.getTarget().getBlockPos()).getBlock();
                if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
                    e.getEntityPlayer().attackEntityFrom(DamageSource.lava, 1);
                    e.getWorld().playSound(null, e.getTarget().getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1, 1.5f);
                    e.setCanceled(true);
                }
            }
        }
    }
}
