package com.atguigu.gmall.item;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import org.junit.jupiter.api.Test;

public class BloomFilterTest {


    @Test
    public void testBloom() {

        // Funnel<? super T> funnel
        // int expectedInsertions 期望保存数据量
        // double fpp 误判率

        Funnel<Long> funnel = Funnels.longFunnel();
        BloomFilter<Long> bloomFilter = BloomFilter.create(funnel, 1000000, 0.0000001);
        bloomFilter.put(48L);
        bloomFilter.put(49L);
        bloomFilter.put(50L);


        System.out.println("48"+bloomFilter.mightContain(48L));
        System.out.println("49"+bloomFilter.mightContain(49L));
        System.out.println("50"+bloomFilter.mightContain(50L));

        System.out.println("51"+bloomFilter.mightContain(51L));
        System.out.println("52"+bloomFilter.mightContain(52L));

    }
}
