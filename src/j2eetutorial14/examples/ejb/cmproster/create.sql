// Create tables for CMP roster application

CREATE TABLE LEAGUE (
	id VARCHAR(8) CONSTRAINT pk_league PRIMARY KEY,
	name VARCHAR(24),
	sport VARCHAR(24));

CREATE TABLE PLAYER (
	id VARCHAR(8) CONSTRAINT pk_player PRIMARY KEY,
	name VARCHAR(24),
	position VARCHAR(24),
	salary VARCHAR(24));

CREATE TABLE TEAM (
	id VARCHAR(8) CONSTRAINT pk_team PRIMARY KEY,
	name VARCHAR(24),
	city VARCHAR(24),
	league_id VARCHAR(8),
	CONSTRAINT fk_league_id FOREIGN KEY (league_id) REFERENCES league(id));
	
CREATE TABLE TEAM_PLAYER (
	player_id VARCHAR(8),
	team_id VARCHAR(8),
	CONSTRAINT fk_player_id FOREIGN KEY (player_id) REFERENCES player(id),
	CONSTRAINT fk_team_id FOREIGN KEY (team_id) REFERENCES team(id));
