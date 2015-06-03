/*
 * The MIT License
 *
 * Copyright 2015 Marius.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.varden.andesite.modloader;

import info.varden.andesite.core.wrapper.PlayerWrapper;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Wrapper for EntityPlayer.
 * @author Marius
 */
public class ForgePlayerWrapper implements PlayerWrapper {
    
    /**
     * The wrapped player.
     */
    private final EntityPlayer player;
    
    /**
     * Wraps a player entity and creates a PlayerWrapper.
     * @param player The player to wrap.
     */
    private ForgePlayerWrapper(EntityPlayer player) {
        this.player = player;
    }
    
    /**
     * Creates a PlayerWrapper for the given player.
     * @param player Player entity to wrap.
     * @return A PlayerWrapper instance.
     */
    public static ForgePlayerWrapper getFor(EntityPlayer player) {
        return new ForgePlayerWrapper(player);
    }
    
    /**
     * Gets the absorption amount of the player.
     * @return Absorption amount.
     */
    @Override
    public float getAbsorptionAmount() {
        return this.player.getAbsorptionAmount();
    }
    
    /**
     * Sets the absorption amount for the player.
     * @param amount The absorption amount to set.
     * @return The PlayerWrapper instance.
     */
    @Override
    public ForgePlayerWrapper setAbsorptionAmount(float amount) {
        this.player.setAbsorptionAmount(amount);
        return this;
    }
}
