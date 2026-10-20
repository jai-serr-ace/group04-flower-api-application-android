package com.example.group05_flower_api_app

import android.net.Uri
import com.example.group05_flower_api_app.ui.upload.UploadViewModel
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UploadViewModelTest {

    private lateinit var viewModel: UploadViewModel

    @Before
    fun setUp() {
        viewModel = UploadViewModel()
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
        assertNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.uploadSuccess)
    }
}
