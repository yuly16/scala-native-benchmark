# scala-native benchmark

## Usage

Test all benchmarks:

```
benchmarkNative --scala SCALA_VERSION --scala-native NATIVE_VERSION
```

Test specific benchmarks, for example we test bounce and brainfuck:

```
benchmarkNative --scala SCALA_VERSION --scala-native NATIVE_VERSION --benchmark-list bounce.BounceBenchmark brainfuck.BrainfuckBenchmark
```

## Benchmark lists

The benchmark lists present below:

```
[1] bounce.BounceBenchmark
[2] brainfuck.BrainfuckBenchmark
[3] cd.CDBenchmark
[4] deltablue.DeltaBlueBenchmark
[5] gcbench.GCBenchBenchmark
[6] histogram.Histogram
[7] json.JsonBenchmark
[8] kmeans.KmeansBenchmark
[9] list.ListBenchmark
[10] mandelbrot.MandelbrotBenchmark
[11] nbody.NbodyBenchmark
[12] permute.PermuteBenchmark
[13] queens.QueensBenchmark
[14] richards.RichardsBenchmark
```

## Note
The script can only be run in linux or WSL. The script is tested in WSL.