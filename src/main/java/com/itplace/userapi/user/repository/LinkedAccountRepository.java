package com.itplace.userapi.user.repository;

import com.itplace.userapi.user.entity.LinkedAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkedAccountRepository extends JpaRepository<LinkedAccount, Long> {

    Optional<LinkedAccount> findByProviderAndProviderId(String provider, String providerId);

}
