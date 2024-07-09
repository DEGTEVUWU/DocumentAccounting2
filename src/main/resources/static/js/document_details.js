document.addEventListener("DOMContentLoaded", function() {
    const documentId = new URLSearchParams(window.location.search).get('id');
    const editButton = document.getElementById('editButton');
    const deleteButton = document.getElementById('deleteButton');

    function loadDocumentData() {
        fetch(`/api/documents/${documentId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Проблема с получением данных');
                }
                return response.json();
            })
            .then(docData => {
                document.getElementById('docTitle').textContent = docData.title || 'Нет заголовка';
                document.getElementById('docNumber').textContent = docData.number;
                document.getElementById('docAuthor').textContent = docData.author ? docData.author.username : 'Неизвестный';
                document.getElementById('docType').textContent = docData.type ? docData.type.type : 'Неопределенный';
                document.getElementById('docContent').textContent = docData.content || 'Нет содержания';
                document.getElementById('docCreationDate').textContent = docData.creationDate;
                document.getElementById('docUpdateDate').textContent = docData.updateDate ? docData.updateDate : 'Нет данных';

                checkUserRole();
                checkCurrentUserIsAuthor();
            })
            .catch(error => {
                console.error('Ошибка:', error);
                document.getElementById('docTitle').textContent = 'Ошибка загрузки';
            });
    }

    function deleteDocument() {
        fetch(`/api/documents/${documentId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Не удалось удалить документ');
                }
                console.log('Документ успешно удалён');
                window.location.href = '/index.html'; // Переадресация на главную страницу после удаления
            })
            .catch((error) => {
                console.error('Ошибка:', error);
            });
    }

    function checkUserRole() {
        fetch(`/api/documents/${documentId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Не удалось получить информацию о пользователе');
                }
                return response.json();
            })
            .then(document => {
                const roles = document.author.roles.map(role => role.name);
                if (roles.includes('ROLE_ADMIN')) {
                    deleteButton.style.display = 'block'; // Показываем кнопку удаления
                    editButton.onclick = function() {
                        window.location.href = `admin_edit_document_form.html?id=${documentId}`;
                    };
                } else {
                    deleteButton.style.display = 'none'; // Скрываем кнопку удаления
                    editButton.onclick = function() {
                        window.location.href = `edit_document_form.html?id=${documentId}`;
                    };
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
            });
    }
    function checkCurrentUserIsAuthor() {
        fetch(`/api/users/check-current-user-is-author/${documentId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Не удалось получить информацию о том, является ли юзер автором документа или нет');
                }
                return response.json();
            })
            .then(isAuthor => {
                if (isAuthor === true) {
                    deleteButton.style.display = 'block'; // Показываем кнопку удаления
                } else {
                    deleteButton.style.display = 'none'; // Скрываем кнопку удаления
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
            });
    }

    // Установить обработчики событий
    deleteButton.onclick = deleteDocument;

    // Проверить роль пользователя при загрузке страницы и является ли юзер автором документа
    // checkUserRole();
    // checkCurrentUserIsAuthor();

    // Загрузить данные документа при загрузке страницы
    loadDocumentData();
});