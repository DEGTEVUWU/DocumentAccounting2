document.addEventListener("DOMContentLoaded", function() {
    const userId = new URLSearchParams(window.location.search).get('id');

    fetch(`/api/users/${userId}`)
        .then(response => response.json())
        .then(userData => {
            let roles = userData.roles ? Array.from(userData.roles).map(role => role.name).join(', ') : ' - ';

            document.getElementById('username').value = userData.username;
            document.getElementById('email').value = userData.email;
            document.getElementById('name').value = userData.name;
            document.getElementById('last_name').value = userData.last_name;
            // document.getElementById("roles").value = roles;
            if (userData.roles) {
                userData.roles.forEach(role => {
                    const checkbox = document.querySelector(`input[name="roles"][value="${role.name}"]`);
                    if (checkbox) {
                        checkbox.checked = true;
                    }
                });
            }
            if (userData.entered_address) {
                document.getElementById('location').value = userData.entered_address;
            }
        })
        .catch(error => console.error('Ошибка:', error));
});

function submitEditUserForm(event) {
    event.preventDefault();
    const form = document.getElementById('editUserForm');
    const formData = new FormData(form);
    const jsonData = Object.fromEntries(formData.entries());
    const userId = new URLSearchParams(window.location.search).get('id');

    // Обработка чекбоксов для ролей и преобразование их в Set<Long>
    const roles = Array.from(form.querySelectorAll('input[name="roles"]:checked')).map(checkbox => checkbox.value);
    const role_ids = roles.map(role => {
        switch(role) {
            case 'ROLE_ADMIN': return 1;
            case 'ROLE_MODERATOR': return 2;
            case 'ROLE_USER': return 3;
            default: return null;
        }
    }).filter(roleId => roleId !== null);

    jsonData.role_ids = role_ids;
    jsonData.entered_address = document.getElementById('location').value;

    fetch(`/api/users/for-admin/${userId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(jsonData),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Не удалось изменить данные пользователя');
            }
            return response.json();
        })
        .then(data => {
            console.log('Пользователь успешно изменен', data);
            window.location.href = `user_details.html?id=${userId}`;
        })
        .catch((error) => {
            console.error('Ошибка:', error);
        });
}