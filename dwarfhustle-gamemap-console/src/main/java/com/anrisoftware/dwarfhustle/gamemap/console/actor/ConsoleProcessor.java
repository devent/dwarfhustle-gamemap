package com.anrisoftware.dwarfhustle.gamemap.console.actor;

/**
 * Processes one line in the language parser and creates messages.
 *
 * @author Erwin Müller
 */
public interface ConsoleProcessor {

	/**
	 * Process the line in the language parser and creates messages.
	 */
	void process(String line);
}
