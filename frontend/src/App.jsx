import React, { useState, useEffect } from 'react';
import { 
  Play, BookOpen, Users, LogOut, ArrowRight, Eye, 
  RefreshCw, Trophy, Shield, Trash2, History, User, Info 
} from 'lucide-react';

const BASE_URL = 'http://localhost:8080/api/game';

// Card Database for Tooltips / Rules
const CARDS_DB = [
  { val: 1, name: 'Contrôleur administratif', count: '5', desc: 'Devinez la carte d’un joueur (sauf Contrôleur). Si c’est correct, il est éliminé.' },
  { val: 2, name: 'Tuteur UTBM', count: '2', desc: 'Regardez secrètement la main d’un autre joueur.' },
  { val: 3, name: 'Jury UTBM', count: '2', desc: 'Comparez votre main avec celle d’un autre joueur. Le plus faible est éliminé.' },
  { val: 4, name: 'Secrétariat UTBM', count: '2', desc: 'Vous êtes protégé de tous les effets jusqu’à votre prochain tour.' },
  { val: 5, name: 'Chargé d’Affaires Pédagogiques', count: '2', desc: 'Choisissez un joueur (vous compris) pour qu’il défausse sa carte et en pioche une nouvelle.' },
  { val: 6, name: 'Directeur de département', count: '1', desc: 'Échangez votre main avec celle d’un autre joueur.' },
  { val: 7, name: 'Réinscription administrative', count: '1', desc: 'Doit être jouée obligatoirement si vous avez aussi le Directeur (6) ou le Chargé d’Affaires (5).' },
  { val: 8, name: 'Validation finale', count: '1', desc: 'Si vous défaussez cette carte pour quelque raison que ce soit, vous êtes éliminé.' },
];

