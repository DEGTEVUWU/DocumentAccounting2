document.addEventListener("DOMContentLoaded", function() {
    const documentId = new URLSearchParams(window.location.search).get('id');

    fetch(`/api/documents/${documentId}`)
        .then(response => response.json())
        .then(docData => {
            document.getElementById('title').value = docData.title;
            document.getElementById('number').value = docData.number;
            document.getElementById('author_id').value = docData.author.idUser; // Предполагаем, что вы получаете authorId
            document.getElementById('content').value = docData.content;
            document.getElementById('type_id').value = docData.type.id; // Предполагаем, что вы получаете typeId
        })
        .catch(error => console.error('Ошибка:', error));
});

function submitEditForm(event) {
    event.preventDefault();
    const form = document.getElementById('editForm');
    const formData = new FormData(form);
    const jsonData = Object.fromEntries(formData.entries());
    const documentId = new URLSearchParams(window.location.search).get('id'); // Добавлено получение documentId
    jsonData.type_id = parseInt(jsonData.type_id);

    fetch(`/api/documents/for_admin/${documentId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(jsonData),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Не удалось изменить документ');
            }
            return response.json();
        })
        .then(data => {
            console.log('Документ успешно изменен', data);
            window.location.href = '/index.html'; // Переадресация на главную страницу после добавления
        })
        .catch((error) => {
            console.error('Ошибка:', error);
        });
}