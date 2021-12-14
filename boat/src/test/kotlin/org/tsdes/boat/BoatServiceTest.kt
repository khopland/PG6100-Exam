package org.tsdes.boat

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.tsdes.boat.db.toDto

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BoatServiceTest @Autowired constructor(
    private val service: BoatService,
    private val repository: BoatRepository
) {
    @Test
    fun testInit() {
        assertTrue(repository.count() == 0L)
    }

    @Test
    fun testCreatePort() {
        val n = repository.count()
        service.registerNewBoat("test", "batty",2)
        assertEquals(n + 1, repository.count())
    }

    @Test
    fun testCreateAndGetPort() {
        val n = repository.count()
        val res = service.registerNewBoat("test", "batty",2)
        val name = res.name
        val id = res.id
        assertEquals(n + 1, repository.count())
        val boat = service.getById(id)
        assertEquals(id, boat?.id)
        assertEquals(name, boat?.name)
    }
    @Test
    fun testCreateAndUpdatePort() {
        val n = repository.count()
        val res = service.registerNewBoat("test", "batty",2)
        val name = res.name
        val id = res.id
        assertEquals(n + 1, repository.count())
        assertTrue( service.updateBoat(res.apply { this.name= "yo" }.toDto()))
        val boat = service.getById(res.id)
        assertEquals(id, boat?.id)
        assertNotEquals(name, boat?.name)
    }

    @Test
    fun testPage() {
        val n = 5
        for (i in 0 until n)
            service.registerNewBoat("test_$n", "batty",2)
        val page = service.getNextPage(n)
        assertEquals(n, page.size)
        for (i in 0 until n - 1)
            assertTrue(page[i].id >= page[i + 1].id)
    }

}