package com.tenpo.challenge.repository;

import com.tenpo.challenge.model.Call;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CallRepository extends CrudRepository<Call, UUID> {
}
