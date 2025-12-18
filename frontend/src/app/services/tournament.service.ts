// src/app/services/tournament.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Match } from '../models/match.model';

@Injectable({ providedIn: 'root' })
export class TournamentService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrlCore}/matches`;

  // Obtener calendario pendiente
  getScheduledMatches() {
    return this.http.get<Match[]>(`${this.url}/scheduled`);
  }

  // Capturar resultado (Finalizar partido)
  finalizeMatch(id: number, homeScore: number, awayScore: number) {
    return this.http.put(`${this.url}/${id}/finish`, { homeScore, awayScore });
  }
}