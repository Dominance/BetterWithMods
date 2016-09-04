package betterwithmods.blocks;

import betterwithmods.BWRegistry;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class BlockGen extends BTWBlock
{
    public static final PropertyBool ISACTIVE = PropertyBool.create("ison");

    public BlockGen(Material material, String name)
    {
        super(material, name);
        this.setSoundType(SoundType.WOOD);
    }

    public abstract ItemStack getGenStack(IBlockState state);

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        List<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(new ItemStack(BWRegistry.axle));
        drops.add(getGenStack(state));
        return drops;
    }

    public abstract EnumFacing getAxleDirection(World world, BlockPos pos);
}
