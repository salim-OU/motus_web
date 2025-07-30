function GameTile({ letter, state, isFirstLetter = false }) {
    // Déterminer la classe CSS selon l'état
    const getClassName = () => {
      let className = 'tile';
      
      switch (state) {
        case 'correct':
          className += ' tile-correct';  // 🔴 Carré rouge
          break;
        case 'present':
          className += ' tile-present';  // 🟡 Cercle jaune
          break;
        case 'absent':
          className += ' tile-absent';   // 🔵 Fond bleu
          break;
        case 'current':
          className += ' tile-current';  // En cours de saisie
          break;
        default:
          className += ' tile-empty';    // Vide
      }
      
      return className;
    };
  
    return (
      <div className={getClassName()}>
        {isFirstLetter && letter ? (
          <span style={{ fontWeight: 'bold' }}>{letter}</span>
        ) : (
          letter || ''
        )}
      </div>
    );
  }
  
  export default GameTile;