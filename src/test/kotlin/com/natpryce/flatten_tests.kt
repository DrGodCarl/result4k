package com.natpryce

import org.junit.Test
import kotlin.test.assertEquals

class SequenceOfSequencesFlattenTests {
    @Test
    fun `a sequence of results of sequences returns a flattened sequence`() {
        val unflattened = listOf(
                Success(listOf(1, 2, 3).asSequence()),
                Success(listOf(4, 5).asSequence()),
                Success(listOf(6).asSequence())
        ).asSequence()
        assertEquals(listOf(1, 2, 3, 4, 5, 6).map { Success(it) }, unflattened.flatten().toList())
    }

    @Test
    fun `a sequence of results of sequences returns a flattened sequence with failures`() {
        val ex = RuntimeException()
        val unflattened = listOf(
                Success(listOf(1, 2, 3).asSequence()),
                Failure(ex),
                Success(listOf(6).asSequence())
        ).asSequence()
        assertEquals(listOf(Success(1), Success(2), Success(3), Failure(ex), Success(6)), unflattened.flatten().toList())
    }

    @Test
    fun `a sequence of results of sequences is lazily evaluated`() {
        val unflattened = listOf(
                Success(listOf(1, 2, 3).asSequence()),
                Failure(RuntimeException()),
                // Would explode if run.
                Success(listOf(6).asSequence().map { it / 0 })
        ).asSequence()
        assertEquals(listOf(1, 2, 3).map { Success(it) }, unflattened.flatten().takeWhile { it is Success }.toList())
    }
}

class SequenceOfIterablesFlattenTests {
    @Test
    fun `a sequence of results of iterables returns a flattened sequence`() {
        val unflattened = listOf(
                Success(listOf(1, 2, 3)),
                Success(listOf(4, 5)),
                Success(listOf(6))
        ).asSequence()
        assertEquals(listOf(1, 2, 3, 4, 5, 6).map { Success(it) }, unflattened.flatten().toList())
    }

    @Test
    fun `a sequence of results of iterables returns a flattened sequence with failures`() {
        val ex = RuntimeException()
        val unflattened = listOf(
                Success(listOf(1, 2, 3)),
                Failure(ex),
                Success(listOf(6))
        ).asSequence()
        assertEquals(listOf(Success(1), Success(2), Success(3), Failure(ex), Success(6)), unflattened.flatten().toList())
    }

    @Test
    fun `a sequence of results of iterables is lazily evaluated`() {
        val ex = RuntimeException()
        var i = 0
        val unflattened = generateSequence {
            i++
            when (i) {
                1 -> Success(listOf(1, 2, 3))
                2 -> Failure(ex)
                else -> throw RuntimeException("NOT LAZY")
            }
        }
        assertEquals(listOf(1, 2, 3).map { Success(it) }, unflattened.flatten().takeWhile { it is Success }.toList())
    }
}

class IterableOfIterablesFlattenTests {
    @Test
    fun `an iterable of results of iterables returns a flattened iterable`() {
        val unflattened = listOf(
                Success(listOf(1, 2, 3)),
                Success(listOf(4, 5)),
                Success(listOf(6))
        )
        assertEquals(listOf(1, 2, 3, 4, 5, 6).map { Success(it) }, unflattened.flatten())
    }

    @Test
    fun `an iterable of results of iterables returns a flattened iterable with failures`() {
        val ex = RuntimeException()
        val unflattened = listOf(
                Success(listOf(1, 2, 3)),
                Failure(ex),
                Success(listOf(6))
        )
        assertEquals<Iterable<Result<Int, Exception>>>(listOf(Success(1), Success(2), Success(3), Failure(ex), Success(6)), unflattened.flatten())
    }
}
