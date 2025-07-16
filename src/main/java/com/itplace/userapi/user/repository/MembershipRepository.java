package com.itplace.userapi.user.repository;

import com.itplace.userapi.user.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, String> {
}
