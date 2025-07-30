// /* ===== src/pages/Scores.jsx - VERSION CORRIGÉE (Erreur JSX fixée) ===== */
// import { useState, useEffect } from 'react';
// import { apiCall, getUser, logout } from '../services/api';

// function Scores() {
//   const [scores, setScores] = useState([]);
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState('');
//   const user = getUser();

//   useEffect(() => {
//     fetchScores();
//   }, []);

//   const fetchScores = async () => {
//     setLoading(true);
//     try {
//       const response = await apiCall(`/api/game/scores/user/${user.id}`);
      
//       if (response && response.ok) {
//         const data = await response.json();
//         console.log('Scores reçus:', data);
//         setScores(data);
//       } else {
//         setError('Erreur lors du chargement des scores');
//       }
//     } catch (error) {
//       setError('Erreur de connexion');
//       console.error('Error fetching scores:', error);
//     } finally {
//       setLoading(false);
//     }
//   };

//   // Calculer les victoires
//   const calculateVictories = () => {
//     return scores.filter(score => {
//       const hasPositiveScore = score.score > 0;
//       const isWon = score.won === true || score.isCompleted === true;
//       console.log('Score object:', score);
//       return hasPositiveScore || isWon;
//     }).length;
//   };

//   if (loading) {
//     return (
//       <div className="container">
//         <div className="game-board">
//           <h1>📊 Mes Scores</h1>
//           <div className="message message-info">
//             Chargement des scores...
//           </div>
//         </div>
//       </div>
//     );
//   }

//   const victories = calculateVictories();
//   const totalGames = scores.length;
//   const bestScore = scores.length > 0 ? Math.max(...scores.map(s => s.score)) : 0;
//   const winRate = totalGames > 0 ? Math.round((victories / totalGames) * 100) : 0;

//   return (
//     <div className="container">
//       <div className="game-board">
//         <h1>📊 Mes Scores</h1>
        
//         {error && (
//           <div className="message message-error">
//             {error}
//           </div>
//         )}

//         {/* Statistiques */}
//         <div className="stats-summary" style={{ 
//           display: 'grid', 
//           gridTemplateColumns: 'repeat(auto-fit, minmax(120px, 1fr))', 
//           gap: '1rem', 
//           margin: '2rem 0',
//           textAlign: 'center'
//         }}>
//           <div style={{ 
//             background: 'white', 
//             padding: '1rem', 
//             borderRadius: '4px', 
//             boxShadow: 'var(--shadow)' 
//           }}>
//             <div style={{ fontSize: '24px', fontWeight: 'bold', color: 'var(--primary)' }}>
//               {totalGames}
//             </div>
//             <div style={{ fontSize: '14px', color: 'var(--gray)' }}>
//               Parties jouées
//             </div>
//           </div>
          
//           <div style={{ 
//             background: 'white', 
//             padding: '1rem', 
//             borderRadius: '4px', 
//             boxShadow: 'var(--shadow)' 
//           }}>
//             <div style={{ fontSize: '24px', fontWeight: 'bold', color: 'var(--correct-color)' }}>
//               {victories}
//             </div>
//             <div style={{ fontSize: '14px', color: 'var(--gray)' }}>
//               Victoires
//             </div>
//           </div>
          
//           <div style={{ 
//             background: 'white', 
//             padding: '1rem', 
//             borderRadius: '4px', 
//             boxShadow: 'var(--shadow)' 
//           }}>
//             <div style={{ fontSize: '24px', fontWeight: 'bold', color: 'var(--present-color)' }}>
//               {bestScore}
//             </div>
//             <div style={{ fontSize: '14px', color: 'var(--gray)' }}>
//               Meilleur score
//             </div>
//           </div>

//           <div style={{ 
//             background: 'white', 
//             padding: '1rem', 
//             borderRadius: '4px', 
//             boxShadow: 'var(--shadow)' 
//           }}>
//             <div style={{ fontSize: '24px', fontWeight: 'bold', color: 'var(--absent-color)' }}>
//               {winRate}%
//             </div>
//             <div style={{ fontSize: '14px', color: 'var(--gray)' }}>
//               Taux de victoire
//             </div>
//           </div>
//         </div>

//         {/* 🔧 CORRECTION: Section debug avec JSX correct */}
//         <div style={{ 
//           background: '#f0f0f0', 
//           padding: '1rem', 
//           margin: '1rem 0', 
//           borderRadius: '4px',
//           fontSize: '12px'
//         }}>
//           <strong>Debug Info:</strong>
//           <br />
//           Total scores: {scores.length}
//           <br />
//           {/* 🔧 CORRECTION: Échapper le caractère > */}
//           Scores &gt; 0: {scores.filter((s) => s.score > 0).length}
//           <br />
//           Victoires calculées: {victories}
//           <br />
//           {scores.length > 0 && (
//             <>
//               Dernier score: {JSON.stringify(scores[0], null, 2).slice(0, 200)}...
//             </>
//           )}
//         </div>

