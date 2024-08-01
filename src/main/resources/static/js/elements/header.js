document.addEventListener("DOMContentLoaded", function() {
    fetch('header.html')
        .then(response => response.text())
        .then(data => {
            document.body.insertAdjacentHTML('beforeend', data);
            updateAuthButton();
            updateProfileButton(); // Обновление кнопки профиля
            adjustSearchButtonBehavior();
        })
        .catch(error => console.error('Ошибка загрузки хедера:', error));
});
function updateAuthButton() {
    const authButton = document.getElementById('authButton');
    const isAuthenticated = checkAuthentication();

    if (isAuthenticated) {
        authButton.textContent = 'Выйти';
        authButton.onclick = function() {
            logout();
        };
    } else {
        authButton.textContent = 'Войти';
        authButton.onclick = function() {
            window.location.href = 'login_user.html';
        };
    }
}
function updateProfileButton() {
    const profileButton = document.getElementById('profileButton');
    const isAuthenticated = checkAuthentication();

    if (isAuthenticated) {
        fetch('api/users/current-user', {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('authToken')}`
            }
        })
            .then(response => response.json())
            .then(user => {
                profileButton.style.display = 'block';
                profileButton.onclick = function() {
                    window.location.href = `user_details.html?id=${user.id}`;
                };
            })
            .catch(error => console.error('Ошибка получения текущего пользователя:', error));
    } else {
        profileButton.style.display = 'none';
    }
}
function checkAuthentication() {
    // Логика проверки авторизации пользователя
    return !!localStorage.getItem('authToken');
}

function logout() {
    localStorage.removeItem('authToken');
    fetch('api/auth/sign-out', {
        method: 'POST',
        credentials: 'include' // необходимо для отправки кук
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            window.location.href = 'index.html';
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function handleAuthButtonClick() {
    const isAuthenticated = checkAuthentication();
    if (isAuthenticated) {
        logout();
    } else {
        window.location.href = 'login_user.html';
    }
}

function toggleSidebar(sidebarId) {
    const sidebar = document.getElementById(sidebarId);

    if (sidebar.style.width === '300px') {
        sidebar.style.width = '0';
    } else {
        sidebar.style.width = '300px';
    }
}
function adjustSearchButtonBehavior() {
    const searchButton = document.getElementById('searchButton');
    if (window.location.pathname.includes('/html/files/files.html')) {
        searchButton.setAttribute('onclick', "toggleSidebar('sidebarForFiles');");
    } else {
        searchButton.setAttribute('onclick', "toggleSidebar('sidebar');");
    }
}

function submitSearchForm(event) {
    event.preventDefault();
    const form = document.getElementById('searchForm');
    const formData = new FormData(form);
    const params = new URLSearchParams(formData).toString();
    console.log("params - ", params);
    localStorage.setItem('searchParams', params);

    window.location.href = 'search_documents.html';
}

function submitSearchFormForFiles(event) {
    event.preventDefault();
    const form = document.getElementById('searchFormForFiles');
    const formData = new FormData(form);
    const params = new URLSearchParams(formData).toString();
    localStorage.setItem('searchParams', params);
    window.location.href = 'search_files.html';
}

function adjustSearchButtonBehavior() {
    const searchButton = document.getElementById('searchButton');
    if (window.location.pathname.includes('files.html')) {
        searchButton.setAttribute('onclick', "toggleSidebar('sidebarForFiles');");
    } else {
        searchButton.setAttribute('onclick', "toggleSidebar('sidebar');");
    }
}

function displaySearchResults(data) {
    const resultsContainer = document.getElementById('resultsContainer');
    resultsContainer.innerHTML = '';

    data.content.forEach(doc => {
        const docElement = document.createElement('div');
        docElement.className = 'document';
        docElement.innerHTML = `
            <h3>${doc.title}</h3>
            <p><strong>Номер:</strong> ${doc.number}</p>
            <p><strong>Автор:</strong> ${doc.author}</p>
            <p><strong>Содержание:</strong> ${doc.content}</p>
            <p><strong>Тип:</strong> ${doc.type}</p>
            <p><strong>Дата создания:</strong> ${doc.creationDate}</p>
        `;
        resultsContainer.appendChild(docElement);
    });
}