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
