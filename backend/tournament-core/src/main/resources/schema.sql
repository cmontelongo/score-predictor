CREATE TABLE IF NOT EXISTS teams (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    iso_code VARCHAR(3) NOT NULL,
    group_name VARCHAR(1) NOT NULL
);

CREATE TABLE IF NOT EXISTS matches (
    id SERIAL PRIMARY KEY,
    home_team VARCHAR(100) NOT NULL,
    away_team VARCHAR(100) NOT NULL,
    home_score INTEGER,          -- Puede ser NULL si no se ha jugado
    away_score INTEGER,          -- Puede ser NULL si no se ha jugado
    match_date TIMESTAMP NOT NULL,
    stadium VARCHAR(150),        -- Nuevo: Dónde se juega
    stage VARCHAR(50),           -- Nuevo: GROUP_A, ROUND_16, FINAL
    status VARCHAR(20) DEFAULT 'SCHEDULED' -- Nuevo: SCHEDULED, LIVE, FINISHED
);