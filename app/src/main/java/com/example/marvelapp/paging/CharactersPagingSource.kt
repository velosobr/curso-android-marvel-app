package com.example.marvelapp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.marvelapp.data.repository.CharactersRemoteDatasource
import com.example.marvelapp.domain.model.Character
import com.example.marvelapp.framework.network.response.DataWrapperResponse
import com.example.marvelapp.framework.network.response.toCharacterModel

class CharactersPagingSource(
    private val remoteDatasource: CharactersRemoteDatasource<DataWrapperResponse>,
    private val query: String
) : PagingSource<Int, Character>() {
    @SuppressWarnings("TooGenericExceptionCaught")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        return try {
            val offset = params.key ?: 0

            val queries = hashMapOf(
                "offset" to offset.toString()
            )

            if (query.isNotEmpty()) {
                queries["nameStartsWith"] = query
            }

            val response = remoteDatasource.fetchCharacters(queries)

            val responseOffSet = response.data.offset
            val totalCharacters = response.data.total

            LoadResult.Page(
                data = response.data.results.map { it.toCharacterModel() },
                prevKey = null,
                nextKey = if (responseOffSet < totalCharacters) {
                    responseOffSet + LIMIT
                } else null
            )

        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(LIMIT) ?: anchorPage?.nextKey?.minus(LIMIT)

        }
    }

    companion object {
        private const val LIMIT = 20
    }
}