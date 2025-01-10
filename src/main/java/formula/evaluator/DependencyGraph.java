package formula.evaluator;

import spreadsheet.CellAddress;

import java.util.*;

public class DependencyGraph {
    private static final Map<CellAddress, Set<CellAddress>> fromToDependency = new HashMap<>();
    private static final Map<CellAddress, Set<CellAddress>> toFromDependency = new HashMap<>();


    private static final Map<CellAddress, Color> colorMap = new HashMap<>();
    private static final Set<CellAddress> vertexSet = new HashSet<>();

    private static final List<CellAddress> sortedCells = new ArrayList<>();

    public void addDependency(CellAddress from, CellAddress to) {
        if (fromToDependency.containsKey(from)) {
            fromToDependency.get(from).add(to);
        } else {
            Set<CellAddress> dests = new HashSet<>();
            dests.add(to);
            fromToDependency.put(from, dests);
        }
        if (toFromDependency.containsKey(to)) {
            toFromDependency.get(to).add(from);
        } else {
            Set<CellAddress> sources = new HashSet<>();
            sources.add(from);
            toFromDependency.put(to, sources);
        }
        vertexSet.add(from);
        vertexSet.add(to);
    }

    public void removeDependenciesFrom(CellAddress from) {
        fromToDependency.getOrDefault(from, new LinkedHashSet<>(0))
                .forEach((to) -> toFromDependency.getOrDefault(to, new LinkedHashSet<>(0)).remove(from));
        fromToDependency.remove(from);
        vertexSet.clear();
        vertexSet.addAll(fromToDependency.keySet());
        fromToDependency.values().forEach(vertexSet::addAll);
    }

    public Set<CellAddress> usedBy(CellAddress cell) {
        return toFromDependency.getOrDefault(cell, new LinkedHashSet<>(0));
    }

    public List<CellAddress> topologicalSort() throws CyclicDependencyException {
        sortedCells.clear();
        vertexSet.forEach(v -> colorMap.put(v, Color.White));
        for (CellAddress vertex : vertexSet) {
            if (colorMap.get(vertex) == Color.White) {
                DFSVisit(vertex);
            }
        }
        return sortedCells;
    }

    private void DFSVisit(CellAddress vertex) throws CyclicDependencyException {
        if (colorMap.get(vertex) == Color.Gray) {
            throw new CyclicDependencyException();
        }
        colorMap.put(vertex, Color.Gray);
        for (CellAddress successor : fromToDependency.getOrDefault(vertex, new HashSet<>(0))) {
            DFSVisit(successor);
        }
        colorMap.put(vertex, Color.Black);
        sortedCells.add(vertex);
    }

    private enum Color {
        White,
        Gray,
        Black
    }
}
