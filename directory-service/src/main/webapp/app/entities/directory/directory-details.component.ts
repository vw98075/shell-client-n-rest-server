import { Component, Vue, Inject } from 'vue-property-decorator';

import { IDirectory } from '@/shared/model/directory.model';
import DirectoryService from './directory.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class DirectoryDetails extends Vue {
  @Inject('directoryService') private directoryService: () => DirectoryService;
  @Inject('alertService') private alertService: () => AlertService;

  public directory: IDirectory = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.directoryId) {
        vm.retrieveDirectory(to.params.directoryId);
      }
    });
  }

  public retrieveDirectory(directoryId) {
    this.directoryService()
      .find(directoryId)
      .then(res => {
        this.directory = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
