const API_URL = '/api/admin/users';
const ROLES_URL = '/api/admin/roles';

document.addEventListener('DOMContentLoaded', () => {
    loadNavbar();
    loadTable();
    loadRoles();
    setupNewUserForm();
    setupModalEvents();
});

// 1. Navbar
function loadNavbar() {
    fetch('/api/user')
        .then(res => res.json())
        .then(user => {
            document.getElementById('navbar-email').textContent = user.email;
            const roles = user.roles.map(r => r.name.replace('ROLE_', '')).join(', ');
            document.getElementById('navbar-roles').textContent = 'with roles: ' + roles;
        })
        .catch(err => console.error('Navbar error:', err));
}

// 2. Таблица
function loadTable() {
    fetch(API_URL)
        .then(res => res.json())
        .then(users => {
            const tbody = document.getElementById('usersTableBody');
            tbody.innerHTML = '';
            users.forEach(user => {
                const roles = user.roles.map(r => r.name.replace('ROLE_', '')).join(', ');
                tbody.innerHTML += `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.firstName}</td>
                        <td>${user.lastName}</td>
                        <td>${user.age}</td>
                        <td>${user.email}</td>
                        <td>${roles}</td>
                        <td><button class="btn btn-info btn-sm text-white" onclick="openEditModal(${user.id})">Edit</button></td>
                        <td><button class="btn btn-danger btn-sm" onclick="openDeleteModal(${user.id})">Delete</button></td>
                    </tr>
                `;
            });
        })
        .catch(err => console.error('Table error:', err));
}

// 3. Роли для селектов
function loadRoles() {
    fetch(ROLES_URL)
        .then(res => res.json())
        .then(roles => {
            const newSelect = document.getElementById('newRoles');
            const editSelect = document.getElementById('editRoles');
            newSelect.innerHTML = '';
            editSelect.innerHTML = '';
            roles.forEach(role => {
                const name = role.name.replace('ROLE_', '');
                newSelect.innerHTML += `<option value="${role.id}">${name}</option>`;
                editSelect.innerHTML += `<option value="${role.id}">${name}</option>`;
            });
        })
        .catch(err => console.error('Roles error:', err));
}

// 4. Создание
function setupNewUserForm() {
    const form = document.getElementById('newUserForm');
    form.addEventListener('submit', (e) => {
        e.preventDefault();
        const roleIds = Array.from(document.getElementById('newRoles').selectedOptions).map(o => Number(o.value));
        const user = {
            firstName: document.getElementById('newFirstName').value,
            lastName: document.getElementById('newLastName').value,
            age: parseInt(document.getElementById('newAge').value),
            email: document.getElementById('newEmail').value,
            password: document.getElementById('newPassword').value,
            roleIds: roleIds
        };
        fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(user)
        }).then(res => {
            if (res.ok) {
                form.reset();
                loadTable();
                const tab = new bootstrap.Tab(document.getElementById('nav-users-table-tab'));
                tab.show();
            } else {
                alert('Ошибка при создании');
            }
        });
    });
}

// 5. Открытие Edit
function openEditModal(id) {
    fetch(`${API_URL}/${id}`)
        .then(res => res.json())
        .then(user => {
            document.getElementById('editId').value = user.id;
            document.getElementById('editFirstName').value = user.firstName;
            document.getElementById('editLastName').value = user.lastName;
            document.getElementById('editAge').value = user.age;
            document.getElementById('editEmail').value = user.email;
            document.getElementById('editPassword').value = '';
            const select = document.getElementById('editRoles');
            for (let opt of select.options) {
                opt.selected = user.roles.some(r => r.id === Number(opt.value));
            }
            new bootstrap.Modal(document.getElementById('editModal')).show();
        });
}

// 6. Открытие Delete
function openDeleteModal(id) {
    fetch(`${API_URL}/${id}`)
        .then(res => res.json())
        .then(user => {
            document.getElementById('deleteId').value = user.id;
            document.getElementById('deleteFirstName').textContent = user.firstName;
            new bootstrap.Modal(document.getElementById('deleteModal')).show();
        });
}

// 7. Обработчики модалок
function setupModalEvents() {
    // Edit
    document.getElementById('editUserForm').addEventListener('submit', (e) => {
        e.preventDefault();
        const id = document.getElementById('editId').value;
        const roleIds = Array.from(document.getElementById('editRoles').selectedOptions).map(o => Number(o.value));
        const user = {
            firstName: document.getElementById('editFirstName').value,
            lastName: document.getElementById('editLastName').value,
            age: parseInt(document.getElementById('editAge').value),
            email: document.getElementById('editEmail').value,
            password: document.getElementById('editPassword').value,
            roleIds: roleIds
        };
        fetch(`${API_URL}/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(user)
        }).then(res => {
            if (res.ok) {
                bootstrap.Modal.getInstance(document.getElementById('editModal')).hide();
                loadTable();
            }
        });
    });

    // Delete
    document.getElementById('deleteUserForm').addEventListener('submit', (e) => {
        e.preventDefault();
        const id = document.getElementById('deleteId').value;
        fetch(`${API_URL}/${id}`, { method: 'DELETE' })
            .then(res => {
                if (res.ok) {
                    bootstrap.Modal.getInstance(document.getElementById('deleteModal')).hide();
                    loadTable();
                }
            });
    });
}