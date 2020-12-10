package com.cryptographyServer.cryptography.server.repository;

import com.cryptographyServer.cryptography.server.entity.KeyEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyRepository extends CrudRepository<KeyEntity, Integer> {
    KeyEntity findByUniqueID(String uniqueID);
}
