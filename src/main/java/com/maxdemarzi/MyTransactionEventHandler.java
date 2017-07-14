package com.maxdemarzi;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventHandler;

public class MyTransactionEventHandler implements TransactionEventHandler {

    public static GraphDatabaseService db;

    public MyTransactionEventHandler(GraphDatabaseService graphDatabaseService) {
        db = graphDatabaseService;
    }

    @Override
    public Object beforeCommit(TransactionData transactionData) throws Exception {

        return null;
    }

    @Override
    public void afterCommit(TransactionData td, Object o) {
        try (Transaction tx = db.beginTx()) {
            for (Relationship relationship : td.createdRelationships()) {
                CuckooFilters.set(
                        relationship.getType().name(),
                        relationship.getStartNode().getId(),
                        relationship.getEndNode().getId()
                );
                CuckooFilters.set(
                        relationship.getType().name(),
                        relationship.getStartNode().getProperty("username")
                                + "-"
                                + relationship.getEndNode().getProperty("username"));
            }

            for (Relationship relationship : td.deletedRelationships()) {
                CuckooFilters.unset(
                        relationship.getType().name(),
                        relationship.getStartNode().getId(),
                        relationship.getEndNode().getId());
                CuckooFilters.unset(
                        relationship.getType().name(),
                        relationship.getStartNode().getProperty("username")
                                + "-"
                                + relationship.getEndNode().getProperty("username"));
            }
            tx.success();
        }
    }

    @Override
    public void afterRollback(TransactionData transactionData, Object o) {

    }
}
