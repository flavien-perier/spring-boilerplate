package io.flavien.demo.user.repository

import io.flavien.demo.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun existsByEmail(email: String): Boolean

    fun getByEmail(email: String): Optional<User>

    fun getUserById(userId: Long): Optional<User>

    @Query("SELECT u FROM app_user u WHERE u.email LIKE %:query%")
    fun find(query: String, page: Pageable): Page<User>
}