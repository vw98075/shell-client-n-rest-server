package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Directory;
import com.mycompany.myapp.repository.DirectoryRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DirectoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DirectoryResourceIT {

    private static final String DEFAULT_PATH = "AAAAAAAAAA";
    private static final String UPDATED_PATH = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/directories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DirectoryRepository directoryRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDirectoryMockMvc;

    private Directory directory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Directory createEntity(EntityManager em) {
        Directory directory = new Directory().path(DEFAULT_PATH).createdTime(DEFAULT_CREATED_TIME);
        return directory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Directory createUpdatedEntity(EntityManager em) {
        Directory directory = new Directory().path(UPDATED_PATH).createdTime(UPDATED_CREATED_TIME);
        return directory;
    }

    @BeforeEach
    public void initTest() {
        directory = createEntity(em);
    }

    @Test
    @Transactional
    void createDirectory() throws Exception {
        int databaseSizeBeforeCreate = directoryRepository.findAll().size();
        // Create the Directory
        restDirectoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(directory)))
            .andExpect(status().isCreated());

        // Validate the Directory in the database
        List<Directory> directoryList = directoryRepository.findAll();
        assertThat(directoryList).hasSize(databaseSizeBeforeCreate + 1);
        Directory testDirectory = directoryList.get(directoryList.size() - 1);
        assertThat(testDirectory.getPath()).isEqualTo(DEFAULT_PATH);
        assertThat(testDirectory.getCreatedTime()).isEqualTo(DEFAULT_CREATED_TIME);
    }

    @Test
    @Transactional
    void createDirectoryWithExistingId() throws Exception {
        // Create the Directory with an existing ID
        directory.setId(1L);

        int databaseSizeBeforeCreate = directoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDirectoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(directory)))
            .andExpect(status().isBadRequest());

        // Validate the Directory in the database
        List<Directory> directoryList = directoryRepository.findAll();
        assertThat(directoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPathIsRequired() throws Exception {
        int databaseSizeBeforeTest = directoryRepository.findAll().size();
        // set the field null
        directory.setPath(null);

        // Create the Directory, which fails.

        restDirectoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(directory)))
            .andExpect(status().isBadRequest());

        List<Directory> directoryList = directoryRepository.findAll();
        assertThat(directoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDirectories() throws Exception {
        // Initialize the database
        directoryRepository.saveAndFlush(directory);

        // Get all the directoryList
        restDirectoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(directory.getId().intValue())))
            .andExpect(jsonPath("$.[*].path").value(hasItem(DEFAULT_PATH)))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())));
    }

    @Test
    @Transactional
    void getDirectory() throws Exception {
        // Initialize the database
        directoryRepository.saveAndFlush(directory);

        // Get the directory
        restDirectoryMockMvc
            .perform(get(ENTITY_API_URL_ID, directory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(directory.getId().intValue()))
            .andExpect(jsonPath("$.path").value(DEFAULT_PATH))
            .andExpect(jsonPath("$.createdTime").value(DEFAULT_CREATED_TIME.toString()));
    }

    @Test
    @Transactional
    void getNonExistingDirectory() throws Exception {
        // Get the directory
        restDirectoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDirectory() throws Exception {
        // Initialize the database
        directoryRepository.saveAndFlush(directory);

        int databaseSizeBeforeUpdate = directoryRepository.findAll().size();

        // Update the directory
        Directory updatedDirectory = directoryRepository.findById(directory.getId()).get();
        // Disconnect from session so that the updates on updatedDirectory are not directly saved in db
        em.detach(updatedDirectory);
        updatedDirectory.path(UPDATED_PATH).createdTime(UPDATED_CREATED_TIME);

        restDirectoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDirectory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedDirectory))
            )
            .andExpect(status().isOk());

        // Validate the Directory in the database
        List<Directory> directoryList = directoryRepository.findAll();
        assertThat(directoryList).hasSize(databaseSizeBeforeUpdate);
        Directory testDirectory = directoryList.get(directoryList.size() - 1);
        assertThat(testDirectory.getPath()).isEqualTo(UPDATED_PATH);
        assertThat(testDirectory.getCreatedTime()).isEqualTo(UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    void putNonExistingDirectory() throws Exception {
        int databaseSizeBeforeUpdate = directoryRepository.findAll().size();
        directory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDirectoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, directory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(directory))
            )
            .andExpect(status().isBadRequest());

        // Validate the Directory in the database
        List<Directory> directoryList = directoryRepository.findAll();
        assertThat(directoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDirectory() throws Exception {
        int databaseSizeBeforeUpdate = directoryRepository.findAll().size();
        directory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(directory))
            )
            .andExpect(status().isBadRequest());

        // Validate the Directory in the database
        List<Directory> directoryList = directoryRepository.findAll();
        assertThat(directoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDirectory() throws Exception {
        int databaseSizeBeforeUpdate = directoryRepository.findAll().size();
        directory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(directory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Directory in the database
        List<Directory> directoryList = directoryRepository.findAll();
        assertThat(directoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDirectoryWithPatch() throws Exception {
        // Initialize the database
        directoryRepository.saveAndFlush(directory);

        int databaseSizeBeforeUpdate = directoryRepository.findAll().size();

        // Update the directory using partial update
        Directory partialUpdatedDirectory = new Directory();
        partialUpdatedDirectory.setId(directory.getId());

        partialUpdatedDirectory.path(UPDATED_PATH).createdTime(UPDATED_CREATED_TIME);

        restDirectoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDirectory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDirectory))
            )
            .andExpect(status().isOk());

        // Validate the Directory in the database
        List<Directory> directoryList = directoryRepository.findAll();
        assertThat(directoryList).hasSize(databaseSizeBeforeUpdate);
        Directory testDirectory = directoryList.get(directoryList.size() - 1);
        assertThat(testDirectory.getPath()).isEqualTo(UPDATED_PATH);
        assertThat(testDirectory.getCreatedTime()).isEqualTo(UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    void fullUpdateDirectoryWithPatch() throws Exception {
        // Initialize the database
        directoryRepository.saveAndFlush(directory);

        int databaseSizeBeforeUpdate = directoryRepository.findAll().size();

        // Update the directory using partial update
        Directory partialUpdatedDirectory = new Directory();
        partialUpdatedDirectory.setId(directory.getId());

        partialUpdatedDirectory.path(UPDATED_PATH).createdTime(UPDATED_CREATED_TIME);

        restDirectoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDirectory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDirectory))
            )
            .andExpect(status().isOk());

        // Validate the Directory in the database
        List<Directory> directoryList = directoryRepository.findAll();
        assertThat(directoryList).hasSize(databaseSizeBeforeUpdate);
        Directory testDirectory = directoryList.get(directoryList.size() - 1);
        assertThat(testDirectory.getPath()).isEqualTo(UPDATED_PATH);
        assertThat(testDirectory.getCreatedTime()).isEqualTo(UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    void patchNonExistingDirectory() throws Exception {
        int databaseSizeBeforeUpdate = directoryRepository.findAll().size();
        directory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDirectoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, directory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(directory))
            )
            .andExpect(status().isBadRequest());

        // Validate the Directory in the database
        List<Directory> directoryList = directoryRepository.findAll();
        assertThat(directoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDirectory() throws Exception {
        int databaseSizeBeforeUpdate = directoryRepository.findAll().size();
        directory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(directory))
            )
            .andExpect(status().isBadRequest());

        // Validate the Directory in the database
        List<Directory> directoryList = directoryRepository.findAll();
        assertThat(directoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDirectory() throws Exception {
        int databaseSizeBeforeUpdate = directoryRepository.findAll().size();
        directory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectoryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(directory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Directory in the database
        List<Directory> directoryList = directoryRepository.findAll();
        assertThat(directoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDirectory() throws Exception {
        // Initialize the database
        directoryRepository.saveAndFlush(directory);

        int databaseSizeBeforeDelete = directoryRepository.findAll().size();

        // Delete the directory
        restDirectoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, directory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Directory> directoryList = directoryRepository.findAll();
        assertThat(directoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
