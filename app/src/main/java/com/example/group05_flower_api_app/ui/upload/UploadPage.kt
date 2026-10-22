package com.example.group05_flower_api_app.ui.upload

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadPage(
    viewModel: UploadViewModel = UploadViewModel(),
    onBackClick: () -> Unit = {},
    onUploadSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.uploadSuccess) {
        if (uiState.uploadSuccess) {
            onUploadSuccess()
            viewModel.resetUploadSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Flower") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Flower Name Field
            OutlinedTextField(
                value = uiState.flowerName,
                onValueChange = viewModel::onFlowerNameChanged,
                enabled = !uiState.isUploading && uiState.uploadedFlowerId == null,
                label = { Text("Flower Name") },
                placeholder = { Text("Enter flower name...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Description Field
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChanged,
                enabled = !uiState.isUploading && uiState.uploadedFlowerId == null,
                label = { Text("Description") },
                placeholder = { Text("Enter flower description...") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            // Tags Field
            OutlinedTextField(
                value = uiState.tags,
                onValueChange = viewModel::onTagsChanged,
                enabled = !uiState.isUploading && uiState.uploadedFlowerId == null,
                label = { Text("Tags") },
                placeholder = { Text("Enter tags (comma separated)...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "The API currently supports flower names, descriptions, and tags. Image uploads and associated quotes are not available yet.",
                style = MaterialTheme.typography.bodySmall
            )

            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Submit Button
            Button(
                onClick = viewModel::upload,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uiState.isUploading
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(if (uiState.isUploading) "Uploading…" else if (uiState.uploadedFlowerId != null) "Retry Tags" else "Upload")
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun UploadPagePreview() {
    MaterialTheme {
        UploadPage()
    }
}
