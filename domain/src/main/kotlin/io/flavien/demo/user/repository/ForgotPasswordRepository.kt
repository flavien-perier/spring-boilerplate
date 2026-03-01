package io.flavien.demo.user.repository

import io.flavien.demo.user.entity.ForgotPassword
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ForgotPasswordRepository  : CrudRepository<ForgotPassword, String> {

    fun deleteByUserId(userId: Long)
}