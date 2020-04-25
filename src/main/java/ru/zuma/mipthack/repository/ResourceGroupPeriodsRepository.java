package ru.zuma.mipthack.repository;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.zuma.mipthack.domain.ResourceGroupPeriod;

import javax.persistence.EntityManager;
import java.util.List;


public class ResourceGroupPeriodsRepository extends SimpleJpaRepository<ResourceGroupPeriod, String> {

    private EntityManager entityManager;

    public ResourceGroupPeriodsRepository(EntityManager entityManager) {
        super(ResourceGroupPeriod.class, entityManager);
        this.entityManager=entityManager;
    }

    @Transactional
    public List<ResourceGroupPeriod> save(List<ResourceGroupPeriod> resourceGroupPeriod) {
        resourceGroupPeriod.forEach(thing -> entityManager.persist(resourceGroupPeriod));
        return resourceGroupPeriod;
    }
}
