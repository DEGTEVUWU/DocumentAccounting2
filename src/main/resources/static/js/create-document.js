document.addEventListener("DOMContentLoaded", function() {
    // Заполнение доступных пользователей
    fetch('/api/users')
        .then(response => response.json())
        .then(users => {
            const availableForSelect = document.getElementById('available_for');
            users.forEach(user => {
                const option = document.createElement('option');
                option.value = user.id;
                option.text = user.username;
                availableForSelect.appendChild(option);
            });
        })
        .catch(error => console.error('Ошибка при загрузке пользователей:', error));
});
// Настройка выбора и отмены выбора по клику
// document.getElementById('available_for').addEventListener('click', function(e) {
//     if (e.target.tagName === 'OPTION') {
//         e.target.selected = !e.target.selected;
//     }
// });

function submitForm(event) {
    event.preventDefault();
    const form = document.getElementById('createForm');
    const formData = new FormData(form);
    const jsonData = Object.fromEntries(formData.entries());
    // Преобразование выбранного типа документа в ID
    jsonData.type_id = parseInt(jsonData.type_id);
    // Добавление флага публичности документа
    jsonData.public_document = document.getElementById('public_document').checked;
    // Преобразование выбранных пользователей в массив ID
    jsonData.available_for = Array.from(document.getElementById('available_for').selectedOptions)
        .map(option => parseInt(option.value));

    fetch('/api/documents', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(jsonData),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Не удалось добавить документ');
            }
            return response.json();
        })
        .then(data => {
            console.log('Документ успешно добавлен', data);
            window.location.href = '/index.html'; // Переадресация на главную страницу после добавления
        })
        .catch((error) => {
            console.error('Ошибка:', error);
        });
}