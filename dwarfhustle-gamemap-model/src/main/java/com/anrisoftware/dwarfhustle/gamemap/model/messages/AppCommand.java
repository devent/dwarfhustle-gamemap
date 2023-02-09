package com.anrisoftware.dwarfhustle.gamemap.model.messages;

import java.io.File;

/**
 *
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public interface AppCommand {

	File getGamedir();

	boolean isSkipLoad();
}
