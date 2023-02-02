package org.shrek.repository;

import com.shrek.model.ClientDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<ClientDTO, Long> {
}
