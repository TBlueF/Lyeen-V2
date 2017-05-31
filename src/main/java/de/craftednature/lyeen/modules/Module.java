/*
 * The MIT License (MIT)
 * 
 * Copyright (c) Blue <https://www.bluecolored.de>
 * Copyright (c) CraftedNature <https://www.craftednature.de>
 * Copyright (c) contributors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.craftednature.lyeen.modules;

import java.io.IOException;

import de.craftednature.lyeen.LyeenPlugin;

public interface Module {

	/**
	 * This method is called once on startup to initialize this module.
	 * 
	 * @throws Throwable if anything goes wrong during initialisation. The module cant be initialized again if this happens, you need to use a new instance!
	 */
	public default void init() throws Throwable {}
	
	/**
	 * This method is called once on startup and might get called later again to (re)load the configuration/data for this module.<br>
	 * <br>
	 * <b>If this method fails with an exception the module is still considererd to be in a working state! The implementation should always respect that.</b>
	 * 
	 * @throws IOException if something could not get loaded correctly
	 */
	public default void load() throws IOException {}
	
	/**
	 * This method is called to save all configuration/data.<br>
	 * <br>
	 * <b>If this method fails with an exception the module is still considererd to be in a working state! The implementation should always respect that.</b>
	 * 
	 * @throws IOException if something could not get saved correctly
	 */
	public default void save() throws IOException {}
	
	/**
	 * This method is called to start the module, after {@link #init()} and {@link #load()} have been called.
	 */
	public default void start(){}
	
	/**
	 * This method is called to stop the module
	 */
	public default void stop(){}
	
	/**
	 * A user-friendly name to identify this module.<br>
	 * This name can e.g. be used to display info or for logging.<br>
	 * This defaults to the simple-class-name of the modules class.
	 * 
	 * @return This modules name.
	 */
	public default String getName(){
		return this.getClass().getSimpleName();
	}
	
	/**
	 * This method should be used to log a message in the name of this module
	 */
	default void logInfo(String msg){
		LyeenPlugin.getLogger().info("[" + getName() + "] " + msg);
	}
	
	/**
	 * This method should be used to log a warning in the name of this module
	 */
	default void logWarning(String msg){
		LyeenPlugin.getLogger().warn("[" + getName() + "] " + msg);
	}
	
	/**
	 * This method should be used to log an error in the name of this module
	 */
	default void logError(String msg){
		LyeenPlugin.getLogger().error("[" + getName() + "] " + msg);
	}
	
	/**
	 * This method should be used to log an error in the name of this module
	 */
	default void logError(String msg, Throwable error){
		LyeenPlugin.getLogger().error("[" + getName() + "] " + msg, error);
	}
}
