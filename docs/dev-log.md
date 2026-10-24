# Dev log

Registro cronologico degli interventi. Ogni voce indica data, obiettivo, modifiche e verifiche. Va aggiornato a ogni lavoro sul progetto.

## 2026-10-24 — Quickhack targeting 0.2.2

**Obiettivo**
- Rimuovere l'intercettazione della rotella nello scanner.
- Agganciare qualsiasi entità vivente in linea di vista entro la render distance (max 64).
- Evidenziare in rosso i bersagli visibili e mostrare una scheda statistiche sotto il mirino con dissolvenza.

**Modifiche**
- `CyberCraftClient`: rimossi hook GLFW della rotella e relativi campi; il cast invia `CastHackPayload(hack, entityId)`; reset outline e scanner alla disconnessione.
- `ScannerHud`: rimosso `crosshairPickEntity`, aggiunto raycast `ProjectileUtil` con portata dinamica; scheda statistiche ridisegnata (tipo, fazione, HP, armatura/robustezza, effetti, vulnerabilità); fade con alpha; hint X/Z.
- `ScannerState`: bersaglio con timestamp, dissolvenza 120 ms e cache dell'ultimo target.
- `EntityOutlineManager` (nuovo): team client rosso, glow sulle entità visibili, filtro distanza/FOV/LOS, max 48, aggiornamento ogni 5 tick, ripristino alla chiusura.
- `Targeting` (nuovo): portata e validità condivise.
- `CastHackPayload` e `NetrunnerManager`: id bersaglio e validazione server esplicita (esistenza, tipo, distanza, LOS, raycast).
- Lang EN/IT: rimosso hint rotella, aggiunte chiavi fazione/tipo/stati.
- Documentazione: `docs/quickhack-targeting.md`, README, gameplay, architecture, changelog, roadmap.

**Verifiche**
- `gradlew build --offline` → `BUILD SUCCESSFUL`; jar `cybercraft-0.2.2.jar` 238.223 byte.
- Server dedicato: `cybercraft 0.2.2`, `Done (0.590s)`, nessun errore.
- Test grafico lato client non eseguito in questa sessione: hotbar, colore outline su entità non giocatore, fade e cast a distanza restano da verificare in un mondo reale.

**Stato**
- Versione `0.2.2` su branch `indev`; in attesa di test gameplay prima dell'integrazione in `main`.

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
