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

import info.varden.andesite.core.BlockBreakSource;
import info.varden.andesite.core.PlayerRequirements;
import info.varden.andesite.core.SilkTouchMode;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameData;

/**
 * Wrapper for Minecraft blocks.
 * @author Marius
 */
public class BlockWrapper {
    
    /**
     * The wrapped block.
     */
    private final Block block;
    
    /**
     * Initializes the block wrapper.
     * @param base Base block to wrap
     */
    private BlockWrapper(Block base) {
        this.block = base;
    }
    
    /**
     * Gets a block wrapper for the block by the given ID.
     * @param name The ID of the block
     * @return A block wrapper for the given block
     */
    public static BlockWrapper getFor(String name) {
        return new BlockWrapper(GameData.getBlockRegistry().getObject(name));
    }
    
    /**
     * Gets the block light level.
     * @return The light level
     */
    public float getLightLevel() {
        return ((float) this.block.getLightValue()) / 15F;
    }
    
    /**
     * Sets the block light level.
     * @param value The light level to set
     * @return The BlockWrapper instance
     */
    public BlockWrapper setLightLevel(float value) {
        this.block.setLightLevel(value);
        return this;
    }
    
    /**
     * Gets the block step sound.
     * @return The step sound
     */
    public Block.SoundType getStepSound() {
        return this.block.stepSound;
    }
    
    /**
     * Sets the block step sound.
     * @param sound The step sound to set
     * @return The BlockWrapper instance
     */
    public BlockWrapper setStepSound(Block.SoundType sound) {
        this.block.setStepSound(sound);
        return this;
    }
    
    /**
     * Gets the block resistance value.
     * @return The resistance value
     */
    public float getResistance() {
        return this.block.getExplosionResistance(null) * 5F;
    }
    
    /**
     * Sets the block resistance value.
     * @param resistance The resistance value to set
     * @return The BlockWrapper instance
     */
    public BlockWrapper setResistance(float resistance) {
        this.block.setResistance(resistance / 3F);
        return this;
    }
    
    /**
     * Gets the block hardness.
     * @return The hardness value
     */
    public float getHardness() {
        return this.block.getBlockHardness(null, null);
    }
    
    /**
     * Sets the block hardness.
     * @param hardness The hardness value
     * @return The BlockWrapper instance
     */
    public BlockWrapper setHardness(float hardness) {
        this.block.setHardness(hardness);
        return this;
    }
    
    /**
     * Gets the block slipperiness.
     * @return The slipperiness value
     */
    public float getSlipperiness() {
        return this.block.slipperiness;
    }
    
    /**
     * Sets the block slipperiness.
     * @param value The slipperiness value
     * @return The BlockWrapper instance
     */
    public BlockWrapper setSlipperiness(float value) {
        this.block.slipperiness = value;
        return this;
    }
    
    /**
     * Gets the block particle gravity.
     * @return Particle gravitational force in meters per square second
     */
    public float getParticleGravity() {
        return this.block.blockParticleGravity;
    }
    
    /**
     * Sets the block particle gravity.
     * @param value Particle gravitational force to set in meters per square second
     * @return The BlockWrapper instance
     */
    public BlockWrapper setParticleGravity(float value) {
        this.block.blockParticleGravity = value;
        return this;
    }
    
    /**
     * Removes default drops for the block.
     * @return The BlockWrapper instance
     */
    public BlockWrapper removeDefaultDrops() {
        MinecraftForge.EVENT_BUS.register(new Object() {
            /**
             * Minecraft Forge block harvesting event handler.
             * @param event Minecraft Forge block harvesting event
             */
            @SubscribeEvent(priority = EventPriority.LOW)
            public void harvestDropsEvent(BlockEvent.HarvestDropsEvent event) {
                event.drops.clear();
                event.dropChance = 0.0F;
            }
        });
        return this;
    }
    
    /**
     * Adds conditional drops to the block.
     * @param mode Required silk touch mode
     * @param source Required block breaking source
     * @param fortuneLevel Required fortune level, or -1 for any level
     * @param items List of item stacks to drop
     * @param dropChance Chance of dropping each item stack
     * @param overrideDrops Whether or not to override existing drops when the given conditions are met
     * @param conditions Requirements for the player if a player broke the block
     * @return The BlockWrapper instance
     */
    public BlockWrapper setConditionalDrops(final SilkTouchMode mode, final BlockBreakSource source, final int fortuneLevel, final List<ItemStack> items, final float dropChance, final boolean overrideDrops, final PlayerRequirements conditions) {
        MinecraftForge.EVENT_BUS.register(new Object() {
            /**
             * Minecraft Forge block harvesting event handler.
             * @param event Minecraft Forge block harvesting event
             */
            @SubscribeEvent(priority = EventPriority.LOWEST)
            public void harvestDropsEvent(BlockEvent.HarvestDropsEvent event) {
                if (
                        (mode == SilkTouchMode.ANY || (mode == SilkTouchMode.SILKTOUCH) == event.isSilkTouching) &&
                        (fortuneLevel == -1 || (event.fortuneLevel == fortuneLevel)) &&
                        (source == BlockBreakSource.ANY || (source == BlockBreakSource.OTHER) == (event.harvester == null))
                    ) {
                    if (event.harvester != null && conditions != null && !conditions.satisfiedBy(event.harvester)) {
                        return;
                    }
                    event.dropChance = dropChance;
                    if (overrideDrops) {
                        event.drops.clear();
                    }
                    event.drops.addAll(items);
                }
            }
        });
        return this;
    }

}
