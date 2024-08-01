document.addEventListener("DOMContentLoaded", function() {
    const params = localStorage.getItem('searchParams');

    if (params) {
        fetchSearchResults(params, 1);
    } else {
        console.error('No search parameters found in localStorage');
    }
});

function fetchSearchResults(params, pageNumber) {
    const urlParams = new URLSearchParams(params);
    urlParams.set('pageNumber', pageNumber);

    fetch(`/api/files/search?${urlParams.toString()}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
    })
        .then(response => response.json())
        .then(data => {
            console.log("data - ", data);
            displaySearchResults(data);
            displayPagination(data, params);
        })
        .catch(error => console.error('Ошибка поиска файлов:', error));
}

function displaySearchResults(data) {
    const tableBody = document.getElementById('filesTable')
        .getElementsByTagName('tbody')[0];
    tableBody.innerHTML = '';

    data.content.forEach(fileData => {
        let row = tableBody.insertRow();
        row.setAttribute('data-id', fileData.id);
        row.innerHTML = `
          <td>${fileData.filename}</td>
          <td>${fileData.filetype}</td>
          <td>${fileData.author}</td>
          <td>${fileData.creationDate}</td>
        `;
        row.addEventListener('click', function() {
            window.location.href = `/html/files/file_details.html?id=${fileData.id}`;
        });
    });
}
function displayPagination(data, params) {
    const paginationContainer = document.getElementById('pagination');
    paginationContainer.innerHTML = '';

    const currentPage = data.number + 1;
    const totalPages = data.totalPages;

    // Создание кнопок для переключения страниц
    if (currentPage > 1) {
        const prevButton = createPaginationButton('<<', currentPage - 1, params);
        paginationContainer.appendChild(prevButton);
    }

    for (let i = Math.max(1, currentPage - 2); i <= Math.min(totalPages, currentPage + 2); i++) {
        const pageButton = createPaginationButton(i, i, params, i === currentPage);
        paginationContainer.appendChild(pageButton);
    }

    if (currentPage < totalPages) {
        const nextButton = createPaginationButton('>>', currentPage + 1, params);
        paginationContainer.appendChild(nextButton);
    }
}

function createPaginationButton(text, pageNumber, params, isCurrent = false) {
    const button = document.createElement('button');
    button.textContent = text;
    button.disabled = isCurrent;

    button.addEventListener('click', function() {
        fetchSearchResults(params, pageNumber);
    });

    return button;
}