# Documentazione CyberCraft

Indice della documentazione pubblica del progetto. Questa cartella va aggiornata a ogni modifica rilevante: il registro cronologico è in [dev-log.md](dev-log.md).

| Documento | Contenuto |
|---|---|
| [gameplay.md](gameplay.md) | Impianti cyberware, quickhack, materiali, worldgen, ricette e comandi |
| [architecture.md](architecture.md) | Struttura del codice, persistenza, rete client/server, rendering |
| [development.md](development.md) | Ambiente, build, test, branching e versione |
| [changelog.md](changelog.md) | Cronologia delle versioni |
| [roadmap.md](roadmap.md) | Prossimi passi e idee |
| [dev-log.md](dev-log.md) | Registro datato degli interventi |

## Regole di aggiornamento

- Ogni intervento su codice, asset o build aggiunge una voce in `dev-log.md` con data, obiettivo, file toccati e verifiche.
- Le modifiche ai contenuti di gioco aggiornano `gameplay.md`.
- Le modifiche a pacchetti, flussi di rete o persistenza aggiornano `architecture.md`.
- Cambi di versione, branch o procedura di build aggiornano `development.md`, `changelog.md` e `README.md`.
- I prossimi passi completati si spostano da `roadmap.md` al changelog.

## Branch

- `main`: versioni stabili e rilasciate.
- `indev`: sviluppo attivo. Le modifiche vengono portate in `main` dopo build e test server puliti.
