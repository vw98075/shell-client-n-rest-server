import { Component, Vue, Inject } from 'vue-property-decorator';

import { required } from 'vuelidate/lib/validators';
import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '@/shared/date/filters';

import AlertService from '@/shared/alert/alert.service';

import { IDirectory, Directory } from '@/shared/model/directory.model';
import DirectoryService from './directory.service';

const validations: any = {
  directory: {
    path: {
      required,
    },
    createdTime: {},
  },
};

@Component({
  validations,
})
export default class DirectoryUpdate extends Vue {
  @Inject('directoryService') private directoryService: () => DirectoryService;
  @Inject('alertService') private alertService: () => AlertService;

  public directory: IDirectory = new Directory();
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.directoryId) {
        vm.retrieveDirectory(to.params.directoryId);
      }
    });
  }

  created(): void {
    this.currentLanguage = this.$store.getters.currentLanguage;
    this.$store.watch(
      () => this.$store.getters.currentLanguage,
      () => {
        this.currentLanguage = this.$store.getters.currentLanguage;
      }
    );
  }

  public save(): void {
    this.isSaving = true;
    if (this.directory.id) {
      this.directoryService()
        .update(this.directory)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Directory is updated with identifier ' + param.id;
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    } else {
      this.directoryService()
        .create(this.directory)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Directory is created with identifier ' + param.id;
          this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Success',
            variant: 'success',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    }
  }

  public convertDateTimeFromServer(date: Date): string {
    if (date && dayjs(date).isValid()) {
      return dayjs(date).format(DATE_TIME_LONG_FORMAT);
    }
    return null;
  }

  public updateInstantField(field, event) {
    if (event.target.value) {
      this.directory[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.directory[field] = null;
    }
  }

  public updateZonedDateTimeField(field, event) {
    if (event.target.value) {
      this.directory[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.directory[field] = null;
    }
  }

  public retrieveDirectory(directoryId): void {
    this.directoryService()
      .find(directoryId)
      .then(res => {
        res.createdTime = new Date(res.createdTime);
        this.directory = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {}
}
