

# 🏆 World Cup Oracle - Backend Engine

***This is a Project was designed and built as hobby, is not intented to replace any predictor, is just to share how to create Microservices and use IA algorithms in Java. It was created using promtps from Google Gemini 3 pro, but lot of bugs and new features were made by me.***

A high-performance football tournament prediction and simulation engine, built on a **Reactive** and Non-Blocking architecture. It leverages advanced mathematical models (Modified Elo Rating and Monte Carlo Simulations) to predict outcomes, adjust rankings in real-time, and manage tournament logistics.


## 🚀 Key Features

### 🧠 Oracle Engine (The Brain)

* **Dynamic Elo Algorithm:** Custom ranking system that adjusts scores based on opponent strength, goal margin, and tournament importance (Adaptive K-Factor).
* **Monte Carlo Simulation:** Stochastic engine that simulates thousands of match scenarios utilizing a 10x10 goal probability matrix.
* **Continuous Learning:** Capability to retrain the model by processing historical data (Kaggle datasets) to recalibrate attack and defense strengths for every nation.
* **Branchless Logic:** Mathematical algorithms optimized to avoid branching (`if/else`) in CPU-intensive calculations, enhancing performance.

### 🏟️ Tournament Core (The Management)

* **Schedule Management:** Reactive endpoints for massive match creation (`Batch Scheduling`).
* **Real-Time Standings:** Automatic calculation of group tables, goal differences, and points following official FIFA tie-breaking rules.
* **Non-Blocking Persistence:** Utilizes R2DBC to handle high-concurrency transactions with PostgreSQL.

## 🛠️ Tech Stack

* **Language:** Java 21+ (Extensive use of `Records` and `Lambdas`).
* **Framework:** Spring Boot 3 (WebFlux).
* **Database:** PostgreSQL.
* **Data Access:** Spring Data R2DBC (Reactive Relational Database Connectivity).
* **Tools:** Lombok, Docker, Maven/Gradle.

## 📐 Mathematical Model Architecture

### 1. Probability Calculation (Elo)
The system calculates the expected win probability ($W_e$) using the standard logistic formula:

<div align="center">
  <img src="https://latex.codecogs.com/svg.image?\bg_white&space;\color{black}E_A=\frac{1}{1+10^{(R_B-R_A)/400}}" title="Elo Formula" />
</div>

Where:
* $E_A$: Expected score for Team A (Probability of winning).
* $R_A$: Current Elo rating of Team A.
* $R_B$: Current Elo rating of Team B.


### 2. Monte Carlo Matrix (10x10)

To predict exact scores, we avoid simple averages. Instead, we generate a probability matrix calculating the likelihood of Team A scoring i goals and Team B scoring j goals.

```java
// Example of optimized (Branchless) logic used in the engine
// 0=HomeWin, 1=Draw, 2=AwayWin
int index = Integer.compare(j, i) + 1; 
outcomes[index] += prob;

```

### 📊 Historical Data Pipeline & Training

To solve the "Cold Start" problem, the Oracle Engine creates a baseline for every national team by ingesting and replaying over 40,000 historical matches. We utilize the International football results dataset from Kaggle.

### 1. Data Ingestion Source
The system expects a CSV file following the standard Kaggle structure (Mart Jürisoo's dataset):

Source: 
[International football results from 1872 to 2024](https://www.kaggle.com/martj42/international-football-results-from-1872-to-2017)

***Format:***
date, home_team, away_team, home_score, away_score, tournament, city, country, neutral

```text
date,home_team,away_team,home_score,away_score,tournament,city,country,neutral
1998-07-12,France,Brazil,3,0,FIFA World Cup,Saint-Denis,France,FALSE
2010-07-11,Netherlands,Spain,0,1,FIFA World Cup,Johannesburg,South Africa,TRUE
2022-12-18,Argentina,France,3,3,FIFA World Cup,Lusail,Qatar,TRUE
```

### 2. The Training Process (ETL)
When the `POST /admin/import-history` endpoint is triggered, the system executes a Reactive ETL Process:

* **Stream Parsing:** The CSV is read line-by-line using non-blocking I/O to minimize memory footprint.

* **Normalization:** Team names are mapped to our internal ISO dictionary (e.g., "Korea Republic" → "South Korea").

* **Chronological Replay:** The engine simulates the matches in strict chronological order.

* **Elo Evolution:** For every match, the EloCalculator updates the ratings of both teams. This allows the system to naturally arrive at the current "Attack Strength" and "Defense Strength" based on recent form rather than static values.

### 3. Triggering the Import

To reset the database and train the model with the latest dataset:

Bash
```text
curl -X POST http://localhost:8080/admin/import-history \
  -F "file=@/path/to/results.csv" \
  -H "Content-Type: multipart/form-data"
```
**Note:** This process might take a few seconds as it recalculates thousands of Elo transactions to ensure mathematical accuracy for the current tournament predictions.

## 📂 Project Structure

```text
src/main/java/com/score_predictor
├── oracle_engine           # AI & Prediction Module
│   ├── business            # Math Logic (Elo, MonteCarlo)
│   ├── service             # Simulation Orchestration
│   └── repository          # Historical Persistence
├── tournament_core         # Tournament Management Module
│   ├── model               # Entities (Match, Team, Group)
│   ├── controller          # Reactive API Endpoints
│   └── service             # Business Logic (Standings, Schedule)
└── shared                  # Common DTOs and Utils

```

## 🔌 API Reference (Examples)

### Predict Next Round

Generates predictions based on the current state of the teams.
`GET /predict`

### Finalize Match

Registers the actual score, updates the standings, and recalibrates the Elo for both teams in a single reactive transaction.
`POST /matches/{id}/finish`

```json
{
  "homeScore": 2,
  "awayScore": 1
}

```

### Batch Schedule Loading

`POST /matches/schedule/batch`

## ⚙️ Installation & Setup

### Prerequisites

* Java 21 JDK
* Docker (for the database)

### 1. Start Database

```bash
docker run --name world-cup-db -e POSTGRES_PASSWORD=password -e POSTGRES_DB=worldcup -p 5432:5432 -d postgres

```

### 2. Configure Environment Variables

In `application.yml` or system variables:

```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/worldcup
    username: postgres
    password: password

```

### 3. Run Application

```bash
./mvnw spring-boot:run

```

### 4. Data Seeding

The system includes SQL scripts to initialize ratings for 80+ national teams with real statistical data (Elo, Attack Strength, Defense Strength).

### 5. Previous matches

A CSV file can be uploaded to help Predictor The system includes SQL scripts to initialize ratings for 80+ national teams with real statistical data (Elo, Attack Strength, Defense Strength).

## 🤝 Contribution

This project uses **Project Reactor**. Please ensure you are familiar with `Mono` and `Flux` concepts before contributing to the service layer.

---

**Built with ❤️ and Java.**