package betterwithmods.common.registry;

import betterwithmods.BWMod;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.*;

/**
 * Purpose:
 *
 * @author Tyler Marshall
 * @version 11/11/16
 */
public abstract class BlockMetaHandler {

    private final ArrayList<BlockMetaRecipe> recipes = Lists.newArrayList();
    private final String type;

    public BlockMetaHandler(String type) {
        this.type = type;
    }

    public void addRecipe(ItemStack input, ItemStack... products) {
        if (input != ItemStack.EMPTY && input.getItem() instanceof ItemBlock) {
            addRecipe(((ItemBlock) input.getItem()).getBlock(), input.getMetadata(), products);
        } else {
            BWMod.logger.info("BlockMeta input: %s must be a block", input);
        }
    }

    public void addRecipe(Block block, int meta, ItemStack... products) {
        addRecipe(new BlockMetaRecipe(type, block, meta, Arrays.asList(products)));
    }

    public void addRecipe(BlockMetaRecipe recipe) {
        recipes.add(recipe);
    }

    public boolean contains(ItemStack stack) {
        return !(stack == null || stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) && contains(((ItemBlock) stack.getItem()).getBlock(), stack.getMetadata());
    }

    public boolean contains(Block block, int meta) {
        return recipes.stream().filter(r -> r.equals(block, meta)).findFirst().isPresent();
    }

    public ArrayList<BlockMetaRecipe> getRecipes() {
        return recipes;
    }

    public BlockMetaRecipe getRecipe(ItemStack stack) {
        if (stack == null)
            return null;
        assert stack.getItem() instanceof ItemBlock;
        return getRecipe(((ItemBlock) stack.getItem()).getBlock(), stack.getMetadata());
    }

    public BlockMetaRecipe getRecipe(Block block, int meta) {
        Optional<BlockMetaRecipe> recipe = recipes.stream().filter(r -> r.equals(block, meta)).findFirst();
        if (recipe.isPresent())
            return recipe.get();
        return null;
    }

    public List<ItemStack> getProducts(Block block, int meta) {
        BlockMetaRecipe recipe = getRecipe(block, meta);
        if (recipe != null)
            return recipe.getOutputs();
        return null;
    }

    public List<BlockMetaRecipe> removeRecipes(ItemStack input) {
        List<BlockMetaRecipe> removed = Lists.newArrayList();
        for (BlockMetaRecipe ir : recipes) {
            if (ir.getStack().isItemEqual(input)) {
                removed.add(ir);
            }
        }
        return removed;
    }

}
