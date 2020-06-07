package xandeer.android.lab.coroutine

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker.Result
import androidx.work.testing.TestListenableWorkerBuilder
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import xandeer.android.lab.coroutine.fakes.CoroutineNetworkFake

@RunWith(JUnit4::class)
class RefreshCoroutineDataWorkTest {
  @Test
  fun testRefreshCoroutineDataWork() {
    val fakeNetwork = CoroutineNetworkFake("OK")

    val context = ApplicationProvider.getApplicationContext<Context>()
    val worker = TestListenableWorkerBuilder<RefreshCoroutineDataWork>(context)
      .setWorkerFactory(RefreshCoroutineDataWork.Factory(fakeNetwork))
      .build()

    val result = worker.startWork().get()

    assertThat(result).isEqualTo(Result.success())
  }
}