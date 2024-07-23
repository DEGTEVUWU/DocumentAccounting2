function submitRegisterForm(event) {
    event.preventDefault();
    const form = document.getElementById('registerForm');
    const formData = new FormData(form);
    const jsonData = Object.fromEntries(formData.entries());

    fetch('api/auth/sign-up', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(jsonData),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Не удалось зарегистрировать пользователя');
            }
            return response.json();
        })
        .then(data => {
            console.log('Пользователь успешно зарегистрирован', data);
            /*
            логика для добавления данных полей регистрации в сессию для следующего извлечения на других старницах
             */
            sessionStorage.setItem('registeredUsername', jsonData.username);
            sessionStorage.setItem('registeredPassword', jsonData.password);
            window.location.href = '/login_user.html';
        })
        .catch((error) => {
            console.error('Ошибка:', error);
        });
}