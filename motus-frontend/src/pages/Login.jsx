import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

function Login() {
  const [identifier, setIdentifier] = useState(''); // username ou email
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    if (!identifier.trim()) {
      setError('Veuillez entrer votre nom d\'utilisateur ou email');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await fetch('http://localhost:8090/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username: identifier }),
      });

      const data = await response.json();

      if (response.ok && data.success) {
        // Sauvegarder le token JWT
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify({
          id: data.userId,
          username: data.username,
          email: data.email
        }));
        
        // Rediriger vers le jeu
        navigate('/game');
      } else {
        setError(data.message || 'Erreur de connexion');
      }
    } catch (error) {
      setError('Erreur de connexion au serveur');
      console.error('Login error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="auth-container fade-in">
        <h1 className="text-center mb-2">🎮 Motus</h1>
        <h2 className="text-center mb-2">Connexion</h2>
        
        {error && (
          <div className="message message-error">
            {error}
          </div>
        )}

        <form onSubmit={handleLogin} className="auth-form">
          <input
            type="text"
            className="form-input"
            placeholder="Nom d'utilisateur ou email"
            value={identifier}
            onChange={(e) => setIdentifier(e.target.value)}
            disabled={loading}
          />
          
          <button 
            type="submit" 
            className="btn"
            disabled={loading}
          >
            {loading ? 'Connexion...' : 'Se connecter'}
          </button>
        </form>

        <div className="text-center mt-2">
          <p>
            Pas de compte ? {' '}
            <Link to="/register" style={{ color: 'var(--primary)' }}>
              S'inscrire
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}

export default Login;