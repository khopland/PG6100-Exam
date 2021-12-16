package org.tsdes.port

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.tsdes.port.service.PortRepository
import org.tsdes.port.service.PortService

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class PortServiceTest @Autowired constructor(
    private val service: PortService,
    private val repository: PortRepository
) {
    @BeforeEach
    fun init(){
        repository.deleteAll()
    }
    @Test
    fun testInit() {
        assertTrue(repository.count() == 0L)
    }

    @Test
    fun testCreatePort() {
        val n = repository.count()
        service.registerNewPort("Bar001", "stormy")
        assertEquals(n + 1, repository.count())
    }

    @Test
    fun testCreateAndGetPort() {
        val n = repository.count()
        val res = service.registerNewPort("Bar001", "stormy")
        assertEquals(n + 1, repository.count())
        val port = service.getById(res.id)
        assertEquals(res.id, port?.id)
        assertEquals(res.name, port?.name)
        assertEquals(res.weather, port?.weather)
    }

    @Test
    fun testPage() {
        val n = 5
        for (i in 0 until n)
            service.registerNewPort("test", "stormy")
        val page = service.getNextPage(n)
        assertEquals(n, page.size)
        for (i in 0 until n - 1)
            assertTrue(page[i].id <= page[i + 1].id)
    }

}