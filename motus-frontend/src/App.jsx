/* ===== src/App.jsx ===== */
import { Routes, Route, Navigate } from 'react-router-dom';
import { isAuthenticated } from './services/api';
import Login from './pages/Login';
import Register from './pages/Register';
import Game from './pages/Game';
import Scores from './pages/Scores';

// Composant pour protéger les routes
function ProtectedRoute({ children }) {
  return isAuthenticated() ? children : <Navigate to="/login" replace />;
}

// Composant pour rediriger si déjà connecté
function AuthRoute({ children }) {
  return !isAuthenticated() ? children : <Navigate to="/game" replace />;
}

function App() {
  return (
    <div className="App">
      <Routes>
        {/* Routes publiques (seulement si non connecté) */}
        <Route 
          path="/login" 
          element={
            <AuthRoute>
              <Login />
            </AuthRoute>
          } 
        />
        <Route 
          path="/register" 
          element={
            <AuthRoute>
              <Register />
            </AuthRoute>
          } 
        />
        
        {/* Routes protégées (seulement si connecté) */}
        <Route 
          path="/game" 
          element={
            <ProtectedRoute>
              <Game />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/scores" 
          element={
            <ProtectedRoute>
              <Scores />
            </ProtectedRoute>
          } 
        />
        
        {/* Redirection par défaut */}
        <Route 
          path="/" 
          element={
            isAuthenticated() ? 
              <Navigate to="/game" replace /> : 
              <Navigate to="/login" replace />
          } 
        />
        
        {/* Page 404 */}
        <Route 
          path="*" 
          element={
            <div className="container">
              <div className="auth-container text-center">
                <h1>404</h1>
                <p>Page non trouvée</p>
                <button 
                  className="btn mt-2"
                  onClick={() => window.location.href = '/'}
                >
                  Retour à l'accueil
                </button>
              </div>
            </div>
          } 
        />
      </Routes>
    </div>
  );
}

export default App;