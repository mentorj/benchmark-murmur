package com.javaxpert.benchmarks.murmur;

import com.google.common.hash.Hashing;
import com.sangupta.murmur.Murmur2;
import com.sangupta.murmur.Murmur3;
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
    public void  hash128WithApacheCommonsMurmur3(MyStringState state, Blackhole bh){
        bh.consume(MurmurHash3.hash128x64(state.targetString.getBytes()));
    }

    @Benchmark
    public void  hashWithMurmur3(MyStringState state, Blackhole bh){
        bh.consume(MurmurHash3.hash32(state.targetString));
    }

    @Benchmark
    public  void hash32WithGuava(MyStringState  state,Blackhole bh){
        bh.consume(Hashing.murmur3_32_fixed(101).hashBytes(state.targetString.getBytes()));
    }

    @Benchmark
    public  void hash128WithGuava(MyStringState  state,Blackhole bh){
        bh.consume(Hashing.murmur3_128(10).hashBytes(state.targetString.getBytes()));
    }

    @Benchmark
    public void  hashWithSangupta(MyStringState state, Blackhole bh){
        bh.consume(Murmur2.hash(state.targetString.getBytes(),state.targetString.length(),0x7f3a21eal));
    }

    @Benchmark
    public void  hash128WithSangupta(MyStringState state, Blackhole bh){
        bh.consume(Murmur3.hash_x64_128(state.targetString.getBytes(),state.targetString.length(),0x7f3a21eal));
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
