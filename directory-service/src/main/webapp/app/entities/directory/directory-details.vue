<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="directory">
        <h2 class="jh-entity-heading" data-cy="directoryDetailsHeading"><span>Directory</span> {{ directory.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Path</span>
          </dt>
          <dd>
            <span>{{ directory.path }}</span>
          </dd>
          <dt>
            <span>Created Time</span>
          </dt>
          <dd>
            <span>{{ directory.createdTime | formatDate }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span> Back</span>
        </button>
        <router-link
          v-if="directory.id"
          :to="{ name: 'DirectoryEdit', params: { directoryId: directory.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./directory-details.component.ts"></script>
