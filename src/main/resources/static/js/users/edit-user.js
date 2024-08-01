document.addEventListener("DOMContentLoaded", function() {
    const userId = new URLSearchParams(window.location.search).get('id');

    fetch(`/api/users/${userId}`)
        .then(response => response.json())
        .then(userData => {
            document.getElementById('username').value = userData.username;
            document.getElementById('email').value = userData.email;
            document.getElementById('name').value = userData.name;
            document.getElementById('lastName').value = userData.lastName;
        })
        .catch(error => console.error('Ошибка:', error));
});

function submitEditUserForm(event) {
    event.preventDefault();
    const form = document.getElementById('editUserForm');
    const formData = new FormData(form);
    const jsonData = Object.fromEntries(formData.entries());
    const userId = new URLSearchParams(window.location.search).get('id');

    fetch(`/api/users/${userId}`, {
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