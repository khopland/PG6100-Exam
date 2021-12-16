package org.tsdes.port.service

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.tsdes.port.db.Port
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Repository
interface PortRepository : CrudRepository<Port, Long>


@Service
@Transactional
class PortService(
    private val repository: PortRepository, private val em: EntityManager
) {
    fun registerNewPort(name: String, weather: String): Port = repository.save(Port(0, name, weather))

    fun getById(id: Long): Port? = repository.findById(id).orElse(null)

    fun updateWhether(id: Long, whether: String): Boolean {
        val port = repository.findById(id).orElse(null).apply { this?.weather = whether } ?: return false
        repository.save(port)
        return true
    }

    fun getNextPage(size: Int, keysetId: Long? = null): List<Port> = when {
        size < 1 || size > 1000 -> throw IllegalArgumentException("Invalid size value: $size")

        else -> when (keysetId) {
            null -> em.createQuery(
                "select p from Port p order by p.id DESC", Port::class.java
            ).apply { maxResults = size }.resultList
            else -> em.createQuery(
                "select p from Port p where p.id<?1 order by p.id DESC,p.name DESC", Port::class.java
            ).setParameter(1, keysetId).apply { maxResults = size }.resultList
        }
    }
}