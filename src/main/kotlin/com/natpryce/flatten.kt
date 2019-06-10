package com.natpryce

/**
 * Flattens out nested sequences contained in results. For instance, if there
 * is a sequence of open database connections that may or may not succeed in
 * returning results and you desire them streamed together, this will enable
 * lazy iteration over the results. Any failing results will be replaced in the
 * sequence with the failure in the original sequence.
 */
fun <T, E> Sequence<Result<Sequence<T>, E>>.flatten(): Sequence<Result<T, E>> {
    val iter = this.iterator()
    return generateSequence {
        if (!iter.hasNext()) {
            // Terminate the sequence
            return@generateSequence null
        }
        when (val nextResult = iter.next()) {
            is Success -> nextResult.value.asSequence().map { Success(it) }
            is Failure -> listOf(nextResult).asSequence()
        }
    }.flatten()
}

/**
 * Flattens out a sequence of result-wrapped iterables. For instance, if there
 * is a sequence of paging API calls being lazily iterated on, this will turn
 * it into one seamless sequence of results, lazily evaluating each element as
 * it's needed. Any failing results will be replaced in the sequence with the
 * failure in the original sequence.
 */
@JvmName("flattenSequenceOfResultOfIterable")
fun <T, E> Sequence<Result<Iterable<T>, E>>.flatten(): Sequence<Result<T, E>> {
    val iter = this.iterator()
    return generateSequence {
        if (!iter.hasNext()) {
            // Terminate the sequence
            return@generateSequence null
        }
        when (val nextResult = iter.next()) {
            is Success -> nextResult.value.asSequence().map { Success(it) }
            is Failure -> listOf(nextResult).asSequence()
        }
    }.flatten()
}

/**
 * Flattens out an iterable of result-wrapped iterables. Any failing results
 * will be replaced in the sequence with the failure in the original sequence.
 */
fun <T, E> Iterable<Result<Iterable<T>, E>>.flatten(): Iterable<Result<T, E>> {
    return this.map {
        when (it) {
            is Success -> it.value.map { s -> Success(s) }
            is Failure -> listOf(it)
        }
    }.flatten()
}
