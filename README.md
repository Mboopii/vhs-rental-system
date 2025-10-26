# VHS Rental System (Spring Boot + React)

## Pokretanje aplikacije (preferirani način - Docker Compose)

Ove upute koriste `docker-compose.yml` za izgradnju i pokretanje oba dijela aplikacije (backend i frontend) unutar Docker kontejnera jednom naredbom.

- Provjerite da su Docker i Docker Compose instalirani i pokrenuti.
- Poželjno imati Docker Desktop.
- Pozicionirajte se u korijenski direktorij cijelog projekta (folder koji sadrži `docker-compose.yml`, `backend` i `frontend`).
- Izgradite image i pokrenite kontejnere pomoću naredbe:
  ```bash
  docker-compose up --build -d
  ```
- Pristup aplikaciji:
  - Backend servis će biti dostupan na http://localhost:8080.
  - Frontend aplikacija će biti dostupna na http://localhost:5173.
- Za zaustavljanje i uklanjanje kontejnera pokrenutih s Docker Compose, iz istog direktorija izvršite:
  ```bash
  docker-compose down
  ```

## Alternativno pokretanje projekta (backend i baza podataka)

Upute za pokretanje backend servisa i H2 in-memory baze podataka.

1.  Klonirajte repozitorij projekta.
2.  Koristio sam:
    - Java Development Kit (JDK) verzije 21.
    - Maven verzije 3.9.11
3.  Pozicionirajte se u korijenski direktorij projekta (gdje se nalazi `pom.xml`) i pokrenite aplikaciju koristeći Maven. Ova naredba će kompajlirati projekt, pokrenuti Spring Boot aplikaciju i inicijalizirati H2 in-memory bazu podataka:
    ```bash
    mvnw spring-boot:run
    ```
4.  Backend aplikacije će biti dostupan na adresi http://localhost:8080.
5.  Prilikom prvog pokretanja, Hibernate će automatski kreirati shemu baze podataka. Nakon toga, `data.sql` skripta će se izvršiti i popuniti bazu s testnim podacima.

## Alternativno pokretanje projekta (frontend)

Za pokretanje frontenda potrebno je koristiti Vite razvojni poslužitelj.

1.  Pozicionirajte se u direktorij `frontend`-a.
2.  Instalirajte ovisnost pomoću naredbe:
    ```bash
    npm install
    ```
3.  Pokrenite frontend pomoću naredbe:
    ```bash
    npm run dev
    ```
4.  Korisničko sučelje će biti dostupno putem web preglednika na adresi http://localhost:5173.

Konfiguracija CORS-a je postavljena u `WebConfig.java` kako bi omogućila komunikaciju između frontenda (localhost:5173) i backenda (localhost:8080).

## Testiranje aplikacije

Za testiranje API endpointa koristio sam Postman alat.

### A. Postman kolekcija

Uvezite priloženu datoteku `vhs-collection.json` u Postman. Ova kolekcija sadrži organizirane zahtjeve za sve implementirane API operacije. Omogućuje testiranje:

- Uspješnih scenarija (dohvaćanje, kreiranje, ažuriranje, brisanje).
- Scenarija s validacijskim greškama (npr., slanje nepotpunih podataka).
- Scenarija koji testiraju poslovnu logiku (npr., pokušaj brisanja korisnika s aktivnim posudbama, pokušaj iznajmljivanja već iznajmljene kazete).

**Postavite Postman varijablu `baseURL` na http://localhost:8080.**

### B. Pregled ključnih API endpointova

- `GET /api/vhs`: Dohvaća listu svih VHS kazeta. Podržava opcionalne query parametre `genre` i `year` za filtriranje.
- `GET /api/vhs/new-releases`: Vraća listu 10 najnovijih VHS naslova.
- `POST /api/vhs`: Kreira novi zapis o VHS kazeti.
- `DELETE /api/vhs/{id}`: Briše specificiranu VHS kazetu, uz prethodnu provjeru da kazeta nije trenutno iznajmljena.
- `GET /api/users/{id}/rentals`: Dohvaća kompletnu povijest posudbi za korisnika s danim ID.
- `PUT /api/users/{id}`: Ažurira podatke postojećeg korisnika. Uključuje provjeru jedinstvenosti email adrese.
- `POST /api/rentals`: Bilježi novu posudbu VHS kazete od strane korisnika.
- `POST /api/rentals/return/{id}`: Bilježi vraćanje prethodno posuđene kazete i automatski izračunava eventualnu zakasninu.

### C. Pokretanje unit i integration testova (Maven)

Projekt uključuje set unit testova (za servisni sloj koristeći Mockito) i integration testova (koristeći Spring Boot testni kontekst). Za pokretanje svih testova:

1.  Pozicionirajte se u korijenski direktorij `backend` dijela projekta.
2.  Izvršite sljedeću Maven naredbu:
    ```bash
    mvnw test
    ```
3.  Maven će kompajlirati kod, izvršiti sve testove pronađene u `src/test/java` direktoriju i prikazati izvještaj o uspješnosti testova.

## Napomene o frontendu

**Simulacija prijave korisnika:** Nisam dodavao pravu registraciju i login ali aplikacija koristi padajući izbornik ("Select user") za simulaciju prijave. Odabir korisnika je preduvjet za omogućavanje funkcionalnosti iznajmljivanja i vraćanja kazeta.

**Dinamički prikaz posudbi:** Komponenta za prikaz posudbi prikazuje ili sve zabilježene posudbe (ako nijedan korisnik nije odabran) ili samo povijest posudbi trenutno odabranog korisnika (koristeći `/api/users/{id}/rentals` endpoint).

**Upravljanje podacima:** Korisničko sučelje uključuje gumbe i forme za uređivanje (PUT) i brisanje (DELETE) korisničkih zapisa.
