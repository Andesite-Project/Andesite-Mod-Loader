/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.varden.andesite.modloader;

import info.varden.andesite.core.wrapper.AndesiteItemStack;
import net.minecraft.item.ItemStack;

/**
 *
 * @author Marius
 */
public abstract class ForgeItemStackTranslator {
    public static AndesiteItemStack translate(ItemStack stack) {
        return null;
    }
    
    public static ItemStack translate(AndesiteItemStack stack) {
        return null;
    }
}
