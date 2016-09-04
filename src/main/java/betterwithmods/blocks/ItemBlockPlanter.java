package betterwithmods.blocks;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockPlanter extends ItemBlockMeta
{
	public ItemBlockPlanter(Block block)
	{
		super(block);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}
	
	@SideOnly(Side.CLIENT)
	public int getColorFromItemstack(ItemStack stack, int colorIndex)
	{
		if(stack.getItemDamage() == 2 && block instanceof BlockPlanter)
			return ((BlockPlanter)block).colorMultiplier(block.getStateFromMeta(stack.getItemDamage()), null, null, colorIndex);
		return -1;
	}
}
