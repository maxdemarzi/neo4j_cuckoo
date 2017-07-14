# neo4j_cuckoo
Using a Cuckoo filter to speed up relationship creation

    Benchmark                                                  (followsCount)  (userCount)   Mode  Cnt       Score       Error  Units
    CuckooBenchmark.measureCuckooCheckLongsNoIndex                        200        10000  thrpt    5  198018.199 ± 35573.548  ops/s
    CuckooBenchmark.measureCuckooCheckLongsWithCacheLookups               200        10000  thrpt    5  103627.767 ±  7625.464  ops/s
    CuckooBenchmark.measureCuckooCheckLongsWithIndexLookups               200        10000  thrpt    5     646.182 ±   299.803  ops/s
    CuckooBenchmark.measureCuckooCheckString                              200        10000  thrpt    5   32507.840 ±  6053.814  ops/s
    CuckooBenchmark.measureCuckooCheckStringWithCacheLookups              200        10000  thrpt    5  106492.428 ± 10154.001  ops/s
    CuckooBenchmark.measureNoFilterCheckLongsNoIndex                      200        10000  thrpt    5    2110.529 ±   135.350  ops/s
    CuckooBenchmark.measureNoFilterCheckLongsWithIndexLookups             200        10000  thrpt    5     658.191 ±   181.235  ops/s        