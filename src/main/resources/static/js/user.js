document.addEventListener("DOMContentLoaded", () => {
    fetchCurrentUserData();
});

function fetchCurrentUserData() {
    fetch('/api/user')
        .then(response => {
            if (!response.ok) {
                throw new Error("Ошибка получения JSON. Статус: " + response.status);
            }
            return response.json();
        })
        .then(user => {
            const rolesString = user.roles.map(role => role.name.replace('ROLE_', '')).join(', ');

            const navbarEmail = document.getElementById('navbar-email');
            const navbarRoles = document.getElementById('navbar-roles');
            if (navbarEmail) navbarEmail.textContent = user.email;
            if (navbarRoles) navbarRoles.textContent = 'with roles: ' + rolesString;

            const tableBody = document.getElementById('user-info-table-body');
            if (tableBody) {
                tableBody.innerHTML = `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.firstName || ''}</td>
                        <td>${user.lastName || ''}</td>
                        <td>${user.age || ''}</td>
                        <td>${user.email}</td>
                        <td>${rolesString}</td>
                    </tr>
                `;
            }
        })
        .catch(error => {
            console.error('Ошибка fetch:', error);
            const tableBody = document.getElementById('user-info-table-body'); // ✅ ИСПРАВЛЕНО
            if (tableBody) {
                tableBody.innerHTML = `<tr><td colspan="6" class="text-center text-danger">Не удалось загрузить профиль</td></tr>`;
            }
        });
}