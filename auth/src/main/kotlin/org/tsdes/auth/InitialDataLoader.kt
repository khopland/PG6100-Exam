package org.tsdes.auth

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.tsdes.auth.db.UserRepository
import org.tsdes.auth.db.UserService

@Component
class InitialDataLoader(
    private val repo: UserRepository,
    private val service: UserService
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        createAdminUser()
    }

    /**
     * Creates a Admin user for the service
     * MUST BE REMOVED IF USED IN PRODUCTION
     * Only implemented for demo purposes
     */
    private fun createAdminUser() {
        if (!repo.findById("admin").isPresent) {
            service.createUser("admin", "admin", setOf("USER", "ADMIN"))
        }
    }

}