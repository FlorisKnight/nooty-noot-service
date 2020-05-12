package com.nooty.nootynoot;

import com.nooty.nootynoot.models.Noot;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface NootRepo extends CrudRepository<Noot, String> {

    Iterable<Noot> findAllByUserId(String userId);
}
