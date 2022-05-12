import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

// prettier-ignore
const Directory = () => import('@/entities/directory/directory.vue');
// prettier-ignore
const DirectoryUpdate = () => import('@/entities/directory/directory-update.vue');
// prettier-ignore
const DirectoryDetails = () => import('@/entities/directory/directory-details.vue');
// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'directory',
      name: 'Directory',
      component: Directory,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'directory/new',
      name: 'DirectoryCreate',
      component: DirectoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'directory/:directoryId/edit',
      name: 'DirectoryEdit',
      component: DirectoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'directory/:directoryId/view',
      name: 'DirectoryView',
      component: DirectoryDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
