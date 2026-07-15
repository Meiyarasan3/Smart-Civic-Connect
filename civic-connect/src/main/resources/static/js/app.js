// ============================================
// Civic Connect — API Client & Auth Manager
// ============================================

const API = '/api';

// ── Token Management ──

function saveAuth(data) {
    localStorage.setItem('token', data.token);
    localStorage.setItem('userId', data.userId);
    localStorage.setItem('userName', data.name);
    localStorage.setItem('userEmail', data.email);
    localStorage.setItem('userRole', data.role);
}

function getToken()    { return localStorage.getItem('token'); }
function getUserId()   { return localStorage.getItem('userId'); }
function getUserName() { return localStorage.getItem('userName'); }
function getUserRole() { return localStorage.getItem('userRole'); }
function isLoggedIn()  { return !!getToken(); }

function logout() {
    localStorage.clear();
    window.location.href = '/login.html';
}

// ── HTTP Helpers ──

async function apiCall(method, url, body = null) {
    const headers = { 'Content-Type': 'application/json' };
    const token = getToken();
    if (token) headers['Authorization'] = 'Bearer ' + token;

    const options = { method, headers };
    if (body) options.body = JSON.stringify(body);

    const response = await fetch(API + url, options);

    if (response.status === 401 || response.status === 403) {
        logout();
        return;
    }

    return await response.json();
}

const api = {
    get:    (url) => apiCall('GET', url),
    post:   (url, body) => apiCall('POST', url, body),
    put:    (url, body) => apiCall('PUT', url, body),
    delete: (url) => apiCall('DELETE', url),
};

// ── Auth API ──

async function registerUser(name, email, password, phone, role) {
    return api.post('/auth/register', { name, email, password, phone, role });
}

async function loginUser(email, password) {
    return api.post('/auth/login', { email, password });
}

// ── Shared UI Helpers ──

function showAlert(container, message, type = 'error') {
    container.innerHTML = `<div class="alert alert-${type}">${message}</div>`;
    setTimeout(() => container.innerHTML = '', 5000);
}

function formatDate(dateStr) {
    if (!dateStr) return '—';
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-IN', {
        day: 'numeric', month: 'short', year: 'numeric',
        hour: '2-digit', minute: '2-digit'
    });
}

function formatCategory(cat) {
    return cat.replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase());
}

function formatStatus(status) {
    return status.replace(/_/g, ' ');
}

// ── Route Guard ──

function requireAuth(allowedRoles) {
    if (!isLoggedIn()) {
        window.location.href = '/login.html';
        return false;
    }
    if (allowedRoles && !allowedRoles.includes(getUserRole())) {
        window.location.href = '/login.html';
        return false;
    }
    return true;
}

// ── Build Navbar ──

function renderNavbar(activePage) {
    const role = getUserRole();
    const name = getUserName();

    let links = '';
    if (role === 'CITIZEN') {
        links = `
            <a href="/citizen-dashboard.html" class="${activePage === 'dashboard' ? 'active' : ''}">Dashboard</a>
            <a href="/citizen-submit.html" class="${activePage === 'submit' ? 'active' : ''}">New Complaint</a>
            <a href="/citizen-track.html" class="${activePage === 'track' ? 'active' : ''}">Track</a>
        `;
    } else if (role === 'ADMIN') {
        links = `
            <a href="/admin-dashboard.html" class="${activePage === 'dashboard' ? 'active' : ''}">Dashboard</a>
            <a href="/admin-complaints.html" class="${activePage === 'complaints' ? 'active' : ''}">Complaints</a>
        `;
    } else if (role === 'DEPARTMENT') {
        links = `
            <a href="/dept-dashboard.html" class="${activePage === 'dashboard' ? 'active' : ''}">Dashboard</a>
        `;
    }

    document.getElementById('navbar').innerHTML = `
        <nav class="navbar-civic">
            <a href="/" class="brand">CIVIC CONNECT</a>
            <div class="nav-links">${links}</div>
            <div class="user-info">
                <span>${name}</span>
                <span class="role-badge">${role}</span>
                <button class="btn-logout" onclick="logout()">Logout</button>
            </div>
        </nav>
    `;
}
