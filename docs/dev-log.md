# Dev log

Registro cronologico degli interventi. Ogni voce indica data, obiettivo, modifiche e verifiche. Va aggiornato a ogni lavoro sul progetto.

## 2026-10-24 — Creazione repository e documentazione pubblica

**Obiettivo**
- Creare la repository GitHub pubblica `CyberCraft`.
- Pubblicare il codice della mod v0.2.1 con branch `main` e `indev`.
- Aggiungere README completo e cartella `docs/`.
- Invitare `Xmilon` come collaboratore.

**Modifiche**
- Aggiunto `.gitignore` per Gradle, build, run, IDE, log e registro locale.
- Aggiunto `README.md` con descrizione, caratteristiche, requisiti, installazione, comandi, build, struttura, documentazione, branch e licenza.
- Aggiunta la cartella `docs/` con indice, gameplay, architettura, sviluppo, changelog, roadmap e questo dev log.
- Inizializzato il repository Git con branch `main` e `indev`.

**Verifiche**
- `gradlew build --offline` già eseguito con successo per la v0.2.1: jar 229.824 byte, 359 entry.
- Server di test: `cybercraft 0.2.1`, `Done`, nessun errore.
- Controllo dei file staged: nessun jar di build, nessun log, nessun segreto; unico jar presente è il wrapper Gradle.

**Stato**
- Repository pubblica attiva su https://github.com/cookiefrr/CyberCraft.
- Commit iniziale `3d78e71` — *Add CyberCraft 0.2.1 source, README and docs* — pubblicato su `main` e `indev`.
- Branch di lavoro corrente: `indev`; `main` resta la versione stabile.
- Collaboratore `Xmilon` (xmilonn@gmail.com) invitato con permesso **write**; invito id `339470281`, creato il 2026-10-24 alle 11:17 UTC, in attesa di accettazione.

<!-- Aggiornare questo file a ogni intervento: data, obiettivo, modifiche, verifiche, stato. -->