//         {/* Liste des scores */}
//         <div className="scores-list">
//           <h3>Historique des parties</h3>
          
//           {scores.length === 0 ? (
//             <div className="message message-info">
//               Aucune partie jouée pour le moment.
//               <br />
//               <button 
//                 className="btn mt-1"
//                 onClick={() => window.location.href = '/game'}
//               >
//                 Jouer votre première partie
//               </button>
//             </div>
//           ) : (
//             <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
//               {scores
//                 .sort((a, b) => new Date(b.dateTime) - new Date(a.dateTime))
//                 .map((score, index) => (
//                   <div key={score.id || index} className="score-item">
//                     <div>
//                       <div style={{ fontWeight: 'bold', fontSize: '16px' }}>
//                         Score: {score.score} points
//                         <span style={{ 
//                           marginLeft: '0.5rem',
//                           padding: '2px 6px',
//                           borderRadius: '3px',
//                           fontSize: '12px',
//                           background: score.score > 0 ? '#d1fae5' : '#fee2e2',
//                           color: score.score > 0 ? '#059669' : '#dc2626'
//                         }}>
//                           {score.score > 0 ? 'GAGNÉ' : 'PERDU'}
//                         </span>
//                       </div>
//                       <div style={{ fontSize: '14px', color: 'var(--gray)' }}>
//                         {score.word?.word ? `Mot: ${score.word.word}` : 'Mot inconnu'}
//                         {score.word?.difficulty && (
//                           <span style={{ 
//                             marginLeft: '0.5rem',
//                             padding: '2px 6px',
//                             borderRadius: '3px',
//                             fontSize: '12px',
//                             background: score.word.difficulty === 'EASY' ? '#d1fae5' :
//                                        score.word.difficulty === 'MEDIUM' ? '#fef3c7' : '#fee2e2',
//                             color: score.word.difficulty === 'EASY' ? '#059669' :
//                                    score.word.difficulty === 'MEDIUM' ? '#d97706' : '#dc2626'
//                           }}>
//                             {score.word.difficulty}
//                           </span>
//                         )}
//                       </div>
//                     </div>
//                     <div style={{ textAlign: 'right' }}>
//                       <div style={{ 
//                         fontSize: '18px',
//                         color: score.score > 0 ? 'var(--correct-color)' : 'var(--gray)'
//                       }}>
//                         {score.score > 0 ? '🏆' : '😞'}
//                       </div>
//                       <div style={{ fontSize: '12px', color: 'var(--gray)' }}>
//                         {new Date(score.dateTime).toLocaleDateString('fr-FR', {
//                           day: '2-digit',
//                           month: '2-digit',
//                           year: '2-digit',
//                           hour: '2-digit',
//                           minute: '2-digit'
//                         })}
//                       </div>
//                     </div>
//                   </div>
//                 ))
//               }
//             </div>
//           )}
//         </div>

//         {/* Boutons de navigation */}
//         <div className="buttons" style={{ marginTop: '2rem' }}>
//           <button 
//             className="btn"
//             onClick={() => window.location.href = '/game'}
//             style={{ marginRight: '1rem' }}
//           >
//             Jouer une partie
//           </button>
          
//           <button 
//             className="btn btn-secondary"
//             onClick={fetchScores}
//             style={{ marginRight: '1rem' }}
//           >
//             Actualiser
//           </button>
          
//           <button 
//             className="btn btn-secondary"
//             onClick={logout}
//           >
//             Déconnexion
//           </button>
//         </div>
//       </div>
//     </div>
//   );
// }

// export default Scores;

/* ===== src/pages/Scores.jsx - VERSION CORRIGÉE (Erreur JSON fixée) ===== */
import { useState, useEffect } from 'react';
import { apiCall, getUser, logout } from '../services/api';

