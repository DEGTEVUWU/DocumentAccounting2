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

    fetch(`/api/documents/search?${urlParams.toString()}`, {
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
        .catch(error => console.error('Ошибка поиска документов:', error));
}

function displaySearchResults(data) {
    const tableBody = document.getElementById('documentsTable').getElementsByTagName('tbody')[0];
    tableBody.innerHTML = ''; // Очистить предыдущие результаты

    data.content.forEach(docData => {
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
}
function displayPagination(data, params) {
    const paginationContainer = document.getElementById('pagination');
    paginationContainer.innerHTML = ''; // Очистить предыдущие элементы

    const currentPage = data.number + 1; // Номера страниц начинаются с 0, поэтому добавляем 1
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

function truncateText(text, maxLength) {
    if (text.length > maxLength) {
        return text.slice(0, maxLength) + "...";
    } else {
        return text;
    }
}
