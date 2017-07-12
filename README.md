# neo4j_cuckoo
Using a Cuckoo filter to speed up relationship creation

    Benchmark                                                  (followsCount)  (userCount)   Mode  Cnt      Score      Error  Units
    CuckooBenchmark.measureSingleCheck                                    200        10000  thrpt    5  44699.695 ± 7445.563  ops/s
    CuckooBenchmark.measureSingleCheckNoFilter                            200        10000  thrpt    5   1749.085 ±  280.061  ops/s
    CuckooBenchmark.measureSingleCheckNoFilterJustIds                     200        10000  thrpt    5   1834.238 ±   97.648  ops/s
    CuckooBenchmark.measureSingleCheckNoFilterWithIndexLookup             200        10000  thrpt    5    723.870 ±  100.064  ops/s
    CuckooBenchmark.measureSingleCheckWithIndexLookup                     200        10000  thrpt    5    685.302 ±  140.652  ops/s