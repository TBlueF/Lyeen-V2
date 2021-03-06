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

package de.craftednature.lyeen;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin (
	id = LyeenPlugin.PLUGIN_ID,
	name = LyeenPlugin.PLUGIN_NAME,
	version = LyeenPlugin.PLUGIN_VERSION
)

public class LyeenPlugin {
	
	public static final String PLUGIN_ID = "lyeen";
	public static final String PLUGIN_NAME = "Lyeen";
	public static final String PLUGIN_VERSION = "7-0.1";
	
	public static LyeenPlugin instance;
	
	@Inject private Logger log;
	
	public void init(){
		instance = this;
	}

	@Listener(order = Order.DEFAULT)
	public void onServerStart(GameStartingServerEvent evt){
		init();
	}
	
	public static LyeenPlugin getInstance(){
		return instance;
	}
	
	public static Logger getLogger(){
		return instance.log;
	}
	
}
