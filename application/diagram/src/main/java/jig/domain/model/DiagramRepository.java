package jig.domain.model;

public interface DiagramRepository {

    DiagramIdentifier registerSource(DiagramSource source);

    DiagramSource getSource(DiagramIdentifier identifier);

    void register(DiagramIdentifier identifier, Diagram source);

    Diagram get(DiagramIdentifier identifier);
}
