package com.javaxpert.benchmarks.murmur;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.digest.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Measurement(iterations = 5)
public class HashingMurmurBenchmark {

    @State(Scope.Thread)
    public static class MyStringState{
        public String targetString;

        @Setup(Level.Invocation)
        public void generateString(){
            targetString = "Hashed String" + System.currentTimeMillis();
        }
    }
    @Benchmark
    public void  hashWithMurmur2(MyStringState state, Blackhole bh){
        bh.consume(MurmurHash2.hash32(state.targetString));
    }

    @Benchmark
    public void  longHashWithMurmur2(MyStringState state, Blackhole bh){
        bh.consume(MurmurHash2.hash64(state.targetString));
    }

    @Benchmark
    public void  longHashWithMurmur3(MyStringState state, Blackhole bh){
        bh.consume(MurmurHash3.hash64(state.targetString.getBytes()));
    }

    @Benchmark
    public void  hashWithMurmur3(MyStringState state, Blackhole bh){
        bh.consume(MurmurHash3.hash32(state.targetString));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(HashingMurmurBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(3)
                .addProfiler(GCProfiler.class)
                .build();

        new Runner(opt).run();
    }
}
