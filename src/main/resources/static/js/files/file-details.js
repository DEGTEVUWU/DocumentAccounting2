document.addEventListener("DOMContentLoaded", function() {
    const fileId = new URLSearchParams(window.location.search).get('id');
    const editButton = document.getElementById('editButton');
    const deleteButton = document.getElementById('deleteButton');

    function loadDocumentData() {
        fetch(`/api/files/show_data_file/${fileId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Проблема с получением данных');
                }
                return response.json();
            })
            .then(docData => {
                document.getElementById('fileName').textContent = docData.filename || 'Нет заголовка';
                document.getElementById('fileType').textContent = docData.filetype;
                document.getElementById('fileAuthor').textContent = docData.author ? docData.author : 'Неизвестный';

                const img = document.createElement('img');
                img.src = `/api/files/${docData.id}/thumbnail`;
                img.alt = `${docData.filename}`;
                img.width = 100;
                img.height = 100;
                document.getElementById('fileMiniature').appendChild(img);

                document.getElementById('fileCreationDate').textContent = docData.creationDate;

                checkUserPermissions();
            })
            .catch(error => {
                console.error('Ошибка:', error);
                document.getElementById('docTitle').textContent = 'Ошибка загрузки';
            });
    }

    function deleteDocument() {
        fetch(`/api/files/${fileId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Не удалось удалить файл');
                }
                console.log('Файл успешно удалён');
                window.location.href = '/files.html';
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

        const authorFetch = fetch(`/api/users/check-current-user-is-author-for-files/${fileId}`, {
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
                const isAdmin = roles.includes('ROLE_ADMIN');
                console.log("получил две переменые роли текущего юзера и является ли он автором файла {}", currentUser, isAuthor);

                if (isAuthor || isAdmin) {
                    deleteButton.style.display = 'block';
                    editButton.style.display = 'block';

                    editButton.onclick = function() {
                        const editUrl = `edit_file_form.html?id=${fileId}`;
                        window.location.href = editUrl;
                    };
                } else {
                    deleteButton.style.display = 'none';
                    editButton.style.display = 'none';
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
            });
    }

    deleteButton.onclick = deleteDocument;
    loadDocumentData();
});
