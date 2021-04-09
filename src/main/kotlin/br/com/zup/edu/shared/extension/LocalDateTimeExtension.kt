package br.com.zup.edu.shared.extension

import com.google.protobuf.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.toTimestamp(): Timestamp {
    val createdAt = this.atZone(ZoneId.of("UTC")).toInstant()
    return Timestamp.newBuilder()
        .setSeconds(createdAt.epochSecond)
        .setNanos(createdAt.nano)
        .build()
}