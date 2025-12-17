import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Prediction } from '../models/prediction.model';

@Injectable({ providedIn: 'root' })
export class OracleService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrlOracle}`;

  getPredictions() {
    return this.http.get<Prediction[]>(`${this.url}/predict`);
  }
}