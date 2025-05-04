package kuke.board.common.snowflake;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SnowflakeJavaTest {
	SnowflakeJava snowflakeJava = new SnowflakeJava();
	Snowflake snowflake = new Snowflake();

	@Test
	void nextIdTest() throws ExecutionException, InterruptedException {
		// given
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		List<Future<List<Long>>> futures = new ArrayList<>();
		int repeatCount = 1000;
		int idCount = 1000;

		// when
		for (int i = 0; i < repeatCount; i++) {
			futures.add(executorService.submit(() -> generateIdList(snowflakeJava, idCount)));
		}

		// then
		List<Long> result = new ArrayList<>();
		for (Future<List<Long>> future : futures) {
			List<Long> idList = future.get();
			for (int i = 1; i < idList.size(); i++) {
				assertThat(idList.get(i)).isGreaterThan(idList.get(i - 1));
			}
			result.addAll(idList);
		}
		assertThat(result.stream().distinct().count()).isEqualTo(repeatCount * idCount);

		executorService.shutdown();
	}

	@Test
	@DisplayName("코틀린으로 변환한 Snowflake 코드 테스트")
	void nextIdTestKt() throws ExecutionException, InterruptedException {
		// given
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		List<Future<List<Long>>> futures = new ArrayList<>();
		int repeatCount = 1000;
		int idCount = 1000;

		// when
		for (int i = 0; i < repeatCount; i++) {
			futures.add(executorService.submit(() -> generateIdList(snowflake, idCount)));
		}

		// then
		List<Long> result = new ArrayList<>();
		for (Future<List<Long>> future : futures) {
			List<Long> idList = future.get();
			for (int i = 1; i < idList.size(); i++) {
				assertThat(idList.get(i)).isGreaterThan(idList.get(i - 1));
			}
			result.addAll(idList);
		}
		assertThat(result.stream().distinct().count()).isEqualTo(repeatCount * idCount);

		executorService.shutdown();
	}

	List<Long> generateIdList(SnowflakeJava snowflakeJava, int count) {
		List<Long> idList = new ArrayList<>();
		while (count-- > 0) {
			idList.add(snowflakeJava.nextId());
		}
		return idList;
	}

	List<Long> generateIdList(Snowflake snowflake, int count) {
		List<Long> idList = new ArrayList<>();
		while (count-- > 0) {
			idList.add(snowflake.nextId());
		}
		return idList;
	}

	@Test
	void nextIdPerformanceTest() throws InterruptedException {
		// given
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		int repeatCount = 1000;
		int idCount = 1000;
		CountDownLatch latch = new CountDownLatch(repeatCount);

		// when
		long start = System.nanoTime();
		for (int i = 0; i < repeatCount; i++) {
			executorService.submit(() -> {
				generateIdList(snowflakeJava, idCount);
				latch.countDown();
			});
		}

		latch.await();

		long end = System.nanoTime();
		System.out.println("times = %s ms".formatted((end - start) / 1_000_000));

		executorService.shutdown();
	}

	@Test
	@DisplayName("코틀린으로 변환한 Snowflake 코드 테스트")
	void nextIdPerformanceTestKt() throws InterruptedException {
		// given
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		int repeatCount = 1000;
		int idCount = 1000;
		CountDownLatch latch = new CountDownLatch(repeatCount);

		// when
		long start = System.nanoTime();
		for (int i = 0; i < repeatCount; i++) {
			executorService.submit(() -> {
				generateIdList(snowflake, idCount);
				latch.countDown();
			});
		}

		latch.await();

		long end = System.nanoTime();
		System.out.println("times = %s ms".formatted((end - start) / 1_000_000));

		executorService.shutdown();
	}
}