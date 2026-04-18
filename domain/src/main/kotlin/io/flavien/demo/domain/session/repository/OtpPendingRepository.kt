package io.flavien.demo.domain.session.repository

import io.flavien.demo.domain.session.entity.OtpPending
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OtpPendingRepository : CrudRepository<OtpPending, String>
