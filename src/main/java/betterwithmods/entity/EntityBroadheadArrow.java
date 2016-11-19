package betterwithmods.entity;

import betterwithmods.BWMItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Purpose:
 *
 * @author Tyler Marshall
 * @version 11/18/16
 */
public class EntityBroadheadArrow extends EntityArrow {

    public EntityBroadheadArrow(World worldIn) {
        super(worldIn);
        setDamage(10);
    }

    public EntityBroadheadArrow(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityBroadheadArrow(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(BWMItems.BROADHEAD_ARROW);
    }
}
