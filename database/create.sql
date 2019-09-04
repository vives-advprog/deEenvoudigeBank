CREATE TABLE IF NOT EXISTS KLANT (
  id       int auto_increment primary key,
  naam     VARCHAR(50)  NOT NULL,
  voornaam VARCHAR(50)  NOT NULL,
  adres    VARCHAR(100) NOT NULL,
  postcode VARCHAR(10)  NOT NULL,
  gemeente VARCHAR(50)  NOT NULL,
  status   VARCHAR(15)  NOT NULL
);

CREATE TABLE IF NOT EXISTS REKENING (
  rekeningnummer VARCHAR(20) primary key,
  eigenaar       int            not null,
  status         VARCHAR(15)    NOT NULL,
  saldo          decimal(10, 2) NOT NULL
);

ALTER TABLE REKENING
  ADD CONSTRAINT IF NOT EXISTS KLANT_EIGENAAR_ID FOREIGN KEY (eigenaar) REFERENCES KLANT(id);
