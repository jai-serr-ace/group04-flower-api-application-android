package com.example.group05_flower_api_app

import com.example.group05_flower_api_app.data.api.*
import com.example.group05_flower_api_app.ui.result.ResultViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ResultViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val api: FlowerApiService = mockk()
    private lateinit var vm: ResultViewModel

    @Before fun setup() {
        Dispatchers.setMain(dispatcher)
        vm = ResultViewModel(api)
    }
    @After fun teardown() { Dispatchers.resetMain() }

    @Test fun `search uses name filter and fetches exact matching detail`() {
        coEvery { api.getFlowers(name = "Rose") } returns Response.success(FlowerPageResponse(items = listOf(
            FlowerResponse(flowerId = "1", name = "Wild Rose"),
            FlowerResponse(flowerId = "2", name = "Rose")
        )))
        coEvery { api.getFlowerById("2") } returns Response.success(FlowerResponse(flowerId = "2", name = "Rose", description = "Real notes"))
        vm.loadFlowerDetails(" Rose ")
        dispatcher.scheduler.advanceUntilIdle()
        coVerify { api.getFlowerById("2") }
        assertEquals("Real notes", vm.uiState.value.description)
        assertEquals(emptyList<String>(), vm.uiState.value.tags)
        assertEquals("", vm.uiState.value.quote)
        assertNull(vm.uiState.value.errorMessage)
    }

    @Test fun `id navigation bypasses search`() {
        coEvery { api.getFlowerById("7") } returns Response.success(FlowerResponse(flowerId = "7", name = "Lily"))
        vm.loadFlowerDetails("", "7")
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals("Lily", vm.uiState.value.flowerName)
        coVerify(exactly = 0) { api.getFlowers(any(), any(), any(), any()) }
    }

    @Test fun `empty search reports no results`() {
        coEvery { api.getFlowers(name = "Missing") } returns Response.success(FlowerPageResponse())
        vm.loadFlowerDetails("Missing")
        dispatcher.scheduler.advanceUntilIdle()
        assertNotNull(vm.uiState.value.errorMessage)
        assertEquals("", vm.uiState.value.description)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test fun `not found and offline clear previously loaded content`() {
        coEvery { api.getFlowerById("7") } returns Response.success(FlowerResponse(flowerId = "7", name = "Lily"))
        vm.loadFlowerDetails("", "7")
        dispatcher.scheduler.advanceUntilIdle()
        coEvery { api.getFlowerById("8") } returns Response.error(404, "".toResponseBody())
        vm.loadFlowerDetails("", "8")
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals("Flower not found.", vm.uiState.value.errorMessage)
        assertEquals("", vm.uiState.value.flowerName)
        coEvery { api.getFlowerById("8") } throws IOException("offline")
        vm.loadFlowerDetails("", "8")
        dispatcher.scheduler.advanceUntilIdle()
        assertNotNull(vm.uiState.value.errorMessage)
        assertFalse(vm.uiState.value.isLoading)
    }
}
