document.addEventListener("DOMContentLoaded", function() {
    const userId = new URLSearchParams(window.location.search).get('id');
    const editButton = document.getElementById('editButton');
    const deleteButton = document.getElementById('deleteButton');

    function loadUserData() {
        fetch(`/api/users/${userId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Проблема с получением данных');
                }
                return response.json();
            })
            .then(userData => {
                let roles = userData.roles ? Array.from(userData.roles).map(role => role.name).join(', ') : ' - ';

                document.getElementById('Username').textContent = userData.username;
                document.getElementById('Name').textContent = userData.name ? userData.name : ' - ';
                document.getElementById('LastName').textContent = userData.lastName ? userData.lastName : ' - ';
                document.getElementById('Email').textContent = userData.email ? userData.email : ' - ';
                document.getElementById('Roles').textContent = roles;

                checkCurrentUserAndRole(userData.id);
            })
            .catch(error => {
                console.error('Ошибка:', error);
                document.getElementById('docTitle').textContent = 'Ошибка загрузки';
            });
    }

    function deleteUser() {
        fetch(`/api/users/${userId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Не удалось удалить юзера');
                }
                console.log('Юзер успешно удалён');
                window.location.href = '/users.html'; // Переадресация на главную страницу после удаления
            })
            .catch((error) => {
                console.error('Ошибка:', error);
            });
    }
    // function checkUserRole() {
    //     fetch(`/api/users/${userId}`, {
    //         method: 'GET',
    //         headers: {
    //             'Content-Type': 'application/json',
    //         }
    //     })
    //         .then(response => {
    //             if (!response.ok) {
    //                 throw new Error('Не удалось получить информацию о пользователе');
    //             }
    //             return response.json();
    //         })
    //         .then(user => {
    //             const roles = user.roles.map(role => role.name);
    //             if (roles.includes('ROLE_ADMIN')) {
    //                 deleteButton.style.display = 'block'; // Показываем кнопку удаления
    //                 editButton.onclick = function() {
    //                     window.location.href = `admin_edit_user_form.html?id=${userId}`;
    //                 };
    //             } else {
    //                 deleteButton.style.display = 'none'; // Скрываем кнопку удаления
    //                 editButton.onclick = function() {
    //                     window.location.href = `edit_user_form.html?id=${userId}`;
    //                 };
    //             }
    //         })
    //         .catch(error => {
    //             console.error('Ошибка:', error);
    //         });
    // }
    // function checkCurrentUser() {
    //     fetch(`/api/users/current-user`, {
    //         method: 'GET',
    //         headers: {
    //             'Content-Type': 'application/json',
    //         }
    //     })
    //         .then(response => {
    //             if (!response.ok) {
    //                 throw new Error('Не удалось получить информацию о текущем юзере по аутентификации');
    //             }
    //             return response.json();
    //         })
    //         .then(user => {
    //             if (user.id === userId) {
    //                 deleteButton.style.display = 'block'; // Показываем кнопку удаления
    //             } else {
    //                 deleteButton.style.display = 'none'; // Скрываем кнопку удаления
    //             }
    //         })
    //         .catch(error => {
    //             console.error('Ошибка:', error);
    //         });
    // }
    function checkCurrentUserAndRole(userIdFromData) {
        fetch(`/api/users/current-user`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Не удалось получить информацию о текущем юзере по аутентификации');
                }
                return response.json();
            })
            .then(currentUser => {
                const isAdminOrModerator = currentUser.roles.some(role => role.name === 'ROLE_ADMIN' || role.name === 'ROLE_MODERATOR');
                const isCurrentUser = currentUser.id === userIdFromData;

                if (isAdminOrModerator) {
                    deleteButton.style.display = 'block'; // Показываем кнопку удаления для админов и модераторов
                    editButton.onclick = function() {
                        window.location.href = `admin_edit_user_form.html?id=${userId}`;
                    };
                } else {
                    editButton.onclick = function() {
                        window.location.href = `edit_user_form.html?id=${userId}`;
                    };
                }

                if (isCurrentUser || isAdminOrModerator) {
                    deleteButton.style.display = 'block'; // Показываем кнопку удаления для текущего пользователя и админов
                } else {
                    deleteButton.style.display = 'none'; // Скрываем кнопку удаления для остальных пользователей
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
            });
    }

    // Установить обработчики событий
    // editButton.onclick = function() {
    //     window.location.href = `edit_user_form.html?id=${userId}`;
    // };
    deleteButton.onclick = deleteUser;

    // checkUserRole();
    // checkCurrentUser();

    // Загрузить данные документа при загрузке страницы
    loadUserData();
});