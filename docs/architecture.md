# Architettura

Documento tecnico della versione `0.2.1` (Minecraft 1.21.1, Fabric, mapping Mojang ufficiali).

## Stack

| Componente | Versione |
|---|---|
| Minecraft | 1.21.1 |
| Fabric Loader | 0.20.1 (richiesto >= 0.16.9) |
| Fabric API | 0.116.17+1.21.1 |
| Loom | 1.7.4 |
| Gradle | 8.10.2 |
| Java | 21 |

## Mappa dei pacchetti

```
com.cybercraft
├── CyberCraft.java              entrypoint comune: registri, eventi server, tick
├── client/
│   ├── CyberCraftClient.java    keybind, receiver S2C, tick client, HUD
│   ├── ClientCyberwareState.java cache dello stato cyberware
│   ├── CyberwareScreen.java     schermata Ripperdoc con REMOVE
│   ├── EdgerunnersBookScreen.java manuale in-game
│   ├── ScannerHud.java          overlay scanner e pannelli
│   ├── ScannerState.java        bersaglio, upload, tempi di compilazione
│   └── SandevistanFx.java       scie particellari e overlay Sandevistan
├── cyberware/
│   ├── Cyberware.java           catalogo dei 42 impianti
│   ├── CyberwareSlot.java       enum dei 10 slot
│   ├── CyberwareData.java       record persistente + Codec
│   ├── CyberwareManager.java    tolleranza, RAM, capacità, installazione, rimozione
│   ├── ImplantEffects.java      modificatori, effetti passivi, hook di combattimento
│   ├── SandevistanManager.java  attivazione, durata, cooldown, rallentamento mob
│   └── DoubleJumpHandler.java   doppio salto server-authoritative
├── menu/
│   └── ImplantForgeMenu.java    CraftingMenu con stillValid sul Banco Impianti
├── network/                     payload C2S/S2C e registrazione canali
├── netrunning/                  Cyberdeck, Quickhack, NetrunnerManager
├── item/ block/ registry/ util/ contenuti e registri
```

## Persistenza

Lo stato del giocatore è un **Fabric Data Attachment** (`ModAttachments.CYBERWARE`) di tipo `CyberwareData`:

- `initializer`: `CyberwareData.DEFAULT` (nessun impianto, tolleranza 100, RAM 100).
- `persistent(Codec)`: salvataggio nel file del mondo.
- `copyOnDeath()`: gli impianti restano dopo la morte.

Campi del record:

| Campo | Tipo | Significato |
|---|---|---|
| `implants` | `List<String>` | ID degli impianti installati |
| `tolerance` | `float` | Tolleranza corrente (0–100) |
| `ram` | `float` | RAM corrente (0–100) |
| `overloaded` | `boolean` | Stato di sovraccarico |
| `selectedHack` | `int` | Indice del quickhack selezionato |
| `sandevistanEnd` | `long` | Fine del cooldown Sandevistan/Berserk |
| `sandevistanActiveUntil` | `long` | Fine dell'effetto attivo |

## Autorità del server

Tutte le azioni sono validate lato server; i payload C2S sono intenzioni.

- `CyberwareManager.tick` rigenera tolleranza (3/s, dimezzata in sovraccarico) e RAM (0,34/s), gestisce entrata/uscita dal sovraccarico, riapplica gli effetti e invia lo stato al client.
- `tryInstall` verifica slot libero, duplicati, sovraccarico e capacità disponibile prima di modificare il Data Attachment.
- `tryRemove` verifica lo slot, restituisce l'item (o lo fa cadere), chiama `ImplantEffects.removeModifiers`, azzera i timer Sandevistan/Berserk per gli impianti corrispondenti e sincronizza il client.
- La RAM non può superare 100; `withRam` applica il clamp.
- La capacità massima è `min(60, 20 + 2 × livello XP)` più 20 con il Compressore Cromato.

## Effetti

`ImplantEffects` centralizza i modificatori di attributo e gli hook:

- I modificatori sono **transient** e vengono riapplicati ogni tick: sopravvivono a morte, cambio dimensione e ricreazione dell'entità.
- In sovraccarico i modificatori vengono rimossi.
- Effetti passivi con durata breve (salto, visione notturna, respirazione, resistenza al fuoco, rigenerazione, invisibilità) vengono rinnovati periodicamente.
- Gli hook di combattimento gestiscono Lame da Mantide, Braccia di Gorilla, lanciatore, microgeneratore, guarigione su uccisione, Biomonitor, adrenalina, Shock 'n Awe e Secondo Cuore.
- Le mappe di cooldown sono in memoria e vengono svuotate alla chiusura del server.

`SandevistanManager` applica Speed III e Haste II, rallenta i mob entro 15 blocchi con un `AttributeModifier` `ADD_MULTIPLIED_TOTAL` −0.85 e tiene traccia dei mob rallentati per rimuovere il modificatore alla scadenza.

## Rete

| Payload | Direzione | Scopo |
|---|---|---|
| `DoubleJumpPayload` | C2S | Richiesta doppio salto |
| `SandevistanPayload` | C2S | Attivazione Sandevistan/Berserk |
| `CastHackPayload` | C2S | Lancio quickhack |
| `SelectHackPayload` | C2S | Cambio quickhack (+1/−1) |
| `RemoveImplantPayload` | C2S | Rimozione impianto per ordinale di slot |
| `CyberwareSyncPayload` | S2C | Stato completo per HUD e schermata |
| `OpenCyberwareScreenPayload` | S2C | Apertura schermata Cyberware |
| `OpenBookPayload` | S2C | Apertura Edgerunner's Book |
| `HackResultPayload` | S2C | Esito del quickhack e feedback upload |

I receiver C2S girano su `context.server().execute(...)`, quindi la logica di gioco resta sul thread del server. Al join viene inviato subito uno stato sincronizzato.

## Client

- Le keybind sono registrate in `CyberCraftClient` e rimappabili nella categoria CyberCraft.
- `ClientCyberwareState` mantiene l'ultimo `CyberwareData` ricevuto.
- `ScannerHud` disegna scanner, reticolo, RAM, quickhack e pannello bersaglio tramite `HudRenderCallback`.
- `SandevistanFx` emette particelle nel mondo e disegna l'overlay; è registrato prima dello scanner per restare sotto i pannelli.
- `CyberwareScreen` è una `Screen` con layout responsivo e modalità compatta; invia `RemoveImplantPayload` al click su REMOVE.
- Le schermate e l'HUD richiedono la mod anche lato client.

## Banco Impianti

`ImplantForgeMenu` estende `CraftingMenu` e sovrascrive `stillValid(Player)` per controllare `ModBlocks.IMPLANT_FORGE`. Il tipo di menu resta `MenuType.CRAFTING`, quindi il client usa la schermata di crafting vanilla senza mixin. La correzione evita che il server consideri il menu non valido e lo chiuda al primo tick.

## Asset e datapack

- Asset: `assets/cybercraft/` con modelli, blockstate, texture e lang (`en_us.json`, `it_it.json`).
- Dati: `data/cybercraft/recipe/`, `loot_table/`, `tags/` e `worldgen/` (configured/placed feature).
- Il worldgen viene aggiunto ai biomi tramite `BiomeModifications` e `ResourceKey<PlacedFeature>`.
- Le ricette degli impianti sono pattern 3×3 senza legno; i materiali dipendono dal tier.
