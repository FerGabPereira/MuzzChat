package com.fernandopereira.muzzchat.data.local

import com.fernandopereira.muzzchat.domain.model.User
import com.fernandopereira.muzzchat.domain.model.User.ME
import com.fernandopereira.muzzchat.domain.model.User.SARAH
import kotlinx.coroutines.flow.first

/**
 * Populates the database on first launch with messages designed to exercise both
 * visual rules defined in the spec:
 *
 * - **isGroupedWithNext**: consecutive same-sender messages sent < 20 s apart share
 *   reduced spacing. Demonstrated within each cluster.
 * - **DateHeader**: a header is inserted when two consecutive messages are > 1 h apart.
 *   Demonstrated by the 2-hour gap between the two clusters.
 */
class DatabaseSeeder(private val dao: MessageDao) {
    suspend fun seedIfEmpty() {
        if (dao.observeAll().first().isNotEmpty()) return
        buildSeedMessages().forEach { dao.insert(it) }
    }

    private fun buildSeedMessages(): List<MessageEntity> {
        val clusterB = System.currentTimeMillis() - 180.seconds
        val clusterA = clusterB - TWO_HOURS // gap > 1 h → DateHeader between clusters

        return listOf(
            // Cluster A
            entity(SARAH, "Hey! Are you free later?", clusterA),
            entity(ME, "Yeah, what's up?", clusterA + 12.seconds),
            entity(ME, "Something on your mind?", clusterA + 19.seconds), // < 20 s → grouped
            entity(SARAH, "I was thinking we could grab coffee", clusterA + 40.seconds),
            entity(SARAH, "There's a new place near the office", clusterA + 55.seconds), // < 20 s → grouped
            // Cluster B — 2 h later, DateHeader inserted before this cluster
            entity(ME, "Sorry, just got out of a meeting!", clusterB),
            entity(ME, "What were you saying about coffee?", clusterB + 9.seconds), // < 20 s → grouped
            entity(SARAH, "No worries! Coffee tomorrow? ☕", clusterB + 35.seconds),
            entity(SARAH, "There's a spot near the office", clusterB + 48.seconds), // < 20 s → grouped
            entity(ME, "10am works perfectly!", clusterB + 90.seconds),
        )
    }

    private fun entity(sender: User, text: String, timestamp: Long) =
        MessageEntity(id = 0L, text = text, senderId = sender.id, timestamp = timestamp)

    companion object {
        private const val TWO_HOURS = 2 * 60 * 60 * 1_000L
        private val Int.seconds get() = this * 1_000L
    }
}
