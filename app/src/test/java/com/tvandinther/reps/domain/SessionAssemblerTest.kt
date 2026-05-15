package com.tvandinther.reps.domain

import com.tvandinther.reps.data.model.SetWithBreak
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionAssemblerTest {

    private val assembler = SessionAssembler()

    private fun makeRow(
        id: Long,
        exerciseId: Long,
        loggedAt: Long,
        sessionBreak: Int = 0,
    ) = SetWithBreak(
        id = id,
        exerciseId = exerciseId,
        volumeValue = 10.0,
        resistanceValue = 100.0,
        rpe = null,
        note = null,
        loggedAt = loggedAt,
        sessionBreak = sessionBreak,
    )

    @Test
    fun `empty input returns empty list`() {
        val result = assembler.assemble(emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `single session groups correctly`() {
        val rows = listOf(
            makeRow(id = 3, exerciseId = 1, loggedAt = 3000, sessionBreak = 0),
            makeRow(id = 2, exerciseId = 2, loggedAt = 2000, sessionBreak = 0),
            makeRow(id = 1, exerciseId = 1, loggedAt = 1000, sessionBreak = 0),
        )

        val result = assembler.assemble(rows)

        assertEquals(1, result.size)
        val session = result[0]
        assertEquals(1000L, session.startedAt)
        val exerciseIds = session.sets.map { it.exerciseId }.toSet()
        assertEquals(setOf(1L, 2L), exerciseIds)
        val setsForEx1 = session.sets.first { it.exerciseId == 1L }.sets
        assertEquals(2, setsForEx1.size)
        assertEquals(1000L, setsForEx1[0].loggedAt)
        assertEquals(3000L, setsForEx1[1].loggedAt)
    }

    @Test
    fun `two sessions separated by gap are split correctly`() {
        // DESC order: newest first. session_break=1 on the boundary row marks the older session.
        val rows = listOf(
            makeRow(id = 4, exerciseId = 1, loggedAt = 10_000_000, sessionBreak = 0),
            makeRow(id = 3, exerciseId = 1, loggedAt =  9_000_000, sessionBreak = 0),
            // gap > 90 min here — row below is the last set of the previous session
            makeRow(id = 2, exerciseId = 1, loggedAt =  2_000_000, sessionBreak = 1),
            makeRow(id = 1, exerciseId = 1, loggedAt =  1_000_000, sessionBreak = 0),
        )

        val result = assembler.assemble(rows)

        assertEquals(2, result.size)
        val newerSession = result[0]
        val olderSession = result[1]
        assertEquals(2, newerSession.sets.first().sets.size)
        assertEquals(2, olderSession.sets.first().sets.size)
        assertTrue(newerSession.startedAt > olderSession.startedAt)
    }
}
