package com.app.intermediatesubmission

import com.app.intermediatesubmission.di.models.StoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<StoryItem> {
        val items: MutableList<StoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = StoryItem(
                "test",
                "test",
                "test",
                "test",
                20.0,
                "1"
            )
            items.add(story)
        }
        return items
    }
}