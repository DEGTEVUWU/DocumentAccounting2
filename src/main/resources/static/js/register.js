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
            window.location.href = '/index.html';
        })
        .catch((error) => {
            console.error('Ошибка:', error);
        });
}