-- Select all entities of a given type that are associated with a given (sub*-)topic.
-- 
WITH RECURSIVE sub_topic
AS (
	SELECT id, parent_id
	FROM topic t
	WHERE t.name = ':topic_name'
	UNION ALL
	SELECT t.id, t.parent_id
	FROM topic t
	INNER JOIN
		sub_topic st ON st.id = t.parent_id
)
SELECT DISTINCT e.*
FROM entity e
INNER JOIN (sub_topic st, topic_ref tr, topic t)
ON tr.topic_id = st.id AND tr.topic_id = t.id AND tr.entity_id = e.id
WHERE e.entity_type = ':entity_type'
ORDER BY e.name;