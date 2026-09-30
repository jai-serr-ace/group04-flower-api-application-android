# FlowerAPI Proposal

## 1. The pitch (one paragraph)
FlowerAPI provides a user-contributed database of flowers for aesthetic, educational, and other uses.
Authenticated users are able to upload flowers, and clients can browse, search, and display flower information and associated quotes.
A web or mobile client may use the API to display searchable flower collections while allowing users to create and/or add to entries.

## 2. Resources
| Resource | Key fields                                                                | Relationships                      |
|----------|---------------------------------------------------------------------------|------------------------------------|
| User     | user id, email, username, OAuth provider id                               | a User can upload many flowers     |
| Flower   | flower id, scientific name, user (uploader) id, common name, notes, image | a flower is dispalyed with a quote |
| Quote    | quote id, quote text, author                                              | a quote is displayed with a flower |
| ...      | ...                                                                       | ...                                |

## 3. ER sketch

```mermaid
erDiagram
    USER ||--o{ FLOWER : owns
    FLOWER ||o--o{ FQLINKS : links
    QUOTE ||o--o{ FQLINKS : links
    USER {
        bigint user_id PK "generated"
        string username
        string email UK
        string provider_id
    }
    FLOWER {
        bigint flower_id PK "generated"
        string scientific_name "nullable"
        bigint user_id FK "'uploaded by'"
        string name
        string notes "nullable"
        string img_link "image ref"
    }
    QUOTE {
        bigint quote_id PK "generated"
        string quote_text "non null"
        string author "nullable"
    }
    FQLINKS {
        bigint link_id PK "generated"
        bigint flower_id FK "to FLOWER"
        bigint quote_id FK "to QUOTE"
    }
```

## 4. Endpoints
| Verb   | Path                        | Auth       | Purpose                 |
|--------|-----------------------------|------------|-------------------------|
| GET    | /api/v1/flowers             | public     | list flowers            |
| GET    | /api/v1/flowers/{flower_id} | public     | view specific flower    |
| POST   | /api/v1/flowers             | user       | upload a flower         |
| PATCH  | /api/v1/flowers/{flower_id} | user/admin | edit an uploaded flower |
| DELETE | /api/v1/flowers/{flower_id} | user/admin | delete flower           |
| GET    | /api/v1/quotes              | public     | list quotes             |
| POST   | /api/v1/quotes              | user       | upload a quote          |
| GET    | /api/v1/users/me            | user       | view current user       |
| GET    | /api/v1/health              | public     | check if API is running |
| ...    | ...                         | ...        | ...                     |
The `GET /flowers` and `GET /quotes` collection endpoints support pagination. Flowers can be filtered by scientific name and common name and sorted by name. Users may edit or delete only flowers they own; administrators may manage all flowers.

- `200 OK` successful read
- `201 Created` successful creation
- `400 Bad Request` invalid search input
- `401 Unauthorized` missing/invalid auth
- `403 Forbidded` missing/insufficient permissions
- `404 Not Found` missing resource

## 5. Technical choices
- **Database host:** Railway -- natively supports PostgreSQL, which we have experience with
- **OAuth2 provider:** Auth0
- **Repo layout:** split repo -- recommended by Dr. C

## 6. Risks
- **Authentication implemented but not working:** OAuth may work for login but fail unexpectedly at some point when the API needs user access. 
- **Incorrect permissions:** Testing API only logged in as admin may lead to inconsistent results across permission layers.

## 7. Team and Sprint 1
Who owns what in Sprint 1. Link your Project board and Sprint 1 milestone.
