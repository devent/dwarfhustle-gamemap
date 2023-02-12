package com.anrisoftware.dwarfhustle.gamemap.model.messages;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

/**
 *
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public interface AppCommand {

	File getGamedir();

	boolean isSkipLoad();

	String getRemoteServer();

	String getRemoteUser();

	String getRemotePassword();

	String getRemoteDatabase();

	default boolean isUseRemoteServer() {
		return StringUtils.isNotBlank(getRemoteServer());
	}
}
