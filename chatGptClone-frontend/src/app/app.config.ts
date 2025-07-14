import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  provideZonelessChangeDetection
} from '@angular/core';
import { provideHttpClient } from '@angular/common/http';


import { provideRouter } from '@angular/router';
// import { provideForms } from '@angular/forms';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideHttpClient()
    //provideForms() // ✅ Permet l’utilisation de ngModel dans tous les composants
  ]
};
