
- make use of inheritance - introduce 
- 
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Employee {

    @Id
    private UUID employeeId;

    private String employeeName;

    private String employeeEmail;

username
}
and clacces that extend Employee

courier and assistant - extending employee

courier specific fields
courier_phone_number
courier_area

IN SQL it is : 

CREATE TABLE employees (
employee_id UUID PRIMARY KEY,
employee_name VARCHAR(255) NOT NULL
);

CREATE TABLE couriers (
employee_id UUID PRIMARY KEY,
courier_phone_number VARCHAR(30),

    CONSTRAINT fk_courier_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(employee_id)
);


PRACTICAL WORK FOR SPRING INTERNALS:

Practical work in the Shipment application
I see two worthwhile exercises, but neither requires a major implementation.
First: implement a simple @Around timing aspect for ShipmentService and observe how it intercepts method calls. This would demonstrate AOP, advice, pointcuts and proxies simultaneously.
Second: compare the existing CompletableFuture-based PDF generation with Spring's @Async. This connects Spring's proxy infrastructure with Java concurrency, which you've already covered.
We can leave the detailed behavior of @Transactional—propagation, isolation, rollback rules and persistence-context boundaries—for the dedicated transactions section.
One important distinction to retain throughout: Spring AOP proxying is not the only form of proxying you encounter in a Java application. Hibernate's lazy-loading entity proxies, for example, serve a different purpose, even though they rely on related Java mechanisms.
That gives us a good conceptual foundation without needing to study Spring's internal class hierarchy.