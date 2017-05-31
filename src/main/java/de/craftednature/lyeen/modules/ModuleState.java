package de.craftednature.lyeen.modules;

/**
 * Represents the state of a {@link Module}
 */
public enum ModuleState {
	
	/**
	 * Initial state of every {@link Module}.<br>
	 * <br>
	 * {@link Module#init()} and {@link Module#load()} have not been called yet.
	 */
	CONSTRUCTED,
	
	/**
	 * The {@link Module} is initialized and ready to be started and used.<br>
	 * <br>
	 * {@link Module#init()} <i>(and {@link Module#load()})</i> has been called.<br>
	 * {@link Module#start()} has not been called yet.
	 */
	INITIALIZED,
	
	/**
	 * The {@link Module} is initialized and started.<br>
	 * <br>
	 * {@link Module#start()} has been called.
	 * {@link Module#stop()} has not been called yet.
	 */
	STARTED,
	
	/**
	 * The {@link Module} is stopped.<br>
	 * <br>
	 * {@link Module#stop()} has been called.
	 */
	STOPPED
}