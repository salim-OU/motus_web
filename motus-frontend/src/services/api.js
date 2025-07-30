
const BASE_URL = 'http://localhost:8090';

// Utilitaire pour vérifier si l'utilisateur est connecté
export const isAuthenticated = () => {
  const token = localStorage.getItem('token');
  return !!token;
};

// Récupérer le token JWT
export const getToken = () => {
  return localStorage.getItem('token');
};

// Récupérer les infos utilisateur
export const getUser = () => {
  const user = localStorage.getItem('user');
  return user ? JSON.parse(user) : null;
};

// Déconnexion
export const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  window.location.href = '/login';
};

// 🔧 CORRECTION: Appel API avec gestion d'erreurs améliorée
export const apiCall = async (endpoint, options = {}) => {
  const token = getToken();
  
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` }),
      ...options.headers,
    },
    ...options,
  };

  try {
    console.log('🔍 API Call:', BASE_URL + endpoint);
    console.log('🔍 Method:', config.method || 'GET');
    console.log('🔍 Headers:', config.headers);
    
    const response = await fetch(`${BASE_URL}${endpoint}`, config);
    
    console.log('📊 Response Status:', response.status);
    console.log('📊 Response OK:', response.ok);
    
    // Si token expiré, déconnecter
    if (response.status === 401) {
      console.log('❌ Token expiré, déconnexion automatique');
      logout();
      return null;
    }
    
    // Log des erreurs détaillées
    if (!response.ok) {
      const errorText = await response.text();
      console.error('❌ Erreur API:', {
        status: response.status,
        statusText: response.statusText,
        errorBody: errorText
      });
      
      // Ne pas throw pour les erreurs de sauvegarde de score
      if (endpoint.includes('/score')) {
        console.log('⚠️ Erreur de sauvegarde, mais on continue le jeu');
        return { ok: false, status: response.status, error: errorText };
      }
      
      throw new Error(`API Error: ${response.status} - ${errorText}`);
    }
    
    console.log('✅ API Success');
    return response;
    
  } catch (error) {
    console.error('❌ Network/Fetch Error:', error);
    
    // Pour les erreurs de score, ne pas faire planter l'app
    if (endpoint.includes('/score')) {
      console.log('⚠️ Erreur réseau pour score, jeu continue');
      return { ok: false, error: error.message };
    }
    
    throw error;
  }
};