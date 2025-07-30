// ===== src/components/GameBoard.jsx - VERSION COMPLÈTE CORRIGÉE =====

import { useState, useEffect, useCallback } from 'react';
import GameTile from './GameTile';
import { apiCall, getUser, logout } from '../services/api';

function GameBoard() {
  // État du jeu
  const [currentWord, setCurrentWord] = useState('');
  const [guesses, setGuesses] = useState(['', '', '', '', '', '']);
  const [currentGuess, setCurrentGuess] = useState('');
  const [currentRow, setCurrentRow] = useState(0);
  const [gameStatus, setGameStatus] = useState('playing'); // playing, won, lost
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [wordId, setWordId] = useState(null);
  
  const user = getUser();

  // Récupérer un mot aléatoire au démarrage
  useEffect(() => {
    fetchNewWord();
  }, []);

  // 🔧 CORRECTION: Récupération de mot avec gestion d'erreurs
  const fetchNewWord = async () => {
    setLoading(true);
    try {
      console.log('🎲 Tentative récupération mot...');
      
      const response = await apiCall('/api/game/word/random');
      
      if (response && response.ok) {
        const data = await response.json();
        console.log('✅ Mot reçu du backend:', data);
        
        // Gestion flexible des formats de réponse
        let word, id;
        
        if (typeof data === 'string') {
          // Si c'est juste une chaîne
          word = data.toUpperCase();
          id = 1;
        } else if (data.word) {
          // Si c'est un objet avec propriété word
          word = data.word.toUpperCase();
          id = data.id || 1;
        } else {
          // Format inattendu, utiliser valeur par défaut
          word = 'MOTUS';
          id = 1;
        }
        
        setCurrentWord(word);
        setWordId(id);
        setMessage(`Trouvez le mot de ${word.length} lettres commençant par "${word[0]}"`);
        
      } else {
        console.error('❌ Échec récupération mot, utilisation mot par défaut');
        setCurrentWord('MOTUS');
        setWordId(1);
        setMessage('Trouvez le mot de 5 lettres commençant par "M" (mot par défaut)');
      }
    } catch (error) {
      console.error('❌ Erreur récupération mot:', error);
      setMessage('Erreur de connexion - Utilisation mot par défaut');
      setCurrentWord('MOTUS');
      setWordId(1);
    } finally {
      setLoading(false);
    }
  };

  // Calculer l'état de chaque lettre
  const getLetterState = (letter, position, rowIndex) => {
    // Si pas de lettre ou ligne future
    if (!letter || rowIndex > currentRow) return 'empty';
    
    // Si ligne courante
    if (rowIndex === currentRow) return 'current';
    
    const guess = guesses[rowIndex];
    if (!guess || guess.length === 0) return 'empty';
    
    // Logique Motus exacte
    // 1. Lettre bien placée (carré rouge)
    if (currentWord[position] === letter) {
      return 'correct';
    }
    
    // 2. Lettre présente mais mal placée (cercle jaune)
    if (currentWord.includes(letter)) {
      return 'present';
    }
    
    // 3. Lettre absente (fond bleu)
    return 'absent';
  };

  // Soumettre une tentative
  const submitGuess = async () => {
    const guess = currentGuess.toUpperCase().trim();
    
    // Validations
    if (guess.length !== currentWord.length) {
      setMessage(`Le mot doit contenir ${currentWord.length} lettres`);
      return;
    }
    
    if (!/^[A-ZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞŸŒ]+$/.test(guess)) {
      setMessage('Utilisez uniquement des lettres');
      return;
    }

    // Mettre à jour les tentatives
    const newGuesses = [...guesses];
    newGuesses[currentRow] = guess;
    setGuesses(newGuesses);

    // Vérifier victoire
    if (guess === currentWord) {
      setGameStatus('won');
      setMessage('🎉 Bravo ! Vous avez trouvé le mot !');
      await saveScore(true);
    } else if (currentRow === 5) {
      setGameStatus('lost');
      setMessage(`😞 Perdu ! Le mot était "${currentWord}"`);
      await saveScore(false);
    } else {
      setCurrentRow(currentRow + 1);
      setCurrentGuess('');
      setMessage(`Tentative ${currentRow + 2}/6`);
    }
  };

  // 🔧 CORRECTION: Sauvegarde score avec paramètres URL
  const saveScore = async (won) => {
    try {
      const score = won ? (6 - currentRow) * 100 : 0;
      
      console.log('💾 Tentative sauvegarde score:', {
        userId: user?.id,
        wordId: wordId,
        score: score,
        won: won,
        currentRow: currentRow
      });
      
      // Validation des données
      if (!user?.id || !wordId) {
        console.error('❌ Données manquantes pour sauvegarde:', {
          userId: user?.id,
          wordId: wordId
        });
        return;
      }
      
      // 🔧 CORRECTION: Utiliser paramètres URL (format attendu par votre backend)
      const endpoint = `/api/game/score?userId=${user.id}&wordId=${wordId}&score=${score}`;
      console.log('📡 Endpoint complet:', `http://localhost:8090${endpoint}`);
      
      const response = await apiCall(endpoint, {
        method: 'POST'
        // PAS de body - votre backend attend les paramètres dans l'URL
      });
      
      if (response && response.ok) {
        console.log('✅ Score sauvegardé avec succès');
        try {
          const result = await response.json();
          console.log('📊 Résultat sauvegarde:', result);
        } catch (e) {
          console.log('✅ Score sauvegardé (pas de JSON en retour)');
        }
      } else {
        console.log('⚠️ Problème sauvegarde score:', response);
        setMessage(prev => prev + ' (Score non sauvegardé)');
      }
    } catch (error) {
      console.error('❌ Erreur sauvegarde score:', error);
      console.log('⚠️ Le jeu continue malgré l\'erreur de sauvegarde');
      setMessage(prev => prev + ' (Erreur sauvegarde)');
    }
  };

  // Nouvelle partie
  const newGame = () => {
    setGuesses(['', '', '', '', '', '']);
    setCurrentGuess('');
    setCurrentRow(0);
    setGameStatus('playing');
    setMessage('');
    fetchNewWord();
  };

  // 🔧 CORRECTION: Gestion clavier avec useCallback
  const handleKeyPress = useCallback((e) => {
    // Empêcher si on tape dans un input
    if (document.activeElement.tagName === 'INPUT') return;
    
    if (gameStatus !== 'playing') return;
    
    if (e.key === 'Enter') {
      e.preventDefault();
      submitGuess();
    } else if (e.key === 'Backspace') {
      e.preventDefault();
      setCurrentGuess(prev => prev.slice(0, -1));
    } else if (/^[a-zA-ZàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿœÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞŸŒ]$/.test(e.key)) {
      if (currentGuess.length < currentWord.length) {
        e.preventDefault();
        setCurrentGuess(prev => prev + e.key.toUpperCase());
      }
    }
  }, [gameStatus, currentGuess, currentWord.length, currentRow]);

  // Écouter les touches du clavier
  useEffect(() => {
    window.addEventListener('keydown', handleKeyPress);
    return () => window.removeEventListener('keydown', handleKeyPress);
  }, [handleKeyPress]);

  // Debug utilisateur
  useEffect(() => {
    console.log('🔍 Info utilisateur:', user);
    console.log('🔍 Word ID:', wordId);
    console.log('🔍 Current Row:', currentRow);
  }, [user, wordId, currentRow]);

  if (loading) {
    return (
      <div className="container">
        <div className="game-board">
          <h1>🎮 Motus</h1>
          <div className="message message-info">
            Chargement du mot...
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container">
      <div className="game-board">
        <h1>🎮 Motus</h1>
        
        {/* Message de statut */}
        {message && (
          <div className={`message ${gameStatus === 'won' ? 'message-success' : 
                                    gameStatus === 'lost' ? 'message-error' : 
                                    'message-info'}`}>
            {message}
          </div>
        )}

        {/* Grille de jeu */}
        <div className="grid">
          {guesses.map((guess, rowIndex) => (
            <div key={rowIndex} className="row">
              {Array.from({ length: currentWord.length }).map((_, colIndex) => {
                let letter = '';
                
                if (rowIndex < currentRow) {
                  // Lignes déjà jouées
                  letter = guess[colIndex] || '';
                } else if (rowIndex === currentRow) {
                  // Ligne courante
                  letter = currentGuess[colIndex] || '';
                  
                  // Première lettre toujours visible
                  if (colIndex === 0 && !letter) {
                    letter = currentWord[0];
                  }
                } else if (colIndex === 0) {
                  // Première lettre des lignes futures
                  letter = currentWord[0];
                }
                
                const state = getLetterState(letter, colIndex, rowIndex);
                const isFirstLetter = colIndex === 0;
                
                return (
                  <GameTile
                    key={colIndex}
                    letter={letter}
                    state={state}
                    isFirstLetter={isFirstLetter}
                  />
                );
              })}
            </div>
          ))}
        </div>

        {/* Zone de saisie */}
        {gameStatus === 'playing' && (
          <div className="input-section">
            <input
              type="text"
              className="word-input"
              value={currentGuess}
              onChange={(e) => {
                // Filtrage strict des caractères
                const value = e.target.value
                  .toUpperCase()
                  .replace(/[^A-ZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞŸŒ]/g, '');
                
                if (value.length <= currentWord.length) {
                  setCurrentGuess(value);
                }
              }}
              onKeyDown={(e) => {
                if (e.key === 'Enter') {
                  e.preventDefault();
                  submitGuess();
                }
              }}
              placeholder={`Mot de ${currentWord.length} lettres`}
              maxLength={currentWord.length}
              autoFocus
            />
            <button 
              className="btn"
              onClick={submitGuess}
              disabled={currentGuess.length !== currentWord.length}
            >
              Valider
            </button>
          </div>
        )}

        {/* Boutons de navigation */}
        <div className="buttons" style={{ marginTop: '2rem' }}>
          <button 
            className="btn"
            onClick={newGame}
            style={{ marginRight: '1rem' }}
          >
            Nouveau mot
          </button>
          
          <button 
            className="btn btn-secondary"
            onClick={() => window.location.href = '/scores'}
            style={{ marginRight: '1rem' }}
          >
            Mes scores
          </button>
          
          <button 
            className="btn btn-secondary"
            onClick={logout}
          >
            Déconnexion
          </button>
        </div>

        {/* Instructions */}
        <div className="text-center mt-2" style={{ fontSize: '14px', color: 'var(--gray)' }}>
          <p>🔴 = Bien placée | 🟡 = Mal placée | 🔵 = Absente</p>
          <p>Tapez dans le champ ou utilisez le clavier directement</p>
        </div>

        {/* Debug info (temporaire) */}
        <div style={{ 
          fontSize: '12px', 
          color: 'var(--gray)', 
          marginTop: '1rem',
          padding: '0.5rem',
          background: '#f5f5f5',
          borderRadius: '4px'
        }}>
          <strong>Debug:</strong> 
          User ID: {user?.id} | 
          Word ID: {wordId} | 
          Word: {currentWord} | 
          Row: {currentRow}
        </div>
      </div>
    </div>
  );
}

export default GameBoard;