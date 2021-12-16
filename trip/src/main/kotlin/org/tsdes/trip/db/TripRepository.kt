package org.tsdes.trip.db

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TripRepository : CrudRepository<Trip, Long>{
    @Query("select t from Trip t where t.userId = :id order by t.id DESC")
    fun findByUserId(@Param("id") userId: String): List<Trip>

    @Query("select t from Trip t where t.destination = :id order by t.id DESC")
    fun findByDestinationId(@Param("id") DestinationId: Long): List<Trip>
}