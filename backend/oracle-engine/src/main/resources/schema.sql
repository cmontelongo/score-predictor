CREATE TABLE IF NOT EXISTS historical_matches (
    id SERIAL PRIMARY KEY,
    match_date DATE NOT NULL,
    home_team VARCHAR(100) NOT NULL,
    away_team VARCHAR(100) NOT NULL,
    home_score INTEGER NOT NULL,
    away_score INTEGER NOT NULL,
    tournament_name VARCHAR(100),
    importance_factor INTEGER DEFAULT 40
);

CREATE TABLE IF NOT EXISTS team_ratings (
    id SERIAL PRIMARY KEY,
    team_name VARCHAR(100) NOT NULL UNIQUE,
    elo_rating INTEGER NOT NULL,
    attack_strength DECIMAL(5,2),
    defense_strength DECIMAL(5,2)
);

INSERT INTO team_ratings (team_name, elo_rating, attack_strength, defense_strength)
VALUES 
    -- === UEFA (Europe) - Tier 1 ===
    ('France', 2140, 2.3, 0.7),
    ('England', 2090, 2.1, 0.8),
    ('Belgium', 2070, 2.0, 0.9),
    ('Spain', 2060, 1.9, 0.8),
    ('Netherlands', 2050, 1.9, 0.9),
    ('Portugal', 2040, 2.0, 0.9),
    ('Italy', 2030, 1.6, 0.7),      -- Strong defense, missed WC but high Elo
    ('Germany', 2020, 2.1, 1.1),    -- High attack, vulnerable defense
    ('Croatia', 1980, 1.5, 0.8),
    ('Denmark', 1950, 1.5, 0.9),

    -- === CONMEBOL (South America) ===
    ('Brazil', 2160, 2.5, 0.6),
    ('Argentina', 2150, 2.3, 0.6),
    ('Uruguay', 1980, 1.6, 0.8),
    ('Colombia', 1920, 1.4, 0.9),
    ('Ecuador', 1860, 1.2, 1.0),
    ('Chile', 1830, 1.2, 1.2),
    ('Peru', 1810, 1.0, 1.1),
    ('Paraguay', 1740, 0.9, 1.1),
    ('Venezuela', 1680, 1.0, 1.5),
    ('Bolivia', 1620, 1.1, 1.8),

    -- === UEFA (Europe) - Tier 2/3 ===
    ('Switzerland', 1910, 1.4, 0.9),
    ('Serbia', 1850, 1.7, 1.4),     -- Strong attack
    ('Poland', 1810, 1.5, 1.3),
    ('Ukraine', 1790, 1.3, 1.2),
    ('Wales', 1780, 1.1, 1.2),
    ('Sweden', 1770, 1.2, 1.1),
    ('Turkey', 1750, 1.3, 1.4),
    ('Austria', 1740, 1.2, 1.3),
    ('Scotland', 1730, 1.1, 1.3),
    ('Czech Republic', 1720, 1.2, 1.3),
    ('Norway', 1710, 1.6, 1.4),     -- Haaland effect (High Attack)
    ('Hungary', 1700, 1.1, 1.2),
    ('Greece', 1680, 0.9, 1.1),

    -- === CONCACAF (North & Central America) ===
    ('United States', 1850, 1.3, 1.1), -- Often listed as 'USA' in some datasets
    ('Mexico', 1840, 1.2, 1.2),
    ('Canada', 1760, 1.4, 1.5),
    ('Costa Rica', 1720, 0.9, 1.3),
    ('Panama', 1680, 1.0, 1.4),
    ('Jamaica', 1620, 1.1, 1.5),
    ('Honduras', 1580, 0.8, 1.6),

    -- === CAF (Africa) ===
    ('Morocco', 1880, 1.2, 0.8),    -- Excellent defense
    ('Senegal', 1830, 1.4, 1.1),
    ('Tunisia', 1740, 0.9, 1.2),
    ('Nigeria', 1720, 1.3, 1.4),
    ('Algeria', 1710, 1.3, 1.3),
    ('Egypt', 1700, 1.2, 1.2),
    ('Cameroon', 1660, 1.2, 1.5),
    ('Ghana', 1640, 1.1, 1.6),
    ('Ivory Coast', 1630, 1.3, 1.5),
    ('Mali', 1620, 1.0, 1.3),

    -- === AFC (Asia & Australia) ===
    ('Japan', 1820, 1.4, 1.1),
    ('Iran', 1800, 1.1, 1.1),
    ('South Korea', 1790, 1.3, 1.2), -- Sometimes 'Korea Republic'
    ('Australia', 1730, 1.1, 1.3),
    ('Saudi Arabia', 1650, 1.0, 1.7),
    ('Qatar', 1600, 0.9, 1.8),
    ('Iraq', 1580, 0.8, 1.5),
    ('United Arab Emirates', 1550, 0.9, 1.6),
    ('Uzbekistan', 1540, 1.0, 1.5),
    ('China', 1500, 0.8, 1.7),

    -- === OTHERS / LOWER TIER (For testing variety) ===
    ('New Zealand', 1600, 1.0, 1.4),
    ('Iceland', 1580, 0.9, 1.4),
    ('North Macedonia', 1550, 1.0, 1.6),
    ('Albania', 1540, 0.8, 1.3),
    ('Slovenia', 1530, 1.0, 1.3),
    ('Ireland', 1520, 0.9, 1.4),
    ('Finland', 1510, 0.9, 1.4),
    ('South Africa', 1500, 0.9, 1.5),
    ('El Salvador', 1450, 0.7, 1.8),
    ('Guatemala', 1420, 0.7, 1.8)

ON CONFLICT (team_name) DO UPDATE 
SET 
    elo_rating = EXCLUDED.elo_rating,
    attack_strength = EXCLUDED.attack_strength,
    defense_strength = EXCLUDED.defense_strength;

CREATE INDEX IF NOT EXISTS idx_match_date ON historical_matches(match_date);

CREATE INDEX IF NOT EXISTS idx_teams ON historical_matches(home_team, away_team);
