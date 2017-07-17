# neo4j_cuckoo
Using a Cuckoo filter to speed up relationship creation

    Benchmark                                                  (followsCount)  (userCount)   Mode  Cnt       Score       Error  Units
    CuckooBenchmark.measureCuckooCheckLongsNoIndex                        200        10000  thrpt    5  440794.042 ± 40687.072  ops/s
    CuckooBenchmark.measureCuckooCheckLongsWithCacheLookups               200        10000  thrpt    5  202273.139 ± 47443.431  ops/s
    CuckooBenchmark.measureCuckooCheckLongsWithIndexLookups               200        10000  thrpt    5     705.672 ±   187.501  ops/s
    CuckooBenchmark.measureCuckooCheckString                              200        10000  thrpt    5   32086.242 ± 14233.161  ops/s
    CuckooBenchmark.measureCuckooCheckStringWithCacheLookups              200        10000  thrpt    5  219745.072 ± 15983.950  ops/s
    CuckooBenchmark.measureNoFilterCheckLongsNoIndex                      200        10000  thrpt    5    4189.132 ±   272.084  ops/s
    CuckooBenchmark.measureNoFilterCheckLongsWithIndexLookups             200        10000  thrpt    5     683.849 ±   184.560  ops/s        