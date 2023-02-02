package org.shrek.repository;

import com.shrek.model.CreditDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditRepository extends JpaRepository<CreditDTO, Long> {
}
