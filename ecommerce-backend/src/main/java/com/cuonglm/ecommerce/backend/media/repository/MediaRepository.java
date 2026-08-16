package com.cuonglm.ecommerce.backend.media.repository;

import com.cuonglm.ecommerce.backend.media.dto.internal.MediaInfoView;
import com.cuonglm.ecommerce.backend.media.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * MediaRepository – Repository của Media.
 *
 * @author cuonglmptit
 * @since Monday, 24 November 2025
 */
@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {
    Optional<MediaInfoView> findMediaInfoById(UUID id);

    List<MediaInfoView> findMediaInfoByIdIn(Collection<UUID> ids);
}
