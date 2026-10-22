package com.example.group05_flower_api_app

import android.net.Uri
import com.example.group05_flower_api_app.data.api.FlowerApiService
import com.example.group05_flower_api_app.data.api.FlowerResponse
import com.example.group05_flower_api_app.ui.upload.UploadViewModel
import com.example.group05_flower_api_app.data.api.UserProfileResponse
import com.example.group05_flower_api_app.data.api.TagResponse
import io.mockk.coVerify
import java.io.IOException
import okhttp3.ResponseBody.Companion.toResponseBody
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class UploadViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val apiService: FlowerApiService = mockk(relaxed = true)
    private lateinit var viewModel: UploadViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { apiService.createFlower(any(), any()) } returns Response.success(
            FlowerResponse(id = "fl-123", name = "Orchid")
        )
        coEvery { apiService.getCurrentUser(any()) } returns Response.success(UserProfileResponse(id = "7"))
        viewModel = UploadViewModel(apiService = apiService, authTokenProvider = { "test-token" })
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onFlowerNameChanged updates state correctly`() {
        viewModel.onFlowerNameChanged("Rose")
        assertEquals("Rose", viewModel.uiState.value.flowerName)
    }

    @Test
    fun `onDescriptionChanged updates state correctly`() {
        viewModel.onDescriptionChanged("Beautiful red flower")
        assertEquals("Beautiful red flower", viewModel.uiState.value.description)
    }

    @Test
    fun `onQuoteChanged updates state correctly`() {
        viewModel.onQuoteChanged("A rose by any other name would smell as sweet")
        assertEquals("A rose by any other name would smell as sweet", viewModel.uiState.value.quote)
    }

    @Test
    fun `onTagsChanged updates state correctly`() {
        viewModel.onTagsChanged("red, spring, flora")
        assertEquals("red, spring, flora", viewModel.uiState.value.tags)
    }

    @Test
    fun `onImageSelected updates state correctly`() {
        val dummyUri: Uri = mockk(relaxed = true)
        viewModel.onImageSelected(dummyUri)
        assertEquals(dummyUri, viewModel.uiState.value.imageUri)

        viewModel.clearImage()
        assertNull(viewModel.uiState.value.imageUri)
    }

    @Test
    fun `upload with empty flower name sets error message`() {
        viewModel.upload()
        assertEquals("Flower name cannot be empty", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.uploadSuccess)
    }

    @Test
    fun `upload with valid flower name succeeds`() {
        viewModel.onFlowerNameChanged("Orchid")
        viewModel.upload()
        testDispatcher.scheduler.advanceUntilIdle()
        assertNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.uploadSuccess)
    }
    @Test
    fun `upload requires sign in without calling server`() {
        val signedOut = UploadViewModel(apiService = apiService, authTokenProvider = { null })
        signedOut.onFlowerNameChanged("Rose")
        signedOut.upload()
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(signedOut.uiState.value.uploadSuccess)
        assertNotNull(signedOut.uiState.value.errorMessage)
        coVerify(exactly = 0) { apiService.createFlower(any(), any()) }
    }

    @Test
    fun `upload sends authenticated database fields and normalized tags`() {
        coEvery { apiService.addTagToFlower(any(), any(), any()) } returns Response.success(TagResponse("fl-123", "red"))
        viewModel.onFlowerNameChanged(" Rose ")
        viewModel.onDescriptionChanged(" Red petals ")
        viewModel.onTagsChanged(" red, garden, red, , ")
        viewModel.upload()
        viewModel.upload()
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify(exactly = 1) { apiService.createFlower("Bearer test-token", match {
            it.name == "Rose" && it.notes == "Red petals" && it.userId == 7L
        }) }
        coVerify(exactly = 1) { apiService.addTagToFlower("Bearer test-token", "fl-123", mapOf("userTag" to "red")) }
        coVerify(exactly = 1) { apiService.addTagToFlower("Bearer test-token", "fl-123", mapOf("userTag" to "garden")) }
        assertEquals("fl-123", viewModel.uiState.value.uploadedFlowerId)
        assertTrue(viewModel.uiState.value.uploadSuccess)
    }

    @Test
    fun `rejected upload keeps form and reports failure`() {
        coEvery { apiService.createFlower(any(), any()) } returns Response.error(401, "".toResponseBody())
        viewModel.onFlowerNameChanged("Rose")
        viewModel.upload()
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.uploadSuccess)
        assertFalse(viewModel.uiState.value.isUploading)
        assertEquals("Rose", viewModel.uiState.value.flowerName)
        assertNotNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `network failure does not report success`() {
        coEvery { apiService.createFlower(any(), any()) } throws IOException("offline")
        viewModel.onFlowerNameChanged("Rose")
        viewModel.upload()
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.uploadSuccess)
        assertFalse(viewModel.uiState.value.isUploading)
        assertNotNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `tag retry does not create duplicate flower`() {
        coEvery { apiService.addTagToFlower(any(), any(), any()) } returns Response.error(500, "".toResponseBody())
        viewModel.onFlowerNameChanged("Rose")
        viewModel.onTagsChanged("red")
        viewModel.upload()
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.uploadSuccess)
        assertEquals("fl-123", viewModel.uiState.value.uploadedFlowerId)
        coEvery { apiService.addTagToFlower(any(), any(), any()) } returns Response.success(TagResponse("fl-123", "red"))
        viewModel.upload()
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify(exactly = 1) { apiService.createFlower(any(), any()) }
        assertTrue(viewModel.uiState.value.uploadSuccess)
    }

    @Test
    fun `missing database identity prevents creation`() {
        coEvery { apiService.getCurrentUser(any()) } returns Response.success(UserProfileResponse())
        viewModel.onFlowerNameChanged("Rose")
        viewModel.upload()
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.uploadSuccess)
        assertNotNull(viewModel.uiState.value.errorMessage)
        coVerify(exactly = 0) { apiService.createFlower(any(), any()) }
    }

}
