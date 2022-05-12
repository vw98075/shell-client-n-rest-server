<template>
  <div>
    <h2 id="page-heading" data-cy="DirectoryHeading">
      <span id="directory-heading">Directories</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh List</span>
        </button>
        <router-link :to="{ name: 'DirectoryCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-directory"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span> Create a new Directory </span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && directories && directories.length === 0">
      <span>No directories found</span>
    </div>
    <div class="table-responsive" v-if="directories && directories.length > 0">
      <table class="table table-striped" aria-describedby="directories">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span>ID</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('path')">
              <span>Path</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'path'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('createdTime')">
              <span>Created Time</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdTime'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="directory in directories" :key="directory.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'DirectoryView', params: { directoryId: directory.id } }">{{ directory.id }}</router-link>
            </td>
            <td>{{ directory.path }}</td>
            <td>{{ directory.createdTime | formatDate }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'DirectoryView', params: { directoryId: directory.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'DirectoryEdit', params: { directoryId: directory.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(directory)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline">Delete</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
        <infinite-loading
          ref="infiniteLoading"
          v-if="totalItems > itemsPerPage"
          :identifier="infiniteId"
          slot="append"
          @infinite="loadMore"
          force-use-infinite-wrapper=".el-table__body-wrapper"
          :distance="20"
        >
        </infinite-loading>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <span slot="modal-title"
        ><span id="directoryserviceApp.directory.delete.question" data-cy="directoryDeleteDialogHeading"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-directory-heading">Are you sure you want to delete this Directory?</p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-directory"
          data-cy="entityConfirmDeleteButton"
          v-on:click="removeDirectory()"
        >
          Delete
        </button>
      </div>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./directory.component.ts"></script>
