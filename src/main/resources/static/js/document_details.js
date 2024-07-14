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

                checkUserPermissions();

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

    function checkUserPermissions() {
        const getTheRoleOfTheCurrentUser = fetch(`/api/users/current-user`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        }).then(response => {
            console.log('получили инфу о текущем авторизованном пользователе', response);
            if (!response.ok) {
                throw new Error('Не удалось получить информацию о текущем авторизованном пользователе');
            }
            return response.json();
        });

        const authorFetch = fetch(`/api/users/check-current-user-is-author/${documentId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        }).then(response => {
            if (!response.ok) {
                throw new Error('Не удалось получить информацию о том, является ли юзер автором документа или нет');
            }
            return response.json();
        });

        Promise.all([getTheRoleOfTheCurrentUser, authorFetch])
            .then(([currentUser, isAuthor]) => {
                const roles = currentUser.roles.map(role => role.name);
                console.log('вытащили из документа роли {}', roles);
                if (isAuthor || roles.includes('ROLE_ADMIN')) {
                    deleteButton.style.display = 'block'; // Показываем кнопку удаления
                } else {
                    deleteButton.style.display = 'none'; // Скрываем кнопку удаления
                }

                editButton.onclick = function() {
                    const editUrl = roles.includes('ROLE_ADMIN') ? `admin_edit_document_form.html?id=${documentId}` : `edit_document_form.html?id=${documentId}`;
                    window.location.href = editUrl;
                };
            })
            .catch(error => {
                console.error('Ошибка:', error);
            });
    }

    deleteButton.onclick = deleteDocument;

    // Загрузить данные документа при загрузке страницы
    loadDocumentData();
});