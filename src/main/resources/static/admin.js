document.addEventListener("DOMContentLoaded", () => {
    loadUsers();
    loadCurrentUserInfo();
});

// 1. Загрузка таблицы (GET /api/admin)
function loadUsers() {
    fetch('/api/admin')
        .then(response => response.json())
        .then(users => {
            const tableBody = document.getElementById('all-users-table-body');
            tableBody.innerHTML = '';
            users.forEach(user => {
                const roles = user.roles.map(r => r.name.replace('ROLE_', '')).join(', ');
                tableBody.innerHTML += `
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
        .catch(error => console.error('Ошибка загрузки:', error));
}

// 2. Загрузка шапки (GET /api/user)
function loadCurrentUserInfo() {
    fetch('/api/user')
        .then(response => response.json())
        .then(user => {
            const emailEl = document.getElementById('navbar-email');
            const rolesEl = document.getElementById('navbar-roles');
            if (emailEl && rolesEl) {
                emailEl.textContent = user.email;
                rolesEl.textContent = 'with roles: ' + user.roles.map(r => r.name.replace('ROLE_', '')).join(', ');
            }
        });
}

// 3. Модалка Edit
function openEditModal(id) {
    fetch(`/api/admin/${id}`)
        .then(response => response.json())
        .then(user => {
            document.getElementById('edit-id').value = user.id;
            document.getElementById('edit-name').value = user.firstName;
            document.getElementById('edit-lastName').value = user.lastName;
            document.getElementById('edit-age').value = user.age;
            document.getElementById('edit-email').value = user.email;

            const select = document.getElementById('edit-roles');
            for (let opt of select.options) {
                opt.selected = user.roles.some(r => r.id == opt.value);
            }
            new bootstrap.Modal(document.getElementById('editModal')).show();
        });
}

// 4. Модалка Delete
function openDeleteModal(id) {
    fetch(`/api/admin/${id}`)
        .then(response => response.json())
        .then(user => {
            document.getElementById('delete-id').value = user.id;
            document.getElementById('delete-name').value = user.firstName;
            document.getElementById('delete-lastName').value = user.lastName;
            document.getElementById('delete-age').value = user.age;
            document.getElementById('delete-email').value = user.email;

            const select = document.getElementById('delete-roles');
            for (let opt of select.options) {
                opt.selected = user.roles.some(r => r.id == opt.value);
            }
            new bootstrap.Modal(document.getElementById('deleteModal')).show();
        });
}

// 5. Обработчик создания (POST)
document.getElementById('createForm')?.addEventListener('submit', (e) => {
    e.preventDefault();
    const roleIds = Array.from(document.getElementById('create-roles').selectedOptions).map(o => Number(o.value));
    const user = {
        firstName: document.getElementById('create-name').value,
        lastName: document.getElementById('create-lastName').value,
        age: parseInt(document.getElementById('create-age').value),
        email: document.getElementById('create-email').value,
        password: document.getElementById('create-password').value,
        roleIds: roleIds
    };
    fetch('/api/admin', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(user)
    })
        .then(async res => {
            if (res.ok) {
                document.getElementById('createForm').reset();
                loadUsers();
                const tab = new bootstrap.Tab(document.querySelector('[data-bs-target="#users-table"]'));
                tab.show();
            } else {
                const errorText = await res.text();
                alert('Ошибка: ' + errorText);
            }
        })
        .catch(error => console.error('Ошибка:', error));
});

// 6. Обработчик редактирования (PUT)
document.getElementById('editModalForm')?.addEventListener('submit', (e) => {
    e.preventDefault();
    const id = document.getElementById('edit-id').value;
    const roleIds = Array.from(document.getElementById('edit-roles').selectedOptions).map(o => Number(o.value));
    const user = {
        firstName: document.getElementById('edit-name').value,
        lastName: document.getElementById('edit-lastName').value,
        age: parseInt(document.getElementById('edit-age').value),
        email: document.getElementById('edit-email').value,
        password: document.getElementById('edit-password').value,
        roleIds: roleIds
    };
    fetch(`/api/admin/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(user)
    }).then(res => {
        if (res.ok) {
            bootstrap.Modal.getInstance(document.getElementById('editModal')).hide();
            loadUsers();
        }
    });
});

// 7. Обработчик удаления (DELETE)
document.getElementById('deleteModalForm')?.addEventListener('submit', (e) => {
    e.preventDefault();
    const id = document.getElementById('delete-id').value;
    fetch(`/api/admin/${id}`, { method: 'DELETE' })
        .then(res => {
            if (res.ok) {
                bootstrap.Modal.getInstance(document.getElementById('deleteModal')).hide();
                loadUsers();
            }
        });
});