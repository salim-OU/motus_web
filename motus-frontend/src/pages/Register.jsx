import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

function Register() {
  const [formData, setFormData] = useState({
    username: '',
    email: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    
    // Validation simple
    if (!formData.username.trim() || !formData.email.trim()) {
      setError('Tous les champs sont obligatoires');
      return;
    }

    if (formData.username.length < 3) {
      setError('Le nom d\'utilisateur doit contenir au moins 3 caractères');
      return;
    }

    if (!formData.email.includes('@')) {
      setError('Email invalide');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await fetch('http://localhost:8090/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
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
        setError(data.message || 'Erreur lors de l\'inscription');
      }
    } catch (error) {
      setError('Erreur de connexion au serveur');
      console.error('Register error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="auth-container fade-in">
        <h1 className="text-center mb-2">🎮 Motus</h1>
        <h2 className="text-center mb-2">Inscription</h2>
        
        {error && (
          <div className="message message-error">
            {error}
          </div>
        )}

        <form onSubmit={handleRegister} className="auth-form">
          <input
            type="text"
            name="username"
            className="form-input"
            placeholder="Nom d'utilisateur"
            value={formData.username}
            onChange={handleChange}
            disabled={loading}
          />
          
          <input
            type="email"
            name="email"
            className="form-input"
            placeholder="Email"
            value={formData.email}
            onChange={handleChange}
            disabled={loading}
          />
          
          <button 
            type="submit" 
            className="btn"
            disabled={loading}
          >
            {loading ? 'Inscription...' : 'S\'inscrire'}
          </button>
        </form>

        <div className="text-center mt-2">
          <p>
            Déjà un compte ? {' '}
            <Link to="/login" style={{ color: 'var(--primary)' }}>
              Se connecter
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}

export default Register;