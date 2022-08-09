# Account-Service
An API for sending payrolls to the employee's account on the corporate website. Built with Java and Spring Framework.

### Roles
The following role model is used.

|                           | Anonymous | User | Accountant | Administrator | Auditor |
|---------------------------|-----------|------|------------|---------------|---------|
| POST api/auth/signup      | +         | +    | +          | +             | +       |
| POST api/auth/changepass  |           | +    | +          | +             | -       |
| GET api/empl/payment      | -         | +    | +          | -             | -       |
| POST api/acct/payments    | -         | -    | +          | -             | -       |
| PUT api/acct/payments     | -         | -    | +          | -             | -       |
| GET api/admin/user        | -         | -    | -          | +             | -       |
| DELETE api/admin/user     | -         | -    | -          | +             | -       |
| PUT api/admin/user/role   | -         | -    | -          | +             | -       |
| PUT api/admin/user/access | -         | -    | -          | +             | -       |
| GET api/security/events   | -         | -    | -          | -             | +       |


### Logging events
The following information security events are logged.

| Description                                                  | Event           |
|--------------------------------------------------------------|-----------------|
| A user has been successfully registered                      | CREATE_USER     |
| A user has changed the password successfully                 | CHANGE_PASSWORD |
| A user is trying to access a resource without access rights  | ACCESS_DENIED   |
| Failed authentication                                        | LOGIN_FAILED    |
| A role is granted to a user                                  | GRANT_ROLE      |
| A role has been revoked                                      | REMOVE_ROLE     |
| The Administrator has locked the user                        | LOCK_USER       |
| The Administrator has unlocked a user                        | UNLOCK_USER     |
| The Administrator has deleted a user                         | DELETE_USER     |
| A user has been blocked on suspicion of a brute force attack | BRUTE_FORCE     |

The security events are stored with the fields below:
```json
{
    "date": "<date>",
    "action": "<event_name from table>",
    "subject": "<The user who performed the action>",
    "object": "<The object on which the action was performed>",
    "path": "<api>"
}
```


### Endpoints

#### POST api/auth/signup 
Available to unauthorized users for registration and accepts data in the JSON format:
```json
{
   "name": "<String value, not empty>",
   "lastname": "<String value, not empty>",
   "email": "<String value, not empty>",
   "password": "<String value, not empty>"
}
```

#### POST api/auth/changepass 
Changes password of the authenticated user. Accepts the following JSON body:
```json
{
   "new_password": "<String value, not empty>"
}
```

#### GET api/empl/payment
Gives access to the payroll of an employee. Accepts optional
period parameter <mm-YYYY>.

#### POST api/acct/payments 
Uploads payrolls. Accepts the following JSON body:
```json
[
    {
        "employee": "<user email>",
        "period": "<mm-YYYY>",
        "salary": <Long>
    },
    {
        "employee": "<user1 email>",
        "period": "<mm-YYYY>",
        "salary": <Long>
    },
    ...
    {
        "employee": "<userN email>",
        "period": "<mm-YYYY>",
        "salary": <Long>
    }
]
```

#### PUT api/acct/payments 
Changes the salary of a specific user. The following JSON data is accepted:
```json
{
    "employee": "<user email>",
    "period": "<mm-YYYY>",
    "salary": <Long>
}
```

#### GET api/admin/user
Obtains information about all users.

#### DELETE api/admin/user/{user email}
Deletes user specified by {user email}.


#### PUT api/admin/user/role 
Sets the roles. The following JSON body is accepted:
```json
{
    "user": "<String value, not empty>",
    "role": "<User role>",
    "operation": "<[GRANT, REMOVE]>"
}
```
#### PUT api/admin/user/access
Locks and unlocks users.
The following JSON body is accepted:
```json
{
    "user": "<String value, not empty>",
    "operation": "<[LOCK, UNLOCK]>"
}
```

#### GET api/security/events
Responds with an array of objects representing the security events of the service sorted in ascending order by ID.
