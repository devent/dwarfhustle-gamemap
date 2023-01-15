package com.anrisoftware.dwarfhustle.gamemap.jme;

import com.anrisoftware.dwarfhustle.model.actor.ActorsModule;
import com.google.inject.AbstractModule;

/**
 *
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class AppModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new ActorsModule());
	}
}
