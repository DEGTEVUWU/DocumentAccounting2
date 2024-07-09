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