/**
    логика по извлечению данных из sessionStorage и заполнению полей
 */
document.addEventListener('DOMContentLoaded', function() {
    const username = sessionStorage.getItem('registeredUsername');
    const password = sessionStorage.getItem('registeredPassword');
    if (username && password) {
        document.getElementById('username').value = username;
        document.getElementById('password').value = password;
        // Очистка sessionStorage после заполнения полей
        sessionStorage.removeItem('registeredUsername');
        sessionStorage.removeItem('registeredPassword');
    }
});

function submitLoginForm(event) {
    event.preventDefault();
    const form = document.getElementById('loginForm');
    const formData = new FormData(form);
    const jsonData = Object.fromEntries(formData.entries());

    fetch('api/auth/sign-in', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(jsonData),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Пользователь не зарегистрирован');
            }
            return response.json();
        })
        .then(data => {
            console.log('Добро пожаловать, ', data);
            localStorage.setItem('authToken', data.jwtToken);
            window.location.href = '/index.html';
        })
        .catch((error) => {
            console.error('Ошибка:', error);
        });
}