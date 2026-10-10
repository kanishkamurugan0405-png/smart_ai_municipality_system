// Smart Municipality - API Client

const API_BASE = window.location.origin.includes('8080') ? '' : 'http://localhost:8080';

const api = {
    // Auth
    async login(email, password) {
        const res = await fetch(`${API_BASE}/api/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Login failed');
        return data;
    },

    async register(name, email, password, phone, role = 'CITIZEN') {
        const res = await fetch(`${API_BASE}/api/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password, phone, role })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Registration failed');
        return data;
    },

    // Complaints
    async createComplaint(formData) {
        const res = await fetch(`${API_BASE}/api/complaints`, {
            method: 'POST',
            body: formData
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Failed to submit complaint');
        return data;
    },

    async getAllComplaints(filters = {}) {
        const params = new URLSearchParams();
        if (filters.category && filters.category !== 'ALL') params.append('category', filters.category);
        if (filters.severity && filters.severity !== 'ALL') params.append('severity', filters.severity);
        if (filters.status && filters.status !== 'ALL') params.append('status', filters.status);
        if (filters.departmentId) params.append('departmentId', filters.departmentId);
        if (filters.location) params.append('location', filters.location);
        if (filters.search) params.append('search', filters.search);

        const res = await fetch(`${API_BASE}/api/complaints?${params.toString()}`);
        if (!res.ok) throw new Error('Failed to fetch complaints');
        return await res.json();
    },

    async getComplaintById(id) {
        const res = await fetch(`${API_BASE}/api/complaints/${id}`);
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Complaint not found');
        return data;
    },

    async getUserComplaints(userId) {
        const res = await fetch(`${API_BASE}/api/complaints/user/${userId}`);
        if (!res.ok) throw new Error('Failed to fetch user complaints');
        return await res.json();
    },

    async assignDepartment(id, departmentId, comment, officerName) {
        const res = await fetch(`${API_BASE}/api/complaints/${id}/assign`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ departmentId, comment, officerName })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Failed to assign department');
        return data;
    },

    async updateStatus(id, status, comment, officerName) {
        const res = await fetch(`${API_BASE}/api/complaints/${id}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status, comment, officerName })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Failed to update status');
        return data;
    },

    async resolveComplaint(id, resolutionNote, resolvedBy) {
        const res = await fetch(`${API_BASE}/api/complaints/${id}/resolve`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ resolutionNote, resolvedBy })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Failed to resolve complaint');
        return data;
    },

    // Departments
    async getDepartments() {
        const res = await fetch(`${API_BASE}/api/departments`);
        if (!res.ok) throw new Error('Failed to fetch departments');
        return await res.json();
    },

    // Hotspots
    async getHotspots() {
        const res = await fetch(`${API_BASE}/api/hotspots`);
        if (!res.ok) throw new Error('Failed to fetch hotspots');
        return await res.json();
    },

    // Dashboard Statistics
    async getDashboardStats() {
        const res = await fetch(`${API_BASE}/api/dashboard/statistics`);
        if (!res.ok) throw new Error('Failed to fetch dashboard statistics');
        return await res.json();
    }
};

// Toast notification helper
function showToast(message, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    let icon = 'ℹ️';
    if (type === 'success') icon = '✅';
    if (type === 'error') icon = '⚠️';
    toast.innerHTML = `<span>${icon}</span> <span>${message}</span>`;
    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(10px)';
        toast.style.transition = 'all 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}
