package com.dicoding.picodiploma.loginwithanimation

import com.dicoding.picodiploma.loginwithanimation.data.remote.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.StoryResponse

object DataDummy {
    fun generateDummyStoryResponse(): StoryResponse {
        val listStory: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val listStoryItem = ListStoryItem(
                createdAt = "2023-10-20T12:26:05Z",
                description = "Description $i",
                id = "id_$i",
                lat = i.toDouble() * 10,
                lon = i.toDouble() * 10,
                name = "Name $i",
                photoUrl = "https://i5.walmartimages.com/asr/c4989269-84f7-4287-9e21-a8e1aa9c32e9_1.81183265b108342329af982043f52e3f.jpeg"
            )
            listStory.add(listStoryItem)
        }
        return StoryResponse(
            error = false, message = "Stories fetched successfully", listStory = listStory
        )
    }
}