package cmpe273.group6.client.Service;

import cmpe273.group6.client.Entity.Camera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Long> {
    Camera findCameraById(long Id);
}
