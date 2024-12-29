package org.dwarfhustle.gamemap.tester.gui.javafx.states

import org.junit.jupiter.api.Test

import com.google.inject.Guice

/**
 * @see TestingKeyMappingsProvider
 * @author Erwin Müller <erwin@muellerpublic.de>
 */
class TesterKeyMappingsProviderTest {

	@Test
	void load_TestingKeyMappingsProvider() {
		def injector = Guice.createInjector()
		def mappings = injector.getInstance(TesterKeyMappingProvider)
		assert mappings.get().size() == 2
	}
}
