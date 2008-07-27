package org.jbehave.scenario.parser;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jbehave.scenario.Scenario;
import org.jbehave.scenario.parser.scenarios.MyPendingScenario;
import org.junit.Test;

public class ScenarioFileLoaderBehaviour {

    @Test
    public void canLoadScenario() {
    	StepParser parser = mock(StepParser.class);
        ScenarioFileLoader loader = new ScenarioFileLoader(parser);
        ScenarioDefinition definition = loader.loadStepsFor(MyPendingScenario.class);
        definition.getSteps();
        verify(parser).findSteps("Given my scenario");
    }

    @Test
    public void canLoadScenarioWithCustomFilenameResolver() {
    	StepParser parser = mock(StepParser.class);
        ScenarioFileLoader loader = new ScenarioFileLoader(new CasePreservingResolver(".scenario"), parser);
        loader.loadStepsFor(MyPendingScenario.class).getSteps();
        verify(parser).findSteps("Given my scenario");
    }
    
    @Test(expected = ScenarioNotFoundException.class)
    public void cannotLoadScenarioForInexistentResource() {
        ScenarioFileLoader loader = new ScenarioFileLoader();
        loader.loadStepsFor(InexistentScenario.class);
    }

    @Test(expected = InvalidScenarioResourceException.class)
    public void cannotLoadScenarioForInvalidResource() {
        ScenarioFileLoader loader = new ScenarioFileLoader(new InvalidClassLoader());
        loader.loadStepsFor(MyPendingScenario.class);
    }

    static class InexistentScenario extends Scenario {

    }

    static class InvalidClassLoader extends ClassLoader {

        @Override
        public InputStream getResourceAsStream(String name) {
            return new InputStream() {

                public int available() throws IOException {
                    return 1;
                }

                @Override
                public int read() throws IOException {
                    throw new IOException("invalid");
                }

            };
        }
    }
}