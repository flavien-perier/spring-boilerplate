package io.flavien.demo.session.repository

import io.flavien.demo.session.entity.AccessToken
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccessTokenRepository : CrudRepository<AccessToken, String> {

    fun deleteByUserId(userId: Long)
}