document.addEventListener("DOMContentLoaded", function() {

    fetch('/api/users')
        .then(response => response.json())
        .then(users => {
            const tableBody = document.getElementById('usersTable').getElementsByTagName('tbody')[0];
            users.forEach(userData => {
                console.log(userData); // Проверка каждого пользователя
                let row = tableBody.insertRow();
                row.setAttribute('data-id', userData.id);
                let roles = userData.roles ? Array.from(userData.roles).map(role => role.name).join(', ') : ' - ';
                row.innerHTML = `

          <td>${userData.username}</td>
          <td>${userData.name}</td>
          <td>${userData.lastName ? userData.lastName :  ' - '}</td>
          <td>${roles}</td>

        `;
                row.addEventListener('click', function() {
                    window.location.href = `user_details.html?id=${userData.id}`;
                });
            });
        })
        .catch(error => console.error('Ошибка:', error));
});
