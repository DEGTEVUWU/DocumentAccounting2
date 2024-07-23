async function uploadFile(event) {
    event.preventDefault(); // Предотвращаем стандартное поведение формы

    const fileInput = document.getElementById('fileInput');
    const file = fileInput.files[0];

    const publicDocument = document.getElementById('public_document').checked;
    const availableFor = Array.from(document.getElementById('available_for').selectedOptions)
        .map(option => parseInt(option.value));

    const params = {
        public_document: publicDocument,
        available_for: availableFor
    };

    const formData = new FormData();
    formData.append('file', file);
    formData.append('params', JSON.stringify(params));

    try {
        const response = await fetch('/api/files/upload', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            const result = await response.json();
            alert('File uploaded successfully: ' + result.id);
            console.log('Доступ к файлу успешно изменен', result);
            window.location.href = `files.html`;
        } else {
            alert('File upload failed.');
        }
    } catch (error) {
        console.error('Ошибка при загрузке файла:', error);
    }
}

async function viewFile() {
    const fileId = document.getElementById('fileIdInput').value;
    window.open(`/api/files/${fileId}?download=false`, '_blank');
}

async function downloadFile() {
    const fileId = document.getElementById('fileIdInput').value;
    window.open(`/api/files/${fileId}?download=true`, '_blank');
}

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
