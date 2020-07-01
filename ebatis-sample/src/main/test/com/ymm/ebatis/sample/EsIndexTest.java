package com.ymm.ebatis.sample;

import com.ymm.ebatis.sample.entity.RecentOrderModel;
import com.ymm.ebatis.sample.mapper.RecentOrderIndexMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.rest.RestStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author weilong.hu
 * @since 2020/6/28 18:49
 */
@Slf4j
public class EsIndexTest extends ESAbstractTest {
    private RecentOrderIndexMapper recentOrderIndexMapper;

    @Before
    public void startup() {
        recentOrderIndexMapper = createEsMapper(RecentOrderIndexMapper.class);
    }

    @SneakyThrows
    @Test
    public void indexRecentOrderBoolean() {
        Boolean bool = recentOrderIndexMapper.indexRecentOrderBoolean(new RecentOrderModel());
        log.info("index result:{}", bool);
    }

    @SneakyThrows
    @Test
    public void indexRecentOrderBool() {
        boolean bool = recentOrderIndexMapper.indexRecentOrderBool(new RecentOrderModel());
        log.info("index result:{}", bool);
    }


    @SneakyThrows
    @Test
    public void indexRecentOrderString() {
        String id = recentOrderIndexMapper.indexRecentOrderString(new RecentOrderModel());
        log.info("index id:{}", id);
    }

    @SneakyThrows
    @Test
    public void indexRecentOrderVoid() {
        recentOrderIndexMapper.indexRecentOrderVoid(new RecentOrderModel());
        log.info("index success ");
    }

    @SneakyThrows
    @Test
    public void indexRecentOrderIndexResponse() {
        IndexResponse indexResponse = recentOrderIndexMapper.indexRecentOrderIndexResponse(new RecentOrderModel());
        String response = getJsonResult(indexResponse);
        log.info("indexResponse success ：{}", response);
    }

    @SneakyThrows
    @Test
    public void indexRecentOrderRestStatus() {
        RestStatus restStatus = recentOrderIndexMapper.indexRecentOrderRestStatus(new RecentOrderModel());
        log.info("index success restStatus：{}", restStatus);
    }

    @SneakyThrows
    @Test
    public void indexRecentOrderCompletableFuture() {
        AtomicReference<Throwable> ex = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CompletableFuture<RestStatus> restStatusCompletableFuture = recentOrderIndexMapper.indexRecentOrderCompletableFuture(new RecentOrderModel());
        restStatusCompletableFuture.whenCompleteAsync((r, e) -> {
            log.info("index result:{}", r);
            ex.set(e);
            countDownLatch.countDown();
        });
        countDownLatch.await();
        log.info("index success restStatus：{}", restStatusCompletableFuture.get());
        Assert.assertNull(ex.get());
    }

    @SneakyThrows
    @Test
    public void indexRecentOrderFutureVoid() {
        AtomicReference<Throwable> ex = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CompletableFuture<Void> voidCompletableFuture = recentOrderIndexMapper.indexRecentOrderFutureVoid(new RecentOrderModel());
        voidCompletableFuture.whenCompleteAsync((v, e) -> {
            log.info("index over,result:{}", v, e);
            ex.set(e);
            countDownLatch.countDown();
        });
        countDownLatch.await();
        log.info("index success");
        Assert.assertNull(ex.get());
    }
}
