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

import info.varden.andesite.core.Action;
import info.varden.andesite.core.ActionData;
import info.varden.andesite.core.ActionExecutionContext;
import info.varden.andesite.core.AndesiteProject;
import info.varden.andesite.core.wrapper.BlockWrapper;
import info.varden.andesite.io.AndesiteIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.Logger;

/**
 * The Andesite Mod Loader main mod class for Minecraft Forge.
 * @author Marius
 */
@Mod(modid = AndesiteML.MODID, name = AndesiteML.NAME, version = AndesiteML.VERSION)
public class AndesiteML {
    /**
     * Andesite Mod Loader mod ID.
     */
    public static final String MODID = "andesite";
    /**
     * Andesite Mod Loader mod name.
     */
    public static final String NAME = "Andesite Mod Loader";
    /**
     * Andesite Mod Loader version.
     */
    public static final String VERSION = "0.0.1 Dev Alpha";
    
    /**
     * Current Andesite version number.
     */
    public static final int ANDESITE_VERSION = 1;
    
    /**
     * Main logger.
     */
    private static Logger andesiteLog = null;
    /**
     * Currently loading Andesite mod.
     */
    private static String currentlyLoading = null;
    
    /**
     * Andesite Mod Loader pre-initialization.
     * @param event FML pre-initialization event
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        andesiteLog = event.getModLog();
    }
    
    /**
     * Andesite Mod Loader post-initialization.
     * @param event FML post-initialization event
     */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        File[] mods = getModDir().listFiles();
        for (File mod : mods) {
            if (mod.getName().endsWith(".anp")) {
                currentlyLoading = null;
                AndesiteML.info("Reading mod " + mod.getName());
                try {
                    AndesiteProject project = AndesiteIO.readSignedPackage(mod);
                    currentlyLoading = project.properties.modid;
                    Action[] actions = project.getAllActions();
                    AndesiteML.info("Found " + actions.length + " actions");
                    for (int i = 0; i < actions.length; i++) {
                        AndesiteML.info("Executing action " + (i + 1) + " of " + actions.length);
                        if (actions[i].getClass().getAnnotation(ActionData.class).version() > ANDESITE_VERSION) {
                            AndesiteML.warn("Action " + (i + 1) + " is not supported in this version of Andesite Mod Loader. This might cause trouble.");
                        }
                        actions[i].execute(new ActionExecutionContext() {

                            /**
                             * Returns an Andesite block wrapper for the given block ID.
                             * @param block The block ID to wrap
                             * @return A block wrapper for use by the Andesite Project
                             */
                            @Override
                            public BlockWrapper getBlockWrapperFor(String block) {
                                return new ForgeBlockWrapper();
                            }
                            
                        });
                    }
                } catch (InvalidKeyException e) {
                    AndesiteML.error("Failed to read mod package: Package is corrupt: Public key invalid");
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    AndesiteML.error("Failed to read mod package: File not found?! It was there a second ago, I swear!! (You shouldn't delete mods while they're being loaded...)");
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    AndesiteML.error("Failed to read mod package: Package is corrupt: Not in Andesite Mod Package format, or an action in the package is corrupt");
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    AndesiteML.error("Failed to read mod package: Missing cryptographic algorithms: Your computer is highly insecure and incapable of reading Andesite mods.");
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    AndesiteML.error("Failed to read mod package: Missing cryptographic algorithms: Your computer is highly insecure and incapable of reading Andesite mods.");
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    AndesiteML.error("Failed to read mod package: Missing cryptographic algorithms: Your computer is highly insecure and incapable of reading Andesite mods.");
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    AndesiteML.error("Failed to read mod package: Package is corrupt: Block size invalid");
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    AndesiteML.error("Failed to read mod package: Package is corrupt: The package data is not padded properly");
                    e.printStackTrace();
                } catch (ClassCastException e) {
                    AndesiteML.error("Failed to read mod package: Package is corrupt: Action is not an Action");
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    AndesiteML.error("Failed to read mod package: Andesite Mod Loader is broken: Can not instantiate Action - this is an Andesite Mod Loader bug, please report it");
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    AndesiteML.error("Failed to read mod package: Andesite Mod Loader is broken: Can not access Action - this is an Andesite Mod Loader bug, please report it");
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    AndesiteML.error("Failed to read mod package: Andesite Mod Loader is broken: Action constructor fails to invoke - this is an Andesite Mod Loader bug, please report it");
                    e.printStackTrace();
                } catch (SecurityException e) {
                    AndesiteML.error("Failed to read mod package: Andesite Mod Loader is broken: Action cannot be read - this is an Andesite Mod Loader bug, please report it");
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    AndesiteML.error("Failed to read mod package: Andesite Mod Loader is broken: Action does not support instantiation - this is an Andesite Mod Loader bug, please report it");
                    e.printStackTrace();
                } catch (SignatureException e) {
                    AndesiteML.error("Failed to read mod package: Impersonation: Hash does not match content - SOMEONE HAS TAMPERED WITH THIS MOD!! Mod will not be loaded.");
                    e.printStackTrace();
                } catch (IOException e) {
                    AndesiteML.error("Failed to read mod package: Input/output error: Unspecified error while reading the file. Please ensure you did not disconnect or remove the media the mod file was stored on while the game was loading");
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Gets the line prefix for the main logger.
     * @return Line prefix
     */
    private static String getLogPrefix() {
        if (currentlyLoading == null) {
            return "<core>: ";
        }
        return "[" + currentlyLoading + "] ";
    }
    
    /**
     * Writes an information level message to the logger.
     * @param msg The message to write
     */
    public static void info(String msg) {
        andesiteLog.info(getLogPrefix() + msg);
    }
    
    /**
     * Writes a warning level message to the logger.
     * @param msg The message to write
     */
    public static void warn(String msg) {
        andesiteLog.warn(getLogPrefix() + msg);
    }
    
    /**
     * Writes an error level message to the logger.
     * @param msg The message to write
     */
    public static void error(String msg) {
        andesiteLog.error(getLogPrefix() + msg);
    }
    
    /**
     * Returns the Andesite Mod directory.
     * @return Directory containing Andesite mods
     */
    private static File getModDir() {
        return new File(Minecraft.getMinecraft().mcDataDir, "mods");
    }
}
