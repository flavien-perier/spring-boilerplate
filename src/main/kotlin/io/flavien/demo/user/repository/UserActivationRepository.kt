package io.flavien.demo.user.repository

import io.flavien.demo.user.entity.UserActivation
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserActivationRepository : CrudRepository<UserActivation, String> {

    fun deleteByUserId(userId: Long)
}