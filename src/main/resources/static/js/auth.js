// Smart Municipality - Auth & Session Manager

const auth = {
    getUser() {
        const str = localStorage.getItem('smart_muni_user');
        return str ? JSON.parse(str) : null;
    },

    setUser(user) {
        localStorage.setItem('smart_muni_user', JSON.stringify(user));
    },

    logout() {
        localStorage.removeItem('smart_muni_user');
        window.location.href = 'login.html';
    },

    requireRole(expectedRole) {
        const user = this.getUser();
        if (!user) {
            window.location.href = 'login.html';
            return null;
        }

        if (expectedRole && user.role !== expectedRole) {
            if (user.role === 'CITIZEN') {
                window.location.href = 'citizen-dashboard.html';
            } else if (user.role === 'MUNICIPAL_OFFICER') {
                window.location.href = 'officer-dashboard.html';
            }
            return null;
        }

        return user;
    },

    initNavbar() {
        const user = this.getUser();
        const userBadge = document.getElementById('nav-user-badge');
        const userNameSpan = document.getElementById('nav-user-name');
        const userAvatarSpan = document.getElementById('nav-user-avatar');

        if (user && userBadge && userNameSpan) {
            userNameSpan.textContent = user.name;
            if (userAvatarSpan) {
                userAvatarSpan.textContent = user.name.charAt(0).toUpperCase();
            }
        }
    }
};

document.addEventListener('DOMContentLoaded', () => {
    auth.initNavbar();
});
