/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.varden.andesite.modloader;

import info.varden.andesite.core.wrapper.AndesiteItemStack;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 *
 * @author Marius
 */
public abstract class ForgeItemStackTranslator {
    public static AndesiteItemStack translate(ItemStack stack) {
        return AndesiteItemStack.create(GameRegistry.findUniqueIdentifierFor(stack.getItem()).toString(), stack.stackSize, stack.getMetadata());
    }
    
    public static ItemStack translate(AndesiteItemStack stack) {
        if (GameData.getItemRegistry().containsKey(stack.getID())) {
            return new ItemStack(Item.getByNameOrId(stack.getID()), stack.getAmount(), stack.getMeta());
        } else if (GameData.getBlockRegistry().containsKey(stack.getID())) {
            return new ItemStack(Block.getBlockFromName(stack.getID()), stack.getAmount(), stack.getMeta());
        } else {
            AndesiteML.error("The item name " + stack.getID() + " is not present as an item or block on this Minecraft installation!");
            return new ItemStack(Block.getBlockFromName("minecraft:air"), 0);
        }
    }
}