function Scores() {
  const [scores, setScores] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const user = getUser();

  useEffect(() => {
    fetchScores();
  }, []);

  const fetchScores = async () => {
    setLoading(true);
    try {
      const response = await apiCall(`/api/game/scores/user/${user.id}`);
      
      if (response && response.ok) {
        // Récupérer le texte brut d'abord pour l'inspecter
        const rawText = await response.text();
        
        // Debug - vérifier la réponse
        console.log('Response length:', rawText.length);
        console.log('Content-Type:', response.headers.get('content-type'));
        
        // Vérifier si la réponse ressemble à du JSON
        const trimmed = rawText.trim();
        if (!trimmed.startsWith('{') && !trimmed.startsWith('[')) {
          console.error('Response is not JSON:', rawText.slice(0, 200));
          setError('Réponse du serveur invalide');
          return;
        }
        
        // Vérifier les problèmes potentiels autour de la position 58611
        if (rawText.length > 58600) {
          console.log('Characters around position 58611:');
          console.log('Before:', rawText.slice(58605, 58611));
          console.log('At error:', rawText.slice(58611, 58620));
          console.log('Character codes:', [...rawText.slice(58605, 58620)].map(c => c.charCodeAt(0)));
        }
        
        // Essayer de parser le JSON
        let data;
        try {
          data = JSON.parse(rawText);
        } catch (jsonError) {
          console.error('JSON Parse Error:', jsonError);
          console.error('Raw response (first 500 chars):', rawText.slice(0, 500));
          console.error('Raw response (last 500 chars):', rawText.slice(-500));
          
          if (rawText.length > 58600) {
            console.error('Raw response (around error position):', rawText.slice(58600, 58700));
          }
          
          // Essayer de trouver où le JSON devient invalide
          let validJsonEnd = rawText.length;
          for (let i = Math.min(rawText.length - 1, 58700); i >= 0; i -= 100) {
            try {
              JSON.parse(rawText.slice(0, i));
              validJsonEnd = i;
              console.log('Found valid JSON up to position:', validJsonEnd);
              break;
            } catch (e) {
              // Continuer la recherche
            }
          }
          
          setError('Erreur de format des données reçues du serveur');
          return;
        }
        
        console.log('Scores reçus:', data);
        setScores(Array.isArray(data) ? data : []);
        
      } else {
        console.error('Response not OK:', response.status, response.statusText);
        setError(`Erreur ${response.status}: ${response.statusText}`);
      }
    } catch (error) {
      console.error('Error fetching scores:', error);
      
      // Messages d'erreur plus spécifiques
      if (error.name === 'SyntaxError') {
        setError('Données corrompues reçues du serveur');
      } else if (error.name === 'TypeError') {
        setError('Erreur de connexion au serveur');
      } else {
        setError('Erreur inattendue lors du chargement');
      }
    } finally {
      setLoading(false);
    }
  };

  // Calculer les victoires
  const calculateVictories = () => {
    return scores.filter(score => {
      const hasPositiveScore = score.score > 0;
      const isWon = score.won === true || score.isCompleted === true;
      console.log('Score object:', score);
      return hasPositiveScore || isWon;
    }).length;
  };

  if (loading) {
    return (
      <div className="container">
        <div className="game-board">
          <h1>📊 Mes Scores</h1>
          <div className="message message-info">
            Chargement des scores...
          </div>
        </div>
      </div>
    );
  }

  const victories = calculateVictories();
  const totalGames = scores.length;
  const bestScore = scores.length > 0 ? Math.max(...scores.map(s => s.score || 0)) : 0;
  const winRate = totalGames > 0 ? Math.round((victories / totalGames) * 100) : 0;

  return (
    <div className="container">
      <div className="game-board">
        <h1>📊 Mes Scores</h1>
        
        {error && (
          <div className="message message-error">
            {error}
            <button 
              className="btn btn-sm"
              onClick={fetchScores}
              style={{ marginLeft: '1rem' }}
            >
              Réessayer
            </button>
          </div>
        )}

        {/* Statistiques */}
        <div className="stats-summary" style={{ 
          display: 'grid', 
          gridTemplateColumns: 'repeat(auto-fit, minmax(120px, 1fr))', 
          gap: '1rem', 
          margin: '2rem 0',
          textAlign: 'center'
        }}>
          <div style={{ 
            background: 'white', 
            padding: '1rem', 
            borderRadius: '4px', 
            boxShadow: 'var(--shadow)' 
          }}>
            <div style={{ fontSize: '24px', fontWeight: 'bold', color: 'var(--primary)' }}>
              {totalGames}
            </div>
            <div style={{ fontSize: '14px', color: 'var(--gray)' }}>
              Parties jouées
            </div>
          </div>
          
          <div style={{ 
            background: 'white', 
            padding: '1rem', 
            borderRadius: '4px', 
            boxShadow: 'var(--shadow)' 
          }}>
            <div style={{ fontSize: '24px', fontWeight: 'bold', color: 'var(--correct-color)' }}>
              {victories}
            </div>
            <div style={{ fontSize: '14px', color: 'var(--gray)' }}>
              Victoires
            </div>
          </div>
          
          <div style={{ 
            background: 'white', 
            padding: '1rem', 
            borderRadius: '4px', 
            boxShadow: 'var(--shadow)' 
          }}>
            <div style={{ fontSize: '24px', fontWeight: 'bold', color: 'var(--present-color)' }}>
              {bestScore}
            </div>
            <div style={{ fontSize: '14px', color: 'var(--gray)' }}>
              Meilleur score
            </div>
          </div>

          <div style={{ 
            background: 'white', 
            padding: '1rem', 
            borderRadius: '4px', 
            boxShadow: 'var(--shadow)' 
          }}>
            <div style={{ fontSize: '24px', fontWeight: 'bold', color: 'var(--absent-color)' }}>
              {winRate}%
            </div>
            <div style={{ fontSize: '14px', color: 'var(--gray)' }}>
              Taux de victoire
            </div>
          </div>
        </div>

        {/* Section debug avec JSX correct */}
        <div style={{ 
          background: '#f0f0f0', 
          padding: '1rem', 
          margin: '1rem 0', 
          borderRadius: '4px',
          fontSize: '12px'
        }}>
          <strong>Debug Info:</strong>
          <br />
          Total scores: {scores.length}
          <br />
          {/* Échapper le caractère > */}
          Scores &gt; 0: {scores.filter((s) => (s.score || 0) > 0).length}
          <br />
          Victoires calculées: {victories}
          <br />
          {scores.length > 0 && (
            <>
              Dernier score: {JSON.stringify(scores[0], null, 2).slice(0, 200)}...
            </>
          )}
        </div>

        {/* Liste des scores */}
        <div className="scores-list">
          <h3>Historique des parties</h3>
          
          {scores.length === 0 ? (
            <div className="message message-info">
              {error ? 'Impossible de charger les scores.' : 'Aucune partie jouée pour le moment.'}
              <br />
              <button 
                className="btn mt-1"
                onClick={() => window.location.href = '/game'}
              >
                {error ? 'Jouer une partie' : 'Jouer votre première partie'}
              </button>
            </div>
          ) : (
            <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
              {scores
                .sort((a, b) => new Date(b.dateTime || 0) - new Date(a.dateTime || 0))
                .map((score, index) => (
                  <div key={score.id || index} className="score-item">
                    <div>
                      <div style={{ fontWeight: 'bold', fontSize: '16px' }}>
                        Score: {score.score || 0} points
                        <span style={{ 
                          marginLeft: '0.5rem',
                          padding: '2px 6px',
                          borderRadius: '3px',
                          fontSize: '12px',
                          background: (score.score || 0) > 0 ? '#d1fae5' : '#fee2e2',
                          color: (score.score || 0) > 0 ? '#059669' : '#dc2626'
                        }}>
                          {(score.score || 0) > 0 ? 'GAGNÉ' : 'PERDU'}
                        </span>
                      </div>
                      <div style={{ fontSize: '14px', color: 'var(--gray)' }}>
                        {score.word?.word ? `Mot: ${score.word.word}` : 'Mot inconnu'}
                        {score.word?.difficulty && (
                          <span style={{ 
                            marginLeft: '0.5rem',
                            padding: '2px 6px',
                            borderRadius: '3px',
                            fontSize: '12px',
                            background: score.word.difficulty === 'EASY' ? '#d1fae5' :
                                       score.word.difficulty === 'MEDIUM' ? '#fef3c7' : '#fee2e2',
                            color: score.word.difficulty === 'EASY' ? '#059669' :
                                   score.word.difficulty === 'MEDIUM' ? '#d97706' : '#dc2626'
                          }}>
                            {score.word.difficulty}
                          </span>
                        )}
                      </div>
                    </div>
                    <div style={{ textAlign: 'right' }}>
                      <div style={{ 
                        fontSize: '18px',
                        color: (score.score || 0) > 0 ? 'var(--correct-color)' : 'var(--gray)'
                      }}>
                        {(score.score || 0) > 0 ? '🏆' : '😞'}
                      </div>
                      <div style={{ fontSize: '12px', color: 'var(--gray)' }}>
                        {score.dateTime ? new Date(score.dateTime).toLocaleDateString('fr-FR', {
                          day: '2-digit',
                          month: '2-digit',
                          year: '2-digit',
                          hour: '2-digit',
                          minute: '2-digit'
                        }) : 'Date inconnue'}
                      </div>
                    </div>
                  </div>
                ))
              }
            </div>
          )}
        </div>

        {/* Boutons de navigation */}
        <div className="buttons" style={{ marginTop: '2rem' }}>
          <button 
            className="btn"
            onClick={() => window.location.href = '/game'}
            style={{ marginRight: '1rem' }}
          >
            Jouer une partie
          </button>
          
          <button 
            className="btn btn-secondary"
            onClick={fetchScores}
            style={{ marginRight: '1rem' }}
            disabled={loading}
          >
            {loading ? 'Chargement...' : 'Actualiser'}
          </button>
          
          <button 
            className="btn btn-secondary"
            onClick={logout}
          >
            Déconnexion
          </button>
        </div>
      </div>
    </div>
  );
}

export default Scores;