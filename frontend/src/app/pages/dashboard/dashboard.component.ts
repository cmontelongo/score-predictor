import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TournamentService } from '../../services/tournament.service';
import { OracleService } from '../../services/oracle.service';
import { Match } from '../../models/match.model';
import { Prediction } from '../../models/prediction.model';
import { forkJoin } from 'rxjs'; // <--- AGREGAR ESTO
import Swal from 'sweetalert2'; // <--- AGREGAR ESTO

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  private tournamentService = inject(TournamentService);
  private oracleService = inject(OracleService);
  private cdr = inject(ChangeDetectorRef);

  matches: Match[] = [];
  predictions: Prediction[] = [];

  scoreInputs: { [key: number]: { home: number, away: number } } = {};

  isLoading = true;

  countryCodes: { [key: string]: string } = {
    // --- EUROPE (UEFA) ---
    'France': 'fr',
    'England': 'gb-eng',
    'Belgium': 'be',
    'Spain': 'es',
    'Netherlands': 'nl',
    'Portugal': 'pt',
    'Italy': 'it',
    'Germany': 'de',
    'Croatia': 'hr',
    'Denmark': 'dk',
    'Switzerland': 'ch', // Confoederatio Helvetica
    'Serbia': 'rs',
    'Poland': 'pl',
    'Wales': 'gb-wls',
    'Ukraine': 'ua',
    'Sweden': 'se',
    'Norway': 'no',
    'Turkey': 'tr',
    'Austria': 'at',
    'Scotland': 'gb-sct',
    'Czech Republic': 'cz',
    'Hungary': 'hu',
    'Greece': 'gr',
    'Russia': 'ru',
    'Slovakia': 'sk',
    'Romania': 'ro',
    'Ireland': 'ie',
    'Finland': 'fi',
    'Albania': 'al',
    'Slovenia': 'si',
    'North Macedonia': 'mk',
    'Iceland': 'is',
    'Bosnia and Herzegovina': 'ba',

    // --- SOUTH AMERICA (CONMEBOL) ---
    'Brazil': 'br',
    'Argentina': 'ar',
    'Uruguay': 'uy',
    'Colombia': 'co',
    'Ecuador': 'ec',
    'Chile': 'cl',
    'Peru': 'pe',
    'Paraguay': 'py',
    'Venezuela': 've',
    'Bolivia': 'bo',

    // --- NORTH AMERICA (CONCACAF) ---
    'United States': 'us',
    'USA': 'us',
    'Mexico': 'mx',
    'Canada': 'ca',
    'Costa Rica': 'cr',
    'Panama': 'pa',
    'Jamaica': 'jm',
    'Honduras': 'hn',
    'El Salvador': 'sv',
    'Guatemala': 'gt',
    'Haiti': 'ht',

    // --- AFRICA (CAF) ---
    'Morocco': 'ma',
    'Senegal': 'sn',
    'Tunisia': 'tn',
    'Cameroon': 'cm',
    'Ghana': 'gh',
    'Egypt': 'eg',
    'Nigeria': 'ng',
    'Algeria': 'dz',
    'Ivory Coast': 'ci',
    'Mali': 'ml',
    'Burkina Faso': 'bf',
    'South Africa': 'za',

    // --- ASIA & OCEANIA (AFC/OFC) ---
    'Japan': 'jp',
    'Iran': 'ir',
    'South Korea': 'kr',
    'Korea Republic': 'kr',
    'Australia': 'au',
    'Saudi Arabia': 'sa',
    'Qatar': 'qa',
    'Iraq': 'iq',
    'United Arab Emirates': 'ae',
    'Uzbekistan': 'uz',
    'China': 'cn',
    'New Zealand': 'nz',
    'Oman': 'om',
    'Jordan': 'jo',
    'Bahrain': 'bh',
    'Kuwait': 'kw'
  };

  getFlagUrl(teamName: string): string {
    const code = this.countryCodes[teamName];
    if (code) {
      return `https://flagcdn.com/${code}.svg`;
    }
    return 'https://flagcdn.com/un.svg';
  }

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.isLoading = true;

    forkJoin({
      matches: this.tournamentService.getScheduledMatches(),
      predictions: this.oracleService.getPredictions()
    }).subscribe({
      next: (response) => {
        const matchesData = response.matches || [];
        const allPredictions = response.predictions || [];

        this.initializeInputs(matchesData);
        this.matches = matchesData;

        const relevantPredictions = allPredictions.filter(pred =>
          matchesData.some(match =>
            match.homeTeam === pred.homeTeam &&
            match.awayTeam === pred.awayTeam
          )
        );

        const uniquePredictionsMap = new Map();

        relevantPredictions.forEach(p => {
          const key = `${p.homeTeam}-${p.awayTeam}`;

          uniquePredictionsMap.set(key, p);
        });

        this.predictions = Array.from(uniquePredictionsMap.values());

        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error:', err);
        this.isLoading = false;
      }
    });
  }

  private initializeInputs(matches: Match[]) {
    const newInputs = { ...this.scoreInputs };
    let hasChanges = false;

    matches.forEach(m => {
      if (m.id && !newInputs[m.id]) {
        newInputs[m.id] = { home: 0, away: 0 };
        hasChanges = true;
      }
    });

    if (hasChanges) {
      this.scoreInputs = { ...newInputs };
    }
  }

  saveResult(matchId: number) {
    const scores = this.scoreInputs[matchId];
    if (!scores) return;

    const match = this.matches.find(m => m.id === matchId);
    const homeName = match ? match.homeTeam : 'Home';
    const awayName = match ? match.awayTeam : 'Away';

    Swal.fire({
      title: 'Finalize Match?',
      html: `
        <div style="font-size: 1.1rem; color: #cbd5e1; margin-bottom: 10px;">
          The following score will be recorded:
        </div>
        <div style="font-size: 1.5rem; font-weight: 800; color: #fbbf24; display: flex; justify-content: center; gap: 10px; align-items: center;">
           <span>${homeName}</span> 
           <span style="font-size: 2rem; color: white;">${scores.home} - ${scores.away}</span> 
           <span>${awayName}</span>
        </div>
      `,
      icon: 'question',
      showCancelButton: true,

      background: '#1e293b',
      color: '#fff',

      confirmButtonText: 'Yes, Confirm',
      confirmButtonColor: '#fbbf24',

      cancelButtonText: 'Cancel',
      cancelButtonColor: '#ef4444',
      
      showClass: { popup: 'swal2-show', backdrop: 'swal2-backdrop-show', icon: 'swal2-icon-show' },
      hideClass: { popup: 'swal2-hide', backdrop: 'swal2-backdrop-hide', icon: 'swal2-icon-hide' }
    }).then((result) => {

      if (result.isConfirmed) {

        Swal.fire({
          title: 'Processing...',
          text: 'Updating table and retraining AI oracle.',
          background: '#1e293b',
          color: '#fff',
          allowOutsideClick: false,
          didOpen: () => {
            Swal.showLoading();
          }
        });

        this.tournamentService.finalizeMatch(matchId, scores.home, scores.away)
          .subscribe({
            next: () => {
              Swal.fire({
                title: 'Match Finalized!',
                text: 'The oracle has updated its predictions.',
                icon: 'success',
                background: '#1e293b',
                color: '#fff',
                confirmButtonColor: '#3b82f6'
              });

              const currentInputs = { ...this.scoreInputs };
              delete currentInputs[matchId];
              this.scoreInputs = currentInputs;

              this.loadData();
            },
            error: (err) => {
              Swal.fire({
                title: 'Error',
                text: 'Error saving results.',
                icon: 'error',
                background: '#1e293b',
                color: '#fff'
              });
            }
          });
      }
    });
  }

  getPrediction(home: string, away: string): Prediction | undefined {
    return this.predictions.find(p => p.homeTeam === home && p.awayTeam === away);
  }
}