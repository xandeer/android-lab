package xandeer.android.lab.coroutine

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import xandeer.android.lab.coroutine.fakes.CoroutineNetworkCompletableFake
import xandeer.android.lab.coroutine.fakes.CoroutineNetworkFake
import xandeer.android.lab.coroutine.fakes.TitleDaoFake

class TitleRepositoryTest {
  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  @Test
  fun whenRefreshTitleSuccess_insertsRows() = runBlockingTest {
    val titleDao = TitleDaoFake("title")
    val subject = TitleRepository(
      CoroutineNetworkFake("OK"),
      titleDao
    )
    subject.refreshTitle()
    Truth.assertThat(titleDao.nextInsertedOrNull()).isEqualTo("OK")
  }

  @Test(expected = TitleRefreshError::class)
  fun whenRefreshTitleTimeout_throws() = runBlockingTest {
    val network = CoroutineNetworkCompletableFake()
    val subject = TitleRepository(
      network,
      TitleDaoFake("title")
    )

    launch {
      subject.refreshTitle()
    }

    advanceTimeBy(5_000)
  }
}