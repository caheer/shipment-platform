SELECT s.shipment_id, st.shipment_tag_id, t.tag_code, t.tag_name
FROM shipments s
         JOIN shipment_tags st ON s.shipment_id = st.shipment_id
         JOIN tags t ON st.tag_code = t.tag_code
order by s.shipment_id
