package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Directory;
import com.mycompany.myapp.repository.DirectoryRepository;

import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Directory}.
 */
@Service
@Transactional
public class DirectoryService {

    private final Logger log = LoggerFactory.getLogger(DirectoryService.class);

    private final DirectoryRepository directoryRepository;

    public DirectoryService(DirectoryRepository directoryRepository) {
        this.directoryRepository = directoryRepository;
    }

    /**
     * Save a directory.
     *
     * @param directory the entity to save.
     * @return the persisted entity.
     */
    public Directory save(Directory directory) {
        log.debug("Request to save Directory : {}", directory);
        if(directory.getCreatedTime() == null)
            directory.setCreatedTime(Instant.now());
        return directoryRepository.save(directory);
    }

    /**
     * Update a directory.
     *
     * @param directory the entity to save.
     * @return the persisted entity.
     */
    public Directory update(Directory directory) {
        log.debug("Request to save Directory : {}", directory);
        return directoryRepository.save(directory);
    }

    /**
     * Partially update a directory.
     *
     * @param directory the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Directory> partialUpdate(Directory directory) {
        log.debug("Request to partially update Directory : {}", directory);

        return directoryRepository
            .findById(directory.getId())
            .map(existingDirectory -> {
                if (directory.getPath() != null) {
                    existingDirectory.setPath(directory.getPath());
                }
                if (directory.getCreatedTime() != null) {
                    existingDirectory.setCreatedTime(directory.getCreatedTime());
                }

                return existingDirectory;
            })
            .map(directoryRepository::save);
    }

    /**
     * Get all the directories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Directory> findAll(Pageable pageable) {
        log.debug("Request to get all Directories");
        return directoryRepository.findAll(pageable);
    }

    /**
     * Get one directory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Directory> findOne(Long id) {
        log.debug("Request to get Directory : {}", id);
        return directoryRepository.findById(id);
    }

    /**
     * Delete the directory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Directory : {}", id);
        directoryRepository.deleteById(id);
    }
}
