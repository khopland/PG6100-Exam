package org.tsdes.boat

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.tsdes.boat.db.Boat
import org.tsdes.dto.BoatDto
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Repository
interface BoatRepository : CrudRepository<Boat, Long>


@Service
@Transactional
class BoatService(
    private val repository: BoatRepository,
    private val em: EntityManager
) {
    fun registerNewBoat(name: String, builder: String, numberOfCrew: Int): Boat =
        repository.save(Boat(0, name, builder, numberOfCrew))

    fun updateBoat(dto: BoatDto): Boolean = updateBoat(dto.id!!, dto.name, dto.builder, dto.numberOfCrew)

    fun updateBoat(id: Long, name: String, builder: String, numberOfCrew: Int): Boolean {
        return if (!repository.existsById(id)) false
        else {
            repository.save(Boat(id, name, builder, numberOfCrew))
            true
        }


    }

    fun getById(id: Long): Boat? = repository.findById(id).orElse(null)

    fun getNextPage(size: Int, keysetId: Long? = null): List<Boat> = when {
        size < 1 || size > 1000 -> throw IllegalArgumentException("Invalid size value: $size")

        else -> when (keysetId) {
            null -> em.createQuery(
                "select p from Boat p order by p.id DESC",
                Boat::class.java
            ).apply { maxResults = size }.resultList
            else -> em.createQuery(
                "select p from Boat p where p.id<?1 order by p.id DESC,p.name DESC",
                Boat::class.java
            ).setParameter(1, keysetId).apply { maxResults = size }.resultList
        }
    }
}