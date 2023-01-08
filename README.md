# l0-registry

**Purpose**: A registry of N TIKI addresses mapped to a customer-specified user_id 

**Why?**: To simplify cross-device syncing and backend integrations —customers do not need to maintain a list of tiki addresses in their user model.

**How**: Clients can elect to register a TIKI address with a specified user_id or allow TIKI to autogenerate one for them. 

*Note: It's recommended that customers use a one-way hashing function (like SHA256) before uploading as a decoupling method.* 


## Requirements:
### Infrastructure:
A stateless spring-boot microservice, a hosted Postgres database,  and a round-robin load balancer. 

Deployed as a Docker container via GH actions calling terraform scripts. Performance and error monitoring handled by Sentry. 

See [l0-auth](https://github.com/tiki/l0-auth) for an example. 

### Data Structure:
*Note: skeleton framework only, not to be taken as final table structure*

<img width="429" alt="image" src="https://user-images.githubusercontent.com/3769672/211176045-162ab542-8380-4603-af46-425aaa64f424.png">

### APIs  
`POST /register`  
Parameters: `address`, `stringToSign`, `signature`, and optional `user_id`  
Returns: `user_id`

`GET /id/{user_id}`  
Parameters: `user_id`  
Returns: list of registered `tiki_address`

`POST /id/{user_id}/keygen`  
Parameters: `user_id`  
Returns: `private_key`

### Security 
- An API ID (or valid JWT) is required to `GET` / `POST` records. This requires moving the API ID system currently implemented in the [l0-storage](http://github.com/tiki/l0-storage) service to the [l0-auth](http://github.com/tiki/l0-auth) service. l0-auth service will need to add a `userinfo` / `isValid` style endpoint check revocation and return the appId (aid)

- For sensitive applications, an additional security step will be encouraged. Client applications (by design) are insecure, meaning any client can register any `address` they have the private key for to any `user_id`. To limit authorized users to specific user_id patterns, signing key(s) can be generated per user_id. Typical implementations call `POST /id/{user_id}/keygen` on Login. Requires a valid API Key (not ID).

