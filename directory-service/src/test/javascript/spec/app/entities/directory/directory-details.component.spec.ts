/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import DirectoryDetailComponent from '@/entities/directory/directory-details.vue';
import DirectoryClass from '@/entities/directory/directory-details.component';
import DirectoryService from '@/entities/directory/directory.service';
import router from '@/router';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Directory Management Detail Component', () => {
    let wrapper: Wrapper<DirectoryClass>;
    let comp: DirectoryClass;
    let directoryServiceStub: SinonStubbedInstance<DirectoryService>;

    beforeEach(() => {
      directoryServiceStub = sinon.createStubInstance<DirectoryService>(DirectoryService);

      wrapper = shallowMount<DirectoryClass>(DirectoryDetailComponent, {
        store,
        localVue,
        router,
        provide: { directoryService: () => directoryServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundDirectory = { id: 123 };
        directoryServiceStub.find.resolves(foundDirectory);

        // WHEN
        comp.retrieveDirectory(123);
        await comp.$nextTick();

        // THEN
        expect(comp.directory).toBe(foundDirectory);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundDirectory = { id: 123 };
        directoryServiceStub.find.resolves(foundDirectory);

        // WHEN
        comp.beforeRouteEnter({ params: { directoryId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.directory).toBe(foundDirectory);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        comp.previousState();
        await comp.$nextTick();

        expect(comp.$router.currentRoute.fullPath).toContain('/');
      });
    });
  });
});
