document.addEventListener("DOMContentLoaded", function() {
    const fileId = new URLSearchParams(window.location.search).get('id');

    fetch(`/api/files/show_data_file/${fileId}`)
        .then(response => response.json())
        .then(docData => {
            fetch('/api/users')
                .then(response => response.json())
                .then(users => {
                    const availableForSelect = document.getElementById('available_for');
                    users.forEach(user => {
                        const option = document.createElement('option');
                        option.value = user.id;
                        option.text = user.username;

                        // Проверяем, есть ли пользователь в списке доступных
                        if (docData.available_for.includes(user.id)) {
                            option.selected = true;
                        }

                        availableForSelect.appendChild(option);
                    });
                })
                .catch(error => console.error('Ошибка при загрузке пользователей:', error));
        })
        .catch(error => console.error('Ошибка:', error));
});

function submitEditForm(event) {
    event.preventDefault();
    const form = document.getElementById('editForm');
    const formData = new FormData(form);
    const jsonData = Object.fromEntries(formData.entries());
    const fileId = new URLSearchParams(window.location.search).get('id'); // Добавлено получение documentId
    jsonData.type_id = parseInt(jsonData.type_id);
    jsonData.public_document = document.getElementById('public_document').checked;
    jsonData.available_for = Array.from(document.getElementById('available_for').selectedOptions)
        .map(option => parseInt(option.value));

    fetch(`/api/files/${fileId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(jsonData),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Не удалось изменить доступ к файлу');
            }
            return response.json();
        })
        .then(data => {
            console.log('Доступ к файлу успешно изменен', data);
            window.location.href = `file_details.html?id=${fileId}`;
        })
        .catch((error) => {
            console.error('Ошибка:', error);
        });
}