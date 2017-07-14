package com.maxdemarzi;

import org.neo4j.graphdb.*;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class CuckooBenchmark {
    private GraphDatabaseService db;
    private Random rand = new Random();
    private RelationshipType FOLLOWS = RelationshipType.withName("FOLLOWS");
    private HashMap<String,Long> cache = new HashMap<>();

    @Param({"10000"})
    private int userCount;

    @Param({"200"})
    private int followsCount;

    @Setup
    public void prepare() throws IOException {
        db = new TestGraphDatabaseFactory().newImpermanentDatabase();
        populateDb(db);
    }

    @TearDown
    public void tearDown() {
        db.shutdown();
    }

    private void populateDb(GraphDatabaseService db) throws IOException {
        ArrayList<Node> users = new ArrayList<>();

        try (Transaction tx = db.beginTx()) {
            for (int i = 0; i < userCount; i++) {
                users.add(createUser(db, "user" + i));
            }
            tx.success();
        }

        Transaction tx = db.beginTx();
        try  {
            for (int i = 0; i < userCount; i++){
                Node user =  users.get(i);

                for (int j = 0; j < followsCount; j++) {
                    user.createRelationshipTo(users.get(rand.nextInt(userCount)), FOLLOWS);
                }

                if(i % 100 == 0){
                    tx.success();
                    tx.close();
                    tx = db.beginTx();
                }

            }
            tx.success();
        } finally {
            tx.close();
        }
    }

    private Node createUser(GraphDatabaseService db, String value) {
        Node node = db.createNode(Label.label("User"));
        node.setProperty("username", value);
        cache.put(value, node.getId());
        return node;
    }

    private boolean doubleCheck(long n1Id, long n2Id) {
        try(Transaction tx = db.beginTx()) {
            Node n1 = db.getNodeById(n1Id);
            Node n2 = db.getNodeById(n2Id);
            if (n1.getDegree(FOLLOWS, Direction.OUTGOING) < n2.getDegree(FOLLOWS, Direction.OUTGOING)) {
                for (Relationship r1 : n1.getRelationships(Direction.OUTGOING, FOLLOWS)) {
                    if (r1.getEndNode().equals(n2)) {
                        return true;
                    }
                }
            } else {
                for (Relationship r1 : n2.getRelationships(Direction.INCOMING, FOLLOWS)) {
                    if (r1.getStartNode().equals(n1)) {
                        return true;
                    }
                }
            }
            tx.success();
        }
        return false;
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(1)
    @Threads(4)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public boolean measureCuckooCheckLongsNoIndex() throws IOException {
        long n1Id = rand.nextInt(userCount);
        long n2Id = rand.nextInt(userCount);

        if(CuckooFilters.get("FOLLOWS", n1Id, n2Id)) {
            if (doubleCheck(n1Id, n2Id)) return true;
        }
        return false;
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(1)
    @Threads(4)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public boolean measureNoFilterCheckLongsNoIndex() throws IOException {
        long n1Id = rand.nextInt(userCount);
        long n2Id = rand.nextInt(userCount);

        return doubleCheck(n1Id, n2Id);
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(1)
    @Threads(4)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public boolean measureCuckooCheckLongsWithIndexLookups() throws IOException {
        long n1Id = rand.nextInt(userCount);
        long n2Id = rand.nextInt(userCount);

        try(Transaction tx = db.beginTx()) {
            Node n1 = db.findNode(Labels.User, "username", "user" + n1Id);
            Node n2 = db.findNode(Labels.User, "username", "user" + n2Id);

            if(CuckooFilters.get("FOLLOWS", n1.getId(), n2.getId())) {
                if (n1.getDegree(FOLLOWS, Direction.OUTGOING) < n2.getDegree(FOLLOWS, Direction.OUTGOING)) {
                    for (Relationship r1 : n1.getRelationships(Direction.OUTGOING, FOLLOWS)) {
                        if (r1.getEndNode() == n2) {
                            return true;
                        }
                    }
                } else {
                    for (Relationship r1 : n2.getRelationships(Direction.INCOMING, FOLLOWS)) {
                        if (r1.getStartNode() == n1) {
                            return true;
                        }
                    }
                }
            }
            tx.success();
        }
        return false;
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(1)
    @Threads(4)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public boolean measureNoFilterCheckLongsWithIndexLookups() throws IOException {
        long n1Id = rand.nextInt(userCount);
        long n2Id = rand.nextInt(userCount);

        try(Transaction tx = db.beginTx()) {
            Node n1 = db.findNode(Labels.User, "username", "user" + n1Id);
            Node n2 = db.findNode(Labels.User, "username", "user" + n2Id);

            if (n1.getDegree(FOLLOWS, Direction.OUTGOING) < n2.getDegree(FOLLOWS, Direction.OUTGOING)) {
                for (Relationship r1 : n1.getRelationships(Direction.OUTGOING, FOLLOWS)) {
                    if (r1.getEndNode() == n2) {
                        return true;
                    }
                }
            } else {
                for (Relationship r1 : n2.getRelationships(Direction.INCOMING, FOLLOWS)) {
                    if (r1.getStartNode() == n1) {
                        return true;
                    }
                }
            }
            tx.success();
        }
        return false;
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(1)
    @Threads(4)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public boolean measureCuckooCheckString() throws IOException {
        long n1Id = rand.nextInt(userCount);
        long n2Id = rand.nextInt(userCount);

        if(CuckooFilters.get("FOLLOWS", "user" + n1Id + "-user" + n2Id )) {
            try(Transaction tx = db.beginTx()) {
                Node n1 = db.findNode(Labels.User, "username",  "user" + n1Id);
                Node n2 = db.findNode(Labels.User, "username",  "user" + n2Id);

                if (n1.getDegree(FOLLOWS, Direction.OUTGOING) < n2.getDegree(FOLLOWS, Direction.OUTGOING)) {
                    for (Relationship r1 : n1.getRelationships(Direction.OUTGOING, FOLLOWS)) {
                        if (r1.getEndNode() == n2) {
                            return true;
                        }
                    }
                } else {
                    for (Relationship r1 : n2.getRelationships(Direction.INCOMING, FOLLOWS)) {
                        if (r1.getStartNode() == n1) {
                            return true;
                        }
                    }
                }
                tx.success();
            }

        }
        return false;
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(1)
    @Threads(4)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public boolean measureCuckooCheckLongsWithCacheLookups() throws IOException {
        long n1Id = rand.nextInt(userCount);
        long n2Id = rand.nextInt(userCount);

        try(Transaction tx = db.beginTx()) {
            Node n1 = db.getNodeById(cache.get("user" + n1Id));
            Node n2 = db.getNodeById(cache.get("user" + n2Id));

            if(CuckooFilters.get("FOLLOWS", n1.getId(), n2.getId())) {
                if (n1.getDegree(FOLLOWS, Direction.OUTGOING) < n2.getDegree(FOLLOWS, Direction.OUTGOING)) {
                    for (Relationship r1 : n1.getRelationships(Direction.OUTGOING, FOLLOWS)) {
                        if (r1.getEndNode() == n2) {
                            return true;
                        }
                    }
                } else {
                    for (Relationship r1 : n2.getRelationships(Direction.INCOMING, FOLLOWS)) {
                        if (r1.getStartNode() == n1) {
                            return true;
                        }
                    }
                }
            }
            tx.success();
        }
        return false;
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 5)
    @Fork(1)
    @Threads(4)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public boolean measureCuckooCheckStringWithCacheLookups() throws IOException {
        long n1Id = rand.nextInt(userCount);
        long n2Id = rand.nextInt(userCount);

        if(CuckooFilters.get("FOLLOWS", "user" + n1Id + "-user" + n2Id )) {
            try(Transaction tx = db.beginTx()) {
                Node n1 = db.getNodeById(cache.get("user" + n1Id));
                Node n2 = db.getNodeById(cache.get("user" + n2Id));

                if (n1.getDegree(FOLLOWS, Direction.OUTGOING) < n2.getDegree(FOLLOWS, Direction.OUTGOING)) {
                    for (Relationship r1 : n1.getRelationships(Direction.OUTGOING, FOLLOWS)) {
                        if (r1.getEndNode() == n2) {
                            return true;
                        }
                    }
                } else {
                    for (Relationship r1 : n2.getRelationships(Direction.INCOMING, FOLLOWS)) {
                        if (r1.getStartNode() == n1) {
                            return true;
                        }
                    }
                }
                tx.success();
            }

        }
        return false;
    }
}
