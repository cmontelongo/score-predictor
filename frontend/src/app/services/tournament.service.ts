// src/app/services/tournament.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Match } from '../models/match.model';

@Injectable({ providedIn: 'root' })
export class TournamentService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrlCore}/matches`;

  getScheduledMatches() {
    return this.http.get<Match[]>(`${this.url}/scheduled`);
  }

  finalizeMatch(id: number, homeScore: number, awayScore: number) {
    return this.http.put(`${this.url}/${id}/finish`, { homeScore, awayScore });
  }
}