export default function App() {
  const [screen, setScreen] = useState('MENU'); // MENU, SETUP, GAME
  const [playerCount, setPlayerCount] = useState(4);
  const [playerNames, setPlayerNames] = useState(['Joueur 1', 'Joueur 2', 'Joueur 3', 'Joueur 4']);
  const [error, setError] = useState('');
  
  // Game State
  const [gameState, setGameState] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [errorState, setErrorState] = useState('');
  
  // Local Screen Logic
  const [showRules, setShowRules] = useState(false);
  const [passDevicePending, setPassDevicePending] = useState(false);
  const [revealHand, setRevealHand] = useState(false);
  
  // Action Modals State
  const [pendingCardToPlay, setPendingCardToPlay] = useState(null); // The card object being played
  const [selectedTarget, setSelectedTarget] = useState('');
  const [selectedGuess, setSelectedGuess] = useState('');
  const [showActionModal, setShowActionModal] = useState(false);
  
  // Peek Screen
  const [showPeekModal, setShowPeekModal] = useState(false);

  // Sync player name input sizes
  useEffect(() => {
    setPlayerNames(prev => {
      const copy = [...prev];
      if (copy.length < playerCount) {
        for (let i = copy.length; i < playerCount; i++) {
          copy.push(`Joueur ${i + 1}`);
        }
      }
      return copy.slice(0, playerCount);
    });
  }, [playerCount]);

  // REST API Helpers
  const fetchState = async (id) => {
    try {
      const res = await fetch(`${BASE_URL}/${id}`);
      if (!res.ok) throw new Error('Impossible de charger la partie');
      const data = await res.json();
      setGameState(data);
      setErrorState('');
    } catch (err) {
      setErrorState(err.message);
    }
  };

  const handleStartGame = async () => {
    // Validate fields
    for (let i = 0; i < playerCount; i++) {
      if (!playerNames[i] || playerNames[i].trim() === '') {
        setError('Veuillez entrer les noms de tous les joueurs.');
        return;
      }
    }
    setError('');
    setIsLoading(true);

    try {
      const res = await fetch(`${BASE_URL}/create`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ playerNames: playerNames.slice(0, playerCount) }),
      });
      if (!res.ok) throw new Error('Erreur lors de la création de la partie');
      const data = await res.json();
      setGameState(data);
      setScreen('GAME');
      setPassDevicePending(false);
      setRevealHand(true);
      setPendingCardToPlay(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleDraw = async () => {
    if (!gameState) return;
    setIsLoading(true);
    try {
      const res = await fetch(`${BASE_URL}/${gameState.gameId}/draw`, {
        method: 'POST',
      });
      if (!res.ok) {
        const errMsg = await res.text();
        throw new Error(errMsg || 'Erreur lors de la pioche');
      }
      const data = await res.json();
      setGameState(data);
      setErrorState('');
    } catch (err) {
      setErrorState(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  const initiatePlayCard = (card) => {
    if (isLoading) return;
    
    // Check Countess Rule constraint before continuing
    const hand = gameState.activePlayerHand || [];
    const hasReinscription = hand.some(c => c.valeur === 7);
    const hasDirecteur = hand.some(c => c.valeur === 6);
    const hasCharge = hand.some(c => c.valeur === 5);
    
    if (hasReinscription && (hasDirecteur || hasCharge) && card.valeur !== 7) {
      setErrorState('Règle de Réinscription : Vous devez obligatoirement défausser la Réinscription administrative.');
      return;
    }
    
    setErrorState('');

    if (card.necessiteCible) {
      setPendingCardToPlay(card);
      // Auto-select first available valid target if possible
      const validTargets = getValidTargets();
      if (validTargets.length > 0) {
        setSelectedTarget(validTargets[0].nom);
      } else {
        setSelectedTarget('');
      }
      // If Guard, select default guess card type
      setSelectedGuess(CARDS_DB[1].name); // Default guess Tuteur (2)
      setShowActionModal(true);
    } else {
      // Direct play
      executePlayCard(card, null, null);
    }
  };

  const getValidTargets = () => {
    if (!gameState) return [];
    // Targets can't be eliminated, protected, or current player (except for Prince/Chargé which might allow targeting self in general,
    // but the backend d'origine rules: "si cible != null && !cible.estProtege()". 
    // Usually, Prince card (5) can target anybody including yourself.
    // Let's filter players who are in play and not protected.
    return gameState.joueurs.filter(p => 
      !p.elimine && 
      (!p.protege || p.nom === gameState.activePlayerName) && 
      (pendingCardToPlay?.valeur === 5 || p.nom !== gameState.activePlayerName)
    );
  };

  const executePlayCard = async (card, target, guess) => {
    setIsLoading(true);
    setShowActionModal(false);
    try {
      const res = await fetch(`${BASE_URL}/${gameState.gameId}/play`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          cardNom: card.nom,
          targetPlayerName: target,
          guessedCardNom: guess,
        }),
      });
      if (!res.ok) {
        const errMsg = await res.text();
        throw new Error(errMsg || 'Erreur lors du jeu de la carte');
      }
      const data = await res.json();
      setGameState(data);
      setErrorState('');

      // If Tuteur UTBM (Prêtre) was played and returned a peek result, show the peek modal
      if (card.valeur === 2 && data.tuteurStageEffectResult) {
        setShowPeekModal(true);
      }
    } catch (err) {
      setErrorState(err.message);
    } finally {
      setIsLoading(false);
      setPendingCardToPlay(null);
    }
  };

  const handleNextTurn = async () => {
    if (!gameState) return;
    setIsLoading(true);
    try {
      const res = await fetch(`${BASE_URL}/${gameState.gameId}/next-turn`, {
        method: 'POST',
      });
      if (!res.ok) {
        const errMsg = await res.text();
        throw new Error(errMsg || 'Erreur lors du passage de tour');
      }
      const data = await res.json();
      setGameState(data);
      setErrorState('');
      
      // Enter Pass-Device state before showing the new active player's hand
      setPassDevicePending(true);
      setRevealHand(false);
    } catch (err) {
      setErrorState(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleStartNewRound = async () => {
    if (!gameState) return;
    setIsLoading(true);
    try {
      const res = await fetch(`${BASE_URL}/${gameState.gameId}/restart`, {
        method: 'POST',
      });
      if (!res.ok) {
        const errMsg = await res.text();
        throw new Error(errMsg || 'Erreur lors de la relance');
      }
      const data = await res.json();
      setGameState(data);
      setErrorState('');
      setPassDevicePending(true);
      setRevealHand(false);
      setPendingCardToPlay(null);
    } catch (err) {
      setErrorState(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  const quitGame = () => {
    setGameState(null);
    setScreen('MENU');
  };

  // Render Card Icon depending on its value
  const getCardIcon = (val) => {
    switch (val) {
      case 1: return '👮'; // Garde
      case 2: return '🔍'; // Prêtre
      case 3: return '⚔️'; // Baron
      case 4: return '🛡️'; // Servante
      case 5: return '🎓'; // Prince
      case 6: return '👑'; // Roi
      case 7: return '📝'; // Comtesse
      case 8: return '🏅'; // Princesse
      default: return '❓';
    }
  };

  return (
    <div className="app-container">
      {/* ----------------- MENU SCREEN ----------------- */}
      {screen === 'MENU' && (
        <div className="menu-card glass-panel">
          <div className="menu-logo">💌</div>
          <h1 className="menu-title">Love Letter</h1>
          <div className="menu-subtitle">Édition UTBM</div>
          <div className="menu-buttons">
            <button className="btn btn-primary" onClick={() => setScreen('SETUP')}>
              <Play size={18} /> Nouvelle partie
            </button>
            <button className="btn btn-secondary" onClick={() => setShowRules(true)}>
              <BookOpen size={18} /> Règles du jeu
            </button>
          </div>
        </div>
      )}

      {/* ----------------- SETUP SCREEN ----------------- */}
      {screen === 'SETUP' && (
        <div className="setup-card glass-panel">
          <h2 className="setup-title">Configuration</h2>
          <p className="setup-subtitle">Configurez votre partie locale</p>
          
          {error && <div className="error-banner">{error}</div>}

          <div className="form-group">
            <span className="form-label">Nombre de joueurs</span>
            <div className="player-count-buttons">
              {[2, 3, 4].map(num => (
                <button
                  key={num}
                  className={`btn-count ${playerCount === num ? 'active' : ''}`}
                  onClick={() => setPlayerCount(num)}
                >
                  {num} Joueurs
                </button>
              ))}
            </div>
          </div>

          <div className="form-group">
            <span className="form-label">Noms des joueurs</span>
            <div className="player-inputs">
              {Array.from({ length: playerCount }).map((_, idx) => (
                <input
                  key={idx}
                  type="text"
                  className="input-field"
                  placeholder={`Joueur ${idx + 1}`}
                  value={playerNames[idx] || ''}
                  onChange={(e) => {
                    const copy = [...playerNames];
                    copy[idx] = e.target.value;
                    setPlayerNames(copy);
                  }}
                />
              ))}
            </div>
          </div>

          <div style={{ display: 'flex', gap: '1rem', marginTop: '2rem' }}>
            <button className="btn btn-secondary" style={{ flex: 1 }} onClick={() => setScreen('MENU')}>
              Retour
            </button>
            <button className="btn btn-primary" style={{ flex: 2 }} onClick={handleStartGame} disabled={isLoading}>
              {isLoading ? 'Lancement...' : 'Démarrer la partie'} <ArrowRight size={18} />
            </button>
          </div>
        </div>
      )}

      {/* ----------------- GAME SCREEN ----------------- */}
      {screen === 'GAME' && gameState && (
        <div className="game-container">
          
          {/* MAIN BOARD */}
          <div className="game-main">
            
            {/* Header info */}
            <div className="game-header glass-panel">
              <div className="game-title-group">
                <h2>Love Letter : UTBM</h2>
                <p>Première personne à {gameState.pionsPourGagner} jetons gagne la partie</p>
              </div>
              <button className="btn btn-secondary" onClick={quitGame}>
                <LogOut size={16} /> Quitter
              </button>
            </div>

            {/* Opponents Row */}
            <div className="opponents-row">
              {gameState.joueurs.map((p) => {
                const isCurrent = p.nom === gameState.activePlayerName;
                return (
                  <div 
                    key={p.nom} 
                    className={`opponent-card glass-panel 
                      ${isCurrent ? 'active-turn' : ''} 
                      ${p.elimine ? 'eliminated' : ''}
                    `}
                  >
                    <span className="opponent-name">{p.nom}</span>
                    <div className="opponent-avatar">
                      {p.elimine ? '💀' : isCurrent ? '👤' : '❓'}
                    </div>
                    
                    <span className={`opponent-status 
                      ${p.protege ? 'status-protected' : ''}
                      ${p.elimine ? 'status-eliminated' : ''}
                    `}>
                      {p.elimine ? 'Éliminé' : p.protege ? 'Protégé' : 'En jeu'}
                    </span>
                    <span className="pion-badge">🏅 {p.nombrePions} Pions</span>
                  </div>
                );
              })}
            </div>

            {/* Play Area / Controls */}
            <div className="play-area glass-panel" style={{ flexGrow: 1, justifyContent: 'center' }}>
              {errorState && <div className="error-banner" style={{ width: '100%' }}>{errorState}</div>}
              
              {gameState.partieTerminee ? (
                /* Global Game Over */
                <div style={{ textAlign: 'center' }}>
                  <Trophy size={48} color="#f1c40f" style={{ margin: '0 auto 1rem auto' }} />
                  <h3 style={{ fontSize: '1.75rem', marginBottom: '0.5rem' }}>Partie Terminée !</h3>
                  <p style={{ fontSize: '1.1rem', marginBottom: '1.5rem' }}>
                    Le gagnant est : <strong>{gameState.gagnantsPartie?.join(', ')}</strong> 🎉
                  </p>
                  <button className="btn btn-primary" onClick={quitGame}>
                    Retour au menu principal
                  </button>
                </div>
              ) : gameState.finDeManche ? (
                /* Round Over */
                <div style={{ textAlign: 'center' }}>
                  <Trophy size={36} color="#4a8c9e" style={{ margin: '0 auto 0.5rem auto' }} />
                  <h3 style={{ fontSize: '1.5rem', marginBottom: '0.5rem' }}>Manche Terminée !</h3>
                  <p style={{ marginBottom: '1.5rem' }}>
                    Gagnant(s) de la manche : <strong>{gameState.gagnantsManche?.join(', ')}</strong> (+1 jeton)
                  </p>
                  <button className="btn btn-primary" onClick={handleStartNewRound} disabled={isLoading}>
                    <RefreshCw size={16} /> Lancer la manche suivante
                  </button>
                </div>
              ) : (
                /* Standard turn active */
                <>
                  <div className="turn-banner">
                    Tour de : {gameState.activePlayerName}
                  </div>
                  
                  {revealHand ? (
                    <>
                      <div className="hand-container">
                        {gameState.activePlayerHand?.map((card, idx) => (
                          <div 
                            key={idx} 
                            className={`love-card`}
                            onClick={() => initiatePlayCard(card)}
                          >
                            <div className="card-top">
                              <span className={`card-val card-val-${card.valeur}`}>{card.valeur}</span>
                              <span>{getCardIcon(card.valeur)}</span>
                            </div>
                            <div className="card-body">
                              <span className="card-name">{card.nom}</span>
                              <span className="card-desc">{CARDS_DB.find(c => c.val === card.valeur)?.desc}</span>
                            </div>
                          </div>
                        ))}
                      </div>

                      <div style={{ display: 'flex', gap: '1rem', marginTop: '1.5rem' }}>
                        <button 
                          className="btn btn-primary" 
                          onClick={handleDraw} 
                          disabled={gameState.activePlayerHand?.length >= 2 || isLoading}
                        >
                          Piocher une carte
                        </button>
                        
                        <button 
                          className="btn btn-secondary" 
                          onClick={handleNextTurn} 
                          disabled={gameState.activePlayerHand?.length !== 1 || isLoading}
                        >
                          Terminer le tour <ArrowRight size={18} />
                        </button>
                      </div>
                    </>
                  ) : (
                    /* Reveal card block (Pass and Play shield) */
                    <div style={{ textAlign: 'center', padding: '2rem' }}>
                      <Shield size={48} color="#4a8c9e" style={{ margin: '0 auto 1rem auto' }} />
                      <p style={{ fontSize: '1.1rem', marginBottom: '1.5rem' }}>
                        Cliquez ci-dessous pour révéler votre jeu.
                      </p>
                      <button className="btn btn-primary" onClick={() => setRevealHand(true)}>
                        <Eye size={18} /> Révéler mon jeu
                      </button>
                    </div>
                  )}
                </>
              )}
            </div>

          </div>

          {/* SIDEBAR */}
          <div className="sidebar">
            {/* Deck remaining */}
            <div className="sidebar-section glass-panel deck-info">
              <span className="form-label" style={{ marginBottom: 0 }}>Pioche</span>
              <span className="deck-count">{gameState.deckCount}</span>
              <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>cartes restantes</span>
            </div>

            {/* Action History / logs */}
            <div className="sidebar-section glass-panel" style={{ flexGrow: 2 }}>
              <span className="logs-title"><History size={16} /> Historique</span>
              <div className="logs-content">
                {gameState.logs?.slice().reverse().map((log, idx) => (
                  <div key={idx} className="log-item">{log}</div>
                ))}
              </div>
            </div>

            {/* Discard pile */}
            <div className="sidebar-section glass-panel" style={{ flexGrow: 1 }}>
              <span className="discard-title"><Trash2 size={16} /> Défausse</span>
              <div className="discard-content">
                {gameState.defausse?.slice().reverse().map((disc, idx) => (
                  <div key={idx} className="discard-item">🗑️ {disc}</div>
                ))}
              </div>
            </div>
          </div>

        </div>
      )}

      {/* ----------------- RULES FLOATING DIALOG ----------------- */}
      {showRules && (
        <div className="overlay">
          <div className="modal-content glass-panel rules-modal">
            <h2 className="modal-title">Règles du Jeu</h2>
            
            <div className="rules-content">
              <span className="rules-section-title">Objectif</span>
              <p style={{ fontSize: '0.9rem', marginBottom: '1rem' }}>
                Soyez le dernier joueur en lice ou possédez la carte de plus haute valeur à la fin de la manche pour remporter un pion.
              </p>

              <span className="rules-section-title">Déroulement du tour</span>
              <ul className="rules-list">
                <li>Chaque joueur commence avec une carte en main.</li>
                <li>Au début de votre tour, piochez une carte.</li>
                <li>Choisissez une carte à jouer parmi les deux, puis défaussez-la et appliquez son effet.</li>
                <li>Passez l'appareil au joueur suivant.</li>
              </ul>

              <span className="rules-section-title">Les Cartes UTBM</span>
              <div>
                {CARDS_DB.map(c => (
                  <div key={c.val} className="rules-card-item">
                    <strong>{c.val} - {c.name} (x{c.count})</strong> : {c.desc}
                  </div>
                ))}
              </div>
            </div>

            <button className="btn btn-primary" style={{ marginTop: '1.5rem', width: '100%' }} onClick={() => setShowRules(false)}>
              Fermer les règles
            </button>
          </div>
        </div>
      )}

      {/* ----------------- ACTION REQUIREMENT MODAL (TARGET / GUESS) ----------------- */}
      {showActionModal && pendingCardToPlay && (
        <div className="overlay">
          <div className="modal-content glass-panel">
            <h3 className="modal-title" style={{ fontSize: '1.5rem' }}>
              Action requise : {pendingCardToPlay.nom}
            </h3>
            
            <div className="modal-body">
              {/* Target Selection */}
              {getValidTargets().length > 0 ? (
                <div className="form-group" style={{ textAlign: 'left' }}>
                  <label className="form-label">Sélectionner un joueur cible :</label>
                  <select 
                    className="input-field" 
                    value={selectedTarget}
                    onChange={(e) => setSelectedTarget(e.target.value)}
                  >
                    {getValidTargets().map(t => (
                      <option key={t.nom} value={t.nom}>{t.nom}</option>
                    ))}
                  </select>
                </div>
              ) : (
                <p style={{ color: 'var(--danger)', marginBottom: '1rem', fontStyle: 'italic' }}>
                  Aucun joueur ciblable disponible. L'effet sera défaussé sans cible.
                </p>
              )}

              {/* Guess Selection (only for Guard / Contrôleur administratif) */}
              {pendingCardToPlay.valeur === 1 && getValidTargets().length > 0 && (
                <div className="form-group" style={{ textAlign: 'left' }}>
                  <label className="form-label">Deviner sa carte :</label>
                  <div className="select-card-grid">
                    {CARDS_DB.filter(c => c.val > 1).map(c => (
                      <button
                        key={c.val}
                        className={`btn-card-select ${selectedGuess === c.name ? 'active' : ''}`}
                        style={{
                          background: selectedGuess === c.name ? 'var(--primary)' : 'white',
                          color: selectedGuess === c.name ? 'white' : 'var(--text-main)'
                        }}
                        onClick={() => setSelectedGuess(c.name)}
                      >
                        {c.val} - {c.name}
                      </button>
                    ))}
                  </div>
                </div>
              )}
            </div>

            <div style={{ display: 'flex', gap: '1rem' }}>
              <button 
                className="btn btn-secondary" 
                style={{ flex: 1 }} 
                onClick={() => {
                  setShowActionModal(false);
                  setPendingCardToPlay(null);
                }}
              >
                Annuler
              </button>
              <button 
                className="btn btn-primary" 
                style={{ flex: 1 }}
                onClick={() => {
                  const target = getValidTargets().length > 0 ? selectedTarget : null;
                  const guess = pendingCardToPlay.valeur === 1 ? selectedGuess : null;
                  executePlayCard(pendingCardToPlay, target, guess);
                }}
              >
                Confirmer
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ----------------- PEEK SCREEN OVERLAY (Tuteur UTBM Peek) ----------------- */}
      {showPeekModal && gameState && gameState.tuteurStageEffectResult && (
        <div className="overlay">
          <div className="modal-content glass-panel" style={{ maxWidth: '400px' }}>
            <h3 className="modal-title">🔍 Révélation de Carte</h3>
            <div className="modal-body" style={{ padding: '1.5rem', background: '#e9eef7', borderRadius: 'var(--radius-sm)' }}>
              <p style={{ fontSize: '1.1rem', fontWeight: 600, color: 'var(--primary-hover)' }}>
                {gameState.tuteurStageEffectResult}
              </p>
            </div>
            <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '1.5rem' }}>
              Ne montrez pas cet écran aux autres joueurs !
            </p>
            <button className="btn btn-primary" style={{ width: '100%' }} onClick={() => setShowPeekModal(false)}>
              Fermer
            </button>
          </div>
        </div>
      )}

      {/* ----------------- PASS DEVICE SCREEN OVERLAY (Pass and Play Shield) ----------------- */}
      {passDevicePending && gameState && (
        <div className="overlay" style={{ background: '#1a202c', zIndex: 200 }}>
          <div className="modal-content glass-panel" style={{ maxWidth: '450px', padding: '3rem 2rem' }}>
            <Shield size={64} color="#6c5ce7" style={{ margin: '0 auto 1.5rem auto', animation: 'float 3s ease-in-out infinite' }} />
            <h3 className="modal-title" style={{ fontSize: '1.75rem', marginBottom: '1rem' }}>
              Passer l'appareil
            </h3>
            <p style={{ fontSize: '1.1rem', marginBottom: '2rem', color: '#4a5568' }}>
              C'est au tour de <strong>{gameState.activePlayerName}</strong>.<br />
              Donnez-lui l'appareil et cliquez sur le bouton pour continuer.
            </p>
            <button 
              className="btn btn-accent" 
              style={{ width: '100%', padding: '1rem' }} 
              onClick={() => {
                setPassDevicePending(false);
                setRevealHand(false);
              }}
            >
              C'est mon tour ! <ArrowRight size={18} />
            </button>
          </div>
        </div>
      )}
    </div>
  );
}


