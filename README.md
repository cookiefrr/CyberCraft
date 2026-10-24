# CyberCraft

Mod per **Minecraft 1.21.1** su **Fabric** ispirata a Cyberpunk 2077. Aggiunge cyberware installabile, netrunning, Sandevistan, un HUD scanner, minerali, banchi da lavoro e un manuale consultabile in gioco.

**Versione:** `0.2.2` · **Branch stabile:** `main` · **Sviluppo:** `indev`

**Repository:** https://github.com/cookiefrr/CyberCraft

```bash
git clone https://github.com/cookiefrr/CyberCraft.git
```

---

## Caratteristiche

### Cyberware
- **42 impianti** distribuiti nei **10 slot** corporei della tassonomia Cyberpunk 2077: sistema operativo, corteccia frontale, oculare, mani, braccia, sistema nervoso, circolatorio, integumentario, scheletro, gambe.
- Un impianto per slot, con **capacità** calcolata dal livello XP: `min(60, 20 + 2 × livello)` più eventuali bonus.
- **Tolleranza** massima 100: si consuma attivando gli impianti, rigenera nel tempo e a zero provoca **sovraccarico** (impianti spenti, nausea e danno periodico).
- Installazione con il **Collegamento Neurale**, il **Tavolo di Installazione** o la schermata Cyberware.
- **Rimozione** dalla schermata Cyberware: l'impianto torna in inventario o cade a terra se l'inventario è pieno.

### Netrunning
- **Cyberdeck** con tre quickhack: Cortocircuito, Riavvio Ottico e Contagio.
- **RAM massima 100**, rigenerazione passiva; l'Ex-Disk aumenta solo la velocità di rigenerazione.
- **HUD scanner** con reticolo dinamico, pannello quickhack, pannello bersaglio, barra di upload e glitch da danno.
- Quickhack lanciabili mirando un qualsiasi `LivingEntity` nel campo visivo, fino a `min(render distance × 16, 64)` blocchi.
- Outline rosso sui bersagli validi in vista e pannello statistiche quando il mirino li inquadra.

### Sandevistan e Berserk
- **Sandevistan**: 10 secondi di Velocità III e Rapidità II; i mob entro 15 blocchi vengono rallentati dell'85%. Ricarica 30 secondi.
- **Berserk**: Forza II, Resistenza II e Velocità I per 10 secondi.
- Effetto visivo lato client con scie particellari, tinta teal-verde, vignettatura, speed line e scanline.
- Attivazione con keybind o con il Collegamento Neurale; validazione interamente lato server.

### Mondi, materiali e crafting
- **3 minerali** con worldgen: titanio, neodimio e litio; lingotti, blocchi di stoccaggio, fusione e blasting.
- **4 componenti**: scheda circuito, servomotore, gel biologico, lente ottica.
- **70 ricette** senza legno negli impianti: ferro/rame per il tier 1, titanio/oro per il tier 2, neodimio/diamante per il tier 3.
- **Banco Impianti** con griglia 3×3 e **Tavolo di Installazione**.
- **Blocco al Neon** e **Metallo Scuro**, oltre ai blocchi dei minerali.
- **Edgerunner's Book**: manuale in-game con descrizione, icona e ricetta di ogni contenuto.

### Interfaccia
- Schermata Cyberware ridisegnata in stile Ripperdoc: giocatore al centro, 5 slot per lato, barra capacità verticale e pannello dettaglio con REMOVE.
- HUD scanner con palette ciano/ambra/rosso/lime, compilazione dati e feedback di caricamento.
- Lingue incluse: **italiano** e **inglese**.

---

## Requisiti

| Componente | Versione |
|---|---|
| Minecraft | 1.21.1 |
| Fabric Loader | >= 0.16.9 (testato con 0.20.1) |
| Fabric API | 0.116.17+1.21.1 |
| Java | 21 |

La mod va installata sia sul client sia sul server: HUD, schermate e keybind richiedono il codice client.

## Installazione

1. Installa Fabric Loader per Minecraft 1.21.1.
2. Metti **Fabric API** nella cartella `mods`.
3. Copia `cybercraft-0.2.2.jar` nella cartella `mods`.
4. Avvia il gioco con il profilo Fabric 1.21.1.

Non lasciare due versioni di CyberCraft nella stessa cartella `mods`.

## Comandi

| Azione | Tasto predefinito | Rimappabile |
|---|---|---|
| Attiva Sandevistan / Berserk | `V` | Sì |
| Apri schermata Cyberware | `C` | Sì |
| Attiva scanner | `R` | Sì |
| Esegui quickhack | `G` | Sì |
| Quickhack precedente | `X` | Sì |
| Quickhack successivo | `Z` | Sì |
| Installa impianto / apri Cyberware | Tasto destro con Neural Link | — |
| Cambia quickhack | Maiusc + tasto destro con Cyberdeck | — |

Tutte le keybind sono in **Opzioni → Comandi → CyberCraft**.

## Compilazione

Serve un **JDK 21**. Il wrapper Gradle è incluso.

```bash
./gradlew build
```

In ambienti senza rete si può usare la cache locale:

```bash
./gradlew build --offline
```

Il jar viene creato in `build/libs/cybercraft-<versione>.jar`. Il progetto usa Gradle 8.10.2, Loom 1.7.4 e mapping Mojang ufficiali.

Avvio di un server di prova:

```bash
./gradlew runServer
```

## Struttura del progetto

```
src/main/java/com/cybercraft/
├── CyberCraft.java              entrypoint comune ed eventi server
├── client/                      keybind, HUD, schermate, effetti client
├── cyberware/                   catalogo, dati, manager, effetti, Sandevistan
├── menu/                        menu del Banco Impianti
├── network/                     payload C2S/S2C e registrazione canali
├── netrunning/                  Cyberdeck e quickhack
├── item/ block/ registry/ util/ item, blocchi, registri e utility
src/main/resources/
├── assets/cybercraft/           modelli, texture, lingue
└── data/cybercraft/             ricette, loot table, worldgen
```

## Documentazione

- [docs/README.md](docs/README.md) — indice della documentazione
- [docs/gameplay.md](docs/gameplay.md) — impianti, quickhack, materiali, worldgen e ricette
- [docs/architecture.md](docs/architecture.md) — architettura tecnica e flussi di rete
- [docs/development.md](docs/development.md) — ambiente, build, test e flusso di lavoro
- [docs/changelog.md](docs/changelog.md) — cronologia delle versioni
- [docs/roadmap.md](docs/roadmap.md) — prossimi passi
- [docs/dev-log.md](docs/dev-log.md) — registro degli interventi
- [docs/quickhack-targeting.md](docs/quickhack-targeting.md) — targeting dal mirino, outline rosso e scheda statistiche

## Branch

- `main`: versioni stabili e rilasciate.
- `indev`: sviluppo attivo; le modifiche vengono integrate in `main` solo dopo build e test server puliti.

## Stato e limiti noti

- La build `0.2.2` è verificata con `gradlew build --offline` e con un server dedicato; il test gameplay lato client in un mondo reale è ancora da completare.
- Gli effetti dello scanner e del Sandevistan sono overlay 2D e particelle, non shader o post-processing.
- Le texture sono pixel art segnaposto, non definitive.
- Il Banco Impianti usa la schermata di crafting vanilla con una validazione dedicata del blocco.
- Con RAM massima 100 i costi dei quickhack sono da ricalibrare.

## Autore e licenza

Autore: **Ian**.

Licenza: **All Rights Reserved** (come dichiarato in `fabric.mod.json`). Nessun permesso di redistribuzione o riuso è concesso senza autorizzazione.
