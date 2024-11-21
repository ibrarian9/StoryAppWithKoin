package com.app.intermediatesubmission

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.app.intermediatesubmission.adapter.StoryAdapter
import com.app.intermediatesubmission.di.StoryRepository
import com.app.intermediatesubmission.di.models.StoryItem
import com.app.intermediatesubmission.ui.listStory.ListStoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ListStoryModelsTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var repo: StoryRepository

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<StoryItem> = PagingData.from(emptyList())
        val expected = MutableLiveData<PagingData<StoryItem>>()
        expected.value = data
        Mockito.`when`(repo.getAllStory()).thenReturn(expected)

        val mainViewModel = ListStoryViewModel(repo)
        val actual: PagingData<StoryItem> = mainViewModel.getAllStory().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actual)
        Assert.assertEquals(0, differ.snapshot().size)
    }
    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummy = DataDummy.generateDummyStoryResponse()
        val data: PagingData<StoryItem> = StoryPagingSource.snapshot(dummy)
        val expected = MutableLiveData<PagingData<StoryItem>>()
        expected.value = data

        Mockito.`when`(repo.getAllStory()).thenReturn(expected)
        val mainViewModel = ListStoryViewModel(repo)
        val actual: PagingData<StoryItem> = mainViewModel.getAllStory().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actual)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummy.size, differ.snapshot().size)
        Assert.assertEquals(dummy[0], differ.snapshot()[0])
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<StoryItem>>>() {
    companion object {
        fun snapshot(items: List<StoryItem>): PagingData<StoryItem> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryItem>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}