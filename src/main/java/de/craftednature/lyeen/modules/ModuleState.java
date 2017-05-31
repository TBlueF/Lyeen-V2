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