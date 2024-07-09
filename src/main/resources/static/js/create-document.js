function submitForm(event) {
    event.preventDefault();
    const form = document.getElementById('createForm');
    const formData = new FormData(form);
    const jsonData = Object.fromEntries(formData.entries());
    // Преобразование выбранного типа документа в ID
    jsonData.type_id = parseInt(jsonData.type_id);

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