package com.ivan_degtev.documentaccounting2.specification;

import com.ivan_degtev.documentaccounting2.dto.document.DocumentParamsDTO;
import com.ivan_degtev.documentaccounting2.model.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DocumentSpecification {
    public Specification build(DocumentParamsDTO params) {
        return withTitleCount(params.getTitleCont())
                .and(withAuthorCount(params.getAuthorCont()))
                .and(withContentCount(params.getContentCont()))
                .and(withTypeCount(params.getTypeCont()))
                .and(withNumber(params.getNumber()))
                .and(withCreatedDate(params.getCreationDate()));
    }

    private Specification<Document> withTitleCount(String titleCont) {
        return (root, query, cb) -> titleCont == null || titleCont.isEmpty()
                ? cb.conjunction()
                : cb.like((root.get("title")), "%" + titleCont + "%");
    }

    private Specification<Document> withNumber(Long number) {
        return (root, query, cb) -> number == null
                ? cb.conjunction()
                : cb.equal(root.get("number"), number);
    }

    private Specification<Document> withAuthorCount(String authorCont) {
        return (root, query, cb) -> authorCont == null
                ? cb.conjunction()
                : cb.like((root.get("author").get("username")), "%" + authorCont + "%");
    }

    private Specification<Document> withContentCount(String contentCont) {
        return (root, query, cb) -> contentCont == null
                ? cb.conjunction()
                : cb.like((root.get("content")), "%" + contentCont + "%");
    }

    private Specification<Document> withTypeCount(String typeCont) {
        return (root, query, cb) -> typeCont == null
                ? cb.conjunction()
                : cb.like((root.get("type").get("type")), "%" + typeCont + "%");
    }

    private Specification<Document> withCreatedDate(LocalDate date) {
        return (root, query, cb) -> date == null
                ? cb.conjunction()
                : cb.equal(root.get("creationDate"), date);
    }
    public Sort createSort(DocumentParamsDTO params) {
        Sort sort = Sort.by("title").ascending(); // сортировка по умолчанию
        if (params.getSortBy() != null && !params.getSortBy().isEmpty()) {
            sort = "desc".equalsIgnoreCase(params.getSortDirection())
                    ? Sort.by(params.getSortBy()).descending()
                    : Sort.by(params.getSortBy()).ascending();
        }
        return sort;
    }
    /*
    сортировка работает дефолтно по названию док-та, если даёться другой параметр в запросе, дефолтно используется прямая
    сортировка, для изменения нужно дать в заапросе обратную desc
     */
}
