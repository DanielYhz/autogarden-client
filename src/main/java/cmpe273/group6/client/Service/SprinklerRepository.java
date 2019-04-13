package cmpe273.group6.client.Service;


import cmpe273.group6.client.Entity.Sprinkler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprinklerRepository extends JpaRepository<Sprinkler, Long> {
    Sprinkler findSprinklerById(long Id);
}


