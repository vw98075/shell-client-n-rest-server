<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="directoryserviceApp.directory.home.createOrEditLabel" data-cy="DirectoryCreateUpdateHeading">Create or edit a Directory</h2>
        <div>
          <div class="form-group" v-if="directory.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="directory.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="directory-path">Path</label>
            <input
              type="text"
              class="form-control"
              name="path"
              id="directory-path"
              data-cy="path"
              :class="{ valid: !$v.directory.path.$invalid, invalid: $v.directory.path.$invalid }"
              v-model="$v.directory.path.$model"
              required
            />
            <div v-if="$v.directory.path.$anyDirty && $v.directory.path.$invalid">
              <small class="form-text text-danger" v-if="!$v.directory.path.required"> This field is required. </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="directory-createdTime">Created Time</label>
            <div class="d-flex">
              <input
                id="directory-createdTime"
                data-cy="createdTime"
                type="datetime-local"
                class="form-control"
                name="createdTime"
                :class="{ valid: !$v.directory.createdTime.$invalid, invalid: $v.directory.createdTime.$invalid }"
                :value="convertDateTimeFromServer($v.directory.createdTime.$model)"
                @change="updateInstantField('createdTime', $event)"
              />
            </div>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span>Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="$v.directory.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./directory-update.component.ts"></script>
