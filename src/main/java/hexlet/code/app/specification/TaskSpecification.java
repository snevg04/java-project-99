package hexlet.code.app.specification;

import hexlet.code.app.dto.TaskParamsDTO;
import hexlet.code.app.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {
    public Specification<Task> build(TaskParamsDTO params) {
        return withTitleCont(params.getTitleCont())
                .and(withAssigneeId(params.getAssigneeId()))
                .and((withStatus(params.getStatus())))
                .and(withLabelId(params.getLabelId()));
    }

    private Specification<Task> withTitleCont(String name) {
        return (root, query, cb) -> name == null ? cb.conjunction()
                : cb.like(root.get("name"), name);
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) -> assigneeId == null ? cb.conjunction()
                : cb.equal(root.get("assignee"), assigneeId);
    }

    private Specification<Task> withStatus(String slug) {
        return (root, query, cb) -> slug == null ? cb.conjunction()
                : cb.equal(root.get("slug"), slug);
    }

    private Specification<Task> withLabelId(Long labelId) {
        return (root, query, cb) -> {
            if (labelId == null) {
                return cb.conjunction();
            }

            query.distinct(true);
            var labelJoin = root.join("labels");
            return cb.equal(labelJoin.get("id"), labelId);
        };
    }
}
