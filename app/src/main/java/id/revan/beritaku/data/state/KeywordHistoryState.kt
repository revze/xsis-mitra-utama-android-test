package id.revan.beritaku.data.state

import id.revan.beritaku.data.model.Keyword

data class KeywordHistoryState(
    var isLoading: Boolean = false,
    var keywords: List<Keyword> = mutableListOf()
)