package dev.oscarrojas.kittypen.core.io;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommandRequestStrategyFactory {

    private final Map<String, CommandRequestStrategy> strategyMap;

    public CommandRequestStrategyFactory(List<CommandRequestStrategy> strategies) {
        strategyMap = new HashMap<>();
        for (CommandRequestStrategy strategy : strategies) {
            strategyMap.put(strategy.getStrategyName(), strategy);
        }
    }

    @Nullable
    public CommandRequestStrategy getCommandStrategy(String strategyName) {
        return strategyMap.get(strategyName);
    }

}
