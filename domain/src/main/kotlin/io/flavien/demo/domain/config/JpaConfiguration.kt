package io.flavien.demo.domain.config

import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.repository.UserRepository
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EntityScan(basePackageClasses = [User::class])
@EnableJpaRepositories(basePackageClasses = [UserRepository::class])
class JpaConfiguration
