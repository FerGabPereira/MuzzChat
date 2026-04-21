package com.fernandopereira.muzzchat.domain.model

enum class User(val id: String) {
    ME("me"),
    SARAH("sarah");

    fun other(): User = if (this == ME) SARAH else ME
}
