package org.dwarfhustle.gamemap.tester.gui.javafx.states;

import com.anrisoftware.dwarfhustle.gui.javafx.states.AbstractKeyMappingProvider;
import com.anrisoftware.dwarfhustle.gui.javafx.states.KeyMappingMap;

import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Provides the {@link KeyMappingMap} from {@code TesterKeyMapping.groovy.}
 *
 * @author Erwin Müller <erwin@muellerpublic.de>
 */
public class TesterKeyMappingProvider extends AbstractKeyMappingProvider {

    public TesterKeyMappingProvider() throws ResourceException, ScriptException {
        super(TesterKeyMappingProvider.class.getResource("TesterKeyMapping.groovy"), "TesterKeyMapping.groovy");
    }

}
