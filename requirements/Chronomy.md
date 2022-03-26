# Chronomy

## System requirements

### Stakeholders

- Users:
  - Who offers services: ==offeror==
  - Who asks for services: ==requester== 

> Offerors and requesters are not exclusive categories. The role depend on the usage of the app.



> POSTS is the generic word to refer to offers and requests

### Stories

#### User

1. creating a profile
   1. Name, nickname, phone number, email, address, skills, description
2. edit profile
   1. CRUD skills (only offeror) , edit name, surname, ...
3. can visualize the posts using TCU or Hours
4. can report another user

#### Offeror

1. publicize theirs skills

   1. Writes them in theirs profile (skill+description)

      > NB: skills are the categories used to filter the posts. The description will be used to specify which particular abilities the user has in that field

2. search for requests

   1. Filtering by category, area, end date, start date

3. advertise an offer (the service that they can do)

   1. Position, start date, end date, description, category, title, expiration date

4. check the published offers

5. answer to request to contact Requester to offer a service

   > Service is another word for offer

1. CRUD offer
2. suspend offer
   1. offers can be active or not
3. check requesters comments and rating on their page
4. can visualize the request that they have accepted
5. can reject a request that they have taken on
   1. UP TO ONE DAY BEFORE
6. receives theirs TCU when the request has been completed

#### Requester

1. must have enough TCU to request for a service

2. has an initial credit of ==300TCU/5h==

3. search for offers 

   1. filtering by category, area, end date, start date

4. answer to offer to contact Offeror and get a service

5. publish request

6. check previous offers (history)

7. give feedback to completed offer

8. says when the request has been taken over 

9. says when the request has been completed

10. says when the request has failed (=> offeror didn't complete the job, didn't come)

11. edit and republish the failed request after it has been declared failed

12. can suspend the request

13. gets theirs TCU blocked when a request has been taken

14. gets theirs TCU removed when the request has been completed

    

### Differences between offers and requests

- Requests MUST have duration 
- Requests and Offers CAN have start and end dates, MUST have publish date
  - end date is expiration date of the post
  - start date IS NOT publish date
  - publish date is the moment in which the post is visible
- Offers can be active or suspended or deleted (?)
- Requests are unique instances 
- Requests can be pending, taken, completed, failed or suspended 

### Conversion TCU - time

1 min = 1 TCU

### Functional Requirements

| ID   | Description |
| ---- | ----------- |
|      |             |
|      |             |
|      |             |
|      |             |
|      |             |
|      |             |
|      |             |
|      |             |
|      |             |
|      |             |
|      |             |

