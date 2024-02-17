package com.wafflestudio.ai.icebreaker.outbound.icebreaking

import com.wafflestudio.ai.icebreaker.application.icebreaking.IceBreakingHistory
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

@Table("ice_breaking_history")
data class IceBreakingHistoryEntity(
    @Id
    val id: Long = 0,
    @Column("meet_up_id")
    val meetUpId: String,
    @Column("type")
    val type: IceBreakingHistory.Type,
    @Column("json")
    val json: String,
) {
    fun toDomain(): IceBreakingHistory {
        return IceBreakingHistory(
            id = id,
            meetUpId = meetUpId,
            type = type,
            json = json
        )
    }

    companion object {
        fun from(iceBreakingHistory: IceBreakingHistory): IceBreakingHistoryEntity {
            return IceBreakingHistoryEntity(
                id = iceBreakingHistory.id,
                meetUpId = iceBreakingHistory.meetUpId,
                type = iceBreakingHistory.type,
                json = iceBreakingHistory.json
            )
        }
    }
}

interface IceBreakingHistoryRepository: CoroutineCrudRepository<IceBreakingHistoryEntity, Long> {
    suspend fun findAllByMeetUpIdAndIdGreaterThanOrderById(meetUpId: String, id: Long): List<IceBreakingHistoryEntity>
}