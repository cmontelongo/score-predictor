export interface Match {
  id?: number;
  homeTeam: string;
  awayTeam: string;
  homeScore?: number;
  awayScore?: number;
  matchDate: string;
  stadium: string;
  stage: string;
  status: 'SCHEDULED' | 'LIVE' | 'FINISHED';
}