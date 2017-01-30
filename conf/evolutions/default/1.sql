# Notification

# --- !Ups

CREATE TABLE history (
  dt DATETIME NOT NULL,
  url varchar(255) NOT NULL,
  title varchar(255)
);

# --- !Downs

DROP TABLE history;