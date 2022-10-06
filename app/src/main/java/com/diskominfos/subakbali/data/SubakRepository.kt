package com.diskominfos.subakbali.data

import androidx.lifecycle.LiveData
import com.diskominfos.subakbali.api.ApiService

class SubakRepository (
    private val apiService: ApiService
    )
//{
//        fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
//            return Pager(config = PagingConfig(
//                pageSize = 5
//            ),
//                pagingSourceFactory = {
//                    StoryPagingSource(apiService, token)
//                }).liveData
//        }
//}