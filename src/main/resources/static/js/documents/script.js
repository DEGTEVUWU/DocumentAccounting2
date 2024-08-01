document.addEventListener("DOMContentLoaded", function() {
    fetch('/api/users/current-user')
        .then(response => response.json())
        .then(user => {
            const roles = user.roles.map(role => role.name);
            let endpoint = '/api/documents/for_user';

            if (roles.includes('ROLE_ADMIN') || roles.includes('ROLE_MODERATOR')) {
                endpoint = '/api/documents';
            }
            return fetch(endpoint);
        })
        .then(response => response.json())
        .then(documents => {
            const tableBody = document.getElementById('documentsTable').getElementsByTagName('tbody')[0];
            documents.forEach(docData => {
                let row = tableBody.insertRow();
                row.setAttribute('data-id', docData.id); // Устанавливаем атрибут `data-id`
                row.innerHTML = `
          <td>${docData.number}</td>
          <td>${docData.title}</td>
          <td>${docData.author ? docData.author.username : 'Неизвестный'}</td>
          <td>${docData.type ? docData.type.type : 'Неопределенный'}</td>
          <td>${truncateText(docData.content, 150)}</td> 
          <td>${docData.creationDate}</td>
          <td>${docData.updateDate ? docData.updateDate : 'Нет данных'}</td>
        `;
                row.addEventListener('click', function() {
                    window.location.href = `document_details.html?id=${docData.id}`;
                });
            });
        })
        .catch(error => console.error('Ошибка:', error));
});

function truncateText(text, maxLength) {
    if (text.length > maxLength) {
        return text.slice(0, maxLength) + "...";
    } else {
        return text;
    }
}
