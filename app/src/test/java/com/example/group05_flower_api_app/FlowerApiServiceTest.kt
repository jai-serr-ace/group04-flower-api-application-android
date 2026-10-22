package com.example.group05_flower_api_app

import com.example.group05_flower_api_app.data.api.*
import com.google.gson.JsonParser
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FlowerApiServiceTest {
    private lateinit var server: MockWebServer
    private lateinit var api: FlowerApiService
    @Before fun setup() {
        server = MockWebServer()
        server.start()
        api = Retrofit.Builder().baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create()).build().create(FlowerApiService::class.java)
    }
    @After fun teardown() { server.shutdown() }

    @Test fun `search parses backend pagination and detail fields`() = runTest {
        server.enqueue(MockResponse().setBody("""{"items":[{"flowerId":12,"name":"Rose","userId":7,"notes":"Red petals"}],"page":0,"size":20,"totalItems":1,"totalPages":1}"""))
        val flower = api.getFlowers(name = "Rose").body()!!.items.single()
        val request = server.takeRequest()
        assertEquals("/api/v1/flowers?name=Rose&page=0&size=20", request.path)
        assertNull(request.getHeader("Authorization"))
        assertEquals("12", flower.resolvedId())
        assertEquals("7", flower.ownerId)
        assertEquals("Red petals", flower.description)
        server.enqueue(MockResponse().setBody("""{"flowerId":12,"name":"Rose","userId":7,"notes":"Red petals"}"""))
        assertEquals("Rose", api.getFlowerById("12").body()!!.resolvedName())
        assertEquals("/api/v1/flowers/12", server.takeRequest().path)
    }

    @Test fun `upload sends bearer token and backend JSON fields`() = runTest {
        server.enqueue(MockResponse().setResponseCode(201).setBody("""{"flowerId":12,"name":"Rose","userId":7,"notes":"Red petals"}"""))
        api.createFlower("Bearer token", FlowerCreateRequest(name = "Rose", userId = 7, notes = "Red petals"))
        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/api/v1/flowers", request.path)
        assertEquals("Bearer token", request.getHeader("Authorization"))
        val json = JsonParser.parseString(request.body.readUtf8()).asJsonObject
        assertEquals(7L, json["userId"].asLong)
        assertEquals("Red petals", json["notes"].asString)
        assertFalse(json.has("imageUrl"))
        server.enqueue(MockResponse().setResponseCode(201).setBody("""{"flowerId":12,"userTag":"garden"}"""))
        assertEquals("garden", api.addTagToFlower("Bearer token", "12", mapOf("userTag" to "garden")).body()!!.userTag)
        val tag = server.takeRequest()
        assertEquals("/api/v1/tags/12", tag.path)
        assertEquals("garden", JsonParser.parseString(tag.body.readUtf8()).asJsonObject["userTag"].asString)
    }

    @Test fun `changing base URL resets cached flower service`() {
        val previous = ApiClient.baseUrl
        try {
            ApiClient.baseUrl = server.url("/").toString()
            val first = ApiClient.flowerApiService
            ApiClient.baseUrl = server.url("/other/").toString()
            assertNotSame(first, ApiClient.flowerApiService)
        } finally { ApiClient.baseUrl = previous }
    }
}
