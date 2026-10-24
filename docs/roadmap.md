# Roadmap

## Priorità alta

- [ ] Test gameplay completo lato client in un mondo: apertura Banco Impianti, REMOVE, schermata Cyberware, scanner, rotella/hotbar, outline, scheda statistiche, Sandevistan FX.
- [ ] Installare `cybercraft-0.2.2.jar` nell'istanza Modrinth a gioco chiuso, rimuovendo la 0.2.1.
- [ ] Verificare in gioco che il team client rosso colori l'outline anche delle entità non giocatore; in caso contrario serve un renderer custom.
- [ ] Verificare `copyOnDeath` con morte e respawn reali.
- [ ] Ricalibrare i costi dei quickhack con RAM massima 100.

## Interfaccia e resa

- [ ] GUI dedicata del Banco Impianti e del Tavolo di Installazione.
- [ ] Shader reali per desaturazione e aberrazione cromatica dello scanner.
- [ ] Motion blur o post-processing per il Sandevistan.
- [ ] Texture definitive al posto dei segnaposto.

## Contenuti

- [ ] Fazioni (Arasaka, Militech, Maelstrom) con mob e ricompense.
- [ ] Nuove armi coerenti con il catalogo, se `weapons.md` verrà popolato.
- [ ] Bilanciamento di costi, tolleranza e ricette dopo il test in gioco.

## Manutenzione

- [ ] Aggiungere test automatici per Codec e ricette.
- [ ] Controllo periodico delle versioni Fabric API / loader compatibili con 1.21.1.
- [ ] Aggiornare `docs/` a ogni modifica e mantenere il changelog allineato alla versione.
