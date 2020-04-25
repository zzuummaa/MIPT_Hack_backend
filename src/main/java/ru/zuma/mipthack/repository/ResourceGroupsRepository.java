package ru.zuma.mipthack.repository;

import org.springframework.data.repository.CrudRepository;
import ru.zuma.mipthack.domain.ResourceGroup;

public interface ResourceGroupsRepository extends CrudRepository<ResourceGroup, String> {
}
