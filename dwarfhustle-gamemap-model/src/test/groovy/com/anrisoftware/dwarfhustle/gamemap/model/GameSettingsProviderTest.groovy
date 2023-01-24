package com.anrisoftware.dwarfhustle.gamemap.model

import static org.junit.jupiter.params.provider.Arguments.of

import java.time.LocalTime
import java.util.stream.Stream

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import com.anrisoftware.dwarfhustle.gamemap.model.ObservableGameSettings.CommandItem
import com.anrisoftware.dwarfhustle.gamemap.model.ObservableGameSettings.GameSettings

import groovy.util.logging.Slf4j

/**
 * @see GameSettingsProvider
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
class GameSettingsProviderTest {

	static gameSettingsSource() {
		Stream.of(
				of({
					def gs = new GameSettings()
					return gs
				}, """---
locale: "en_US"
tickLength: 0.033333335
tickLongLength: 0.06666667
windowFullscreen: false
windowWidth: 1024
windowHeight: 768
iconSize: "MEDIUM"
textPosition: "RIGHT"
commandsSplitPosition: 0.71
cameraPosX: 0.002901543
cameraPosY: -0.013370683
cameraPosZ: 28.217747
cameraRotX: -4.8154507E-6
cameraRotY: 0.9999911
cameraRotZ: 0.0012241602
cameraRotW: 0.004027171
commandsSet: []
"""), //
				of({
					def gs = new GameSettings()
					gs.commandsSet.add(new CommandItem(LocalTime.of(10, 0, 0, 0), "foo command"))
					return gs
				}, """---
locale: "en_US"
tickLength: 0.033333335
tickLongLength: 0.06666667
windowFullscreen: false
windowWidth: 1024
windowHeight: 768
iconSize: "MEDIUM"
textPosition: "RIGHT"
commandsSplitPosition: 0.71
cameraPosX: 0.002901543
cameraPosY: -0.013370683
cameraPosZ: 28.217747
cameraRotX: -4.8154507E-6
cameraRotY: 0.9999911
cameraRotZ: 0.0012241602
cameraRotW: 0.004027171
commandsSet:
- time:
  - 10
  - 0
  line: "foo command"
"""), //
				of({
					def gs = new GameSettings()
					gs.commandsSet.add(new CommandItem(LocalTime.of(10, 0, 0, 0), "foo command"))
					gs.commandsSet.add(new CommandItem(LocalTime.of(10, 0, 0, 0), "bar command"))
					return gs
				}, """---
locale: "en_US"
tickLength: 0.033333335
tickLongLength: 0.06666667
windowFullscreen: false
windowWidth: 1024
windowHeight: 768
iconSize: "MEDIUM"
textPosition: "RIGHT"
commandsSplitPosition: 0.71
cameraPosX: 0.002901543
cameraPosY: -0.013370683
cameraPosZ: 28.217747
cameraRotX: -4.8154507E-6
cameraRotY: 0.9999911
cameraRotZ: 0.0012241602
cameraRotW: 0.004027171
commandsSet:
- time:
  - 10
  - 0
  line: "foo command"
"""), //
				of({
					def gs = new GameSettings()
					gs.commandsSet.add(new CommandItem(LocalTime.of(10, 0, 0, 0), "foo command"))
					gs.commandsSet.add(new CommandItem(LocalTime.of(10, 0, 0, 1), "bar command"))
					return gs
				}, """---
locale: "en_US"
tickLength: 0.033333335
tickLongLength: 0.06666667
windowFullscreen: false
windowWidth: 1024
windowHeight: 768
iconSize: "MEDIUM"
textPosition: "RIGHT"
commandsSplitPosition: 0.71
cameraPosX: 0.002901543
cameraPosY: -0.013370683
cameraPosZ: 28.217747
cameraRotX: -4.8154507E-6
cameraRotY: 0.9999911
cameraRotZ: 0.0012241602
cameraRotW: 0.004027171
commandsSet:
- time:
  - 10
  - 0
  line: "foo command"
- time:
  - 10
  - 0
  - 0
  - 1
  line: "bar command"
"""), //
				)
	}

	@ParameterizedTest
	@MethodSource("gameSettingsSource")
	void save_to_string(def gsp, def expected) {
		def gs = gsp()
		def p = new GameSettingsProvider(gs)
		p.mapper = new ObjectMapperProvider().get()
		def s = p.saveAsString()
		log.info "save_to_string {} ```{}```\n```{}```", gs, s, expected
		assert s == expected
	}

	@ParameterizedTest
	@MethodSource("gameSettingsSource")
	void load_from_string(def gspExpected, def s) {
		GameSettings expected = gspExpected()
		def p = new GameSettingsProvider()
		p.mapper = new ObjectMapperProvider().get()
		p.loadFromString(s)
		log.info "load_from_string {} ```{}```", s, expected
		assert p.get().locale.get() == expected.locale
		assert p.get().commandsList.size() == expected.commandsSet.size()
		assert p.get().commandsSet.size() == expected.commandsSet.size()
	}
}
